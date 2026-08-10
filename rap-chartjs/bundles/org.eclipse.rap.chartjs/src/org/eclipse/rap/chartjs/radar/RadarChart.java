package org.eclipse.rap.chartjs.radar;

import org.eclipse.rap.chartjs.AbstractChart;
import org.eclipse.swt.widgets.Composite;

public class RadarChart extends AbstractChart
{

    private static final long serialVersionUID = -1772500179510502494L;

    public RadarChart(Composite parent, int style)
    {
        super(parent, style);

    }

    public void load(RadarChartRowData data, RadarChartOptions options)
    {
        drawChart("radar", options.toJson(), data.toJson());
    }

}
