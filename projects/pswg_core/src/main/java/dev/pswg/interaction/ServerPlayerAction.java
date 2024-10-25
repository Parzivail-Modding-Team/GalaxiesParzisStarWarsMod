package dev.pswg.interaction;

/**
 * Each type of action the server can notify a player about
 */
public enum ServerPlayerAction
{
	/**
	 * This member denotes any invalid action
	 */
	INVALID,

	/**
	 * The left-using item has been consumed
	 */
	CONSUME_LEFT_ITEM
}
