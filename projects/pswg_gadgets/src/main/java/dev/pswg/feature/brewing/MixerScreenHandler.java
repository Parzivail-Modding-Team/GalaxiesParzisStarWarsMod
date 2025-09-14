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
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(6));
	}

	public MixerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(GadgetsScreenHandlerTypes.MIXER, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.propertyDelegate = propertyDelegate;

		/// FUEL
		this.addSlot(new Slot(inventory, 0, 140, 131));
		/// INPUT
		this.addSlot(new Slot(inventory, 1, 140, 72));
		/// OUTPUT
		this.addSlot(new OutputSlot(inventory, 2, 140, 26));

		this.addPlayerSlots(playerInventory, 8, 174);

		this.addProperties(propertyDelegate);
	}

	public float getMapX()
	{
		return propertyDelegate.get(0) / 100f;
	}

	public float getMapY()
	{
		return propertyDelegate.get(1) / 100f;
	}

	public int getLitTimeRemaining()
	{
		return propertyDelegate.get(2);
	}

	public int getLitTimeTotal()
	{
		return propertyDelegate.get(3);
	}

	public int getBellowProgress()
	{
		return propertyDelegate.get(4);
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id)
	{
		if (id == 0)
		{
			if (getLitTimeRemaining() != 0)
				propertyDelegate.set(4, Math.min(getBellowProgress() + 15, MixerBlockEntity.MAX_BELLOW_PROGRESS));
			return true;
		}
		return super.onButtonClick(player, id);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
