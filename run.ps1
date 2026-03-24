param(
  [switch]$SkipTests = $true,
  [switch]$Docker = $false
)

if ($Docker) {
  Write-Host "Building and starting with Docker Compose..." -ForegroundColor Cyan
  docker-compose up --build
} else {
  Write-Host "Building project..." -ForegroundColor Cyan
  $testFlag = if ($SkipTests) { "-DskipTests" } else { "" }
  mvn clean package $testFlag

  if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed."
    exit $LASTEXITCODE
  }

  Write-Host "Starting Spring Boot application..." -ForegroundColor Cyan
  mvn spring-boot:run
}
