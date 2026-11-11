// Real-engine visual comparison, not a live RAP/TEMOS servlet qualification.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const http = require('node:http');
const path = require('node:path');
const { Builder } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const project = path.resolve(__dirname, '..');
const required = name => { assert.ok(process.env[name], `Set ${name}`); return path.resolve(process.env[name]); };
const roots = { legacy4: required('TINYMCE_LEGACY_RESOURCES'), previous8: required('TINYMCE_PREVIOUS_RESOURCES'),
  aligned8: path.resolve(process.env.TINYMCE_TEST_RESOURCES || path.join(project, 'src/resources/tinymce')) };
const temosCss = fs.readFileSync(required('TINYMCE_TEMOS_CONTENT_CSS'), 'utf8');
const rapCss = fs.readFileSync(path.join(project, 'src/resources/tinymce/ej/content.ej.css'), 'utf8');
const out = path.join(project, 'target/tinymce-style');
fs.mkdirSync(out, { recursive: true });
const htmlContent = '<p>Customer notes: <strong>Important</strong> <span style="font-size:20px;color:#ff0000">Custom format</span></p><table><tbody><tr><td>Order</td><td>Delivery</td></tr><tr><td>12345</td><td>Confirmed</td></tr></tbody></table>';
function page(variant) {
  const prefix = `/${variant}/`;
  return `<!doctype html><html><head><meta charset="utf-8"><title>RAP editor comparison</title>
  <style>body{font:12px Verdana,"Lucida Sans",Arial,sans-serif;color:#4a4a4a;background:white;margin:20px}
  .heading{font-size:12px;font-weight:bold;margin:0 0 14px}label{display:block;margin:8px 0}input{font:inherit;border:1px solid #aaa;padding:4px}</style></head><body>
  <p class="heading">TEMOS20 — Customer / Report notes</p><label>Customer <input value="Example customer"></label>
  <label>Notes</label><div id="host" style="position:relative;width:900px;height:320px"></div>
  <script src="${prefix}tinymce.min.js"></script><script>
  window.events={render:new Set(),send:new Set()};
  window.parentWidget={append(el){document.getElementById('host').appendChild(el)},addListener(){},removeListener(){},getClientArea(){const h=document.getElementById('host');return [0,0,h.clientWidth,h.clientHeight]}};
  window.rap={registerTypeHandler(){},getObject(){return parentWidget},getRemoteObject(){return {set(){},call(){}}},on(n,f){events[n].add(f)},off(n,f){events[n].delete(f)}};
  window.contentCss=${JSON.stringify(temosCss)};window.sample=${JSON.stringify(htmlContent)};
  </script>${variant === 'legacy4' ? '' : `<script src="${prefix}tinymcehandler.js"></script>`}
  <script>
  if(${JSON.stringify(variant)}==='legacy4'){
    const target=document.createElement('div');document.getElementById('host').appendChild(target);
    tinymce.init({target,base_url:'${prefix.slice(0,-1)}',suffix:'.min',theme:'modern',skin:'lightgray',height:230,
      branding:false,resize:false,plugins:['visualblocks','fullscreen'],content_style:contentCss,
      toolbar:'undo redo | formatselect | fontsizeselect bold italic backcolor forecolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | removeformat | fullscreen',
      setup(e){window.editor=e;e.on('init',()=>{e.setContent(sample);window.ready=true})}});
  }else{
    window.widget=new eclipsesource.TinymceEditor({parent:'host',inline:false,removeToolbar:true,supportTable:true,
      contentCss,configObj:{},basePath:'${prefix}'});widget.setText(sample);events.render.forEach(f=>f());
  }
  </script></body></html>`;
}
const failures = [];
const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (url.pathname === '/favicon.ico') { res.writeHead(204); res.end(); return; }
  const parts = url.pathname.split('/').filter(Boolean), variant = parts.shift();
  if (!roots[variant]) { res.writeHead(404); res.end(); return; }
  if (parts.join('/') === 'page.html') { res.writeHead(200, { 'Content-Type': 'text/html' }); res.end(page(variant)); return; }
  const file = path.resolve(roots[variant], ...parts);
  if (!file.startsWith(roots[variant] + path.sep) || !fs.existsSync(file) || !fs.statSync(file).isFile()) {
    failures.push(url.pathname); res.writeHead(404); res.end(); return;
  }
  const types = { '.js':'application/javascript', '.css':'text/css', '.woff':'font/woff' };
  res.writeHead(200, { 'Content-Type':types[path.extname(file)] || 'application/octet-stream' }); res.end(fs.readFileSync(file));
});
(async () => {
  let driver;
  try {
    await new Promise(resolve=>server.listen(0, '127.0.0.1', resolve));
    const options = new chrome.Options().setChromeBinaryPath(process.env.TINYMCE_TEST_CHROME || 'C:/tools/chromium-runtime-win/chrome.exe')
      .addArguments('--headless=new','--no-sandbox','--disable-gpu','--window-size=1100,850');
    driver = await new Builder().forBrowser('chrome').setChromeOptions(options)
      .setChromeService(new chrome.ServiceBuilder(process.env.TINYMCE_TEST_CHROMEDRIVER || 'C:/tools/chromium-runtime-win/chromedriver.exe')).build();
    const js = (s,...args)=>driver.executeScript(s,...args), result = {};
    const screenshot = async name=>fs.writeFileSync(path.join(out,name+'.png'),Buffer.from(await driver.takeScreenshot(),'base64'));
    for (const variant of Object.keys(roots)) {
      await driver.get(`http://127.0.0.1:${server.address().port}/${variant}/page.html`);
      await driver.wait(()=>js('return window.ready || (window.widget && widget.ready && widget.viewReady)'),15000);
      if (variant !== 'legacy4') { await js('window.editor=widget.editor;'); }
      if (variant === 'aligned8') { await driver.wait(()=>js("return !!document.getElementById('ej-tinymce-rap-ui-style').sheet"),5000); }
      result[variant] = await js(`const c=editor.getContainer(),b=editor.getBody(),button=c.querySelector('.tox-mbtn,.mce-menubtn button');
        const ui=getComputedStyle(button),body=editor.getWin().getComputedStyle(b),outer=getComputedStyle(c);
        return {version:tinymce.majorVersion+'.'+tinymce.minorVersion,uiFont:ui.fontFamily,uiFontSize:ui.fontSize,
          borderRadius:outer.borderRadius,borderWidth:outer.borderTopWidth,bodyFont:body.fontFamily,bodySize:body.fontSize,bodyMargin:body.margin,
          cellPadding:editor.getWin().getComputedStyle(b.querySelector('td')).padding,
          tableCollapse:editor.getWin().getComputedStyle(b.querySelector('table')).borderCollapse,
          bodyLineHeight:body.lineHeight,bodyColor:body.color,customSize:editor.getWin().getComputedStyle(b.querySelector('span')).fontSize,
          tablePresent:!!b.querySelector('td')};`);
      await screenshot(variant+'-editor');
      if (variant === 'legacy4') {
        await js("editor.windowManager.open({title:'Insert/Edit Link',body:[{type:'textbox',name:'url',label:'URL'},{type:'textbox',name:'text',label:'Text to display'}]});");
      } else { await js("editor.execCommand('mceLink');"); }
      await driver.wait(()=>js('return !!document.querySelector(".tox-dialog,.mce-window")'),5000);
      await screenshot(variant+'-dialog');
      if (variant === 'aligned8') {
        assert.match(result[variant].uiFont,/Verdana/); assert.equal(result[variant].uiFontSize,'12px');
        assert.equal(result[variant].borderRadius,'2px'); assert.equal(result[variant].borderWidth,'1px');
        assert.equal(result[variant].bodySize,'13px'); assert.equal(result[variant].bodyLineHeight,'19.5px');
        assert.equal(result[variant].customSize,'20px'); assert.ok(result[variant].tablePresent);
        assert.equal(result[variant].bodyMargin,result.legacy4.bodyMargin);
        assert.equal(result[variant].cellPadding,result.legacy4.cellPadding);
        assert.equal(result[variant].tableCollapse,result.legacy4.tableCollapse);
        const dialog = await js("const d=document.querySelector('.tox-dialog');return {radius:getComputedStyle(d).borderRadius,font:getComputedStyle(d.querySelector('.tox-label')).fontSize}");
        assert.deepEqual(dialog,{radius:'2px',font:'12px'});
        assert.equal(await js("return getComputedStyle(editor.getContainer().querySelector('.tox-editor-header')).boxShadow"),'none');
        await js('editor.windowManager.close(); widget.setEnable(false);');
        await screenshot('aligned8-readonly');
        assert.equal(await js('return getComputedStyle(widget.editorView.getContainer()).borderTopWidth'),'0px');
        assert.equal(await js('return widget.editorView.getWin().getComputedStyle(widget.editorView.getBody()).fontSize'),'13px');
        await js(`widget.destroy();window.widget=new eclipsesource.TinymceEditor({parent:'host',inline:false,
          removeToolbar:true,supportTable:true,contentCss:arguments[0],configObj:{},basePath:'/aligned8/'});
          widget.setText(sample);events.render.forEach(f=>f());`,temosCss+'\nbody {margin:22px} td {padding:9px} table {border-collapse:collapse}');
        await driver.wait(()=>js('return widget.ready && widget.viewReady'),10000);
        await js('window.editor=widget.editor;');
        assert.equal(await js('return editor.getWin().getComputedStyle(editor.getBody()).margin'),'22px');
        assert.equal(await js("return editor.getWin().getComputedStyle(editor.getBody().querySelector('td')).padding"),'9px');
        assert.equal(await js("return editor.getWin().getComputedStyle(editor.getBody().querySelector('table')).borderCollapse"),'collapse');
        await js("editor.getBody().querySelector('td').style.padding='7px';");
        assert.equal(await js("return editor.getWin().getComputedStyle(editor.getBody().querySelector('td')).padding"),'7px');
        await js(`widget.destroy();window.widget=new eclipsesource.TinymceEditor({parent:'host',inline:false,
          removeToolbar:true,supportTable:true,contentCss:arguments[0],configObj:{},basePath:'/aligned8/'});
          widget.setText('<p>RAP default</p>');events.render.forEach(f=>f());`,rapCss);
        await driver.wait(()=>js('return widget.ready && widget.viewReady'),10000);
        await js('window.editor=widget.editor;');
        assert.equal(await js('return editor.getWin().getComputedStyle(editor.getBody()).fontSize'),'12px');
        await js("widget.parent.getClientArea=()=>[0,0,360,300];document.getElementById('host').style.width='360px';widget.layout();");
        await screenshot('aligned8-narrow');
        assert.ok(await js('return editor.getContentAreaContainer().getBoundingClientRect().height>30'));
      }
    }
    assert.deepEqual(failures,[]);
    fs.writeFileSync(path.join(out,'comparison.json'),JSON.stringify(result,null,2));
    console.log(JSON.stringify(result,null,2));
  } finally { if(driver)await driver.quit(); server.close(); }
})().catch(e=>{console.error(e);process.exitCode=1});
