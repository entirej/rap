# RAP HTML proxy and upload fixes

Date: 2026-10-28

Status: Reviewed fixes based on `develop` commit `3cbc340`. Source commit/push
and development Maven publication were authorized on 2026-10-28. Application-server
deployment is outside this publication. TEMOS and Builder source files were not
changed.

## 1. HTML proxy: issue and fix

Previously, `HtmlProxyServiceHandler` used a static `HashMap` shared by all UI
sessions. The request supplied a token and optional action; the handler looked
up the provider and invoked it without checking which session registered it.
A valid token obtained from another session could therefore address that
session's provider. This is an ownership defect; this review does not claim
that UUID tokens were guessable or that the endpoint bypassed the deployed
authentication filter chain.

There were two related lifecycle problems:

- Unknown/missing tokens were dereferenced and could cause a null-pointer error.
- A renderer retained its registration when cleared or disposed. The provider
  captured the renderer/controller, so a global registry could retain those
  objects and stale action URLs.

The implementation now:

- Stores registrations in the owning RAP `UISession`, with synchronized access.
  The static `HtmlProxy.INSTANCE` remains a compatibility facade and holds no
  global provider map. Requests can only resolve tokens in their current session.
- Returns HTTP 404 for missing, unknown, removed, or destroyed-session tokens.
- Clears registrations on UI-session destruction using one listener per registry.
- Gives the renderer a closeable registration handle. Replacement, explicit
  clearing, empty values, failed setup, and browser disposal release the handle.
  Closing a handle does not require an ambient RAP request context.
- Checks registration/generation state before serving content; action checks run
  on the owning SWT display. A request that captured an old provider cannot
  use it after replacement, clear, disposal, or revocation.
- Serves normal HTML reads from a published thread-safe snapshot, without
  waiting for the UI thread. Actions use `Display.syncExec` and a scoped RAP
  `ServerPushSession` to wake the UI; service threads do not access SWT controls.
- Rejects blank, control-character-containing, or over-256-character action
  names with HTTP 400. Unsupported HTTP methods receive 405; existing GET/POST
  links remain supported. Explicit `Sec-Fetch-Site: cross-site` action requests
  receive 403. This is input/Fetch Metadata validation, not a command allowlist
  or a replacement for application authentication, authorization, or CSRF policy.
- Aligns the frame policy with the existing `SAMEORIGIN` header by removing the
  wildcard from CSP `frame-ancestors`; adds `nosniff` and retains no-store headers.
- Substitutes `%RAP_URL_PATH%` literally and prepares content before navigating
  the browser to the proxy URL.

### Follow-up: empty-value regression found during test-app review

The first implementation changed an empty initial value from a proxy URL to
`Browser.setText("")`. RAP 4.8 turns that call into a generated resource URL:
`rwt-resources/org.eclipse.swt.browser/text-1104395608.html`. That response
does not pass through this proxy handler and therefore does not receive its
frame policy. A test app using Spring's default `X-Frame-Options: DENY` can
block that frame; an unauthenticated resource request can also redirect to a
login page carrying DENY. This was a regression in the new empty-value path.

Empty initial values and explicit clearing now use `Browser.setUrl("about:blank")`,
which makes no server request and still releases the old registration.
The new initial-empty test and an added clear assertion both failed before the
correction and pass afterward. A RAP BrowserLCA reproduction confirmed all
three URL choices (old proxy URL, introduced resource URL, corrected about:blank).
No test-app security configuration or TinyMCE resources were changed.
Authenticated browser retesting is still needed to confirm the user's complete
reported screen; the existing missing `borderless` skin is a separate finding.

### Follow-up: first content read waited for a click/scroll

The original new `render` method called `Display.syncExec` even for a plain
HTML GET. RAP's UI can be idle between requests. That call queued the read and
held the iframe HTTP response until another UI request arrived. The symptom
was a blank first update that appeared after a second click or scrolling.
The original request-thread test inadvertently hid this by pumping the display.

