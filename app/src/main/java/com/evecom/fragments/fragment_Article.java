package com.evecom.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evecom.activity.R;
import com.evecom.activity.ShowArticleActivity;
import com.evecom.activity.WriteArticleActivity;
import com.evecom.adapter.MyArticleListViewAdapter;
import com.evecom.bean.Article;
import com.evecom.bean.ArticleTypeOne;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 个人信息中的文章fragment
 * Created by Bob on 2017/3/10.
 */
public class fragment_Article extends Fragment {

    /**
     * view
     */
    private View view;
    /**
     * 文章列表
     */
    ListView lvMyArticle;
    /**
     * 底部操作菜单
     */
    RelativeLayout rlBottomMenu;
    /**
     * 数据
     */
    List<Article> mList = new ArrayList<>();
    /**
     * 全选checkbox
     */
    CheckBox cbChooseAll;
    /**
     * 编辑
     */
    TextView tvEdit;
    /**
     * 删除
     */
    TextView tvDelete;
    /**
     * 取消
     */
    TextView tvCancel;
    /**
     * 适配器
     */
    private MyArticleListViewAdapter myArticleListViewAdapter;
    /**
     * 保存列表item选中状态的数组
     */
    private boolean[] lastStatus;
    /**
     * 删除数目
     */
    private int deletedCount = 0;
    /**
     * index
     */
    private int index = 0;
    /**
     * 没有数据时显示
     */
    TextView tvNoData;
    /**
     * handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //获得数据后，初始化列表
                initListView();
            } else if (msg.what == 2) {

                //删除数目加一
                deletedCount++;

                //继续判断
                index++;
                deleteCurrentArticleOrNot(index);

            }
        }
    };


    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_article, container, false);

        //初始化控件
        initUI();

        //获取数据
        getData();

        return view;
    }

    /**
     * 初始化控件
     */
    public void initUI() {

        lvMyArticle = (ListView) view.findViewById(R.id.lvMyArticle);
        rlBottomMenu = (RelativeLayout) view.findViewById(R.id.rlBottomOprationMenu);
        cbChooseAll = (CheckBox) view.findViewById(R.id.cbChooseAll);
        tvEdit = (TextView) view.findViewById(R.id.tvEdit);
        tvDelete = (TextView) view.findViewById(R.id.tvDelete);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);

