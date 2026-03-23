$base='https://bpo-connect-backend.onrender.com'
$script:results=@()

function Add-Result($name,$ok,$detail){
  $script:results += [pscustomobject]@{
    Check = $name
    Status = if($ok){'PASS'}else{'FAIL'}
    Detail = $detail
  }
}

try {
  $r = Invoke-RestMethod -Uri "$base/api/tickets" -Method GET -TimeoutSec 60
  $ok = $r -is [System.Array]
  Add-Result '1) GET /api/tickets returns array' $ok "type=$($r.GetType().Name)"
} catch {
  Add-Result '1) GET /api/tickets returns array' $false $_.Exception.Message
}

try {
  $body = @{ username='agent_sarah'; password='pass123' } | ConvertTo-Json
  $r = Invoke-RestMethod -Uri "$base/api/users/login" -Method POST -ContentType 'application/json' -Body $body -TimeoutSec 60
  $ok = ($r -eq 'Login Successful')
  Add-Result '2) POST /api/users/login valid creds' $ok "response=$r"
} catch {
  Add-Result '2) POST /api/users/login valid creds' $false $_.Exception.Message
}

try {
  $r = Invoke-RestMethod -Uri "$base/api/kb/search?query=Password" -Method GET -TimeoutSec 60
  $ok = ($r -is [System.Array]) -and ($r.Count -ge 1)
  Add-Result '3) GET /api/kb/search finds article' $ok "count=$($r.Count)"
} catch {
  Add-Result '3) GET /api/kb/search finds article' $false $_.Exception.Message
}

$ticketId = $null
try {
  $body = @{
    customerId='C001'
    channel='Voice'
    severity='Low'
    description='Render prod smoke test'
    agentId='A101'
    referenceId=('SMOKE-' + [guid]::NewGuid().ToString('N').Substring(0,8))
  } | ConvertTo-Json

  $r = Invoke-RestMethod -Uri "$base/api/tickets" -Method POST -ContentType 'application/json' -Body $body -TimeoutSec 60
  $ticketId = $r.ticketId
  $ok = -not [string]::IsNullOrWhiteSpace($ticketId)
  Add-Result '4) POST /api/tickets creates ticket' $ok "ticketId=$ticketId"
} catch {
  Add-Result '4) POST /api/tickets creates ticket' $false $_.Exception.Message
}

try {
  if ([string]::IsNullOrWhiteSpace($ticketId)) { throw 'Ticket ID missing from previous step' }
  Invoke-RestMethod -Uri "$base/api/tickets/$ticketId/status" -Method PUT -ContentType 'application/json' -Body (@{status='Resolved'}|ConvertTo-Json) -TimeoutSec 60 | Out-Null
  $tickets = Invoke-RestMethod -Uri "$base/api/tickets" -Method GET -TimeoutSec 60
  $match = $tickets | Where-Object { $_.ticketId -eq $ticketId } | Select-Object -First 1
  $ok = ($null -ne $match) -and ($match.status -eq 'Resolved')
  Add-Result '5) PUT /api/tickets/{id}/status persists' $ok "status=$($match.status)"
} catch {
  Add-Result '5) PUT /api/tickets/{id}/status persists' $false $_.Exception.Message
}

Write-Output "SMOKE_RESULTS_START"
$script:results | ConvertTo-Json -Depth 4
Write-Output "SMOKE_RESULTS_END"
