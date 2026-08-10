package org.eclipse.rap.chartjs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.chartjs.line.LineChartOptions;
import org.eclipse.rap.json.JsonObject;
import org.junit.Test;

public class ChartOptionsJsonTest
{
    @Test
    public void tooltipUsesChartJsFourPluginConfiguration()
    {
        JsonObject options = new AbstarctChartOptions().setShowToolTips(false).toJson();

        assertNull(options.get("tooltips"));
        JsonObject tooltip = options.get("plugins").asObject().get("tooltip").asObject();
        assertFalse(tooltip.get("enabled").asBoolean());
        assertTrue(tooltip.get("callbacks").isObject());
    }

    @Test
    public void lineScaleUsesGridAndBorderConfiguration()
    {
        LineChartOptions options = new LineChartOptions();
        options.getGridLines().setDisplay(false);
        options.getGridLines().setDrawBorder(true);
        options.getYAxes().get(0).getTicks().setMin(5d);
        options.getYAxes().get(0).getTicks().setStepSize(2d);

        JsonObject yScale = options.toJson().get("scales").asObject().get("y").asObject();

        assertNull(yScale.get("gridLines"));
        assertFalse(yScale.get("grid").asObject().get("display").asBoolean());
        assertTrue(yScale.get("border").asObject().get("display").asBoolean());
        assertEquals(5d, yScale.get("min").asDouble(), 0.001d);
        assertNull(yScale.get("ticks").asObject().get("min"));
        assertEquals(2d, yScale.get("ticks").asObject().get("stepSize").asDouble(), 0.001d);
    }
}
