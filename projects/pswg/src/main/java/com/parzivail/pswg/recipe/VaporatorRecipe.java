package com.parzivail.pswg.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeSerializers;
import com.parzivail.pswg.container.SwgRecipeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record VaporatorRecipe(Ingredient base, int duration, ItemStack result) implements Recipe<SingleStackRecipeInput>
{

	@Override
	public boolean matches(SingleStackRecipeInput input, World world)
	{
		return this.base.test(input.item());
	}

	@Override
	public ItemStack craft(SingleStackRecipeInput input, RegistryWrapper.WrapperLookup lookup)
	{
		return this.getResult(lookup).copy();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height)
	{
		return width * height >= 1;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup)
	{
		return this.result;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack createIcon()
	{
		return new ItemStack(SwgBlocks.MoistureVaporator.Gx8);
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return SwgRecipeSerializers.Vaporator;
	}

	@Override
	public RecipeType<?> getType()
	{
		return SwgRecipeType.Vaporator;
	}

	@Override
	public DefaultedList<Ingredient> getIngredients()
	{
		var ingredients = DefaultedList.<Ingredient>of();
		ingredients.add(this.base);
		return ingredients;
	}

	public static class Serializer implements RecipeSerializer<VaporatorRecipe>
	{
		private static final MapCodec<VaporatorRecipe> CODEC = RecordCodecBuilder.mapCodec(
				builder -> builder.group(
						Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(VaporatorRecipe::base),
						Codec.INT.fieldOf("duration").forGetter(VaporatorRecipe::duration),
						ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(VaporatorRecipe::result)
				).apply(builder, VaporatorRecipe::new)
		);

		private static final PacketCodec<RegistryByteBuf, VaporatorRecipe> PACKET_CODEC = PacketCodec.tuple(
				Ingredient.PACKET_CODEC, VaporatorRecipe::base,
				PacketCodecs.INTEGER, VaporatorRecipe::duration,
				ItemStack.PACKET_CODEC, VaporatorRecipe::result,
				VaporatorRecipe::new
		);

		@Override
		public MapCodec<VaporatorRecipe> codec()
		{
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, VaporatorRecipe> packetCodec()
		{
			return PACKET_CODEC;
		}
	}
}
