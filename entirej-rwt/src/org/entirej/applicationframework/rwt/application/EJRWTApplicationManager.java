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
package org.entirej.applicationframework.rwt.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTDefaultMenuBuilder;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTDefaultMenuPropertiesBuilder;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTMenuTreeRoot;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationStatusbar;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormContainer;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderers.form.EJRWTFormRenderer;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJManagedFrameworkConnection;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJParameterList;
import org.entirej.framework.core.EJTranslatorHelper;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.properties.EJCoreCanvasProperties;
import org.entirej.framework.core.properties.EJCoreMenuContainer;
import org.entirej.framework.core.properties.EJCoreMenuLeafProperties;
import org.entirej.framework.core.properties.EJCoreMenuProperties;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;

public class EJRWTApplicationManager implements EJApplicationManager, Serializable
{
    private EJFrameworkManager        _frameworkManager;
    private EJRWTApplicationContainer _applicationContainer;

    private EJRWTMessenger            messenger;

    private Shell                     shell;

    private List<EJInternalForm>      embeddedForms = new ArrayList<EJInternalForm>();

    public EJRWTApplicationManager()
    {
        messenger = new EJRWTMessenger(this);
    }

    @Override
    public EJFrameworkManager getFrameworkManager()
    {
        return _frameworkManager;
    }

    public Shell getShell()
    {
        return shell;
    }

    public EJRWTFormContainer getFormContainer()
    {
        return _applicationContainer.getFormContainer();
    }

    public EJRWTApplicationStatusbar getStatusbar()
    {
        return _applicationContainer.getStatusbar();
    }

    @Override
    public void setFrameworkManager(EJFrameworkManager manager)
    {
        _frameworkManager = manager;
    }

    @Override
    public EJMessenger getApplicationMessenger()
    {
        return messenger;
    }

    /**
     * Returns the main application window of this application
     * <p>
     * The window is passed from the Application when building the application
     * via this layout manager. There will only be a root window if the
     * application is started as a stand-alone application or an Applet. If the
     * application is started as a portlet then there will be no root window
     * 
     * @return The root window of this application or null if the application
     *         was started as a portlet application
     */
    public Composite getMainWindow()
    {
        if (_applicationContainer == null)
        {
            throw new IllegalStateException("Unable to access application root window until the application has been build");
        }

        return _applicationContainer.getMainPane();
    }

    /**
     * Builds and initialises the application from a stand alone application or
     * an applet
     * 
     * @param container
     *            The application container is the outline for the application.
     *            It contains all {@link EJSwingApplicationComponent}'s that
     *            make up this application
     * @param rootFrame
     *            If the application is run as a standalone application or
     *            started as an applet then the rootFrame will be the actual
     *            application frame. This is then used to position dialog
     *            windows or messages etc. This ensures that the dialogs belong
     *            to the main application and do not get hidden behind the
     *            application when navigating through windows
     * @param applicationIcon
     *            The icon to use for the application
     */
    public void buildApplication(EJRWTApplicationContainer container, Composite mainWindow)
    {
        if (container == null)
        {
            throw new NullPointerException("The ApplicationContainer cannot bu null");
        }
        shell = mainWindow.getShell();
        //build menu
        EJCoreProperties instance = EJCoreProperties.getInstance();
        EJFrameworkExtensionProperties definedProperties = instance.getApplicationDefinedProperties();

        if (definedProperties != null )
        {
            
            String menuConfigID = definedProperties.getStringProperty("APPLICATION_MENU");
        
            if(menuConfigID!=null && menuConfigID.length()>0)
            {
                EJRWTMenuTreeRoot root = EJRWTDefaultMenuPropertiesBuilder.buildMenuProperties(this, menuConfigID);
                if(root!=null)
                {
                    EJRWTDefaultMenuBuilder.createApplicationMenu(this, shell, root);
                }
            }
            
        }
        
        _applicationContainer = container;
        _applicationContainer.buildApplication(this, mainWindow);
    }

    @Override
    public EJInternalForm getActiveForm()
    {
        if (_applicationContainer == null)
        {
            return null;
        }

        return _applicationContainer.getActiveForm();
    }

    @Override
    public EJInternalForm getForm(String formName)
    {

        EJInternalForm form = _applicationContainer.getForm(formName);
        if (form == null)
        {
            for (EJInternalForm internalForm : embeddedForms)
            {
                if (formName.equals(internalForm.getProperties().getName()))
                {
                    form = internalForm;
                    break;
                }
            }
        }
        return form;
    }

