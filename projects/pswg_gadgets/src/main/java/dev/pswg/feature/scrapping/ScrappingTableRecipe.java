package dev.pswg.feature.scrapping;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.container.GadgetsRecipeSerializers;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.packet.GadgetsPacketUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class ScrappingTableRecipe implements Recipe<ScrappingTableRecipeInput>
{
	private final Ingredient tool;
	private final Ingredient ingredient;
	private final ItemStack primaryResult;
	private final ItemStack secondaryResult;
	private final float secondaryChance;
	private IngredientPlacement ingredientPlacement;

	public ScrappingTableRecipe(Ingredient tool, Ingredient ingredient, ItemStack primaryResult, ItemStack secondaryResult, float secondaryChance)
	{
		this.tool = tool;
		this.ingredient = ingredient;
		this.primaryResult = primaryResult;
		this.secondaryResult = secondaryResult;
		this.secondaryChance = secondaryChance;
		ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(tool(), scrapItem()));
	}

	@Override
	public RecipeType<ScrappingTableRecipe> getType()
	{
		return GadgetsRecipeTypes.SCRAPPING;
	}

	@Override
	public IngredientPlacement getIngredientPlacement()
	{
		return ingredientPlacement;
	}

	@Override
	public RecipeBookCategory getRecipeBookCategory()
	{
		return null;
	}

	@Override
	public boolean matches(ScrappingTableRecipeInput input, World world)
	{
		return Ingredient.matches(this.scrapItem(), input.scrapItem) && (this.tool().isEmpty() || this.tool().get().test(input.tool));
	}

	@Override
	public ItemStack craft(ScrappingTableRecipeInput input, RegistryWrapper.WrapperLookup registries)
	{
		return primaryResult;
	}

	public ItemStack craftSecondary()
	{
		return secondaryResult;
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
	public RecipeSerializer<? extends Recipe<ScrappingTableRecipeInput>> getSerializer()
	{
		return GadgetsRecipeSerializers.SCRAPPING_SERIALIZER;
	}

	Optional<Ingredient> scrapItem()
	{
		return Optional.ofNullable(ingredient);
	}

	Ingredient getIngredient()
	{
		return ingredient;
	}

	public float getSecondaryChance()
	{
		return secondaryChance;
	}

	Optional<Ingredient> tool()
	{
		return Optional.ofNullable(tool);
	}

	Ingredient getTool()
	{
		return tool;
	}

	ItemStack getPrimaryResult()
	{
		return primaryResult.copy();
	}

	ItemStack getSecondaryResult()
	{
		return secondaryResult.copy();
	}

	@FunctionalInterface
	public interface RecipeFactory<T extends ScrappingTableRecipe>
	{
		T create(Ingredient tool, Ingredient ingredient, ItemStack result, ItemStack secondaryResult, float secondaryChance);
	}

	public static class Serializer<T extends ScrappingTableRecipe> implements RecipeSerializer<T>
	{
		private final MapCodec<T> codec;
		private final PacketCodec<RegistryByteBuf, T> packetCodec;

		public Serializer(ScrappingTableRecipe.RecipeFactory<T> recipeFactory)
		{
			this.codec = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							                    Ingredient.CODEC.fieldOf("tool").forGetter(ScrappingTableRecipe::getTool),
							                    Ingredient.CODEC.fieldOf("ingredient").forGetter(ScrappingTableRecipe::getIngredient),
							                    ItemStack.VALIDATED_CODEC.fieldOf("primary_result").forGetter(ScrappingTableRecipe::getPrimaryResult),
							                    ItemStack.VALIDATED_CODEC.fieldOf("secondary_result").forGetter(ScrappingTableRecipe::getSecondaryResult),
							                    Codecs.POSITIVE_FLOAT.fieldOf("secondary_chance").forGetter(ScrappingTableRecipe::getSecondaryChance)
					                    )
					                    .apply(instance, recipeFactory::create)
			);
			this.packetCodec = GadgetsPacketUtil.quintuple(
					Ingredient.PACKET_CODEC,
					ScrappingTableRecipe::getTool,
					Ingredient.PACKET_CODEC,
					ScrappingTableRecipe::getIngredient,
					ItemStack.PACKET_CODEC,
					ScrappingTableRecipe::getPrimaryResult,
					ItemStack.PACKET_CODEC,
					ScrappingTableRecipe::getSecondaryResult,
					PacketCodecs.FLOAT,
					ScrappingTableRecipe::getSecondaryChance,
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
