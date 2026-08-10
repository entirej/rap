/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.chartjs;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.chartjs.internal.ChartPaintListener;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientFileLoader;
import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("deprecation")
public abstract class AbstractChart extends Canvas
{

    private static final String CHART_OPTIONS = "chartOptions";
    private static final String CHART_DATA    = "chartData";
    private static final String CHART_MIN_JS  = "chart.min.js";
    private static final String CHARTPAINT_HANDLER_JS  = "ChartHandler.js";
    private static final String CHART_PULGIN_LBL_JS  = "chartjs-plugin-labels.min.js";
    private static final String CHART_PULGIN_ANNOTATION_JS  = "chartjs-plugin-annotation.min.js";

  //  private static final String CHARTPAINT_LISTENER_JS  = "ChartPaintListener.js";
    private static final String CHART_TYPE    = "chartType";
    private static final String REMOTE_TYPE = "chartjs.AbstractChart";
    private RemoteObject remoteObject;
    private final OperationHandler operationHandler = new AbstractOperationHandler()
    {
        private static final long serialVersionUID = 1L;

        public void handleSet(JsonObject properties)
        {
            
        }
        @Override
        public void handleCall(final String method, final JsonObject parameters)
        {
            Display.getCurrent().asyncExec(new Runnable()
            {

                @Override
                public void run()
                {
                    
                    action(method, parameters);

                }

                
            });
        }
    };
    public AbstractChart(Composite parent, int style)
    {
        super(parent, style);
        registerJS();
        requireJS();
        // NOTE: RAP re-transfers all attached widget data, even if only one of
        // them changes,
        // but JsonObject/JsonArray aren't deep-compared
      //  WidgetUtil.registerDataKeys(CHART_TYPE, CHART_DATA, CHART_OPTIONS);
       // addPaintListener();
        //applyFixes();
        Connection connection = RWT.getUISession().getConnection();
        remoteObject = connection.createRemoteObject(REMOTE_TYPE);

        remoteObject.set("parent", WidgetUtil.getId(this));
        remoteObject.setHandler(operationHandler);
    }

    public void clear()
    {
        setData(CHART_TYPE, JsonObject.NULL); // "null" won't be synchronized
        redraw();
    }

    protected void drawChart(String type, JsonObject options, JsonValue data)
    {
//        setData(CHART_TYPE, type);
//        setData(CHART_OPTIONS, options);
//        setData(CHART_DATA, data);
        
        JsonObject object = new JsonObject();
        
        object.set("type", type);
        object.set("options", options);
        object.set("data", data);
        remoteObject.set("context", object);
    }

    
    protected void action(String method, JsonObject parameters)
    {
        System.out.println(method+"-"+parameters.toString());
        
    }
    
    private void addPaintListener()
    {
        addListener(SWT.Paint, ChartPaintListener.getInstance());
    }

    private void applyFixes()
    {
        // See RAP Bug 391414
        ClientListener listener = new ClientListener("function handleEvent( event ) {\n" + "  event.widget.redraw();" + "}\n");
        addListener(SWT.Show, listener);
    }

    public static void requireJS()
    {
        ClientFileLoader service = RWT.getClient().getService(ClientFileLoader.class);
        service.requireJs(RWT.getResourceManager().getLocation(CHART_MIN_JS));
        service.requireJs(RWT.getResourceManager().getLocation(CHART_PULGIN_LBL_JS));
        service.requireJs(RWT.getResourceManager().getLocation(CHART_PULGIN_ANNOTATION_JS));

        service.requireJs(RWT.getResourceManager().getLocation(CHARTPAINT_HANDLER_JS));
    }

    public static void registerJS()
    {
        ResourceManager manager = RWT.getResourceManager();
        if (!manager.isRegistered(CHART_MIN_JS))
        {
            InputStream inputStream = ChartPaintListener.class.getResourceAsStream(CHART_MIN_JS);
            manager.register(CHART_MIN_JS, inputStream);
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            inputStream = ChartPaintListener.class.getResourceAsStream(CHART_PULGIN_LBL_JS);
            manager.register(CHART_PULGIN_LBL_JS, inputStream);
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            inputStream = ChartPaintListener.class.getResourceAsStream(CHART_PULGIN_ANNOTATION_JS);
            manager.register(CHART_PULGIN_ANNOTATION_JS, inputStream);
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
             inputStream = ChartPaintListener.class.getResourceAsStream(CHARTPAINT_HANDLER_JS);
            manager.register(CHARTPAINT_HANDLER_JS, inputStream);
            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            
        
           

        }
    }

}
