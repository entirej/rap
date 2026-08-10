package org.eclipse.rap.chartjs.bar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.chartjs.AbstarctChartOptions;
import org.eclipse.rap.chartjs.Axis;
import org.eclipse.rap.json.JsonArray;
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
            JsonObject object = new JsonObject();
            object.add("gridLines", gridLines.toJson());
            object.add("display", yaxis.isDisplay());
            object.add("stacked", yaxis.isStacked());
            axis.add("y",object.add("ticks", yaxis.getTicks().toJson()));
            yaxis.getTicks().toJson(object);
        }
        
        for (Axis yaxis : xAxes)
        {
            JsonObject object = new JsonObject();
            object.add("gridLines", gridLines.toJson());
            object.add("display", yaxis.isDisplay());
            object.add("stacked", yaxis.isStacked());
            axis.add("x",object.add("ticks", yaxis.getTicks().toJson()));
            yaxis.getTicks().toJson(object);
        }
        
        if(xAxes.isEmpty())
        {
            JsonObject axisObj = new JsonObject();
            if (barThickness != null)
                axisObj.add("barThickness", barThickness);
            if (maxBarThickness != null)
                axisObj.add("maxBarThickness", maxBarThickness);
            axisObj.add("categoryPercentage", categoryPercentage);
            axisObj.add("barPercentage", barPercentage);
            axisObj.add("stacked", stacked);
            axis.add("x",axisObj);
        }
        if(yAxes.isEmpty())
        {
            JsonObject axisObj = new JsonObject();
            if (barThickness != null)
                axisObj.add("barThickness", barThickness);
            if (maxBarThickness != null)
                axisObj.add("maxBarThickness", maxBarThickness);
            axisObj.add("categoryPercentage", categoryPercentage);
            axisObj.add("barPercentage", barPercentage);
            axisObj.add("stacked", stacked);
            axis.add("y",axisObj);
        }

        return jsonObject;
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
            jsonObject.add("drawBorder", drawBorder);
            
            return jsonObject;
        }
        
    }
}
