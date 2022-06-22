
[CmdletBinding()]
param (
    [Parameter(ParameterSetName = "Default", Mandatory, Position = 0, HelpMessage = "Fetch endpoint")]
    [string]$FetchEndpoint
)
function Run()
{
    $command, $arguments = $args
    Write-Host "`n==> $command $arguments`n" -ForegroundColor Green
    & $command $arguments
    if ($LASTEXITCODE) {
        Write-Error "Command '$args' failed with code: $LASTEXITCODE" -ErrorAction 'Continue'
    }
}

function RunOrExitOnFailure()
{
    Run @args
    if ($LASTEXITCODE) {
        exit $LASTEXITCODE
    }
}

function CallFetchEndpointForEver() {
    for (;;) {
        # Do 50 concurrent api-calls for 600 sec (10 mins) using abs tool
        # https://www.apachehaus.com/cgi-bin/download.plx
        RunOrExitOnFailure abs `
        -t 600 `
        -n 500000000 `
        -c 50 `
        $FetchEndpoint

        # then no api-calls for 600 sec (10 mins)
        Start-Sleep -Second 600
        # repeat
    }
}

function main() {
    CallFetchEndpointForEver
}

main