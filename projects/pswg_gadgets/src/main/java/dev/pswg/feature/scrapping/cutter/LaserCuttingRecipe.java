package dev.pswg.feature.scrapping.cutter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.container.GadgetsRecipeSerializers;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.util.GalaxiesPacketUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

public class LaserCuttingRecipe implements Recipe<SingleStackRecipeInput>
{

	private final Ingredient ingredient;
	private final ItemStack primaryResult;
	private final ItemStack secondaryResult;
	private final float secondaryChance;

	public LaserCuttingRecipe(Ingredient ingredient, ItemStack primaryResult, ItemStack secondaryResult, float secondaryChance)
	{
		this.ingredient = ingredient;
		this.primaryResult = primaryResult;
		this.secondaryResult = secondaryResult;
		this.secondaryChance = secondaryChance;
	}

	@Override
	public boolean matches(SingleStackRecipeInput input, World world)
	{
		return ingredient.test(input.item());
	}

	@Override
	public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup registries)
	{
		return primaryResult.copy();
	}

	public ItemStack craftSecondary()
	{
		return secondaryResult.copy();
	}

	public Ingredient getIngredient()
	{
		return ingredient;
	}

	public ItemStack getPrimaryResult()
	{
		return primaryResult;
	}

	public ItemStack getSecondaryResult()
	{
		return secondaryResult;
	}

	public float getSecondaryChance()
	{
		return secondaryChance;
	}

	@Override
	public RecipeSerializer<? extends Recipe<SingleStackRecipeInput>> getSerializer()
	{
		return GadgetsRecipeSerializers.LASER_CUTTING_SERIALIZER;
	}

	@Override
	public boolean isIgnoredInRecipeBook()
	{
		return true;
	}

	@Override
	public boolean showNotification()
	{
		return false;
	}

	@Override
	public RecipeType<? extends Recipe<SingleStackRecipeInput>> getType()
	{
		return GadgetsRecipeTypes.CUTTING;
	}

	@Override
	public IngredientPlacement getIngredientPlacement()
	{
		return IngredientPlacement.NONE;
	}

	@Override
	public RecipeBookCategory getRecipeBookCategory()
	{
		return null;
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends LaserCuttingRecipe>
	{
		T create(Ingredient ingredient, ItemStack result, ItemStack secondaryResult, float secondaryChance);
	}

	public static class Serializer<T extends LaserCuttingRecipe> implements RecipeSerializer<T>
	{
		private final MapCodec<T> codec;
		private final PacketCodec<RegistryByteBuf, T> packetCodec;

		public Serializer(LaserCuttingRecipe.RecipeFactory<T> recipeFactory)
		{
			this.codec = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							                    Ingredient.CODEC.fieldOf("ingredient").forGetter(LaserCuttingRecipe::getIngredient),
							                    ItemStack.VALIDATED_CODEC.fieldOf("primary_result").forGetter(LaserCuttingRecipe::getPrimaryResult),
							                    ItemStack.VALIDATED_CODEC.fieldOf("secondary_result").forGetter(LaserCuttingRecipe::getSecondaryResult),
							                    Codecs.POSITIVE_FLOAT.fieldOf("secondary_chance").forGetter(LaserCuttingRecipe::getSecondaryChance)
					                    )
					                    .apply(instance, recipeFactory::create)
			);
			this.packetCodec = GalaxiesPacketUtil.quadruple(
					Ingredient.PACKET_CODEC,
					LaserCuttingRecipe::getIngredient,
					ItemStack.PACKET_CODEC,
					LaserCuttingRecipe::getPrimaryResult,
					ItemStack.PACKET_CODEC,
					LaserCuttingRecipe::getSecondaryResult,
					PacketCodecs.FLOAT,
					LaserCuttingRecipe::getSecondaryChance,
					recipeFactory::create
			);
		}

		@Override
		public MapCodec<T> codec()
		{
			return this.codec;
		}

		@Override
		public PacketCodec<RegistryByteBuf, T> packetCodec()
		{
			return this.packetCodec;
		}
	}
}
