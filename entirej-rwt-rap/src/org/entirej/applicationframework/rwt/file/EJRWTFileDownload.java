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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.service.ServiceHandler;

public class EJRWTFileDownload
{

    private static Map<String,String> keys = new HashMap<String, String>();
    
    public static void download(String sourcePath, String outputName)
    {

        UUID randomUUID = UUID.randomUUID();
        String fileKey = randomUUID.toString();
        keys.put(fileKey, sourcePath);
        StringBuffer url = new StringBuffer();
        url.append(RWT.getServiceManager().getServiceHandlerUrl(SERVICE_HANDLER));
        url.append("&filename=");
        url.append(fileKey);
        url.append("&output=");
        url.append(outputName);
        String encodedURL = RWT.getResponse().encodeURL(url.toString());
        //UrlLauncher urlLauncher = RWT.getClient().getService(UrlLauncher.class);
        //urlLauncher.openURL(encodedURL);
        JavaScriptExecutor javaScriptExecutor = RWT.getClient().getService(JavaScriptExecutor.class);
        javaScriptExecutor.execute(String.format("window.location = '%s'", encodedURL));
        
    }

    public final static String         SERVICE_HANDLER       = "EJFileDownloadServiceHandler";

    public static byte[] getData(String name)
    {
        File file = new File(name);
        if (file.exists())
        {
            RandomAccessFile f = null;
            try
            {
                f = new RandomAccessFile(file, "r");
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            try
            {
                // Get and check length
                long longlength = f.length();
                int length = (int) longlength;

                // Read file and return data
                byte[] data = new byte[length];
                f.readFully(data);
                return data;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (f != null)
                    try
                    {
                        f.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
            }
        }

        return new byte[0];
    }

    
    public static ServiceHandler newServiceHandler()
    {
        return new FileDownloadServiceHandler();
    }
    
    private static class FileDownloadServiceHandler implements ServiceHandler
    {

        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
        {
            String fileName = request.getParameter("filename");
           
            String output = request.getParameter("output");
            // Get the file content
            byte[] download = getData(keys.get(fileName));
            // Send the file in the response
            response.setContentType("application/octet-stream");
            response.setContentLength(download.length);
            String contentDisposition = "attachment; filename=\"" + output + "\"";
            response.setHeader("Content-Disposition", contentDisposition);
            response.getOutputStream().write(download);
            keys.remove(fileName);
        }
    }
}
