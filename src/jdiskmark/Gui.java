
package jdiskmark;

import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import javax.swing.JProgressBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 * Store gui references for easy access
 */
public final class Gui {
    
    public static ChartPanel chartPanel = null;
    public static MainFrame mainFrame = null;
    public static SelectFrame selFrame = null;
    public static XYSeries wSeries, wAvgSeries, wMaxSeries, wMinSeries, wIOps;
    public static XYSeries rSeries, rAvgSeries, rMaxSeries, rMinSeries, rIOps;
    public static JFreeChart chart;
    public static JProgressBar progressBar = null;
    public static RunPanel runPanel = null;
    
    public static ChartPanel createChartPanel() {
        
        wSeries = new XYSeries("Writes");
        wAvgSeries = new XYSeries("Write Avg");
        wMaxSeries = new XYSeries("Write Max");
        wMinSeries = new XYSeries("Write Min");
        wIOps = new XYSeries("Write operations per second");
        
        rSeries = new XYSeries("Reads");
        rAvgSeries = new XYSeries("Read Avg");
        rMaxSeries = new XYSeries("Read Max");
        rMinSeries = new XYSeries("Read Min");
        rIOps = new XYSeries("Read operations per second");
        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(wSeries);
        dataset.addSeries(wAvgSeries);
        dataset.addSeries(wMaxSeries);
        dataset.addSeries(wMinSeries);
        dataset.addSeries(rSeries);
        dataset.addSeries(rAvgSeries);
        dataset.addSeries(rMaxSeries);
        dataset.addSeries(rMinSeries);


        XYSeriesCollection datasetIOps = new XYSeriesCollection();
        datasetIOps.addSeries(wIOps);
        datasetIOps.addSeries(rIOps);
        
        chart = ChartFactory.createXYLineChart(
                        "XY Chart", // Title
                        null, // x-axis Label
                        null, // y-axis Label
                        null, // Dataset
                        PlotOrientation.VERTICAL, // Plot Orientation
                        true,// Show Legend
                        true, // Use tooltips
                        false // Configure chart to generate URLs?
                );
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.DARK_GRAY);


        NumberAxis bandwithAxis = new NumberAxis("Bandwidth MB/s");
        bandwithAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis operationsAxis = new NumberAxis("Operations per second");
        operationsAxis.setAutoRangeIncludesZero(false);
            
        plot.setRangeAxis(0, bandwithAxis);
        plot.setRangeAxis(1, operationsAxis);

        NumberAxis range = (NumberAxis) plot.getDomainAxis();
        range.setNumberFormatOverride(NumberFormat.getNumberInstance());

        plot.setDataset(0, dataset);
        plot.setDataset(1, datasetIOps);
        
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        XYLineAndShapeRenderer rendererBandwith = new XYLineAndShapeRenderer(); 
        XYLineAndShapeRenderer rendererIOps = new XYLineAndShapeRenderer();

        plot.setRenderer(0, rendererBandwith); 
        plot.setRenderer(1, rendererIOps); 

        chart.getTitle().setVisible(false);
        chartPanel = new ChartPanel(chart) {
            // Only way to set the size of chart panel
            // ref: http://www.jfree.org/phpBB2/viewtopic.php?p=75516
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 325);
            }
        };
        
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.YELLOW);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(1, Color.WHITE);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(2, Color.GREEN);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(3, Color.RED);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(4, Color.LIGHT_GRAY);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(5, Color.ORANGE);

        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(0, Color.BLUE);
        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(1, Color.WHITE);
        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(2, Color.GREEN);
        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(3, Color.RED);
        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(4, Color.LIGHT_GRAY);
        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(5, Color.YELLOW);
        
        updateLegend();
        return chartPanel;
    }
    
    public static void addWriteMark(DiskMark mark) {
        wIOps.add(mark.markNum, mark.iops);
        wSeries.add(mark.markNum, mark.bwMbSec);
        wAvgSeries.add(mark.markNum, mark.cumAvg);
        if (App.showMaxMin) {
            wMaxSeries.add(mark.markNum, mark.cumMax);
            wMinSeries.add(mark.markNum, mark.cumMin);
        }
        Gui.mainFrame.refreshWriteMetrics();
        System.out.println(mark.toString());
    }
    public static void addReadMark(DiskMark mark) {
        rIOps.add(mark.markNum, mark.iops);
        rSeries.add(mark.markNum, mark.bwMbSec);
        rAvgSeries.add(mark.markNum, mark.cumAvg);
        if (App.showMaxMin) {
            rMaxSeries.add(mark.markNum, mark.cumMax);
            rMinSeries.add(mark.markNum, mark.cumMin);
        }
        Gui.mainFrame.refreshReadMetrics();
        System.out.println(mark.toString());
    }
    
    public static void resetTestData() {
        wIOps.clear();
        rIOps.clear();
        wSeries.clear();
        rSeries.clear();
        wAvgSeries.clear();
        rAvgSeries.clear();
        wMaxSeries.clear();
        rMaxSeries.clear();
        wMinSeries.clear();
        rMinSeries.clear();
        progressBar.setValue(0);
        Gui.mainFrame.refreshReadMetrics();
        Gui.mainFrame.refreshWriteMetrics();
    }
    
    public static void updateLegend() {
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(0, App.writeTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(1, App.writeTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(2, App.writeTest&&App.showMaxMin);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(3, App.writeTest&&App.showMaxMin);
        
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(4, App.readTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(5, App.readTest);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(6, App.readTest&&App.showMaxMin);
        chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(7, App.readTest&&App.showMaxMin);

    }
}
