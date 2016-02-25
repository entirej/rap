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
package org.entirej.applicationframework.rwt.application.form.containers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppFormRenderer;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.renderers.EJManagedFormRendererWrapper;

public class EJRWTFormPopUp
{
    private Shell                 _mainShell;
    private EJPopupFormController _popupController;
    private EJRWTAbstractDialog   _popupDialog;

    public EJRWTFormPopUp(Shell mainShell, EJPopupFormController popupController)
    {
        _mainShell = mainShell;
        _popupController = popupController;
    }

    
    public EJPopupFormController getPopupController()
    {
        return _popupController;
    }
    
    public void showForm()
    {
        final int height = _popupController.getPopupForm().getProperties().getFormHeight();
        final int width = _popupController.getPopupForm().getProperties().getFormWidth();
        EJManagedFormRendererWrapper wrapper  = _popupController.getPopupForm().getManagedRenderer();
        final EJRWTAppFormRenderer formRenderer = (EJRWTAppFormRenderer) wrapper.getUnmanagedRenderer();
        _popupDialog = new EJRWTAbstractDialog(_mainShell)
        {
            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
                formRenderer.createControl(scrollComposite);
                scrollComposite.setContent(formRenderer.getGuiComponent());
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                scrollComposite.setMinSize(width, height);
            }
        };
        _popupDialog.create();
        _popupDialog.getShell().setText(_popupController.getPopupForm().getProperties().getTitle());
        _popupDialog.getShell().setSize(width+50, height+70);//add offset  
        _popupDialog.open();
    }

    public void close()
    {
        if (_popupDialog != null)
        {
            _popupDialog.close();
            _popupDialog = null;
        }
    }

}
