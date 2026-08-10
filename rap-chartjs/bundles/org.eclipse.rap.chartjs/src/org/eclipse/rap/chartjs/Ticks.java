package org.eclipse.rap.chartjs;

import org.eclipse.rap.json.JsonObject;

public class Ticks
{
    private Boolean beginAtZero;
    private boolean display = true;
    private Double  min;
    private Double  max;
    private Double  stepSize      ;
    private Double  suggestedMax;
    private Double  suggestedMin;

    private Integer maxTicksLimit = 20;

    public Boolean getBeginAtZero()
    {
        return beginAtZero;
    }
    
    public void setDisplay(boolean display)
    {
        this.display = display;
    }
    
    public boolean isDisplay()
    {
        return display;
    }

    public void setBeginAtZero(Boolean beginAtZero)
    {
        this.beginAtZero = beginAtZero;
    }

    public Double getMin()
    {
        return min;
    }

    public void setMin(Double min)
    {
        this.min = min;
    }

    public Double getMax()
    {
        return max;
    }

    public void setMax(Double max)
    {
        this.max = max;
    }

    public Double getStepSize()
    {
        return stepSize;
    }

    public void setStepSize(Double stepSize)
    {
        this.stepSize = stepSize;
    }

    public Double getSuggestedMax()
    {
        return suggestedMax;
    }

    public void setSuggestedMax(Double suggestedMax)
    {
        this.suggestedMax = suggestedMax;
    }

    public Double getSuggestedMin()
    {
        return suggestedMin;
    }

    public void setSuggestedMin(Double suggestedMin)
    {
        this.suggestedMin = suggestedMin;
    }

    public Integer getMaxTicksLimit()
    {
        return maxTicksLimit;
    }

    public void setMaxTicksLimit(Integer maxTicksLimit)
    {
        this.maxTicksLimit = maxTicksLimit;
    }

    public JsonObject toJson()
    {
        JsonObject result = new JsonObject();
        if (beginAtZero != null)
            result.add("beginAtZero", beginAtZero);
        if (min != null)
            result.add("min", min);
        if (max != null)
            result.add("max", max);
        if (maxTicksLimit != null)
            result.add("maxTicksLimit", maxTicksLimit);
        if (stepSize != null)
            result.add("stepSize", stepSize);
        if (suggestedMax != null)
            result.add("suggestedMax", suggestedMax);
        if (suggestedMin != null)
            result.add("suggestedMin", suggestedMin);
        result.add("display", display);
        return result;
    }
    public JsonObject toJson( JsonObject result)
    {
        
        if (beginAtZero != null)
            result.add("beginAtZero", beginAtZero);
        if (min != null)
            result.add("min", min);
        if (max != null)
            result.add("max", max);
       
        if (suggestedMax != null)
            result.add("suggestedMax", suggestedMax);
        if (suggestedMin != null)
            result.add("suggestedMin", suggestedMin);
    
        return result;
    }

    /*
     * beginAtZero Boolean if true, scale will include 0 if it is not already
     * included. min Number User defined minimum number for the scale, overrides
     * minimum value from data. more... max Number User defined maximum number
     * for the scale, overrides maximum value from data. more... maxTicksLimit
     * Number 11 Maximum number of ticks and gridlines to show. stepSize Number
     * User defined fixed step size for the scale. more... suggestedMax Number
     * Adjustment used when calculating the maximum data value. more...
     * suggestedMin Number Adjustment used when calculating the minimum data
     * value. more...
     */

}
