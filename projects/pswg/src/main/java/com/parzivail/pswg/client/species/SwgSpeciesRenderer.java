package com.parzivail.pswg.client.species;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.Supplier;

public class SwgSpeciesRenderer
{
	public static final Identifier HUMANOID_BASE_MODEL_ID = Resources.id("species/humanoid_base");

	public static final HashMap<Identifier, SwgSpeciesModel> MODELS = new HashMap<>();

	//		register(SwgSpeciesRegistry.SPECIES_KAMINOAN, new ModelKaminoan<>(true, 0), new ModelKaminoan<>(false, 0));
	//		register(new SwgSpeciesModel(SwgSpeciesRegistry.SPECIES_WOOKIEE_M, new ModelWookiee<>(true, 0)));
	//		register(new SwgSpeciesModel(SwgSpeciesRegistry.SPECIES_WOOKIEE_F, new ModelWookiee<>(false, 0)));
	//		register(SwgSpeciesRegistry.SPECIES_BOTHAN, EMPTY_MODEL);

	public static Supplier<BipedEntityModel<LivingEntity>> getHumanModel()
	{
		return Client.NEM_MANAGER.getOverridingBipedModel(Resources.id("species/human"), HUMANOID_BASE_MODEL_ID, true);
	}

	public static Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> fullModel(Identifier id)
	{
		return Client.NEM_MANAGER.getPlayerModel(id, true);
	}

	public static Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> humanoidBase(Identifier id)
	{
		return Client.NEM_MANAGER.getOverridingPlayerModel(id, HUMANOID_BASE_MODEL_ID, true);
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier(), model);
	}

	private static void register(Identifier speciesSlug, SpeciesGender gender, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(new SwgSpeciesModel(SwgSpecies.toModel(speciesSlug, gender), Suppliers.memoize(model::get), animator));
	}

	public static void register(Identifier speciesSlug, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> male, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> female, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(speciesSlug, SpeciesGender.MALE, male, animator);
		register(speciesSlug, SpeciesGender.FEMALE, female, animator);
	}

	public static void register(Identifier speciesSlug, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> androgynousModel, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(speciesSlug, SpeciesGender.MALE, androgynousModel, animator);
		register(speciesSlug, SpeciesGender.FEMALE, androgynousModel, animator);
	}

	public static Identifier getTexture(LivingEntity player, SwgSpecies species)
	{
		var digest = species.digest();
		return Client.stackedTextureProvider.getId(String.format("species/%s", digest), () -> Client.TEX_TRANSPARENT, () -> species.getTextureStack(player));
	}
}
