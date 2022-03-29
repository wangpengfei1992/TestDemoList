package com.wpf.myviewtest.ui.chart;

/**
 * Author: feipeng.wang
 * Time:   2022/2/15
 * Description : This is description.
 */

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * 蜡烛图和折线图的组合图形
 */
public class CombinedChartUtils {
    private CombinedChart combinedChart;
    private XAxis xAxis;

    public CombinedChartUtils(CombinedChart combinedChart){
        this.combinedChart = combinedChart;
        initSetting();

    }

    /**
     * 常用设置
     */
    private void initSetting() {
        combinedChart.getDescription().setText("");
        combinedChart.getDescription().setTextColor(Color.RED);
        combinedChart.getDescription().setTextSize(16);//设置描述的文字 ,颜色 大小
        combinedChart.setNoDataText("无数据噢"); //没数据的时候显示
        //这里为了 可以使左右滑动
        Matrix m=new Matrix();
        m.postScale(1.5f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
        combinedChart.getViewPortHandler().refresh(m, combinedChart, false);//将图表动画显示之前进行缩放
        combinedChart.animateX(1000); // 立即执行的动画,x轴

        //设置图例
        Legend legend = combinedChart.getLegend();
//        legend.setForm(Legend.LegendForm.NONE); //直接禁止图例,x轴会显示不全, 用这种方法解决
//        legend.setTextColor(Color.TRANSPARENT);

        //设置X轴
        xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴位置
        xAxis.setAxisMinimum(1);//设置x轴最小
//        xAxis.setAxisMaximum(12);//设置x轴最大值
        xAxis.setTextSize(14);
        xAxis.setTextColor(Color.RED);
        xAxis.setEnabled(true);//是否显示x轴是否禁用
        xAxis.setDrawLabels(true); //设置x轴标签 即x轴上显示的数值
        xAxis.setDrawGridLines(true);//是否设置x轴上每个点对应的线 即 竖向的网格线
        xAxis.enableGridDashedLine(2,2,2); //竖线 虚线样式  lineLength控制虚线段的长度 spaceLength控制线之间的空间
        xAxis.setLabelRotationAngle(30f);//设置x轴标签的旋转角度
        xAxis.setGranularity(1f);//x轴上设置间隔尺寸

        //设置Y轴
        YAxis yAxisLef = combinedChart.getAxisLeft();
        yAxisLef.setTextSize(14);
        yAxisLef.setAxisMinimum(0);
        YAxis yAxisRight = combinedChart.getAxisRight();//获取右侧y轴
        yAxisRight.setEnabled(false);//设置是否禁止
    }

    /**
     * 设置单条折线图的K线图 数据
     */
    public void setSingleCombinedData(List<Entry> lineYVals, List<CandleEntry> candleYVals){
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum((float)(candleYVals.size()+0.5)); //防止出现显示一半柱状图的情况
        CombinedData data = new CombinedData();
        LineData lineData = getSingleMa(lineYVals);
        data.setData(lineData);
        CandleData candleData = getCandleData(candleYVals);
        data.setData(candleData);
        combinedChart.setData(data);
    }

    /**
     * 折线图(多条)
     * @param lineChartYs 折线Y轴值
     * @param lineNames   折线图名字
     * @param lineColors  折线颜色
     * @param candleYVals  K线图的y值
     * @return
     */
    public void setMoreCombinedData(List<List<Float>> lineChartYs, List<String> lineNames, List<Integer> lineColors, List<CandleEntry> candleYVals){
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum((float)(candleYVals.size()+1)); //防止出现显示一半柱状图的情况

        CombinedData data = new CombinedData();
        LineData lineData = getMoreMA(lineChartYs, lineNames, lineColors);
        data.setData(lineData);
        CandleData candleData = getCandleData(candleYVals);
        data.setData(candleData);
        combinedChart.setData(data);
    }

    /**
     * 获取蜡烛图(K线图)
     * @param candleYVals
     * @return
     */
    private CandleData getCandleData(List<CandleEntry> candleYVals){
        CandleDataSet candleDataSet = new CandleDataSet(candleYVals, "");
        candleDataSet.setValueTextColor(Color.BLACK);
        candleDataSet.setValueTextSize(14);
        candleDataSet.setShadowColor(Color.DKGRAY);//设置影线的颜色
        candleDataSet.setShadowWidth(0.5f);//设置影线的宽度
        candleDataSet.setShadowColorSameAsCandle(true);//设置影线和蜡烛图的颜色一样
        candleDataSet.setDecreasingColor(Color.GREEN);//设置减少色
        candleDataSet.setDecreasingPaintStyle(Paint.Style.STROKE);//绿跌，空心描边
        candleDataSet.setIncreasingColor(Color.RED);//设置增长色
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);//设置增长红 实心
        candleDataSet.setNeutralColor(Color.RED);//当天价格不涨不跌（一字线）颜色
        candleDataSet.setHighlightEnabled(true);//设置定位线是否可用
        candleDataSet.setHighLightColor(Color.BLACK); //设置定位线的颜色
        candleDataSet.setHighlightLineWidth(0.5f);//设置定位线的线宽
        candleDataSet.setBarSpace(0.9f);//0 至1 之间,越小蜡烛图的宽度越宽
        candleDataSet.setDrawValues(false);//设置是否显示蜡烛图上的文字
        CandleData candleData = new CandleData(candleDataSet);
        return candleData;
    }

