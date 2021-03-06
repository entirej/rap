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
/**
 * 
 */
package org.entirej.applicationframework.rwt.renderers.screen.definition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.applicationframework.rwt.renderers.screen.definition.interfaces.EJRWTScreenRendererDefinitionProperties;
import org.entirej.framework.core.enumerations.EJSeparatorOrientation;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.core.renderers.definitions.interfaces.EJRendererDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;


public abstract class EJRWTScreenRendererDefinition implements EJRendererDefinition
{

    @Override
    public void loadValidValuesForProperty(EJFrameworkExtensionProperties arg0, EJPropertyDefinition arg1)
    {
    }

    @Override
    public void propertyChanged(EJPropertyDefinitionListener arg0, EJFrameworkExtensionProperties arg1, String arg2)
    {
    }

    public boolean allowSpacerItems()
    {
        return true;
    }

    protected EJPropertyDefinitionGroup getScreenPropertyDefinitions()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("Screen Properties");

        EJDevPropertyDefinition title = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.TITLE, EJPropertyDefinitionType.STRING);
        title.setLabel("Title");
        title.setDescription("The title displayed in the screen window");
        title.setMultilingual(true);

        EJDevPropertyDefinition width = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.WIDTH, EJPropertyDefinitionType.INTEGER);
        width.setLabel("Width (pixels)");
        width.setDescription("The width of the screen in <b>Pixels</b>");
        width.setDefaultValue("300");
        width.setMandatory(true);

        EJDevPropertyDefinition height = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.HEIGHT, EJPropertyDefinitionType.INTEGER);
        height.setLabel("Height (pixels)");
        height.setDescription("The height of the screen in <b>Pixels</b>");
        height.setDefaultValue("400");
        height.setMandatory(true);

        EJDevPropertyDefinition maximize = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.MAXIMIZE, EJPropertyDefinitionType.BOOLEAN);
        maximize.setLabel("Maximize");
        maximize.setDescription("Indicates if the screen should be maximized when it is opened. This property is mainly used when creating RAP applications for mobile applications");
        maximize.setDefaultValue("false");

        EJDevPropertyDefinition numCols = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.NUM_COLS, EJPropertyDefinitionType.INTEGER);
        numCols.setLabel("Number of Columns");
        numCols.setDescription("The items will be displayed in a grid. This property defines the number of columns within the grid.");
        numCols.setDefaultValue("1");
        numCols.setMandatory(true);

        EJDevPropertyDefinition cancelButtonLabel = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.CANCEL_BUTTON_TEXT,
                EJPropertyDefinitionType.STRING);
        cancelButtonLabel.setLabel("Cancel Button Label");
        cancelButtonLabel.setDescription("The label displayed on the cancel button");

        EJDevPropertyDefinition saveButtonLabel = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXECUTE_BUTTON_TEXT,
                EJPropertyDefinitionType.STRING);
        saveButtonLabel.setLabel("Execute Button Label");
        saveButtonLabel.setDescription("The label displayed on the execute button");

        mainGroup.addPropertyDefinition(title);
        mainGroup.addPropertyDefinition(width);
        mainGroup.addPropertyDefinition(height);
        mainGroup.addPropertyDefinition(maximize);
        mainGroup.addPropertyDefinition(numCols);
        mainGroup.addPropertyDefinition(saveButtonLabel);
        mainGroup.addPropertyDefinition(cancelButtonLabel);

        return mainGroup;
    }

    protected void addExtraButtonsGroup(EJPropertyDefinitionGroup parentGroup)
    {
        EJDevPropertyDefinition extraButton1Label = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_LABEL,
                EJPropertyDefinitionType.STRING);
        extraButton1Label.setLabel("Extra Button One Label");
        extraButton1Label.setMultilingual(true);
        extraButton1Label.setDescription("The label displayed on the first extra button If no label is specified, the button will not be displayed");

        EJDevPropertyDefinition extraButton1Command = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_ONE_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        extraButton1Command.setLabel("Extra Button One Action Command");
        extraButton1Command.setDescription("The action command to pass to the ExecuteActionCommand processor method for Extra Button One");

        EJDevPropertyDefinition extraButton2Label = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_LABEL,
                EJPropertyDefinitionType.STRING);
        extraButton2Label.setLabel("Extra Button Two Label");
        extraButton2Label.setDescription("The label displayed on the second extra button. If no label is specified, the button will not be displayed");
        extraButton2Label.setMultilingual(true);

        EJDevPropertyDefinition extraButton2Command = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_TWO_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        extraButton2Command.setLabel("Extra Button Two Action Command");
        extraButton2Command.setDescription("The action command to pass to the ExecuteActionCommand processor method for Extra Button Two");

        EJDevPropertyDefinition extraButton3Label = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_LABEL,
                EJPropertyDefinitionType.STRING);
        extraButton3Label.setLabel("Extra Button Three Label");
        extraButton3Label.setDescription("The label displayed on the third extra button. If no label is specified, the button will not be displayed");
        extraButton3Label.setMultilingual(true);

        EJDevPropertyDefinition extraButton3Command = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_THREE_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        extraButton3Command.setLabel("Extra Button Three Action Command");
        extraButton3Command.setDescription("The action command to pass to the ExecuteActionCommand processor method for Extra Button Three");

        EJDevPropertyDefinition extraButton4Label = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_LABEL,
                EJPropertyDefinitionType.STRING);
        extraButton4Label.setLabel("Extra Button Four Label");
        extraButton4Label.setDescription("The label displayed on the fourth extra button. If no label is specified, the button will not be displayed");
        extraButton4Label.setMultilingual(true);

        EJDevPropertyDefinition extraButton4Command = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FOUR_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        extraButton4Command.setLabel("Extra Button Four Action Command");
        extraButton4Command.setDescription("The action command to pass to the ExecuteActionCommand processor method for Extra Button Four");

        EJDevPropertyDefinition extraButton5Label = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_LABEL,
                EJPropertyDefinitionType.STRING);
        extraButton5Label.setLabel("Extra Button Five Label");
        extraButton5Label.setDescription("The label displayed on the fith extra button. If no label is specified, the button will not be displayed");
        extraButton5Label.setMultilingual(true);

        EJDevPropertyDefinition extraButton5Command = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTON_FIVE_COMMAND,
                EJPropertyDefinitionType.ACTION_COMMAND);
        extraButton5Command.setLabel("Extra Button Five Action Command");
        extraButton5Command.setDescription("The action command to pass to the ExecuteActionCommand processor method for Extra Button Five");

        EJDevPropertyDefinitionGroup extraButtonGroup = new EJDevPropertyDefinitionGroup(EJRWTScreenRendererDefinitionProperties.EXTRA_BUTTONS_GROUP);
        extraButtonGroup.setLabel("Extra Buttons");
        extraButtonGroup.addPropertyDefinition(extraButton1Label);
        extraButtonGroup.addPropertyDefinition(extraButton1Command);
        extraButtonGroup.addPropertyDefinition(extraButton2Label);
        extraButtonGroup.addPropertyDefinition(extraButton2Command);
        extraButtonGroup.addPropertyDefinition(extraButton3Label);
        extraButtonGroup.addPropertyDefinition(extraButton3Command);
        extraButtonGroup.addPropertyDefinition(extraButton4Label);
        extraButtonGroup.addPropertyDefinition(extraButton4Command);
        extraButtonGroup.addPropertyDefinition(extraButton5Label);
        extraButtonGroup.addPropertyDefinition(extraButton5Command);
        parentGroup.addSubGroup(extraButtonGroup);
    }

    protected EJPropertyDefinitionGroup getItemPropertyDefinitions()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("ItemDisplayProperties");

        EJDevPropertyDefinition itemPosition = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.ITEM_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        itemPosition.setLabel("Item Orientation");
        itemPosition.setDescription("If the item is fixed in size and smaller than other items within its displayed column, then you can indicate how the item is displayed");
        itemPosition.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        itemPosition.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        itemPosition.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        itemPosition.setDefaultValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY);
        itemPosition.setMandatory(true);

        EJDevPropertyDefinition labelPosition = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.LABEL_POSITION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelPosition.setLabel("Label Position");
        labelPosition.setDescription("The position the items label should be displayed i.e. Before or after the item");
        labelPosition.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY, "Left");
        labelPosition.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_POSITION_RIGHT_PROPERTY, "Right");
        labelPosition.setDefaultValue(EJRWTScreenRendererDefinitionProperties.LABEL_POSITION_LEFT_PROPERTY);

        EJDevPropertyDefinition labelOrientation = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_PROPERTY,
                EJPropertyDefinitionType.STRING);
        labelOrientation.setLabel("Label Orientation");
        labelOrientation.setDescription("The orientation of the labels text");
        labelOrientation.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_LEFT_PROPERTY, "Left");
        labelOrientation.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY, "Right");
        labelOrientation.addValidValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_CENTER_PROPERTY, "Center");
        labelOrientation.setDefaultValue(EJRWTScreenRendererDefinitionProperties.LABEL_ORIENTATION_RIGHT_PROPERTY);
        labelOrientation.setMandatory(true);

        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.HORIZONTAL_SPAN,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("The amount of rows this item should span");
        horizontalSpan.setDefaultValue("1");
        horizontalSpan.setMandatory(true);

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.VERTICAL_SPAN,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("The amount of columns this item should span");
        verticalSpan.setDefaultValue("1");
        verticalSpan.setMandatory(true);

        EJDevPropertyDefinition expandHorizontally = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXPAND_HORIZONTALLY,
                EJPropertyDefinitionType.BOOLEAN);
        expandHorizontally.setLabel("Expand Horizontally");
        expandHorizontally.setDescription("Indicates if this item should expand horizontally when the canvas is stretched.");
        expandHorizontally.setDefaultValue("true");
        expandHorizontally.setMandatory(true);

        EJDevPropertyDefinition expandVertically = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXPAND_VERTICALLY,
                EJPropertyDefinitionType.BOOLEAN);
        expandVertically.setLabel("Expand Vertically");
        expandVertically.setDescription("Indicates if this item should expand vertically when the canvas is stretched.");
        expandVertically.setDefaultValue("false");
        expandVertically.setMandatory(true);

        EJDevPropertyDefinition visualAttribute = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.VISUAL_ATTRIBUTE_PROPERTY,
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

        mainGroup.addPropertyDefinition(itemPosition);
        mainGroup.addPropertyDefinition(labelPosition);
        mainGroup.addPropertyDefinition(labelOrientation);
        mainGroup.addPropertyDefinition(horizontalSpan);
        mainGroup.addPropertyDefinition(verticalSpan);
        mainGroup.addPropertyDefinition(expandHorizontally);
        mainGroup.addPropertyDefinition(expandVertically);
        mainGroup.addPropertyDefinition(displayedWidth);
        mainGroup.addPropertyDefinition(displayedHeight);
        mainGroup.addPropertyDefinition(visualAttribute);

        return mainGroup;
    }

    public EJPropertyDefinitionGroup getSpacerItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("ItemDisplayProperties");

        EJDevPropertyDefinition horizontalSpan = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.HORIZONTAL_SPAN,
                EJPropertyDefinitionType.INTEGER);
        horizontalSpan.setLabel("Horizontal Span");
        horizontalSpan.setDescription("The amount of rows this spacer should span");
        horizontalSpan.setDefaultValue("1");
        horizontalSpan.setMandatory(true);

        EJDevPropertyDefinition verticalSpan = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.VERTICAL_SPAN,
                EJPropertyDefinitionType.INTEGER);
        verticalSpan.setLabel("Vertical Span");
        verticalSpan.setDescription("The amount of columns this spacer should span");
        verticalSpan.setDefaultValue("1");
        verticalSpan.setMandatory(true);

        EJDevPropertyDefinition expandx = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXPAND_HORIZONTALLY,
                EJPropertyDefinitionType.BOOLEAN);
        expandx.setLabel("Expand Horizontally");
        expandx.setDescription("Indicates if this spacer should expand horizontally to fill the gap between items before and after this spacer");

        EJDevPropertyDefinition expandy = new EJDevPropertyDefinition(EJRWTScreenRendererDefinitionProperties.EXPAND_VERTICALLY,
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

        mainGroup.addPropertyDefinition(horizontalSpan);
        mainGroup.addPropertyDefinition(verticalSpan);
        mainGroup.addPropertyDefinition(expandx);
        mainGroup.addPropertyDefinition(expandy);
        mainGroup.addPropertyDefinition(displayedWidth);
        mainGroup.addPropertyDefinition(displayedHeight);

        return mainGroup;
    }

    public EJDevItemRendererDefinitionControl getSpacerItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        int style = SWT.NULL;
        if(itemProperties.isSeparator())
        {
            style = SWT.SEPARATOR;

            if (itemProperties.getSeparatorOrientation() == EJSeparatorOrientation.HORIZONTAL)
            {
                style = style | SWT.HORIZONTAL;
            }
            else
            {
                style = style | SWT.VERTICAL;
            }
        }
        Label text = new Label(parent, style);
        return new EJDevItemRendererDefinitionControl(itemProperties, text);
    }

    public EJPropertyDefinitionGroup getItemGroupPropertiesDefinitionGroup()
    {
        return null;
    }

}
