Add-Type -AssemblyName PresentationFramework

function ShowErrorMessage {
    [CmdletBinding()]
        param (
            [Parameter()]
            [string]
            $Message
        )
    [System.Windows.MessageBox]::Show($Message, "Launch error", "OK", "Error")
}

Start-Sleep -s 1

$configuration_path = "launcher_configuration.ini"
if ([System.IO.File]::Exists($configuration_path)) {
    $configuration = Get-Content $configuration_path -Raw | ConvertFrom-StringData

    $java_command = if ([string]$configuration.java_path -ne "") { $configuration.java_path } else { "java" }
    $java_version = (Get-Command $java_command | Select-Object -ExpandProperty Version).Major
    if ([int]$java_version -lt 17) {
        $displayed_version = if ([string]$java_version -ne "") { $java_version } else { "none" }
        ShowErrorMessage("Java 17+ required for launch, current version - $($displayed_version)")
    } else {
        $jar_path = "application/app.jar"
        if ([System.IO.File]::Exists($jar_path)) {
            & $java_command -jar $jar_path launchedFromScript
        } else {
            ShowErrorMessage("Executable file not found")
        }
    }
} else {
    ShowErrorMessage("Launch configuration not found")
}