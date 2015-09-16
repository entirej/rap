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
package org.entirej.applicationframework.rwt.renderers.lov;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.renderers.screen.EJRWTQueryScreenRenderer;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.data.EJDataRecord;
import org.entirej.framework.core.data.controllers.EJItemLovController;
import org.entirej.framework.core.data.controllers.EJLovController;
import org.entirej.framework.core.enumerations.EJLovDisplayReason;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.renderers.interfaces.EJQueryScreenRenderer;

public class EJRWTLookupFormLovRenderer extends EJRWTStandardLovRenderer
{

    public static final String    SHOW_QUERY_SCREEN = "SHOW_QUERY_SCREEN";

    private EJQueryScreenRenderer _queryScreenRenderer;

    public EJRWTLookupFormLovRenderer()
    {
        _queryScreenRenderer = new EJRWTQueryScreenRenderer();
    }

    @Override
    public EJQueryScreenRenderer getQueryScreenRenderer()
    {
        return _queryScreenRenderer;
    }

    @Override
    public void enterQuery(EJDataRecord record)
    {
        if (_queryScreenRenderer == null)
        {
            EJMessage message = new EJMessage("Please define a Query Screen Renderer for this form before a query operation can be performed.");
            getLovController().getFormController().getMessenger().handleMessage(message);
        }
        else
        {
            _queryScreenRenderer.open(record);
        }
    }

    @Override
    public void initialiseRenderer(EJLovController lovController)
    {
        super.initialiseRenderer(lovController);

        _queryScreenRenderer.initialiseRenderer(lovController);
    }

    @Override
    public void displayLov(EJItemLovController itemToValidate, EJLovDisplayReason displayReason)
    {
        super.displayLov(itemToValidate, displayReason);
        if (displayReason == EJLovDisplayReason.LOV)
        {

            EJFrameworkExtensionProperties rendererProp = getLovController().getDefinitionProperties().getLovRendererProperties();

            if (!rendererProp.getBooleanProperty(SHOW_QUERY_SCREEN, true) && getLovController().getBlockRecordCount() > 0)
            {
                return;
            }

            final ServerPushSession pushSession = new ServerPushSession();
            pushSession.start();
            final Display dp = Display.getDefault();
            new java.util.Timer().schedule(new java.util.TimerTask()
            {
                @Override
                public void run()
                {
                    dp.asyncExec(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            getLovController().enterQuery();
                            pushSession.stop();
                        }
                    });

                }
            }, 100);

        }
    }

    @Override
    protected Control createToolbar(Composite parent)
    {
        Button search = new Button(parent, SWT.PUSH | SWT.FLAT);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gridData.heightHint = 23;
        gridData.widthHint = 35;
        search.setLayoutData(gridData);
        search.setToolTipText("Enter a query");
        search.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_FIND_LOV));
        search.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                getLovController().enterQuery();
            }
        });

        return search;
    }
}
