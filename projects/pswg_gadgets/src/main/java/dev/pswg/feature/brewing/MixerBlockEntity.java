package dev.pswg.feature.brewing;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.pswg.container.GadgetsBlockEntities;
import dev.pswg.container.GadgetsItems;
import dev.pswg.networking.MixerSyncS2CPayload;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class MixerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, MenuProvider, ExtendedMenuProvider<MixerSyncS2CPayload>
{
	protected static final int FUEL_SLOT_INDEX = 0;
	protected static final int INPUT_SLOT_INDEX = 1;
	protected static final int OUTPUT_SLOT_INDEX = 2;
	protected static final int MAX_BELLOW_PROGRESS = 180;

	protected static final int MAX_MAP_X = 512;
	protected static final int MAX_MAP_Y = 512;

	protected NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
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
	public ArrayList<MobEffectInstance> drinkEffects = new ArrayList<>();
	public ArrayList<Integer> drinkColors = new ArrayList<>();
	public ArrayList<ItemStack> drinkFoods = new ArrayList<>();

	protected final ContainerData propertyDelegate;

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
		propertyDelegate = new ContainerData()
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
			public int getCount()
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
			if (mixer.drinkEffects.stream().noneMatch(statusEffectInstance -> statusEffectInstance.getEffect() == effectCell.statusEffect.getEffect()))
				mixer.drinkEffects.add(effectCell.statusEffect);
		}

		if ((!mixer.drinkEffects.isEmpty() || !mixer.drinkColors.isEmpty()))
		{
			ItemStack stack;
			ItemStack outputStack = mixer.inventory.get(OUTPUT_SLOT_INDEX);
			if (outputStack.is(Items.GLASS_BOTTLE))
				stack = new ItemStack(Items.POTION);
			else
			{
				Item item = BuiltInRegistries.ITEM.getValue(BuiltInRegistries.ITEM.getKey(outputStack.getItem()).withSuffix("_filled"));
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
				r += ARGB.red(mixer.drinkColors.get(i));
				g += ARGB.green(mixer.drinkColors.get(i));
				b += ARGB.blue(mixer.drinkColors.get(i));
			}
			r /= Math.max(1, mixer.drinkColors.size());
			g /= Math.max(1, mixer.drinkColors.size());
			b /= Math.max(1, mixer.drinkColors.size());
			float s = 0;
			int n = 0;
			for (int i = 0; i < mixer.drinkFoods.size(); i++)
			{
				var foodComponent = mixer.drinkFoods.get(i).getOrDefault(DataComponents.FOOD, new FoodProperties(0, 0, false));
				s += foodComponent.saturation();
				n += foodComponent.nutrition();
			}
			s /= 1.5f;
			n = (int)((float)n / 1.5f);

			Optional<Integer> color = (r == 0 && b == 0 && g == 0) ? Optional.empty() : Optional.of(ARGB.color(r, g, b));
			stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), color, mixer.drinkEffects.stream().toList(), Optional.empty()));
			stack.set(DataComponents.FOOD, new FoodProperties(n, s, false));
			mixer.inventory.set(OUTPUT_SLOT_INDEX, stack);

			resetMixer(mixer);
		}
	}

	public static void spawnFailParticles(Level world, BlockPos pos)
	{
		if (world instanceof ServerLevel serverWorld)
			serverWorld.sendParticles(
					ParticleTypes.SMOKE,
					false,
					false,
					pos.getCenter().x(),
					pos.getCenter().y(),
					pos.getCenter().z(),
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
		if (!mixer.level.isClientSide())
		{
			for (ServerPlayer player : PlayerLookup.around((ServerLevel)mixer.level, mixer.worldPosition.getCenter(), 6))
				ServerPlayNetworking.send(player, payload);
		}
	}

	public static void tryAddEffect(MixerBlockEntity mixer)
	{
		BrewingCell cell = BrewingMap.getCell(mixer.currentMapX, mixer.currentMapY);
		if (mixer.drinkEffects.size() < 3 && cell.cellType == BrewingCellType.Potion)
		{
			EffectCell effectCell = (EffectCell)cell;
			for (MobEffectInstance statusEffect : mixer.drinkEffects)
				if (effectCell.statusEffect.getEffect() == statusEffect.getEffect())
					return;
			mixer.drinkEffects.add(effectCell.statusEffect);
		}
		sendSyncPacket(mixer);
	}

	public static <T extends BlockEntity> void tick(Level world, BlockPos pos, BlockState state, T blockEntity)
	{
		if (blockEntity instanceof MixerBlockEntity mixer)
		{
			boolean drinkContainerPresent = !mixer.getItem(OUTPUT_SLOT_INDEX).isEmpty() && mixer.getItem(OUTPUT_SLOT_INDEX).is(GadgetsItems.Tags.DRINK_CONTAINER_TAG);
			if (!drinkContainerPresent)
				resetMixer(mixer);

			ItemStack inputStack = mixer.getItem(INPUT_SLOT_INDEX);
			mixer.bellowBacklog = Math.max(mixer.bellowBacklog - 1, 0);
			mixer.litTimeRemaining = Math.max(mixer.litTimeRemaining - 1, 0);

			if (mixer.litTimeRemaining > 0)
			{
				if (mixer.bellowBacklog == 0)
					mixer.bellowProgress = Math.min(mixer.bellowProgress + 5, MAX_BELLOW_PROGRESS);
			}
			else
				mixer.bellowProgress = Math.max(mixer.bellowProgress - 2, 0);

			ItemStack fuelStack = mixer.getItem(FUEL_SLOT_INDEX);
			if (mixer.litTimeRemaining == 0 && drinkContainerPresent && !fuelStack.isEmpty() && world.fuelValues().isFuel(fuelStack) && (!mixer.path.empty() || !mixer.getItem(INPUT_SLOT_INDEX).isEmpty()))
			{
				mixer.litTotalTime = world.fuelValues().burnDuration(fuelStack);
				mixer.litTimeRemaining = world.fuelValues().burnDuration(fuelStack);
				fuelStack.shrink(1);
			}

			if (MixerBrewingPaths.pathMap.containsKey(inputStack.getItem()) && mixer.path.empty() && mixer.litTimeRemaining > 0 && drinkContainerPresent)
			{
				mixer.path.addAll(MixerBrewingPaths.pathMap.get(inputStack.getItem()));
				inputStack.shrink(1);
			}
			if (mixer.litTimeRemaining > 0 && drinkContainerPresent && inputStack.getItem() instanceof DyeItem dyeItem && mixer.drinkColors.size() < 3)
			{
				mixer.drinkColors.add(inputStack.getOrDefault(DataComponents.DYE, net.minecraft.world.item.DyeColor.WHITE).getTextureDiffuseColor());
				inputStack.shrink(1);
				sendSyncPacket(mixer);
			}
			if (mixer.litTimeRemaining > 0 && drinkContainerPresent && inputStack.is(GadgetsItems.Tags.MIXER_FOOD_TAG) && mixer.drinkFoods.size() < 3)
			{
				mixer.drinkFoods.add(inputStack.copyWithCount(1));
				inputStack.shrink(1);
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
			mixer.setChanged();
		}
	}

	@Override
	protected void loadAdditional(ValueInput view)
	{
		currentMapX = view.getFloatOr("current_map_x", 256);
		currentMapY = view.getFloatOr("current_map_y", 256);
		litTimeRemaining = view.getIntOr("lit_time_remaining", 0);
		litTotalTime = view.getIntOr("lit_time_total", 1);
		bellowProgress = view.getIntOr("bellow_progress", 0);
		dangerProgress = view.getIntOr("danger_progress", 0);
		ContainerHelper.loadAllItems(view, inventory);
		List<Float> angleList = view.read("path_angles", Codec.FLOAT.listOf()).get();
		List<Float> lengthList = view.read("path_lengths", Codec.FLOAT.listOf()).get();
		for (int i = 0; i < angleList.size(); i++)
			path.push(Pair.of(angleList.get(i), lengthList.get(i)));
		drinkEffects = new ArrayList<>(view.read("drink_effects", MobEffectInstance.CODEC.listOf()).get());
		super.loadAdditional(view);
	}

	@Override
	protected void saveAdditional(ValueOutput view)
	{

		view.putFloat("current_map_x", currentMapX);
		view.putFloat("current_map_y", currentMapY);
		view.putInt("lit_time_remaining", litTimeRemaining);
		view.putInt("lit_time_total", litTotalTime);
		view.putInt("bellow_progress", bellowProgress);
		view.putInt("danger_progress", dangerProgress);
		ContainerHelper.saveAllItems(view, inventory);
		ArrayList<Float> angleList = new ArrayList<>();
		ArrayList<Float> lengthList = new ArrayList<>();
		for (Pair<Float, Float> pair : path.stream().toList())
		{
			angleList.add(pair.getFirst());
			lengthList.add(pair.getSecond());
		}
		view.store("path_angles", Codec.FLOAT.listOf(), angleList);
		view.store("path_lengths", Codec.FLOAT.listOf(), lengthList);
		view.store("drink_effects", MobEffectInstance.CODEC.listOf(), drinkEffects);

		super.saveAdditional(view);
	}

	@Override
	public void startOpen(ContainerUser user)
	{
		super.startOpen(user);
		sendSyncPacket(this);
	}

	@Override
	protected Component getDefaultName()
	{
		return Component.literal("Mixer");
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
	protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory)
	{
		return new MixerScreenHandler(syncId, playerInventory, this, propertyDelegate, worldPosition, drinkEffects, drinkColors, drinkFoods);
	}

	@Override
	public MixerSyncS2CPayload getScreenOpeningData(ServerPlayer player)
	{
		return new MixerSyncS2CPayload(drinkEffects, drinkColors, drinkFoods);
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
		return 3;
	}
}
