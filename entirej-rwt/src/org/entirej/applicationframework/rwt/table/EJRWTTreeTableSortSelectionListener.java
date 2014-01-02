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
package org.entirej.applicationframework.rwt.table;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeColumn;
import org.entirej.applicationframework.rwt.table.EJRWTTableSortSelectionListener.InvertableSorter;

public class EJRWTTreeTableSortSelectionListener implements SelectionListener
{
    private final TreeViewer       _viewer;
    private final TreeColumn       _column;
    private final InvertableSorter _sorter;
    private final boolean          _keepDirection;
    private InvertableSorter       _currentSorter;

    /**
     * The constructor of this listener.
     * 
     * @param viewer
     *            the tableviewer this listener belongs to
     * @param column
     *            the column this listener is responsible for
     * @param sorter
     *            the sorter this listener uses
     * @param defaultDirection
     *            the default sorting direction of this Listener. Possible
     *            values are {@link SWT.UP} and {@link SWT.DOWN}
     * @param keepDirection
     *            if true, the listener will remember the last sorting direction
     *            of the associated column and restore it when the column is
     *            reselected. If false, the listener will use the default soting
     *            direction
     */
    public EJRWTTreeTableSortSelectionListener(TreeViewer viewer, TreeColumn column, EJRWTAbstractTableSorter sorter, int defaultDirection,
            boolean keepDirection)
    {
        _viewer = viewer;
        _column = column;
        _keepDirection = keepDirection;
        _sorter = defaultDirection == SWT.UP ? sorter : sorter.getInverseSorter();
        _currentSorter = _sorter;

        _column.addSelectionListener(this);
    }

    /**
     * Chooses the column of this listener for sorting of the table. Mainly used
     * when first initializing the table.
     */
    public void chooseColumnForSorting()
    {
        _viewer.getTree().setSortColumn(_column);
        _viewer.getTree().setSortDirection(_currentSorter.getSortDirection());
        _viewer.setSorter(_currentSorter);
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        InvertableSorter newSorter;
        if (_viewer.getTree().getSortColumn() == _column)
        {
            newSorter = ((InvertableSorter) _viewer.getSorter()).getInverseSorter();
        }
        else
        {
            if (_keepDirection)
            {
                newSorter = _currentSorter;
            }
            else
            {
                newSorter = _sorter;
            }
        }
        _currentSorter = newSorter;
        chooseColumnForSorting();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
        widgetSelected(e);
    }
}
