/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 * Contributors: CRESOFT AG - initial API and implementation
 ******************************************************************************/
package org.entirej.applicationframework.rwt.application;

import java.util.concurrent.Callable;

import org.eclipse.rwt.EJRWTHtmlViewSupport;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.Section;
import org.entirej.framework.core.data.controllers.EJFileUpload;
import org.entirej.framework.report.EJReportFrameworkManager;

public interface EJRWTGraphicsProvider
{
    Image getImage(String name, ClassLoader loader);

    float getAvgCharWidth(Font font);

    int getCharHeight(Font font);

    void rendererSection(Section section);

    void open(String output, String name);

    public void promptFileUpload(EJFileUpload fileUpload, Callable<Object> callable);

    public void setReportFrameworkManager(EJReportFrameworkManager manager);

    public EJRWTHtmlViewSupport getHtmlViewSupport();
}
