package org.entirej.applicationframework.rwt.layout;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EJRWTScrolledComposite extends ScrolledComposite
{

    public EJRWTScrolledComposite(Composite parent, int style)
    {
        super(parent, style);

    }
    
    @Override
    public void setContent(final Control content)
    {
       if(content!=null){
           
           content.addDisposeListener(new DisposeListener()
        {
            
            @Override
            public void widgetDisposed(DisposeEvent event)
            {
                EJRWTScrolledComposite.super.setContent(null);
            }
        });
       }
        super.setContent(content);
    }

    @Override
    public Point getOrigin()
    {

        return isDisposed() || (getContent() == null || getContent().isDisposed()) ? new Point(0, 0) : super.getOrigin();
    }
}
