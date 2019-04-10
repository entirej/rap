package org.entirej.applicationframework.rwt.renderers.item;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.entirej.applicationframework.rwt.application.EJRWTApplicationManager;
import org.entirej.applicationframework.rwt.renderer.interfaces.EJRWTAppItemRenderer;
import org.entirej.applicationframework.rwt.table.EJRWTAbstractTableSorter;
import org.entirej.framework.core.EJMessage;
import org.entirej.framework.core.interfaces.EJScreenItemController;
import org.entirej.framework.core.properties.EJCoreVisualAttributeProperties;
import org.entirej.framework.core.properties.interfaces.EJScreenItemProperties;

public class EJRWTHelpItemRenderer implements EJRWTAppItemRenderer, Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private EJScreenItemProperties          screenItemProperties;
    private EJScreenItemController          item;
    protected ToolItem                      _button;
    private EJCoreVisualAttributeProperties va;
    private String                          itemName;
    private ToolBar component;
    private EJRWTApplicationManager _applicationManager;

    @Override
    public void refreshItemRenderer()
    {

    }

    @Override
    public void refreshItemRendererProperty(String propertyName)
    {
    }

    @Override
    public void initialise(EJScreenItemController item, EJScreenItemProperties screenItemProperties)
    {
        this.item = item;
        _applicationManager = (EJRWTApplicationManager) item.getForm().getFrameworkManager().getApplicationManager();
        this.screenItemProperties = screenItemProperties;

    }

    @Override
    public EJScreenItemController getItem()
    {
        return item;
    }

    @Override
    public void clearValue()
    {

    }

    @Override
    public Object getValue()
    {
        return null;
    }

    @Override
    public List<Object> getValidValues()
    {
        return null;
    }

    @Override
    public String getDisplayValue()
    {
        return null;
    }

    @Override
    public void setInitialValue(Object value)
    {

    }

    @Override
    public void setValue(Object value)
    {

    }

    @Override
    public void setMessage(EJMessage message)
    {
    }

    @Override
    public void clearMessage()
    {

    }

    @Override
    public void validationErrorOccurred(boolean error)
    {

    }

    @Override
    public boolean valueEqualsTo(Object value)
    {

        return false;
    }

    @Override
    public void setEditAllowed(boolean editAllowed)
    {

    }

    @Override
    public boolean isEditAllowed()
    {
        return false;
    }

    @Override
    public void setMandatory(boolean mandatory)
    {

    }

    @Override
    public void enableLovActivation(boolean enable)
    {

    }

    @Override
    public void setVisible(boolean visible)
    {
        if (controlState(_button))
        {
            _button.getControl().setVisible(visible);
        }

    }

    protected boolean controlState(ToolItem control)
    {
        return control != null && _button.getControl()!=null && !control.isDisposed();

    }

    @Override
    public void setVisualAttribute(EJCoreVisualAttributeProperties visualAttributeProperties)
    {
        this.va = visualAttributeProperties;

    }

    @Override
    public EJCoreVisualAttributeProperties getVisualAttributeProperties()
    {
        return this.va;
    }

    @Override
    public boolean isVisible()
    {
        if (controlState(_button))
        {
            return _button.getControl().isVisible();
        }

        return false;
    }

    @Override
    public boolean isMandatory()
    {
        return false;
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

    @Override
    public void gainFocus()
    {

    }

    @Override
    public void setRegisteredItemName(String name)
    {
        this.itemName = name;

    }

    @Override
    public String getRegisteredItemName()
    {
        return itemName;
    }

    @Override
    public void setLabel(String label)
    {

    }

    @Override
    public void setHint(String hint)
    {

    }

    @Override
    public boolean isReadOnly()
    {
        return true;
    }

    @Override
    public Control getGuiComponent()
    {
        return component;
    }

    @Override
    public Label getGuiComponentLabel()
    {
        return null;
    }

    @Override
    public void createComponent(Composite composite)
    {
        this.component = createHelpImageButton(composite, JFaceResources.getImage("dialog_help_image"));

    }

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
        
        if(_applicationManager.isHelpSupported())
        {
            _button = new ToolItem(toolBar, SWT.CHECK);
            _button.setImage(image);
           
            _button.setSelection(isHelpActive());
            _button.setToolTipText(JFaceResources.getString("helpToolTip")); //$NON-NLS-1$
            _button.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    helpPressed(_button.getSelection());
                }
    
                private void helpPressed(boolean selection)
                {
                    _applicationManager.setHelpActive(selection);
                    
                }
            });
        }
        return toolBar;
    }

    private boolean isHelpActive()
    {
        return _applicationManager.isHelpActive();
    }

    @Override
    public void createLable(Composite composite)
    {
        

    }

    @Override
    public boolean useFontDimensions()
    {
        return false;
    }

    @Override
    public void setInitialVisualAttribute(EJCoreVisualAttributeProperties va)
    {

    }

    @Override
    public ColumnLabelProvider createColumnLabelProvider(EJScreenItemProperties item, EJScreenItemController controller)
    {
        return null;
    }

    @Override
    public EJRWTAbstractTableSorter getColumnSorter(EJScreenItemProperties item, EJScreenItemController controller)
    {
        return null;
    }

    @Override
    public String formatValue(Object obj)
    {
        return null;
    }

}
