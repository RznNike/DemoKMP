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
    $version_output = & $java_command -version 2>&1
        $version_string = ($version_output | Select-String "version").ToString()
        $java_full_version = [regex]::Match($version_string, '\"(.*?)\"').Groups[1].Value
        if ($java_full_version.StartsWith("1.")) {
            $java_version = $java_full_version.Split('.')[1] # v1-8
        } else {
            $java_version = $java_full_version.Split('.')[0] # v9+
        }
    if ([int]$java_version -lt 17) {
        $displayed_version = if ([string]$java_version -ne "") { $java_version } else { "none" }
        ShowErrorMessage("Java 17+ required for launch, current version - $($displayed_version)")
    } else {
        $jar_path = "application/app.jar"
        if ([System.IO.File]::Exists($jar_path)) {
            & $java_command -jar $jar_path renderApi=$($configuration.render_api)
        } else {
            ShowErrorMessage("Executable file not found")
        }
    }
} else {
    ShowErrorMessage("Launch configuration not found")
}