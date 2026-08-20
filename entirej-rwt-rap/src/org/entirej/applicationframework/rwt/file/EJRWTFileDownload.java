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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.entirej.applicationframework.rwt.application.launcher.EJRWTSessionCleanup;
import org.entirej.framework.core.EJApplicationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EJRWTFileDownload
{
    public static final String SERVICE_HANDLER = "EJFileDownloadServiceHandler";

    private static final String TOKEN_PARAMETER = "token";
    private static final long REGISTRATION_TTL_MILLIS = 15 * 60 * 1000L;
    private static final int MAX_REGISTRATIONS = 1024;
    private static final ConcurrentMap<String, DownloadRegistration> REGISTRATIONS = new ConcurrentHashMap<>();

    public static void download(String sourcePath, String outputName)
    {
        if (sourcePath == null)
        {
            throw new EJApplicationException("The download source path cannot be null");
        }

        Path source = Path.of(sourcePath).toAbsolutePath().normalize();
        if (!Files.isRegularFile(source))
        {
            throw new EJApplicationException(String.format("File not found :%s", source.getFileName()));
        }

        DownloadRegistration registration = register(source, outputName, RWT.getUISession().getId());
        try
        {
            StringBuilder url = new StringBuilder(RWT.getServiceManager().getServiceHandlerUrl(SERVICE_HANDLER));
            url.append('&').append(TOKEN_PARAMETER).append('=');
            url.append(URLEncoder.encode(registration.token, StandardCharsets.UTF_8));

            String encodedUrl = RWT.getResponse().encodeURL(url.toString());
            JavaScriptExecutor javaScriptExecutor = RWT.getClient().getService(JavaScriptExecutor.class);
            if (javaScriptExecutor == null)
            {
                throw new EJApplicationException("The client does not support file downloads");
            }
            javaScriptExecutor.execute("window.location = " + JsonValue.valueOf(encodedUrl).toString() + ";");
        }
        catch (RuntimeException | Error failure)
        {
            registration.close();
            throw failure;
        }
    }

    public static ServiceHandler newServiceHandler()
    {
        return new FileDownloadServiceHandler();
    }

    private static synchronized DownloadRegistration register(Path source, String outputName, String sessionId)
    {
        long now = System.currentTimeMillis();
        purgeExpired(now);
        while (REGISTRATIONS.size() >= MAX_REGISTRATIONS)
        {
            DownloadRegistration oldest = REGISTRATIONS.values().stream()
                    .min(Comparator.comparingLong(registration -> registration.expiresAt))
                    .orElse(null);
            if (oldest == null)
            {
                break;
            }
            oldest.close();
        }

        String token;
        do
        {
            token = UUID.randomUUID().toString();
        }
        while (REGISTRATIONS.containsKey(token));

        EJRWTSessionCleanup cleanup = EJRWTSessionCleanup.getSession().orElse(null);
        DownloadRegistration registration = new DownloadRegistration(
                token,
                source,
                sanitizeOutputName(outputName, source),
                sessionId,
                now + REGISTRATION_TTL_MILLIS,
                cleanup);
        REGISTRATIONS.put(token, registration);
        if (cleanup != null)
        {
            try
            {
                cleanup.addCloseable(registration);
            }
            catch (RuntimeException | Error failure)
            {
                registration.close();
                throw failure;
            }
        }
        return registration;
    }

    private static void purgeExpired(long now)
    {
        for (DownloadRegistration registration : REGISTRATIONS.values())
        {
            if (registration.isExpired(now))
            {
                registration.close();
            }
        }
    }

    static String sanitizeOutputName(String outputName, Path source)
    {
        String candidate = outputName == null || outputName.isBlank() ? source.getFileName().toString() : outputName;
        StringBuilder sanitized = new StringBuilder(Math.min(candidate.length(), 255));
        candidate.codePoints().forEach(codePoint -> {
            if (sanitized.length() >= 255)
            {
                return;
            }
            if (codePoint < 32 || codePoint == 127)
            {
                return;
            }
            if (codePoint == '/' || codePoint == '\\')
            {
                sanitized.append('_');
            }
            else
            {
                sanitized.appendCodePoint(codePoint);
            }
        });
        return sanitized.toString().isBlank() ? "download" : sanitized.toString();
    }

    static String createContentDisposition(String outputName)
    {
        StringBuilder fallback = new StringBuilder(outputName.length());
        outputName.codePoints().forEach(codePoint -> {
            if (codePoint >= 32 && codePoint < 127 && (Character.isLetterOrDigit(codePoint)
                    || codePoint == ' ' || codePoint == '.' || codePoint == '-' || codePoint == '_'))
            {
                fallback.appendCodePoint(codePoint);
            }
            else
            {
                fallback.append('_');
            }
        });
        String encoded = URLEncoder.encode(outputName, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename=\"" + fallback + "\"; filename*=UTF-8''" + encoded;
    }

    private static class FileDownloadServiceHandler implements ServiceHandler
    {
        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
        {
            long now = System.currentTimeMillis();
            purgeExpired(now);

            String token = request.getParameter(TOKEN_PARAMETER);
            DownloadRegistration registration = token == null ? null : REGISTRATIONS.get(token);
            if (registration == null
                    || registration.isExpired(now)
                    || !registration.sessionId.equals(RWT.getUISession().getId())
                    || !registration.claim())
            {
                if (registration != null && registration.isExpired(now))
                {
                    registration.close();
                }
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!Files.isRegularFile(registration.source))
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", createContentDisposition(registration.outputName));
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setContentLengthLong(Files.size(registration.source));

            try (InputStream input = Files.newInputStream(registration.source))
            {
                var output = response.getOutputStream();
                input.transferTo(output);
                output.flush();
            }
        }
    }

    private static final class DownloadRegistration implements Closeable
    {
        private final String token;
        private final Path source;
        private final String outputName;
        private final String sessionId;
        private final long expiresAt;
        private final EJRWTSessionCleanup cleanup;

        private DownloadRegistration(String token, Path source, String outputName, String sessionId, long expiresAt, EJRWTSessionCleanup cleanup)
        {
            this.token = token;
            this.source = source;
            this.outputName = outputName;
            this.sessionId = sessionId;
            this.expiresAt = expiresAt;
            this.cleanup = cleanup;
        }

        private boolean isExpired(long now)
        {
            return now >= expiresAt;
        }

        private boolean claim()
        {
            boolean claimed = REGISTRATIONS.remove(token, this);
            if (claimed)
            {
                removeFromSessionCleanup();
            }
            return claimed;
        }

        @Override
        public void close()
        {
            REGISTRATIONS.remove(token, this);
            removeFromSessionCleanup();
        }

        private void removeFromSessionCleanup()
        {
            if (cleanup != null)
            {
                cleanup.removeCloseable(this);
            }
        }
    }
}
