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
package org.entirej.applicationframework.rwt.renderers.item.definition;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTTreeBlockDefinitionProperties;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;
import org.entirej.framework.dev.properties.interfaces.EJDevScreenItemDisplayProperties;
import org.entirej.framework.dev.renderer.definition.EJDevItemRendererDefinitionControl;
import org.entirej.framework.dev.renderer.definition.interfaces.EJDevItemRendererDefinition;

public class EJRWTListBoxRendererDefinition implements EJDevItemRendererDefinition
{
    public static final String LOV_DEFINITION_NAME = "LOVDEFINITION";
    public static final String DISPLAY_COLUMNS     = "DISPLAY_COLUMNS";
    public static final String COLUMN_NAME         = "COLUMN";
    public static final String COLUMN_WIDTH        = "WIDTH";
    public static final String COLUMN_FORMAT       = "FORMAT";
    public static final String COLUMN_DISPLAYED    = "DISPLAYED";
    public static final String COLUMN_RETURN_ITEM  = "RETURN_ITEM";
    public static final String COLUMN_IMAGE_ITEM   = "IMAGE_ITEM";
    public static final String INITIALIES_LOV      = "INITIALIES_LOV";
    public static final String CSS_KEY             = "CSS_KEY";
    public static final String FILTER             = "FILTER";

    public EJRWTListBoxRendererDefinition()
    {
    }

    public String getRendererClassName()
    {
        return "org.entirej.applicationframework.rwt.renderers.item.EJRWTListItemRenderer";
    }

    public boolean canExecuteActionCommand()
    {
        return true;
    }

    public void loadValidValuesForProperty(EJFrameworkExtensionProperties frameworkExtensionProperties, EJPropertyDefinition propertyDefinition)
    {
        if (COLUMN_NAME.equals(propertyDefinition.getName()) || COLUMN_IMAGE_ITEM.equals(propertyDefinition.getName()))
        {
            String lovDefItemName = frameworkExtensionProperties.getStringProperty(LOV_DEFINITION_NAME);

            if (lovDefItemName != null)
            {
                String lovDefName = lovDefItemName.substring(0, lovDefItemName.indexOf('.'));

                Iterator<String> items = frameworkExtensionProperties.getFormProperties().getLovDefinitionItemNames(lovDefName).iterator();
                while (items.hasNext())
                {
                    String value = items.next();
                    propertyDefinition.addValidValue(value, value);
                }
            }
        }

    }

    public void propertyChanged(EJPropertyDefinitionListener propertyDefListener, EJFrameworkExtensionProperties properties, String propertyName)
    {
        if (LOV_DEFINITION_NAME.equals(propertyName))
        {
            properties.getPropertyList(DISPLAY_COLUMNS).removeAllEntries();
        }
    }

    public EJPropertyDefinitionGroup getItemPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("List Box Renderer");
        mainGroup
                .setDescription("List Boxes contain a list of data which is retreved from an Lov Definition. To give your list boxes static values, create a service tha returns the static data, create an Lov Definition based on this service and then assign the Lov Definition to the List Box");

        EJDevPropertyDefinition lovDefName = new EJDevPropertyDefinition(LOV_DEFINITION_NAME, EJPropertyDefinitionType.LOV_DEFINITION_WITH_ITEMS);
        lovDefName.setLabel("Lov Definition Item Name");
        lovDefName.setDescription("The name of the Lov definition that will provide the data that will be displayed within the List Box");
        lovDefName.setNotifyWhenChanged(true);

        EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(DISPLAY_COLUMNS, "Display Columns");
        list.setDescription("A List Box has a label which the user can see as the selected value and a return value which is the actual value of the item. You can provide a list of columns whos column values will be concatenated together and displayed to the user");

        EJDevPropertyDefinition lovItemName = new EJDevPropertyDefinition(COLUMN_NAME, EJPropertyDefinitionType.STRING);
        lovItemName.setLabel("Item");
        lovItemName.setDescription("The item to display in the List Box value");
        lovItemName.setLoadValidValuesDynamically(true);
        lovItemName.setMandatory(true);

        
        