    /**
     * 获取单条均线
     * @param lineYVals y轴值
     * @return
     */
    private LineData getSingleMa(List<Entry> lineYVals){
        LineDataSet lineDataSet = new LineDataSet(lineYVals, "");
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);// 设置平滑曲线
        lineDataSet.setHighLightColor(Color.RED); //设置高亮线的颜色
        lineDataSet.setHighlightEnabled(false);//设置高亮线是否可用
        lineDataSet.setColor(Color.BLACK);//设置折线颜色
        lineDataSet.setCircleColor(Color.BLUE);//设置交点的圆圈的颜色
        lineDataSet.setDrawCircles(false);//设置是否显示交点
        lineDataSet.setDrawValues(false); //设置是否显示交点处的数值
        lineDataSet.setValueTextColor(Color.RED); //设置交点上值的颜色
        lineDataSet.setValueTextSize(14);//设置交点上值的字体大小
        LineData lineData = new LineData(lineDataSet);
        return lineData;
    }

    /**
     * 折线图(多条)
     * @param lineChartYs 折线Y轴值
     * @param lineNames   折线图名字
     * @param lineColors  折线颜色
     * @return
     */
    private LineData getMoreMA(List<List<Float>> lineChartYs, List<String> lineNames, List<Integer> lineColors){
        LineData lineData = new LineData();

        for (int i = 0; i < lineChartYs.size(); i++) {
            ArrayList<Entry> yValues = new ArrayList<>();
            for (int j = 0; j < lineChartYs.get(i).size(); j++) {
                yValues.add(new Entry(j, lineChartYs.get(i).get(j)));
            }
            LineDataSet lineDataSet = new LineDataSet(yValues, lineNames.get(i));
            lineDataSet.setColor(lineColors.get(i));//设置折线颜色
            lineDataSet.setCircleColor(lineColors.get(i)); //设置交点圆的颜色
            lineDataSet.setValueTextColor(lineColors.get(i));
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);// 设置平滑曲线
            lineDataSet.setHighLightColor(Color.RED); //设置高亮线的颜色
            lineDataSet.setHighlightEnabled(false);//设置高亮线是否可用
            lineDataSet.setDrawCircles(false);//设置是否显示交点
            lineDataSet.setDrawValues(false); //设置是否显示交点处的数值
            lineDataSet.setValueTextSize(14);//设置交点上值的字体大小
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineData.addDataSet(lineDataSet);
        }
        return lineData;
    }
}
