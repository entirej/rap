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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.entirej.applicationframework.rwt.application.EJRWTImageRetriever;

public class EJRWTMenuTreeElement implements Serializable
{
    private String                     _text          = null;
    private String                     _tooltip       = null;
    private String                     _actionCommand = null;
    private List<EJRWTMenuTreeElement> _treeElements  = new LinkedList<EJRWTMenuTreeElement>();
    private Type                       _type;
    private String                     _image;

    public enum Type
    {
        FORM, SUB, SEPARATOR, ACTION
    }

    public EJRWTMenuTreeElement()
    {

    }

    /**
     * Constructs a tree element with a text and a command
     * 
     * @param text
     *            - The text of the tree element
     * @param tooltip
     *            - The tool tip
     */
    protected EJRWTMenuTreeElement(final String text, final String tooltip)
    {
        _text = text;
        _tooltip = tooltip;
    }

    /**
     * Returns the text of the tree element
     * 
     * @return a String representing the tree element text
     */
    public String getText()
    {
        return _text==null? "":_text;
    }

    public String getTooltip()
    {
        return _tooltip;
    }

    /**
     * Returns the action command of the tree element
     * 
     * @return a String representing the action command
     */
    public String getActionCommand()
    {
        return _actionCommand;
    }

    public Image getImage()
    {
        if (_image != null)
        {
            return EJRWTImageRetriever.get(_image);
        }
        return null;
    }

    public void setImage(String image)
    {
        _image = image;
    }

    /**
     * Returns if this element is the parent for other tree element
     * 
     * @return true if the element contains other tree elements, otherwise false
     */
    public boolean isParentTreeElement()
    {
        return getTreeElementCount() < 0;
    }

    public EJRWTMenuTreeElement getParentTreeElement()
    {
        return isParentTreeElement() ? _treeElements.get(0) : null;
    }

    /**
     * Adds a tree element to this element.
     * 
     * @param text
     *            - The text of the added tree element
     * @param tooltip
     *            - The tooltip of the added tree element
     * @return the new added tree element instance
     */
    public EJRWTMenuTreeElement addSubTreeElement(String text, String tooltip, String image)
    {
        EJRWTMenuTreeElement treeElement = new EJRWTMenuTreeElement(text, tooltip);
        treeElement._type = Type.SUB;
        treeElement._image = image;
        _treeElements.add(treeElement);
        return treeElement;
    }

    public EJRWTMenuTreeElement addFormMenuItem(String text, String tooltip, String _actionCommand, String image)
    {
        EJRWTMenuTreeElement treeElement = new EJRWTMenuTreeElement(text, tooltip);
        treeElement._type = Type.FORM;
        treeElement._actionCommand = _actionCommand;
        treeElement._image = image;
        _treeElements.add(treeElement);
        return treeElement;
    }

    public EJRWTMenuTreeElement addActionMenuItem(String text, String tooltip, String _actionCommand, String image)
    {
        EJRWTMenuTreeElement treeElement = new EJRWTMenuTreeElement(text, tooltip);
        treeElement._type = Type.ACTION;
        treeElement._actionCommand = _actionCommand;
        treeElement._image = image;
        _treeElements.add(treeElement);
        return treeElement;
    }

    /**
     * Adds a separator to this menu
     */
    public void addSeparatorItem()
    {
        EJRWTMenuTreeElement treeElement = new EJRWTMenuTreeElement();
        treeElement._type = Type.SEPARATOR;
        _treeElements.add(treeElement);
    }

    /**
     * Returns a list with the tree elements of this tree. The list contains
     * tree elements and separators.
     * 
     * @return a List of EJRWTTreeElementProperties objects
     */
    public List<EJRWTMenuTreeElement> getTreeElements()
    {
        return _treeElements;
    }

    /**
     * Returns the tree element instance at the given index. if the index is out
     * of bounds the method will return null
     * 
     * @param index
     *            - The position of the tree element
     * @return the EJRWTTreeElementProperties at the given position or null if
     *         the index is invalid
     */
    public EJRWTMenuTreeElement getTreeElementAt(int index)
    {
        if (-1 < index && index < getTreeElementCount())
        {
            return _treeElements.get(index);
        }
        return null;
    }

    /**
     * Returns the count of the tree elements items of this tree
     * 
     * @return The number of elements
     */
    public int getTreeElementCount()
    {
        return _treeElements.size();
    }

    @Override
    public String toString()
    {
        return "EJRWTMenuTreeElement [_text=" + _text + "]";
    }

    public Type getType()
    {
        return _type;
    }

}
