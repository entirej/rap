package org.eclipse.rap.chartjs.bar;

import org.eclipse.rap.chartjs.AbstractChart;
import org.eclipse.swt.widgets.Composite;

public class BarChart extends AbstractChart
{

    private static final long serialVersionUID = -1772500179510502494L;

    public BarChart(Composite parent, int style)
    {
        super(parent, style);

    }

    public void load(BarChartRowData data, BarChartOptions options)
    {
        options.indexAxis = "x";
        drawChart("bar", options.toJson(), data.toJson(options));
    }
    public void loadHorizontal(BarChartRowData data, BarChartOptions options)
    {
        options.indexAxis = "y";
        drawChart("bar", options.toJson(), data.toJson(options));
    }

}
