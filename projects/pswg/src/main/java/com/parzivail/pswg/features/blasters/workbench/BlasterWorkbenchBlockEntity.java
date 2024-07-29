package com.parzivail.pswg.features.blasters.workbench;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.util.blockentity.InventoryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class BlasterWorkbenchBlockEntity extends InventoryBlockEntity implements NamedScreenHandlerFactory
{
	public static final int SLOT_BLASTER = 0;
	public static final int SLOT_1 = 1;
	public static final int SLOT_ROD = 2;
	public static final int SLOT_PLATE = 3;
	public static final int SLOT_COIL = 4;
	public static final int SLOT_BOX = 5;

	public BlasterWorkbenchBlockEntity(BlockPos pos, BlockState state)
	{
		super(SwgBlocks.Workbench.BlasterBlockEntityType, pos, state, 6);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.translatable(Resources.container(SwgScreenTypes.Workbench.Blaster));
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new BlasterWorkbenchScreenHandler(syncId, inv, this);
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

	@Override
	public void markDirty()
	{
		super.markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), BlasterWorkbenchBlock.NOTIFY_ALL);
	}

	@Override
	public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup)
	{
		super.readNbt(tag, registryLookup);
	}
}