        //设置监听
        //点击全选
        cbChooseAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbChooseAll.isChecked()) {   //全选中
                    myArticleListViewAdapter.setCheckAllOrNot(true);
                } else {                         //全不选中
                    myArticleListViewAdapter.setCheckAllOrNot(false);
                }
                //刷新列表
                myArticleListViewAdapter.notifyDataSetChanged();
            }
        });

        //点击编辑
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取选中个数
                int checkedCount = myArticleListViewAdapter.getCheckedCount();

                //判断
                if (checkedCount == 0) {
                    Toast.makeText(getActivity(), "请先选择", Toast.LENGTH_SHORT).show();
                } else if (checkedCount > 1) {
                    Toast.makeText(getActivity(), "每次只能选择一项进行编辑", Toast.LENGTH_SHORT).show();
                } else if (checkedCount == 1) {
                    //跳转编辑界面，将选中的文章传过去
                    int indexToEdit = 0;
                    lastStatus = myArticleListViewAdapter.getLastStatus();
                    for (int i = 0;i<lastStatus.length;i++){
                        if (lastStatus[i]){
                            indexToEdit = i;
                        }
                    }
                    Article articleToEdit = mList.get(indexToEdit);
                    Log.d("TAG===articleToEdit",articleToEdit.toString());
                    //点击跳转文章显示页面（传参为当前文章对象）
                    Intent intent = new Intent(getActivity(), WriteArticleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("activity","2");
                    bundle.putSerializable("article", articleToEdit);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

            }
        });

        //点击删除
        tvDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //获取保存checkbox状态的数组
                lastStatus = myArticleListViewAdapter.getLastStatus();

                //获取选中个数
                int checkedCount = myArticleListViewAdapter.getCheckedCount();
                Toast.makeText(getActivity(), checkedCount+"", Toast.LENGTH_SHORT).show();
                //判断
                if (checkedCount == 0) {
                    Toast.makeText(getActivity(), "请先选择", Toast.LENGTH_SHORT).show();
                } else {

                    //从第一个元素开始判断是否被选中（要删除）
                    deleteCurrentArticleOrNot(index);

                }
            }
        });

        //点击取消操作（隐藏底部菜单和item的checkbox）
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlBottomMenu.setVisibility(View.GONE);
                myArticleListViewAdapter.setStatuOfCheckBox(false);
                //刷新列表状态
                myArticleListViewAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 从数据库获取数据
     */
    public void getData() {

        //从SharedPreferences获取用户objectID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", getContext().MODE_PRIVATE);
        String objectID = sharedPreferences.getString("objectID", null);

        //创建一个BMOB查询对象，从数据库获取用户的文章
        final BmobQuery<Article> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("userObjectID", objectID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                bmobQuery.findObjects(getActivity(), new FindListener<Article>() {

                    @Override
                    public void onSuccess(List<Article> list) {
                        if (list.size() > 0) {
                            tvNoData.setVisibility(View.GONE);
                            mList = list;
                            //获取数据成功，通知主线程去初始化列表
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        } else {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }

                });
            }

        }).start();


    }

    /**
     * 从数据库删除文章
     *
     * @param index ：数组中对应的下标
     */
    public void deleArticle(int index) {

        //获得当前要删除的文章对象
        final Article article = mList.get(index);

        //从数据库删除
        article.delete(getActivity(), new DeleteListener() {
            @Override
            public void onSuccess() {
                //紧接着从分表删除
                deleArticleFromTypeTable(article);

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }

    /**
     * 判断当前item是否需要删除
     *
     * @param index1 ：当前下标
     */
    public void deleteCurrentArticleOrNot(int index1) {

        //先判断index1是否在0-lastStatus.length范围内
        if (index1 < lastStatus.length) {

            //判断当前元素是否要删除
            if (lastStatus[index1]) {
                //选中，则删除
                deleArticle(index1);
            } else {
                //未选中，则继续判断下一个
                index++;
                deleteCurrentArticleOrNot(index);
            }

        } else {

            //后面没有元素，显示删除结果
            Toast.makeText(getActivity(),
                    "选择了" + myArticleListViewAdapter.getCheckedCount() + "条，"
                            + "成功删除了" + deletedCount + "条", Toast.LENGTH_SHORT).show();
            //隐藏底部菜单
            rlBottomMenu.setVisibility(View.GONE);

            //删除完成，重新从数据库获取数据，刷新列表
            getData();

            //重置deletedCount、index
            deletedCount = 0;
            index = 0;

        }
    }

    /**
     * 初始化列表
     */
    public void initListView() {

        //构建适配器对象
        myArticleListViewAdapter = new MyArticleListViewAdapter(getActivity(), mList);

        //为listview设置适配器
        lvMyArticle.setAdapter(myArticleListViewAdapter);

        //给listview的item设置监听
        lvMyArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击跳转文章显示页面（传参为当前文章对象）
                Article article = mList.get(position);
                Intent intent = new Intent(getActivity(), ShowArticleActivity.class);
                Bundle bundle = new Bundle();
                //传一个0，告诉文章展示界面展示的是个人文章
                bundle.putString("activity","0");
                bundle.putSerializable("article", article);
                intent.putExtras(bundle);
                //跳转
                startActivity(intent);
            }
        });

        //设置长按监听
        lvMyArticle.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                //若底部菜单为隐藏状态，则显示
                if (rlBottomMenu.getVisibility() == View.GONE) {
                    rlBottomMenu.setVisibility(View.VISIBLE);

                    //显示列表的选择框
                    myArticleListViewAdapter.setStatuOfCheckBox(true);

                    //刷新列表
                    myArticleListViewAdapter.notifyDataSetChanged();

                    return true;
                }

                return false;

            }
        });

    }

    /**
     * 从对应分类表里删除文章
     * @param article:总表里的文章对象
     */
    public void deleArticleFromTypeTable(Article article){

        //获取文章在总表里的分类、objectID
        String type = article.getArticleType();
        String objectID = article.getObjectId();

        //根据所属类别，在对应的分类表中去删除该文章
        switch (type){
            case "分类一":
                //根据文章在总表里的objectID，到分类表找到对应的文章
                BmobQuery<ArticleTypeOne> bmobQuery = new BmobQuery<>();
                bmobQuery.addWhereEqualTo("articleObjectID",objectID);
                bmobQuery.findObjects(getActivity(), new FindListener<ArticleTypeOne>() {
                    @Override
                    public void onSuccess(List<ArticleTypeOne> list) {
                        if (list.size()>0){
                            //删除分表中的文章对象
                            ArticleTypeOne articleTypeOne = new ArticleTypeOne();
                            articleTypeOne.setObjectId(list.get(0).getObjectId());
                            articleTypeOne.delete(getActivity(), new DeleteListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getActivity(),"已从分表中删除",Toast.LENGTH_SHORT).show();
                                    //删除成功，通知进行下一步操作
                                    Message message = new Message();
                                    message.what = 2;
                                    handler.sendMessage(message);
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
                break;
        }

    }

}
