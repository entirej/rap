package org.eclipse.rap.chartjs.line;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.chartjs.line.LineChartRowData.RowInfo;
import org.eclipse.rap.json.JsonObject;
import org.junit.Test;

public class LineChartJsonTest
{
    @Test
    public void datasetUsesChartJsFourLineOptions()
    {
        RowInfo row = new RowInfo();
        row.setLabel("Trend");
        row.setChartStyle(new ChartStyle());
        JsonObject dataset = new LineChartRowData(new String[] { "Q1" })
                .addRow(row, new float[] { 10 })
                .toJson()
                .get("datasets").asArray().get(0).asObject();

        assertNull(dataset.get("lineTension"));
        assertNull(dataset.get("steppedLine"));
        assertFalse(dataset.get("stepped").asBoolean());
    }
}
