package dev.pswg.feature.brewing;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;

import static dev.pswg.feature.brewing.MixerBlockEntity.OUTPUT_SLOT_INDEX;

public class MixerScreenHandler extends ScreenHandler
{
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	private final PropertyDelegate propertyDelegate;
	private final BlockPos blockPos;
	protected final World world;
	public ArrayList<StatusEffectInstance> drinkEffects;

	public MixerScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(11), BlockPos.ORIGIN, new ArrayList<>());
	}

	public MixerScreenHandler(int syncId, PlayerInventory playerInventory, ArrayList<StatusEffectInstance> drinkEffects)
	{
		this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(11), BlockPos.ORIGIN, drinkEffects);
	}

	public MixerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, BlockPos pos, ArrayList<StatusEffectInstance> drinkEffects)
	{
		super(GadgetsScreenHandlerTypes.MIXER, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.propertyDelegate = propertyDelegate;
		this.blockPos = pos;
		this.drinkEffects = drinkEffects;
		if (world.getBlockEntity(blockPos) instanceof MixerBlockEntity mixer)
			MixerBlockEntity.sendSyncPacket(mixer);

		/// FUEL
		this.addSlot(new FuelSlot(inventory, 0, 146, 122, this));
		/// INPUT
		this.addSlot(new BrewingIngredientSlot(inventory, 1, 146, 69));
		/// OUTPUT / DRINK CONTAINER INPUT
		this.addSlot(new DrinkContainerSlot(inventory, 2, 146, 29));

		this.addPlayerSlots(playerInventory, 8, 174);

		this.addProperties(propertyDelegate);
	}

	public float getMapX()
	{
		float val = propertyDelegate.get(0) / 10f;
		if (val < 0)
		{
			Gadgets.LOGGER.warn("map X lower then 0, val equal to: " + val + " delegated prop equal: " + propertyDelegate.get(0) + ", setting to: " + val * -1);
			val = val * -1;
		}
		return val;
	}

	public float getMapY()
	{
		float val = propertyDelegate.get(1) / 10f;
		if (val < 0)
		{
			Gadgets.LOGGER.warn("map Y lower then 0, val equal to: " + val + " delegated prop equal: " + propertyDelegate.get(1) + ", setting to: " + val * -1);
			val = val * -1;
		}
		return val;
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

	public int getEffectCount()
	{
		return drinkEffects.size();
	}

	public int getEffectColor(int i)
	{
		return drinkEffects.get(i).getEffectType().value().getColor();
	}

	public boolean isDrinkContainerPresent() {
		return !inventory.getStack(OUTPUT_SLOT_INDEX).isEmpty() && inventory.getStack(OUTPUT_SLOT_INDEX).isIn(GadgetsItems.Tags.DRINK_CONTAINER_TAG);
	}

	public boolean isOnEffectCell()
	{
		return BrewingMap.getCell(getMapX(), getMapY()) instanceof EffectCell;
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id)
	{
		switch (id)
		{
			case 0:
			{
				/// BELLOW
				propertyDelegate.set(6, Math.min(getBellowProgress(), 4));
				propertyDelegate.set(4, Math.max(getBellowProgress() - 2, 0));
				return true;
			}
			case 1:
			{
				///  CONFIRM
				if (world.getBlockEntity(blockPos) instanceof MixerBlockEntity mixer)
					MixerBlockEntity.craftPotion(mixer);
				return true;
			}
			case 2:
			{
				///  ADD EFFECT
				if (world.getBlockEntity(blockPos) instanceof MixerBlockEntity mixer)
					MixerBlockEntity.tryAddEffect(mixer);
				return true;
			}
			case 3:
			{
				/// CANCEL

			}
		}
		return super.onButtonClick(player, id);
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasStack())
		{
			var slotStack = slot.getStack();
			itemStack = slotStack.copy();
			if (index < this.inventory.size())
			{
				if (!this.insertItem(slotStack, this.inventory.size(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(slotStack, 0, this.inventory.size(), false))
			{
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty())
			{
				slot.setStack(ItemStack.EMPTY);
			}
			else
			{
				slot.markDirty();
			}
		}
		return itemStack;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
