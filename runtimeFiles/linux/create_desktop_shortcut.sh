#!/bin/bash

name="Demo KMP"
desktopFolder=$(xdg-user-dir DESKTOP)
shortcut="$desktopFolder/$name.desktop"

echo "[Desktop Entry]
Type=Application
Name=$name
Icon=$PWD/icon.png
Exec=\"$PWD/launcher\"
Path=$PWD" > "$shortcut"
chmod +x "$shortcut"
