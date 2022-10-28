package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.block.Sliding1x2DoorBlock;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.block.BlockEntityClientSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlidingDoorBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
	private static final int ANIMATION_TIME = 10;

	private static final byte MASK_IDLE = (byte)0b11100000;
	private static final byte MASK_DIRECTION = (byte)0b00010000;
	private static final byte MASK_TIMER = (byte)0b00001111;

	private static final byte MAX_IDLE = MASK_IDLE >> 5;

	private byte timer = MASK_DIRECTION;

	public SlidingDoorBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Door.SlidingBlockEntityType, pos, state);
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
		var state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof Sliding1x2DoorBlock))
			return false;
		return state.get(Sliding1x2DoorBlock.MOVING);
	}

	public void setMoving(boolean moving)
	{
		var state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof Sliding1x2DoorBlock))
			return;
		world.setBlockState(pos, state.with(Sliding1x2DoorBlock.MOVING, moving), Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
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

	public int getTimer()
	{
		return timer & MASK_TIMER;
	}

	public void setTimer(int newTimer)
	{
		timer &= ~MASK_TIMER;
		timer |= (newTimer & MASK_TIMER);
	}

	public int getIdleTimer()
	{
		return (timer & MASK_IDLE) >> 5;
	}

	public void setIdleTimer(int newTimer)
	{
		timer &= ~MASK_IDLE;
		timer |= (newTimer << 5) & MASK_IDLE;
	}

	public void start(boolean opening)
	{
		setTimer(ANIMATION_TIME);
		setDirection(opening);
		sync();
	}

	@Environment(EnvType.CLIENT)
	public float getAnimationTime(float tickDelta)
	{
		if (isMoving())
			return (getTimer() - tickDelta) / ANIMATION_TIME;

		return 0;
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, T be)
	{
		if (!(be instanceof SlidingDoorBlockEntity t))
			return;

		if (world == null)
			return;

		if (t.isMoving() || t.getIdleTimer() != 0)
		{
			t.setIdleTimer(Math.max(0, t.getIdleTimer() - 1));

			var timer = t.getTimer();

			if (timer == 0)
				return;

			timer--;

			if (timer <= 0)
			{
				t.setTimer(0);
				t.setMoving(false);
				t.setIdleTimer(1);
			}
			else
				t.setTimer(timer);

			be.markDirty();
		}
	}

	@Override
	public Identifier getSyncPacketId()
	{
		return SwgPackets.S2C.SyncBlockToClient;
	}

	public BlockState getOccupiedState()
	{
		return world.getBlockState(pos);
	}
}
