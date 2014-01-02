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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

public class EJRWTTreeTableViewerColumnFactory
{
    private final TreeViewer _viewer;

    public EJRWTTreeTableViewerColumnFactory(TreeViewer viewer)
    {
        super();
        this._viewer = viewer;
    }

    public TreeViewerColumn createColumn(String header, int width, ColumnLabelProvider provider)
    {
        return createColumn(header, width, provider, SWT.LEFT, null);

    }

    public TreeViewerColumn createColumn(String header, int width, ColumnLabelProvider provider, EJRWTAbstractTableSorter sorter)
    {
        return createColumn(header, width, provider, SWT.LEFT, sorter);

    }

    public TreeViewerColumn createColumn(String header, int width, ColumnLabelProvider provider, int alignment)
    {
        return createColumn(header, width, provider, alignment, null);

    }

    public TreeViewerColumn createColumn(String header, int width, ColumnLabelProvider provider, int alignment, EJRWTAbstractTableSorter sorter)
    {
        final TreeViewerColumn viewerColumn = new TreeViewerColumn(_viewer, SWT.NONE);
        final TreeColumn column = viewerColumn.getColumn();
        column.setText(header == null ? "" : header);

        if (width > 0)
        {
            column.setWidth(width);
        }
        column.setResizable(true);
        column.setMoveable(true);
        column.setAlignment(alignment);
        viewerColumn.setLabelProvider(provider);
        if (sorter != null)
        {
            new EJRWTTreeTableSortSelectionListener(_viewer, column, sorter, SWT.UP, false);
        }

        return viewerColumn;
    }
}
