/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors: Mojave Innovations GmbH - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.components.EJRWTStatusbar;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTDefaultMenuBuilder;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTDefaultMenuPropertiesBuilder;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTMenuTreeRoot;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTApplicationStatusbar;
import org.entirej.applicationframework.rwt.application.interfaces.EJRWTFormContainer;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.applicationframework.rwt.renderers.form.EJRWTFormRenderer;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJFrameworkManager;
import org.entirej.framework.core.EJManagedFrameworkConnection;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.EJParameterList;
import org.entirej.framework.core.EJTabLayoutComponent;
import org.entirej.framework.core.EJTranslatorHelper;
import org.entirej.framework.core.actionprocessor.interfaces.EJApplicationActionProcessor;
import org.entirej.framework.core.data.controllers.EJApplicationLevelParameter;
import org.entirej.framework.core.data.controllers.EJEmbeddedFormController;
import org.entirej.framework.core.data.controllers.EJFileUpload;
import org.entirej.framework.core.data.controllers.EJFormParameter;
import org.entirej.framework.core.data.controllers.EJInternalQuestion;
import org.entirej.framework.core.data.controllers.EJPopupFormController;
import org.entirej.framework.core.data.controllers.EJQuestion;
import org.entirej.framework.core.interfaces.EJApplicationManager;
import org.entirej.framework.core.interfaces.EJMessenger;
import org.entirej.framework.core.internal.EJInternalForm;
import org.entirej.framework.core.processorfactories.EJActionProcessorFactory;
import org.entirej.framework.core.properties.EJCoreProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.report.EJReport;
import org.entirej.framework.report.EJReportFrameworkInitialiser;
import org.entirej.framework.report.EJReportFrameworkManager;
import org.entirej.framework.report.EJReportParameterList;
import org.entirej.framework.report.data.controllers.EJReportParameter;
import org.entirej.framework.report.enumerations.EJReportExportType;
import org.entirej.framework.report.interfaces.EJReportRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EJRWTApplicationManager implements EJApplicationManager, Serializable
{
    private EJFrameworkManager           _frameworkManager;
    private EJRWTApplicationContainer    _applicationContainer;

    private EJRWTMessenger               messenger;

    private Shell                        shell;

    private List<EJInternalForm>         embeddedForms   = new ArrayList<EJInternalForm>();
    private EJApplicationActionProcessor actionProcessor = null;

    private static final Logger          logger          = LoggerFactory.getLogger(EJRWTApplicationManager.class);

    public EJRWTApplicationManager()
    {
        messenger = new EJRWTMessenger(this);
    }

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

    public void setFrameworkManager(EJFrameworkManager manager)
    {
        _frameworkManager = manager;
        try
        {
            actionProcessor = EJActionProcessorFactory.getInstance().getActionProcessor(manager, EJCoreProperties.getInstance());
        }
        catch (EJApplicationException e)
        {
            logger.warn(e.getMessage());
        }
    }

    public EJApplicationActionProcessor getApplicationActionProcessor()
    {
        return actionProcessor;
    }

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
        // build menu
        EJCoreProperties instance = EJCoreProperties.getInstance();
        EJFrameworkExtensionProperties definedProperties = instance.getApplicationDefinedProperties();

        if (definedProperties != null)
        {

            String menuConfigID = definedProperties.getStringProperty("APPLICATION_MENU");

            if (menuConfigID != null && menuConfigID.length() > 0)
            {
                EJRWTMenuTreeRoot root = EJRWTDefaultMenuPropertiesBuilder.buildMenuProperties(this, menuConfigID);
                if (root != null)
                {
                    EJRWTDefaultMenuBuilder.createApplicationMenu(this, shell, root);
                }
            }

        }

        _applicationContainer = container;
        _applicationContainer.buildApplication(this, mainWindow,null);
    }
    
    
    public void buildServiceApplication(EJRWTApplicationContainer container, Composite mainWindow,String serviceForm)
    {
        if (container == null)
        {
            throw new NullPointerException("The ApplicationContainer cannot bu null");
        }
        if (serviceForm == null)
        {
            throw new NullPointerException("The serviceForm cannot bu null");
        }
        shell = mainWindow.getShell();
       
        
        _applicationContainer = container;
        _applicationContainer.buildApplication(this, mainWindow,serviceForm);
    }

    public EJInternalForm getActiveForm()
    {
        if (_applicationContainer == null)
        {
            return null;
        }

        return _applicationContainer.getActiveForm();
    }

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

    public void removeFormFromContainer(EJInternalForm form)
    {
        if (_applicationContainer == null)
        {
            return;
        }

        _applicationContainer.remove(form);
    }

    @Override
    public void updateFormTitle(EJInternalForm form)
    {
        if (_applicationContainer == null)
        {
            return;
        }

        _applicationContainer.updateFormTitle(form);

    }

    @Override
    public Collection<EJInternalForm> getOpenedForms()
    {
        if (_applicationContainer == null)
        {
            Collections.emptyList();
        }
        return _applicationContainer.getOpenForms();
    }

    public int getOpenedFormCount()
    {
        if (_applicationContainer == null)
        {
            return 0;
        }

        return _applicationContainer.getOpenFormCount();
    }

    public boolean isFormOpened(String formName)
    {
        if (_applicationContainer == null)
        {
            return false;
        }

        return _applicationContainer.isFormOpened(formName);
    }

    @Override
    public boolean isFormOpened(EJInternalForm form)
    {
        if (_applicationContainer == null)
        {
            return false;
        }

        return _applicationContainer.isFormOpened(form);
    }

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

    public void openPopupForm(EJPopupFormController popupController)
    {
        if (_applicationContainer.getFormContainer() != null)
        {
            _applicationContainer.getFormContainer().openPopupForm(popupController);
        }
    }

    public void openEmbeddedForm(EJEmbeddedFormController embeddedController)
    {
        embeddedController.getCallingForm().getRenderer().openEmbeddedForm(embeddedController);
    }

    public void closeEmbeddedForm(EJEmbeddedFormController embeddedController)
    {
        embeddedController.getCallingForm().getRenderer().closeEmbeddedForm(embeddedController);
    }

    public void popupFormClosed()
    {
        if (_applicationContainer.getFormContainer() != null)
        {
            _applicationContainer.getFormContainer().popupFormClosed();
        }
    }

    public EJInternalForm switchToForm(String key)
    {

        return _applicationContainer.switchToForm(key);
    }

    @Override
    public void switchToForm(EJInternalForm form)
    {
        final EJInternalForm toForm = _applicationContainer.switchToForm(form);
        if (toForm != null)
        {
            Display.getDefault().asyncExec(new Runnable()
            {

                @Override
                public void run()
                {
                    toForm.focusGained();

                }
            });
        }

    }

    public EJManagedFrameworkConnection getConnection()
    {
        return _frameworkManager.getConnection();
    }

    public EJApplicationLevelParameter getApplicationLevelParameter(String valueName)
    {
        return _frameworkManager.getApplicationLevelParameter(valueName);
    }

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

    public void changeLocale(Locale locale)
    {
        _frameworkManager.changeLocale(locale);
    }

    public Locale getCurrentLocale()
    {
        return _frameworkManager.getCurrentLocale();
    }

    public EJTranslatorHelper getTranslatorHelper()
    {
        return _frameworkManager.getTranslatorHelper();
    }

    public void handleMessage(EJMessage message)
    {
        messenger.handleMessage(message);
    }

    public void handleException(Exception exception)
    {
        messenger.handleException(exception);
    }

    public void handleException(Exception exception, boolean showUserMessage)
    {
        messenger.handleException(exception, showUserMessage);
    }

    public void askQuestion(EJQuestion question)
    {
        messenger.askQuestion(question);
    }

    @Override
    public void uploadFile(EJFileUpload fileUpload)
    {
        messenger.uploadFile(fileUpload);
        
    }
    
    public void askInternalQuestion(EJInternalQuestion question)
    {
        messenger.askInternalQuestion(question);
    }

    public void openForm(String formName, EJParameterList parameterList, boolean blocking)
    {
        _frameworkManager.openForm(formName, parameterList, blocking);

    }

    public void openForm(String formName, EJParameterList parameterList)
    {
        _frameworkManager.openForm(formName, parameterList);

    }

    public void openForm(String formName)
    {
        _frameworkManager.openForm(formName);

    }

    public void runReport(String reportName)
    {
        runReport(reportName, null);

    }

    public void runReport(String reportName, EJParameterList parameterList)
    {
        if (reportManager == null)
        {
            reportManager = EJReportFrameworkInitialiser.initialiseFramework("report.ejprop");
        }
        EJReport report;
        if (parameterList == null)
        {
            report = reportManager.createReport(reportName);
        }
        else
        {

            EJReportParameterList list = new EJReportParameterList();

            Collection<EJFormParameter> allParameters = parameterList.getAllParameters();
            for (EJFormParameter parameter : allParameters)
            {
                EJReportParameter reportParameter = new EJReportParameter(parameter.getName(), parameter.getDataType());
                reportParameter.setValue(parameter.getValue());

                list.addParameter(reportParameter);
            }
            report = reportManager.createReport(reportName, list);
        }

        EJReportRunner reportRunner = reportManager.createReportRunner();
        String output = reportRunner.runReport(report);

        String name = report.getName();

        EJReportParameter reportParameter = null;
        if(report.hasReportParameter("REPORT_NAME"))
        {
            reportParameter = report.getReportParameter("REPORT_NAME");
        }

        if (reportParameter != null && reportParameter.getValue() != null && !((String) reportParameter.getValue()).isEmpty())
        {
            name = (String) reportParameter.getValue();
        }
        else
        {
            if (report.getOutputName() != null && !report.getOutputName().isEmpty())
            {
                name = report.getOutputName();
            }
        }

        String ext = report.getProperties().getExportType().toString().toLowerCase();
        report.getProperties().getExportType();
        if (report.getProperties().getExportType() == EJReportExportType.XLSX_LARGE)
        {

            ext = EJReportExportType.XLSX.toString().toLowerCase();
        }
        EJRWTImageRetriever.getGraphicsProvider().open(output, String.format("%s.%s", name, ext));

    }

    @Override
    public void runReportAsync(String reportName)
    {
        runReportAsync(reportName, null, null);

    }

    @Override
    public void runReportAsync(String reportName, EJMessage completedMessage)
    {
        runReportAsync(reportName, null, completedMessage);

    }

    @Override
    public void runReportAsync(String reportName, EJParameterList parameterList)
    {
        runReportAsync(reportName, parameterList, null);

    }

    @Override
    public void runReportAsync(final String reportName, final EJParameterList parameterList, final EJMessage completedMessage)
    {
        if (reportManager == null)
        {
            reportManager = EJReportFrameworkInitialiser.initialiseFramework("report.ejprop");
        }
        final Display display = Display.getDefault();

        final ServerPushSession pushSession = new ServerPushSession();
        Runnable job = new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    final EJReport report;
                    if (parameterList == null)
                    {
                        report = reportManager.createReport(reportName);
                    }
                    else
                    {

                        EJReportParameterList list = new EJReportParameterList();

                        Collection<EJFormParameter> allParameters = parameterList.getAllParameters();
                        for (EJFormParameter parameter : allParameters)
                        {
                            EJReportParameter reportParameter = new EJReportParameter(parameter.getName(), parameter.getDataType());
                            reportParameter.setValue(parameter.getValue());

                            list.addParameter(reportParameter);
                        }
                        report = reportManager.createReport(reportName, list);
                    }

                    EJReportRunner reportRunner = reportManager.createReportRunner();
                    final String output = reportRunner.runReport(report);

                    if (!display.isDisposed())
                    {
                        display.asyncExec(new Runnable()
                        {
                            public void run()
                            {
                                String name = report.getName();

                                EJReportParameter reportParameter = null;
                                if(report.hasReportParameter("REPORT_NAME"))
                                {
                                    reportParameter = report.getReportParameter("REPORT_NAME");
                                }

                                if (reportParameter != null && reportParameter.getValue() != null && !((String) reportParameter.getValue()).isEmpty())
                                {
                                    name = (String) reportParameter.getValue();
                                }
                                else
                                {
                                    if (report.getOutputName() != null && !report.getOutputName().isEmpty())
                                    {
                                        name = report.getOutputName();
                                    }
                                }

                                if (completedMessage != null)
                                {
                                    handleMessage(completedMessage);
                                }
                                String ext = report.getProperties().getExportType().toString().toLowerCase();
                                report.getProperties().getExportType();
                                if (report.getProperties().getExportType() == EJReportExportType.XLSX_LARGE)
                                {

                                    ext = EJReportExportType.XLSX.toString().toLowerCase();
                                }
                                EJRWTImageRetriever.getGraphicsProvider().open(output, String.format("%s.%s", name, ext));

                            }
                        });
                    }
                }
                finally
                {
                    display.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            pushSession.stop();
                        }
                    });
                }

            }
        };
        pushSession.start();
        Thread bgThread = new Thread(job);
        bgThread.setDaemon(true);
        bgThread.start();

    }

    public String generateReport(String reportName)
    {
        return generateReport(reportName, null);

    }

    public String generateReport(String reportName, EJParameterList parameterList)
    {
        if (reportManager == null)
        {
            reportManager = EJReportFrameworkInitialiser.initialiseFramework("report.ejprop");
        }
        EJReport report;
        if (parameterList == null)
        {
            report = reportManager.createReport(reportName);
        }
        else
        {

            EJReportParameterList list = new EJReportParameterList();

            Collection<EJFormParameter> allParameters = parameterList.getAllParameters();
            for (EJFormParameter parameter : allParameters)
            {
                EJReportParameter reportParameter = new EJReportParameter(parameter.getName(), parameter.getDataType());
                reportParameter.setValue(parameter.getValue());

                list.addParameter(reportParameter);
            }
            report = reportManager.createReport(reportName, list);
        }

        EJReportRunner reportRunner = reportManager.createReportRunner();
        String output = reportRunner.runReport(report);

        return output;
    }

    private EJReportFrameworkManager reportManager;

    @Override
    public EJTabLayoutComponent getTabLayoutComponent(String name)
    {
        return new EJTabLayoutComponent(this, name);
    }

    @Override
    public void setTabPageVisible(String name, String tabPageName, boolean visible)
    {
        _applicationContainer.setTabPageVisible(name,visible);
        
    }

    @Override
    public String getDisplayedTabPage(String name)
    {
        return  _applicationContainer.getDisplayedTabPage(name);
    }

    @Override
    public void setTabBadge(String name, String pageName, String badge)
    {
        _applicationContainer.setTabBadge(name,pageName,badge);
        
    }

    @Override
    public void showTabPage(String name, String pageName)
    {
        _applicationContainer.showTabPage(name,pageName);
        
    }

}
