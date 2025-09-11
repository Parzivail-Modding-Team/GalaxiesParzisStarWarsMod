package dev.pswg.feature.brewing;

import dev.pswg.container.GadgetsScreenHandlerTypes;
import dev.pswg.feature.scrapping.table.OutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class MixerScreenHandler extends ScreenHandler
{
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;

	public MixerScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(10), new ArrayPropertyDelegate(3));
	}

	public MixerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(GadgetsScreenHandlerTypes.MIXER, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.propertyDelegate = propertyDelegate;

		/// FUEL
		this.addSlot(new Slot(inventory, 0, 139, 130));
		/// INPUT
		this.addSlot(new Slot(inventory, 1, 139, 71));
		/// OUTPUT
		this.addSlot(new OutputSlot(inventory, 2, 139, 25));

		this.addPlayerSlots(playerInventory, 8, 124);
		this.addProperties(propertyDelegate);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id)
	{
		if (id == 0)
		{
			propertyDelegate.set(5, Math.min(propertyDelegate.get(5) + 8, MixerBlockEntity.MAX_BELLOW_PROGRESS));
			return true;
		}
		return super.onButtonClick(player, id);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot)
	{
		return null;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
