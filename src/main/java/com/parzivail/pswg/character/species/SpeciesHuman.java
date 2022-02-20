package com.parzivail.pswg.character.species;

import com.parzivail.pswg.Client;
import com.parzivail.pswg.character.DatapackedSpeciesVariable;
import com.parzivail.pswg.character.SpeciesVariable;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesHuman extends SwgSpecies
{
	private static final SpeciesVariable VAR_SKINTONE = new DatapackedSpeciesVariable(SwgSpeciesRegistry.SPECIES_HUMAN, "skin_tone");

	public SpeciesHuman(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_HUMAN;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] {
				VAR_SKINTONE,
				VAR_HUMANOID_EYEBROWS,
				VAR_HUMANOID_HAIR,
				VAR_HUMANOID_EYE_COLOR,
				VAR_HUMANOID_CLOTHES_TOPS,
				VAR_HUMANOID_CLOTHES_BOTTOMS,
				VAR_HUMANOID_CLOTHES_BELTS,
				VAR_HUMANOID_CLOTHES_BOOTS,
				VAR_HUMANOID_CLOTHES_GLOVES,
				VAR_HUMANOID_CLOTHES_ACCESSORIES,
				VAR_HUMANOID_CLOTHES_OUTERWEAR
		};
	}

	private static Identifier getClothes(SwgSpecies species, PlayerEntity player)
	{
		return Client.remoteTextureProvider.getId(
				String.format("character/%s", player.getUuidAsString()),
				() -> getClothingStack(species, player)
		);
	}

	private static int hashVariableValues(SwgSpecies species, SpeciesVariable... variables)
	{
		var hash = 0;
		for (var variable : variables)
			hash = 31 * hash + species.getVariable(variable).hashCode();
		return hash;
	}

	private static Identifier getClothingStack(SwgSpecies species, PlayerEntity player)
	{
		var hashCode = hashVariableValues(species,
		                                  VAR_HUMANOID_CLOTHES_TOPS,
		                                  VAR_HUMANOID_CLOTHES_BOTTOMS,
		                                  VAR_HUMANOID_CLOTHES_BELTS,
		                                  VAR_HUMANOID_CLOTHES_BOOTS,
		                                  VAR_HUMANOID_CLOTHES_GLOVES,
		                                  VAR_HUMANOID_CLOTHES_ACCESSORIES,
		                                  VAR_HUMANOID_CLOTHES_OUTERWEAR
		);
		return Client.stackedTextureProvider.getId(String.format("clothing/%08x", hashCode), () -> Client.TEX_TRANSPARENT, () -> createClothingStack(species, player));
	}

	private static Collection<Identifier> createClothingStack(SwgSpecies species, PlayerEntity player)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_TOPS));
		stack.add(getTexture(species, VAR_HUMANOID_CLOTHES_BOTTOMS));
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_BELTS);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_BOOTS);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_GLOVES);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_ACCESSORIES);
		appendOptionalLayer(stack, species, VAR_HUMANOID_CLOTHES_OUTERWEAR);
		return stack;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_SKINTONE));
		stack.add(getGenderedTexture(this, VAR_HUMANOID_EYEBROWS));
		stack.add(tint(getGlobalTexture("eyes"), this, VAR_HUMANOID_EYE_COLOR));
		stack.add(getClothes(this, player));
		stack.add(getTexture(this, VAR_HUMANOID_HAIR));
		return stack;
	}
}
