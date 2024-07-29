package com.parzivail.pswg.blockentity;

import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.util.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreatureCageBlockEntity extends BlockEntity
{
	private NbtCompound containedEntityData;
	private Entity containedEntity;

	public CreatureCageBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Cage.CreatureCageBlockEntityType, pos, state);
	}

	@Override
	public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.writeNbt(tag, registryLookup);
		if (containedEntity != null)
			tag.put("containedEntity", EntityUtil.serializeEntity(containedEntity));
		else if (containedEntityData != null)
			tag.put("containedEntity", containedEntityData);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.readNbt(tag, registryLookup);

		if (tag.contains("containedEntity"))
			containedEntityData = tag.getCompound("containedEntity");
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

	public boolean hasContainedEntity()
	{
		return getContainedEntity() != null;
	}

	public Entity getContainedEntity()
	{
		if (containedEntity == null && containedEntityData != null && world != null)
			setContainedEntity(EntityType.getEntityFromNbt(containedEntityData, this.world).orElse(null));
		return containedEntity;
	}

	public void setContainedEntity(Entity e)
	{
		this.containedEntity = e;
		world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL_AND_REDRAW);
		markDirty();
	}

	public static <T extends BlockEntity> void serverTick(World world, BlockPos blockPos, BlockState state, T be)
	{
		if (!(be instanceof CreatureCageBlockEntity t))
		{
		}

//		t.tickEntity();
	}

	public static <T extends BlockEntity> void clientTick(World world, BlockPos blockPos, BlockState state, T be)
	{
		if (!(be instanceof CreatureCageBlockEntity t))
		{
		}

//		t.tickEntity();
	}

	private void tickEntity()
	{
		var entity = getContainedEntity();
		if (entity != null)
			entity.tick();
	}
}
