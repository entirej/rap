/*******************************************************************************
 * Copyright 2013 CRESOFT AG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/

package org.entirej.applicationframework.rwt.renderers.block.definition;

import java.awt.Color;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTInsertScreenRendererDefinition;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTQueryScreenRendererDefinition;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTUpdateScreenRendererDefinition;
import org.entirej.framework.core.enumerations.EJFontStyle;
import org.entirej.framework.core.enumerations.EJFontWeight;
import org.entirej.framework.core.properties.EJCoreVisualAttributeContainer;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.core.properties.interfaces.EJEntireJProperties;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevMainScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJRWTHtmlTableBlockRendererDefinition implements EJDevBlockRendererDefinition
{

    public static final String CELL_SPACING_PROPERTY  = "CELL_SPACING";
    public static final String CELL_PADDING_PROPERTY  = "CELL_PADDING";
    public static final String DISPLAY_WIDTH_PROPERTY = "DISPLAY_WIDTH";
    public static final String CELL_ACTION_COMMAND    = "ACTION_COMMAND";
    public static final String ALLOW_ROW_SORTING      = "ALLOW_ROW_SORTING";
    public static final String ACTIONS                = "ACTIONS";
    public static final String ACTION_ID              = "ACTION_ID";
    public static final String ACTION_KEY             = "ACTION_KEY";
    public static final String HEADER_VA              = "HEADER_VA";
    public static final String ROW_ODD_VA             = "ROW_ODD_VA";
    public static final String ROW_EVEN_VA            = "ROW_EVEN_VA";

    public static final String ROW_SELECTION          = "ROW_SELECTION";
    public static final String ROW_SELECTION_VA       = "ROW_SELECTION_VA";

    public static final String TEXT_SELECTION         = "TEXT_SELECTION";

    public EJRWTHtmlTableBlockRendererDefinition()
    {

    }

    @Override
    public boolean allowSpacerItems()
    {
        return false;
    }

    @Override
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("HTML Table Block");

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(ACTIONS, "Actions");
        EJDevPropertyDefinition actionkey = new EJDevPropertyDefinition(ACTION_KEY, EJPropertyDefinitionType.STRING);
        actionkey.setLabel("Action Key");
        actionkey.setDescription("The action shortcut to trigger action.");
        actionkey.setMandatory(true);
        EJDevPropertyDefinition actionID = new EJDevPropertyDefinition(ACTION_ID, EJPropertyDefinitionType.ACTION_COMMAND);
        actionID.setLabel("Action Command");
        actionID.setDescription("The action command to be used when this action is selected");
        actionID.setMandatory(true);
        list.addPropertyDefinition(actionkey);
        list.addPropertyDefinition(actionID);

        EJDevPropertyDefinition showTableHeader = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        showTableHeader.setLabel("Show Headings");
        showTableHeader.setDescription("If selected, the cloumn headings of the block will be displayed");
        showTableHeader.setDefaultValue("true");

        EJDevPropertyDefinition textSelection = new EJDevPropertyDefinition(TEXT_SELECTION, EJPropertyDefinitionType.BOOLEAN);
        textSelection.setLabel("Text Selection");
        textSelection.setDefaultValue("false");
        
        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the blocks data. This filter can then be used by users to filter the blocks displayed data");
        filter.setDefaultValue("false");
        EJDevPropertyDefinition filterOnRefresh = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.FILTER_KEEP_ON_REFRESH, EJPropertyDefinitionType.BOOLEAN);
        filterOnRefresh.setLabel("Keep Filter on Refresh");
        filterOnRefresh.setDescription("If selected, the renderer will keep filter when  blocks data reloaded");
        filterOnRefresh.setDefaultValue("false");

        EJDevPropertyDefinition cellSpacing = new EJDevPropertyDefinition(CELL_SPACING_PROPERTY, EJPropertyDefinitionType.INTEGER);
        cellSpacing.setLabel("Cell Spacing");
        cellSpacing.setDefaultValue("1");
        cellSpacing.setDescription("Specifies the space kept between each cell in the table (in pixels)");

        EJDevPropertyDefinition cellPadding = new EJDevPropertyDefinition(CELL_PADDING_PROPERTY, EJPropertyDefinitionType.INTEGER);
        cellPadding.setLabel("Cell Padding");
        cellPadding.setDefaultValue("5");
        cellPadding.setDescription("Specifies the space, in pixels, between the cell wall and the cell content");

        EJDevPropertyDefinition headerVA = new EJDevPropertyDefinition(HEADER_VA, EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        headerVA.setLabel("Headings VA");
        headerVA.setDescription("Specifies visual attribute for table header");
        EJDevPropertyDefinition rowOddVA = new EJDevPropertyDefinition(ROW_ODD_VA, EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        rowOddVA.setLabel("Row Odd VA");
        rowOddVA.setDescription("Specifies visual attribute for table odd row");
        EJDevPropertyDefinition rowEvenVA = new EJDevPropertyDefinition(ROW_EVEN_VA, EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        rowEvenVA.setLabel("Row Even VA");
        rowEvenVA.setDescription("Specifies visual attribute for table even row");

        EJDevPropertyDefinition rowSelection = new EJDevPropertyDefinition(ROW_SELECTION, EJPropertyDefinitionType.BOOLEAN);
        rowSelection.setLabel("Row Selection Indicator");
        rowSelection.setDefaultValue("false");
        EJDevPropertyDefinition rowSelectionVA = new EJDevPropertyDefinition(ROW_SELECTION_VA, EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        rowSelectionVA.setLabel("Row Selection Indicator VA");

        mainGroup.addPropertyDefinitionList(list);
        mainGroup.addPropertyDefinition(textSelection);
        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(filterOnRefresh);
        mainGroup.addPropertyDefinition(showTableHeader);
        mainGroup.addPropertyDefinition(headerVA);
        mainGroup.addPropertyDefinition(rowOddVA);
        mainGroup.addPropertyDefinition(rowEvenVA);
        mainGroup.addPropertyDefinition(cellSpacing);
        mainGroup.addPropertyDefinition(cellPadding);
        mainGroup.addPropertyDefinition(rowSelection);
        mainGroup.addPropertyDefinition(rowSelectionVA);

        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("HTML Table: Required Item Properties");

        EJDevPropertyDefinition displayWidth = new EJDevPropertyDefinition(DISPLAY_WIDTH_PROPERTY, EJPropertyDefinitionType.INTEGER);
        displayWidth.setLabel("Displayed Width");
        displayWidth.setDescription("The width (in characters) of this items column within the blocks table");

        mainGroup.addPropertyDefinition(displayWidth);

        EJDevPropertyDefinition allowColumnSorting = new EJDevPropertyDefinition(ALLOW_ROW_SORTING, EJPropertyDefinitionType.BOOLEAN);
        allowColumnSorting.setLabel("Allow Column Sorting");
        allowColumnSorting
                .setDescription("If selected, the user will be able to re-order the data within the block by clicking on the column header. Only block contents will be sorted, no new data will be retreived from the datasource");
        allowColumnSorting.setDefaultValue("true");

        mainGroup.addPropertyDefinition(allowColumnSorting);

        EJDevPropertyDefinition cellActionCommand = new EJDevPropertyDefinition(CELL_ACTION_COMMAND, EJPropertyDefinitionType.ACTION_COMMAND);
        cellActionCommand.setLabel("Cell Action Command");
        cellActionCommand
                .setDescription("If entered, the value in this column will be displayed as a URL and when the user clicks on the url, the action command will be passed to the forms action processor for execution.");

        mainGroup.addPropertyDefinition(cellActionCommand);

        EJDevPropertyDefinition htmlFormat = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.ENABLE_MARKUP,
                EJPropertyDefinitionType.BOOLEAN);
        htmlFormat.setLabel("HTML Formatting");
        htmlFormat.setDescription("If this property is set, the Table formats  HTML tags in column values");
        mainGroup.addPropertyDefinition(htmlFormat);
        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {

        return null;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.html.EJRWTHtmlTableBlockRenderer";
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {
        // no impl

    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {
        // no impl

    }

    @Override
    public EJDevBlockRendererDefinitionControl addBlockControlToCanvas(EJMainScreenProperties mainScreenProperties,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        Composite layoutBody;

        if (mainScreenProperties.getDisplayFrame())
        {
            if (mainScreenProperties.getFrameTitle() != null && mainScreenProperties.getFrameTitle().length() > 0)
            {
                layoutBody = new Group(parent, SWT.NONE);
                ((Group) layoutBody).setText(mainScreenProperties.getFrameTitle());
            }
            else
            {
                layoutBody = new Composite(parent, SWT.BORDER);
            }

        }
        else
        {
            layoutBody = new Composite(parent, SWT.NONE);
        }

        layoutBody.setLayout(new FillLayout());

        if (System.getProperty("os.name").toLowerCase().indexOf("win") > -1)
        {
            Label label = new Label(layoutBody, SWT.NONE);
            label.setText("HTML Table Block");
            return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, Collections.<EJDevItemRendererDefinitionControl> emptyList());
        }

        EJDevItemGroupDisplayProperties displayProperties = null;
        if (blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().size() > 0)
        {
            displayProperties = blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().iterator().next();

        }

        EJFrameworkExtensionProperties blockRendererProperties = mainScreenProperties.getBlockProperties().getBlockRendererProperties();
        boolean addHeader = true;
        StringBuilder header = new StringBuilder();
        if (blockRendererProperties != null)
        {
            addHeader = blockRendererProperties.getBooleanProperty(EJRWTMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY, true);
        }
        int cellPadding = blockDisplayProperties.getBlockRendererProperties().getIntProperty(CELL_PADDING_PROPERTY, 0);
        String paddingStyle = null;
        if (cellPadding > 0)
        {
            String str = String.valueOf(cellPadding);
            paddingStyle = String.format("padding: %spx %spx %spx %spx; ", str, str, str, str);
        }
        if (displayProperties != null)
        {
            for (final EJDevScreenItemDisplayProperties screenItem : displayProperties.getAllItemDisplayProperties())
            {
                if (screenItem.isSpacerItem())
                {
                    continue;
                }

                if (addHeader)
                {
                    String styleClass = "default_all";
                    EJFrameworkExtensionProperties rendererProperties = screenItem.getBlockItemDisplayProperties().getItemRendererProperties();
                    header.append("<th ");

                    String alignment = "left";

                    String alignmentProperty = rendererProperties.getStringProperty("ALIGNMENT");

                    if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
                    {
                        if (alignmentProperty.equals("JUSTIFY"))
                        {
                            alignment = "justify";
                        }
                        else if (alignmentProperty.equals("RIGHT"))
                        {
                            alignment = "right";
                        }
                        else if (alignmentProperty.equals("CENTER"))
                        {
                            alignment = "center";
                        }
                    }

                    String valueVA = blockDisplayProperties.getBlockRendererProperties().getStringProperty(HEADER_VA);
                    if (valueVA != null && valueVA.length() > 0)
                    {
                        styleClass = valueVA;
                        valueVA = rendererProperties.getStringProperty(HEADER_VA);
                        if (valueVA != null && valueVA.length() > 0)
                            styleClass = valueVA;
                    }
                    header.append(String.format(" class=\"%s\" ", styleClass));
                    if (alignment != null)
                    {
                        header.append(String.format(" align=\'%s\'", alignment));
                    }
                    if (paddingStyle != null)
                    {
                        header.append(String.format(" style=\'%s\'", paddingStyle));
                    }
                    header.append("> ");
                    header.append(screenItem.getLabel());
                    header.append("</th>");
                }
            }

            final Browser browser = new Browser(layoutBody, SWT.NONE);

            StringBuilder builder = new StringBuilder();
            {
                builder.append("<html>");
                builder.append(getStyleDef(blockDisplayProperties.getEntireJProperties()));
                builder.append("<div id=\"table\" style=\"float:left;width:100%;height:100%; overflow:auto\">");
                {
                    EJDevBlockDisplayProperties blockProperties = blockDisplayProperties;
                    int cellSpacing = blockProperties.getBlockRendererProperties().getIntProperty(CELL_SPACING_PROPERTY, 0);
                    builder.append("<table border=0 cellspacing=").append(cellSpacing).append(" width=\"100%\" >");
                    {

                        int charHeight = (Display.getDefault().getSystemFont()).getFontData()[0].getHeight();
                        ;
                        String trDef = String.format("<tr style=\"height: %spx\">", String.valueOf(charHeight));

                        if (addHeader)
                        {
                            builder.append(header.toString());
                        }

                        int lastRowSpan = 0;

                        String oddVA = "default_all";
                        String valueVA = blockProperties.getBlockRendererProperties().getStringProperty(ROW_ODD_VA);
                        if (valueVA != null && valueVA.length() > 0)
                        {
                            oddVA = valueVA;
                        }
                        String evenVA = "default_all";
                        valueVA = blockProperties.getBlockRendererProperties().getStringProperty(ROW_EVEN_VA);
                        if (valueVA != null && valueVA.length() > 0)
                        {
                            evenVA = valueVA;
                        }
                        int rowid = 0;

                        for (int k = 0; k < 4; k++)
                        {
                            rowid++;
                            if (lastRowSpan > 1)
                            {
                                for (int i = 1; i < lastRowSpan; i++)
                                {
                                    builder.append(trDef).append("</tr>");

                                }
                                lastRowSpan = 0;
                            }
                            builder.append(trDef);
                            for (final EJDevScreenItemDisplayProperties item : displayProperties.getAllItemDisplayProperties())
                            {
                                String styleClass = (rowid % 2) != 0 ? oddVA : evenVA;

                                String alignment = null;
                                float width = -1;

                                EJFrameworkExtensionProperties rendererProperties = ((EJDevMainScreenItemDisplayProperties) item)
                                        .getBlockRendererRequiredProperties();

                                builder.append(String.format("<td class=\"%s\" ", styleClass));
                                if (paddingStyle != null)
                                {
                                    builder.append(String.format(" style=\'%s\'", paddingStyle));
                                }

                                if (width == -1)
                                {
                                    width = rendererProperties.getIntProperty(DISPLAY_WIDTH_PROPERTY, 0);
                                }

                                if (width > 0)
                                {
                                    Font font = browser.getFont();
                                    if (font != null)
                                    {
                                        float avgCharWidth = getAvgCharWidth(font);
                                        if (avgCharWidth > 0)
                                        {
                                            if (width != 1)
                                            {
                                                // add +1 padding
                                                width = ((int) (((width + 1) * avgCharWidth)));
                                            }
                                        }
                                    }

                                    builder.append(String.format(" width=%s ", width));
                                }
                                if (alignment == null)
                                {
                                    String alignmentProperty = item.getBlockItemDisplayProperties().getItemRendererProperties().getStringProperty("ALIGNMENT");
                                    alignment = "left";

                                    if (alignmentProperty != null && alignmentProperty.trim().length() > 0)
                                    {
                                        if (alignmentProperty.equals("JUSTIFY"))
                                        {
                                            alignment = "justify";
                                        }
                                        else if (alignmentProperty.equals("RIGHT"))
                                        {
                                            alignment = "right";
                                        }
                                        else if (alignmentProperty.equals("CENTER"))
                                        {
                                            alignment = "center";
                                        }
                                    }

                                }
                                if (alignment != null)
                                {
                                    builder.append(String.format(" align=\'%s\'", alignment));
                                }

                                builder.append(">");

                                // builder.append(String.format("<p class=\"default %s\">",
                                // styleClass));
                                String text = item.getReferencedItemName();
                                builder.append(text);

                                builder.append("</td>");
                            }
                            builder.append("</tr>");
                        }
                    }
                    builder.append("</table>");
                }
                builder.append("</<div>");
                builder.append("</html>");
            }

            browser.setText(builder.toString());
        }

        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, Collections.<EJDevItemRendererDefinitionControl> emptyList());
    }

    public boolean useInsertScreen()
    {
        return true;
    }

    public boolean useQueryScreen()
    {
        return true;
    }

    public boolean useUpdateScreen()
    {
        return true;
    }

    public float getAvgCharWidth(Font font)
    {
        GC gc = new GC(Display.getDefault());
        try
        {

            gc.setFont(font);

            return gc.getFontMetrics().getAverageCharWidth();
        }
        finally
        {
            gc.dispose();
        }
    }

    @Override
    public EJDevInsertScreenRendererDefinition getInsertScreenRendererDefinition()
    {
        return new EJRWTInsertScreenRendererDefinition();
    }

    @Override
    public EJDevQueryScreenRendererDefinition getQueryScreenRendererDefinition()
    {
        return new EJRWTQueryScreenRendererDefinition();
    }

    @Override
    public EJDevUpdateScreenRendererDefinition getUpdateScreenRendererDefinition()
    {
        return new EJRWTUpdateScreenRendererDefinition();
    }

    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return false;
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("HTML-Record Block");

        EJDevPropertyDefinition headerVA = new EJDevPropertyDefinition(HEADER_VA, EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        headerVA.setLabel("Headings VA");
        headerVA.setDescription("Specifies visual attribute for table header");
        mainGroup.addPropertyDefinition(headerVA);
        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties screenDisplayProperties, Composite parent,
            FormToolkit formToolkit)
    {
        // No spacers are available for a html record block
        return null;
    }

    // HTML review support
    private static String getStyleDef(EJEntireJProperties properties)
    {

        StringBuilder builder = new StringBuilder();
        builder.append("<style type=\"text/css\">");
        {
            builder.append("*{");
            builder.append("font: 11px Verdana, \"Lucida Sans\", Arial, Helvetica, sans-serif;");
            builder.append("}");

            builder.append("u.default {");
            builder.append("padding: 1px 2px 1px 0px;");
            builder.append("}");

            builder.append("u.default_link {");
            builder.append("padding: 1px 2px 1px 0px;");
            builder.append("text-shadow: none;");
            builder.append("}");

            builder.append("u.default_link:hover {");
            builder.append("cursor: pointer; cursor: hand;");
            builder.append("}");

            builder.append("u.default_link_fg {");
            builder.append("padding: 1px 2px 1px 0px;");
            builder.append("color: #416693;text-shadow: none;");
            builder.append("}");

            builder.append("u.default_link_fg:hover {");
            builder.append("cursor: pointer; cursor: hand;");
            builder.append("}");

            builder.append(".default_all {");
            builder.append("padding: 0px 0px 0px 0px;");
            Font font = Display.getDefault().getSystemFont();

            builder.append("}");

            if (properties != null)
            {
                EJCoreVisualAttributeContainer visualAttributesContainer = properties.getVisualAttributesContainer();
                for (EJCoreVisualAttributeProperties va : visualAttributesContainer.getVisualAttributes())
                {
                    builder.append(" \n");
                    builder.append(".");
                    builder.append(va.getName());
                    builder.append("{");
                    builder.append("padding: 0px 0px 0px 0px;");

                    Font vaFont = getFont(va, font);
                    if (vaFont != null && vaFont.getFontData().length > 0)
                    {
                        FontData fontData = vaFont.getFontData()[0];
                        builder.append("font:");
                        if ((fontData.getStyle() & SWT.BOLD) != 0)
                        {
                            builder.append("bold ");
                        }
                        if ((fontData.getStyle() & SWT.ITALIC) != 0)
                        {
                            builder.append("italic ");
                        }

                        builder.append(fontData.getHeight());
                        builder.append("px ");
                        builder.append(fontData.getName());

                        builder.append(";");
                    }

                    Color backgroundColor = va.getBackgroundColor();
                    if (backgroundColor != null)
                    {
                        String hexString = toHex(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
                        builder.append("background-color: ");
                        builder.append(hexString);
                        builder.append(";");
                    }
                    Color foregroundColor = va.getForegroundColor();
                    if (foregroundColor != null)
                    {
                        String hexString = toHex(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
                        builder.append("color: ");
                        builder.append(hexString);
                        builder.append(";");
                    }
                    builder.append("}");
                }

            }
        }
        builder.append("</style>");

        return builder.toString();

    }

    public static Font getFont(EJCoreVisualAttributeProperties visualAttributeProperties, Font defaultFont)
    {
        if (visualAttributeProperties != null)
        {
            Font font = null;

            if (visualAttributeProperties.getFontName().equals(EJCoreVisualAttributeProperties.UNSPECIFIED)
                    && visualAttributeProperties.getFontStyle() == EJFontStyle.Unspecified
                    && visualAttributeProperties.getFontWeight() == EJFontWeight.Unspecified && !visualAttributeProperties.isFontSizeSet())
            {
                return defaultFont;
            }
            String name = null;
            int style = SWT.NORMAL;
            int size = 11;
            if (defaultFont == null)
            {
                defaultFont = Display.getDefault().getSystemFont();
            }
            if (defaultFont != null)
            {
                name = defaultFont.getFontData()[0].getName();
                style = defaultFont.getFontData()[0].getStyle();

                size = defaultFont.getFontData()[0].getHeight();
            }
            if (visualAttributeProperties.getFontName() != null && !visualAttributeProperties.getFontName().equals(EJCoreVisualAttributeProperties.UNSPECIFIED))
            {
                name = visualAttributeProperties.getFontName();
            }

            if (name == null)
            {
                return defaultFont;
            }

            EJFontStyle fontStyle = visualAttributeProperties.getFontStyle();
            switch (fontStyle)
            {
                case Italic:
                    style = style | SWT.ITALIC;
                    break;

                case Underline:
                    break;
            }

            EJFontWeight fontWeight = visualAttributeProperties.getFontWeight();
            switch (fontWeight)
            {
                case Bold:
                    style = style | SWT.BOLD;
                    break;
            }
            if (visualAttributeProperties.getFontSize() > 0)
            {
                if (visualAttributeProperties.isFontSizeAsPercentage())
                {
                    if (visualAttributeProperties.getFontSize() != 100)
                    {
                        double fontSizeP = visualAttributeProperties.getFontSize();
                        size = (int) (size * (fontSizeP / 100));
                    }
                }
                else
                {

                    size = visualAttributeProperties.getFontSize();
                }
            }
            font = new Font(Display.getDefault(), name, size, style);
            return font;
        }
        return defaultFont;
    }

    public static String toHex(int r, int g, int b)
    {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number)
    {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2)
        {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

}
