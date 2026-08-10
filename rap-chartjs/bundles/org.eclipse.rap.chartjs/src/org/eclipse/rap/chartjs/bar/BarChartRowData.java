package org.eclipse.rap.chartjs.bar;

import static org.eclipse.rap.chartjs.ChartStyle.asCss;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.chartjs.line.LineChartRowData.RowInfo;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;

public class BarChartRowData
{

    private final String[]      labels;
    private final List<RowInfo> rowlabels = new ArrayList<RowInfo>(1);
    private final List<float[]> rows      = new ArrayList<float[]>(1);

    private final List<String[]> rowsToolTips      = new ArrayList<String[]>(1);
    private final List<ChartStyle[]> rowStyles     = new ArrayList<ChartStyle[]>(1);

    public BarChartRowData(String[] labels)
    {
        this.labels = labels;
    }

    public BarChartRowData addRow(RowInfo rowInfo, float[] row)
    {
       return this.addRow(rowInfo, row,toStringArray(row), null);
    }
    public BarChartRowData addRow(RowInfo rowInfo, float[] row,ChartStyle[] styles)
    {
        this.addRow(rowInfo, row,toStringArray(row), styles);
        return this;
    }
    public BarChartRowData addRow(RowInfo rowInfo, float[] row, String[] tootips)
    {
        return this.addRow(rowInfo, row,tootips, null);
    }
    public BarChartRowData addRow(RowInfo rowInfo, float[] row, String[] tootips,ChartStyle[] styles)
    {
        rowlabels.add(rowInfo);
        rows.add(row);
        rowsToolTips.add(tootips);
        if(styles==null)
            styles = new ChartStyle[0];
        rowStyles.add(styles);
        return this;
    }
    
    private String[] toStringArray(float[] row)
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
            
            ChartStyle[] styles=  rowStyles.get(i);
            JsonValue bg;
            JsonValue bc  ;
            JsonValue pc ;
            if(styles.length>0)
            {
                bg =  new JsonArray();
                bc =  new JsonArray();
                pc =  new JsonArray();
                for (ChartStyle chartStyle : styles)
                {
                    ((JsonArray)bg).add(asCss(chartStyle.getFillColor(), chartStyle.getFillOpacity()));
                    ((JsonArray)bc).add(asCss(chartStyle.getStrokeColor()));
                    ((JsonArray)pc).add(asCss(chartStyle.getPointColor()));
                }
            }
            else
            {
                 bg = JsonValue.valueOf(asCss(rowInfo.chartStyle.getFillColor(), rowInfo.chartStyle.getFillOpacity()));
                 bc = JsonValue.valueOf(asCss(rowInfo.chartStyle.getStrokeColor()));
                 pc = JsonValue.valueOf(asCss(rowInfo.chartStyle.getPointColor()));
            }
            
            rowsJson.add(new JsonObject().add("label", (rowInfo.label)).
                  
                    add("hidden", (rowInfo.hidden))
                    .add("backgroundColor", bg).
                    add("borderColor", bc)
                    .add("pointBorderColor", pc)
                    .add("data", asJson(rows.get(i)))
                    .add("dataTooltips", asJson(rowsToolTips.get(i)))
                    );
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

    private JsonArray asJson(float... ints)
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

        public String action ="select";

        String     label;
       
        ChartStyle chartStyle;

        private boolean hidden;

        public String getLabel()
        {
            return label;
        }

        public boolean isHidden()
        {
            return hidden;
        }

        public void setHidden(boolean hidden)
        {
            this.hidden = hidden;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        
        public void setAction(String action)
        {
            this.action = action;
        }
        
        public String getAction()
        {
            return action;
        }
      

      

       
        public ChartStyle getChartStyle()
        {
            return chartStyle;
        }

        public void setChartStyle(ChartStyle chartStyle)
        {
            this.chartStyle = chartStyle;
        }

      

      

    }

}
