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

	public static void resetMixer(MixerBlockEntity mixer)
	{
		mixer.currentMapX = 256;
		mixer.currentMapY = 256;
		mixer.dangerProgress = 0;
		mixer.bellowProgress = 0;
		mixer.path.clear();
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
			BrewingCell cell = BrewingMap.getCell(mixer.currentMapX, mixer.currentMapY);
			if (cell instanceof DangerCell dangerCell)
			{
				mixer.dangerProgress++;
				if (mixer.dangerProgress >= 12)
				{
					ItemStack stack = new ItemStack(GadgetsItems.BANTHA_COOKIE);
					stack.set(DataComponentTypes.CONSUMABLE, ConsumableComponent.builder().consumeEffect(new ApplyEffectsConsumeEffect(dangerCell.statusEffect)).build());
					mixer.inventory.set(OUTPUT_SLOT_INDEX, stack);
					resetMixer(mixer);
				}
			}
			if (cell instanceof CornerCell){
				mixer.dangerProgress++;
				if(mixer.dangerProgress >= 8)
					resetMixer(mixer);
			}
			mixer.dangerProgress = Math.max(0, mixer.dangerProgress - 1);
			if (!mixer.path.empty() && mixer.bellowProgress > 0)
			{
				float mod = 1f;
				if (cell instanceof DangerCell)
				{
					mixer.dangerProgress++;
					mod = 1.25f;
				}
				if(cell instanceof EffectCell)
					mod = 0.25f;
				if(cell instanceof CornerCell)
				{
					mixer.dangerProgress++;
					mod = 0.75f;
				}

				float value = mixer.path.peek().getFirst() * Math.clamp(mixer.path.peek().getSecond(), 0, 0.5f);
				mixer.currentMapY = Math.clamp(mixer.currentMapY - mod * (float)Math.cos(value), 1, 511);
				mixer.currentMapX = Math.clamp(mixer.currentMapX - mod * (float)Math.sin(value), 1, 511);

				float reducedLength = (float)Math.sqrt(mod * mod * (float)Math.cos(value) * (float)Math.cos(value) + (float)Math.sin(value) * (float)Math.sin(value));
				var lastElem = mixer.path.pop();
				Gadgets.LOGGER.info("reduced lenght: "+reducedLength);
				lastElem = new Pair<>(lastElem.getFirst(), lastElem.getSecond() - reducedLength);
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