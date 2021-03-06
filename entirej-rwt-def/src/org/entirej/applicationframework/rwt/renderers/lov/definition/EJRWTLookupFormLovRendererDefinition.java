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
package org.entirej.applicationframework.rwt.renderers.lov.definition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTQueryScreenRendererDefinition;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevLovDefinitionDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevLovRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;


public class EJRWTLookupFormLovRendererDefinition implements EJDevLovRendererDefinition
{
    @Override
    public boolean allowSpacerItems()
    {
        return false;
    }

    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.lov.EJRWTLookupFormLovRenderer";
    }

    public EJPropertyDefinitionGroup getLovPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Lov Renderer Properties");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.HIDE_TABLE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Border");
        showTableBorder.setDescription("If selected, the renderer will hide the lov's standard border");
        showTableBorder.setDefaultValue("false");

        EJDevPropertyDefinition showTableHeader = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        showTableHeader.setLabel("Show Headings");
        showTableHeader.setDescription("If selected, column headings will be displayed");
        showTableHeader.setDefaultValue("true");

       

        EJDevPropertyDefinition showVerticalLines = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.SHOW_VERTICAL_LINES,
                EJPropertyDefinitionType.BOOLEAN);
        showVerticalLines.setLabel("Show Vertical Lines");
        showVerticalLines.setDescription("Indicates if vertical lines should be displayed within the lov");
        showVerticalLines.setDefaultValue("true");

        
        EJDevPropertyDefinition quearyScreen = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.SHOW_QUERY_SCREEN,
                EJPropertyDefinitionType.BOOLEAN);
        quearyScreen.setLabel("Auto show query screen");
        quearyScreen.setDescription("Indicates that the LOV needs to show the query screen automatically when the LOV is displayed.");
        quearyScreen.setDefaultValue("true");

        
        
        EJDevPropertyDefinition message = new EJDevPropertyDefinition("MESSAGE", EJPropertyDefinitionType.STRING);
        message.setLabel("Filter Message");
        message.setDescription("The message text is displayed as a hint for the user, indicating the purpose of the filter..");
        
        EJDevPropertyDefinition htmlFormat = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.ENABLE_MARKUP, EJPropertyDefinitionType.BOOLEAN);
        htmlFormat.setLabel("XHTML Formatting");
        htmlFormat.setDescription("If this property is set, the Table formats certain XHTML tags ");
        

        mainGroup.addPropertyDefinition(message);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(showTableHeader);
        mainGroup.addPropertyDefinition(showVerticalLines);
        mainGroup.addPropertyDefinition(htmlFormat);
        mainGroup.addPropertyDefinition(quearyScreen);

        return mainGroup;
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl

    }

    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Lov Renderer: Required Item Properties");
       

        EJDevPropertyDefinition displayWidth = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayWidth.setLabel("Display Width");
        displayWidth.setDescription("The width (in characters) of this item within the lov");

        EJDevPropertyDefinition headerAlignment = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALIGNMENT,
                EJPropertyDefinitionType.STRING);
        headerAlignment.setLabel("Column Alignment");
        headerAlignment.setDescription("Indicates the alignment of data within the column");
        headerAlignment.setDefaultValue(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT);

        headerAlignment.addValidValue(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_LEFT, "Left");
        headerAlignment.addValidValue(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT, "Right");
        headerAlignment.addValidValue(EJRWTMultiRecordBlockDefinitionProperties.COLUMN_ALLIGN_CENTER, "Center");

        EJDevPropertyDefinition allowColumnSorting = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.ALLOW_ROW_SORTING,
                EJPropertyDefinitionType.BOOLEAN);
        allowColumnSorting.setLabel("Allow Column Sorting");
        allowColumnSorting.setDescription("If selected, the user will be able to re-order the data within the lov by clicking on the column header. Only lov contents will be sorted, no new data will be retreived from the datasource");
        allowColumnSorting.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmResize = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_RESIZE,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmResize.setLabel("Allow Resize of Column");
        allowColunmResize.setDescription("If selected, the user will be able to resize the width of the columns wtihin this lov");
        allowColunmResize.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmReorder = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.ALLOW_COLUMN_REORDER,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmReorder.setLabel("Allow Re-Order of Column");
        allowColunmReorder.setDescription("If selected, the user will be able to move columns of this lov to change their displayed position. The re-positioning will not be saved and the next time the lov is displayed, columns will be displayed in their original positions");
        allowColunmReorder.setDefaultValue("true");

        mainGroup.addPropertyDefinition(displayWidth);
        mainGroup.addPropertyDefinition(headerAlignment);
        mainGroup.addPropertyDefinition(allowColumnSorting);
        mainGroup.addPropertyDefinition(allowColunmResize);
        mainGroup.addPropertyDefinition(allowColunmReorder);

        return mainGroup;
    }

    @Override
    public EJDevPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        return null;
    }

    public boolean executeAutomaticQuery()
    {
        return false;
    }

    public boolean allowsUserQuery()
    {
        return true;
    }

    public EJDevQueryScreenRendererDefinition getQueryScreenRendererDefinition()
    {
        return new EJRWTQueryScreenRendererDefinition();
    }

    public boolean requiresAllRowsRetrieved()
    {
        return false;
    }

    public EJRWTTableRendererDefinitionControl addLovControlToCanvas(EJDevLovDefinitionDisplayProperties displayProperties, Composite parent,
            FormToolkit toolkit)
    {
        EJRWTTableRendererDefinitionControl control = addTable(displayProperties, parent, toolkit);

        toolkit.paintBordersFor(parent);

        return control;

        //
        // Composite client = toolkit.createComposite(parent);
        // GridData gd = new GridData(GridData.FILL_HORIZONTAL |
        // GridData.VERTICAL_ALIGN_FILL);
        // gd.grabExcessHorizontalSpace = true;
        // gd.grabExcessVerticalSpace = true;
        // client.setLayoutData(gd);
        //
        // GridLayout glayout = new GridLayout();
        // glayout.marginWidth = 0;
        // glayout.marginHeight = 0;
        // glayout.numColumns = 1;
        // glayout.makeColumnsEqualWidth = false;
        // glayout.horizontalSpacing = 0;
        // client.setLayout(glayout);
        //
        // TableRendererDefinitionControl control = addTable(displayProperties,
        // client, toolkit);
        //
        // toolkit.paintBordersFor(client);
        //
        // return control;
    }

    private EJRWTTableRendererDefinitionControl addTable(EJDevLovDefinitionDisplayProperties lovDisplayProperties, Composite client, FormToolkit toolkit)
    {
        Map<String, Integer> columnPositions = new HashMap<String, Integer>();

        final ScrolledForm sc = toolkit.createScrolledForm(client);

        GridData scgd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        scgd.grabExcessHorizontalSpace = true;
        scgd.grabExcessVerticalSpace = true;
        sc.setLayoutData(scgd);
        GridLayout gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        sc.getBody().setLayout(gl);
        toolkit.adapt(sc);

        GridLayout glayout = new GridLayout();
        glayout.marginWidth = glayout.marginHeight = 0;
        glayout.numColumns = 1;

        sc.getBody().setLayout(glayout);
        Composite tablePanel = sc.getBody();

        Table table = new Table(tablePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        // gd.widthHint = 300;
        // gd.heightHint = 100;
        gd.verticalSpan = 6;
        gd.horizontalSpan = 2;
        table.setLayoutData(gd);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableLayout tableLayout = new TableLayout();

        // There is only one item group for a flow layout
        Iterator<EJDevItemGroupDisplayProperties> itemGroups = lovDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties()
                .iterator();
        if (itemGroups.hasNext())
        {
            EJDevItemGroupDisplayProperties displayProperties = itemGroups.next();

            int itemCount = 0;
            for (EJDevScreenItemDisplayProperties screenItem : displayProperties.getAllItemDisplayProperties())
            {
                int width = 0;
                TableColumn masterColumn = new TableColumn(table, SWT.NONE);
                masterColumn.setData("SCREEN_ITEM", screenItem);
                masterColumn.setText(screenItem.getLabel());
                masterColumn.setWidth(width);
                ColumnWeightData colData = new ColumnWeightData(5, 50, true);
                tableLayout.addColumnData(colData);
                columnPositions.put(screenItem.getReferencedItemName(), itemCount);
                itemCount++;
            }
        }

        table.setLayout(tableLayout);
        setColumnResizeHook(table, tablePanel);

        return new EJRWTTableRendererDefinitionControl(lovDisplayProperties, table, columnPositions);
    }

    private void setColumnResizeHook(final Table table, final Composite client)
    {
        table.addControlListener(new ControlAdapter()
        {
            public void controlResized(ControlEvent e)
            {
                Rectangle area = client.getClientArea();
                // table.setLayoutDeferred(true);
                Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                int width = area.width - 20 * table.getBorderWidth();
                if (preferredSize.y > area.height + table.getHeaderHeight())
                {
                    // Subtract the scrollbar width from the total column width
                    // if a vertical scrollbar will be required
                    Point vBarSize = table.getVerticalBar().getSize();
                    width -= vBarSize.x;
                }
                Point oldSize = table.getSize();
                boolean shrink = oldSize.x > area.width;
                if (!shrink)
                {
                    // table is getting bigger so make the table
                    // bigger first and then make the columns wider
                    // to match the client area width
                    table.setSize(area.width, area.height);
                }
                int sumColumnWidth = 0;
                TableColumn[] tableColumns = table.getColumns();
                for (TableColumn tableColumn : tableColumns)
                {
                    int columnWidth = tableColumn.getWidth();
                    sumColumnWidth += columnWidth;
                }

                double scale = (double) width / (double) sumColumnWidth;

                sumColumnWidth = 0;
                int newColumnWidth = 0;

                for (int i = 0; i < table.getColumnCount() - 1; i++)
                {
                    if (table.getColumn(i) != null)
                    {
                        newColumnWidth = (int) Math.round(scale * (double) table.getColumn(i).getWidth());
                        table.getColumn(i).setWidth(newColumnWidth);
                        sumColumnWidth += newColumnWidth;
                    }
                }

                if (table.getColumnCount() > 0)
                {
                    table.getColumn(table.getColumnCount() - 1).setWidth(width - sumColumnWidth);
                }

                if (shrink)
                {
                    table.setSize(area.width, area.height);
                }
                // table.setLayoutDeferred(false);
            }
        });
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