    public EJInternalForm createEmbeddedForm(String formName, Composite parent)
    {
        try
        {

            EJInternalForm form = getFrameworkManager().createInternalForm(formName, null);
            if (form != null)
            {

                EJRWTFormRenderer renderer = (EJRWTFormRenderer) form.getRenderer();
                renderer.createControl(parent);
                EJRWTEntireJGridPane gridPane = renderer.getGuiComponent();
                gridPane.cleanLayout();
                embeddedForms.add(form);
                return form;
            }
        }
        catch (Exception e)
        {

            getApplicationMessenger().handleException(e, true);
        }
        return null;
    }

    @Override
    public void removeFormFromContainer(EJInternalForm form)
    {
        if (_applicationContainer == null)
        {
            return;
        }

        _applicationContainer.remove(form);
    }

    @Override
    public int getOpenedFormCount()
    {
        if (_applicationContainer == null)
        {
            return 0;
        }

        return _applicationContainer.getOpenFormCount();
    }

    @Override
    public boolean isFormOpened(String formName)
    {
        if (_applicationContainer == null)
        {
            return false;
        }

        return _applicationContainer.isFormOpened(formName);
    }

    @Override
    public void addFormToContainer(EJInternalForm form, boolean blocking)
    {
        if (_applicationContainer == null)
        {
            throw new IllegalStateException("Unable to open a form until the application has been built");
        }
        _applicationContainer.add(form);
    }

    /**
     * Used to open a specific form as a popup
     * <p>
     * A popup form is a normal form that will be opened in a modal window or as
     * part of the current form. The modal form normally has a direct connection
     * to this form and may receive or return values to or from the calling form
     * 
     * @param popupFormController
     *            The controller holding all required values to open the popup
     *            form
     */
    @Override
    public void openPopupForm(EJPopupFormController popupController)
    {
        if (_applicationContainer.getFormContainer() != null)
        {
            _applicationContainer.getFormContainer().openPopupForm(popupController);
        }
    }

    @Override
    public void popupFormClosed()
    {
        if (_applicationContainer.getFormContainer() != null)
        {
            _applicationContainer.getFormContainer().popupFormClosed();
        }
    }

    @Override
    public EJInternalForm switchToForm(String key)
    {

        return _applicationContainer.switchToForm(key);
    }

    @Override
    public EJManagedFrameworkConnection getConnection()
    {
        return _frameworkManager.getConnection();
    }

    @Override
    public EJApplicationLevelParameter getApplicationLevelParameter(String valueName)
    {
        return _frameworkManager.getApplicationLevelParameter(valueName);
    }

    @Override
    public void setApplicationLevelParameter(String valueName, Object value)
    {
        _frameworkManager.setApplicationLevelParameter(valueName, value);
    }

    /**
     * Used to set the current locale of the application
     * <p>
     * EntireJ stores a locale that is used by various item renderers for
     * example the NumberItemRenderer. It is used for the formatting of the
     * number etc. The default for the locale is {@link Locale.ENGLISH} but can
     * be changed via this method
     * 
     * @param locale
     *            The locale to use for this application
     */
    @Override
    public void changeLocale(Locale locale)
    {
        _frameworkManager.changeLocale(locale);
    }

    @Override
    public Locale getCurrentLocale()
    {
        return _frameworkManager.getCurrentLocale();
    }

    @Override
    public EJTranslatorHelper getTranslatorHelper()
    {
        return _frameworkManager.getTranslatorHelper();
    }

    @Override
    public void handleMessage(EJMessage message)
    {
        messenger.handleMessage(message);
    }

    @Override
    public void handleException(Exception exception)
    {
        messenger.handleException(exception);
    }

    @Override
    public void handleException(Exception exception, boolean showUserMessage)
    {
        messenger.handleException(exception, showUserMessage);
    }

    @Override
    public void askQuestion(EJQuestion question)
    {
        messenger.askQuestion(question);
    }

    @Override
    public void askInternalQuestion(EJInternalQuestion question)
    {
        messenger.askInternalQuestion(question);
    }
    
    @Override
    public void openForm(String formName, EJParameterList parameterList, boolean blocking)
    {
        _frameworkManager.openForm(formName, parameterList, blocking);
        
    }

    @Override
    public void openForm(String formName, EJParameterList parameterList)
    {
        _frameworkManager.openForm(formName, parameterList);
        
    }

    @Override
    public void openForm(String formName)
    {
        _frameworkManager.openForm(formName);
        
    }

}
