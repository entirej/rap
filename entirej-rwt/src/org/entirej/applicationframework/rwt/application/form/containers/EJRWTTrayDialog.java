package org.entirej.applicationframework.rwt.application.form.containers;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class EJRWTTrayDialog extends Dialog
{

    public enum TrayLocation
    {
        RIGHT, LEFT, TOP, BOTTOM
    }

    public static final int RIGHT_TO_LEFT = 1 << 26;

    private Control base;

    private final class ResizeListener extends ControlAdapter
    {

        private final GridData data;
        private final Shell    shell;
        private final int      TRAY_RATIO = 100; // Percentage of extra width
                                                 // devoted to tray when
                                                 // resizing
        private int            remainder  = 0;  // Used to prevent rounding
                                                 // errors from accumulating

        private ResizeListener(GridData data, Shell shell)
        {
            this.data = data;
            this.shell = shell;
        }

        
      
        
        public void controlResized(ControlEvent event)
        {
            int newWidth = shell.getSize().x;
            if (newWidth != shellWidth)
            {
                int shellWidthIncrease = newWidth - shellWidth;
                int trayWidthIncreaseTimes100 = (shellWidthIncrease * TRAY_RATIO) + remainder;
                int trayWidthIncrease = trayWidthIncreaseTimes100 / 100;
                remainder = trayWidthIncreaseTimes100 - (100 * trayWidthIncrease);
                data.widthHint = data.widthHint + trayWidthIncrease;
                shellWidth = newWidth;
                if (!shell.isDisposed())
                {
                    shell.layout();
                }
            }
        }
    }

    
    @Override
    public void create()
    {
        super.create();
        
        base = getShell().getChildren()[0];
    }
    
    protected TrayLocation  location      = TrayLocation.RIGHT;

   
    /**
     * The dialog's tray (null if none).
     */
    private EJRWTDialogTray tray;

    /**
     * The tray's control (null if none).
     */
    private Control         trayControl;

    /**
     * The control that had focus before the tray was opened (null if none).
     */
    private Control         nonTrayFocusControl;



    /*
     * The sash that allows the user to resize the tray.
     */
    private Sash            sash;

    /*
     * Whether or not help is available for this dialog.
     */
    private boolean         helpAvailable = isDialogHelpAvailable();

    private int             shellWidth;

    private ControlAdapter  resizeListener;

    /**
     * The help button (null if none).
     */
    private ToolItem        fHelpButton;

    /**
     * Creates a tray dialog instance. Note that the window will have no visual
     * representation (no widgets) until it is told to open.
     * 
     * @param shell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     */
    protected EJRWTTrayDialog(Shell shell)
    {
        super(shell);
    }

    /**
     * Creates a tray dialog with the given parent.
     * 
     * @param parentShell
     *            the object that returns the current parent shell
     */
    protected EJRWTTrayDialog(IShellProvider parentShell)
    {
        super(parentShell);
    }

    /**
     * Closes this dialog's tray, disposing its widgets.
     * 
     * @throws IllegalStateException
     *             if the tray was not open
     */
    public void closeTray() throws IllegalStateException
    {
        if (getTray() == null)
        {
           // throw new IllegalStateException("Tray was not open"); //$NON-NLS-1$
            return;
        }
        Shell shell = getShell();
        Control focusControl = shell.getDisplay().getFocusControl();
        if (focusControl != null && isContained(trayControl, focusControl))
        {
            if (nonTrayFocusControl != null && !nonTrayFocusControl.isDisposed())
            {
                nonTrayFocusControl.setFocus();
            }
            else
            {
                shell.setFocus();
            }
        }
        nonTrayFocusControl = null;
        if (resizeListener != null)
            shell.removeControlListener(resizeListener);
        resizeListener = null;
        int trayWidth = trayControl.getSize().x  + sash.getSize().x ;
        int trayHeight = trayControl.getSize().y  + sash.getSize().y ;
        trayControl.dispose();
        trayControl = null;
        tray = null;

        sash.dispose();
        sash = null;
        Rectangle bounds = shell.getBounds();
        switch (location)
        {
            case BOTTOM:
            case TOP:

                shell.setBounds(bounds.x , bounds.y, bounds.width , bounds.height- trayHeight);
                
                break;

            default:

                shell.setBounds(bounds.x , bounds.y, bounds.width - trayWidth, bounds.height);
                break;
        }
        if (fHelpButton != null)
        {
            fHelpButton.setSelection(false);
        }
    }

    /**
     * Returns true if the given Control is a direct or indirect child of
     * container.
     * 
     * @param container
     *            the potential parent
     * @param control
     * @return boolean <code>true</code> if control is a child of container
     */
    private boolean isContained(Control container, Control control)
    {
        Composite parent;
        while ((parent = control.getParent()) != null)
        {
            if (parent == container)
            {
                return true;
            }
            control = parent;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#handleShellCloseEvent()
     */
    public void handleShellCloseEvent()
    {
        /*
         * Close the tray to ensure that those dialogs that remember their size
         * do not store the tray size.
         */
        if (getTray() != null)
        {
            closeTray();
        }

        super.handleShellCloseEvent();
    }
    
    

   

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets
     * .Composite)
     */
    protected Control createButtonBar(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        composite.setFont(parent.getFont());

        // create help control if needed
        if (isHelpAvailable())
        {
            Control helpControl = createHelpControl(composite);
            ((GridData) helpControl.getLayoutData()).horizontalIndent = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        }
        Control buttonSection = super.createButtonBar(composite);
        ((GridData) buttonSection.getLayoutData()).grabExcessHorizontalSpace = true;
        return composite;
    }

    /**
     * Creates a new help control that provides access to context help.
     * <p>
     * The <code>TrayDialog</code> implementation of this method creates the
     * control, registers it for selection events including selection, Note that
     * the parent's layout is assumed to be a <code>GridLayout</code> and the
     * number of columns in this layout is incremented. Subclasses may override.
     * </p>
     * 
     * @param parent
     *            the parent composite
     * @return the help control
     */
    protected Control createHelpControl(Composite parent)
    {
        Image helpImage = JFaceResources.getImage(DLG_IMG_HELP);
        if (helpImage != null)
        {
            return createHelpImageButton(parent, helpImage);
        }
        return createHelpLink(parent);
    }

    /*
     * Creates a button with a help image. This is only used if there is an
     * image available.
     */
    private ToolBar createHelpImageButton(Composite parent, Image image)
    {
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.NO_FOCUS);
        ((GridLayout) parent.getLayout()).numColumns++;
        toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        final Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
        toolBar.setCursor(cursor);
        toolBar.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent e)
            {
                cursor.dispose();
            }
        });
        fHelpButton = new ToolItem(toolBar, SWT.CHECK);
        fHelpButton.setImage(image);
        fHelpButton.setSelection(isHelpActive());
        fHelpButton.setToolTipText(JFaceResources.getString("helpToolTip")); //$NON-NLS-1$
        fHelpButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                helpPressed(fHelpButton.getSelection());
            }
        });
        return toolBar;
    }

    protected boolean isHelpActive()
    {
        return false;
    }

    /*
     * Creates a help link. This is used when there is no help image available.
     */
    private Link createHelpLink(Composite parent)
    {
        Link link = new Link(parent, SWT.WRAP | SWT.NO_FOCUS);
        ((GridLayout) parent.getLayout()).numColumns++;
        link.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        link.setText("<a>" + IDialogConstants.HELP_LABEL + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        link.setToolTipText(IDialogConstants.HELP_LABEL);
        link.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                helpPressed(fHelpButton.getSelection());
            }
        });
        return link;
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
            return !grid.makeColumnsEqualWidth && (grid.horizontalSpacing == 0) && (grid.marginWidth == 0) && (grid.marginHeight == 0)
                    ;
        }
        return false;
    }

    /**
     * Returns whether or not context help is available for this dialog. This
     * can affect whether or not the dialog will display additional help
     * mechanisms such as a help control in the button bar.
     * 
     * @return whether or not context help is available for this dialog
     */
    public boolean isHelpAvailable()
    {
        return helpAvailable;
    }

    /**
     * The tray dialog's default layout is a modified version of the default
     * <code>Window</code> layout that can accomodate a tray, however it still
     * conforms to the description of the <code>Window</code> default layout.
     * <p>
     * Note: Trays may not be supported with all custom layouts on the dialog's
     * Shell. To avoid problems, use a single outer <code>Composite</code> for
     * your dialog area, and set your custom layout on that
     * <code>Composite</code>.
     * </p>
     * 
     * @see org.eclipse.jface.window.Window#getLayout()
     * @return a newly created layout or <code>null</code> for no layout
     */
    protected Layout getLayout()
    {
        GridLayout layout = (GridLayout) super.getLayout();
        layout.numColumns = 5;
        layout.horizontalSpacing = 0;
        return layout;
    }

    /**
     * Returns the tray currently shown in the dialog, or <code>null</code> if
     * there is no tray.
     * 
     * @return the dialog's current tray, or <code>null</code> if there is none
     */
    public EJRWTDialogTray getTray()
    {
        return tray;
    }

    
    protected void helpPressed(boolean active)
    {
        
    }

    /**
     * Constructs the tray's widgets and displays the tray in this dialog. The
     * dialog's size will be adjusted to accommodate the tray.
     * 
     * @param tray
     *            the tray to show in this dialog
     * @throws IllegalStateException
     *             if the dialog already has a tray open
     * @throws UnsupportedOperationException
     *             if the dialog does not support trays, for example if it uses
     *             a custom layout.
     */
    public void openTray(final TrayLocation location, EJRWTDialogTray tray, int size) throws IllegalStateException, UnsupportedOperationException
    {
        if (tray == null)
        {
            throw new NullPointerException("Tray was null"); //$NON-NLS-1$
        }
        if (getTray() != null)
        {
            throw new IllegalStateException("Tray was already open"); //$NON-NLS-1$
        }
        if (!isCompatibleLayout(getShell().getLayout()))
        {
            throw new UnsupportedOperationException("Trays not supported with custom layouts"); //$NON-NLS-1$
        }
        this.location = location;
        final Shell shell = getShell();
        
        Control focusControl = shell.getDisplay().getFocusControl();
        if (focusControl != null && isContained(shell, focusControl))
        {
            nonTrayFocusControl = focusControl;
        }
        
        switch (location)
        {
            case BOTTOM:
            case TOP:
     
                sash = new Sash(shell, SWT.HORIZONTAL);
                sash.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                break;

            default:
                sash = new Sash(shell, SWT.VERTICAL);
                sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
                break;
        }

        
        trayControl = tray.createContents(shell);
        final GridData data;
        switch (location)
        {
            case TOP:
            case BOTTOM:
                data = new GridData(GridData.FILL_HORIZONTAL);
                data.heightHint = size;
                
                GridLayout grid = (GridLayout) getShell().getLayout();
                grid.numColumns = 1;
//                data.horizontalSpan = 5;
//                ((GridData)leftSeparator.getLayoutData()).horizontalSpan = 5;
//                ((GridData)sash.getLayoutData()).horizontalSpan = 5;
//                ((GridData)rightSeparator.getLayoutData()).horizontalSpan = 5;
//                ((GridData)rightSeparator.getLayoutData()).horizontalSpan = 5;
                break;

            default:
                data = new GridData(GridData.FILL_VERTICAL);
                data.widthHint = size;
               
                break;

        }

        trayControl.setLayoutData(data);
        
        Rectangle bounds = shell.getBounds();
       
        switch (location)
        {
            case TOP:
                base.moveBelow(sash);
                trayControl.moveAbove(sash);
            case BOTTOM:
                shell.setBounds(bounds.x , bounds.y, bounds.width , bounds.height+  + size+10);
               Rectangle rectangle = Display.getCurrent().getBounds();
               if((shell.getBounds().y+shell.getBounds().height)>rectangle.height)
               {
                   int  y = shell.getBounds().y - ((shell.getBounds().y+shell.getBounds().height) - rectangle.height);
                   if(y<0)
                   {
                       y = 0;
                   }
                   
                   shell.setBounds(shell.getBounds().x , y, shell.getBounds().width , shell.getBounds().height);
               }

                break;
            case LEFT:
                base.moveBelow(sash);
                trayControl.moveAbove(sash);
                shell.setBounds(bounds.x, bounds.y, bounds.width + size+10, bounds.height);
                break;
            default:
                
                shell.setBounds(bounds.x, bounds.y, bounds.width + size+10, bounds.height);

                break;

        }
        
                sash.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                if (event.detail != SWT.DRAG)
                {
                    Rectangle clientArea = shell.getClientArea();
                    
                    switch (location)
                    {
                        case TOP:
                        {
                            int newHeight = clientArea.height - event.y - (sash.getSize().y );
                            newHeight = clientArea.height - (newHeight+20);
                            if (newHeight != data.heightHint)
                            {
                                data.heightHint = newHeight;
                                shell.layout();
                            }
                        }
                        break;
                        case BOTTOM:
                        {
                            int newHeight = clientArea.height - event.y - (sash.getSize().y );
                            if (newHeight != data.heightHint)
                            {
                                data.heightHint = newHeight;
                                shell.layout();
                            }
                        }
                        break;
                        
                        default:
                            int newWidth = clientArea.width - event.x - (sash.getSize().x );
                            if (newWidth != data.widthHint)
                            {
                                data.widthHint = newWidth;
                                shell.layout();
                            }   
                    }
                    
                   
                }
            }
        });
        
        
        
        
        
        
       // shellWidth = shell.getSize().x;

        // resizeListener = new ResizeListener(data, shell);
        // shell.addControlListener(resizeListener);

        this.tray = tray;
    }

    /**
     * Sets whether or not context help is available for this dialog. This can
     * affect whether or not the dialog will display additional help mechanisms
     * such as a help control in the button bar.
     * 
     * @param helpAvailable
     *            whether or not context help is available for the dialog
     */
    public void setHelpAvailable(boolean helpAvailable)
    {
        this.helpAvailable = helpAvailable;
    }

    /**
     * Tests if dialogs that have help control should show it all the time or
     * only when explicitly requested for each dialog instance.
     * 
     * @return <code>true</code> if dialogs that support help control should
     *         show it by default, <code>false</code> otherwise.
     * @since 3.2
     */
    public  boolean isDialogHelpAvailable()
    {
        return false;
    }

    
}
