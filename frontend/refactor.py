import os
import glob
import re
import shutil

base_dir = '/Volumes/U-DRIVE-MAC/Mains/3. Gits Works/Hospital-Automation/frontend'
pages_dir = os.path.join(base_dir, 'pages')
if not os.path.exists(pages_dir):
    os.makedirs(pages_dir)

# Move all html except index.html
html_files = glob.glob(os.path.join(base_dir, '*.html'))
for f in html_files:
    filename = os.path.basename(f)
    if filename != 'index.html':
        shutil.move(f, os.path.join(pages_dir, filename))

# Update the moved HTML files
moved_files = glob.glob(os.path.join(pages_dir, '*.html'))
for f in moved_files:
    with open(f, 'r') as file:
        content = file.read()
    
    # Update asset paths
    content = content.replace('href="css/style.css"', 'href="../css/style.css"')
    content = content.replace('src="elements/components.js"', 'src="../elements/components.js"')
    content = content.replace('src="js/data.js"', 'src="../js/data.js"')
    content = content.replace('src="js/app.js"', 'src="../js/app.js"')
    content = content.replace('href="index.html"', 'href="../index.html"')
    
    with open(f, 'w') as file:
        file.write(content)

# Update components.js
comp_path = os.path.join(base_dir, 'elements', 'components.js')
with open(comp_path, 'r') as file:
    comp = file.read()

comp = comp.replace("const pages = [", "const isRoot = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');\n    const prefixPage = isRoot ? 'pages/' : '';\n    const prefixIndex = isRoot ? '' : '../';\n    const pages = [")

comp = comp.replace("{ url: 'index.html'", "{ url: prefixIndex + 'index.html'")
for p in ['giris', 'profil', 'doktor-panel', 'hasta-listesi', 'kayit', 'personel-kayit', 'rezervasyon', 'muayene', 'odeme', 'api-test']:
    comp = comp.replace(f"url: '{p}.html'", f"url: prefixPage + '{p}.html'")

comp = comp.replace('href="api-test.html"', 'href="${isRoot ? \'pages/\' : \'\'}api-test.html"')
comp = comp.replace("banner.innerHTML = `", "const isRoot = window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/');\n  banner.innerHTML = `")

for p in ['giris', 'profil', 'doktor-panel', 'hasta-listesi', 'kayit', 'personel-kayit', 'rezervasyon', 'muayene', 'odeme']:
    comp = comp.replace(f'href="{p}.html"', f'href="${{isRoot ? \'pages/\' : \'\'}}{p}.html"')
comp = comp.replace('href="index.html"', 'href="${isRoot ? \'\' : \'../\'}index.html"')

comp = comp.replace("if (pageMap[currentPage] === p.url) {", "if (p.url.endsWith(pageMap[currentPage])) {")

with open(comp_path, 'w') as file:
    file.write(comp)

# Update app.js
app_path = os.path.join(base_dir, 'js', 'app.js')
with open(app_path, 'r') as file:
    app = file.read()

app = app.replace("const checkAccess = (href) => {", "const checkAccess = (href) => {\n    const filename = href.split('/').pop();")
app = app.replace("if (href ===", "if (filename ===")
app = app.replace("includes(href)", "includes(filename)")

app = app.replace('window.location.href = "index.html";', 'window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "index.html" : "../index.html";')
app = app.replace('window.location.href = "kayit.html";', 'window.location.href = (window.location.pathname.endsWith("index.html") || window.location.pathname.endsWith("/")) ? "pages/kayit.html" : "kayit.html";')
# wait what about logout? 
# "document.querySelector('#logout-btn').addEventListener('click', () => { ... window.location.href = 'index.html'; })"
# There is a generic logout function? No, let's see where index.html is referenced.

with open(app_path, 'w') as file:
    file.write(app)

print("Refactor complete")
