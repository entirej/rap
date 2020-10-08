package org.entirej.applicationframework.rwt.application.form.containers;

import java.util.Stack;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.SingletonUtil;

public class EJRWTDialogContext
{

   // private volatile 

    private EJRWTDialogContext()
    {
        // keep private
    }
    
    @SuppressWarnings("unchecked")
    public Stack<EJRWTAbstractDialog> getDialogs()
    {
        Stack<EJRWTAbstractDialog> dialogs = null;
        dialogs  = (Stack<EJRWTAbstractDialog>) RWT.getUISession().getAttribute("EJRWTDialogContext.dialogs");
        if(dialogs==null) {
            dialogs = new Stack<>();
            RWT.getUISession().setAttribute("EJRWTDialogContext.dialogs",dialogs);
        }
        
        return dialogs;
    }
    

    public static EJRWTDialogContext get()
    {
        return SingletonUtil.getSessionInstance(EJRWTDialogContext.class);
    }

    public void open(EJRWTAbstractDialog dialog)
    {
        Stack<EJRWTAbstractDialog> dialogs = getDialogs();
        dialogs.remove(dialog);//remove and add to popto top
        dialogs.add(dialog);
    }

    public void close(EJRWTAbstractDialog dialog)
    {
        getDialogs().remove(dialog);
    }

    public boolean isCurrent(EJRWTAbstractDialog dialog)
    {
        Stack<EJRWTAbstractDialog> dialogs = getDialogs();
        return dialogs.size()<2 || dialogs.peek() == dialog;
    }
}
