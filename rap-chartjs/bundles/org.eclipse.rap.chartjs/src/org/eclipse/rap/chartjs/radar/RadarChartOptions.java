package org.eclipse.rap.chartjs.radar;

import org.eclipse.rap.chartjs.AbstarctChartOptions;
import org.eclipse.rap.chartjs.Axis;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;

/**
 * @author anuradhagunasekara
 *
 */
public class RadarChartOptions extends AbstarctChartOptions
{
    private Float scaleMin;
    private Float scaleMax;
    private Float scaleStep;

    public RadarChartOptions()
    {

    }

    @Override
    public JsonObject toJson()
    {
        JsonObject jsonObject = super.toJson();
        JsonObject axis = new JsonObject();
        JsonObject scale = new JsonObject();
        
      
        
        jsonObject.add("scales", axis);
       
        axis.add("r", scale);
        JsonObject ticks = new JsonObject();
        scale.add("ticks", ticks);

        if (scaleMin != null)
        {
            scale.add("min", scaleMin.intValue());
        }
        if (scaleMax != null)
        {
            scale.add("max", scaleMax.intValue());
        }
      
        if (scaleStep != null)
        {
            ticks.add("stepSize", scaleStep.intValue());
        }
        return jsonObject;
    }

    public Float getScaleMin()
    {
        return scaleMin;
    }

    public void setScaleMin(Float scaleMin)
    {
        this.scaleMin = scaleMin;
    }

    public Float getScaleMax()
    {
        return scaleMax;
    }

    public void setScaleMax(Float scaleMax)
    {
        this.scaleMax = scaleMax;
    }

    public Float getScaleStep()
    {
        return scaleStep;
    }

    public void setScaleStep(Float scaleStep)
    {
        this.scaleStep = scaleStep;
    }

}
