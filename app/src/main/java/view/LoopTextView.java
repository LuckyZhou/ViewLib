package view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wuba.zhuanzhuan.R;
import com.wuba.zhuanzhuan.utils.AppUtils;
import com.wuba.zhuanzhuan.utils.DimensUtil;

import java.util.ArrayList;


/**
 * description：可以无限滚动的文本框，垂直
 * ===============================
 * creator zhoudeshan
 * create time 016/8/16 15:02
 * ===============================
 * reasons for modification锛
 * Modifier
 * Modify time
 */
public class LoopTextView extends View {

    public LoopTextView(Context context) {
        super(context);
    }

    public LoopTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoopTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 某个item被选中的监听器
     */
    interface OnItemSelectedListener {
        /**
         * 某个item被选中的回调
         *
         * @param currentSelectedIndex 被选中的currentSelectedIndex
         */
        void onItemSelected(int currentSelectedIndex, View view);
    }

    /**
     * 需要显示的数据源
     */
    private ArrayList<String> mData;

    /**
     * item 被选中的监听器
     */
    private OnItemSelectedListener onItemSelectedListener;


    /**
     * 当前被选中的item距离中部选中区域顶部的位置，【0，mItemHeight】
     */
    private int mMoveSpace = 0;
    /**
     * 每个item的高度
     */
    private int mItemHeight = DimensUtil.dip2px(40);
    /**
     * 当前中部被选中的index
     */
    private int currentSelectedIndex;
    /**
     * 手指離開了屏幕之後的劃定速度
     */
    private float preLastSpeed;
    /**
     * 默認一边能够显示的数目，默认为3
     */
    private int maxCountOneSide = 3;
    /**
     * 文字的尺寸
     */
    private float textSize = DimensUtil.dip2px(14);
    /**
     * 中部被选中的文字绘制的基准线
     */
    private float base;

    /**
     * 中部分割线的颜色
     */
    private int middleLineColor = AppUtils.getColor(R.color.leave_message_bg_grey);
    /**
     * 中间分割线的高度
     */
    private float middleLineWidth = DimensUtil.dip2px(0.5f);
    /**
     * 没被选中的文字的颜色
     */
    private int commonTextColor = AppUtils.getColor(R.color.tv_goods_desc_text_color);
    /**
     * 处于选中区域的文字的颜色
     */
    private int selectedTextColor = AppUtils.getColor(R.color.zzBlackColorForText);

