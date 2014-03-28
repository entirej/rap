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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTMultiRecordBlockDefinitionProperties;
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


public class EJRWTHtmlTableBlockRendererDefinition implements EJDevBlockRendererDefinition
{

    public static final String CELL_SPACING_PROPERTY  = "CELL_SPACING";
    public static final String CELL_PADDING_PROPERTY  = "CELL_PADDING";
    public static final String DISPLAY_WIDTH_PROPERTY = "DISPLAY_WIDTH";
    public static final String ACTIONS                = "ACTIONS";
    public static final String ACTION_ID              = "ACTION_ID";
    public static final String ACTION_KEY             = "ACTION_KEY";
    public static final String HEADER_VA              = "HEADER_VA";
    public static final String ROW_ODD_VA             = "ROW_ODD_VA";
    public static final String ROW_EVEN_VA            = "ROW_EVEN_VA";

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
        EJDevPropertyDefinition actionkey = new EJDevPropertyDefinition(ACTION_KEY,
                EJPropertyDefinitionType.STRING);
        actionkey.setLabel("Action Key");
        actionkey.setDescription("The action shortcut to trigger action.");
        actionkey.setMandatory(true);
        EJDevPropertyDefinition actionID = new EJDevPropertyDefinition(ACTION_ID,
                EJPropertyDefinitionType.ACTION_COMMAND);
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

        EJDevPropertyDefinition cellSpacing = new EJDevPropertyDefinition(CELL_SPACING_PROPERTY, EJPropertyDefinitionType.INTEGER);
        cellSpacing.setLabel("Cell Spacing");
        cellSpacing.setDefaultValue("1");
        cellSpacing.setDescription("Specifies the space kept between each cell in the table (in pixels)");

        EJDevPropertyDefinition cellPadding = new EJDevPropertyDefinition(CELL_PADDING_PROPERTY, EJPropertyDefinitionType.INTEGER);
        cellPadding.setLabel("Cell Padding");
        cellPadding.setDefaultValue("5");
        cellPadding.setDescription("Specifies the space, in pixels, between the cell wall and the cell content");
        
        
        EJDevPropertyDefinition headerVA = new EJDevPropertyDefinition(HEADER_VA,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        headerVA.setLabel("Headings VA");
        headerVA.setDescription("Specifies visual attribute for table header");
        EJDevPropertyDefinition rowOddVA = new EJDevPropertyDefinition(ROW_ODD_VA,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        rowOddVA.setLabel("Row Odd VA");
        rowOddVA.setDescription("Specifies visual attribute for table odd row");
        EJDevPropertyDefinition rowEvenVA = new EJDevPropertyDefinition(ROW_EVEN_VA,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
        rowEvenVA.setLabel("Row Even VA");
        rowEvenVA.setDescription("Specifies visual attribute for table even row");

        mainGroup.addPropertyDefinitionList(list);
        mainGroup.addPropertyDefinition(showTableHeader);
        mainGroup.addPropertyDefinition(headerVA);
        mainGroup.addPropertyDefinition(rowOddVA);
        mainGroup.addPropertyDefinition(rowEvenVA);
        mainGroup.addPropertyDefinition(cellSpacing);
        mainGroup.addPropertyDefinition(cellPadding);

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

        Label browser = new Label(layoutBody, SWT.NONE);
        browser.setText("HTML TABLE RENDERER");
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
        
        EJDevPropertyDefinition headerVA = new EJDevPropertyDefinition(HEADER_VA,
                EJPropertyDefinitionType.VISUAL_ATTRIBUTE);
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

}
