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

import java.util.Collections;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
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
import org.entirej.framework.dev.properties.interfaces.EJDevItemGroupDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevMainScreenItemDisplayProperties;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevBlockRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevBlockRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevInsertScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevQueryScreenRendererDefinition;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevUpdateScreenRendererDefinition;

public class EJRWTTreeRecordBlockDefinition implements EJDevBlockRendererDefinition
{
    public EJRWTTreeRecordBlockDefinition()
    {
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.blocks.EJRWTTreeRecordBlockRenderer";
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

        EJDevPropertyDefinition doubleClickActionCommand = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.DOUBLE_CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        doubleClickActionCommand.setLabel("Double Click Action Command");
        doubleClickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user double clicks on this block");
        EJDevPropertyDefinition clickActionCommand = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.CLICK_ACTION_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        clickActionCommand.setLabel("Click Action Command");
        clickActionCommand.setDescription("Add an action command that will be sent to the action processor when a user clicks on this block");

        EJDevPropertyDefinition hideSelection = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.HIDE_SELECTION, EJPropertyDefinitionType.BOOLEAN);
        hideSelection.setLabel("Hide Tree Selection");
        hideSelection.setDescription("If selected, the renderer will hide the tree standard selection");
        hideSelection.setDefaultValue("false");

        EJDevPropertyDefinition showTableBorder = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.HIDE_TREE_BORDER,
                EJPropertyDefinitionType.BOOLEAN);
        showTableBorder.setLabel("Hide Tree Border");
        showTableBorder.setDescription("If selected, the renderer will hide the tree standard border");
        showTableBorder.setDefaultValue("false");
        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the table. This filter can then be used by users to filter table data");
        filter.setDefaultValue("false");
        
        EJDevPropertyDefinition message = new EJDevPropertyDefinition("MESSAGE", EJPropertyDefinitionType.STRING);
        message.setLabel("Filter Message");
        message.setDescription("The message text is displayed as a hint for the user, indicating the purpose of the filter.");
        

        EJDevPropertyDefinition showcolorEffect = new EJDevPropertyDefinition(EJRWTMultiRecordBlockDefinitionProperties.COLORING_EFFECT,
                EJPropertyDefinitionType.BOOLEAN);
        showcolorEffect.setLabel("Enable Zebra Colouring");
        showcolorEffect.setDescription("If set, the renderer will display block rows in alternative coloring, e.g. Grey / White");
        showcolorEffect.setDefaultValue("true");
        
        
        EJDevPropertyDefinition parentItem = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.PARENT_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        parentItem.setLabel("Child");
        parentItem.setMandatory(true);
        parentItem.setDescription("A TreeRecord displays records in a tree hierarchy. The hierarchy is made by joining this item to a Parent Item. ");

        EJDevPropertyDefinition relationItem = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.RELATION_ITEM,
                EJPropertyDefinitionType.BLOCK_ITEM);
        relationItem.setLabel("Parent");
        relationItem.setMandatory(true);
        relationItem.setDescription("Use to join to the Child Item to create the hierarchy for the data displayed within this block");

        EJDevPropertyDefinition imageItem = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.NODE_IMAGE_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        imageItem.setLabel("Image Item");
        imageItem
                .setDescription("It is possible to dynamically add an image to the tree node by supplying the path of a picture within your project or by supplying a byteArray within the items value");

        EJDevPropertyDefinition expandLevel = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.NODE_EXPAND_LEVEL,
                EJPropertyDefinitionType.INTEGER);
        expandLevel.setLabel("Expand Level");
        expandLevel.setDescription("Indicates the level to which the tree will be opened by default when the form is opened");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The background, foreground and font attributes applied for screen item");
        visualAttribute.setMandatory(false);

        mainGroup.addPropertyDefinition(visualAttribute);
        mainGroup.addPropertyDefinition(relationItem);
        mainGroup.addPropertyDefinition(parentItem);
      
        mainGroup.addPropertyDefinition(imageItem);
        mainGroup.addPropertyDefinition(expandLevel);
        mainGroup.addPropertyDefinition(doubleClickActionCommand);
        mainGroup.addPropertyDefinition(clickActionCommand);
        mainGroup.addPropertyDefinition(hideSelection);
        mainGroup.addPropertyDefinition(showTableBorder);
        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(message);
        mainGroup.addPropertyDefinition(showcolorEffect);

        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("TITLE_BAR");
        sectionGroup.setLabel("Title Bar");

        EJDevPropertyDefinition title = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_TITLE,
                EJPropertyDefinitionType.STRING);
        title.setLabel("Title");
        title.setDescription("The title to be displayed on this title bar");

        EJDevPropertyDefinition showTitleBarExpanded = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpanded.setLabel("Title Bar Expanded");
        showTitleBarExpanded.setDescription("If selected , this blocks title bar will be expanded by default");
        showTitleBarExpanded.setDefaultValue("true");

        EJDevPropertyDefinition titleBarmode = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE,
                EJPropertyDefinitionType.STRING);
        titleBarmode.setLabel("Title Bar Mode");
        titleBarmode
                .setDescription("If you are using the Title Bars for your blocks, then it is possible to expand or collapse the block to either show or hide the content. Setting this property to either Tree or Twistie will enable the expand functionality for this block");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP, "Group");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TWISTIE, "Twistie");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE, "Tree Node");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_NO_EXPAND, "Not Expandable");

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTIONS, "Actions");
        EJDevPropertyDefinition actionID = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_ID,
                EJPropertyDefinitionType.ACTION_COMMAND);
        actionID.setLabel("Action Command");
        actionID.setDescription("The action command to be used when this action is selected");
        actionID.setMandatory(true);

        EJDevPropertyDefinition actionImage = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_IMAGE,
                EJPropertyDefinitionType.PROJECT_FILE);
        actionImage.setLabel("Action Image");
        actionImage.setDescription("The image to display in the title bar for this action");
        actionImage.setMandatory(true);

        EJDevPropertyDefinition actionName = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_NAME,
                EJPropertyDefinitionType.STRING);
        actionName.setLabel("Action Name");
        actionName.setDescription("The name of this action");

        EJDevPropertyDefinition actionTooltip = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_ACTION_TOOLTIP,
                EJPropertyDefinitionType.STRING);
        actionTooltip.setLabel("Action Tooltip");
        actionTooltip.setDescription("The text entered here will be displayed when the user hovers the mouse pointer over this action");

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

    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Tree-Record Block: Required Item Properties");

        EJDevPropertyDefinition prefix = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.ITEM_PREFIX, EJPropertyDefinitionType.STRING);
        prefix.setLabel("Prefix");
        prefix.setDescription("If you are displaying multiple items as part of the tree, then the Prefix and Suffix properties can be used to seperate the item labels");
        prefix.setMandatory(false);
        EJDevPropertyDefinition suffix = new EJDevPropertyDefinition(EJRWTTreeBlockDefinitionProperties.ITEM_SUFFIX, EJPropertyDefinitionType.STRING);
        suffix.setLabel("Suffix");
        prefix.setDescription("If you are displaying multiple items as part of the tree, then the Prefix and Suffix properties can be used to seperate the item labels");
        suffix.setMandatory(false);

        mainGroup.addPropertyDefinition(prefix);
        mainGroup.addPropertyDefinition(suffix);

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

        layoutBody.setLayout(new FillLayout());

        EJDevItemGroupDisplayProperties displayProperties = null;
        if (blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().size() > 0)
        {
            displayProperties = blockDisplayProperties.getMainScreenItemGroupDisplayContainer().getAllItemGroupDisplayProperties().iterator().next();

        }
        StringBuilder builder = new StringBuilder();
        if (displayProperties != null)
            for (EJDevScreenItemDisplayProperties screenItem : displayProperties.getAllItemDisplayProperties())
            {
                if (!screenItem.isSpacerItem())
                {
                    EJFrameworkExtensionProperties properties = ((EJDevMainScreenItemDisplayProperties) screenItem).getBlockRendererRequiredProperties();
                    String prefix = properties.getStringProperty(EJRWTTreeBlockDefinitionProperties.ITEM_PREFIX);
                    if (prefix != null)
                    {
                        builder.append(prefix);
                    }
                    builder.append(screenItem.getReferencedItemName());
                    String sufix = properties.getStringProperty(EJRWTTreeBlockDefinitionProperties.ITEM_SUFFIX);
                    if (sufix != null)
                    {
                        builder.append(sufix);
                    }

                }
            }
        String tag = builder.toString();
        if (tag.length() == 0)
        {
            tag = "<empty>";
        }
        final Tree browser = new Tree(layoutBody, SWT.BORDER);
        for (int i = 0; i < 4; i++)
        {
            TreeItem iItem = new TreeItem(browser, 0);

            iItem.setText(tag + " " + (i + 1));
            for (int j = 0; j < 4; j++)
            {
                TreeItem jItem = new TreeItem(iItem, 0);
                jItem.setText(tag + " " + (j + 1));

            }
        }

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
        return null;
    }

}
