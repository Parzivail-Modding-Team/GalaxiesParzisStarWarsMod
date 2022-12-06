package com.parzivail.pswg.recipe;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgRecipeSerializers;
import com.parzivail.pswg.container.SwgRecipeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.World;

public record VaporatorRecipe(Identifier id, Ingredient base, int duration,
                              ItemStack result) implements Recipe<Inventory>
{

	@Override
	public boolean matches(Inventory inv, World world)
	{
		return this.base.test(inv.getStack(0));
	}

	@Override
	public ItemStack craft(Inventory inv)
	{
		return this.getOutput().copy();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(int width, int height)
	{
		return width * height >= 1;
	}

	@Override
	public ItemStack getOutput()
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
	public Identifier getId()
	{
		return this.id;
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
		@Override
		public VaporatorRecipe read(Identifier identifier, JsonObject jsonObject)
		{
			var ingredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "ingredient"));
			var duration = JsonHelper.getInt(jsonObject, "duration");

			var result = JsonHelper.getObject(jsonObject, "result");
			try
			{
				var resultItem = result.get("item").getAsString();
				var resultCount = JsonHelper.getInt(result, "count", 1);
				var itemResult = ItemStringReader.item(Registries.ITEM.getReadOnlyWrapper(), new StringReader(resultItem));

				var itemStack = new ItemStack(itemResult.item(), resultCount);
				itemStack.setNbt(itemResult.nbt());
				return new VaporatorRecipe(identifier, ingredient, duration, itemStack);
			}
			catch (CommandSyntaxException e)
			{
				var crashReport = CrashReport.create(e, "Recipe parsing");
				var element = crashReport.addElement("Recipe");
				element.add("Identifier", identifier);
				throw new CrashException(crashReport);
			}
		}

		@Override
		public VaporatorRecipe read(Identifier identifier, PacketByteBuf packetByteBuf)
		{
			var ingredient = Ingredient.fromPacket(packetByteBuf);
			var duration = packetByteBuf.readInt();
			var itemStack = packetByteBuf.readItemStack();
			return new VaporatorRecipe(identifier, ingredient, duration, itemStack);
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
