package dev.pswg.feature.brewing;

import com.mojang.datafixers.util.Pair;
import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

public class MixerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, NamedScreenHandlerFactory
{
	protected static final int FUEL_SLOT_INDEX = 0;
	protected static final int INPUT_SLOT_INDEX = 1;
	protected static final int OUTPUT_SLOT_INDEX = 2;

	protected static final int MAX_BELLOW_PROGRESS = 88;

	protected static final int MAX_MAP_X = 512;
	protected static final int MAX_MAP_Y = 512;

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	float currentMapX;
	float currentMapY;
	int litTimeRemaining;
	int litTotalTime;
	int bellowProgress;
	int dangerProgress;
	public Stack<Pair<Float, Float>> path = new Stack<>();

	protected final PropertyDelegate propertyDelegate;

	public MixerBlockEntity(BlockPos pos, BlockState state)
	{
		super(GadgetsBlockEntities.MIXER_BLOCK_ENTITY, pos, state);
		currentMapX = 256;
		currentMapY = 256;
		litTimeRemaining = 1;
		litTotalTime = 1;
		bellowProgress = 0;
		dangerProgress = 0;
		propertyDelegate = new PropertyDelegate()
		{
			@Override
			public int get(int index)
			{
				return switch (index)
				{
					case 0 -> (int)(currentMapX * 10);
					case 1 -> (int)(currentMapY * 10);
					case 2 -> litTimeRemaining;
					case 3 -> litTotalTime;
					case 4 -> bellowProgress;
					case 5 -> dangerProgress;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value)
			{
				switch (index)
				{
					case 0 -> currentMapX = value / 100f;
					case 1 -> currentMapY = value / 100f;
					case 2 -> litTimeRemaining = value;
					case 3 -> litTotalTime = value;
					case 4 -> bellowProgress = value;
					case 5 -> dangerProgress = value;
				}
			}

			@Override
			public int size()
			{
				return 6;
			}
		};
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity)
	{
		if (blockEntity instanceof MixerBlockEntity mixer)
		{
			ItemStack inputStack = mixer.getStack(INPUT_SLOT_INDEX);
			if (inputStack.contains(GadgetsItems.Components.BREWING_PATH) && mixer.path.empty() && mixer.litTimeRemaining > 0)
			{
				mixer.path.addAll(inputStack.get(GadgetsItems.Components.BREWING_PATH));
				inputStack.decrement(1);
			}
			BrewingCell currentCell = BrewingMap.getCell(mixer.currentMapX, mixer.currentMapY);
			///    if (!world.isClient)
			///		Gadgets.LOGGER.info("S|  x: " + mixer.currentMapX / 16f + " y: " + mixer.currentMapY / 16f);
			if (currentCell instanceof DangerCell dangerCell)
			{
				mixer.dangerProgress++;
				Gadgets.LOGGER.info("danger: " + dangerCell);
				if (mixer.dangerProgress >= 25)
				{
					ItemStack stack = new ItemStack(GadgetsItems.BANTHA_COOKIE);
					stack.set(DataComponentTypes.CONSUMABLE, ConsumableComponent.builder().consumeEffect(new ApplyEffectsConsumeEffect(dangerCell.statusEffect)).build());
					mixer.inventory.set(OUTPUT_SLOT_INDEX, stack);
				}
			}
			mixer.dangerProgress--;
			if (!mixer.path.empty() && mixer.bellowProgress > 0)
			{
				if (currentCell instanceof DangerCell dangerCell)
				{
					mixer.dangerProgress++;
				}
				float value = mixer.path.peek().getFirst() * Math.clamp(mixer.path.peek().getSecond(), 0, 0.5f);
				mixer.currentMapY = Math.clamp(Math.max(0, mixer.currentMapY + (float)Math.sin(value)), 1, 511);
				mixer.currentMapX = Math.clamp(Math.max(0, mixer.currentMapX + (float)Math.cos(value)), 1, 511);

				var lastElem = mixer.path.pop();
				lastElem = new Pair<>(lastElem.getFirst(), lastElem.getSecond() - 0.5f);
				if (lastElem.getSecond() > 0)
					mixer.path.push(lastElem);
			}
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
		return new MixerScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
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