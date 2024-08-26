package org.eclipse.rwt;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.swt.widgets.Composite;

public interface EJRWTHtmlViewSupport
{

    public Composite createHtmlView(int widthHint,Composite parent,ActionCallback callback);

    public static interface ActionCallback
    {
        public void action(String method, JsonObject parameters);
    }
    
    public static interface HtmlTextSupport
    {
        public void setText(String text);
        public String getText();
        void layout(int widthHint);
    }
    
}
