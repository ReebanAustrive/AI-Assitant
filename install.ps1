# Create directory
New-Item -ItemType Directory -Force -Path "$HOME\.ccms\bin"

# Download JAR
Invoke-WebRequest -Uri "https://github.com/ReebanAustrive/AI-Assitant-Micro-Saas/releases/latest/download/ccms.jar" `
    -OutFile "$HOME\.ccms\bin\ccms.jar"

@"
@echo off
java -jar "%USERPROFILE%\.ccms\bin\ccms.jar" %*
"@ | Out-File -FilePath "$HOME\.ccms\bin\ccms.bat" -Encoding ASCII

# Add to PATH
$path = [Environment]::GetEnvironmentVariable("PATH", "User")
if ($path -notlike "*\.ccms\bin*") {
    [Environment]::SetEnvironmentVariable("PATH", "$path;$HOME\.ccms\bin", "User")
}

Write-Host "CCMS CLI installed successfully!"
Write-Host "Open a new terminal and run: ccms init --repo <url> --arch <path>"
