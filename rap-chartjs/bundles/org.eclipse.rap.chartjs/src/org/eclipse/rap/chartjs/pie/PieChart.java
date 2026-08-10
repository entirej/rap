package org.eclipse.rap.chartjs.pie;

import org.eclipse.rap.chartjs.AbstractChart;
import org.eclipse.rap.chartjs.pie.PieChartRowData.RowInfo;
import org.eclipse.swt.widgets.Composite;

public class PieChart extends AbstractChart
{

    private static final long serialVersionUID = -1772500179510502494L;

    public PieChart(Composite parent, int style)
    {
        super(parent, style);

    }

    public void load(PieChartRowData data, PieChartOptions options)
    {
        
//        data = new PieChartRowData(new String[]{"l1","l2"});
//        RowInfo rowInfo = new RowInfo();
//        rowInfo.setLabel("1");
//        data.addRow(rowInfo, new double[]{100,200});
        drawChart(options.getViewType(), options.toJson(), data.toJson());
    }

}
