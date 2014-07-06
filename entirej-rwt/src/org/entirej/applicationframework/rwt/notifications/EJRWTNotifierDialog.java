/*******************************************************************************
 * Copyright 2013 Mojave Innovations GmbH
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
package org.entirej.applicationframework.rwt.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;

public class EJRWTNotifierDialog
{

    // how long the the tray popup is displayed after fading in (in
    // milliseconds)
    private static final int   DISPLAY_TIME  = 4500;
    // how long each tick is when fading in (in ms)
    private static final int   FADE_TIMER    = 50;
    // how long each tick is when fading out (in ms)
    private static final int   FADE_IN_STEP  = 30;
    // how many tick steps we use when fading out
    private static final int   FADE_OUT_STEP = 8;

    // how high the alpha value is when we have finished fading in
    private static final int   FINAL_ALPHA   = 225;

    // title foreground color
    private static Color       _titleFgColor = ColorCache.getColor(40, 73, 97);
    // text foreground color
    private static Color       _fgColor      = _titleFgColor;

    // contains list of all active popup shells
    private static List<Shell> _activeShells = new ArrayList<Shell>();

    /**
     * Creates and shows a notification dialog with a specific title, message
     * and a
     * 
     * @param title
     * @param message
     * @param type
     */
    public static void notify(final String title, final String message, final Image image, final int width, final int height, final boolean autoHide)
    {
        Display.getCurrent().asyncExec(new Runnable()
        {

            @Override
            public void run()
            {
                final Shell _shell = new Shell(Display.getDefault().getActiveShell(), SWT.NO_FOCUS | SWT.NO_TRIM);
                _shell.setLayout(new FillLayout());
                _shell.setForeground(_fgColor);
                _shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
                _shell.addListener(SWT.Dispose, new Listener()
                {
                    @Override
                    public void handleEvent(Event event)
                    {
                        if (_activeShells != null && _shell != null)
                            _activeShells.remove(_shell);
                    }
                });

                final Composite inner = new Composite(_shell, SWT.BORDER);
                inner.setData(EJ_RWT.CUSTOM_VARIANT, "notifierDialog");
                GridLayout gl = new GridLayout(3, false);
                gl.marginLeft = 5;
                gl.marginTop = 0;
                gl.marginRight = 5;
                gl.marginBottom = 5;

                inner.setLayout(gl);

                GC gc = new GC(_shell);

                String lines[] = message.split("\n");
                Point longest = null;

                for (String line : lines)
                {
                    Point extent = gc.stringExtent(line);
                    if (longest == null)
                    {
                        longest = extent;
                        continue;
                    }

                    if (extent.x > longest.x)
                    {
                        longest = extent;
                    }
                }
                gc.dispose();

                CLabel imgLabel = new CLabel(inner, SWT.NONE);
                imgLabel.setLayoutData(new GridData(GridData.FILL | GridData.FILL));
                imgLabel.setImage(image);

                CLabel titleLabel = new CLabel(inner, SWT.NONE);
                titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
                titleLabel.setText(title);
                titleLabel.setForeground(_titleFgColor);
                Font f = titleLabel.getFont();
                FontData fd = f.getFontData()[0];
                titleLabel.setFont(new Font(Display.getDefault(), new FontData(fd.getName(), fd.getHeight(), SWT.BOLD)));

                final CLabel closeLabel = new CLabel(inner, SWT.NONE);
                closeLabel.setLayoutData(new GridData(GridData.FILL | GridData.FILL));
                closeLabel.setImage(EJRWTImageRetriever.get(EJRWTImageRetriever.IMG_CLOSE));
                closeLabel.addMouseListener(new MouseAdapter()
                {
                    @Override
                    public void mouseUp(MouseEvent e)
                    {
                        fadeOut(closeLabel.getShell());
                    }
                });

                Label text = new Label(inner, SWT.WRAP);

                text.setFont(new Font(Display.getDefault(), new FontData(fd.getName(), fd.getHeight() - 2, SWT.NONE)));
                GridData gd = new GridData(GridData.FILL_BOTH);
                gd.horizontalSpan = 3;
                text.setLayoutData(gd);
                text.setForeground(_fgColor);
                text.setText(message);

                _shell.setSize(width, height);

                if (Display.getDefault().getActiveShell() == null || Display.getDefault().getActiveShell().getMonitor() == null)
                {
                    return;
                }

                Rectangle clientArea = Display.getDefault().getActiveShell().getMonitor().getClientArea();

                int startX = clientArea.x + clientArea.width - (width + 2);
                int startY = clientArea.y + clientArea.height - (height + 2);

                // move other shells up
                if (!_activeShells.isEmpty())
                {
                    List<Shell> modifiable = new ArrayList<Shell>(_activeShells);
                    Collections.reverse(modifiable);
                    for (Shell shell : modifiable)
                    {
                        Point curLoc = shell.getLocation();
                        shell.setLocation(curLoc.x, curLoc.y - 100);
                        if (curLoc.y - 100 < 0)
                        {
                            _activeShells.remove(shell);
                            shell.dispose();
                        }
                    }
                }

                _shell.setLocation(startX, startY);
                _shell.setAlpha(0);
                _shell.setVisible(true);

                _activeShells.add(_shell);

                fadeIn(_shell, autoHide);

            }
        });

    }

    private static void fadeIn(final Shell _shell, final boolean autoHide)
    {
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (_shell == null || _shell.isDisposed())
                    {
                        return;
                    }

                    int cur = _shell.getAlpha();
                    cur += FADE_IN_STEP;

                    if (cur > FINAL_ALPHA)
                    {
                        _shell.setAlpha(FINAL_ALPHA);
                        if (autoHide)
                        {
                            startTimer(_shell);
                        }
                        return;
                    }

                    _shell.setAlpha(cur);
                    Display.getDefault().timerExec(FADE_TIMER, this);
                }
                catch (Exception err)
                {
                    err.printStackTrace();
                }
            }

        };
        Display.getDefault().timerExec(FADE_TIMER, run);
    }

    private static void startTimer(final Shell _shell)
    {
        Runnable run = new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    if (_shell == null || _shell.isDisposed())
                    {
                        return;
                    }

                    fadeOut(_shell);
                }
                catch (Exception err)
                {
                    err.printStackTrace();
                }
            }

        };
        Display.getDefault().timerExec(DISPLAY_TIME, run);
    }

    private static void fadeOut(final Shell _shell)
    {
        final Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (_shell == null || _shell.isDisposed())
                    {
                        return;
                    }

                    int cur = _shell.getAlpha();
                    cur -= FADE_OUT_STEP;

                    if (cur <= 0)
                    {
                        _shell.setAlpha(0);

                        _shell.dispose();
                        _activeShells.remove(_shell);
                        return;
                    }

                    _shell.setAlpha(cur);

                    Display.getDefault().timerExec(FADE_TIMER, this);
                }
                catch (Exception err)
                {
                    err.printStackTrace();
                }
            }
        };
        Display.getDefault().timerExec(FADE_TIMER, run);
    }

    static final class ColorCache
    {
        public static final RGB        BLACK = new RGB(0, 0, 0);
        public static final RGB        WHITE = new RGB(255, 255, 255);

        private static Map<RGB, Color> _colorTable;
        private static ColorCache      _instance;

        static
        {
            _colorTable = new HashMap<RGB, Color>();
            new ColorCache();
        }

        private ColorCache()
        {
            _instance = this;
        }

        public static ColorCache getInstance()
        {
            return _instance;
        }

        /**
         * Disposes of all colors. DO ONLY CALL THIS WHEN YOU ARE SHUTTING DOWN
         * YOUR APPLICATION!
         */
        public static void disposeColors()
        {
            Iterator<Color> e = _colorTable.values().iterator();
            while (e.hasNext())
            {
                e.next().dispose();
            }

            _colorTable.clear();
        }

        public static Color getWhite()
        {
            return getColorFromRGB(new RGB(255, 255, 255));
        }

        public static Color getBlack()
        {
            return getColorFromRGB(new RGB(0, 0, 0));
        }

        public static Color getColorFromRGB(RGB rgb)
        {
            Color color = _colorTable.get(rgb);

            if (color == null)
            {
                color = new Color(Display.getCurrent(), rgb);
                _colorTable.put(rgb, color);
            }

            return color;
        }

        public static Color getColor(int r, int g, int b)
        {
            RGB rgb = new RGB(r, g, b);
            Color color = _colorTable.get(rgb);

            if (color == null)
            {
                color = new Color(Display.getCurrent(), rgb);
                _colorTable.put(rgb, color);
            }

            return color;
        }
    }
}
