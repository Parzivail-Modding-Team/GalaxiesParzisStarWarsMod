package dev.pswg.feature.scrapping.table;

import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.container.GalaxiesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ScrappingTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, StackedContentsCompatible, MenuProvider
{
	protected static final int CUTTER_SLOT_INDEX = 0;
	protected static final int SPANNER_SLOT_INDEX = 1;
	protected static final int CALIBRATOR_SLOT_INDEX = 2;
	protected static final int INPUT_SLOT_INDEX = 3;
	public static final int[] OUTPUT_SLOTS = new int[] { 4, 5, 6, 7, 8, 9 };
	public static final int MAX_TOOL_PROGRESS = 480;

	protected NonNullList<ItemStack> inventory = NonNullList.withSize(10, ItemStack.EMPTY);

	int cutterProgress;
	int spannerProgress;
	int calibratorProgress;

	private final RecipeManager.CachedCheck<ScrappingTableRecipeInput, ? extends ScrappingTableRecipe> matchGetter;

	protected final ContainerData propertyDelegate;

	public ScrappingTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.SCRAPPING_TABLE_BLOCK_ENTITY, pos, state);
		this.matchGetter = RecipeManager.createCheck(GadgetsRecipeTypes.SCRAPPING);
		cutterProgress = -1;
		spannerProgress = -1;
		calibratorProgress = -1;
		propertyDelegate = new ContainerData()
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
			public int getCount()
			{
				return 3;
			}
		};
	}

	@Override
	protected Component getDefaultName()
	{
		return Component.literal("Scrapping Table");
	}

	@Override
	protected void loadAdditional(ValueInput view)
	{
		super.loadAdditional(view);
		this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(view, this.inventory);
	}

	@Override
	protected void saveAdditional(ValueOutput view)
	{
		super.saveAdditional(view);
		ContainerHelper.saveAllItems(view, this.inventory);
	}

	public static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T blockEntity)
	{
		if (world instanceof ServerLevel serverWorld)
		{
			if (blockEntity instanceof ScrappingTableBlockEntity scrappingBlockEntity)
			{
				var inputStack = scrappingBlockEntity.getItem(INPUT_SLOT_INDEX);

				for (int toolIndex = 0; toolIndex < 3; toolIndex++)
				{
					var recipeInput = new ScrappingTableRecipeInput(scrappingBlockEntity.getItem(toolIndex), inputStack);
					var recipeEntry = scrappingBlockEntity.matchGetter.getRecipeFor(recipeInput, serverWorld).orElse(null);

					ItemStack outputStack = ItemStack.EMPTY;
					ItemStack secondaryOutputStack = ItemStack.EMPTY;
					boolean canOutput = false;
					if (recipeEntry != null)
					{
						outputStack = recipeEntry.value().getPrimaryResult();
						secondaryOutputStack = recipeEntry.value().getSecondaryResult();
						canOutput = scrappingBlockEntity.areOutputSlotsAvailable(outputStack, secondaryOutputStack, toolIndex);
					}

					if (foundRecipe(scrappingBlockEntity, serverWorld, toolIndex) && recipeEntry != null && canOutput)
					{
						float secondaryChance = recipeEntry.value().getSecondaryChance();
						if (scrappingBlockEntity.propertyDelegate.get(toolIndex) + 1 > MAX_TOOL_PROGRESS)
						{

							if (scrappingBlockEntity.inventory.get(toolIndex * 2 + 4).getItem() == outputStack.getItem())
								scrappingBlockEntity.inventory.get(toolIndex * 2 + 4).grow(outputStack.getCount());
							else
								scrappingBlockEntity.inventory.set(toolIndex * 2 + 4, outputStack);

							if (secondaryChance <= Math.abs(world.getRandom().nextFloat()))
							{
								if (scrappingBlockEntity.inventory.get(toolIndex * 2 + 5).getItem() == secondaryOutputStack.getItem())
									scrappingBlockEntity.inventory.get(toolIndex * 2 + 5).grow(secondaryOutputStack.getCount());
								else
									scrappingBlockEntity.inventory.set(toolIndex * 2 + 5, secondaryOutputStack);
							}

							switch (toolIndex)
							{
								case 0:
									decreaseComponent(inputStack, GalaxiesItems.Components.METAL_COMPONENT, 2);
									decreaseComponent(inputStack, GalaxiesItems.Components.PLASTIC_COMPONENT, 1);
									break;
								case 1:
									decreaseComponent(inputStack, GalaxiesItems.Components.TECH_COMPONENT, 2);
									decreaseComponent(inputStack, GalaxiesItems.Components.METAL_COMPONENT, 1);
									break;
								case 2:
									decreaseComponent(inputStack, GalaxiesItems.Components.ENERGY_COMPONENT, 2);
									decreaseComponent(inputStack, GalaxiesItems.Components.TECH_COMPONENT, 1);
							}
							damageTool(scrappingBlockEntity.getItem(toolIndex));
							scrappingBlockEntity.propertyDelegate.set(toolIndex, 0);
						}
						else
							scrappingBlockEntity.propertyDelegate.set(toolIndex, Math.max(scrappingBlockEntity.propertyDelegate.get(toolIndex) - 10, 0));
					}
					else
					{
						scrappingBlockEntity.propertyDelegate.set(toolIndex, -1);
					}

				}
				scrappingBlockEntity.setChanged();
			}
		}
	}

	public boolean areOutputSlotsAvailable(ItemStack primary, ItemStack secondary, int toolIndex)
	{
		int slotPrimary = toolIndex * 2 + 4;
		int slotSecondary = toolIndex * 2 + 5;
		ItemStack primaryOutputStack = inventory.get(slotPrimary);
		ItemStack secondaryOutputStack = inventory.get(slotSecondary);

		boolean primaryAvailable = false;
		if (primaryOutputStack.is(primary.getItem()))
		{
			if (primaryOutputStack.getCount() < getMaxStackSize(primary) && primaryOutputStack.getCount() < primaryOutputStack.getMaxStackSize() || primaryOutputStack.getCount() < primary.getMaxStackSize())
			{
				primaryAvailable = true;
			}
		}
		boolean secondaryAvailable = false;
		if (secondaryOutputStack.is(secondary.getItem()))
		{
			if (secondaryOutputStack.getCount() < getMaxStackSize(secondary) && secondaryOutputStack.getCount() < secondaryOutputStack.getMaxStackSize() || secondaryOutputStack.getCount() < secondary.getMaxStackSize())
			{
				secondaryAvailable = true;
			}
		}
		return (primaryAvailable || primaryOutputStack.isEmpty()) && (secondaryAvailable || secondaryOutputStack.isEmpty());
	}

	public static void damageTool(ItemStack tool)
	{
		if (tool.getDamageValue() + 1 < tool.getMaxDamage())
			tool.setDamageValue(tool.getDamageValue() + 1);
	}

	public static boolean foundRecipe(ScrappingTableBlockEntity scrappingTableBlockEntity, ServerLevel world, int toolIndex)
	{
		var inputStack = scrappingTableBlockEntity.getItem(INPUT_SLOT_INDEX);
		var recipeInput = new ScrappingTableRecipeInput(
				scrappingTableBlockEntity.getItem(toolIndex),
				inputStack
		);
		RecipeHolder<? extends ScrappingTableRecipe> recipeEntry = null;
		if (!inputStack.isEmpty())
			recipeEntry = scrappingTableBlockEntity.matchGetter.getRecipeFor(recipeInput, world).orElse(null);
		if (recipeEntry != null)
		{
			int c = scrappingTableBlockEntity.getMaxStackSize();
			for (int slot : OUTPUT_SLOTS)
			{
				if (canAcceptRecipeOutput(world.registryAccess(), recipeEntry, recipeInput, scrappingTableBlockEntity.inventory, c, slot))
					return true;
			}
		}

		return false;
	}

	private static void decreaseComponent(ItemStack stack, DataComponentType<Integer> component, int value)
	{
		if (stack.has(component))
		{
			stack.set(component, stack.get(component) - value);
			if (stack.get(component) <= 0)
				stack.shrink(1);
		}
	}

	private static boolean canAcceptRecipeOutput(RegistryAccess dynamicRegistryManager, @Nullable RecipeHolder<? extends ScrappingTableRecipe> recipe, ScrappingTableRecipeInput input, NonNullList<ItemStack> inventory, int maxCount, int slot)
	{
		if (!inventory.get(INPUT_SLOT_INDEX).isEmpty() && recipe != null)
		{
			ItemStack itemStack = recipe.value().assemble(input);
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
					return outputStack.getCount() < maxCount && outputStack.getCount() < outputStack.getMaxStackSize() || outputStack.getCount() < itemStack.getMaxStackSize();
				}
			}
		}
		return false;
	}

	@Override
	protected NonNullList<ItemStack> getItems()
	{
		return inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> inventory)
	{
		this.inventory = inventory;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack)
	{
		if (Arrays.stream(OUTPUT_SLOTS).anyMatch(value -> value == slot))
			return false;
		if (slot == INPUT_SLOT_INDEX)
			return true;//return stack.isIn(GadgetsItems.Tags.SCRAP_TAG);
		if (slot == CUTTER_SLOT_INDEX)
			return stack.is(GadgetsItems.CUTTER_ITEM);
		if (slot == CALIBRATOR_SLOT_INDEX)
			return stack.is(GadgetsItems.CALIBRATOR_ITEM);
		if (slot == SPANNER_SLOT_INDEX)
			return stack.is(GadgetsItems.SPANNER_ITEM);

		return false;
	}

	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory)
	{
		return new ScrappingTableScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	@Override
	public int[] getSlotsForFace(Direction side)
	{
		return new int[0];
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir)
	{
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir)
	{
		return false;
	}

	@Override
	public int getContainerSize()
	{
		return this.inventory.size();
	}

	@Override
	public void fillStackedContents(StackedItemContents finder)
	{
		for (ItemStack itemStack : this.inventory)
		{
			finder.accountStack(itemStack);
		}
	}
}
