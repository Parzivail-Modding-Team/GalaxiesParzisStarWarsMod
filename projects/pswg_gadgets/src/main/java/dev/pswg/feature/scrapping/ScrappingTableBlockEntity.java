package dev.pswg.feature.scrapping;

import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ScrappingTableBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeInputProvider
{
	protected static final int CUTTER_SLOT_INDEX = 0;
	protected static final int SPANNER_SLOT_INDEX = 1;
	protected static final int CALIBRATOR_SLOT_INDEX = 2;
	protected static final int INPUT_SLOT_INDEX = 3;
	protected static final int OUTPUT_1_SLOT_INDEX = 4;
	protected static final int OUTPUT_2_SLOT_INDEX = 5;
	protected static final int OUTPUT_3_SLOT_INDEX = 6;
	protected static final int OUTPUT_4_SLOT_INDEX = 7;
	private static final int[] OUTPUT_SLOTS = new int[] { 4, 5, 6, 7 };

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);

	int cutterProgress;
	int spannerProgress;
	int calibratorProgress;

	private final ServerRecipeManager.MatchGetter<ScrappingTableRecipeInput, ? extends ScrappingTableRecipe> matchGetter;

	protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
	{
		@Override
		public int get(int index)
		{
			return switch (index)
			{
				case 0 -> cutterProgress;
				case 1 -> spannerProgress;
				case 2 -> calibratorProgress;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value)
		{
			switch (index)
			{
				case 0:
					cutterProgress = value;
					break;
				case 1:
					spannerProgress = value;
					break;
				case 2:
					calibratorProgress = value;
			}
		}

		@Override
		public int size()
		{
			return 4;
		}
	};

	public ScrappingTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.SCRAPPING_TABLE_BLOCK_ENTITY, pos, state);
		this.matchGetter = ServerRecipeManager.createCachedMatchGetter(GadgetsRecipeTypes.SCRAPPING);
	}

	@Override
	protected Text getContainerName()
	{
		return Text.literal("Scrapping Table");
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
	{
		super.readNbt(nbt, registries);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory, registries);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
	{
		super.writeNbt(nbt, registries);
		Inventories.writeNbt(nbt, this.inventory, registries);
	}

	public void tick()
	{

	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks()
	{
		return inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory)
	{
		this.inventory = inventory;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack)
	{
		if (Arrays.stream(OUTPUT_SLOTS).anyMatch(value -> value == slot))
			return false;
		if (slot == INPUT_SLOT_INDEX)
			return stack.isIn(GadgetsItems.Tags.SCRAP_TAG);
		if (slot == CUTTER_SLOT_INDEX)
			return stack.isOf(GadgetsItems.CUTTER_ITEM);
		if (slot == CALIBRATOR_SLOT_INDEX)
			return stack.isOf(GadgetsItems.CALIBRATOR_ITEM);
		if (slot == SPANNER_SLOT_INDEX)
			return stack.isOf(GadgetsItems.SPANNER_ITEM);

		return false;
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return null;
	}

	@Override
	public int[] getAvailableSlots(Direction side)
	{
		return new int[0];
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
	{
		return false;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir)
	{
		return false;
	}

	@Override
	public int size()
	{
		return this.inventory.size();
	}

	@Override
	public void provideRecipeInputs(RecipeFinder finder)
	{
		for (ItemStack itemStack : this.inventory)
		{
			finder.addInput(itemStack);
		}
	}
}
