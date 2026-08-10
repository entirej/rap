/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.chartjs;

import org.eclipse.swt.graphics.RGB;

public class ChartStyle
{

    private RGB     fillColor        = new RGB(128, 128, 128);
    private float   fillOpacity      = 0.6f;
    private RGB     strokeColor      = new RGB(0, 0, 0);
    private RGB     pointColor       = new RGB(0, 0, 0);
    private RGB     pointStrokeColor = new RGB(255, 255, 255);
    private boolean fill             = true;

    public ChartStyle()
    {
        // keep all initial values
    }

    public ChartStyle(int red, int green, int blue)
    {
        this(red, green, blue, 1);
    }

    public ChartStyle(int red, int green, int blue, float fillOpacity)
    {
        RGB rgb = new RGB(red, green, blue);
        setFillColor(rgb);
        setStrokeColor(rgb);
        setPointColor(rgb);
        setFillOpacity(fillOpacity);
    }

    public ChartStyle setFillColor(RGB fillColor)
    {
        this.fillColor = fillColor;
        return this;
    }

    public RGB getFillColor()
    {
        return fillColor;
    }

    public ChartStyle setFillOpacity(float fillOpacity)
    {
        this.fillOpacity = fillOpacity;
        return this;
    }

    public float getFillOpacity()
    {
        return this.fillOpacity;
    }

    public ChartStyle setStrokeColor(RGB strokeColor)
    {
        this.strokeColor = strokeColor;
        return this;
    }

    public RGB getStrokeColor()
    {
        return strokeColor;
    }

    public ChartStyle setPointColor(RGB pointColor)
    {
        this.pointColor = pointColor;
        return this;
    }

    public RGB getPointColor()
    {
        return pointColor;
    }

    public ChartStyle setPointStrokeColor(RGB pointStrokeColor)
    {
        this.pointStrokeColor = pointStrokeColor;
        return this;
    }

    public RGB getPointStrokeColor()
    {
        return pointStrokeColor;
    }

    public boolean isFill()
    {
        return fill;
    }

    public void setFill(boolean fill)
    {
        this.fill = fill;
    }

    public static String asCss(RGB rgb)
    {
        return "rgb( " + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")";
    }

    public static String asCss(RGB rgb, float fillOpacity)
    {
        return "rgba( " + rgb.red + ", " + rgb.green + ", " + rgb.blue + "," + fillOpacity + ")";
    }

}
