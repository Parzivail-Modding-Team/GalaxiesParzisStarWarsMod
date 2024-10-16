package dev.pswg.api;

/**
 * Client entry point for PSWG addons
 */
public interface GalaxiesClientAddon
{
	/**
	 * Called when PSWG is accepting client resources for registration. Use this
	 * to register client-only content through the provided APIs.
	 */
	void onGalaxiesClientReady();
}
