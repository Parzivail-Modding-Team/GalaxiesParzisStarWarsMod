package com.parzivail.pswg.api;

/**
 * Common entry point for PSWG addons
 */
public interface PswgAddon
{
	/**
	 * Called when PSWG is ready to start loading addons, but is no content
	 * has been registered. Use this to register event handlers and other
	 * things that will fire as content is registered. See {@link PswgContent}
	 * for available events.
	 */
	default void onPswgStarting()
	{
	}

	/**
	 * Called when PSWG is accepting content for registration. Use this
	 * to register content through {@link PswgContent}.
	 */
	void onPswgReady();
}
