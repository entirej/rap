package org.entirej.applicationframework.rwt.renderers.stack;

import java.util.ArrayList;
import java.util.List;

public class EJRWTStackedItemRendererConfig
{

    public static final int                    DEFUALT = 0;
    private final EJRWTStackedItemRendererType type;

    private EJRWTStackedItemRendererConfig(EJRWTStackedItemRendererType type)
    {
        this.type = type;
    }

    private String  label;
    private String  tooltip;

    private int     xSpan              = DEFUALT;
    private int     ySpan              = DEFUALT;
    private int     width              = DEFUALT;
    private int     height             = DEFUALT;

    private Boolean expandHorizontally = null;
    private Boolean expandVertically   = null;

    public EJRWTStackedItemRendererType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getTooltip()
    {
        return tooltip;
    }

    public void setTooltip(String tooltip)
    {
        this.tooltip = tooltip;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getXSpan()
    {
        return xSpan;
    }

    public void setXSpan(int xSpan)
    {
        this.xSpan = xSpan;
    }

    public int getYSpan()
    {
        return ySpan;
    }

    public void setYSpan(int ySpan)
    {
        this.ySpan = ySpan;
    }

    public Boolean getExpandHorizontally()
    {
        return expandHorizontally;
    }

    public void setExpandHorizontally(Boolean expandHorizontally)
    {
        this.expandHorizontally = expandHorizontally;
    }

    public Boolean getExpandVertically()
    {
        return expandVertically;
    }

    public void setExpandVertically(Boolean expandVertically)
    {
        this.expandVertically = expandVertically;
    }

    public static class LOVSupportConfig extends EJRWTStackedItemRendererConfig
    {

        private LOVSupportConfig(EJRWTStackedItemRendererType type)
        {
            super(type);

        }

        private String  lovMapping;

        private boolean validateLov = true;
        private boolean lovEnabled  = false;

        public boolean isLovEnabled()
        {

            return lovEnabled;

        }

        public void setLovEnabled(boolean lovEnabled)
        {
            this.lovEnabled = lovEnabled;
        }

        public String getLovMapping()
        {
            return lovMapping;
        }

        public void setLovMapping(String lovMapping)
        {
            this.lovMapping = lovMapping;
            if(lovMapping!=null)
            {
                lovEnabled = true;
            }
        }

        public boolean isValidateLov()
        {
            return validateLov;
        }

        public void setValidateLov(boolean validateLov)
        {
            this.validateLov = validateLov;
        }
    }

    public static class Text extends LOVSupportConfig
    {
        public Text()
        {
            super(EJRWTStackedItemRendererType.TEXT);
        }
    }

    public static class TextArea extends LOVSupportConfig
    {
        public TextArea()
        {
            super(EJRWTStackedItemRendererType.TEXT_AREA);
        }

        private int lines = 3;

        public int getLines()
        {
            return lines;
        }

        public void setLines(int lines)
        {
            this.lines = lines;
        }

    }

    public static class Label extends EJRWTStackedItemRendererConfig
    {
        public Label()
        {
            super(EJRWTStackedItemRendererType.LABEL);
        }
    }

    public static class Spacer extends EJRWTStackedItemRendererConfig
    {
        public Spacer()
        {
            super(EJRWTStackedItemRendererType.SPACER);
        }
    }

    public static class Date extends LOVSupportConfig
    {
        public enum ReturnType
        {
            UTIL_DATE, SQL_DATE, SQL_TIME, SQL_TIMESTAMP

        }

        public Date()
        {
            super(EJRWTStackedItemRendererType.DATE);
        }

        private String     format;
        private ReturnType returnType = ReturnType.UTIL_DATE;

        public String getFormat()
        {
            return format;
        }

        public void setReturnType(ReturnType returnType)
        {
            this.returnType = returnType;
        }

        public ReturnType getReturnType()
        {
            return returnType;
        }

        public void setFormat(String format)
        {
            this.format = format;
        }
    }

    public static class Number extends LOVSupportConfig
    {
        public enum DataType
        {
            NUMBER, INTEGER, FLOAT, BIG_DECIMAL, DOUBLE, LONG
        };

        DataType numberType;

        public Number(DataType numberType)
        {
            super(EJRWTStackedItemRendererType.NUMBER);
            this.numberType = numberType;
        }

        private String format;

        public String getFormat()
        {
            return format;
        }

        public void setFormat(String format)
        {
            this.format = format;
        }

        public DataType getDataType()
        {
            return numberType;
        }

    }

    public static class Combo extends LOVSupportConfig implements ActionSupportConfig
    {

        private List<Column> entries = new ArrayList<Column>();

        public Combo()
        {
            super(EJRWTStackedItemRendererType.COMBO);
        }

        private int    visibleItemCount = -1;

        private String lovDefinition;
        private String itemName;

        private String actionCommand;

        public String getActionCommand()
        {
            return actionCommand;
        }

        public void setActionCommand(String actionCommand)
        {
            this.actionCommand = actionCommand;
        }

        public String getLovDefinition()
        {
            return lovDefinition;
        }

        public String getItemName()
        {
            return itemName;
        }

        public void setItemName(String itemName)
        {
            this.itemName = itemName;
        }

        public void setLovDefinition(String lovDefinition)
        {
            this.lovDefinition = lovDefinition;
        }

        public int getVisibleItemCount()
        {
            return visibleItemCount;
        }

        public void setVisibleItemCount(int visibleItemCount)
        {
            this.visibleItemCount = visibleItemCount;
        }

        public List<Column> getColumns()
        {
            return entries;
        }

        public void addColumn(String item, boolean displayed)
        {
            addColumn(item, displayed, null, null);
        }

        public void addColumn(String item, boolean displayed, String returnItem)
        {
            addColumn(item, displayed, returnItem, null);
        }

        public void addColumn(String item, boolean displayed, String returnItem, String datatypeFormat)
        {
            Column column = new Column();

            column.datatypeFormat = datatypeFormat;
            column.displayed = displayed;
            column.item = item;
            column.returnItem = returnItem;
            entries.add(column);
        }

        public static class Column
        {
            private String  item;
            private boolean displayed;
            private String  datatypeFormat;
            private String  returnItem;

            private Column()
            {

            }

            public String getDatatypeFormat()
            {
                return datatypeFormat;
            }

            public String getItem()
            {
                return item;
            }

            public String getReturnItem()
            {
                return returnItem;
            }

            public boolean isDisplayed()
            {
                return displayed;
            }

        }

    }

    public static class CheckBox extends EJRWTStackedItemRendererConfig implements ActionSupportConfig
    {
        public CheckBox()
        {
            super(EJRWTStackedItemRendererType.CHECKBOX);
        }

        private Object checkBoxCheckedValue;
        private Object checkBoxUnCheckedValue;

        public Object getCheckBoxCheckedValue()
        {
            return checkBoxCheckedValue;
        }

        public void setCheckBoxCheckedValue(Object checkBoxCheckedValue)
        {
            this.checkBoxCheckedValue = checkBoxCheckedValue;
        }

        public Object getCheckBoxUnCheckedValue()
        {
            return checkBoxUnCheckedValue;
        }

        public void setCheckBoxUnCheckedValue(Object checkBoxUnCheckedValue)
        {
            this.checkBoxUnCheckedValue = checkBoxUnCheckedValue;
        }

        private String actionCommand;

        public String getActionCommand()
        {
            return actionCommand;
        }

        public void setActionCommand(String actionCommand)
        {
            this.actionCommand = actionCommand;
        }
    }

    public static class Button extends EJRWTStackedItemRendererConfig implements ActionSupportConfig
    {
        public Button()
        {
            super(EJRWTStackedItemRendererType.BUTTON);
        }

        private String image;

        public String getImage()
        {
            return image;
        }

        public void setImage(String image)
        {
            this.image = image;
        }

        private String actionCommand;

        public String getActionCommand()
        {
            return actionCommand;
        }

        public void setActionCommand(String actionCommand)
        {
            this.actionCommand = actionCommand;
        }

    }

    public static interface ActionSupportConfig
    {

        public String getActionCommand();

        public void setActionCommand(String actionCommand);

    }

}