        EJDevPropertyDefinition filter = new EJDevPropertyDefinition(FILTER, EJPropertyDefinitionType.BOOLEAN);
        filter.setLabel("Add Filter");
        filter.setDescription("If selected, the renderer will display a filter field above the item data. This filter can then be used by users to filter the item displayed data");
        filter.setDefaultValue("false");
        
        filter.setDefaultValue("false");
        
        EJDevPropertyDefinition lovDisplayItem = new EJDevPropertyDefinition(COLUMN_DISPLAYED, EJPropertyDefinitionType.BOOLEAN);
        lovDisplayItem.setLabel("Displayed");
        lovDisplayItem.setDescription("Indicates if the items value will displayed in the List list or just used for mapping of values");
        lovDisplayItem.setDefaultValue("true");

        EJDevPropertyDefinition lovItemFormat = new EJDevPropertyDefinition(COLUMN_FORMAT, EJPropertyDefinitionType.STRING);
        lovItemFormat.setLabel("Datatype Format");
        lovItemFormat
                .setDescription("You can provide a default formatting option for the items value before it is displayed in the List Box. This is most important for Numbers and Dates. EntireJ uses the standard java.text.DecimalFormat and java.text.SimpleDataFormat options (##0.#####E0, yyyy.MM.dd");

        EJDevPropertyDefinition returnItem = new EJDevPropertyDefinition(COLUMN_RETURN_ITEM, EJPropertyDefinitionType.BLOCK_ITEM);
        returnItem.setLabel("Return Item");
        returnItem.setDescription("This value will be set to the given value of the specified item when the user chooses a value from the List Box");
       
        
        EJDevPropertyDefinition imageItem = new EJDevPropertyDefinition(COLUMN_IMAGE_ITEM, EJPropertyDefinitionType.STRING);
        imageItem.setLabel("Image Item");
        imageItem.setLoadValidValuesDynamically(true);
        list.addPropertyDefinition(lovItemName);
        list.addPropertyDefinition(lovDisplayItem);
        list.addPropertyDefinition(returnItem);
        list.addPropertyDefinition(lovItemFormat);

        EJDevPropertyDefinition initialiseLov = new EJDevPropertyDefinition(INITIALIES_LOV, EJPropertyDefinitionType.BOOLEAN);
        initialiseLov.setLabel("Populate on creation");
        initialiseLov
                .setDescription("Because List Boxes are based upon lov definitions, they need to make a query to be created. Thsi could take time dependin on how many list boxes you are displaying. You can set the Populate On Creation to false to delay the population of the List Box until either the items gets set to a value in the action processor or you request that the item renderer be refreshed");
        initialiseLov.setDefaultValue("true");

        EJDevPropertyDefinition customCSSKey = new EJDevPropertyDefinition(CSS_KEY, EJPropertyDefinitionType.STRING);
        customCSSKey.setLabel("Custom CSS Key");
        customCSSKey
                .setDescription("Indicates custom CSS key in project CSS file that can customize  item look and feel. Please refer to Entirej RWT CSS guide.");

        mainGroup.addPropertyDefinition(lovDefName);
        mainGroup.addPropertyDefinition(filter);
        mainGroup.addPropertyDefinition(imageItem);
        mainGroup.addPropertyDefinition(initialiseLov);
        mainGroup.addPropertyDefinition(customCSSKey);
        mainGroup.addPropertyDefinitionList(list);

        return mainGroup;
    }

    @Override
    public EJDevItemRendererDefinitionControl getItemControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        List list = new List(parent, SWT.DROP_DOWN);
        list.add("Value 1");
        list.add("Value 2");
        list.add("Value 3");

        return new EJDevItemRendererDefinitionControl(itemProperties, list);
    }

    @Override
    public Control getLabelControl(EJDevScreenItemDisplayProperties itemProperties, Composite parent, FormToolkit toolkit)
    {
        String labelText = itemProperties.getLabel();
        Label label = new Label(parent, SWT.NULL);
        label.setText(labelText == null ? "" : labelText);

        return label;
    }

    public boolean isReadOnly()
    {
        return false;
    }
}
