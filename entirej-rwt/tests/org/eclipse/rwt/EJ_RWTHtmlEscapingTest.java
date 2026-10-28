package org.eclipse.rwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EJ_RWTHtmlEscapingTest
{
    @Test
    public void preservesNullAndEmptyValues()
    {
        assertNull(EJ_RWT.escapeHtml(null));
        assertEquals("", EJ_RWT.escapeHtml(""));
    }

    @Test
    public void escapesUntrustedMarkupAndAttributeQuotes()
    {
        assertEquals("&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt;&amp;&#39;",
                EJ_RWT.escapeHtml("<script>alert(\"x\")</script>&'"));
    }

    @Test
    public void preservesUnicodeAndOrdinaryText()
    {
        assertEquals("Gr\u00fc\u00dfe \u4e2d\u6587 &amp; report", EJ_RWT.escapeHtml("Gr\u00fc\u00dfe \u4e2d\u6587 & report"));
    }
}
