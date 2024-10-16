package dev.pswg.configuration;

/**
 * The configuration values native to PSWG core
 */
public final class GalaxiesConfig
{
	/**
	 * Whether the user has allowed PSWG and addons to ask
	 * the user if they want their crash automatically reported
	 * to the development team via Rollbar
	 */
	public boolean askToSendCrashReports = true;

	/**
	 * Whether the user has requested that PSWG and addons not
	 * notify them for updates, or search for them over the internet
	 */
	public boolean isUpdateCheckingDisabled = false;
}
