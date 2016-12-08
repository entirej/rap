package org.entirej.applicationframework.rwt.layout;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class EJRWTScrolledComposite extends ScrolledComposite
{

    public EJRWTScrolledComposite(Composite parent, int style)
    {
        super(parent, style);

    }

    @Override
    public Point getOrigin()
    {

        return isDisposed() ? new Point(0, 0) : super.getOrigin();
    }
}
