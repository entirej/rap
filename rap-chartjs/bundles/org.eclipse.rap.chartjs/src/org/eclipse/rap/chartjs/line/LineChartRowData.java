package org.eclipse.rap.chartjs.line;

import static org.eclipse.rap.chartjs.ChartStyle.asCss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.rap.chartjs.ChartStyle;
import org.eclipse.rap.chartjs.bar.BarChartRowData;
import org.eclipse.rap.chartjs.bar.BarChartRowData.RowInfo;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;

public class LineChartRowData
{

    private final String[]      labels;
    private final List<RowInfo> rowlabels = new ArrayList<RowInfo>(1);
    private final List<float[]> rows      = new ArrayList<float[]>(1);
    private final List<String[]> rowsToolTips      = new ArrayList<String[]>(1);
    private final List<ChartStyle[]> rowStyles     = new ArrayList<ChartStyle[]>(1);


    public LineChartRowData(String[] labels)
    {
        this.labels = labels;
    }

    public LineChartRowData addRow(RowInfo rowInfo, float[] row, String[] tootips)
    {
       
        return this.addRow(rowInfo, row, tootips,null);
    }
    
    public LineChartRowData addRow(RowInfo rowInfo, float[] row, String[] tootips, ChartStyle[] styles)
    {
        rowlabels.add(rowInfo);
        rows.add(row);
        rowsToolTips.add(tootips);
        if(styles==null)
            styles = new ChartStyle[0];
        rowStyles.add(styles);
        return this;
    }
    
    public LineChartRowData addRow(RowInfo rowInfo, float[] row)
    {
        
        return this.addRow(rowInfo, row, toStringArray(row),null);
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

    public LineChartRowData addRow(RowInfo rowInfo, float[] row,ChartStyle[] styles)
    {
        return this.addRow(rowInfo, row, toStringArray(row),styles);
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
            
            ChartStyle[] styles=  rowStyles.get(i);
            JsonValue bg;
            JsonValue bc  ;
            JsonValue pc ;
            if( styles.length>0)
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
            
            rowsAction.add(rowInfo.action);
            rowsJson.add(new JsonObject().add("label", (rowInfo.label)).
                    add("pointStyle", (rowInfo.pointStyle)).
                    add("hidden", (rowInfo.hidden))
                    .add("showLine", (rowInfo.showLine)).add("fill", rowInfo.fill).add("borderWidth", rowInfo.lineWidth).add("lineTension", rowInfo.lineTension).add("steppedLine", rowInfo.steppedLine)
                    .add("backgroundColor", bg).add("borderColor", bg).add("pointBackgroundColor", pc)
                    .add("pointBorderColor", pc).add("pointBorderWidth", rowInfo.lineWidth).add("data", asJson(rows.get(i))).add("dataTooltips", asJson(rowsToolTips.get(i))));
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

        String     label;
        String     pointStyle  = "circle";
        boolean    fill;
        boolean    hidden = true;
        boolean    showLine    = true;
        ChartStyle chartStyle;
        int        lineWidth   = 1;
        double     lineTension = 0.4;
        String     steppedLine = "false";
        String     action = "select";

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

        public String getSteppedLine()
        {
            return steppedLine;
        }
        
        public void setAction(String action)
        {
            this.action = action;
        }
        
        public String getAction()
        {
            return action;
        }

        public void setSteppedLine(String steppedLine)
        {
            if ("before".equals(steppedLine) || "after".equals(steppedLine))
            {
                this.steppedLine = steppedLine;
            }
            else
                this.steppedLine = null;

        }

        public boolean isFill()
        {
            return fill;
        }

        public void setFill(boolean fill)
        {
            this.fill = fill;
        }

        public ChartStyle getChartStyle()
        {
            return chartStyle;
        }

        public void setChartStyle(ChartStyle chartStyle)
        {
            this.chartStyle = chartStyle;
        }

        public void setLineWidth(int lineWidth)
        {
            this.lineWidth = lineWidth;
        }

        public int getLineWidth()
        {
            return lineWidth;
        }

        public void setPointStyle(String pointStyle)
        {
            this.pointStyle = pointStyle;
        }

        public String getPointStyle()
        {
            return pointStyle;
        }

        public void setShowLine(boolean showLine)
        {
            this.showLine = showLine;
        }

        public boolean isShowLine()
        {
            return showLine;
        }

        public double getLineTension()
        {
            return lineTension;
        }

        public void setLineTension(double lineTension)
        {
            this.lineTension = lineTension;
        }

    }

}
