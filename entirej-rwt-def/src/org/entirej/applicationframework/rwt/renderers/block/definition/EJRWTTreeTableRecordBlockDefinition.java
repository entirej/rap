/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
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
 *     Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.renderers.block.definition;

import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTTreeTableBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTInsertScreenRendererDefinition;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTQueryScreenRendererDefinition;
import org.entirej.applicationframework.rwt.renderers.screen.definition.EJRWTUpdateScreenRendererDefinition;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.core.properties.interfaces.EJMainScreenProperties;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.properties.interfaces.EJDevBlockDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJRWTTreeTableRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJRWTTreeTableRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.blocks.EJRWTTreeTableRecordBlockRenderer";
    }

    @Override
    public boolean allowSpacerItems()
    {
        return false;
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        // no impl
    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        // no impl
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

    /*
     * (non-Javadoc)
     * 
     * @seeorg.entirej.framework.renderers.IBlockRenderer#
     * getBlockPropertyDefinitionGroup()
     */
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block");

        EJDevPropertyDefinition doubleClickActionCommand = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        doubleClickActionCommand.setLabel("Double Click Action Command");
        doubleClickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user double clicks on this block");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.HIDE_TREE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Tree Border");
        showTableBorder.setDescription("If selected, the renderer will hide the tree standard border");
        showTableBorder.setDefaultValue("false");

        EJDevPropertyDefinition showTableHeader = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.SHOW_HEADING_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        showTableHeader.setLabel("Show Tree Heading");
        showTableHeader.setDescription("If selected, the heading of the Tree will be displayed, otherwise a Tree will be displayed without a header");
        showTableHeader.setDefaultValue("true");

        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will show Filter support");
        filter.setDefaultValue("false");

        EJDevPropertyDefinition showVerticalLines = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.SHOW_VERTICAL_LINES,
                EJPropertyDefinitionType.BOOLEAN);
        showVerticalLines.setLabel("Show Vertical Lines");
        showVerticalLines.setDescription("Inicates if the tree show show vertical lines");
        showVerticalLines.setDefaultValue("true");

        EJDevPropertyDefinition parentItem = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.PARENT_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        parentItem.setLabel("Parent Item");
        parentItem.setMandatory(true);
        parentItem.setDescription("Parent item is used to match with relation item that build tree hierarchy using records.");

        EJDevPropertyDefinition relationItem = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.RELATION_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        relationItem.setLabel("Relation Item");
        relationItem.setMandatory(true);
        relationItem.setDescription("Relation item that build tree hierarchy using records.");

        EJDevPropertyDefinition imageItem = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.NODE_IMAGE_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        imageItem.setLabel("Image Item");
        imageItem.setDescription("item that provide node images [ url / byte array ].");
        EJDevPropertyDefinition expandLevel = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.NODE_EXPAND_LEVEL,
                EJPropertyDefinitionType.INTEGER);
        expandLevel.setLabel("Expand Level");
        // expandLevel.setDescription("item that provide node images [ url / byte array ].");

        mainGroup.addPropertyDefinition(parentItem);
        mainGroup.addPropertyDefinition(relationItem);
        mainGroup.addPropertyDefinition(imageItem);
        mainGroup.addPropertyDefinition(expandLevel);
        mainGroup.addPropertyDefinition(doubleClickActionCommand);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(showTableHeader);
        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(showVerticalLines);

        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("TITLE_BAR");
        sectionGroup.setLabel("Title Bar");

        EJDevPropertyDefinition title = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE,
                EJPropertyDefinitionType.STRING);
        title.setLabel("Title");
        title.setDescription("Title Bar Caption.");

        EJDevPropertyDefinition showTitleBarExpanded = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpanded.setLabel("Title Bar Expanded");
        showTitleBarExpanded.setDescription("If selected, the renderer will display section title bar expanded by default.");
        showTitleBarExpanded.setDefaultValue("true");

        EJDevPropertyDefinition titleBarmode = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE,
                EJPropertyDefinitionType.STRING);
        titleBarmode.setLabel("Title Bar Mode");
        titleBarmode.setDescription("The Title Bar dispaly mode");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP, "Group");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TWISTIE, "Twistie");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE, "Tree Node");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_NO_EXPAND, "Not Expandable");
        // titleBarmode.setDefaultValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP);
        // titleBarmode.setMandatory(true);

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTIONS, "Actions");
        EJDevPropertyDefinition actionID = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_ID,
                EJPropertyDefinitionType.ACTION_COMMAND);
        actionID.setLabel("Action Command");
        actionID.setDescription("Action command id for action processor.");
        actionID.setMandatory(true);

        EJDevPropertyDefinition actionImage = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_IMAGE,
                EJPropertyDefinitionType.PROJECT_FILE);
        actionImage.setLabel("Action Image");
        actionImage.setDescription("Action image file from project path.");
        actionImage.setMandatory(true);

        EJDevPropertyDefinition actionName = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_NAME,
                EJPropertyDefinitionType.STRING);
        actionName.setLabel("Action Name");
        actionName.setDescription("Action name");

        EJDevPropertyDefinition actionTooltip = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_TOOLTIP,
                EJPropertyDefinitionType.STRING);
        actionTooltip.setLabel("Action Tooltip");
        actionTooltip.setDescription("Action tooltip.");

        list.addPropertyDefinition(actionID);
        list.addPropertyDefinition(actionImage);
        list.addPropertyDefinition(actionName);
        list.addPropertyDefinition(actionTooltip);

        sectionGroup.addPropertyDefinition(titleBarmode);
        sectionGroup.addPropertyDefinition(title);
        sectionGroup.addPropertyDefinitionList(list);

        sectionGroup.addPropertyDefinition(showTitleBarExpanded);
        mainGroup.addSubGroup(sectionGroup);

        return mainGroup;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.entirej.framework.renderers.IBlockRenderer#
     * getRequiredItemPropertiesDefinitionGroup()
     */
    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block: Required Item Properties");

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.DISPLAY_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth
                .setDescription("The width (in characters) of this items column within the blocks table.If zero specified system default width will be applied.");

        EJDevPropertyDefinition headerAllignment = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALIGNMENT,
                EJPropertyDefinitionType.STRING);
        headerAllignment.setLabel("Column Alignment");
        headerAllignment.setDescription("Indicates the alignment of the column.");
        headerAllignment.setDefaultValue(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_LEFT);

        headerAllignment.addValidValue(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_LEFT, "Left");
        headerAllignment.addValidValue(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_RIGHT, "Right");
        headerAllignment.addValidValue(EJRWTTreeTableBlockDefinitionProperties.COLUMN_ALLIGN_CENTER, "Center");

        EJDevPropertyDefinition allowColumnSorting = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.ALLOW_ROW_SORTING,
                EJPropertyDefinitionType.BOOLEAN);
        allowColumnSorting.setLabel("Allow Column Sorting");
        allowColumnSorting.setDescription("If selected, the user will be able to re-order the data within the column by clicking on the column header");
        allowColumnSorting.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmResize = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.ALLOW_COLUMN_RESIZE,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmResize.setLabel("Allow Resize of Column");
        allowColunmResize.setDescription("If selected, the user will be able to resize the width of the table columns");
        allowColunmResize.setDefaultValue("true");

        EJDevPropertyDefinition allowColunmReorder = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.ALLOW_COLUMN_REORDER,
                EJPropertyDefinitionType.BOOLEAN);
        allowColunmReorder.setLabel("Allow Re-Order of Column");
        allowColunmReorder.setDescription("If selected, the user will be able to change the order of the displayed column");
        allowColunmReorder.setDefaultValue("true");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJRWTTreeTableBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The background, foreground and font attributes applied for screen item");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(displayedWidth);
        mainGroup.addPropertyDefinition(headerAllignment);
        /*
         * mainGroup.addPropertyDefinition(columnHeaderVA);
         * 
         * mainGroup.addPropertyDefinition(underlineHeader);
         */
        mainGroup.addPropertyDefinition(allowColumnSorting);
        mainGroup.addPropertyDefinition(allowColunmResize);
        mainGroup.addPropertyDefinition(allowColunmReorder);
        mainGroup.addPropertyDefinition(visualAttribute);

        return mainGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        // No spacers are available for a multi record block
        return null;
    }

    @Override
    public EJDevBlockRendererDefinitionControl addBlockControlToCanvas(EJMainScreenProperties mainScreenProperties,
            EJDevBlockDisplayProperties blockDisplayProperties, Composite parent, FormToolkit toolkit)
    {
        EJFrameworkExtensionProperties rendererProperties = blockDisplayProperties.getBlockRendererProperties();
        if (blockDisplayProperties != null)
        {
            rendererProperties = rendererProperties.getPropertyGroup(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
        }
        Composite layoutBody;
        if (rendererProperties != null
                && rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE) != null
                && !EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP.equals(rendererProperties
                        .getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE)))
        {
            int style = ExpandableComposite.TITLE_BAR;

            String mode = rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE);
            if (EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TWISTIE.equals(mode))
            {
                style = style | ExpandableComposite.TWISTIE;
            }
            else if (EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE.equals(mode))
            {
                style = style | ExpandableComposite.TREE_NODE;
            }
            if (rendererProperties.getBooleanProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED, true))
            {
                style = style | ExpandableComposite.EXPANDED;
            }
            String title = rendererProperties.getStringProperty(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE);
            Section section = toolkit.createSection(parent, style);
            if (title != null)
                section.setText(title);
            section.setFont(parent.getFont());
            section.setForeground(parent.getForeground());
            if (mainScreenProperties.getDisplayFrame())
            {

                layoutBody = new Group(section, SWT.NONE);
                layoutBody.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                if (mainScreenProperties.getFrameTitle() != null)
                    ((Group) layoutBody).setText(mainScreenProperties.getFrameTitle());

            }
            else
            {
                layoutBody = toolkit.createComposite(section);
            }
            section.setClient(layoutBody);
        }
        else
        {

            if (mainScreenProperties.getDisplayFrame())
            {

                layoutBody = new Group(parent, SWT.NONE);
                if (mainScreenProperties.getFrameTitle() != null)
                    ((Group) layoutBody).setText(mainScreenProperties.getFrameTitle());

            }
            else
            {
                layoutBody = new Composite(parent, SWT.NONE);
            }
        }

        layoutBody.setLayout(new GridLayout(mainScreenProperties.getNumCols(), false));

        Label browser = new Label(layoutBody, SWT.NONE);
        browser.setText("TREE RENDERER");
        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, Collections.<EJDevItemRendererDefinitionControl> emptyList());
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        return null;
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
