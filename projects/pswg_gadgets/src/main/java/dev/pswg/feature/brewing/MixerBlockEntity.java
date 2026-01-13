package dev.pswg.feature.brewing;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import dev.pswg.packet.MixerSyncS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
	int foodItemCount;
	int drinkSaturation;
	int drinkNutrition;
	int drinkColor;
	public Stack<Pair<Float, Float>> path = new Stack<>();
	public ArrayList<StatusEffectInstance> drinkEffects = new ArrayList<>();
	public ArrayList<Integer> drinkColors = new ArrayList<>();
	public ArrayList<ItemStack> drinkFoods = new ArrayList<>();

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

	public static boolean isMixerReset(MixerBlockEntity mixer)
	{
		return
				mixer.currentMapX == 256
				&& mixer.currentMapY == 256
				&& mixer.dangerProgress == 0
				&& mixer.bellowProgress == 0
				&& mixer.bellowBacklog == 0
				&& mixer.foodItemCount == 0
				&& mixer.drinkSaturation == 0
				&& mixer.drinkNutrition == 0
				&& mixer.drinkColor == 0
				&& mixer.path.empty()
				&& mixer.drinkEffects.isEmpty()
				&& mixer.drinkColors.isEmpty();
	}
	public static void resetMixer(MixerBlockEntity mixer)
	{
		if (!isMixerReset(mixer))
		{
			mixer.currentMapX = 256;
			mixer.currentMapY = 256;
			mixer.dangerProgress = 0;
			mixer.bellowProgress = 0;
			mixer.bellowBacklog = 0;
			mixer.foodItemCount = 0;
			mixer.drinkSaturation = 0;
			mixer.drinkNutrition = 0;
			mixer.drinkColor = 0;
			mixer.path.clear();
			mixer.drinkEffects.clear();
			mixer.drinkColors.clear();
			mixer.drinkFoods.clear();
			sendSyncPacket(mixer);
		}
	}

	public static void craftPotion(MixerBlockEntity mixer)
	{
		BrewingCell cell = BrewingMap.getCell(mixer.currentMapX, mixer.currentMapY);
		if (cell.cellType == BrewingCellType.Potion && mixer.drinkEffects.size() < 3)
		{
			EffectCell effectCell = (EffectCell)cell;
			if (mixer.drinkEffects.stream().noneMatch(statusEffectInstance -> statusEffectInstance.getEffectType() == effectCell.statusEffect.getEffectType()))
				mixer.drinkEffects.add(effectCell.statusEffect);
		}

		if ((!mixer.drinkEffects.isEmpty() || !mixer.drinkColors.isEmpty()))
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
			int r = 0;
			int g = 0;
			int b = 0;
			for (int i = 0; i < mixer.drinkColors.size(); i++)
			{
				r += ColorHelper.getRed(mixer.drinkColors.get(i));
				g += ColorHelper.getGreen(mixer.drinkColors.get(i));
				b += ColorHelper.getBlue(mixer.drinkColors.get(i));
			}
			r /= Math.max(1, mixer.drinkColors.size());
			g /= Math.max(1, mixer.drinkColors.size());
			b /= Math.max(1, mixer.drinkColors.size());
			float s = 0;
			int n = 0;
			for (int i = 0; i < mixer.drinkFoods.size(); i++)
			{
				var foodComponent = mixer.drinkFoods.get(i).getOrDefault(DataComponentTypes.FOOD, new FoodComponent(0, 0, false));
				s += foodComponent.saturation();
				n += foodComponent.nutrition();
			}
			s /= 1.5f;
			n = (int)((float)n / 1.5f);

			Optional<Integer> color = (r == 0 && b == 0 && g == 0) ? Optional.empty() : Optional.of(ColorHelper.getArgb(r, g, b));
			stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), color, mixer.drinkEffects.stream().toList(), Optional.empty()));
			stack.set(DataComponentTypes.FOOD, new FoodComponent(n, s, false));
			mixer.inventory.set(OUTPUT_SLOT_INDEX, stack);

			resetMixer(mixer);
		}
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

	public static void sendSyncPacket(MixerBlockEntity mixer)
	{
		var payload = new MixerSyncS2CPayload(mixer.drinkEffects, mixer.drinkColors, mixer.drinkFoods);
		if (!mixer.world.isClient())
		{
			for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld)mixer.world, mixer.pos.toCenterPos(), 6))
				ServerPlayNetworking.send(player, payload);
		}
	}

	public static void tryAddEffect(MixerBlockEntity mixer)
	{
		BrewingCell cell = BrewingMap.getCell(mixer.currentMapX, mixer.currentMapY);
		if (mixer.drinkEffects.size() < 3 && cell.cellType == BrewingCellType.Potion)
		{
			EffectCell effectCell = (EffectCell)cell;
			for (StatusEffectInstance statusEffect : mixer.drinkEffects)
				if (effectCell.statusEffect.getEffectType() == statusEffect.getEffectType())
					return;
			mixer.drinkEffects.add(effectCell.statusEffect);
		}
		sendSyncPacket(mixer);
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
					mixer.bellowProgress = Math.min(mixer.bellowProgress + 5, MAX_BELLOW_PROGRESS);
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

			if (MixerBrewingPaths.pathMap.containsKey(inputStack.getItem()) && mixer.path.empty() && mixer.litTimeRemaining > 0 && drinkContainerPresent)
			{
				mixer.path.addAll(MixerBrewingPaths.pathMap.get(inputStack.getItem()));
				inputStack.decrement(1);
			}
			if (mixer.litTimeRemaining > 0 && drinkContainerPresent && inputStack.getItem() instanceof DyeItem dyeItem && mixer.drinkColors.size() < 3)
			{
				mixer.drinkColors.add(dyeItem.getColor().getEntityColor());
				inputStack.decrement(1);
				sendSyncPacket(mixer);
			}
			if (mixer.litTimeRemaining > 0 && drinkContainerPresent && inputStack.isIn(GadgetsItems.Tags.MIXER_FOOD_TAG) && mixer.drinkFoods.size() < 3)
			{
				mixer.drinkFoods.add(inputStack.copyWithCount(1));
				inputStack.decrement(1);
				sendSyncPacket(mixer);
			}
			BrewingCell cell = BrewingMap.getCell(mixer.currentMapX, mixer.currentMapY);
			if (cell.cellType == BrewingCellType.Danger)
			{
				EffectCell effectCell = (EffectCell)cell;
				mixer.dangerProgress++;
				if (mixer.dangerProgress >= 12)
				{
					spawnFailParticles(world, pos);
					mixer.drinkEffects.set(0, effectCell.statusEffect);
					craftPotion(mixer);
				}

			}
			if (cell.cellType == BrewingCellType.Corner)
			{
				mixer.dangerProgress++;
				if (mixer.dangerProgress >= 8)
				{
					resetMixer(mixer);
					craftPotion(mixer);
					spawnFailParticles(world, pos);
				}
			}
			mixer.dangerProgress = Math.max(0, mixer.dangerProgress - 1);
			if (!mixer.path.empty() && mixer.bellowBacklog > 0 && mixer.bellowProgress > 5)
			{
				float mod = 1f;
				if (cell.cellType == BrewingCellType.Danger)
				{
					mixer.dangerProgress++;
					mod = 1.25f;
				}
				if (cell.cellType == BrewingCellType.Potion)
					mod = 0.9f;
				if (cell.cellType == BrewingCellType.Corner)
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
			mixer.markDirty();
		}
	}

	@Override
	protected void readData(ReadView view)
	{
		currentMapX = view.getFloat("current_map_x", 256);
		currentMapY = view.getFloat("current_map_y", 256);
		litTimeRemaining = view.getInt("lit_time_remaining", 0);
		litTotalTime = view.getInt("lit_time_total", 1);
		bellowProgress = view.getInt("bellow_progress", 0);
		dangerProgress = view.getInt("danger_progress", 0);
		Inventories.readData(view, inventory);
		List<Float> angleList = view.read("path_angles", Codec.FLOAT.listOf()).get();
		List<Float> lengthList = view.read("path_lengths", Codec.FLOAT.listOf()).get();
		for (int i = 0; i < angleList.size(); i++)
			path.push(Pair.of(angleList.get(i), lengthList.get(i)));
		drinkEffects = new ArrayList<>(view.read("drink_effects", StatusEffectInstance.CODEC.listOf()).get());
		super.readData(view);
	}

	@Override
	protected void writeData(WriteView view)
	{

		view.putFloat("current_map_x", currentMapX);
		view.putFloat("current_map_y", currentMapY);
		view.putInt("lit_time_remaining", litTimeRemaining);
		view.putInt("lit_time_total", litTotalTime);
		view.putInt("bellow_progress", bellowProgress);
		view.putInt("danger_progress", dangerProgress);
		Inventories.writeData(view, inventory);
		ArrayList<Float> angleList = new ArrayList<>();
		ArrayList<Float> lengthList = new ArrayList<>();
		for (Pair<Float, Float> pair : path.stream().toList())
		{
			angleList.add(pair.getFirst());
			lengthList.add(pair.getSecond());
		}
		view.put("path_angles", Codec.FLOAT.listOf(), angleList);
		view.put("path_lengths", Codec.FLOAT.listOf(), lengthList);
		view.put("drink_effects", StatusEffectInstance.CODEC.listOf(), drinkEffects);

		super.writeData(view);
	}

	@Override
	public void onOpen(ContainerUser user)
	{
		super.onOpen(user);
		sendSyncPacket(this);
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
		MixerBlockEntity mixer = this;
		var factory = new ExtendedScreenHandlerFactory<>()
		{
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
			{
				return new MixerScreenHandler(syncId, playerInventory, mixer, propertyDelegate, pos, drinkEffects, drinkColors, drinkFoods);
			}

			@Override
			public Text getDisplayName()
			{
				return Text.of("Mixer");
			}

			@Override
			public Object getScreenOpeningData(ServerPlayerEntity player)
			{
				return new MixerSyncS2CPayload(drinkEffects, drinkColors, drinkFoods);
			}
		};
		if (playerInventory.player instanceof ServerPlayerEntity serverPlayer)
			serverPlayer.openHandledScreen(factory);
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
		return 3;
	}
}