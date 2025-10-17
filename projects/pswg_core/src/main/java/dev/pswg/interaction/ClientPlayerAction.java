package dev.pswg.interaction;

/**
 * Each type of action a player can notify the server about
 */
public enum ClientPlayerAction
{
	/**
	 * This member denotes any invalid action
	 */
	INVALID,

	/**
	 * The player has stopped left-using the current item
	 */
	RELEASE_USE_LEFT_ITEM,

	/**
	 * The player has requested the primary item action for the current item
	 */
	PRIMARY_ITEM_ACTION,
}
