$base='https://bpo-connect-6e9mhg39v-azars-projects-e39ca787.vercel.app'
$script:results=@()

function Add-Result($check,$status,$detail){
  $script:results += [pscustomobject]@{
    check=$check
    status=$status
    detail=$detail
  }
}

try {
  $r = Invoke-RestMethod "$base/api/tickets" -TimeoutSec 60
  Add-Result 'GET tickets via vercel' 'PASS' "count=$($r.Count)"
} catch {
  Add-Result 'GET tickets via vercel' 'FAIL' $_.Exception.Message
}

$ticketId = $null
try {
  $body = @{
    customerId='C001'
    channel='Voice'
    severity='Low'
    description='e2e verify'
    agentId='A101'
    referenceId=('E2E-'+[guid]::NewGuid().ToString('N').Substring(0,8))
  } | ConvertTo-Json

  $r = Invoke-RestMethod "$base/api/tickets" -Method POST -ContentType 'application/json' -Body $body -TimeoutSec 60
  $ticketId = $r.ticketId
  Add-Result 'POST ticket via vercel' 'PASS' "ticketId=$ticketId"
} catch {
  Add-Result 'POST ticket via vercel' 'FAIL' $_.Exception.Message
}

try {
  if (-not $ticketId) { throw 'no ticket id' }
  Invoke-RestMethod "$base/api/tickets/$ticketId/escalate" -Method POST -TimeoutSec 60 | Out-Null

  $all = Invoke-RestMethod "$base/api/tickets" -TimeoutSec 60
  $match = $all | Where-Object { $_.ticketId -eq $ticketId } | Select-Object -First 1
  if ($match -and $match.status -eq 'Escalated') {
    Add-Result 'POST escalate via vercel' 'PASS' "status=$($match.status)"
  } else {
    Add-Result 'POST escalate via vercel' 'FAIL' "status=$($match.status)"
  }
} catch {
  Add-Result 'POST escalate via vercel' 'FAIL' $_.Exception.Message
}

Write-Output 'E2E_RESULTS_START'
$script:results | ConvertTo-Json -Depth 4
Write-Output 'E2E_RESULTS_END'
