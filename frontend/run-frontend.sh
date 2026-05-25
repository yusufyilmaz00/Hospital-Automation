#!/bin/bash
echo "Frontend sunucusu başlatılıyor..."
echo "Tarayıcınızda şu adrese gidin: http://localhost:3000"
echo "Çıkış yapmak için CTRL+C tuşlarına basın."
python3 -m http.server 3000
