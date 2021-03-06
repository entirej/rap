/*******************************************************************************
 * Copyright 2014 CRESOFT AG
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
package org.entirej.applicationframework.rwt.file;

import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class EJRWTFileUpload
{
    private EJRWTFileUpload()
    {
    }

    public static void promptFileUpload(String title,long fileSizeLimit,long uploadTimeLimit,String [] ext, final FileSelectionCallBack callBack)
    {

        final FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SHELL_TRIM | SWT.APPLICATION_MODAL | SWT.SINGLE);
        
        fileDialog.setText(title);
        fileDialog.setFilterExtensions(ext);
        if(fileSizeLimit>0)
            fileDialog.setUploadSizeLimit(fileSizeLimit);
        if(uploadTimeLimit>0)
            fileDialog.setUploadTimeLimit(uploadTimeLimit);
        fileDialog.open(new DialogCallback()
        {

            @Override
            public void dialogClosed(int returnCode)
            {
                String fileName = fileDialog.getFileName();
                callBack.select(fileName != null && fileName.isEmpty() ? null : new String [] {fileName});

            }
        });

    }

    public static void promptMultipleFileUpload(String title,long fileSizeLimit,long uploadTimeLimit,String [] ext, final FileSelectionCallBack callBack)
    {

        final FileDialog fileDialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SHELL_TRIM | SWT.APPLICATION_MODAL | SWT.MULTI);
        fileDialog.setText(title);
        if(fileSizeLimit>0)
            fileDialog.setUploadSizeLimit(fileSizeLimit);
        if(uploadTimeLimit>0)
            fileDialog.setUploadTimeLimit(uploadTimeLimit);
        fileDialog.open(new DialogCallback()
        {

            @Override
            public void dialogClosed(int returnCode)
            {

                String[] fileNames = fileDialog.getFileNames();
                callBack.select(fileNames != null && fileNames.length == 0 ? null : fileNames);
            }
        });

    }

    public static interface FileSelectionCallBack
    {

        void select(String[] files);

    }

    
}
