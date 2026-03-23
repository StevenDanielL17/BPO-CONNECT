param(
  [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

Write-Host "Running smoke tests against $BaseUrl" -ForegroundColor Cyan

function Assert-True($condition, $message) {
  if (-not $condition) {
    throw "FAILED: $message"
  }
}

# 1. Login
$loginBody = @{ username = "agent_sarah"; password = "pass123" } | ConvertTo-Json
$loginResp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/users/login" -ContentType "application/json" -Body $loginBody
Assert-True ($loginResp -eq "Login Successful") "Login should succeed for seed user"
Write-Host "PASS: Login" -ForegroundColor Green

# 2. KB search
$kbResp = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/kb/search?query=Password"
Assert-True ($kbResp.Count -ge 1) "KB search should return at least one article"
Write-Host "PASS: Knowledge base search" -ForegroundColor Green

# 3. Create ticket
$ticketBody = @{
  customerId = "C001"
  channel = "Voice"
  severity = "High"
  description = "Smoke test ticket"
  agentId = "A101"
  referenceId = "REF-SMOKE"
} | ConvertTo-Json
$ticketResp = Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/tickets" -ContentType "application/json" -Body $ticketBody
Assert-True ([string]::IsNullOrWhiteSpace($ticketResp.ticketId) -eq $false) "Ticket ID should be present"
Write-Host "PASS: Ticket creation" -ForegroundColor Green

# 4. List tickets
$listResp = Invoke-RestMethod -Method Get -Uri "$BaseUrl/api/tickets"
Assert-True ($listResp.Count -ge 1) "Ticket list should contain entries"
Write-Host "PASS: Ticket list" -ForegroundColor Green

# 5. Escalate first ticket
$ticketId = $ticketResp.ticketId
Invoke-RestMethod -Method Post -Uri "$BaseUrl/api/tickets/$ticketId/escalate" | Out-Null
Write-Host "PASS: Ticket escalation endpoint" -ForegroundColor Green

Write-Host "All smoke tests passed." -ForegroundColor Cyan
