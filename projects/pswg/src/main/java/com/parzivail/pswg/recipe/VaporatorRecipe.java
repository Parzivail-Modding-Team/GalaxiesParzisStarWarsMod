package com.parzivail.pswg.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeSerializers;
import com.parzivail.pswg.container.SwgRecipeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Optional;

public record VaporatorRecipe(Ingredient base, int duration, ItemStack result) implements Recipe<Inventory>
{

	@Override
	public boolean matches(Inventory inv, World world)
	{
		return this.base.test(inv.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv, DynamicRegistryManager registryManager)
	{
		return this.getResult(registryManager).copy();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height)
	{
		return width * height >= 1;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager registryManager)
	{
		return this.result;
	}

	public int getDuration()
	{
		return duration;
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
		private static final Codec<VaporatorRecipe> CODEC = RecordCodecBuilder.create(
				builder -> builder.group(
						Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(VaporatorRecipe::base),
						Codec.INT.fieldOf("duration").forGetter(VaporatorRecipe::duration),
						RecordCodecBuilder.<ItemStack>create(instance -> instance.group(
								Registries.ITEM.getCodec().fieldOf("item").forGetter(ItemStack::getItem),
								Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
								NbtCompound.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.getNbt()))
						).apply(instance, (item, integer, nbtCompound) -> {
							var stack = new ItemStack(item, integer);
							nbtCompound.ifPresent(stack::setNbt);
							return stack;
						})).fieldOf("result").forGetter(VaporatorRecipe::result)
				).apply(builder, VaporatorRecipe::new)
		);

		@Override
		public Codec<VaporatorRecipe> codec()
		{
			return CODEC;
		}

		@Override
		public VaporatorRecipe read(PacketByteBuf packetByteBuf)
		{
			var ingredient = Ingredient.fromPacket(packetByteBuf);
			var duration = packetByteBuf.readInt();
			var itemStack = packetByteBuf.readItemStack();
			return new VaporatorRecipe(ingredient, duration, itemStack);
		}

		@Override
		public void write(PacketByteBuf packetByteBuf, VaporatorRecipe recipe)
		{
			recipe.base.write(packetByteBuf);
			packetByteBuf.writeInt(recipe.duration);
			packetByteBuf.writeItemStack(recipe.result);
		}
	}
}
