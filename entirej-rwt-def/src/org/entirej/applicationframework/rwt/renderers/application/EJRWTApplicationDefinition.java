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
/**
 * 
 */
package org.entirej.applicationframework.rwt.renderers.application;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.entirej.applicationframework.rwt.renderers.block.definition.interfaces.EJRWTSingleRecordBlockDefinitionProperties;
import org.entirej.framework.core.application.definition.interfaces.EJApplicationDefinition;
import org.entirej.framework.core.properties.EJCoreLayoutItem.TYPE;
import org.entirej.framework.core.properties.definitions.EJPropertyDefinitionType;
import org.entirej.framework.core.properties.definitions.interfaces.EJFrameworkExtensionProperties;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinition;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionGroup;
import org.entirej.framework.core.properties.definitions.interfaces.EJPropertyDefinitionListener;
import org.entirej.framework.dev.properties.EJDevPropertyDefinition;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionGroup;
import org.entirej.framework.dev.properties.EJDevPropertyDefinitionList;

public class EJRWTApplicationDefinition implements EJApplicationDefinition
{

    public static final String DISPLAY_TAB_BORDER            = "DISPLAY_TAB_BORDER";
    public static final String APPLICATION_MENU              = "APPLICATION_MENU";

    public static final String APP_MESSAGING                 = "APP_MESSAGING";
    public static final String APP_MSG_ERROR                 = "APP_MSG_ERROR";
    public static final String APP_MSG_HINT                  = "APP_MSG_HINT";
    public static final String APP_MSG_INFO                  = "APP_MSG_INFO";
    public static final String APP_MSG_WARNING               = "APP_MSG_WARNING";

    public static final String APP_MSG_TYPE                  = "APP_MSG_TYPE";
    public static final String APP_MSG_TYPE_DIALOG           = "DIALOG";
    public static final String APP_MSG_TYPE_NOTIFICATION     = "NOTIFICATION";
    public static final String APP_MSG_TYPE_BOTH             = "BOTH";

    public static final String APP_MSG_WIDTH                 = "WIDTH";
    public static final String APP_MSG_HEIGHT                = "HEIGHT";

    public static final String APP_MSG_NOTIFICATION_AUTOHIDE = "APP_MSG_NOTIFICATION_AUTOHIDE";

    public static final String SPRING_SECURITY               = "SPRING_SECURITY";
    public static final String SPRING_SECURITY_AUTH          = "SPRING_SECURITY_AUTH";

    public static final String SPRING_SECURITY_CONFIG        = "SPRING_SECURITY_CONFIG";

    public static final String SERVICE                       = "SERVICE";
    public static final String SERVICE_LIST                  = "SERVICE_LIST";
    public static final String SERVICE_PATH                  = "SERVICE_PATH";
    public static final String SERVICE_NAME                  = "SERVICE_NAME";
    public static final String SERVICE_FORM                  = "SERVICE_FORM";

    @Override
    public String getApplicationManagerClassName()
    {
        return "org.entirej.applicationframework.rwt.application.EJRWTApplicationManager";
    }

