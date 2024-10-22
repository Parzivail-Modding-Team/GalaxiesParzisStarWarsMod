package dev.pswg.interaction;

public interface ILivingEntityLeftClickSupport
{
	boolean galaxies$isUsingItemLeft();

	void galaxies$stopUsingItemLeft();

	void galaxies$setLeftFlag(int mask, boolean value);

	int galaxies$getItemUseLeftTimeLeft();

	void galaxies$consumeItemLeft();
}
