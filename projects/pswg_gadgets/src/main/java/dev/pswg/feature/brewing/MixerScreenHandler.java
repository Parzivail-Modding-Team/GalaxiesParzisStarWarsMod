package dev.pswg.feature.brewing;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsScreenHandlerTypes;
import java.util.ArrayList;
import java.util.HashMap;

import dev.pswg.container.GalaxiesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static dev.pswg.feature.brewing.MixerBlockEntity.OUTPUT_SLOT_INDEX;

public class MixerScreenHandler extends AbstractContainerMenu
{
	private final Container inventory;
	private final Inventory playerInventory;
	private final ContainerData propertyDelegate;
	private final BlockPos blockPos;
	protected final Level world;
	public ArrayList<MobEffectInstance> drinkEffects;
	public ArrayList<Integer> drinkColors;
	public ArrayList<ItemStack> drinkFoods;

	public MixerScreenHandler(int syncId, Inventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(11), BlockPos.ZERO, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	public MixerScreenHandler(int syncId, Inventory playerInventory, ArrayList<MobEffectInstance> drinkEffects, ArrayList<Integer> drinkColors, ArrayList<ItemStack> drinkFoods)
	{
		this(syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(11), BlockPos.ZERO, drinkEffects, drinkColors, drinkFoods);
	}

	public MixerScreenHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate, BlockPos pos, ArrayList<MobEffectInstance> drinkEffects, ArrayList<Integer> drinkColors, ArrayList<ItemStack> drinkFoods)
	{
		super(GadgetsScreenHandlerTypes.MIXER, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.level();
		this.propertyDelegate = propertyDelegate;
		this.blockPos = pos;
		this.drinkEffects = drinkEffects;
		this.drinkColors = drinkColors;
		this.drinkFoods = drinkFoods;
		if (world.getBlockEntity(blockPos) instanceof MixerBlockEntity mixer)
			MixerBlockEntity.sendSyncPacket(mixer);

		/// FUEL
		this.addSlot(new FuelSlot(inventory, 0, 146, 122, this));
		/// INPUT
		this.addSlot(new BrewingIngredientSlot(inventory, 1, 146, 69));
		/// OUTPUT / DRINK CONTAINER INPUT
		this.addSlot(new DrinkContainerSlot(inventory, 2, 146, 29));

		this.addStandardInventorySlots(playerInventory, 8, 174);

		this.addDataSlots(propertyDelegate);
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
		return drinkEffects.get(i).getEffect().value().getColor();
	}

	public int getDyeColor(int i)
	{
		return drinkColors.get(i);
	}

	public int getFoodColor(int i)
	{
		return MixerFoodColors.getColor(drinkFoods.get(i));
	}

	public boolean isDrinkContainerPresent() {
		return !inventory.getItem(OUTPUT_SLOT_INDEX).isEmpty() && inventory.getItem(OUTPUT_SLOT_INDEX).is(GalaxiesItems.Tags.DRINK_CONTAINER_TAG);
	}

	public boolean isOnEffectCell()
	{
		return BrewingMap.getCell(getMapX(), getMapY()) instanceof EffectCell;
	}

	public boolean wasReset()
	{
		return getMapX() == 256 && getMapY() == 256 && drinkEffects.isEmpty() && getBellowProgress() == 0;
	}

	public boolean canAddEffect()
	{
		if (BrewingMap.getCell(getMapX(), getMapY()) instanceof EffectCell effectCell)
		{
			MobEffectInstance currentEffect = effectCell.statusEffect;
			for (MobEffectInstance effectInstance : drinkEffects)
				if (effectInstance.getEffect() == currentEffect.getEffect())
					return false;
			return true;
		}
		return false;
	}

	@Override
	public boolean clickMenuButton(Player player, int id)
	{
		switch (id)
		{
			case 0:
			{
				///  BELLOW
				propertyDelegate.set(6, Math.min(getBellowProgress(), 4));
				propertyDelegate.set(4, Math.max(getBellowProgress() - 2, 0));
				return true;
			}
			case 1:
			{
				///  CONFIRM
				if (world.getBlockEntity(blockPos) instanceof MixerBlockEntity mixer)
				{
					MixerBlockEntity.craftPotion(mixer);
					if(mixer.getLevel() instanceof ServerLevel serverWorld){
						var data = BrewingDataState.getBrewingDataState(serverWorld.getServer());
						Player playerEntity = this.playerInventory.player;

						for(MobEffectInstance effect: mixer.drinkEffects)
						{
							data.getPlayerData().getOrDefault(playerEntity.getStringUUID(), new PlayerBrewingData(new HashMap<>())).foundEffects.replace(effect.getEffect(), true);
						}
					}
				}

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
				if (world.getBlockEntity(blockPos) instanceof MixerBlockEntity mixer)
					MixerBlockEntity.resetMixer(mixer);
				return true;

			}
		}
		return super.clickMenuButton(player, id);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		var itemStack = ItemStack.EMPTY;
		var slot = this.slots.get(index);
		if (slot != null && slot.hasItem())
		{
			var slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index < this.inventory.getContainerSize())
			{
				if (!this.moveItemStackTo(slotStack, this.inventory.getContainerSize(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.moveItemStackTo(slotStack, 0, this.inventory.getContainerSize(), false))
			{
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty())
			{
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}
		return itemStack;
	}

	@Override
	public boolean stillValid(Player player)
	{
		return this.inventory.stillValid(player);
	}
}
