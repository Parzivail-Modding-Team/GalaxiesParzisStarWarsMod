package dev.pswg.feature.scrapping.table;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.container.GadgetsRecipeSerializers;
import dev.pswg.container.GadgetsRecipeTypes;
import dev.pswg.util.GalaxiesPacketUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.Optional;

public class ScrappingTableRecipe implements Recipe<ScrappingTableRecipeInput>
{
	private final Ingredient tool;
	private final Ingredient ingredient;
	private final ItemStack primaryResult;
	private final ItemStack secondaryResult;
	private final float secondaryChance;
	private PlacementInfo ingredientPlacement;

	public ScrappingTableRecipe(Ingredient tool, Ingredient ingredient, ItemStack primaryResult, ItemStack secondaryResult, float secondaryChance)
	{
		this.tool = tool;
		this.ingredient = ingredient;
		this.primaryResult = primaryResult;
		this.secondaryResult = secondaryResult;
		this.secondaryChance = secondaryChance;
		ingredientPlacement = PlacementInfo.createFromOptionals(List.of(tool(), scrapItem()));
	}

	@Override
	public RecipeType<ScrappingTableRecipe> getType()
	{
		return GadgetsRecipeTypes.SCRAPPING;
	}

	@Override
	public PlacementInfo placementInfo()
	{
		return ingredientPlacement;
	}

	@Override
	public RecipeBookCategory recipeBookCategory()
	{
		return null;
	}

	@Override
	public boolean matches(ScrappingTableRecipeInput input, Level world)
	{
		return Ingredient.testOptionalIngredient(this.scrapItem(), input.scrapItem) && (this.tool().isEmpty() || this.tool().get().test(input.tool));
	}

	@Override
	public ItemStack assemble(ScrappingTableRecipeInput input, HolderLookup.Provider registries)
	{
		return primaryResult.copy();
	}

	public ItemStack craftSecondary()
	{
		return secondaryResult;
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
		private final StreamCodec<RegistryFriendlyByteBuf, T> packetCodec;

		public Serializer(ScrappingTableRecipe.RecipeFactory<T> recipeFactory)
		{
			this.codec = RecordCodecBuilder.mapCodec(
					instance -> instance.group(
							                    Ingredient.CODEC.fieldOf("tool").forGetter(ScrappingTableRecipe::getTool),
							                    Ingredient.CODEC.fieldOf("ingredient").forGetter(ScrappingTableRecipe::getIngredient),
							                    ItemStack.STRICT_CODEC.fieldOf("primary_result").forGetter(ScrappingTableRecipe::getPrimaryResult),
							                    ItemStack.STRICT_CODEC.fieldOf("secondary_result").forGetter(ScrappingTableRecipe::getSecondaryResult),
							                    ExtraCodecs.POSITIVE_FLOAT.fieldOf("secondary_chance").forGetter(ScrappingTableRecipe::getSecondaryChance)
					                    )
					                    .apply(instance, recipeFactory::create)
			);
			this.packetCodec = GalaxiesPacketUtil.quintuple(
					Ingredient.CONTENTS_STREAM_CODEC,
					ScrappingTableRecipe::getTool,
					Ingredient.CONTENTS_STREAM_CODEC,
					ScrappingTableRecipe::getIngredient,
					ItemStack.STREAM_CODEC,
					ScrappingTableRecipe::getPrimaryResult,
					ItemStack.STREAM_CODEC,
					ScrappingTableRecipe::getSecondaryResult,
					ByteBufCodecs.FLOAT,
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
		public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
		{
			return this.packetCodec;
		}
	}

}
