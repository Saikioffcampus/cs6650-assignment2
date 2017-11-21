package com.cs6650.assignment2.client;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.*;

/**
 * Created by saikikwok on 18/11/2017.
 */
public class ClientChartFactory extends JFrame {
    private XYSeriesCollection dataCollection;

    public ClientChartFactory(){
        dataCollection = new XYSeriesCollection();
    }

    public void getChart(ConcurrentLinkedQueue<Long> dataSet, String chartTitle) throws IOException {
        XYSeries series = new XYSeries(chartTitle);
        for(int i = 0; !dataSet.isEmpty(); i++){
            series.add(i, dataSet.poll());
        }
        dataCollection.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "Request",
                "Latancy", dataCollection);

        File file = new File(chartTitle);
        ChartUtilities.saveChartAsJPEG(file, chart, 500, 270);
    }

}
