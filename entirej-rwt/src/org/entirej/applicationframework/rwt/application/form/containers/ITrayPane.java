package org.entirej.applicationframework.rwt.application.form.containers;

import org.entirej.applicationframework.rwt.application.form.containers.EJRWTTrayDialog.TrayLocation;

public interface ITrayPane
{

    /**
     * Closes this dialog's tray, disposing its widgets.
     * 
     * @throws IllegalStateException
     *             if the tray was not open
     */
    void closeTray() throws IllegalStateException;

    /**
     * Returns the tray currently shown in the dialog, or <code>null</code> if
     * there is no tray.
     * 
     * @return the dialog's current tray, or <code>null</code> if there is none
     */
    EJRWTDialogTray getTray();

    /**
     * Constructs the tray's widgets and displays the tray in this dialog. The
     * dialog's size will be adjusted to accommodate the tray.
     * 
     * @param tray
     *            the tray to show in this dialog
     * @throws IllegalStateException
     *             if the dialog already has a tray open
     * @throws UnsupportedOperationException
     *             if the dialog does not support trays, for example if it uses
     *             a custom layout.
     */
    void openTray(TrayLocation location, EJRWTDialogTray tray, int size) throws IllegalStateException, UnsupportedOperationException;

}