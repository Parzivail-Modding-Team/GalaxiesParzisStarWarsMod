package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class TatooineHomeDoorBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable
{
	private static final int ANIMATION_TIME = 15;

	private static final byte MASK_MOVING = (byte)0b10000000;
	private static final byte MASK_DIRECTION = (byte)0b01000000;
	private static final byte MASK_POWERED = (byte)0b00100000;
	private static final byte MASK_TIMER = (byte)0b00011111;
	private byte timer = 0;

	public TatooineHomeDoorBlockEntity()
	{
		super(SwgBlocks.Door.TatooineHomeBlockEntityType);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		super.toTag(tag);

		tag.putByte("timer", timer);

		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);

		timer = tag.getByte("timer");
	}

	@Override
	public void fromClientTag(CompoundTag compoundTag)
	{
		timer = compoundTag.getByte("timer");
	}

	@Override
	public CompoundTag toClientTag(CompoundTag compoundTag)
	{
		compoundTag.putByte("timer", timer);

		return compoundTag;
	}

	public boolean isMoving()
	{
		return (timer & MASK_MOVING) != 0;
	}

	public void setMoving(boolean moving)
	{
		timer &= ~MASK_MOVING;
		if (moving)
			timer |= MASK_MOVING;

		this.markDirty();
	}

	public boolean isOpening()
	{
		return (timer & MASK_DIRECTION) != 0;
	}

	public void setDirection(boolean opening)
	{
		timer &= ~MASK_DIRECTION;
		if (opening)
			timer |= MASK_DIRECTION;
	}

	public void setPowered(boolean powered)
	{
		timer &= ~MASK_POWERED;
		if (powered)
			timer |= MASK_POWERED;
	}

	public boolean isPowered()
	{
		return (timer & MASK_POWERED) != 0;
	}

	public int getTimer()
	{
		return timer & MASK_TIMER;
	}

	public void setTimer(int newTimer)
	{
		timer &= ~MASK_TIMER;
		timer |= (newTimer & MASK_TIMER);
	}

	public void startMoving()
	{
		setTimer(ANIMATION_TIME);
		setMoving(true);
	}

	@Environment(EnvType.CLIENT)
	public float getAnimationTime(float tickDelta)
	{
		if (isMoving())
			return (getTimer() - tickDelta + 1) / ANIMATION_TIME;

		return 1;
	}

	@Override
	public void tick()
	{
		if (isMoving())
		{
			int timer = getTimer();

			if (timer == 0)
				return;

			timer--;

			if (timer <= 0)
			{
				boolean opening = isOpening();
				setDirection(!opening);

				setTimer(0);
				setMoving(false);
			}
			else
				setTimer(timer);

			if (!world.isClient)
				sync();
		}
	}
}
