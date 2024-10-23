package dev.pswg.interaction;

/**
 * Each type of action a player can notify the server about
 */
public enum PlayerAction
{
	/**
	 * This member denotes any invalid action
	 */
	INVALID,

	/**
	 * The player has stopped left-using the current item
	 */
	RELEASE_USE_LEFT_ITEM
}
