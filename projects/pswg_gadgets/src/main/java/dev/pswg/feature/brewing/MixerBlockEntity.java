package dev.pswg.feature.brewing;

import dev.pswg.container.GadgetsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MixerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, NamedScreenHandlerFactory
{
	protected static final int FUEL_SLOT_INDEX = 0;
	protected static final int INPUT_SLOT_INDEX = 1;
	protected static final int OUTPUT_SLOT_INDEX = 2;

	protected static final int MAX_BELLOW_PROGRESS = 44;

	protected static final int MAX_MAP_X = 512;
	protected static final int MAX_MAP_Y = 512;

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	int currentMapX;
	int currentMapY;
	int litTimeRemaining;
	int litTotalTime;
	int bellowProgress;

	protected final PropertyDelegate propertyDelegate;

	public MixerBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.MIXER_BLOCK_ENTITY, pos, state);
		currentMapX = MAX_MAP_X / 2;
		currentMapY = MAX_MAP_Y / 2;
		litTimeRemaining = 1;
		litTotalTime = 1;
		bellowProgress = 0;
		propertyDelegate = new PropertyDelegate()
		{
			@Override
			public int get(int index)
			{
				return switch (index)
				{
					case 1 -> currentMapX;
					case 2 -> currentMapY;
					case 3 -> litTimeRemaining;
					case 4 -> litTotalTime;
					case 5 -> bellowProgress;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value)
			{
				switch (index)
				{
					case 1 -> currentMapX = value;
					case 2 -> currentMapY = value;
					case 3 -> litTimeRemaining = value;
					case 4 -> litTotalTime = value;
					case 5 -> bellowProgress = value;
				}
			}

			@Override
			public int size()
			{
				return 5;
			}
		};
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity)
	{
		if (blockEntity instanceof MixerBlockEntity mixer)
		{
			mixer.litTimeRemaining = Math.max(mixer.litTimeRemaining - 1, 0);
			mixer.bellowProgress = Math.max(mixer.bellowProgress - 1, 0);
			ItemStack fuelStack = mixer.getStack(FUEL_SLOT_INDEX);
			if (mixer.litTimeRemaining == 0 && !fuelStack.isEmpty() && world.getFuelRegistry().isFuel(fuelStack))
			{
				mixer.litTotalTime = world.getFuelRegistry().getFuelTicks(fuelStack);
				mixer.litTimeRemaining = world.getFuelRegistry().getFuelTicks(fuelStack);
				fuelStack.decrement(1);
			}
		}
	}

	@Override
	protected Text getContainerName()
	{
		return Text.literal("Mixer");
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
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return new MixerScreenHandler(syncId, playerInventory);
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
		return 3;
	}
}