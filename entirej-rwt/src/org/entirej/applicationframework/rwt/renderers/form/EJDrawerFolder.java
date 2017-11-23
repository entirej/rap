package org.entirej.applicationframework.rwt.renderers.form;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.rwt.EJ_RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;
import org.entirej.framework.core.data.controllers.EJCanvasController;
import org.entirej.framework.core.enumerations.EJCanvasDrawerPosition;
import org.entirej.framework.core.properties.containers.interfaces.EJCanvasPropertiesContainer;
import org.entirej.framework.core.properties.interfaces.EJCanvasProperties;
import org.entirej.framework.core.properties.interfaces.EJDrawerPageProperties;

public class EJDrawerFolder extends Composite
{
    private final Color SYSTEM_COLOR_RED = new Color(Display.getCurrent(), 221, 0, 0);
    private DrawerTab              active;
    EJRWTFormRenderer              ejrwtFormRenderer;

    private EJCanvasDrawerPosition position = EJCanvasDrawerPosition.LEFT;

    final EJCanvasController       canvasController;
    final Map<String, DrawerTab>   tabPages = new HashMap<String, DrawerTab>();
    private GridData               lineData;
    private Composite              seprator;

    EJDrawerFolder(EJRWTFormRenderer ejrwtFormRenderer, EJCanvasController canvasController, Composite parent, int style)
    {
        super(parent, SWT.NONE);
        this.ejrwtFormRenderer = ejrwtFormRenderer;
        this.canvasController = canvasController;
        GridLayout layout = new GridLayout(2, false);
        layout.marginBottom = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        setLayout(layout);

        lineData = new GridData(GridData.FILL_VERTICAL);
        lineData.widthHint = 1;
        seprator = new Composite(this, SWT.NONE);
        seprator.setData(EJ_RWT.CUSTOM_VARIANT, "drawer_base");
        seprator.setLayoutData(lineData);
    }

    public void addSeperator()
    {
        new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        lineData.verticalSpan++;
        layout(true);
    }

    public void setPosition(EJCanvasDrawerPosition position)
    {
        this.position = position;
    }

    protected void selection(String page)
    {

    }

    public void dispose()
    {
        for (DrawerTab tab : tabPages.values())
        {
            if (tab.shell != null)
                tab.shell.dispose();
        }
        super.dispose();
        SYSTEM_COLOR_RED.dispose();
    }

    class TabButton extends Composite
    {
       
        private int         mouse            = 0;
        private boolean     selection        = false;
        private String      text             = "";
        int                 index            = 0;
        String              badge            = null;
        private Canvas      canvas;

        public TabButton(Composite parent, int style)
        {
            super(parent, style);
            setLayout(new FillLayout());
            redrawCanvas();

        }


        @Override
        public void setData(Object data)
        {
            super.setData(data);
            canvas.setData(data);
        }

        @Override
        public void setData(String key, Object value)
        {
            super.setData(key, value);
            canvas.setData(key, value);
        }

