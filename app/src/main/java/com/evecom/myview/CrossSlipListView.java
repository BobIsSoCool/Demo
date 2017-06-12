package com.evecom.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * @author zheng.li
 * item可左右滑动的ListView
 * */
public class CrossSlipListView extends ListView
{

    private CrossSlipView currentView,lastView;//当前和历史视图
    private int currentPosition, lastPosition = -1;//当前和历史位置
    private int downX,downY;//点击处的标示
    private int miniDircetuon;//默认的滑动最小距离
    private boolean moved = false;//是否可以拖动
    private boolean closed = true;//是否全部关闭
    private boolean openSlide = true;//是否开启侧滑

    public CrossSlipListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        //获取当前系统默认的滑动行为距离
        miniDircetuon = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public CrossSlipListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CrossSlipListView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override public boolean onTouchEvent(MotionEvent ev)
    {
        // TODO Auto-generated method stub
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(openSlide){
                    currentPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
                    if(currentPosition != Adapter.IGNORE_ITEM_VIEW_TYPE){
                        currentView = (CrossSlipView) getChildAt(currentPosition - getFirstVisiblePosition());
                        downX = (int) ev.getX();
                        downY = (int) ev.getY();
                        if(lastPosition == currentPosition || closed){
                            /**点击的是同一个或者列表处于初始状态*/
                            moved = true;
                            currentView.move_X = downX;
                        }else{
                            moved = false;
                            /**对之前的展开项进行归位操作*/
                            if(null != lastView){
                                lastView.closeMenu();
                            }
                        }
                        lastPosition = currentPosition;
                        lastView = currentView;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(openSlide){
                    if (Math.abs(downX - ev.getX()) < Math.abs(downY - ev.getY()) * dp2px(2)){
                        /** 手势操作为上下滑动，而非左右侧滑 */
                        break;
                    }
                    if (moved){
                        int distance = (int) (currentView.move_X - ev.getX());
                        if (currentView.direction == CrossSlipView.DIRECTION.SLIDE_TO_OPEN_RIGHT){
                            distance = distance + currentView.rightView.getWidth();
                        }else if (currentView.direction == CrossSlipView.DIRECTION.SLIDE_TO_OPEN_LEFT){
                            distance = distance - currentView.leftView.getWidth();
                        }
                        currentView.slideDistance(distance);
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(openSlide){
                    if(moved){
                        boolean slideToLeft = ((currentView.move_X - ev.getX())>0)?true:false;
                        if(slideToLeft){//向左滑动
                            /**
                             * 场景1、隐藏左侧菜单
                             * 场景2、弹出右侧菜单
                             * 场景3、隐藏左侧菜单后直接弹出右侧菜单
                             * */
                            if(currentView.direction == CrossSlipView.DIRECTION.SLIDE_TO_OPEN_LEFT){
                                if( currentView.isRightExist && currentView.isLeftExist && (currentView.move_X - ev.getX())>(currentView.leftView.getWidth() / 2)
                                        && (currentView.move_X - ev.getX())<(currentView.rightView.getWidth() / 2 + currentView.leftView.getWidth())){
                                    //关闭左侧菜单和右侧菜单
                                    currentView.closeMenu();
                                    closed = true;
                                }else if( currentView.isRightExist &&  currentView.isLeftExist &&
                                        (currentView.move_X - ev.getX())>(currentView.rightView.getWidth() / 2 + currentView.leftView.getWidth())){
                                    //关闭左侧菜单并直接弹出右侧菜单
                                    currentView.openFullRightMenu();
                                    closed = false;
                                }else if(currentView.isRightExist && ! currentView.isLeftExist && (currentView.move_X - ev.getX())>(currentView.rightView.getWidth() / 2)){
                                    //弹出右侧菜单
                                    currentView.openRightMenu();
                                    closed = false;
                                }else{
                                    //关闭左侧菜单
                                    currentView.closeMenu();
                                    closed = true;
                                }
                            }else if(currentView.direction == CrossSlipView.DIRECTION.CLOSE){
                                if( currentView.isRightExist &&(currentView.move_X - ev.getX())>(currentView.rightView.getWidth() / 2)){
                                    //弹出右侧菜单
                                    currentView.openRightMenu();
                                    closed = false;
                                }else{
                                    //关闭右侧菜单
                                    currentView.closeMenu();
                                    closed = true;
                                }
                            }
                        }else{//向右滑动
                            /**
                             * 场景1、隐藏右侧菜单
                             * 场景2、弹出左侧菜单
                             * 场景3、隐藏右侧菜单后直接弹出左侧菜单
                             * */
                            if(currentView.direction == CrossSlipView.DIRECTION.SLIDE_TO_OPEN_RIGHT){
                                if( currentView.isLeftExist &&  currentView.isRightExist &&
                                        Math.abs(currentView.move_X - ev.getX()) > (currentView.rightView.getWidth() / 2)
                                        && Math.abs(currentView.move_X - ev.getX())<(currentView.leftView.getWidth() / 2 +currentView.rightView.getWidth())){
                                    //关闭右侧菜单和左侧菜单
                                    currentView.closeMenu();
                                    closed = true;
                                }else if( currentView.isLeftExist && currentView.isRightExist &&
                                        Math.abs(currentView.move_X - ev.getX()) > (currentView.leftView.getWidth() / 2 +currentView.rightView.getWidth())){
                                    //关闭右侧菜单并直接打开左侧菜单
                                    currentView.openFullLeftMenu();
                                    closed = false;
                                }else{
                                    //关闭右侧菜单
                                    currentView.closeMenu();
                                    closed = true;
                                }
                            }else if(currentView.direction == CrossSlipView.DIRECTION.CLOSE){
                                if(currentView.isLeftExist && Math.abs(currentView.move_X - ev.getX()) > (currentView.leftView.getWidth() / 2)){
                                    //弹出左侧菜单
                                    currentView.openLeftMenu();
                                    closed = false;
                                }else{
                                    //关闭左侧菜单
                                    currentView.closeMenu();
                                    closed = true;
                                }
                            }
                        }
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }
                break;

        }
        return super.onTouchEvent(ev);
    }

    public boolean isOpenSlide()
    {
        return openSlide;
    }

    public void setOpenSlide(boolean openSlide)
    {
        this.openSlide = openSlide;
    }

    /** 将dp转换为px*/
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

}
