Set shell = CreateObject("WScript.Shell")
desktopPath = shell.SpecialFolders("Desktop")
appPath = CreateObject("Scripting.FileSystemObject").GetParentFolderName(WScript.ScriptFullName)
Set link = shell.CreateShortcut(desktopPath & "\Demo KMP.lnk")
link.IconLocation = appPath & "\icon.ico"
link.TargetPath = appPath & "\run.vbs"
link.WindowStyle = 3
link.WorkingDirectory = appPath
link.Save