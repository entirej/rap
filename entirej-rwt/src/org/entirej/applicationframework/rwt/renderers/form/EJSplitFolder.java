package org.entirej.applicationframework.rwt.renderers.form;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class EJSplitFolder
{

    private final SashForm pane;
    private Set<Control>  hidden = new HashSet<>();
    private Shell fakeShell;

    public EJSplitFolder(SashForm pane)
    {
        this.pane = pane;
        fakeShell = new Shell();
        pane.addDisposeListener(e->fakeShell.dispose());
    }

    public void setVisible(String splitPageCanvasName, boolean visible)
    {
        
        if (visible)
        {
            Optional<Control> findFirst = hidden.stream().filter(c -> splitPageCanvasName.equals(c.getData("canvas_id"))).findFirst();
            if (findFirst.isPresent())
                findFirst.get().setParent(pane);
        }
        else 
        {
            Control[] children = pane.getChildren();
            for (Control control : children)
            {
                if(splitPageCanvasName.equals(control.getData("canvas_id")))
                {
                    control.setParent(fakeShell);
                    hidden.add(control);
                    break;
                }
            }
        }

        pane.layout();

    }

}