The corrected test now starts the first read after an empty value and waits for
its result **without pumping any UI events**. It timed out before the fix and
passes after it. Content and generation state are now published with volatile
fields; reads check session-registration validity and return the prepared HTML
immediately. Actions retain UI dispatch. A server-push session is started while
non-empty content is registered and stopped on replacement, clear, failure, or
disposal; RAP stops push sessions when the UI session ends. Tests check that
push does not remain active after clear/disposal and that a request-thread
action executes once on the UI thread and can replace the content.

The local test app must reload the rebuilt runtime (restart if hot replacement
cannot apply the added fields), then verify the first Update without moving or
scrolling. This task has not restarted or deployed the user's running app.

Main files:

- `entirej-rwt-rap/src/org/entirej/applicationframework/rwt/renderers/html/HtmlProxyServiceHandler.java`
- `entirej-rwt-rap/src/org/entirej/applicationframework/rwt/renderers/html/EJRWTExtHtmlViewItemRenderer.java`

## 2. Uploads: issue and fix

The single-file callback used the condition
`fileName != null && fileName.isEmpty() ? null : new String[] { fileName }`.
If the dialog returned `null`, that expression produced `[null]`. Neither
callback explicitly required an OK result, making cancellation handling depend
on the dialog's filename state.

The multi-file method also accepted extension filters but never called
`FileDialog.setFilterExtensions(ext)`.

The implementation now:

- Returns `null` for cancelled dialogs and for null/empty selections.
- Returns a fresh array containing only non-null, non-empty selected paths in
  their original order for an OK result.
- Passes the configured extension filters to both single-file and multi-file
  RAP dialogs. Existing positive size/time limits are retained.

The extension change fixes file-picker filtering. Browser extension hints are
not server-side file-content/type validation; no such guarantee is claimed.

Main file:
`entirej-rwt-rap/src/org/entirej/applicationframework/rwt/file/EJRWTFileUpload.java`

## Verification

Initial command: `mvn clean verify` in `entirej-rwt-rap`, using Maven 3.9.9 and
JDK 21. After the empty-value correction, `mvn -o verify` passed the full suite.

| Test class | Passed |
|---|---:|
| `HtmlProxyServiceHandlerTest` | 7 |
| `EJRWTExtHtmlViewItemRendererTest` | 8 |
| `EJRWTFileUploadTest` | 4 |
| Existing `EJRWTFileDownloadTest` | 3 |
| Existing `FileUploadCompatibilityTest` | 2 |
| Total | 24 |

No failures, errors, or skipped tests. The test-only RAP 4.8 testfixture dependency
was added for actual headless widget/dialog tests; production dependencies are
unchanged. Proxy tests use independent session stores and servlet request/response
doubles to verify that cross-session requests never read content or invoke actions.
Widget tests cover replacement, clear, disposal, revocation, and a request thread
dispatching to its owning display. Dialog tests inspect real RAP file-upload
controls and exercise Cancel callbacks.

Build log: `entirej-rwt-rap/target/local-review-build.log`

Test reports: `entirej-rwt-rap/target/surefire-reports/`

Local JAR: `entirej-rwt-rap/target/entirej-rwt-appframework-rcp-DEVELOPMENT.jar`

SHA-256: `824A114D17F8CD5285AF0A6E4E83E9384A62E26385A650003D246E0E7EE6A36B`

## Browser review before publication

1. Render an existing ExtHtmlView and use its normal action links; replace the
   content and confirm the old URL no longer works.
2. Open two independent user sessions (and two RAP UI sessions/tabs sharing an
   HTTP session); verify one UI session cannot load or execute the other's token.
3. Close the owning form/tab and log out; verify stale URLs do not execute actions.
4. Check any intentionally embedded content against the same-origin frame policy.
5. Check single/multiple upload cancellation, successful selections, configured
   extension filters, and the existing size/time limits in supported browsers.

Automated tests pass locally. During browser review the user reported the blank
initial view and the first content read waiting for scrolling, which led to the
two regression fixes documented above. The complete deployed-browser checklist
has not been certified by automation.