    @Override
    public EJPropertyDefinitionGroup getApplicationPropertyDefinitionGroup()
    {
        EJDevPropertyDefinitionGroup mainGroup = new EJDevPropertyDefinitionGroup("RWTAPP");
        mainGroup.setLabel("Eclipse RAP Application Framework");

        {// Spring support

            try
            {
                Class<?> support = Class.forName("org.entirej.applicationframework.rwt.spring.EJSpringSupport");
                if (support != null)
                {
                    mainGroup.setLabel("Eclipse RAP Application Framework with Spring Security");
                    EJDevPropertyDefinitionGroup springGroup = new EJDevPropertyDefinitionGroup(SPRING_SECURITY, "Spring Security Support");
                    mainGroup.addSubGroup(springGroup);

                    {// Config

                        EJDevPropertyDefinition type = new EJDevPropertyDefinition(SPRING_SECURITY_CONFIG, EJPropertyDefinitionType.PROJECT_CLASS_FILE);
                        type.setLabel("Security Config Provider");
                        type.setClassParent("org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityConfigProvider");
                        springGroup.addPropertyDefinition(type);

                    }
                    {// Auth

                        EJDevPropertyDefinition type = new EJDevPropertyDefinition(SPRING_SECURITY_AUTH, EJPropertyDefinitionType.PROJECT_CLASS_FILE);
                        type.setLabel("Authentication Provider");
                        type.setClassParent("org.entirej.applicationframework.rwt.spring.ext.EJSpringSecurityAuthenticationProvider");
                        springGroup.addPropertyDefinition(type);

                    }
                }
            }
            catch (ClassNotFoundException e)
            {
                // ignore
            }

        }

        {// services

            EJDevPropertyDefinitionGroup serviceGroup = new EJDevPropertyDefinitionGroup(SERVICE, "Services");
            mainGroup.addSubGroup(serviceGroup);

            EJDevPropertyDefinitionList list = new EJDevPropertyDefinitionList(SERVICE_LIST, "Paths");
            serviceGroup.addPropertyDefinitionList(list);

            EJDevPropertyDefinition serviceName = new EJDevPropertyDefinition(SERVICE_NAME, EJPropertyDefinitionType.STRING);
            serviceName.setLabel("Service Name");
            EJDevPropertyDefinition servicePath = new EJDevPropertyDefinition(SERVICE_PATH, EJPropertyDefinitionType.STRING);
            servicePath.setLabel("Service Path");

            EJDevPropertyDefinition serviceForm = new EJDevPropertyDefinition(SERVICE_FORM, EJPropertyDefinitionType.FORM_ID);
            serviceForm.setLabel("Service Form");
            list.addPropertyDefinition(servicePath);
            list.addPropertyDefinition(serviceName);
            list.addPropertyDefinition(serviceForm);

        }

        EJDevPropertyDefinition applicationMenu = new EJDevPropertyDefinition(APPLICATION_MENU, EJPropertyDefinitionType.MENU_GROUP);
        applicationMenu.setLabel("Application Menu");
        applicationMenu.setDescription("The Application Menu is the standard drop down menu displayed at the top of the screen. The menu is created using the <a href=\"http://docs.entirej.com/display/EJ1/Application+Menu\">EntireJ Menu Editor</a>");

        mainGroup.addPropertyDefinition(applicationMenu);
        EJDevPropertyDefinition displayTabBorder = new EJDevPropertyDefinition(DISPLAY_TAB_BORDER, EJPropertyDefinitionType.BOOLEAN);
        displayTabBorder.setLabel("Display border on tabs");
        displayTabBorder.setDescription("Indicates if borders should be used to surround tab canvases. Displaying borders on tabs can lead to many unwanted frames displayed on your application.");

        mainGroup.addPropertyDefinition(displayTabBorder);

        EJDevPropertyDefinitionGroup actionGroup = new EJDevPropertyDefinitionGroup(EJRWTSingleRecordBlockDefinitionProperties.ACTION_GROUP, "Shortcuts");
        mainGroup.addSubGroup(actionGroup);

        EJDevPropertyDefinition queryAction = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ACTION_QUERY_KEY, EJPropertyDefinitionType.STRING);
        queryAction.setLabel("Query");
        queryAction.setDefaultValue("SHIFT+Q");
        queryAction.setDescription("Use this shortcut to open the Query Screen");
        EJDevPropertyDefinition insertAction = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ACTION_INSERT_KEY, EJPropertyDefinitionType.STRING);
        insertAction.setLabel("Insert");
        insertAction.setDefaultValue("SHIFT+I");
        queryAction.setDescription("Use this shortcut to open the Insert Screen");
        EJDevPropertyDefinition updateAction = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ACTION_UPDATE_KEY, EJPropertyDefinitionType.STRING);
        updateAction.setLabel("Update");
        updateAction.setDefaultValue("SHIFT+U");
        queryAction.setDescription("Use this shortcut to open the Update Screen");
        EJDevPropertyDefinition deleteAction = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ACTION_DELETE_KEY, EJPropertyDefinitionType.STRING);
        deleteAction.setLabel("Delete");
        deleteAction.setDefaultValue("SHIFT+D");
        queryAction.setDescription("Use this shortcut to delete the current record");
        EJDevPropertyDefinition refreshAction = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ACTION_REFRESH_KEY, EJPropertyDefinitionType.STRING);
        refreshAction.setLabel("Refresh");
        refreshAction.setDefaultValue("SHIFT+R");
        queryAction.setDescription("Use this shortcut to refresh the current block by calling the blocks execute last query method");
        EJDevPropertyDefinition lovAction = new EJDevPropertyDefinition(EJRWTSingleRecordBlockDefinitionProperties.ACTION_LOV_KEY, EJPropertyDefinitionType.STRING);
        lovAction.setLabel("Lov");
        queryAction.setDescription("Use this shortcut to open the lov assigned with the current item");
        lovAction.setDefaultValue("SHIFT+ARROW_DOWN");

        actionGroup.addPropertyDefinition(queryAction);
        actionGroup.addPropertyDefinition(insertAction);
        actionGroup.addPropertyDefinition(updateAction);
        actionGroup.addPropertyDefinition(deleteAction);
        actionGroup.addPropertyDefinition(refreshAction);
        actionGroup.addPropertyDefinition(lovAction);

        // Application message settings

        EJDevPropertyDefinitionGroup messageGroup = new EJDevPropertyDefinitionGroup(APP_MESSAGING, "Application Messaging");
        messageGroup.setDescription("EntireJ gives you the possibility to have application messages displayed either in standard popup message boxes or in notifications that rise up from the bottom of the application. You can decide which option you would prefer or have a  combination of both");
        // add error message settings
        {
            EJDevPropertyDefinitionGroup errorMsgGroup = new EJDevPropertyDefinitionGroup(APP_MSG_ERROR, "Error Message");

            EJDevPropertyDefinition type = new EJDevPropertyDefinition(APP_MSG_TYPE, EJPropertyDefinitionType.STRING);
            type.setLabel("Display Type");
            type.setDescription("Indicates how messages are handeld within you application. Setting a value of Dialog will display messages as a popup dialog, setting to Notification will make messages display as notifications within the bottom right of your screen");
            type.addValidValue(APP_MSG_TYPE_DIALOG, "Dialog");
            type.addValidValue(APP_MSG_TYPE_NOTIFICATION, "Notifiaction");
            type.addValidValue(APP_MSG_TYPE_BOTH, "Both");
            type.setDefaultValue(APP_MSG_TYPE_DIALOG);

            EJDevPropertyDefinition notificationAutoHide = new EJDevPropertyDefinition(APP_MSG_NOTIFICATION_AUTOHIDE, EJPropertyDefinitionType.BOOLEAN);
            notificationAutoHide.setLabel("Auto Hide Notification");
            notificationAutoHide.setDescription("Indicates if the notifications should automatically hide after being displayed. If this is not set, the user must close the notifications manually");

            EJDevPropertyDefinition width = new EJDevPropertyDefinition(APP_MSG_WIDTH, EJPropertyDefinitionType.INTEGER);
            width.setLabel("Width");
            width.setDescription("The width of the notification");
            EJDevPropertyDefinition height = new EJDevPropertyDefinition(APP_MSG_HEIGHT, EJPropertyDefinitionType.INTEGER);
            height.setLabel("Height");
            height.setDescription("The height of the notification");

            errorMsgGroup.addPropertyDefinition(type);
            errorMsgGroup.addPropertyDefinition(notificationAutoHide);
            errorMsgGroup.addPropertyDefinition(width);
            errorMsgGroup.addPropertyDefinition(height);
            messageGroup.addSubGroup(errorMsgGroup);
        }

        // add warning message settings
        {
            EJDevPropertyDefinitionGroup warningMsgGroup = new EJDevPropertyDefinitionGroup(APP_MSG_WARNING, "Warning Message");

            EJDevPropertyDefinition type = new EJDevPropertyDefinition(APP_MSG_TYPE, EJPropertyDefinitionType.STRING);
            type.setLabel("Display Type");
            type.setDescription("Indicates how messages are handeld within you application. Setting a value of Dialog will display messages as a popup dialog, setting to Notification will make messages display as notifications within the bottom right of your screen");
            type.addValidValue(APP_MSG_TYPE_DIALOG, "Dialog");
            type.addValidValue(APP_MSG_TYPE_NOTIFICATION, "Notifiaction");
            type.addValidValue(APP_MSG_TYPE_BOTH, "Both");
            type.setDefaultValue(APP_MSG_TYPE_DIALOG);

            EJDevPropertyDefinition notificationAutoHide = new EJDevPropertyDefinition(APP_MSG_NOTIFICATION_AUTOHIDE, EJPropertyDefinitionType.BOOLEAN);
            notificationAutoHide.setLabel("Auto Hide Notification");
            notificationAutoHide.setDescription("Indicates if the notifications should automatically hide after being displayed. If this is not set, the user must close the notifications manually");

            EJDevPropertyDefinition width = new EJDevPropertyDefinition(APP_MSG_WIDTH, EJPropertyDefinitionType.INTEGER);
            width.setLabel("Width");
            width.setDescription("The width of the notification");
            EJDevPropertyDefinition height = new EJDevPropertyDefinition(APP_MSG_HEIGHT, EJPropertyDefinitionType.INTEGER);
            height.setLabel("Height");
            height.setDescription("The height of the notification");

            warningMsgGroup.addPropertyDefinition(type);
            warningMsgGroup.addPropertyDefinition(notificationAutoHide);
            warningMsgGroup.addPropertyDefinition(width);
            warningMsgGroup.addPropertyDefinition(height);
            messageGroup.addSubGroup(warningMsgGroup);
        }

        // add information message settings
        {
            EJDevPropertyDefinitionGroup infoMsgGroup = new EJDevPropertyDefinitionGroup(APP_MSG_INFO, "Information Message");

            EJDevPropertyDefinition type = new EJDevPropertyDefinition(APP_MSG_TYPE, EJPropertyDefinitionType.STRING);
            type.setLabel("Display Type");
            type.setDescription("Indicates how messages are handeld within you application. Setting a value of Dialog will display messages as a popup dialog, setting to Notification will make messages display as notifications within the bottom right of your screen");
            type.addValidValue(APP_MSG_TYPE_DIALOG, "Dialog");
            type.addValidValue(APP_MSG_TYPE_NOTIFICATION, "Notifiaction");
            type.addValidValue(APP_MSG_TYPE_BOTH, "Both");
            type.setDefaultValue(APP_MSG_TYPE_NOTIFICATION);

            EJDevPropertyDefinition notificationAutoHide = new EJDevPropertyDefinition(APP_MSG_NOTIFICATION_AUTOHIDE, EJPropertyDefinitionType.BOOLEAN);
            notificationAutoHide.setLabel("Notification Auto Hide");
            notificationAutoHide.setDescription("Indicates if the notifications should automatically hide after being displayed. If this is not set, the user must close the notifications manually");

            notificationAutoHide.setDefaultValue("true");
            EJDevPropertyDefinition width = new EJDevPropertyDefinition(APP_MSG_WIDTH, EJPropertyDefinitionType.INTEGER);
            width.setLabel("Width");
            width.setDescription("The width of the notification");
            EJDevPropertyDefinition height = new EJDevPropertyDefinition(APP_MSG_HEIGHT, EJPropertyDefinitionType.INTEGER);
            height.setLabel("Height");
            height.setDescription("The height of the notification");

            infoMsgGroup.addPropertyDefinition(type);
            infoMsgGroup.addPropertyDefinition(notificationAutoHide);
            infoMsgGroup.addPropertyDefinition(width);
            infoMsgGroup.addPropertyDefinition(height);
            messageGroup.addSubGroup(infoMsgGroup);
        }

        // add hint message settings
        {
            EJDevPropertyDefinitionGroup hintMsgGroup = new EJDevPropertyDefinitionGroup(APP_MSG_HINT, "Hint Message");

            EJDevPropertyDefinition notificationAutoHide = new EJDevPropertyDefinition(APP_MSG_NOTIFICATION_AUTOHIDE, EJPropertyDefinitionType.BOOLEAN);
            notificationAutoHide.setLabel("Auto Hide Notification");
            notificationAutoHide.setDescription("Indicates if the notifications should automatically hide after being displayed. If this is not set, the user must close the notifications manually");
            notificationAutoHide.setDefaultValue("true");

            EJDevPropertyDefinition width = new EJDevPropertyDefinition(APP_MSG_WIDTH, EJPropertyDefinitionType.INTEGER);
            width.setLabel("Width");
            width.setDescription("The width of the notification");
            EJDevPropertyDefinition height = new EJDevPropertyDefinition(APP_MSG_HEIGHT, EJPropertyDefinitionType.INTEGER);
            height.setLabel("Height");
            height.setDescription("The height of the notification");

            hintMsgGroup.addPropertyDefinition(notificationAutoHide);
            hintMsgGroup.addPropertyDefinition(width);
            hintMsgGroup.addPropertyDefinition(height);
            messageGroup.addSubGroup(hintMsgGroup);
        }

        mainGroup.addSubGroup(messageGroup);
        return mainGroup;
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
    public TYPE[] getSupportedLayoutTypes()
    {
        return TYPE.values();
    }

}
