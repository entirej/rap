package org.entirej.applicationframework.rwt.file;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.Rule;
import org.junit.Test;

public class EJRWTFileUploadTest
{
    @Rule
    public TestContext context = new TestContext();

    @Test
    public void cancellationAndEmptyResultsAreAlwaysNull()
    {
        assertNull(EJRWTFileUpload.selectedFiles(SWT.CANCEL, "previous.txt"));
        assertNull(EJRWTFileUpload.selectedFiles(SWT.OK, (String[]) null));
        assertNull(EJRWTFileUpload.selectedFiles(SWT.OK, (String) null));
        assertNull(EJRWTFileUpload.selectedFiles(SWT.OK, ""));
        assertNull(EJRWTFileUpload.selectedFiles(SWT.OK));
    }

    @Test
    public void validSelectionsRetainOrderAndExcludeEmptyEntries()
    {
        assertArrayEquals(new String[] { "first.csv" }, EJRWTFileUpload.selectedFiles(SWT.OK, "first.csv"));
        String[] files = { "first.csv", null, "", "second.xlsx" };
        assertArrayEquals(new String[] { "first.csv", "second.xlsx" }, EJRWTFileUpload.selectedFiles(SWT.OK, files));
        assertNull(files[1]);
        assertEquals("", files[2]);
    }

    @Test
    public void multipleDialogAppliesExtensionFiltersAndCancelCallbackOnce()
    {
        checkDialog(true, new String[] { "*.csv", "*.xlsx" });
    }

    @Test
    public void singleDialogKeepsItsFiltersAndCancelCallbackOnce()
    {
        checkDialog(false, new String[] { "*.json" });
    }

    private static void checkDialog(boolean multiple, String[] extensions)
    {
        Display display = new Display();
        Shell owner = new Shell(display);
        owner.open();
        List<String[]> callbacks = new ArrayList<>();
        if (multiple)
        {
            EJRWTFileUpload.promptMultipleFileUpload("Upload", 1024, 5000, extensions, callbacks::add);
        }
        else
        {
            EJRWTFileUpload.promptFileUpload("Upload", 1024, 5000, extensions, callbacks::add);
        }
        List<Control> controls = new ArrayList<>();
        for (Shell shell : display.getShells())
        {
            collect(shell, controls);
        }
        FileUpload upload = controls.stream().filter(FileUpload.class::isInstance).map(FileUpload.class::cast).findFirst().orElseThrow();
        assertArrayEquals(extensions, upload.getFilterExtensions());
        assertEquals(multiple, (upload.getStyle() & SWT.MULTI) != 0);

        Button cancel = controls.stream().filter(Button.class::isInstance).map(Button.class::cast)
                .filter(button -> SWT.getMessage("SWT_Cancel").equals(button.getText())).findFirst().orElseThrow();
        cancel.notifyListeners(SWT.Selection, new Event());
        while (display.readAndDispatch()) { }
        assertEquals(1, callbacks.size());
        assertNull(callbacks.get(0));
    }

    private static void collect(Composite parent, List<Control> controls)
    {
        for (Control child : parent.getChildren())
        {
            controls.add(child);
            if (child instanceof Composite composite)
            {
                collect(composite, controls);
            }
        }
    }
}