    /*******滚动相关************/
    /**
     * 之前按下的X
     */
    float mPreX;
    /**
     * 之前按下的Y
     */
    float mPreY;
    /**
     * 是否即将停止
     */
    boolean willStop;
    /**
     * 是否是往上移动
     */
    boolean isMoveUp;
    /**
     * 是否正在滚动
     */
    boolean isScroll = false;
    /**
     * 当前滚动的速度
     */
    float lastSpeed;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null) {
            return;
        }

        /***获取基准线***/
        base = (float) getMeasuredHeight() / 2 + textSize / 2;

        /***纠正currentSelectedIndex,mMoveSpace**/
        changeIndexAndMoveSpace();

        /***绘制每一个item***/
        drawItems(canvas);

        /***绘制中部的选择区域********/
        drawMiddleSelectedSpace(canvas);

        notifyListener();
    }

    /**
     * 绘制总部的分割线
     */
    private void drawMiddleSelectedSpace(Canvas canvas) {
        canvas.drawLine(0, getMiddleLineTwoTop(), getMeasuredWidth(), getMiddleLineTwoTop(), middleLinePaint);
        canvas.drawLine(0, getMiddleLineOneTop(), getMeasuredWidth(), getMiddleLineOneTop(), middleLinePaint);
    }

    /**
     * 纠正index 与 mMoveSpace
     * 当选中的item距离中部选中区域的顶部的值（mMoveSpace）小于0的时候，调整index + 1
     * 但选中的item距离总部选中区域的顶部的值（mMoveSpace）大于最大高度的时候，调整 index - 1
     *
     * 但调整后的index 超过数据数目，或者小于0，循环调整数据
     */
    private void changeIndexAndMoveSpace() {
        if (mMoveSpace > 0) {
            currentSelectedIndex = currentSelectedIndex - mMoveSpace / mItemHeight;
        } else if (mMoveSpace < 0) {
            currentSelectedIndex = currentSelectedIndex - mMoveSpace / mItemHeight + 1;
        }

        if (currentSelectedIndex < 0) {
            currentSelectedIndex = currentSelectedIndex + mData.size();
        } else if (currentSelectedIndex >= mData.size()) {
            currentSelectedIndex = mData.size() - currentSelectedIndex;
        }

        if (mMoveSpace < 0) {
            mMoveSpace = mItemHeight;
        } else if (mMoveSpace >= mItemHeight) {
            mMoveSpace = 0;
        }
    }

    /**
     * 绘制每一个item
     * 绘制两层，实现黑色选中的效果
     * 文字的位置以来 mMoveSpace，currentSelectedIndex
     * @param canvas 绘制幕布
     */
    private void drawItems(Canvas canvas) {
        canvas.save();
        canvas.drawRect(new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight()), middleBlackSpacePaint);
        drawItems(canvas, textPaint);
        canvas.restore();

        canvas.save();
        canvas.clipRect(0, base - (mItemHeight - textSize) / 2 - textSize + 15, getMeasuredWidth(), base + (mItemHeight - textSize) / 2);
        canvas.drawRect(new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight()), middleBlackSpacePaint);
        drawItems(canvas, hardColorPaint);
        canvas.restore();
    }

    /**
     * 获取选中区域第二条线的高度
     * @return 高度
     */
    private float getMiddleLineTwoTop() {
        return base + (mItemHeight - textSize) / 2;
    }

    /**
     * 获取选中区域第一条线的高度
     * @return 高度
     */
    public float getMiddleLineOneTop() {
        return base - (mItemHeight - textSize) / 2 - textSize + 15;
    }

    /**
     * 绘制每一个item
     * @param canvas 幕布
     * @param paint 画笔
     */
    private void drawItems(Canvas canvas, Paint paint) {
        int currentSelectedIndexBaseLine = (int) (base + mMoveSpace);
        for (int i = 0; i <= 2 * maxCountOneSide + 1; i++) {
            float baseLine = currentSelectedIndexBaseLine - mItemHeight * (maxCountOneSide - i);
            canvas.save();
            int start = (int) ((getMeasuredWidth() - paint.measureText(getData(i))) / 2 + 0.5f);
            canvas.drawText(getData(i), start, baseLine, paint);
            canvas.restore();
        }
    }

    /**
     * 获取当前需要显示的数据
     * @param index 数据的位置
     * @return 需要展示的数据
     */
    private String getData(int index) {
        if (mData == null) {
            return "";
        }
        int position = currentSelectedIndex - (maxCountOneSide - index);
        if (position < 0) {
            position = currentSelectedIndex - (maxCountOneSide - index) + mData.size();
        }

        if (position >= mData.size()) {
            position = index + currentSelectedIndex - maxCountOneSide - mData.size();
        }

        if(position < 0){
            position = 0;
        }
        if(position >= mData.size()){
            position = mData.size() - 1;
        }
        return mData.get(position);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clearAllState();
                mPreX = event.getX();
                mPreY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                isMoveUp = event.getY() < mPreY;
                float dx = event.getX() - mPreX;
                float dy = event.getY() - mPreY;
                mPreX = event.getX();
                mPreY = event.getY();
                if (Math.abs(dy) > Math.abs(dx / 2)) {
                    mMoveSpace = (int) (mMoveSpace + dy);
                    invalidate();
                    lastSpeed = dy;
                }
                break;
            case MotionEvent.ACTION_UP:
                mPreX = event.getX();
                mPreY = event.getY();
                lastSpeed = Math.abs(lastSpeed) > mItemHeight ? (lastSpeed > 0) ? mItemHeight - 10 : -(mItemHeight - 10) : lastSpeed;
                startScroll();
            default:
                break;
        }
        return true;
    }

    /**
     * 清楚所有的滚动状态，并且取消滚动
     */
    private void clearAllState() {
        lastSpeed = 0;
        preLastSpeed = 0;
        isScroll = false;
        willStop = false;
        isMoveUp = false;
        removeCallbacks(runnable);
    }

    /**
     * 启动滚动的逻辑
     */
    private void startScroll() {
        isScroll = true;
        preLastSpeed = lastSpeed;
        startNextScroll();
    }

    /**
     * 开启下一次的滚动
     */
    private void startNextScroll() {
        postDelayed(runnable, 18);
    }

    /**
     * 处理滚动的runnable，每隔18ms发送一次滚动绘制
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (willStop && mMoveSpace == 0) {
                isScroll = false;
            }

            if (!isScroll) {
                return;
            }

            float minSpeed = mItemHeight / 40;
            if (willStop || !((preLastSpeed > 0 && lastSpeed > 0) || (preLastSpeed < 0 && lastSpeed < 0)) || Math.abs(lastSpeed) < 2 * minSpeed) {
                willStop = true;
                if (Math.abs(preLastSpeed) >= 2 * minSpeed) {
                    mMoveSpace += isMoveUp ? -2 * minSpeed : 2 * minSpeed;
                } else {
                    mMoveSpace += (mMoveSpace < (mItemHeight / 2)) ? -2 * minSpeed : 2 * minSpeed;
                }
                if (mMoveSpace < 0) {
                    mMoveSpace = 0;
                }

            } else {
                if (lastSpeed > 0) {
                    lastSpeed -= minSpeed;
                } else if (lastSpeed < 0) {
                    lastSpeed += minSpeed;
                }

                mMoveSpace += lastSpeed;
            }

            invalidate();
            postDelayed(runnable, 18);
        }
    };

    /**
     * 通知item select监听器
     */
    private void notifyListener() {
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(currentSelectedIndex, this);
        }
    }

    /**
     * 画笔
     */
    TextPaint textPaint = new TextPaint();
    TextPaint middleBlackSpacePaint = new TextPaint();
    TextPaint hardColorPaint = new TextPaint();
    TextPaint middleLinePaint = new TextPaint();

    {
        textPaint.setTextSize(textSize);
        hardColorPaint.setTextSize(textSize);
        middleLinePaint.setStrokeWidth(middleLineWidth);

        textPaint.setAntiAlias(true);
        hardColorPaint.setAntiAlias(true);
        middleBlackSpacePaint.setAntiAlias(true);
        middleLinePaint.setAntiAlias(true);

        textPaint.setColor(commonTextColor);
        hardColorPaint.setColor(selectedTextColor);
        middleBlackSpacePaint.setColor(AppUtils.getColor(R.color.white));
        middleLinePaint.setColor(middleLineColor);
    }

    /**
     * 设置显示的数据源
     * @param mData 数据源
     */
    public boolean setmData(ArrayList<String> mData) {
        if(mData == null || mData.equals(this.mData)){
            return false;
        }

        this.mData = mData;
        willStop = true;

        if(currentSelectedIndex >= mData.size()){
            currentSelectedIndex = mData.size() - 1;
        }

        if(currentSelectedIndex < 0){
            currentSelectedIndex = 0;
        }

        return true;
    }

    /**
     * 设置当前被选中的index
     * @param selectIndex 被选中的index
     */
    public void setSelect(int selectIndex) {
        this.currentSelectedIndex = selectIndex;
    }

    /**
     * 设置监听器，每次滑动完成之后，在重新绘制
     * @param onItemSelectedListener item的选择监听器
     */
    public void setOnItemClick(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
}