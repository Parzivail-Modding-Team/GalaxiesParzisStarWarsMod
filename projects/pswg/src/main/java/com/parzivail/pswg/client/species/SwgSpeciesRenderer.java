package com.parzivail.pswg.client.species;

import com.google.common.base.Suppliers;
import com.parzivail.pswg.Client;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.character.SpeciesGender;
import com.parzivail.pswg.character.SwgSpecies;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswg.client.render.player.PlayerSpeciesModelRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import com.parzivail.util.math.MathUtil;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class SwgSpeciesRenderer
{
	private static final HashMap<SwgSpecies, ModelPart> FEMALE_CUBE_CACHE = new HashMap<>();

	public static final HashMap<Identifier, SwgSpeciesModel> MODELS = new HashMap<>();

	static
	{
		//		register(SwgSpeciesRegistry.SPECIES_KAMINOAN, new ModelKaminoan<>(true, 0), new ModelKaminoan<>(false, 0));
		//		register(new SwgSpeciesModel(SwgSpeciesRegistry.SPECIES_WOOKIEE_M, new ModelWookiee<>(true, 0)));
		//		register(new SwgSpeciesModel(SwgSpeciesRegistry.SPECIES_WOOKIEE_F, new ModelWookiee<>(false, 0)));
		//		register(SwgSpeciesRegistry.SPECIES_BOTHAN, EMPTY_MODEL);
		register(SwgSpeciesRegistry.SPECIES_AQUALISH, nemSource(Resources.id("species/aqualish")), null);
		register(SwgSpeciesRegistry.SPECIES_BITH, nemSource(Resources.id("species/bith")), null);
		register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, nemSource(Resources.id("species/chagrian")), null);
		register(SwgSpeciesRegistry.SPECIES_KAMINOAN, nemSource(Resources.id("species/kaminoan")), null);
		register(SwgSpeciesRegistry.SPECIES_JAWA, nemSource(Resources.id("species/jawa")), null);
		register(SwgSpeciesRegistry.SPECIES_TOGRUTA, nemSource(Resources.id("species/togruta_m")), nemSource(Resources.id("species/togruta_f")), null);
		register(SwgSpeciesRegistry.SPECIES_TWILEK, nemSource(Resources.id("species/twilek")), SwgSpeciesRenderer::animateTwilek);
		register(SwgSpeciesRegistry.SPECIES_HUMAN, nemSource(Resources.id("species/human")), null);
		register(SwgSpeciesRegistry.SPECIES_CHISS, nemSource(Resources.id("species/human")), null);
		register(SwgSpeciesRegistry.SPECIES_PANTORAN, nemSource(Resources.id("species/human")), null);
		register(SwgSpeciesRegistry.SPECIES_WOOKIEE, nemSource(Resources.id("species/wookiee")), null);
	}

	private static Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> nemSource(Identifier id)
	{
		return NemManager.INSTANCE.getPlayerModel(id, true);
	}

	private static void register(SwgSpeciesModel model)
	{
		MODELS.put(model.identifier(), model);
	}

	private static void register(Identifier speciesSlug, SpeciesGender gender, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> model, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(new SwgSpeciesModel(SpeciesGender.toModel(speciesSlug, gender), Suppliers.memoize(model::get), animator));
	}

	private static void register(Identifier speciesSlug, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> male, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> female, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(speciesSlug, SpeciesGender.MALE, male, animator);
		register(speciesSlug, SpeciesGender.FEMALE, female, animator);
	}

	private static void register(Identifier speciesSlug, Supplier<PlayerEntityModel<AbstractClientPlayerEntity>> androgynousModel, PlayerSpeciesModelRenderer.Animator animator)
	{
		register(speciesSlug, SpeciesGender.MALE, androgynousModel, animator);
		register(speciesSlug, SpeciesGender.FEMALE, androgynousModel, animator);
	}

	public static Identifier getTexture(PlayerEntity player, SwgSpecies species)
	{
		var hashCode = species.hashCode();
		return Client.stackedTextureProvider.getId(String.format("species/%08x", hashCode), () -> Client.TEX_TRANSPARENT, () -> species.getTextureStack(player));
	}

	public static void mutateModel(PlayerEntity entity, SwgSpecies species, PlayerEntityRenderer renderer)
	{
		var model = renderer.getModel();
		if (!FEMALE_CUBE_CACHE.containsKey(species))
		{
			try
			{
				var chest = model.body.getChild("chest");
				FEMALE_CUBE_CACHE.put(species, chest);
			}
			catch (NoSuchElementException e)
			{
				FEMALE_CUBE_CACHE.put(species, null);
			}
		}

		var chest = FEMALE_CUBE_CACHE.get(species);
		if (chest == null)
			return;

		var isFemale = species.getGender() == SpeciesGender.FEMALE;
		var armorHidesCube = false;

		var armorPair = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
		if (armorPair != null)
		{
			var metadata = ArmorRenderer.getMetadata(armorPair.getLeft());
			armorHidesCube = metadata.femaleModelAction() == ArmorRenderer.FemaleChestplateAction.HIDE_CUBE;
		}
		else
		{
			var vanillaArmor = ArmorRenderer.getVanillaArmor(entity, EquipmentSlot.CHEST);
			if (vanillaArmor != null)
				armorHidesCube = true;
		}

		chest.visible = isFemale && !armorHidesCube;
	}

	public static void animateTwilek(AbstractClientPlayerEntity entity, PlayerEntityModel<AbstractClientPlayerEntity> model, PlayerSpeciesModelRenderer renderer, float tickDelta)
	{
		var h = entity.getPitch(tickDelta) / MathUtil.toDegreesf;
		var h2 = (float)Math.pow(h, 2);
		var h3 = (float)Math.pow(h, 3);
		var h4 = (float)Math.pow(h, 4);

		var tailBaseL = model.head.getChild("TailBaseL");
		var tailMidL = tailBaseL.getChild("TailMidL");
		var tailLowerL = tailMidL.getChild("TailLowerL");

		var tailBaseR = model.head.getChild("TailBaseR");
		var tailMidR = tailBaseR.getChild("TailMidR");
		var tailLowerR = tailMidR.getChild("TailLowerR");

		// https://www.desmos.com/calculator/52kcd69qgc
		tailBaseL.pitch = tailBaseR.pitch = (-2.34f * h3 + 11.05f * h2 + 3.4f * h + 7.06f) / MathUtil.toDegreesf;
		tailMidL.pitch = tailMidR.pitch = (5.11f * h4 + 2.01f * h3 - 10.2f * h2 - 26.38f * h - 1.48f) / MathUtil.toDegreesf;
		tailLowerL.pitch = tailLowerR.pitch = (-3.15f * h4 + 2.57f * h2 - 23.03f * h - 2.93f) / MathUtil.toDegreesf;

		var y = MathHelper.wrapDegrees(entity.getYaw(tickDelta) - MathHelper.lerp(tickDelta, entity.prevBodyYaw, entity.bodyYaw)) / MathUtil.toDegreesf;
		tailBaseL.roll = Math.max(0, y / 3f);
		tailBaseR.roll = Math.min(0, y / 3f);
	}
}
