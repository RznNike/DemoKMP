#!/bin/bash

showErrorMessage() {
    zenity --title "Launch error" --error --text "$1" || # alter os
    kdialog --title "Launch error" --error "$1" || # kde
    xdialog --title "Launch error" --msgbox "$1" || # xorg
    fly-dialog --title "Launch error" --error "$1" # astra
}

sleep 1

configuration_path="launcher_configuration.ini"
if [[ -f "$configuration_path" ]]; then
    declare -A configuration
    while read line; do
        clean_line="${line//[$'\t\r\n']}"
        key="${clean_line%=*}"
        value="${clean_line#*=}"
        if [[ ! -z $key ]]; then
            configuration[$key]=$value
        fi
    done < "$configuration_path"

    java_command=${configuration["java_path"]}
    if [[ -z $java_command ]]; then
        java_command="java"
    fi
    java_version=$($java_command -version 2>&1 | grep -oP 'version "?(1\.)?\K\d+' || true)
    if [[ $java_version -lt 17 ]]; then
        if [[ -z $java_version ]]; then
            displayed_version="none"
        else
            displayed_version=$java_version
        fi
        showErrorMessage "Java 17+ required for launch, current version - $displayed_version"
    else
        jar_path="application/app.jar"
        if [[ -f "$jar_path" ]]; then
            nohup $java_command -jar $jar_path &
            sleep 1
        else
            showErrorMessage "Executable file not found"
        fi
    fi
else
    showErrorMessage "Launch configuration not found"
fi