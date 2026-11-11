package org.entirej.applicationframework.rwt.component;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ResourceManager;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.junit.Rule;
import org.junit.Test;

public class EJRWTTinymceEditorResourcesTest
{
    @Rule
    public TestContext context = new TestContext();

    @Test
    public void allDeclaredAssetsExistAndRegisterWithTheirOriginalPaths() throws Exception
    {
        EJRWTTinymceEditor.registerResources();
        ResourceManager manager = RWT.getResourceManager();
        for (String fieldName : new String[] { "RESOURCE_FILES", "RESOURCE_FILES_SUB" })
        {
            Field field = EJRWTTinymceEditor.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            for (String asset : (String[]) field.get(null))
            {
                String registered = "tinymceeditor/" + asset;
                assertTrue(registered, manager.isRegistered(registered));
                assertTrue(manager.getLocation(registered).endsWith(asset));
                // RAP's TestResourceManager records registrations but intentionally returns no content stream.
                try (InputStream original = EJRWTTinymceEditor.openResource("resources/tinymce/" + asset))
                {
                    assertNotNull(asset, original);
                    assertTrue(asset, original.readAllBytes().length > 0);
                }
            }
        }
        assertFalse(manager.isRegistered("tinymceeditor/skins/borderless/skin.min.css"));
    }

    @Test
    public void partialRegistrationDoesNotHideMissingSkinsAndModels()
    {
        ResourceManager manager = RWT.getResourceManager();
        manager.register("tinymceeditor/tinymce.min.js", new ByteArrayInputStream(new byte[] { 1 }));
        EJRWTTinymceEditor.registerResources();
        assertTrue(manager.isRegistered("tinymceeditor/skins/ui/oxide/skin.min.css"));
        assertTrue(manager.isRegistered("tinymceeditor/skins/ui/oxide/content.min.css"));
        assertTrue(manager.isRegistered("tinymceeditor/models/dom/model.min.js"));
        assertTrue(manager.isRegistered("tinymceeditor/icons/default/icons.min.js"));
    }

    @Test
    public void repeatedInitializationRepairsOneMissingAsset()
    {
        ResourceManager manager = RWT.getResourceManager();
        EJRWTTinymceEditor.registerResources();
        manager.unregister("tinymceeditor/skins/ui/oxide/content.min.css");
        EJRWTTinymceEditor.registerResources();
        EJRWTTinymceEditor.registerResources();
        assertTrue(manager.isRegistered("tinymceeditor/skins/ui/oxide/content.min.css"));
    }

    @Test
    public void leadingSlashClasspathPathsResolveTheSameCss() throws Exception
    {
        try (InputStream relative = EJRWTTinymceEditor.openResource("resources/tinymce/ej/content.ej.css");
                InputStream absolute = EJRWTTinymceEditor.openResource("/resources/tinymce/ej/content.ej.css"))
        {
            assertNotNull(absolute);
            assertArrayEquals(relative.readAllBytes(), absolute.readAllBytes());
        }
    }

    @Test
    public void packagedHandlerDoesNotRequestAnUnshippedSkin() throws Exception
    {
        try (InputStream input = EJRWTTinymceEditor.openResource("resources/tinymce/tinymcehandler.js"))
        {
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertFalse(script.contains("skin: 'borderless'"));
            assertTrue(script.contains("skin: 'oxide'"));
            assertTrue(script.contains("license_key: 'gpl'"));
            assertTrue(script.contains(".tox.tox-tinymce-aux { z-index: 1000001; }"));
        }
    }

    @Test
    public void distributionSupportDocumentsAreNotEmbeddedInRuntime() throws Exception
    {
        EJRWTTinymceEditor.registerResources();
        for (String asset : new String[] { "LICENSE.md", "UPSTREAM-NOTICES.txt", "GPL-2.0-or-later.txt",
                "DOMPurify-LICENSE", "PrismJS-LICENSE", "PrismThemes-LICENSE", "PROVENANCE.txt" })
        {
            try (InputStream input = EJRWTTinymceEditor.openResource("META-INF/licenses/tinymce/" + asset))
            {
                assertNull(asset, input);
            }
        }
        try (InputStream input = EJRWTTinymceEditor.openResource("META-INF/third-party/tinymce/tinymce-8.10.2-source.tar.gz"))
        {
            assertNull(input);
        }
        for (String asset : new String[] { "license.md", "notices.txt", "COPYING" })
        {
            assertNull(EJRWTTinymceEditor.openResource("resources/tinymce/" + asset));
            assertFalse(RWT.getResourceManager().isRegistered("tinymceeditor/" + asset));
        }
    }

    @Test
    public void obsoleteTinyMce4AssetsAreNotBundled() throws Exception
    {
        assertNull(EJRWTTinymceEditor.openResource("resources/tinymce/skins/lightgray/skin.min.css"));
        assertNull(EJRWTTinymceEditor.openResource("resources/tinymce/themes/modern/theme.min.js"));
    }
}
