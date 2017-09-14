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
package org.entirej.applicationframework.rwt.application.components.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.ToolItem;

public abstract class EJRWTAction extends Action
{
    boolean          showText;
    private ToolItem toolItem;

    public void setShowText(boolean showText)
    {
        this.showText = showText;
    }

    public boolean isShowText()
    {
        return showText;
    }

    public void setToolItem(ToolItem toolItem)
    {
        this.toolItem = toolItem;
    }

    public ToolItem getToolItem()
    {
        return toolItem;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        if (toolItem != null && !toolItem.isDisposed())
        {
            toolItem.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    public abstract Image getImage();
}
