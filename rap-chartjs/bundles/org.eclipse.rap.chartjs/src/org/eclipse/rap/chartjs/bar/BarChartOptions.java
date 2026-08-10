package org.eclipse.rap.chartjs.bar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.chartjs.AbstarctChartOptions;
import org.eclipse.rap.chartjs.Axis;
import org.eclipse.rap.json.JsonObject;

public class BarChartOptions extends AbstarctChartOptions
{

    float      barPercentage      = 0.9f;
    float      categoryPercentage = 0.8f;
    Integer    barThickness;
    Integer    maxBarThickness;
    String     indexAxis = "x";
    boolean    stacked;
    
    GridLines gridLines = new GridLines();

    List<Axis> yAxes              = new ArrayList<Axis>();
    List<Axis> xAxes              = new ArrayList<Axis>();

    public BarChartOptions()
    {
        yAxes.add(new Axis());// default
    }

    public List<Axis> getYAxes()
    {
        return yAxes;
    }
    
    public GridLines getGridLines()
    {
        return gridLines;
    }
    
    public List<Axis> getxAxes()
    {
        return xAxes;
    }

    public float getBarPercentage()
    {
        return barPercentage;
    }

    public void setBarPercentage(float barPercentage)
    {
        this.barPercentage = barPercentage;
    }

    public float getCategoryPercentage()
    {
        return categoryPercentage;
    }

    public void setCategoryPercentage(float categoryPercentage)
    {
        this.categoryPercentage = categoryPercentage;
    }

    public Integer getBarThickness()
    {
        return barThickness;
    }

    public void setBarThickness(Integer barThickness)
    {
        this.barThickness = barThickness;
    }

    public Integer getMaxBarThickness()
    {
        return maxBarThickness;
    }

    public void setMaxBarThickness(Integer maxBarThickness)
    {
        this.maxBarThickness = maxBarThickness;
    }
    
    public void setStacked(boolean stacked)
    {
        this.stacked = stacked;
    }
    @Override
    public AbstarctChartOptions setAnimation(boolean animation)
    {
        return super.setAnimation(animation);
    }
    
    public boolean isStacked()
    {
        return stacked;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject jsonObject = super.toJson();
    
        
        JsonObject axis = new JsonObject();
        
      
        
        jsonObject.add("scales", axis);
        jsonObject.add("indexAxis", indexAxis);
        for (Axis yaxis : yAxes)
        {
            JsonObject object = scaleOptions(yaxis);
            axis.add("y", object);
            yaxis.getTicks().toJson(object);
        }
        
        for (Axis xaxis : xAxes)
        {
            JsonObject object = scaleOptions(xaxis);
            axis.add("x", object);
            xaxis.getTicks().toJson(object);
        }
        
        if(xAxes.isEmpty())
        {
            axis.add("x", defaultScaleOptions());
        }
        if(yAxes.isEmpty())
        {
            axis.add("y", defaultScaleOptions());
        }

        return jsonObject;
    }

    void applyTo(JsonObject dataset)
    {
        dataset.add("barPercentage", barPercentage);
        dataset.add("categoryPercentage", categoryPercentage);
        if (barThickness != null)
            dataset.add("barThickness", barThickness);
        if (maxBarThickness != null)
            dataset.add("maxBarThickness", maxBarThickness);
    }

    private JsonObject scaleOptions(Axis axis)
    {
        return new JsonObject()
                .add("grid", gridLines.toJson())
                .add("border", gridLines.toBorderJson())
                .add("display", axis.isDisplay())
                .add("stacked", stacked || axis.isStacked())
                .add("ticks", axis.getTicks().toJson());
    }

    private JsonObject defaultScaleOptions()
    {
        return new JsonObject()
                .add("grid", gridLines.toJson())
                .add("border", gridLines.toBorderJson())
                .add("stacked", stacked);
    }
    
    public static class GridLines{
        boolean  display = true;
        boolean  drawBorder = false;
        
        public void setDisplay(boolean display)
        {
            this.display = display;
        }
        public boolean isDisplay()
        {
            return display;
        }
        
        public void setDrawBorder(boolean drawBorder)
        {
            this.drawBorder = drawBorder;
        }
        
        public boolean isDrawBorder()
        {
            return drawBorder;
        }
        
        public JsonObject toJson()
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("display", display);
            return jsonObject;
        }

        JsonObject toBorderJson()
        {
            return new JsonObject().add("display", drawBorder);
        }
        
    }
}
