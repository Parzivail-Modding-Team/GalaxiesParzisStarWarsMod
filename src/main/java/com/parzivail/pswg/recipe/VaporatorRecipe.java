package com.parzivail.pswg.recipe;

import com.google.gson.JsonObject;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeSerializers;
import com.parzivail.pswg.container.SwgRecipeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public record VaporatorRecipe(Identifier id, Ingredient base, int duration,
                              ItemStack result) implements Recipe<Inventory>
{

	public boolean matches(Inventory inv, World world)
	{
		return this.base.test(inv.getStack(0));
	}

	public ItemStack craft(Inventory inv)
	{
		return this.getOutput().copy();
	}

	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height)
	{
		return width * height >= 1;
	}

	public ItemStack getOutput()
	{
		return this.result;
	}

	public int getDuration()
	{
		return duration;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack createIcon()
	{
		return new ItemStack(SwgBlocks.MoistureVaporator.Gx8);
	}

	public Identifier getId()
	{
		return this.id;
	}

	public RecipeSerializer<?> getSerializer()
	{
		return SwgRecipeSerializers.Vaporator;
	}

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
		public VaporatorRecipe read(Identifier identifier, JsonObject jsonObject)
		{
			var ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			var duration = JsonHelper.getInt(jsonObject, "duration");
			var itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
			return new VaporatorRecipe(identifier, ingredient, duration, itemStack);
		}

		public VaporatorRecipe read(Identifier identifier, PacketByteBuf packetByteBuf)
		{
			var ingredient = Ingredient.fromPacket(packetByteBuf);
			var duration = packetByteBuf.readInt();
			var itemStack = packetByteBuf.readItemStack();
			return new VaporatorRecipe(identifier, ingredient, duration, itemStack);
		}

		public void write(PacketByteBuf packetByteBuf, VaporatorRecipe recipe)
		{
			recipe.base.write(packetByteBuf);
			packetByteBuf.writeInt(recipe.duration);
			packetByteBuf.writeItemStack(recipe.result);
		}
	}
}
