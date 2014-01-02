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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class EJRWTTableAutoResizeAdapter extends ControlAdapter
{
    private Table _table  = null;
    private int[] _widths = null;

    public EJRWTTableAutoResizeAdapter(Table table)
    {
        this._table = table;
        _widths = getWidth(table);
    }

    public static int[] getWidth(Table table)
    {
        int[] widths = null;
        TableColumn[] columns = table.getColumns();
        widths = new int[columns.length];
        for (int i = 0; i < columns.length; i++)
        {
            widths[i] = columns[i].getWidth();
        }
        return widths;
    }

    @Override
    public void controlResized(ControlEvent e)
    {
        layout(_table, _widths);
    }

    public static void layout(Table table, int[] widths)
    {
        table.setRedraw(false);
        try
        {
            Composite inComposite = table.getParent();
            Rectangle area = inComposite.getClientArea();
            Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            int width = area.width; // - 2 * table.getBorderWidth();
            if (preferredSize.y > area.height + table.getHeaderHeight())
            {
                // Subtract the scrollbar width from the total column width
                // if a vertical scrollbar will be required
                Point vBarSize = table.getVerticalBar().getSize();
                width -= vBarSize.x;
            }
            Point oldSize = table.getSize();
            if (oldSize.x > area.width)
            {
                // table is getting smaller so make the columns
                // smaller first and then resize the table to
                // match the client area width
                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    table.getColumn(i).setWidth((int) ((double) widths[i] / 100 * width));
                }
                table.setSize(area.width, area.height);
            }
            else
            {
                // table is getting bigger so make the table
                // bigger first and then make the columns wider
                // to match the client area width
                table.setSize(area.width, area.height);
                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    table.getColumn(i).setWidth((int) ((double) widths[i] / 100 * width));
                }
            }
        }
        finally
        {
            table.setRedraw(true);
        }
    }

}
