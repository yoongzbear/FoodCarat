/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author mastu
 */
public class ChartUtility {
    //generate pie chart
    public static ChartPanel createPieChart(DefaultPieDataset dataset, String chartTitle) {
        JFreeChart pieChart = ChartFactory.createPieChart(
            chartTitle,
            dataset,
            true,
            true,
            false
        );

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(325, 200));
        return chartPanel;
    }
    
    //generate bar chart
    public static ChartPanel createBarChart(DefaultCategoryDataset dataset, String chartTitle, String xAxis, String yAxis) {
        JFreeChart barChart = ChartFactory.createBarChart(
                chartTitle,
                xAxis, //x-axis
                yAxis, //y-axis
                dataset, //dataset
                PlotOrientation.VERTICAL, //orientation
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(325, 200));
        return chartPanel;
    }
}
