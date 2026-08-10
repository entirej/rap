package org.eclipse.rap.chartjs.radar;

import static org.eclipse.rap.chartjs.ChartStyle.asCss;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;

public class RadarChartRowData
{

    private final String[]       labels;
    private final List<RowInfo>  rowlabels    = new ArrayList<RowInfo>(5);
    private final List<double[]> rows         = new ArrayList<double[]>(5);

    private final List<String[]> rowsToolTips = new ArrayList<String[]>(5);

    public RadarChartRowData(String[] labels)
    {
        this.labels = labels;
    }

    public RadarChartRowData addRow(RowInfo rowInfo, double[] row)
    {
        this.addRow(rowInfo, row, toStringArray(row));
        return this;
    }

    public RadarChartRowData addRow(RowInfo rowInfo, double[] row, String[] tootips)
    {
        rowlabels.add(rowInfo);
        rows.add(row);
        rowsToolTips.add(tootips);
        return this;
    }

    private String[] toStringArray(double[] row)
    {
        String[] str = new String[row.length];
        for (int i = 0; i < str.length; i++)
        {
            str[i] = String.valueOf(row[i]);

        }
        return str;
    }

    JsonObject toJson()
    {
        JsonObject result = new JsonObject();
        result.add("labels", asJson(labels));
        JsonArray rowsJson = new JsonArray();
        JsonArray rowsAction = new JsonArray();
        for (int i = 0; i < rows.size(); i++)
        {

            RowInfo rowInfo = rowlabels.get(i);
            rowsAction.add(rowInfo.action);
            JsonObject jsonObject = new JsonObject();
            if (rowInfo.borderWidth > 0)
                jsonObject.add("borderWidth", rowInfo.borderWidth);

            if (rowInfo.chartStyle != null)
            {
                jsonObject.add("backgroundColor", asCss(rowInfo.chartStyle.getFillColor(), rowInfo.chartStyle.getFillOpacity()));
                jsonObject.add("borderColor", asCss(rowInfo.chartStyle.getStrokeColor()));
                jsonObject.add("fill", rowInfo.chartStyle.isFill());
            }

            jsonObject.add("dataTooltips", asJson(rowsToolTips.get(i)));
            jsonObject.add("label", rowInfo.label);
            jsonObject.add("data", asJson(rowInfo, rows.get(i)));
            rowsJson.add(jsonObject);
        }
        result.add("datasets", rowsJson);
        result.add("actions", rowsAction);
        return result;
    }

   

    private JsonArray asJson(String... strings)
    {
        JsonArray result = new JsonArray();
        for (int i = 0; i < strings.length; i++)
        {
            result.add(strings[i]);
        }
        return result;
    }

    private JsonArray asJson(RowInfo info, double... ints)
    {
        JsonArray result = new JsonArray();
        for (int i = 0; i < ints.length; i++)
        {

            result.add(ints[i]);
        }
        return result;
    }

    public static class RowInfo
    {

        String     action      = "selection";
        String     label;
        boolean    hidden;
        ChartStyle chartStyle;
        int        borderWidth = 1;

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public boolean getHidden()
        {
            return hidden;
        }

        public void setHidden(boolean hidden)
        {
            this.hidden = hidden;
        }

        public ChartStyle getChartStyle()
        {
            return chartStyle;
        }

        public String getAction()
        {
            return action;
        }

        public void setAction(String action)
        {
            this.action = action;
        }

        public void setChartStyle(ChartStyle chartStyle)
        {
            this.chartStyle = chartStyle;
        }

        public int getBorderWidth()
        {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth)
        {
            this.borderWidth = borderWidth;
        }

    }

}
