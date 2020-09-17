package org.entirej.applicationframework.rwt.application.form.containers;

import java.util.Stack;

import org.eclipse.rap.rwt.SingletonUtil;

public class EJRWTDialogContext
{

    private volatile Stack<EJRWTAbstractDialog> dialogs = new Stack<>();

    private EJRWTDialogContext()
    {
        // keep private
    }

    public static EJRWTDialogContext get()
    {
        return SingletonUtil.getSessionInstance(EJRWTDialogContext.class);
    }

    public void open(EJRWTAbstractDialog dialog)
    {
        dialogs.add(dialog);
    }

    public void close(EJRWTAbstractDialog dialog)
    {
        dialogs.remove(dialog);
    }

    public boolean isCurrent(EJRWTAbstractDialog dialog)
    {
        return dialogs.size()<2 || dialogs.peek() == dialog;
    }
}
