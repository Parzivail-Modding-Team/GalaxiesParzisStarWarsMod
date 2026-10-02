param(
	[Parameter(ValueFromRemainingArguments = $true)]
	[string[]]$Arguments
)

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$BinDir = Join-Path $ScriptDir "bin"
$ConfigPath = Join-Path $ScriptDir "toolchain.properties"
$JarPath = $env:TOOLCHAIN_JAR
$ToolchainVersion = $env:TOOLCHAIN_VERSION
$ToolchainRepository = $env:TOOLCHAIN_REPOSITORY

function Get-LatestLocalJar
{
	if (-not (Test-Path $BinDir))
	{
		return $null
	}

	return Get-ChildItem -Path $BinDir -File -Filter "toolchain-*.jar" |
		Sort-Object LastWriteTimeUtc -Descending |
		Select-Object -First 1
}

function Read-ToolchainProperties
{
	if (-not (Test-Path $ConfigPath))
	{
		return
	}

	$properties = Get-Content $ConfigPath |
		Where-Object { $_.Trim() -and -not $_.TrimStart().StartsWith("#") } |
		Out-String |
		ConvertFrom-StringData

	if (-not $ToolchainVersion -and $properties.ContainsKey("version"))
	{
		$script:ToolchainVersion = $properties["version"].Trim()
	}

	if (-not $ToolchainRepository -and $properties.ContainsKey("repository"))
	{
		$script:ToolchainRepository = $properties["repository"].Trim()
	}
}

function Download-ToolchainJar
{
	param(
		[string]$Repository,
		[string]$Version
	)

	$target = Join-Path $BinDir "toolchain-$Version.jar"
	$url = "https://github.com/$Repository/releases/download/$Version/toolchain-$Version.jar"

	New-Item -ItemType Directory -Path $BinDir -Force | Out-Null

	$previousProgressPreference = $ProgressPreference
	$ProgressPreference = 'SilentlyContinue'

	try
	{
		Invoke-WebRequest -Uri $url -OutFile $target
	}
	catch
	{
		Remove-Item -Force -ErrorAction SilentlyContinue $target
		throw "Failed to download toolchain jar from $url"
	}
	finally
	{
		$ProgressPreference = $previousProgressPreference
	}

	if (-not (Test-Path $target))
	{
		throw "Failed to download toolchain jar from $url"
	}

	return $target
}

function Resolve-ToolchainJar
{
	if ($JarPath)
	{
		if (-not (Test-Path $JarPath))
		{
			throw "Configured toolchain jar was not found: $JarPath"
		}

		return $JarPath
	}

	Read-ToolchainProperties

	if ($ToolchainVersion)
	{
		$targetJar = Join-Path $BinDir "toolchain-$ToolchainVersion.jar"

		if (Test-Path $targetJar)
		{
			return $targetJar
		}

		if ($ToolchainRepository)
		{
			return Download-ToolchainJar -Repository $ToolchainRepository -Version $ToolchainVersion
		}

		throw "toolchain.properties defines version '$ToolchainVersion' but no repository. Add 'repository = owner/repo' to enable downloads."
	}

	$latestLocalJar = Get-LatestLocalJar

	if ($latestLocalJar)
	{
		return $latestLocalJar.FullName
	}

	throw "Could not find a local toolchain jar in $BinDir. Create toolchain.properties with repository/version or build one with '.\gradlew.bat toolchainJar'."
}

$resolvedJar = Resolve-ToolchainJar
$javaOptions = @()

if ($env:TOOLCHAIN_JAVA_OPTS)
{
	$javaOptions += $env:TOOLCHAIN_JAVA_OPTS -split '\s+'
}

Set-Location $ScriptDir
& java @javaOptions -jar $resolvedJar @Arguments
exit $LASTEXITCODE
