package org.eclipse.rap.chartjs.pie;

import org.eclipse.rap.chartjs.AbstarctChartOptions;

public class PieChartOptions extends AbstarctChartOptions
{
    private String viewType = "pie";
    public PieChartOptions()
    {

    }
    
    public void setViewType(String viewType)
    {
        this.viewType = viewType;
    }
    
    public String getViewType()
    {
        return viewType;
    }

}
