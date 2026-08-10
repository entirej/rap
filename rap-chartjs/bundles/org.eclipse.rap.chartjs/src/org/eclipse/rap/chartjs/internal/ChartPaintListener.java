/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.chartjs.internal;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.scripting.ClientListener;


public class ChartPaintListener extends ClientListener {

  public static ChartPaintListener getInstance() {
    return SingletonUtil.getSessionInstance( ChartPaintListener.class );
  }

  private ChartPaintListener() {
    super( getText() );
  }

  private static String getText() {
    String path = "org/eclipse/rap/chartjs/internal/ChartPaintListener.js";
    return ResourceLoaderUtil.readTextContent( path );
  }

}
