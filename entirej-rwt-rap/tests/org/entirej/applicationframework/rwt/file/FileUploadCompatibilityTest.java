package org.entirej.applicationframework.rwt.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload2.core.MultipartInput;
import org.junit.Test;

public class FileUploadCompatibilityTest
{
    private static final byte[] BOUNDARY = "AaB03x".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] PAYLOAD  = ("--AaB03x\r\n"
            + "Content-Disposition: form-data; name=\"field\"\r\n"
            + "\r\n"
            + "value\r\n"
            + "--AaB03x--\r\n").getBytes(StandardCharsets.US_ASCII);

    @Test
    public void parsesMultipartWithLegacyFileUpload() throws Exception
    {
        MultipartStream input = new MultipartStream(new ByteArrayInputStream(PAYLOAD), BOUNDARY, 4096, null);

        assertTrue(input.skipPreamble());
        input.readHeaders();

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        input.readBodyData(body);

        assertEquals("value", body.toString(StandardCharsets.US_ASCII));
    }

    @Test
    public void parsesMultipartWithRapFileUpload2() throws Exception
    {
        MultipartInput input = MultipartInput.builder()
                .setInputStream(new ByteArrayInputStream(PAYLOAD))
                .setBoundary(BOUNDARY)
                .get();

        assertTrue(input.skipPreamble());
        input.readHeaders();

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        input.readBodyData(body);

        assertEquals("value", body.toString(StandardCharsets.US_ASCII));
    }
}
