package org.eclipse.rap.chartjs.line;

import org.eclipse.rap.chartjs.AbstractChart;
import org.eclipse.swt.widgets.Composite;

public class LineChart extends AbstractChart
{

    private static final long serialVersionUID = -1772500179510502494L;

    public LineChart(Composite parent, int style)
    {
        super(parent, style);

    }

    public void load(LineChartRowData data, LineChartOptions options)
    {
        drawChart("line", options.toJson(), data.toJson());
    }

}
