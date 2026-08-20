package org.entirej.applicationframework.rwt.spring.ext;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class AbstractTemplateServletTest
{
    @Test
    public void substitutionsAreHtmlEscaped()
    {
        String value = "<script>alert(\"x\")</script> & '$1'";
        String template = "<div title=\"${value}\">${value}</div>";

        String result = AbstractTemplateServlet.substituteVariables(template, Map.of("value", value));

        String escaped = "&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt; &amp; &#39;$1&#39;";
        assertEquals("<div title=\"" + escaped + "\">" + escaped + "</div>", result);
    }

    @Test
    public void unknownVariablesRemainInTemplate()
    {
        assertEquals("${unknown}", AbstractTemplateServlet.substituteVariables("${unknown}", Map.of()));
    }
}
