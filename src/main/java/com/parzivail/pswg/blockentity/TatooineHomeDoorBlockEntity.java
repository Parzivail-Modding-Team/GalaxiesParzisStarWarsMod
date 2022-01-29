package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.util.block.BlockEntityClientSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TatooineHomeDoorBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
	private static final int ANIMATION_TIME = 10;

	private static final byte MASK_MOVING = (byte)0b10000000;
	private static final byte MASK_DIRECTION = (byte)0b01000000;
	private static final byte MASK_POWERED = (byte)0b00100000;
	private static final byte MASK_TIMER = (byte)0b00011111;
	private byte timer = 0;

	public TatooineHomeDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Door.TatooineHomeBlockEntityType, pos, state);
	}

	@Override
	public void writeNbt(NbtCompound tag)
	{
		super.writeNbt(tag);

		tag.putByte("timer", timer);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt()
	{
		return toClientTag(super.toInitialChunkDataNbt());
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		super.readNbt(tag);

		timer = tag.getByte("timer");
	}

	@Override
	public void fromClientTag(NbtCompound compoundTag)
	{
		timer = compoundTag.getByte("timer");
	}

	@Override
	public NbtCompound toClientTag(NbtCompound compoundTag)
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
		sync();
	}

	@Environment(EnvType.CLIENT)
	public float getAnimationTime(float tickDelta)
	{
		if (isMoving())
			return (getTimer() - tickDelta) / ANIMATION_TIME;

		return 1;
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T be)
	{
		if (!(be instanceof TatooineHomeDoorBlockEntity t))
			return;

		if (world == null)
			return;

		if (t.isMoving())
		{
			var timer = t.getTimer();

			if (timer == 0)
				return;
			else if (timer == ANIMATION_TIME - 1 && world.isClient)
				world.playSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SwgSounds.Door.PNEUMATIC, SoundCategory.BLOCKS, 1, 1, true);

			timer--;

			if (timer <= 0)
			{
				var opening = t.isOpening();
				t.setDirection(!opening);

				t.setTimer(0);
				t.setMoving(false);

				t.sync();
			}
			else
				t.setTimer(timer);

			be.markDirty();
		}
	}
}
