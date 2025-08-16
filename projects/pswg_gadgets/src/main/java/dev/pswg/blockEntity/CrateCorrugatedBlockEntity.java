package dev.pswg.blockEntity;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.screenHandler.CrateGenericSmallScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class CrateCorrugatedBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory
{
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(15, ItemStack.EMPTY);
	public CrateCorrugatedBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.CORRUGATED_CRATE_BLOCK_ENTITY, pos, state);
	}

	@Override
	public Text getDisplayName()
	{
		return Text.literal("Corrugated Crate");
	}

	@Override
	protected Text getContainerName()
	{
		return Text.of("Corrugated Crate");
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks()
	{
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory)
	{
		this.inventory = inventory;
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
	{
		super.writeNbt(nbt, registries);
		if (!this.writeLootTable(nbt))
		{
			Inventories.writeNbt(nbt, this.inventory, registries);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
	{
		super.readNbt(nbt, registries);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(nbt))
		{
			Inventories.readNbt(nbt, this.inventory, registries);
		}
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateGenericSmallScreenHandler(GadgetsScreenHandlerTypes.CORRUGATED, syncId, inv, this);
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return createMenu(syncId, playerInventory, playerInventory.player);
	}

	@Override
	public int size()
	{
		return 15;
	}

	@Override
	public void onOpen(PlayerEntity player)
	{
		generateLoot(player);
		super.onOpen(player);
	}
}