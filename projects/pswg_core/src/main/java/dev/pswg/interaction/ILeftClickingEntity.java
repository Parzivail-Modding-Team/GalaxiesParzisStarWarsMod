package dev.pswg.interaction;

/**
 * Prescribes the functionality that entities must support in order to
 * left-use items
 */
public interface ILeftClickingEntity
{
	/**
	 * Determines if the entity is currently left-using an item
	 *
	 * @return True if the entity is currently left-using an item
	 */
	boolean isUsingItemLeft();

	/**
	 * Instructs the entity to stop left-using the current item
	 */
	void stopUsingItemLeft();
}
