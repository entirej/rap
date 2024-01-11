package org.eclipse.rwt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Display;

public class EJRWTAsync
{

    private static Method method;

    static{
        try
        {
            method = Display.class.getDeclaredMethod("isValidThread");

            method.setAccessible(true);
        }
        catch (NoSuchMethodException | SecurityException e)
        {

            e.printStackTrace();
        }

    }

    public static void runUISafe(Display display, Runnable run)
    {
        if(!display.isDisposed())
        try
        {
            if ((boolean) method.invoke(display))
                run.run();
            else
                display.asyncExec(run);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

    }

}
