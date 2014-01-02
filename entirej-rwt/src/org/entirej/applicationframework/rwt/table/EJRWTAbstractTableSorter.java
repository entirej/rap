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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.entirej.applicationframework.rwt.table.EJRWTTableSortSelectionListener.InvertableSorter;

public abstract class EJRWTAbstractTableSorter extends InvertableSorter
{
    private final InvertableSorter _inverse = new InvertableSorter()
                                           {
                                               @Override
                                               public int compare(Viewer viewer, Object e1, Object e2)
                                               {
                                                   return -1 * EJRWTAbstractTableSorter.this.compare(viewer, e1, e2);
                                               }

                                               @Override
                                               InvertableSorter getInverseSorter()
                                               {
                                                   return EJRWTAbstractTableSorter.this;
                                               }

                                               @Override
                                               public int getSortDirection()
                                               {
                                                   return SWT.DOWN;
                                               }
                                           };

    @Override
    InvertableSorter getInverseSorter()
    {
        return _inverse;
    }

    @Override
    public int getSortDirection()
    {
        return SWT.UP;
    }
}
