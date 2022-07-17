package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.util.block.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PowerCouplingBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
	private int syncTimer = 0;
	private final Set<BlockPos> targetPositions = new HashSet<>();
	private final HashMap<BlockPos, Integer> removalQueue = new HashMap<>();

	public PowerCouplingBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Power.CouplingBlockEntityType, pos, state);
	}

	@Override
	public void writeNbt(NbtCompound tag)
	{
		var targets = new NbtList();
		for (var pos : this.targetPositions)
			targets.add(NbtLong.of(pos.asLong()));

		tag.put("targets", targets);

		super.writeNbt(tag);
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		targetPositions.clear();

		var targets = tag.getList("targets", NbtElement.LONG_TYPE);
		for (var target : targets)
			targetPositions.add(BlockPos.fromLong(((NbtLong)target).longValue()));

		if (world != null && !world.isClient)
			syncTimer = 10;

		super.readNbt(tag);
	}

	private void removeFrom(BlockPos pos)
	{
		targetPositions.remove(pos.subtract(this.pos));
		sync();
	}

	public boolean attachTo(World world, BlockPos pos)
	{
		if (!(world.getBlockEntity(pos) instanceof PowerCouplingBlockEntity))
			return false;

		targetPositions.add(pos.subtract(this.pos));
		sync();
		markDirty();
		return true;
	}

	@Override
	public NbtCompound toInitialChunkDataNbt()
	{
		return toClientTag(super.toInitialChunkDataNbt());
	}

	@Override
	public void fromClientTag(NbtCompound tag)
	{
		readNbt(tag);
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag)
	{
		writeNbt(tag);
		return tag;
	}

	public Set<BlockPos> getTargets()
	{
		return targetPositions;
	}

	public static <T extends BlockEntity> void serverTick(World world, BlockPos blockPos, BlockState state, T be)
	{
		if (!(be instanceof PowerCouplingBlockEntity t))
			return;

		var invalidTargets = t.getTargets().stream().filter(pos -> {
			var targetState = world.getBlockState(pos.add(t.pos));
			return !targetState.isOf(SwgBlocks.Power.Coupling);
		}).toList();

		for (var target : invalidTargets)
		{
			if (!t.removalQueue.containsKey(target))
				t.removalQueue.put(target, 10);

			var timer = t.removalQueue.get(target);

			if (timer == 0)
				t.removeFrom(target.add(t.pos));
			else
				t.removalQueue.put(target, timer - 1);
		}

		// Remove all positions from the queue which are not currently invalid
		t.removalQueue.keySet().retainAll(t.removalQueue.keySet().stream().filter(invalidTargets::contains).toList());

		if (t.syncTimer > 0)
		{
			t.syncTimer--;
			if (t.syncTimer == 0)
			{
				t.sync();
				t.markDirty();
			}
		}
	}

	@Override
	public Identifier getSyncPacketId()
	{
		return SwgPackets.S2C.SyncBlockToClient;
	}
}
