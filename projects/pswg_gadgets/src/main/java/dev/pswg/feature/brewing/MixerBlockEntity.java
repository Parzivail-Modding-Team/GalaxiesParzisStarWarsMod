package dev.pswg.feature.brewing;

import com.mojang.datafixers.util.Pair;
import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
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

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class MixerBlockEntity extends LockableContainerBlockEntity implements SidedInventory, NamedScreenHandlerFactory
{
	protected static final int FUEL_SLOT_INDEX = 0;
	protected static final int INPUT_SLOT_INDEX = 1;
	protected static final int OUTPUT_SLOT_INDEX = 2;
	protected static final int MAX_BELLOW_PROGRESS = 180;

	protected static final int MAX_MAP_X = 512;
	protected static final int MAX_MAP_Y = 512;

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	float currentMapX;
	float currentMapY;
	int litTimeRemaining;
	int litTotalTime;
	int bellowProgress;
	int dangerProgress;
	int bellowBacklog;
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
		bellowBacklog = 0;
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
					case 6 -> bellowBacklog;
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
					case 6 -> bellowBacklog = value;
				}
			}

			@Override
			public int size()
			{
				return 7;
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

	public static void craftPotion(MixerBlockEntity mixer, StatusEffectInstance... effects)
	{
		ItemStack stack;
		ItemStack outputStack = mixer.inventory.get(OUTPUT_SLOT_INDEX);
		if (outputStack.isOf(Items.GLASS_BOTTLE))
			stack = new ItemStack(Items.POTION);
		else
		{
			Item item = Registries.ITEM.get(Registries.ITEM.getId(outputStack.getItem()).withSuffixedPath("_filled"));
			if (item != null)
				stack = new ItemStack(item);
			else
				stack = outputStack;
		}
		stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.empty(), List.of(effects), Optional.empty()));
		mixer.inventory.set(OUTPUT_SLOT_INDEX, stack);
		resetMixer(mixer);
	}

	public static void spawnFailParticles(World world, BlockPos pos)
	{
		if (world instanceof ServerWorld serverWorld)
			serverWorld.spawnParticles(
					ParticleTypes.SMOKE,
					false,
					false,
					pos.toCenterPos().getX(),
					pos.toCenterPos().getY(),
					pos.toCenterPos().getZ(),
					20,
					0,
					0,
					0,
					0.05f
			);
	}

	public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T blockEntity)
	{
		if (blockEntity instanceof MixerBlockEntity mixer)
		{
			boolean drinkContainerPresent = !mixer.getStack(OUTPUT_SLOT_INDEX).isEmpty() && mixer.getStack(OUTPUT_SLOT_INDEX).isIn(GadgetsItems.Tags.DRINK_CONTAINER_TAG);
			if (!drinkContainerPresent)
				resetMixer(mixer);

			ItemStack inputStack = mixer.getStack(INPUT_SLOT_INDEX);
			mixer.bellowBacklog = Math.max(mixer.bellowBacklog - 1, 0);
			mixer.litTimeRemaining = Math.max(mixer.litTimeRemaining - 1, 0);

			if (mixer.litTimeRemaining > 0)
			{
				if (mixer.bellowBacklog == 0)
				{

					mixer.bellowProgress = Math.min(mixer.bellowProgress + 5, MAX_BELLOW_PROGRESS);
				}
			}
			else
				mixer.bellowProgress = Math.max(mixer.bellowProgress - 2, 0);

			ItemStack fuelStack = mixer.getStack(FUEL_SLOT_INDEX);
			if (mixer.litTimeRemaining == 0 && drinkContainerPresent && !fuelStack.isEmpty() && world.getFuelRegistry().isFuel(fuelStack) && (!mixer.path.empty() || !mixer.getStack(INPUT_SLOT_INDEX).isEmpty()))
			{
				mixer.litTotalTime = world.getFuelRegistry().getFuelTicks(fuelStack);
				mixer.litTimeRemaining = world.getFuelRegistry().getFuelTicks(fuelStack);
				fuelStack.decrement(1);
			}

			if (inputStack.contains(GadgetsItems.Components.BREWING_PATH) && mixer.path.empty() && mixer.litTimeRemaining > 0 && drinkContainerPresent)
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
					resetMixer(mixer);
					spawnFailParticles(world, pos);
					craftPotion(mixer, dangerCell.statusEffect);
				}

			}
			if (cell instanceof CornerCell){
				mixer.dangerProgress++;
				if (mixer.dangerProgress >= 8)
				{
					resetMixer(mixer);
					craftPotion(mixer);
					spawnFailParticles(world, pos);
				}
			}
			if (cell instanceof EffectCell effectCell)
			{
				if (mixer.bellowProgress >= MAX_BELLOW_PROGRESS - 8)
					craftPotion(mixer, effectCell.statusEffect);
			}
			mixer.dangerProgress = Math.max(0, mixer.dangerProgress - 1);
			if (!mixer.path.empty() && mixer.bellowBacklog > 0 && mixer.bellowProgress > 5)
			{
				float mod = 1f;
				if (cell instanceof DangerCell)
				{
					mixer.dangerProgress++;
					mod = 1.25f;
				}
				if(cell instanceof EffectCell)
					mod = 0.9f;
				if(cell instanceof CornerCell)
				{
					mixer.dangerProgress++;
					mod = 0.95f;
				}
				mod = Math.min(mod, mixer.path.peek().getSecond());
				float value = mixer.path.peek().getFirst();
				float deltaY = -mod * (float)Math.sin(value);
				float deltaX = mod * (float)Math.cos(value);
				mixer.currentMapY = Math.clamp(mixer.currentMapY + deltaY, 0, 512);
				mixer.currentMapX = Math.clamp(mixer.currentMapX + deltaX, 0, 512);

				var lastElem = mixer.path.pop();
				lastElem = new Pair<>(lastElem.getFirst(), lastElem.getSecond() - mod);
				if (lastElem.getSecond() > 0)
					mixer.path.push(lastElem);
			}
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
	{
		currentMapX = nbt.getInt("current_map_x");
		currentMapY = nbt.getInt("current_map_y");
		litTimeRemaining = nbt.getInt("lit_time_remaining");
		litTotalTime = nbt.getInt("lit_time_total");
		bellowProgress = nbt.getInt("bellow_progress");
		dangerProgress = nbt.getInt("danger_progress");
		Inventories.readNbt(nbt, inventory, registries);
		NbtList angleList = nbt.getList("path_angles", NbtElement.FLOAT_TYPE);
		NbtList lengthList = nbt.getList("path_lengths", NbtElement.FLOAT_TYPE);
		for (int i = 0; i < angleList.size(); i++)
			path.push(Pair.of(angleList.getFloat(i), lengthList.getFloat(i)));
		super.readNbt(nbt, registries);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries)
	{
		nbt.putFloat("current_map_x", currentMapX);
		nbt.putFloat("current_map_y", currentMapY);
		nbt.putInt("lit_time_remaining", litTimeRemaining);
		nbt.putInt("lit_time_total", litTotalTime);
		nbt.putInt("bellow_progress", bellowProgress);
		nbt.putInt("danger_progress", dangerProgress);
		Inventories.writeNbt(nbt, inventory, registries);
		NbtList angleList = new NbtList();
		NbtList lengthList = new NbtList();
		for (Pair<Float, Float> pair : path.stream().toList())
		{
			angleList.add(NbtFloat.of(pair.getFirst()));
			lengthList.add(NbtFloat.of(pair.getSecond()));
		}
		nbt.put("path_angles", angleList);
		nbt.put("path_lengths", lengthList);

		super.writeNbt(nbt, registries);
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