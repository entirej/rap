/*******************************************************************************
 * Copyright 2014 Mojave Innovations GmbH
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
package org.entirej.applicationframework.rwt.file;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class EJRWTFileUpload
{
    private EJRWTFileUpload()
    {
    }

    public static String promptFileUpload(String title)
    {

        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SHELL_TRIM | SWT.APPLICATION_MODAL | SWT.SINGLE);
        fileDialog.setText(title);
        fileDialog.open();
        String fileName = fileDialog.getFileName();
        return fileName!=null && fileName.isEmpty()? null :fileName;
    }

    public static String[] promptMultipleFileUpload(String title)
    {

        FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SHELL_TRIM | SWT.APPLICATION_MODAL | SWT.MULTI);
        fileDialog.setText(title);
        fileDialog.open();
        String[] fileNames = fileDialog.getFileNames();
        return fileNames!=null && fileNames.length==0?null:fileNames;
    }
}
