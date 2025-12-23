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

	/**
	 * Whether mouse movements should be scaled with field of
	 * view, e.g., slowing down the mouse inputs when aiming-down-
	 * sights with a blaster
	 */
	public boolean scaleMouseWithFieldOfView = true;
}
