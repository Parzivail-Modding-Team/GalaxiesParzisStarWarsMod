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
import net.minecraft.world.World;

public class VaporatorRecipe implements Recipe<Inventory>
{
	private final Ingredient base;
	private final int duration;
	private final ItemStack result;
	private final Identifier id;

	public VaporatorRecipe(Identifier id, Ingredient base, int duration, ItemStack result)
	{
		this.id = id;
		this.base = base;
		this.duration = duration;
		this.result = result;
	}

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

	public static class Serializer implements RecipeSerializer<VaporatorRecipe>
	{
		public VaporatorRecipe read(Identifier identifier, JsonObject jsonObject)
		{
			Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			int duration = JsonHelper.getInt(jsonObject, "duration");
			ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
			return new VaporatorRecipe(identifier, ingredient, duration, itemStack);
		}

		public VaporatorRecipe read(Identifier identifier, PacketByteBuf packetByteBuf)
		{
			Ingredient ingredient = Ingredient.fromPacket(packetByteBuf);
			int duration = packetByteBuf.readInt();
			ItemStack itemStack = packetByteBuf.readItemStack();
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
