package org.entirej.applicationframework.rwt.application.form.containers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayDialog.TrayLocation;
import org.entirej.applicationframework.rwt.layout.EJRWTEntireJGridPane;

public  class EJRWTTrayPane extends EJRWTEntireJGridPane implements ITrayPane
{

   

    public static final int RIGHT_TO_LEFT = 1 << 26;

    private Control         base;

    protected TrayLocation  location      = TrayLocation.RIGHT;

    /**
     * The dialog's tray (null if none).
     */
    private EJRWTDialogTray tray;

    /**
     * The tray's control (null if none).
     */
    private Control         trayControl;
    
    private int sizeCache =-1;

  

    /*
     * The sash that allows the user to resize the tray.
     */
    private Sash            sash;

    public EJRWTTrayPane(Composite parent)
    {
        super(parent, 5);
        cleanLayout();
        
        
    }

    public void initBase(Control base)
    {
        this.base = base;
    }
    
    
    /* (non-Javadoc)
     * @see org.entirej.applicationframework.rwt.application.form.containers.ITrayPane#closeTray()
     */
    @Override
    public void closeTray() throws IllegalStateException
    {
        if (getTray() == null)
        {
            return;
            //throw new IllegalStateException("Tray was not open"); //$NON-NLS-1$
        }

        trayControl.dispose();
        trayControl = null;
        tray = null;

        sash.dispose();
        sash = null;
        layout(true);

    }

    /*
     * Returns whether or not the given layout can support the addition of a
     * tray.
     */
    private boolean isCompatibleLayout(Layout layout)
    {
        if (layout != null && layout instanceof GridLayout)
        {
            GridLayout grid = (GridLayout) layout;
            return !grid.makeColumnsEqualWidth && (grid.horizontalSpacing == 0) && (grid.marginWidth == 0) && (grid.marginHeight == 0);
        }
        return false;
    }

    public GridLayout getLayout()
    {
        GridLayout layout = (GridLayout) super.getLayout();
        layout.numColumns = 5;
        layout.horizontalSpacing = 0;
        return layout;
    }

    /* (non-Javadoc)
     * @see org.entirej.applicationframework.rwt.application.form.containers.ITrayPane#getTray()
     */
    @Override
    public EJRWTDialogTray getTray()
    {
        return tray;
    }

    /* (non-Javadoc)
     * @see org.entirej.applicationframework.rwt.application.form.containers.ITrayPane#openTray(org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayDialog.TrayLocation, org.entirej.applicationframework.rwt.application.form.containers.EJRWTDialogTray, int)
     */
    @Override
    public void openTray(final TrayLocation location, EJRWTDialogTray tray, int size) throws IllegalStateException, UnsupportedOperationException
    {
        if(sizeCache>0)
        {
            size = sizeCache;
        }
        if (tray == null)
        {
            throw new NullPointerException("Tray was null"); //$NON-NLS-1$
        }
        if (getTray() != null)
        {
            throw new IllegalStateException("Tray was already open"); //$NON-NLS-1$
        }
        if (!isCompatibleLayout(getLayout()))
        {
            throw new UnsupportedOperationException("Trays not supported with custom layouts"); //$NON-NLS-1$
        }
        this.location = location;

        switch (location)
        {
            case BOTTOM:
            case TOP:
                
                sash = new Sash(this, SWT.HORIZONTAL);
                sash.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                break;

            default:
                sash = new Sash(this, SWT.VERTICAL);
                sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
                break;
        }

        trayControl = tray.createContents(this);
        final GridData data;
        switch (location)
        {
            case TOP:
            case BOTTOM:
                data = new GridData(GridData.FILL_HORIZONTAL);
                data.heightHint = size;

                GridLayout grid = (GridLayout) getLayout();
                grid.numColumns = 1;
                // data.horizontalSpan = 5;
                // ((GridData)leftSeparator.getLayoutData()).horizontalSpan = 5;
                // ((GridData)sash.getLayoutData()).horizontalSpan = 5;
                // ((GridData)rightSeparator.getLayoutData()).horizontalSpan =
                // 5;
                // ((GridData)rightSeparator.getLayoutData()).horizontalSpan =
                // 5;
                break;

            default:
                data = new GridData(GridData.FILL_VERTICAL);
                data.widthHint = size;

                break;

        }

        trayControl.setLayoutData(data);
        trayControl.addControlListener(new ControlListener()
        {
            
            @Override
            public void controlResized(ControlEvent e)
            {
                if(trayControl==null || trayControl.isDisposed())
                    return;
                Rectangle bounds = trayControl.getBounds();
                switch (location)
                {
                    case TOP:
                    case BOTTOM:
                        sizeCache = bounds.height;
                        break;

                    default:
                        sizeCache =bounds.width;

                        break;

                }
                
            }
            
            @Override
            public void controlMoved(ControlEvent e)
            {
                // TODO Auto-generated method stub
                
            }
        });

        Rectangle bounds = this.getBounds();

        switch (location)
        {
            case TOP:
                base.moveBelow(sash);
                trayControl.moveAbove(sash);
            case BOTTOM:
                this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height + +size + 10);
                Rectangle rectangle = Display.getCurrent().getBounds();
                if ((this.getBounds().y + this.getBounds().height) > rectangle.height)
                {
                    int y = this.getBounds().y - ((this.getBounds().y + this.getBounds().height) - rectangle.height);
                    if (y < 0)
                    {
                        y = 0;
                    }

                    this.setBounds(this.getBounds().x, y, this.getBounds().width, this.getBounds().height);
                }

                break;
            case LEFT:
                base.moveBelow(sash);
                trayControl.moveAbove(sash);
                this.setBounds(bounds.x, bounds.y, bounds.width + size + 10, bounds.height);
                break;
            default:

                this.setBounds(bounds.x, bounds.y, bounds.width + size + 10, bounds.height);

                break;

        }

        sash.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                if (event.detail != SWT.DRAG)
                {
                    Rectangle clientArea = getClientArea();

                    switch (location)
                    {
                        case TOP:
                        {
                            int newHeight = clientArea.height - event.y - (sash.getSize().y );
                            newHeight = clientArea.height - (newHeight + 20);
                            if (newHeight != data.heightHint)
                            {
                                data.heightHint = newHeight;
                                layout();
                            }
                        }
                            break;
                        case BOTTOM:
                        {
                            int newHeight = clientArea.height - event.y - (sash.getSize().y );
                            if (newHeight != data.heightHint)
                            {
                                data.heightHint = newHeight;
                                layout();
                            }
                        }
                            break;

                        default:
                            int newWidth = clientArea.width - event.x - (sash.getSize().x );
                            if (newWidth != data.widthHint)
                            {
                                data.widthHint = newWidth;
                                layout();
                            }
                    }

                }
            }
        });

        this.tray = tray;
        getParent().layout(true);
    }

}
