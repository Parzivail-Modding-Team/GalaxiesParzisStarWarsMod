package dev.pswg.api;

/**
 * Common entry point for all PSWG addons
 */
public interface GalaxiesAddon
{
	/**
	 * Called when PSWG is ready to start loading addons, but is no content
	 * has been registered. Use this to register event handlers and other
	 * things that will fire as content is registered.
	 * for available events.
	 */
	default void onGalaxiesStarting()
	{
	}

	/**
	 * Called when PSWG is accepting content for registration. Use this
	 * to register content through the provided APIs.
	 */
	void onGalaxiesReady();
}
