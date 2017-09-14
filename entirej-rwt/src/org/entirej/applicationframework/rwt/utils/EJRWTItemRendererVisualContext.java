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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public class EJRWTItemRendererVisualContext
{
    private Color _backgroundColor;
    private Color _foregroundColor;
    private Font  _itemFont;

    /**
     * @param backgroundColor
     * @param foregroundColor
     * @param itemFont
     */
    public EJRWTItemRendererVisualContext(Color backgroundColor, Color foregroundColor, Font itemFont)
    {
        _backgroundColor = backgroundColor;
        _foregroundColor = foregroundColor;
        _itemFont = itemFont;
    }

    /**
     * @return the _backgroundColor
     */
    public Color getBackgroundColor()
    {
        return _backgroundColor;
    }

    /**
     * @return the _foregroundColor
     */
    public Color getForegroundColor()
    {
        return _foregroundColor;
    }

    /**
     * @return the _itemFont
     */
    public Font getItemFont()
    {
        return _itemFont;
    }

}
