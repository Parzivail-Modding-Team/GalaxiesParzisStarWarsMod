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

	/**
	 * Called when client PSWG is finalizing, and all addons have been loaded. Use
	 * this to freeze registries after all client content has been registered.
	 */
	default void onGalaxiesFinalizing()
	{
	}
}
