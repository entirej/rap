package org.entirej.applicationframework.rwt.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

public class EJRWTFileDownloadTest
{
    @Test
    public void outputNameRemovesPathSeparatorsAndHeaderControls()
    {
        String name = EJRWTFileDownload.sanitizeOutputName("../evil\r\nname.txt", Path.of("safe.txt"));

        assertEquals(".._evilname.txt", name);
        assertFalse(name.contains("\r"));
        assertFalse(name.contains("\n"));
    }

    @Test
    public void blankOutputNameFallsBackToSourceName()
    {
        assertEquals("safe.txt", EJRWTFileDownload.sanitizeOutputName(null, Path.of("folder", "safe.txt")));
        assertEquals("safe.txt", EJRWTFileDownload.sanitizeOutputName("\r\n", Path.of("safe.txt")));
        assertEquals("download", EJRWTFileDownload.sanitizeOutputName("\u0000", Path.of("safe.txt")));
    }

    @Test
    public void contentDispositionHasSafeFallbackAndUtf8Name()
    {
        String header = EJRWTFileDownload.createContentDisposition("résumé \"Q1\".pdf");

        assertEquals("attachment; filename=\"r_sum_ _Q1_.pdf\"; filename*=UTF-8''r%C3%A9sum%C3%A9%20%22Q1%22.pdf", header);
        assertFalse(header.contains("\r"));
        assertFalse(header.contains("\n"));
        assertTrue(header.contains("filename*=UTF-8''"));
    }
}
