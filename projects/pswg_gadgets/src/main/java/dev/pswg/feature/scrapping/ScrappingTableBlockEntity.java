package dev.pswg.feature.scrapping;

import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ScrappingTableBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeInputProvider, NamedScreenHandlerFactory
{
	protected static final int CUTTER_SLOT_INDEX = 0;
	protected static final int SPANNER_SLOT_INDEX = 1;
	protected static final int CALIBRATOR_SLOT_INDEX = 2;
	protected static final int INPUT_SLOT_INDEX = 3;
	protected static final int OUTPUT_1_SLOT_INDEX = 4;
	protected static final int OUTPUT_2_SLOT_INDEX = 5;
	protected static final int OUTPUT_3_SLOT_INDEX = 6;
	protected static final int OUTPUT_4_SLOT_INDEX = 7;
	public static final int[] OUTPUT_SLOTS = new int[] { 4, 5, 6, 7 };
	public static final int MAX_TOOL_PROGRESS = 48;

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(8, ItemStack.EMPTY);

	int cutterProgress;
	int spannerProgress;
	int calibratorProgress;

	private final ServerRecipeManager.MatchGetter<ScrappingTableRecipeInput, ? extends ScrappingTableRecipe> matchGetter;

	protected final PropertyDelegate propertyDelegate;

	public ScrappingTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.SCRAPPING_TABLE_BLOCK_ENTITY, pos, state);
		this.matchGetter = ServerRecipeManager.createCachedMatchGetter(GadgetsRecipeTypes.SCRAPPING);
		cutterProgress = -1;
		spannerProgress = -1;
		calibratorProgress = -1;
		propertyDelegate = new PropertyDelegate()
		{
			@Override
			public int get(int index)
			{
				return switch (index)
				{
					case 0 -> ScrappingTableBlockEntity.this.cutterProgress;
					case 1 -> ScrappingTableBlockEntity.this.spannerProgress;
					case 2 -> ScrappingTableBlockEntity.this.calibratorProgress;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value)
			{
				switch (index)
				{
					case 0:
						ScrappingTableBlockEntity.this.cutterProgress = value;
						break;
					case 1:
						ScrappingTableBlockEntity.this.spannerProgress = value;
						break;
					case 2:
						ScrappingTableBlockEntity.this.calibratorProgress = value;
				}
			}

			@Override
			public int size()
			{
				return 3;
			}
		};
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

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity)
	{
		if (world instanceof ServerWorld serverWorld)
		{
			if (blockEntity instanceof ScrappingTableBlockEntity scrappingBlockEntity)
			{
				var inputStack = scrappingBlockEntity.getStack(INPUT_SLOT_INDEX);

				for (int toolIndex = 0; toolIndex < 3; toolIndex++)
				{
					if (foundRecipe(scrappingBlockEntity, serverWorld, toolIndex))
					{
						if (scrappingBlockEntity.propertyDelegate.get(toolIndex) + 1 > MAX_TOOL_PROGRESS)
						{
							var recipeInput = new ScrappingTableRecipeInput(scrappingBlockEntity.getStack(toolIndex), inputStack);
							var recipeEntry = scrappingBlockEntity.matchGetter.getFirstMatch(recipeInput, serverWorld).orElse(null);
							ItemStack outputStack = recipeEntry.value().getResult();
							int outputSlot = scrappingBlockEntity.getAvailableOutputSlot(outputStack);
							if (scrappingBlockEntity.inventory.get(outputSlot).getItem() == outputStack.getItem())
								scrappingBlockEntity.inventory.get(outputSlot).increment(outputStack.getCount());
							else
								scrappingBlockEntity.inventory.set(outputSlot, outputStack);

							switch (toolIndex)
							{
								case 0:
									decreaseComponent(inputStack, GadgetsItems.Components.METAL_COMPONENT, 2);
									decreaseComponent(inputStack, GadgetsItems.Components.PLASTIC_COMPONENT, 1);
									break;
								case 1:
									decreaseComponent(inputStack, GadgetsItems.Components.TECH_COMPONENT, 2);
									decreaseComponent(inputStack, GadgetsItems.Components.METAL_COMPONENT, 1);
									break;
								case 2:
									decreaseComponent(inputStack, GadgetsItems.Components.ENERGY_COMPONENT, 2);
									decreaseComponent(inputStack, GadgetsItems.Components.TECH_COMPONENT, 1);
							}
							damageTool(scrappingBlockEntity.getStack(toolIndex));
							scrappingBlockEntity.propertyDelegate.set(toolIndex, 0);
						}
						else
							scrappingBlockEntity.propertyDelegate.set(toolIndex, Math.max(scrappingBlockEntity.propertyDelegate.get(toolIndex) - 1, 0));

					}
					else
					{
						scrappingBlockEntity.propertyDelegate.set(toolIndex, -1);
					}
				}
				scrappingBlockEntity.markDirty();
			}
		}
	}

	public int getAvailableOutputSlot(ItemStack stack)
	{
		for (int slot : OUTPUT_SLOTS)
		{
			ItemStack outputStack = inventory.get(slot);
			if (outputStack.isEmpty())
			{
				return slot;
			}
			else
			{
				if (outputStack.isOf(stack.getItem()))
					if (outputStack.getCount() < getMaxCount(stack) && outputStack.getCount() < outputStack.getMaxCount() || outputStack.getCount() < stack.getMaxCount())
					{
						return slot;
					}
			}
		}
		return 0;
	}

	public static void damageTool(ItemStack tool)
	{
		tool.setDamage(tool.getDamage() + 1);
		if (tool.getDamage() >= tool.getMaxDamage())
			tool.decrement(1);
	}

	public static boolean foundRecipe(ScrappingTableBlockEntity scrappingTableBlockEntity, ServerWorld world, int toolIndex)
	{
		var inputStack = scrappingTableBlockEntity.getStack(INPUT_SLOT_INDEX);
		var recipeInput = new ScrappingTableRecipeInput(
				scrappingTableBlockEntity.getStack(toolIndex),
				inputStack
		);
		RecipeEntry<? extends ScrappingTableRecipe> recipeEntry = null;
		if (!inputStack.isEmpty())
			recipeEntry = scrappingTableBlockEntity.matchGetter.getFirstMatch(recipeInput, world).orElse(null);
		if (recipeEntry != null)
		{
			int c = scrappingTableBlockEntity.getMaxCountPerStack();
			for (int slot : OUTPUT_SLOTS)
			{
				if (canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, recipeInput, scrappingTableBlockEntity.inventory, c, slot))
					return true;
			}
		}

		return false;
	}

	private static void decreaseComponent(ItemStack stack, ComponentType<Integer> component, int value)
	{
		if (stack.contains(component))
		{
			stack.set(component, stack.get(component) - value);
			if (stack.get(component) <= 0)
				stack.decrement(1);
		}
	}

	private static boolean canAcceptRecipeOutput(DynamicRegistryManager dynamicRegistryManager, @Nullable RecipeEntry<? extends ScrappingTableRecipe> recipe, ScrappingTableRecipeInput input, DefaultedList<ItemStack> inventory, int maxCount, int slot)
	{
		if (!inventory.get(INPUT_SLOT_INDEX).isEmpty() && recipe != null)
		{
			ItemStack itemStack = recipe.value().craft(input, dynamicRegistryManager);
			if (itemStack.isEmpty())
			{
				return false;
			}
			else
			{
				ItemStack outputStack = inventory.get(slot);
				if (outputStack.isEmpty())
				{
					return true;
				}
				else
				{
					return outputStack.getCount() < maxCount && outputStack.getCount() < outputStack.getMaxCount() || outputStack.getCount() < itemStack.getMaxCount();
				}
			}
		}
		return false;
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
			return true;//return stack.isIn(GadgetsItems.Tags.SCRAP_TAG);
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
		return new ScrappingTableScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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
