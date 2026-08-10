package org.eclipse.rap.chartjs.bar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.chartjs.bar.BarChartRowData.RowInfo;
import org.eclipse.rap.json.JsonObject;
import org.junit.Test;

public class BarChartJsonTest
{
    @Test
    public void barSizingOptionsAreWrittenToDatasets()
    {
        BarChartOptions options = new BarChartOptions();
        options.setBarPercentage(0.7f);
        options.setCategoryPercentage(0.6f);
        options.setBarThickness(12);
        options.setMaxBarThickness(24);

        RowInfo row = new RowInfo();
        row.setLabel("Sales");
        row.setChartStyle(new ChartStyle());
        JsonObject data = new BarChartRowData(new String[] { "Q1" })
                .addRow(row, new float[] { 10 })
                .toJson(options);
        JsonObject dataset = data.get("datasets").asArray().get(0).asObject();

        assertEquals(0.7f, dataset.get("barPercentage").asFloat(), 0.001f);
        assertEquals(0.6f, dataset.get("categoryPercentage").asFloat(), 0.001f);
        assertEquals(12, dataset.get("barThickness").asInt());
        assertEquals(24, dataset.get("maxBarThickness").asInt());

        JsonObject xScale = options.toJson().get("scales").asObject().get("x").asObject();
        assertNull(xScale.get("barPercentage"));
        assertNull(xScale.get("categoryPercentage"));
        assertTrue(xScale.get("grid").isObject());
        assertTrue(xScale.get("border").isObject());
    }
}
