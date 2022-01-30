package com.parzivail.pswg.species.species;

import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.pswg.species.SpeciesVariable;
import com.parzivail.pswg.species.SwgSpecies;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class SpeciesTogruta extends SwgSpecies
{
	private static final SpeciesVariable VAR_BODY = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA,
	                                                                    "body",
	                                                                    "orange",
	                                                                    "blue",
	                                                                    "green",
	                                                                    "light_blue",
	                                                                    "lilac",
	                                                                    "orange",
	                                                                    "pink",
	                                                                    "red",
	                                                                    "yellow"
	);

	private static final SpeciesVariable VAR_FACE = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA,
	                                                                    "face",
	                                                                    SpeciesVariable.NONE,
	                                                                    SpeciesVariable.NONE,
	                                                                    "1",
	                                                                    "2",
	                                                                    "3",
	                                                                    "4",
	                                                                    "5",
	                                                                    "6",
	                                                                    "7"
	);

	private static final SpeciesVariable VAR_EYEBROWS = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA,
	                                                                        "eyebrow",
	                                                                        "default",
	                                                                        "default"
	);

	private static final SpeciesVariable VAR_LOWER_MONTRAL = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA,
	                                                                             "lower_montral",
	                                                                             "cream",
	                                                                             "beige",
	                                                                             "cream",
	                                                                             "white"
	);

	private static final SpeciesVariable VAR_UPPER_MONTRAL = new SpeciesVariable(SwgSpeciesRegistry.SPECIES_TOGRUTA,
	                                                                             "upper_montral",
	                                                                             "blue",
	                                                                             SpeciesVariable.NONE,
	                                                                             "blue",
	                                                                             "brown",
	                                                                             "dark_blue",
	                                                                             "green",
	                                                                             "light_green",
	                                                                             "pink",
	                                                                             "purple",
	                                                                             "red",
	                                                                             "turquoise"
	);

	public SpeciesTogruta(String serialized)
	{
		super(serialized);
	}

	@Override
	public Identifier getSlug()
	{
		return SwgSpeciesRegistry.SPECIES_TOGRUTA;
	}

	@Override
	public SpeciesVariable[] getVariables()
	{
		return new SpeciesVariable[] { VAR_BODY, VAR_FACE, VAR_EYEBROWS, VAR_LOWER_MONTRAL, VAR_UPPER_MONTRAL };
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Collection<Identifier> getTextureStack(PlayerEntity player, SwgSpecies species)
	{
		var stack = new ArrayList<Identifier>();
		stack.add(getGenderedTexture(this, VAR_BODY));
		stack.add(getGenderedTexture(this, VAR_LOWER_MONTRAL));

		if (SpeciesVariable.isNotEmpty(this, VAR_UPPER_MONTRAL))
			stack.add(getGenderedTexture(this, VAR_UPPER_MONTRAL));

		if (SpeciesVariable.isNotEmpty(this, VAR_FACE))
			stack.add(getTexture(this, VAR_FACE));

		stack.add(getGlobalTexture("eyes"));
		stack.add(getGenderedTexture(this, VAR_EYEBROWS));
		stack.add(getClothes(player, gender));
		return stack;
	}
}
