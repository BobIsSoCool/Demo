package com.evecom.myview;
import android.content.ContentValues;
import android.content.Context;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * @author zheng.li
 * CrossSlipListView配对item
 * */
public class CrossSlipView extends FrameLayout
{
    public View centerView,leftView,rightView;//中央视图，左侧视图，右侧视图
    private Scroller operationScroller;//滚动管理
    public boolean isLeftExist,isRightExist;//左右菜单存在标记
    public int move_X;//视图起始位移点
    private int baseX;//基准位X轴
    public static int SLIDE_TO_OPEN_LEFT = 0;//滑动左侧布局出现标记
    public static int SLIDE_TO_OPEN_RIGHT = 1;//滑动右侧布局出现标记
    public DIRECTION direction = DIRECTION.CLOSE;//默认位置状态是关闭
    public enum DIRECTION{//位置参数
        SLIDE_TO_OPEN_LEFT,SLIDE_TO_OPEN_RIGHT,CLOSE;
    }

    public CrossSlipView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public CrossSlipView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CrossSlipView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param conterView
     *        中央视图
     * @param leftView
     *        左侧视图
     * @param rightView
     *        右侧视图
     * */
    public CrossSlipView(View conterView,View leftView,View rightView){
        super(conterView.getContext());
        this.centerView = conterView;
        this.leftView = leftView;
        this.rightView = rightView;
        operationScroller = new Scroller(conterView.getContext(), new BounceInterpolator());

        isLeftExist = (null == leftView)?false:true;
        isRightExist = (null == rightView)?false:true;

        init();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(isLeftExist){
            leftView.measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        centerView.measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                getMeasuredHeight(), MeasureSpec.EXACTLY));
        if(isRightExist){
            rightView.measure(MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        // TODO Auto-generated method stub
        if(isLeftExist){
            leftView.layout(-leftView.getMeasuredWidth(), 0, 0, centerView.getMeasuredHeight());
        }
        centerView.layout(0, 0, getMeasuredWidth(), centerView.getMeasuredHeight());
        if(isRightExist){
            rightView.layout(getMeasuredWidth(), 0, getMeasuredWidth() + rightView.getMeasuredWidth(), centerView.getMeasuredHeight());
        }
    }

    @Override public void computeScroll()
    {
        // TODO Auto-generated method stub
        super.computeScroll();
        if(direction != DIRECTION.CLOSE){
            if(operationScroller.computeScrollOffset()){
                slideDistance(operationScroller.getCurrX());
                postInvalidate();
            }
        }else{
            if(operationScroller.computeScrollOffset()){
                slideDistance(baseX - operationScroller.getCurrX());
                postInvalidate();
            }
        }
    }

    /** 初始化控件*/
    private void init(){
        //设置一个item的宽和高，其实就是设置宽充满而已
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        if(isLeftExist){
            leftView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            addView(leftView);
        }
        centerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        addView(centerView);
        if(isRightExist){
            rightView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            addView(rightView);
        }
    }



    /** 左右滑动距离 */
    public void slideDistance(int distance){
        if(direction == DIRECTION.SLIDE_TO_OPEN_LEFT){
            if(distance < -leftView.getWidth()){
                distance = -leftView.getWidth();
            }
            if(distance > 0){
                distance = 0;
            }
        }else if(direction == DIRECTION.SLIDE_TO_OPEN_RIGHT){
            if(distance > rightView.getWidth()){
                distance = rightView.getWidth();
            }
            if(distance < 0){
                distance = 0;
            }
        }else{
            if(distance >= 0){
                if(isRightExist){
                    if(distance > rightView.getWidth()){
                        distance = rightView.getWidth();
                    }
                    if(distance < 0){
                        distance = 0;
                    }
                }else{
                    if(distance > 0){
                        distance = 0;
                    }
                }

            }else{
                if(isLeftExist){
                    if(distance < -leftView.getWidth()){
                        distance = -leftView.getWidth();
                    }
                    if(distance > 0){
                        distance = 0;
                    }
                }else{
                    if(distance < 0){
                        distance = 0;
                    }
                }

            }
        }
        if(isLeftExist){
            leftView.layout(-distance - leftView.getWidth(),  centerView.getTop(), -distance, getMeasuredHeight());
        }
        centerView.layout(-distance,  centerView.getTop(), centerView.getWidth() - distance, getMeasuredHeight());
        if(isRightExist){
            rightView.layout(centerView.getWidth() - distance,  centerView.getTop(), centerView.getWidth() + rightView.getWidth() - distance, getMeasuredHeight());
        }

    }

    /** 弹出右侧菜单*/
    public void openRightMenu(){
        direction = DIRECTION.SLIDE_TO_OPEN_RIGHT;
        operationScroller.startScroll(-centerView.getLeft(), 0, rightView.getWidth() / 2, 0, 500);
        postInvalidate();
    }

    /** 弹出左侧菜单*/
    public void openLeftMenu(){
        Log.w("zheng.li","左侧菜单");
        direction = DIRECTION.SLIDE_TO_OPEN_LEFT;
        operationScroller.startScroll(-centerView.getLeft(), 0, -leftView.getWidth() / 2, 0, 500);
        postInvalidate();
    }

    /** 弹出右侧菜单*/
    public void openFullRightMenu(){
        direction = DIRECTION.SLIDE_TO_OPEN_RIGHT;
        operationScroller.startScroll(-centerView.getLeft(), 0, rightView.getWidth(), 0, 500);
        postInvalidate();
    }

    /** 弹出左侧菜单*/
    public void openFullLeftMenu(){
        Log.w("zheng.li","左侧菜单");
        direction = DIRECTION.SLIDE_TO_OPEN_LEFT;
        operationScroller.startScroll(-centerView.getLeft(), 0, -leftView.getWidth(), 0, 500);
        postInvalidate();
    }

    /** 关闭菜单*/
    public void closeMenu(){
        Log.w("zheng.li","关闭菜单");
        direction = DIRECTION.CLOSE;
        baseX = -centerView.getLeft();
        operationScroller.startScroll(0, 0, -centerView.getLeft(), 0, 500);
        postInvalidate();
    }


}
