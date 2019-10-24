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
package org.entirej.applicationframework.rwt.application.form.containers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.layout.EJRWTScrolledComposite;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppFormRenderer;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.renderers.EJManagedFormRendererWrapper;

public class EJRWTFormModal
{
    private Shell                 _mainShell;
    private EJInternalForm _form;
    private EJRWTAbstractDialog   _popupDialog;

    public EJRWTFormModal(Shell mainShell, EJInternalForm form)
    {
        _mainShell = mainShell;
        _form = form;
    }

    
    public EJInternalForm getForm()
    {
        return _form;
    }
    
    public void showForm()
    {
        final int height = _form.getProperties().getFormHeight();
        final int width = _form.getProperties().getFormWidth();
        EJManagedFormRendererWrapper wrapper  = _form.getManagedRenderer();
        final EJRWTAppFormRenderer formRenderer = (EJRWTAppFormRenderer) wrapper.getUnmanagedRenderer();
        final EJRWTApplicationManager applicationManager = (EJRWTApplicationManager) _form.getFrameworkManager().getApplicationManager();
        _popupDialog = new EJRWTAbstractDialog(_mainShell)
        {
            @Override
            protected boolean isHelpActive()
            {
                return applicationManager.isHelpActive();
            }
            
            @Override
            public boolean isHelpAvailable()
            {
                return false;//applicationManager.isHelpSupported();
            }
            
            @Override
            protected void helpPressed(boolean active)
            {
                applicationManager.setHelpActive(active);
            }
            
            @Override
            public void createBody(Composite parent)
            {
                parent.setLayout(new FillLayout());
                final ScrolledComposite scrollComposite = new EJRWTScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
                formRenderer.init();
                formRenderer.create(scrollComposite);
                scrollComposite.setContent(formRenderer.getGuiComponent());
                scrollComposite.setExpandHorizontal(true);
                scrollComposite.setExpandVertical(true);
                scrollComposite.setMinSize(width, height);
            }
            
            @Override
            public void canceled()
            {
                _form.close();
            }
        };
        _popupDialog.create();
        _popupDialog.getShell().setText(_form.getProperties().getTitle());
        _popupDialog.getShell().setSize(width+50, height+70);//add offset  
        _popupDialog.open();
        _popupDialog.activateDialog();
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
