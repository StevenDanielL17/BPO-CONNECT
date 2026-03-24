# Setup script for BPOConnect Project
# This script uses Chocolatey to install the required dependencies.

# Check for administrative privileges
if (-not ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Warning "Please run this script as an Administrator."
    exit
}

# Check if Chocolatey is installed
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Write-Host "Chocolatey not found. Installing Chocolatey..."
    Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

Write-Host "Installing project dependencies..."

# Install OpenJDK 17
Write-Host "Installing OpenJDK 17..."
choco install openjdk17 -y

# Install Maven
Write-Host "Installing Maven..."
choco install maven -y

# Install k6 (optional for stress testing)
Write-Host "Installing k6..."
choco install k6 -y

# Install Docker Desktop (info)
Write-Host "Note: Docker Desktop needs to be installed manually from https://www.docker.com/products/docker-desktop"

Write-Host "Installation complete. Please restart your terminal for changes to take effect."