        private void redrawCanvas()
        {
            if (canvas != null)
            {
                // canvas.setParent(null);
                canvas.dispose();

            }
            canvas = new Canvas(this, getStyle());
            canvas.setData(EJ_RWT.CUSTOM_VARIANT, getData(EJ_RWT.CUSTOM_VARIANT));
            canvas.addPaintListener(new PaintListener()
            {
                public void paintControl(PaintEvent e)
                {
                    paint(e);
                }
            });
            canvas.addMouseListener(new MouseAdapter()
            {

                public void mouseDown(MouseEvent e)
                {
                    mouse = 2;

                }

                public void mouseUp(MouseEvent e)
                {
                    mouse = 1;
                    if (e.x < 0 || e.y < 0 || e.x > getBounds().width || e.y > getBounds().height)
                    {
                        mouse = 0;
                    }

                    if (mouse == 1)
                        notifyListeners(SWT.Selection, new Event());
                }
            });
            canvas.addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent e)
                {
                    if (e.keyCode == '\r' || e.character == ' ')
                    {
                        Event event = new Event();
                        notifyListeners(SWT.Selection, event);
                    }
                }
            });
            layout(true);
        }

        public void setSelection(boolean selection)
        {
            this.selection = selection;
            if (selection)
                setData(EJ_RWT.CUSTOM_VARIANT, "drawer_select");
            else
                setData(EJ_RWT.CUSTOM_VARIANT, "drawer");
        }

        public void setText(String string)
        {
            this.text = string;
            GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            data.heightHint = ((int) EJRWTImageRetriever.getGraphicsProvider().getAvgCharWidth(getFont()) * (string.length() + 10));
            data.widthHint = getParent().getBounds().width;
            setLayoutData(data);
            redrawCanvas();
        }

        public void paint(PaintEvent e)
        {

            int offset = 5;
            e.gc.setAdvanced(true);
            // e.gc.setAntialias(SWT.ON);
            Point p = e.gc.stringExtent(text);
            if (badge != null && !badge.isEmpty())
            {
                Color color = e.gc.getForeground();
                Color bgColor = e.gc.getBackground();
                e.gc.setBackground(SYSTEM_COLOR_RED);
                e.gc.fillOval(p.y-4, 3, 14, 14);

                e.gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
                e.gc.drawText(badge, p.y , 3, true);
                e.gc.setForeground(color);
                e.gc.setBackground(bgColor);
                offset += 14;
            }

            Transform tr = null;
            tr = new Transform(e.display);

            Rectangle rectangle = getParent().getParent().getBounds();
            Rectangle r = getBounds();

            int width = 0;
            tr.translate((width / 2), (r.height / 2));
            if (position == EJCanvasDrawerPosition.LEFT)
            {
                tr.rotate(90F);
                e.gc.setTransform(tr);

                // e.gc.drawString(text, (p.x / 2) * -1, 0, true);
                e.gc.drawString(text, ((r.height / 2) * -1) + offset, 0 - (p.y), true);
            }

            if (position == EJCanvasDrawerPosition.RIGHT)
            {
                tr.rotate(270F);

                e.gc.setTransform(tr);

                // e.gc.drawString(text, (p.x / 2) * -1, 0, true);
                e.gc.drawString(text, ((r.height / 2) * -1) + offset, 0 + (p.y / 2), true);
            }

        }
    }

    public void showPage(String pageName)
    {
        for (DrawerTab tab : tabPages.values())
        {
            if (tab.page.getName().equals(pageName))
            {
                tab.showTab(false);
            }
        }

    }

    public void setTabPageVisible(String pageName, boolean visible)
    {
        for (DrawerTab tab : tabPages.values())
        {
            if (tab.page.getName().equals(pageName))
            {
                if (visible)
                {
                    tab.showTab(false);
                }
                else
                    tab.shell.setVisible(visible);
            }
        }

    }

    public Control getFolder()
    {
        return this;
    }

    public DrawerTab newTab(EJDrawerPageProperties page)
    {
        return new DrawerTab(page);
    }

    public String getActiveKey()
    {
        for (DrawerTab tab : tabPages.values())
        {
            if (tab.shell != null && tab.shell.isVisible())
            {
                return tab.page.getName();
            }
        }
        return null;
    }

    public void setDrawerPageBadge(String tabPageName, String badge)
    {

        for (DrawerTab tab : tabPages.values())
        {
            if (tab.page.getName().equals(tabPageName))
            {
                tab.rotatingButton.badge = badge;
                tab.rotatingButton.redrawCanvas();
                break;
            }
        }
    }

    public void put(String name, DrawerTab tab)
    {
        tabPages.put(name, (DrawerTab) tab);

    }

    class DrawerTab
    {
        EJDrawerPageProperties page;
        int                    index;
        Shell                  shell;
        EJRWTEntireJGridPane   composite;
        final AtomicBoolean    init = new AtomicBoolean(true);
        private TabButton      rotatingButton;
        protected long      deactive;

        DrawerTab(EJDrawerPageProperties page)
        {
            this.page = page;
        }

        public void create(boolean b)
        {

            Display current = Display.getCurrent();
            shell = new Shell(current, SWT.RESIZE | SWT.ON_TOP);
            shell.addListener(SWT.Deactivate, new Listener()
            {
                
                @Override
                public void handleEvent(Event event)
                {
                   if(shell.isVisible() )
                   {
                      
                       
                       Display.getDefault().asyncExec(new Runnable()
                    {
                        
                        @Override
                        public void run()
                        {
                            shell.setVisible(false);
                            // selection(null);
                            active.rotatingButton.setSelection(false);
                            active.deactive = System.currentTimeMillis();
                        }
                    });
                   }
                    
                }
            });
            shell.setLayout(new FillLayout());
            final ControlListener listener = new ControlListener()
            {

                @Override
                public void controlResized(ControlEvent e)
                {
                    update();

                }

                @Override
                public void controlMoved(ControlEvent e)
                {
                    update();

                }

                void update()
                {
                    if (shell != null && !shell.isDisposed() && shell.isVisible() && !EJDrawerFolder.this.isDisposed())
                    {
                        Point point = EJDrawerFolder.this.toDisplay(0, 0);
                        Rectangle bounds = EJDrawerFolder.this.getBounds();
                        if (position == EJCanvasDrawerPosition.RIGHT)
                            shell.setLocation((point.x + bounds.width) - 1, point.y);
                        if (position == EJCanvasDrawerPosition.LEFT)
                            shell.setLocation((point.x - page.getDrawerWidth()) + 1, point.y);
                        shell.setSize(page.getDrawerWidth(), bounds.height);
                    }
                }
            };
            EJDrawerFolder.this.addControlListener(listener);
            EJDrawerFolder.this.getShell().addControlListener(listener);
            EJDrawerFolder.this.addDisposeListener(new DisposeListener()
            {

                @Override
                public void widgetDisposed(DisposeEvent event)
                {
                    EJDrawerFolder.this.getShell().removeControlListener(listener);

                }
            });

            composite = new EJRWTEntireJGridPane(shell, page.getNumCols(), SWT.BORDER);

            composite.setData(EJ_RWT.CUSTOM_VARIANT, EJ_RWT.CSS_CV_FORM);

            rotatingButton = new TabButton(EJDrawerFolder.this, SWT.None);
            addSeperator();
            if (position == EJCanvasDrawerPosition.RIGHT && tabPages.size() == 0)
            {
                // seprator.dispose();
                // seprator = new Composite(EJDrawerFolder.this,SWT.NONE);
                // seprator.setData(EJ_RWT.CUSTOM_VARIANT, "drawer_base");
                // seprator.setLayoutData(lineData);
            }
            rotatingButton.setText(page.getPageTitle() != null && page.getPageTitle().length() > 0 ? page.getPageTitle() : page.getName());
            lineData.verticalSpan++;
            rotatingButton.addListener(SWT.Selection, new Listener()
            {

                public void handleEvent(Event e)
                {
                    if (init.getAndSet(false))
                    {
                        final EJCanvasPropertiesContainer containedCanvases = page.getContainedCanvases();
                        for (EJCanvasProperties pageProperties : containedCanvases.getAllCanvasProperties())
                        {
                            ejrwtFormRenderer.createCanvas(composite, pageProperties, canvasController);
                        }
                        composite.layout(true);
                    }
                    showTab(true);

                }

            });
            layout(true);

        }

        private void showTab(boolean toggle)
        {
            if (active != null && active.shell.isVisible() && active.shell != shell)
            {
                active.shell.setVisible(false);
                active.rotatingButton.setSelection(false);
            }
            
            if(Math.abs(deactive-System.currentTimeMillis())<300)
            {
                return;//avoid close events
            }
            active = this;
            active.rotatingButton.setSelection(true);

            if (toggle && shell.isVisible())
            {
                shell.setVisible(false);
                // selection(null);
                active.rotatingButton.setSelection(false);
            }
            else
            {

                Point point = EJDrawerFolder.this.toDisplay(0, 0);
                Rectangle bounds = EJDrawerFolder.this.getBounds();

                if (position == EJCanvasDrawerPosition.RIGHT)
                    shell.setLocation((point.x + bounds.width) - 1, point.y);
                if (position == EJCanvasDrawerPosition.LEFT)
                    shell.setLocation((point.x - page.getDrawerWidth()) + 1, point.y);
                shell.setSize(page.getDrawerWidth(), bounds.height);
                shell.open();
                selection(active.page.getName());
            }
        }

        public void setIndex(int index)
        {
            this.index = index;

        }

    }
}
