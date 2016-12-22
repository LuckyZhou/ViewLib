package view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * description：日期选择器，必须调用init
 * ===============================
 * creator：zhoudeshan
 * create time：2016/10/23 05:02 PM
 * ===============================
 * reasons for modification：
 * Modifier：
 * Modify time：
 */
public class DateSelectView extends LinearLayout implements LoopTextView.OnItemSelectedListener {

    private static final int DEFAULT_MAX_YEAR = 2025;
    private static final int DEFAULT_MIN_YEAR = 1991;
    /**
     * 年的选择view
     */
    private LoopTextView yearLoopView;
    /**
     * 月的选择view
     */
    private LoopTextView monthLoopView;
    /**
     * 日的选择viw
     */
    private LoopTextView dayLoopView;

    /**
     * 最大的日期
     */
    private DateItem maxDateItem;
    /**
     * 最小的日期
     */
    private DateItem minDateItem;

    /**
     * 当前被选中的日期
     */
    private DateItem currentDateItem;

    public DateSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setGravity(HORIZONTAL);
        addChildDateLoopTextView();
    }

    public DateSelectView(Context context) {
        super(context);

        setGravity(HORIZONTAL);
        addChildDateLoopTextView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 添加日期滚动的item view
     * 年，月，日，水平的三个
     */
    private void addChildDateLoopTextView() {
        yearLoopView = new LoopTextView(getContext());
        monthLoopView = new LoopTextView(getContext());
        dayLoopView = new LoopTextView(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        yearLoopView.setLayoutParams(params);
        monthLoopView.setLayoutParams(params);
        dayLoopView.setLayoutParams(params);

        yearLoopView.setOnItemClick(this);
        monthLoopView.setOnItemClick(this);
        dayLoopView.setOnItemClick(this);

        addView(yearLoopView);
        addView(monthLoopView);
        addView(dayLoopView);
    }

    @Override
    public void onItemSelected(int currentSelectedIndex, View view) {
        if (view == null || yearLoopView == null || monthLoopView == null || dayLoopView == null || currentDateItem == null) {
            return;
        }

        if (view == yearLoopView) {
            currentDateItem.year = getMinSelectableYear() + currentSelectedIndex;
            if (monthLoopView.setmData(getMonthData())) {
                monthLoopView.invalidate();
            }

            if (dayLoopView.setmData(getDayData())) {
                dayLoopView.invalidate();
            }
        }

        if (view == monthLoopView) {
            currentDateItem.month = getMinSelectableMonth() + currentSelectedIndex;
            if (dayLoopView.setmData(getDayData())) {
                dayLoopView.invalidate();
            }
        }

        if (view == dayLoopView) {
            currentDateItem.day = getMinSelectableDay() + currentSelectedIndex;
        }
    }

    /**
     * 初始化
     * 设置最大的可以选择的日期，如果传null， 默认最大可选日期为DEFAULT_MAX_YEAR 12 31
     * 设置最小的可以选择的日期，如果传null，默认最小可选日期为DEFAULT_MIN_YEAR 1 1
     * 设置当前选中的日期，如果传null，当前选中的日期为 DEFAULT_MIN_YEAR 1 1
     */
    public void init(DateItem maxDate, DateItem minDate, DateItem defaultSelectedDate) {
        this.maxDateItem = maxDate == null ? new DateItem(DEFAULT_MAX_YEAR, 12, 31) : maxDate;
        this.minDateItem = minDate == null ? new DateItem(DEFAULT_MIN_YEAR, 1, 1) : minDate;
        this.currentDateItem = defaultSelectedDate == null ? new DateItem(minDateItem.year, minDateItem.month, minDateItem.day) : defaultSelectedDate;

        setLoopViewData();
    }

    /**
     * 设置日期选择器的数据
     */
    private void setLoopViewData() {
        if (yearLoopView == null || monthLoopView == null || dayLoopView == null || currentDateItem == null) {
            return;
        }

        yearLoopView.setmData(getYearsData());
        monthLoopView.setmData(getMonthData());
        dayLoopView.setmData(getDayData());

        yearLoopView.setSelect(currentDateItem.year - minDateItem.year);
        monthLoopView.setSelect(currentDateItem.month - getMinSelectableMonth());
        dayLoopView.setSelect(currentDateItem.day - getMinSelectableDay());

        yearLoopView.invalidate();
        monthLoopView.invalidate();
        dayLoopView.invalidate();
    }

    /**
     * 获取年的可以选择的数据
     *
     * @return 可以选择的年的数目
     */
    public ArrayList<String> getYearsData() {
        return addIntegerToList(getMaxSelectableYear(), getMinSelectableYear(), "年");
    }

    /**
     * 获取月份
     *
     * @return 可以选择的月份
     */
    public ArrayList<String> getMonthData() {
        return addIntegerToList(getMaxSelectableMonth(), getMinSelectableMonth(), "月");
    }

    /**
     * 获取天
     *
     * @return 可以选择的天
     */
    public ArrayList<String> getDayData() {
        return addIntegerToList(getMaxSelectableDay(), getMinSelectableDay(), "日");
    }


    /**
     * 添加int 到选择的数组中
     *
     * @param maxSelectableYear 最多可以选择的年份
     * @param minSelectableYear 最小可以选择的年份
     *
     * @return 显示的可以选择的年份的数组
     */
    private ArrayList<String> addIntegerToList(int maxSelectableYear, int minSelectableYear, String appendString) {
        ArrayList<String> data = new ArrayList<String>();
        for (int year = minSelectableYear; year <= maxSelectableYear; year++) {
            if (year < 10) {
                data.add("0" + year + appendString);
            } else {
                data.add(year + appendString);
            }
        }

        return data;
    }


    /**
     * 获取最大可以选择的年份
     *
     * @return 年份
     */
    private int getMaxSelectableYear() {
        return maxDateItem == null ? DEFAULT_MAX_YEAR : maxDateItem.year;
    }

    /**
     * 获取最小可以选择的年份
     *
     * @return 年份
     */
    private int getMinSelectableYear() {
        return minDateItem == null ? DEFAULT_MIN_YEAR : minDateItem.year;
    }

    /**
     * 获取最大可以选择的月份
     *
     * @return 月份
     */
    private int getMaxSelectableMonth() {
        if (currentDateItem == null || maxDateItem == null) {
            return 12;
        }

        return currentDateItem.year == maxDateItem.year ? maxDateItem.month : 12;
    }

    /**
     * 获取最小可以选择的月份
     *
     * @return 月份
     */
    private int getMinSelectableMonth() {
        if (currentDateItem == null || minDateItem == null) {
            return 1;
        }
        return currentDateItem.year == minDateItem.year ? minDateItem.month : 1;
    }

    /**
     * 获取最大可以选择的日子
     *
     * @return 日子
     */
    private int getMaxSelectableDay() {
        if (currentDateItem == null || maxDateItem == null) {
            return getNormalDays();
        }

        return (currentDateItem.year == maxDateItem.year && currentDateItem.month == maxDateItem.month) ? Math.min(maxDateItem.day, getNormalDays()) : getNormalDays();
    }

    /**
     * 获取最小可以选择的日子
     *
     * @return 日子
     */
    private int getMinSelectableDay() {
        if (currentDateItem == null || minDateItem == null) {
            return 1;
        }

        return (currentDateItem.year == minDateItem.year && currentDateItem.month == minDateItem.month) ? minDateItem.day : 1;
    }

    /**
     * 获取通常的最大的天数
     *
     * @return 最大的天数
     */
    private int getNormalDays() {
        if (currentDateItem == null) {
            return 31;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, currentDateItem.year);
        calendar.set(Calendar.MONTH, currentDateItem.month - 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    public static class DateItem {
        private int year;
        private int month;
        private int day;

        /**
         * 初始化，设置年月日
         *
         * @param year  年，数字，比如2016
         * @param month 月，数字，从1开始，比如1
         * @param day   日，数字，不可以大于31，不可以小于1
         */
        public DateItem(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }
    }

    /**
     * 获取当前选中的 年 月 日
     */
    public DateItem getCurrentSelectedDate() {
        return currentDateItem;
    }
}
