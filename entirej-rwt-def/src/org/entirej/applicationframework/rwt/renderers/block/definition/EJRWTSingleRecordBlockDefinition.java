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
/**
 * 
 */
package org.entirej.applicationframework.rwt.renderers.block.definition;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
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


public class EJRWTSingleRecordBlockDefinition implements EJDevBlockRendererDefinition
{

    public EJRWTSingleRecordBlockDefinition()
    {

    }

    @Override
    public boolean allowMultipleItemGroupsOnMainScreen()
    {
        return true;
    }

    @Override
    public boolean allowSpacerItems()
    {
        return true;
    }

    @Override
    public EJPropertyDefinitionGroup getBlockPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Single-Record Block");

        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR);
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
        titleBarmode.setDescription("If you are using the Title Bars for your blocks, then it is possible to expand or collapse the block to either show or hide the content. Setting this property to either Tree or Twistie will enable the expand functionality for this block");
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

    @Override
    public EJPropertyDefinitionGroup getItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinition itemPosition = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        itemPosition.setLabel("Item Orientation");
        itemPosition.setDescription("If the item is fixed in size and smaller than other items within its displayed column, then you can indicate how the item is displayed");
        itemPosition.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        itemPosition.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        itemPosition.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        itemPosition.setDefaultValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);
        itemPosition.setMandatory(true);

        EJDevPropertyDefinition labelPosition = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelPosition.setLabel("Label Position");
        labelPosition.setDescription("The position the items label should be displayed i.e. Before or after the item");
        labelPosition.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY, "Left");
        labelPosition.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY, "Right");
        labelPosition.setDefaultValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);

        EJDevPropertyDefinition labelOrientation = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelOrientation.setLabel("Label Orientation");
        labelOrientation.setDescription("The orientation of the labels text");
        labelOrientation.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        labelOrientation.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        labelOrientation.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        labelOrientation.setDefaultValue(EJRWTSingleRecordBlockDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY);
        labelOrientation.setMandatory(true);

        EJDevPropertyDefinition initiallyDisplayed = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.INITIALLY_DISPLAYED_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        initiallyDisplayed.setLabel("Initially Displayed");
        initiallyDisplayed
        .setDescription("Indicates if this item should be displayed to the user when the form starts. This property is effective if the Displayed property has been set true");
        initiallyDisplayed.setDefaultValue("true");
        initiallyDisplayed.setMandatory(true);

        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("Indicates how many columns this item should span");
        horizontalSpan.setDefaultValue("1");

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("Indicates how many rows this item should span");
        verticalSpan.setDefaultValue("1");

        EJDevPropertyDefinition expandHorizontally = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandHorizontally.setLabel("Expand Horizontally");
        expandHorizontally.setDescription("Indicates if this item should expand horizontally when the canvas is stretched.");
        expandHorizontally.setDefaultValue("true");

        EJDevPropertyDefinition expandVertically = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandVertically.setLabel("Expand Vertically");
        expandVertically.setDescription("Indicates if this item should expand vertically when the canvas is stretched.");
        expandVertically.setDefaultValue("false");

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        visualAttribute.setLabel("Visual Attribute");
        visualAttribute.setDescription("The visual attribute that should be applied to this item");
        visualAttribute.setMandatory(false);

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth.setDescription("Indicates width (in characters) of this item. If no value or zero has been entered, the width of the item will depend upon its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition displayedHeight = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedHeight.setLabel("Displayed Height");
        displayedWidth.setDescription("Indicates the height (in characters) of this item. If no value or zero has been entered, the height of the item will be relevent to its contents");
        displayedHeight.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionGroup mainScreenGroup = new EJDevPropertyDefinitionGroup(
                EJRWTSingleRecordBlockDefinitionProperties.MAIN_DISPLAY_COORDINATES_GROUP);
        mainScreenGroup.addPropertyDefinition(itemPosition);
        mainScreenGroup.addPropertyDefinition(labelPosition);
        mainScreenGroup.addPropertyDefinition(labelOrientation);
        mainScreenGroup.addPropertyDefinition(initiallyDisplayed);
        mainScreenGroup.addPropertyDefinition(horizontalSpan);
        mainScreenGroup.addPropertyDefinition(verticalSpan);
        mainScreenGroup.addPropertyDefinition(expandHorizontally);
        mainScreenGroup.addPropertyDefinition(expandVertically);
        mainScreenGroup.addPropertyDefinition(displayedWidth);
        mainScreenGroup.addPropertyDefinition(displayedHeight);
        mainScreenGroup.addPropertyDefinition(visualAttribute);

        return mainScreenGroup;
    }

    @Override
    public EJPropertyDefinitionGroup getSpacerItemPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_XSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("Indicates how many columns this spacer should span");

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_YSPAN_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("Indicates how many rows this spacer should span");

        EJDevPropertyDefinition expandx = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_X_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandx.setLabel("Expand Horizontally");
        expandx.setDescription("Indicates if this spacer should expand horizontally to fill the gap between items before and after this spacer");

        EJDevPropertyDefinition expandy = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.MAIN_EXPAND_Y_PROPERTY,
                EJPropertyDefinitionType.BOOLEAN);
        expandy.setLabel("Expand Vertically");
        expandy.setDescription("Indicates if this spacer should expand vertically to fill the gap between items above and below this spacer");

        EJDevPropertyDefinition displayedWidth = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.DISPLAYED_WIDTH_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedWidth.setLabel("Displayed Width");
        displayedWidth.setDescription("Indicates the width (in characters) of this spacer. If no value or zero has been entered, the width of the item will be relevent to its contents");
        displayedWidth.setNotifyWhenChanged(true);

        EJDevPropertyDefinition displayedHeight = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.DISPLAYED_HEIGHT_PROPERTY,
                EJPropertyDefinitionType.INTEGER);
        displayedHeight.setLabel("Displayed Height");
        displayedWidth.setDescription("Indicates the height (in characters) of this spacer. If no value or zero has been entered, the height of the item will be relevent to its contents");
        displayedHeight.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionGroup mainScreenGroup = new EJDevPropertyDefinitionGroup(
                EJRWTSingleRecordBlockDefinitionProperties.MAIN_DISPLAY_COORDINATES_GROUP);
        mainScreenGroup.addPropertyDefinition(horizontalSpan);
        mainScreenGroup.addPropertyDefinition(verticalSpan);
        mainScreenGroup.addPropertyDefinition(expandx);
        mainScreenGroup.addPropertyDefinition(expandy);
        mainScreenGroup.addPropertyDefinition(displayedWidth);
        mainScreenGroup.addPropertyDefinition(displayedHeight);

        return mainScreenGroup;
    }

    @Override
    public boolean useInsertScreen()
    {
        return true;
    }

    @Override
    public boolean useQueryScreen()
    {
        return true;
    }

    @Override
    public boolean useUpdateScreen()
    {
        return true;
    }

    @Override
    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.blocks.EJRWTSingleRecordBlockRenderer";
    }

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {
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

        EJRWTBlockPreviewerCreator creator = new EJRWTBlockPreviewerCreator();
        List<EJDevItemRendererDefinitionControl> itemControls = creator.addBlockPreviewControl(this, blockDisplayProperties, layoutBody, toolkit);

        return new EJDevBlockRendererDefinitionControl(blockDisplayProperties, itemControls);
    }

    @Override
    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        Label text = new Label(parent, SWT.NULL);

        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    @Override
    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup sectionGroup = new EJDevPropertyDefinitionGroup("Single-Record Block");

        EJDevPropertyDefinition showTitleBarExpanded = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_EXPANDED,
                EJPropertyDefinitionType.BOOLEAN);
        showTitleBarExpanded.setLabel("Title Bar Expanded");
        showTitleBarExpanded.setDescription("If selected, the renderer will display section title bar expanded by default.");
        showTitleBarExpanded.setDefaultValue("true");

        EJDevPropertyDefinition titleBarmode = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE,
                EJPropertyDefinitionType.STRING);
        titleBarmode.setLabel("Title Bar Mode");
        titleBarmode.setDescription("If you are using the Title Bars for your blocks, then it is possible to expand or collapse the block to either show or hide the content. Setting this property to either Tree or Twistie will enable the expand functionality for this block");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP, "Group");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TWISTIE, "Twistie");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_TREE_NODE, "Tree Node");
        titleBarmode.addValidValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_NO_EXPAND, "Not Expandable");
        titleBarmode.setDefaultValue(EJRWTSingleRecordBlockDefinitionProperties.ITEM_GROUP_TITLE_BAR_MODE_GROUP);

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
        sectionGroup.addPropertyDefinitionList(list);

        sectionGroup.addPropertyDefinition(showTitleBarExpanded);

        return sectionGroup;
    }

}
