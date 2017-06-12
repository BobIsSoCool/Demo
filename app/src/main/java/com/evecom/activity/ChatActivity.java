package com.evecom.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evecom.adapter.ChatMessageAdapter;
import com.evecom.bean.ChatMessage;
import com.evecom.bean.ChatRecorder;
import com.evecom.bean.UserData;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * 聊天界面
 * Created by wub on 2017/4/19.
 */
public class ChatActivity extends Activity {

    /**
     * 点击返回图标
     */
    ImageView ivBack;
    /**
     * 顶部昵称
     */
    TextView tvTopName;
    /**
     * 聊天列表
     */
    ListView lvChat;
    /**
     * 消息输入框
     */
    EditText etMessage;
    /**
     * 点击发送消息
     */
    TextView tvSendMessage;
    /**
     * 当前用户id
     */
    private String currentUserObjectID;
    /**
     * 消息集合
     */
    private List<ChatMessage> mList = new ArrayList<>();
    /**
     * 实时数据
     */
    private BmobRealTimeData data = new BmobRealTimeData();
    /**
     * 适配器
     */
    ChatMessageAdapter adapter;
    /**
     * 聊天对象的id
     */
    private String idOfTheUserYouTalkWith;
    /**
     * 最新一条聊天消息
     */
    private String message = "";
    /**
     *
     */
    private ChatRecorder chatRecorder = null;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取主题
        int theme = getSharedPreferences("user", MODE_PRIVATE).getInt("theme", 0);
        //设置主题
        if (theme == 0) {
            setTheme(R.style.CustomStyleOne);
        } else {
            setTheme(theme);
        }

        //布局
        setContentView(R.layout.activity_chat);

        //状态栏沉浸模式（安卓5.0之后才支持）
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //状态栏背景透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //连接服务器
        init();

        //初始化控件
        initUI();

        //设置监听
        setListenners();

        //当前用户id
        currentUserObjectID = getSharedPreferences("user", MODE_PRIVATE).getString("objectID", null);

        //得到当前聊天的对象信息
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        idOfTheUserYouTalkWith = intent.getStringExtra("userObjectID");
        String avatarUrl = intent.getStringExtra("avatarUrl");

        //构建一个适配器实例
        adapter = new ChatMessageAdapter(this);
        //把聊天对象的头像url传过去（因为是两个人聊天，对方的url都是这个，传过去不再需要根据用户id去查询）
        adapter.setUserAvatarUrl(avatarUrl);
        //显示顶部昵称
        tvTopName.setText(userName);

