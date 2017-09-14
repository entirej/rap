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
package org.entirej.applicationframework.rwt.application.components.menu;

import java.io.Serializable;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;
import org.entirej.applicationframework.rwt.application.components.menu.EJRWTMenuTreeElement.Type;
import org.entirej.framework.core.EJActionProcessorException;
import org.entirej.framework.core.EJApplicationException;
import org.entirej.framework.core.EJMessageFactory;
import org.entirej.framework.core.actionprocessor.interfaces.EJMenuActionProcessor;
import org.entirej.framework.core.enumerations.EJFrameworkMessage;
import org.entirej.framework.core.interfaces.EJApplicationManager;

public class EJRWTDefaultMenuBuilder implements Serializable
{
    public static final String   COMMAND_MESSAGE_HISTORY = "_COMMAND_MESSAGE_HISTORY";
    public static final String   COMMAND_ABOUT_DIALOG    = "COMMAND_MESSAGE_HISTORY";

    private TreeViewer           _menuTree;
    private EJApplicationManager _applicationManager;
    private Composite            _parent;

    public EJRWTDefaultMenuBuilder(EJApplicationManager appManager, Composite parent)
    {
        this._applicationManager = appManager;
        this._parent = parent;
    }

    public static void createApplicationMenu(EJApplicationManager applicationManager, Shell shell, EJRWTMenuTreeRoot root)
    {
        EJMenuActionProcessor actionProcessor = null;
        if (root.getActionProcessorClassName() != null && root.getActionProcessorClassName().length() > 0)
        {
            try
            {
                Class<?> processorClass = Class.forName(root.getActionProcessorClassName());
                try
                {
                    Object processorObject = processorClass.newInstance();
                    if (processorObject instanceof EJMenuActionProcessor)
                    {
                        actionProcessor = (EJMenuActionProcessor) processorObject;
                    }
                    else
                    {
                        throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_NAME,
                                processorClass.getName(), "EJMenuActionProcessor"));
                    }
                }
                catch (InstantiationException e)
                {
                    throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                            processorClass.getName()), e);
                }
                catch (IllegalAccessException e)
                {
                    throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                            processorClass.getName()), e);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_FOR_MENU,
                        root.getActionProcessorClassName()));
            }
        }
        Menu appMenuBar = shell.getDisplay().getMenuBar();
        if (appMenuBar == null)
        {
            appMenuBar = new Menu(shell, SWT.BAR);
            shell.setMenuBar(appMenuBar);
        }
        createMenu(applicationManager, appMenuBar, root, actionProcessor);

    }

    public static void createMenu(final EJApplicationManager applicationManager, Menu parent, EJRWTMenuTreeElement root,
            final EJMenuActionProcessor menuActionProcessor)
    {
        List<EJRWTMenuTreeElement> treeElements = root.getTreeElements();
        for (final EJRWTMenuTreeElement treeElement : treeElements)
        {
            switch (treeElement.getType())
            {
                case ACTION:
                {
                    MenuItem action = new MenuItem(parent, SWT.PUSH);
                    action.setText(treeElement.getText());
                    action.setImage(treeElement.getImage());
                    if (menuActionProcessor != null)
                    {
                        action.addSelectionListener(new SelectionAdapter()
                        {
                            @Override
                            public void widgetSelected(SelectionEvent evnt)
                            {
                                try
                                {
                                    menuActionProcessor.executeActionCommand(treeElement.getActionCommand());
                                }
                                catch (EJActionProcessorException e)
                                {
                                    applicationManager.getApplicationMessenger().handleException(e, true);
                                }
                            }
                        });
                    }
                    break;
                }
                case FORM:
                {
                    MenuItem form = new MenuItem(parent, SWT.PUSH);
                    form.setText(treeElement.getText());
                    form.setImage(treeElement.getImage());
                    form.addSelectionListener(new SelectionAdapter()
                    {
                        @Override
                        public void widgetSelected(SelectionEvent e)
                        {
                            applicationManager.getFrameworkManager().openForm(treeElement.getActionCommand(), null, false);
                        }
                    });
                    break;
                }
                case SEPARATOR:
                {
                    new MenuItem(parent, SWT.SEPARATOR);
                    break;
                }
                case SUB:
                {
                    MenuItem sub = new MenuItem(parent, SWT.CASCADE);
                    sub.setText(treeElement.getText());
                    sub.setImage(treeElement.getImage());
                    Menu subMenu = new Menu(parent);
                    sub.setMenu(subMenu);
                    createMenu(applicationManager, subMenu, treeElement, menuActionProcessor);

                    break;
                }
            }
        }
    }

    private TreeViewer createMenuTree(EJRWTMenuTreeRoot root, boolean tselectionMode)
    {
        _menuTree = new TreeViewer(_parent);

        _menuTree.setContentProvider(new EJRWTMenuTreeContentProvider());
        _menuTree.setLabelProvider(new LabelProvider()
        {
            @Override
            public String getText(Object element)
            {
                if (element instanceof EJRWTMenuTreeElement)
                {
                    return ((EJRWTMenuTreeElement) element).getText();
                }
                return "<EMPTY>";
            }

            @Override
            public Image getImage(Object element)
            {
                if (element instanceof EJRWTMenuTreeElement)
                {
                    return ((EJRWTMenuTreeElement) element).getImage();
                }
                return super.getImage(element);
            }

        });
        _menuTree.setAutoExpandLevel(2);
        _menuTree.setInput(root);

        EJMenuActionProcessor actionProcessor = null;
        if (root.getActionProcessorClassName() != null && root.getActionProcessorClassName().length() > 0)
        {
            try
            {
                Class<?> processorClass = Class.forName(root.getActionProcessorClassName());
                try
                {
                    Object processorObject = processorClass.newInstance();
                    if (processorObject instanceof EJMenuActionProcessor)
                    {
                        actionProcessor = (EJMenuActionProcessor) processorObject;
                    }
                    else
                    {
                        throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_NAME,
                                processorClass.getName(), "EJMenuActionProcessor"));
                    }
                }
                catch (InstantiationException e)
                {
                    throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                            processorClass.getName()), e);
                }
                catch (IllegalAccessException e)
                {
                    throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.UNABLE_TO_CREATE_ACTION_PROCESSOR,
                            processorClass.getName()), e);
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new EJApplicationException(EJMessageFactory.getInstance().createMessage(EJFrameworkMessage.INVALID_ACTION_PROCESSOR_FOR_MENU,
                        root.getActionProcessorClassName()));
            }
        }
        final EJMenuActionProcessor menuActionProcessor = actionProcessor;

        if (tselectionMode)
        {
            _menuTree.getTree().addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseUp(MouseEvent event)
                {
                    ISelection selection = _menuTree.getSelection();
                    if (selection instanceof IStructuredSelection)
                    {
                        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                        if (structuredSelection.getFirstElement() instanceof EJRWTMenuTreeElement)
                        {
                            EJRWTMenuTreeElement element = (EJRWTMenuTreeElement) structuredSelection.getFirstElement();
                            if (element.getType() == Type.FORM)
                            {
                                _applicationManager.getFrameworkManager().openForm(element.getActionCommand(), null, false);
                            }
                            else if (element.getType() == Type.ACTION && menuActionProcessor != null)
                            {
                                try
                                {
                                    menuActionProcessor.executeActionCommand(element.getActionCommand());
                                }
                                catch (EJActionProcessorException e)
                                {
                                    _applicationManager.getApplicationMessenger().handleException(e, true);
                                }
                            }

                        }
                    }
                }
            });

        }
        _menuTree.getTree().addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0)
            {
                ISelection selection = _menuTree.getSelection();
                if (selection instanceof IStructuredSelection)
                {
                    final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    if (structuredSelection.getFirstElement() instanceof EJRWTMenuTreeElement)
                    {

                        Display.getCurrent().asyncExec(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                try
                                {
                                    EJRWTMenuTreeElement element = (EJRWTMenuTreeElement) structuredSelection.getFirstElement();
                                    if (element.getType() == Type.FORM)
                                    {
                                        _applicationManager.getFrameworkManager().openForm(element.getActionCommand(), null, false);
                                    }
                                    else if (element.getType() == Type.ACTION && menuActionProcessor != null)
                                    {
                                        try
                                        {
                                            menuActionProcessor.executeActionCommand(element.getActionCommand());
                                        }
                                        catch (EJActionProcessorException e)
                                        {
                                            _applicationManager.getApplicationMessenger().handleException(e, true);
                                        }
                                    }
                                }
                                catch (EJApplicationException e)
                                {
                                    _applicationManager.handleException(e);
                                }

                            }
                        });

                    }
                }
            }

        });
        return _menuTree;
    }

    public Control createTreeComponent(String menuId)
    {
        EJRWTMenuTreeRoot root = EJRWTDefaultMenuPropertiesBuilder.buildMenuProperties(_applicationManager, menuId);

        _menuTree = createMenuTree(root, false);
        return _menuTree.getControl();
    }

    public Control createTreeComponent(String menuId, boolean tselectionMode)
    {
        EJRWTMenuTreeRoot root = EJRWTDefaultMenuPropertiesBuilder.buildMenuProperties(_applicationManager, menuId);

        _menuTree = createMenuTree(root, tselectionMode);
        return _menuTree.getControl();
    }

}
