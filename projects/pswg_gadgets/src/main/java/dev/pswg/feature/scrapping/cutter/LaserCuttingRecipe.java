package dev.pswg.feature.scrapping.cutter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.container.GadgetsRecipeSerializers;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.util.GalaxiesPacketUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public class LaserCuttingRecipe implements Recipe<SingleRecipeInput>
{

	private final Ingredient ingredient;
	private final ItemStackTemplate primaryResult;
	private final ItemStackTemplate secondaryResult;
	private final float secondaryChance;

	public LaserCuttingRecipe(
		Ingredient ingredient,
		ItemStackTemplate primaryResult,
		ItemStackTemplate secondaryResult,
		float secondaryChance
	)
	{
		this.ingredient = ingredient;
		this.primaryResult = primaryResult;
		this.secondaryResult = secondaryResult;
		this.secondaryChance = secondaryChance;
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level world)
	{
		return ingredient.test(input.item());
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input)
	{
		return primaryResult.create();
	}

	public ItemStack craftSecondary()
	{
		return secondaryResult.create();
	}

	public Ingredient getIngredient()
	{
		return ingredient;
	}

	public ItemStackTemplate getPrimaryResult()
	{
		return primaryResult;
	}

	public ItemStackTemplate getSecondaryResult()
	{
		return secondaryResult;
	}

	public float getSecondaryChance()
	{
		return secondaryChance;
	}

	@Override
	public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer()
	{
		return GadgetsRecipeSerializers.LASER_CUTTING_SERIALIZER;
	}

	@Override
	public boolean isSpecial()
	{
		return true;
	}

	@Override
	public boolean showNotification()
	{
		return false;
	}

	@Override
	public String group()
	{
		return "";
	}

	@Override
	public RecipeType<? extends Recipe<SingleRecipeInput>> getType()
	{
		return GadgetsRecipeTypes.CUTTING;
	}

	@Override
	public PlacementInfo placementInfo()
	{
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public RecipeBookCategory recipeBookCategory()
	{
		return RecipeBookCategories.CRAFTING_MISC;
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends LaserCuttingRecipe>
	{
		T create(Ingredient ingredient, ItemStackTemplate result, ItemStackTemplate secondaryResult, float secondaryChance);
	}

	public static <T extends LaserCuttingRecipe> RecipeSerializer<T> createSerializer(LaserCuttingRecipe.RecipeFactory<T> recipeFactory)
	{
		MapCodec<T> codec = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
						                    Ingredient.CODEC.fieldOf("ingredient").forGetter(LaserCuttingRecipe::getIngredient),
						                    ItemStackTemplate.CODEC.fieldOf("primary_result").forGetter(LaserCuttingRecipe::getPrimaryResult),
						                    ItemStackTemplate.CODEC.fieldOf("secondary_result").forGetter(LaserCuttingRecipe::getSecondaryResult),
						                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("secondary_chance").forGetter(LaserCuttingRecipe::getSecondaryChance)
				                    )
				                    .apply(instance, recipeFactory::create)
		);
		StreamCodec<RegistryFriendlyByteBuf, T> packetCodec = GalaxiesPacketUtil.quadruple(
				Ingredient.CONTENTS_STREAM_CODEC,
				LaserCuttingRecipe::getIngredient,
				ItemStackTemplate.STREAM_CODEC,
				LaserCuttingRecipe::getPrimaryResult,
				ItemStackTemplate.STREAM_CODEC,
				LaserCuttingRecipe::getSecondaryResult,
				ByteBufCodecs.FLOAT,
				LaserCuttingRecipe::getSecondaryChance,
				recipeFactory::create
		);
		return new RecipeSerializer<>(codec, packetCodec);
	}
}
