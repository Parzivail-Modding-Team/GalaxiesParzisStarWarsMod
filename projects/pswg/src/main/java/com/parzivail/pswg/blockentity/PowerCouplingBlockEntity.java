package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PowerCouplingBlockEntity extends BlockEntity
{
	private int syncTimer = 0;
	private final Set<BlockPos> targetPositions = new HashSet<>();
	private final HashMap<BlockPos, Integer> removalQueue = new HashMap<>();

	public PowerCouplingBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Power.CouplingBlockEntityType, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.writeNbt(nbt, registryLookup);

		nbt.put("targets", new NbtLongArray(this.targetPositions.stream().mapToLong(BlockPos::asLong).toArray()));
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.readNbt(nbt, registryLookup);

		targetPositions.clear();

		// TODO: old code for migrating old data, remove at some point
		{
			var targets = nbt.getList("targets", NbtElement.LONG_TYPE);
			for (var target : targets)
				targetPositions.add(BlockPos.fromLong(((NbtLong)target).longValue()));
		}
		var targets = nbt.getLongArray("targets");
		for (var target: targets) {
			targetPositions.add(BlockPos.fromLong(target));
		}

		if (world != null && !world.isClient)
			syncTimer = 10;
	}

	private void removeFrom(BlockPos pos)
	{
		targetPositions.remove(pos.subtract(this.pos));
		world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL_AND_REDRAW);
		markDirty();
	}

	public boolean attachTo(World world, BlockPos pos)
	{
		if (!(world.getBlockEntity(pos) instanceof PowerCouplingBlockEntity))
			return false;

		targetPositions.add(pos.subtract(this.pos));
		world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL_AND_REDRAW);
		markDirty();
		return true;
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket()
	{
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup)
	{
		return createComponentlessNbt(registryLookup);
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

		// TODO: why does a freshly loaded block need a delayed sync?
		if (t.syncTimer > 0)
		{
			t.syncTimer--;
			if (t.syncTimer == 0)
			{
				world.updateListeners(t.pos, t.getCachedState(), t.getCachedState(), Block.NOTIFY_ALL);
				t.markDirty();
			}
		}
	}
}
