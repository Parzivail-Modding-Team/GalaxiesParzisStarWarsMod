package dev.pswg.blockEntity;

import dev.pswg.blockEntity.screenHandler.CrateGenericSmallScreenHandler;
import dev.pswg.container.GalaxiesBlockEntities;
import dev.pswg.container.GalaxiesScreenHandlerTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class CrateCorrugatedBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory
{
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(15, ItemStack.EMPTY);
	public CrateCorrugatedBlockEntity(BlockPos pos, BlockState state)
	{
		super(GalaxiesBlockEntities.CORRUGATED_CRATE_BLOCK_ENTITY, pos, state);
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
	protected void writeData(WriteView view)
	{
		super.writeData(view);
		if (!this.writeLootTable(view))
		{
			Inventories.writeData(view, this.inventory);
		}
	}

	@Override
	protected void readData(ReadView view)
	{
		super.readData(view);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(view))
		{
			Inventories.readData(view, this.inventory);
		}
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return new CrateGenericSmallScreenHandler(GalaxiesScreenHandlerTypes.CORRUGATED, syncId, inv, this);
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
	public void onOpen(ContainerUser user)
	{
		if (user instanceof PlayerEntity player)
			generateLoot(player);
		else
			generateLoot(null);
		super.onOpen(user);
	}
}