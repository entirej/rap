/*******************************************************************************
 * Copyright 2013 CRESOFT AG
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
 *     CRESOFT AG - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package org.entirej.applicationframework.rwt.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.entirej.framework.core.enumerations.EJFontStyle;
import org.entirej.framework.core.enumerations.EJFontWeight;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;

public enum EJRWTVisualAttributeUtils
{
    INSTANCE;

    private Map<String, Color> _backgroundColors = new HashMap<String, Color>();
    private Map<String, Color> _foregroundColors = new HashMap<String, Color>();
    private Map<String, Font>  _fonts            = new HashMap<String, Font>();

    public Color getBackground(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        if (visualAttributeProperties != null)
        {
            Color background = _backgroundColors.get(visualAttributeProperties.getName());

            if (background != null)
            {
                return background;
            }
            if (visualAttributeProperties.getBackgroundRGB() == null
                    || visualAttributeProperties.getBackgroundRGB().equals(EJCoreVisualAttributeProperties.UNSPECIFIED))
            {
                return null;
            }
            java.awt.Color backgroundColor = visualAttributeProperties.getBackgroundColor();
            background = new Color(Display.getDefault(), backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
            _backgroundColors.put(visualAttributeProperties.getName(), background);
            return background;
        }

        return null;

    }

    public Color getForeground(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        if (visualAttributeProperties != null)
        {
            Color foreground = _foregroundColors.get(visualAttributeProperties.getName());
            if (foreground != null)
            {
                return foreground;
            }
            if (visualAttributeProperties.getForegroundRGB() == null
                    || visualAttributeProperties.getForegroundRGB().equals(EJCoreVisualAttributeProperties.UNSPECIFIED))
            {
                return null;
            }
            java.awt.Color foregroundColor = visualAttributeProperties.getForegroundColor();
            foreground = new Color(Display.getDefault(), foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
            _foregroundColors.put(visualAttributeProperties.getName(), foreground);
            return foreground;
        }

        return null;

    }

    public Font getFont(EJCoreVisualAttributeProperties visualAttributeProperties, Font defaultFont)
    {
        if (visualAttributeProperties != null)
        {
            Font font = _fonts.get(visualAttributeProperties.getName());
            if (font != null)
            {
                return font;
            }

            if (visualAttributeProperties.getFontName().equals(EJCoreVisualAttributeProperties.UNSPECIFIED)
                    && visualAttributeProperties.getFontStyle() == EJFontStyle.Unspecified
                    && visualAttributeProperties.getFontWeight() == EJFontWeight.Unspecified && !visualAttributeProperties.isFontSizeSet())
            {
                return defaultFont;
            }
            String name = null;
            int style = SWT.NORMAL;
            int size = 11;
            if (defaultFont == null)
            {
                defaultFont = Display.getDefault().getSystemFont();
            }
            if (defaultFont != null)
            {
                name = defaultFont.getFontData()[0].getName();
                style = defaultFont.getFontData()[0].getStyle();

                size = defaultFont.getFontData()[0].getHeight();
            }
            if (visualAttributeProperties.getFontName() != null && !visualAttributeProperties.getFontName().equals(EJCoreVisualAttributeProperties.UNSPECIFIED))
            {
                name = visualAttributeProperties.getFontName();
            }

            if (name == null)
            {
                return defaultFont;
            }

            EJFontStyle fontStyle = visualAttributeProperties.getFontStyle();
            switch (fontStyle)
            {
                case Italic:
                    style = style | SWT.ITALIC;
                    break;

                case Underline:
                    break;
            }

            EJFontWeight fontWeight = visualAttributeProperties.getFontWeight();
            switch (fontWeight)
            {
                case Bold:
                    style = style | SWT.BOLD;
                    break;
            }
            if (visualAttributeProperties.getFontSize() > 0)
            {
                if(visualAttributeProperties.isFontSizeAsPercentage())
                {
                    if(visualAttributeProperties.getFontSize()!=100)
                    {
                        double fontSizeP = visualAttributeProperties.getFontSize();
                        size = (int)(size* (fontSizeP/100)); 
                    }
                }
                else
                {

                    size = visualAttributeProperties.getFontSize();
                }
            }
            font = new Font(Display.getDefault(), name, size, style);
            _fonts.put(visualAttributeProperties.getName(), font);
            return font;
        }
        return defaultFont;
    }
    
    @Override
    public String toString()
    {
        return super.toString();
    }
}