        //如果有未读消息，则加载未读消息
        loadMessageNotRead();

    }

    /**
     * 初始化控件
     */
    public void initUI() {

        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTopName = (TextView) findViewById(R.id.tvTopName);
        lvChat = (ListView) findViewById(R.id.lvChat);
        etMessage = (EditText) findViewById(R.id.etMessage);
        tvSendMessage = (TextView) findViewById(R.id.tvSendMessage);

    }

    /**
     * 设置监听
     */
    public void setListenners() {

        //点击返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //点击发送消息
        tvSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    /**
     * 发送一条聊天消息
     */
    public void sendMessage() {

        //获取输入框内容
        message = etMessage.getText().toString();
        //判断是否为空
        if (message.equals("")) {
            Toast.makeText(this, "消息不能为空", Toast.LENGTH_SHORT).show();
        } else {

            //创建一个消息实例
            ChatMessage chatMessage = new ChatMessage();

            //设置属性
            //发送者id（即当前用户的id）
            chatMessage.setIdOfTheSender(currentUserObjectID);
            //接收者id
            chatMessage.setIdOfTheReciever(idOfTheUserYouTalkWith);
            //消息内容
            chatMessage.setMessage(message);
            //日期
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            String date = sDateFormat.format(new java.util.Date());
            chatMessage.setDate(date);
            //默认设置为“未读”状态
            chatMessage.setReadOrNot(false);

            //发布
            chatMessage.save(ChatActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    //发布成功，将输入框清空
                    etMessage.setText("");
                    //更新表ChatRecorder中的最后一条消息、未读消息数+1、未用用户id设置为对方id
                    queryChatRecorder();
                }

                @Override
                public void onFailure(int i, String s) {
                    Toast.makeText(ChatActivity.this, "消息发送失败", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * 连接服务器，监听数据变化
     */
    private void init() {
        Bmob.initialize(this, "234704f54ac1b1d67a645bad577e7c02");
        data.start(this, new ValueEventListener() {

            @Override
            public void onDataChange(JSONObject arg0) {
                //数据发生变化时调用
                if (BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))) {
                    JSONObject data = arg0.optJSONObject("data");
                    //得到新的消息，添加到集合
                    //数据变化，判断是不是属于两者的新增聊天消息
                    //获取接收者id、发送者id、消息是否是未读
                    String idOfTheReciever = data.optString("idOfTheReciever");
                    String idOfTheSender = data.optString("idOfTheSender");
                    boolean readOrNot = data.optBoolean("readOrNot");

                    if (idOfTheSender.equals(currentUserObjectID) && idOfTheReciever.equals(idOfTheUserYouTalkWith) && !readOrNot) {
                        //情况1：消息是自己发给对方的，显示即可
                        ChatMessage chatMessage
                                = new ChatMessage(idOfTheSender, idOfTheReciever, data.optString("message"), data.optString("date"), readOrNot);
                        mList.add(chatMessage);
                        adapter.addList(mList);
                        lvChat.setAdapter(adapter);

                    } else if (idOfTheSender.equals(idOfTheUserYouTalkWith) && idOfTheReciever.equals(currentUserObjectID) && !readOrNot) {
                        //情况2：消息是聊天对象发的，显示，然后更新该消息为“已读”，并更新ChatRecorder表中记录的未读消息数为0
                        ChatMessage chatMessage
                                = new ChatMessage(idOfTheSender, idOfTheReciever, data.optString("message"), data.optString("date"), readOrNot);
                        mList.add(chatMessage);
                        adapter.addList(mList);
                        lvChat.setAdapter(adapter);
                        //更新该条消息为“已读”
                        String id = data.optString("objectId");
                        ChatMessage chatMessage1 = new ChatMessage();
                        chatMessage1.setObjectId(id);
                        chatMessage1.setReadOrNot(true);
                        chatMessage1.update(ChatActivity.this);
                        //更新未读消息数为0
                        updateNotReadMessageToZero();
                    }else {
                        //情况3：其他情况，不作处理
                    }

                }

            }

            @Override
            public void onConnectCompleted() {
                if (data.isConnected()) {
                    data.subTableUpdate("ChatMessage");
                }
            }
        });
    }

    /**
     * 查询记录
     */
    public void queryChatRecorder() {

        //查询是否存在记录
        BmobQuery<ChatRecorder> query1 = createQuery(currentUserObjectID, idOfTheUserYouTalkWith);
        query1.findObjects(this, new FindListener<ChatRecorder>() {
            @Override
            public void onSuccess(List<ChatRecorder> list) {
                if (list.size() > 0) {
                    //存在，则更新对应数据
                    updateChatRecorder(list.get(0));
                } else {
                    //不存在，将id换个顺序再查一次
                    BmobQuery<ChatRecorder> query2 = createQuery(idOfTheUserYouTalkWith, currentUserObjectID);
                    query2.findObjects(ChatActivity.this, new FindListener<ChatRecorder>() {
                        @Override
                        public void onSuccess(List<ChatRecorder> list) {
                            if (list.size() > 0) {
                                //存在，则更新对应数据
                                updateChatRecorder(list.get(0));
                            } else {
                                //第二次查询依旧不存在，说明没有记录，创建一个记录
                                createChatRecorder();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(ChatActivity.this, "查询失败" + i + s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ChatActivity.this, "查询失败" + i + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 创建查询
     *
     * @param idA
     * @param idB
     */
    public BmobQuery<ChatRecorder> createQuery(String idA, String idB) {

        //创建一个复合查询，先查询两用户是否有过聊天记录

        //复合查询条件一（用户1）
        BmobQuery<ChatRecorder> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("idOfUserA", idA);
        //复合查询条件二（用户2）
        BmobQuery<ChatRecorder> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("idOfUserB", idB);
        //组合
        List<BmobQuery<ChatRecorder>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        //总查询
        BmobQuery<ChatRecorder> query = new BmobQuery<>();
        query.and(queries);

        return query;
    }

    /**
     * 更新表ChatRecorder
     * 最后一条消息设置为message、未读消息数+1、未读用户id设置为对方id
     */
    public void updateChatRecorder(ChatRecorder chatRecorder) {

        //更新最后一条聊天消息
        chatRecorder.setTheLastMessage(message);
        //设置未读消息数+1
        int oldCount = chatRecorder.getCountNotRead();
        chatRecorder.setCountNotRead(++oldCount);
        //设置未读用户id为对方id
        chatRecorder.setIdHasMessageToRead(idOfTheUserYouTalkWith);

        //更新
        chatRecorder.update(this);

    }

    /**
     * 创建一条记录
     */
    public void createChatRecorder() {

        //创建对象
        ChatRecorder chatRecorder = new ChatRecorder();

        //设置属性
        //用户A的id
        chatRecorder.setIdOfUserA(currentUserObjectID);
        //用户B的id
        chatRecorder.setIdOfUserB(idOfTheUserYouTalkWith);
        //最后一条消息
        chatRecorder.setTheLastMessage(message);
        //未读消息数（创建时默认为1）
        chatRecorder.setCountNotRead(1);
        //有未读消息的用户的id
        chatRecorder.setIdHasMessageToRead(idOfTheUserYouTalkWith);

        //保存到BMOB数据库
        chatRecorder.save(this);

    }

    /**
     * 进入界面，查询是否有未读消息，有则加载显示
     */
    public void loadMessageNotRead() {

        //创建复合查询（消息的发送者id为连天对象id、接收者id为当前用户id、消息的状态为“未读”）

        //条件1
        BmobQuery<ChatMessage> query1 = new BmobQuery<>();
        query1.addWhereEqualTo("idOfTheReciever", currentUserObjectID);

        //条件2
        BmobQuery<ChatMessage> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("readOrNot", false);

        //条件3
        BmobQuery<ChatMessage> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("idOfTheSender", idOfTheUserYouTalkWith);

        //组合
        List<BmobQuery<ChatMessage>> queries = new ArrayList<>();
        queries.add(query1);
        queries.add(query2);
        queries.add(query3);

        //创建总查询
        BmobQuery<ChatMessage> query = new BmobQuery<>();
        query.and(queries);

        //查询
        query.findObjects(this, new FindListener<ChatMessage>() {
            @Override
            public void onSuccess(List<ChatMessage> list) {
                if (list.size() > 0) {
                    //存在未读消息，加载到列表
                    adapter.addList(list);
                    lvChat.setAdapter(adapter);
                    //加载完之后，将对应消息设置为已读状态，
                    // 并更新ChatRecorder表中对应记录的未读消息数为0
                    //批量更新list中消息状态为“已读”
                    updateChatMessageBeenRead(list);
                } else {
                }
            }

            @Override
            public void onError(int i, String s) {
                //查询失败，提示错误码
                Toast.makeText(ChatActivity.this, "" + i + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 批量更新list中消息状态为“已读”
     *
     * @param list
     */
    public void updateChatMessageBeenRead(List<ChatMessage> list) {

        //创建一个用于批量更新的集合
        List<BmobObject> listToUpdate = new ArrayList<>();

        //遍历集合，将每条消息设置为“已读”
        for (int i = 0; i < list.size(); i++) {
            ChatMessage chatMessage = list.get(i);
            chatMessage.setReadOrNot(true);
            //添加到集合
            listToUpdate.add(chatMessage);
        }

        //批量更新
        //第一种方式：v3.5.0之前的版本
        new BmobObject().updateBatch(this, listToUpdate, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ChatActivity.this, "已将未读消息更新为已读", Toast.LENGTH_SHORT).show();
                //接下来更新ChatRecorder中未读消息数为0
                updateNotReadMessageToZero();
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(ChatActivity.this, "批量更新失败" + code + msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 更新ChatRecorder中未读消息数为0
     */
    public void updateNotReadMessageToZero() {

        //查询是否存在记录
        BmobQuery<ChatRecorder> query1 = createQuery(currentUserObjectID, idOfTheUserYouTalkWith);
        query1.findObjects(this, new FindListener<ChatRecorder>() {
            @Override
            public void onSuccess(List<ChatRecorder> list) {
                if (list.size() > 0) {
                    //存在，则更新对应数据
                    ChatRecorder chatRecorder = list.get(0);
                    chatRecorder.setCountNotRead(0);
                    chatRecorder.update(ChatActivity.this);
                } else {
                    //不存在，将id换个顺序再查一次
                    BmobQuery<ChatRecorder> query2 = createQuery(idOfTheUserYouTalkWith, currentUserObjectID);
                    query2.findObjects(ChatActivity.this, new FindListener<ChatRecorder>() {
                        @Override
                        public void onSuccess(List<ChatRecorder> list) {
                            if (list.size() > 0) {
                                //存在，则更新对应数据
                                ChatRecorder chatRecorder = list.get(0);
                                chatRecorder.setCountNotRead(0);
                                chatRecorder.update(ChatActivity.this);
                            } else {

                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(ChatActivity.this, "查询失败" + i + s, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ChatActivity.this, "查询失败" + i + s, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
