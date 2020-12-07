package com.parzivail.pswg;

import com.parzivail.pswg.client.species.SwgSpeciesInstance;
import com.parzivail.pswg.command.SpeciesArgumentType;
import com.parzivail.pswg.command.SpeciesVariantArgumentType;
import com.parzivail.pswg.component.SwgEntityComponents;
import com.parzivail.pswg.component.SwgPersistentComponents;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.dimension.DimensionTeleporter;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.handler.PlayerPacketHandler;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.Objects;

public class Galaxies implements ModInitializer
{
	public static final ItemGroup Tab = FabricItemGroupBuilder
			.create(Resources.identifier("main"))
			.icon(() -> new ItemStack(SwgItems.Lightsaber.Lightsaber))
			.build();

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		// TODO: dual weilding one on and one off lightsaber might play the sound twice
		// TODO: new blaster models

		TrackedDataHandlers.register();

		SwgRecipeType.register();
		SwgRecipeSerializers.register();

		SwgEntities.register();

		SwgStructures.General.register();

		SwgSounds.register();

		SwgBlocks.register(SwgBlocks.Sand.Tatooine, Resources.identifier("sand_tatooine"));

		SwgBlocks.register(SwgBlocks.Stone.Temple, Resources.identifier("stone_temple"));
		SwgBlocks.register(SwgBlocks.Stone.TempleBricks, Resources.identifier("stone_temple_bricks"));
		SwgBlocks.register(SwgBlocks.Stone.TempleBricksChisled, Resources.identifier("stone_temple_bricks_chisled"));
		SwgBlocks.register(SwgBlocks.Stone.TempleSlabSideSmooth, Resources.identifier("stone_temple_slab_side_smooth"));
		SwgBlocks.register(SwgBlocks.Stone.TempleSmooth, Resources.identifier("stone_temple_smooth"));

		SwgBlocks.register(SwgBlocks.Crate.OctagonBlockEntityType, Resources.identifier("crate_octagon"));
		SwgBlocks.register(SwgBlocks.Crate.OctagonOrange, Resources.identifier("crate_octagon_orange"));
		SwgBlocks.register(SwgBlocks.Crate.OctagonGray, Resources.identifier("crate_octagon_gray"));
		SwgBlocks.register(SwgBlocks.Crate.OctagonBlack, Resources.identifier("crate_octagon_black"));

		SwgBlocks.register(SwgBlocks.Crate.MosEisley, SwgBlocks.Crate.MosEisleyBlockEntityType, Resources.identifier("crate_mos_eisley"));

		SwgBlocks.register(SwgBlocks.Leaves.Sequoia, Resources.identifier("leaves_sequoia"));

		SwgBlocks.register(SwgBlocks.Log.Sequoia, Resources.identifier("log_sequoia"));

		SwgBlocks.register(SwgBlocks.Panel.ImperialBase, Resources.identifier("panel_imperial_base"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialBlackBase, Resources.identifier("panel_imperial_black_base"));
		SwgBlocks.register(SwgBlocks.Panel.Imperial1, Resources.identifier("panel_imperial_1"));
		SwgBlocks.register(SwgBlocks.Panel.Imperial2, Resources.identifier("panel_imperial_2"));
		SwgBlocks.register(SwgBlocks.Panel.Imperial3, Resources.identifier("panel_imperial_3"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLight1, Resources.identifier("panel_imperial_light_1"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLight2, Resources.identifier("panel_imperial_light_2"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLight3, Resources.identifier("panel_imperial_light_3"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLight4, Resources.identifier("panel_imperial_light_4"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLight5, Resources.identifier("panel_imperial_light_5"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLight6, Resources.identifier("panel_imperial_light_6"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLightTall1, Resources.identifier("panel_imperial_light_tall_1"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLightTall2, Resources.identifier("panel_imperial_light_tall_2"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLightTall3, Resources.identifier("panel_imperial_light_tall_3"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLightTall4, Resources.identifier("panel_imperial_light_tall_4"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLightDecoy, Resources.identifier("panel_imperial_light_decoy"));
		SwgBlocks.register(SwgBlocks.Panel.ImperialLightOff, Resources.identifier("panel_imperial_light_off"));

		SwgBlocks.register(SwgBlocks.Machine.Spoked, Resources.identifier("machine_spoked"));

		SwgBlocks.register(SwgBlocks.MoistureVaporator.Gx8, SwgBlocks.MoistureVaporator.Gx8BlockEntityType, Resources.identifier("moisture_vaporator_gx8"));

		SwgBlocks.register(SwgBlocks.Ore.Chromium, Resources.identifier("ore_chromium"));
		SwgBlocks.register(SwgBlocks.Ore.Cortosis, Resources.identifier("ore_cortosis"));
		SwgBlocks.register(SwgBlocks.Ore.Diatium, Resources.identifier("ore_diatium"));
		SwgBlocks.register(SwgBlocks.Ore.Exonium, Resources.identifier("ore_exonium"));
		SwgBlocks.register(SwgBlocks.Ore.Helicite, Resources.identifier("ore_helicite"));
		SwgBlocks.register(SwgBlocks.Ore.Ionite, Resources.identifier("ore_ionite"));
		SwgBlocks.register(SwgBlocks.Ore.Kelerium, Resources.identifier("ore_kelerium"));
		SwgBlocks.register(SwgBlocks.Ore.Rubindum, Resources.identifier("ore_rubindum"));
		SwgBlocks.register(SwgBlocks.Ore.Thorolide, Resources.identifier("ore_thorolide"));
		SwgBlocks.register(SwgBlocks.Ore.Titanium, Resources.identifier("ore_titanium"));

		SwgBlocks.register(SwgBlocks.Workbench.Blaster, SwgBlocks.Workbench.BlasterBlockEntityType, Resources.identifier("workbench_blaster"));

		Registry.register(Registry.ITEM, Resources.identifier("lightsaber"), SwgItems.Lightsaber.Lightsaber);

		Registry.register(Registry.ITEM, Resources.identifier("a280"), SwgItems.Blaster.A280);
		Registry.register(Registry.ITEM, Resources.identifier("e11"), SwgItems.Blaster.E11);

		Registry.register(Registry.ITEM, Resources.identifier("debug"), SwgItems.Debug.Debug);

		Registry.register(Registry.ITEM, Resources.identifier("salt_pile"), SwgItems.Food.SaltPile);

		Registry.register(Registry.ITEM, Resources.identifier("jogan_fruit"), SwgItems.Food.JoganFruit);
		Registry.register(Registry.ITEM, Resources.identifier("chasuka"), SwgItems.Food.Chasuka);
		Registry.register(Registry.ITEM, Resources.identifier("meiloorun"), SwgItems.Food.Meiloorun);
		Registry.register(Registry.ITEM, Resources.identifier("mynock_wing"), SwgItems.Food.MynockWing);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_mynock_wing"), SwgItems.Food.FriedMynockWing);
		Registry.register(Registry.ITEM, Resources.identifier("bantha_chop"), SwgItems.Food.BanthaChop);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_bantha_chop"), SwgItems.Food.BanthaSteak);
		Registry.register(Registry.ITEM, Resources.identifier("nerf_chop"), SwgItems.Food.NerfChop);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_nerf_chop"), SwgItems.Food.NerfSteak);
		Registry.register(Registry.ITEM, Resources.identifier("gizka_chop"), SwgItems.Food.GizkaChop);
		Registry.register(Registry.ITEM, Resources.identifier("cooked_gizka_chop"), SwgItems.Food.GizkaSteak);
		Registry.register(Registry.ITEM, Resources.identifier("flangth_takeout"), SwgItems.Food.FlangthTakeout);
		Registry.register(Registry.ITEM, Resources.identifier("flangth_plate"), SwgItems.Food.FlangthPlate);

		Registry.register(Registry.ITEM, Resources.identifier("death_stick_red"), SwgItems.Food.DeathStickRed);
		Registry.register(Registry.ITEM, Resources.identifier("death_stick_yellow"), SwgItems.Food.DeathStickYellow);

		Registry.register(Registry.ITEM, Resources.identifier("blue_milk"), SwgItems.Food.BlueMilk);
		Registry.register(Registry.ITEM, Resources.identifier("blue_puff_cube"), SwgItems.Food.BluePuffCube);
		Registry.register(Registry.ITEM, Resources.identifier("blue_yogurt"), SwgItems.Food.BlueYogurt);

		Registry.register(Registry.ITEM, Resources.identifier("qrikki_bread"), SwgItems.Food.QrikkiBread);
		Registry.register(Registry.ITEM, Resources.identifier("qrikki_waffle"), SwgItems.Food.QrikkiWaffle);

		SwgDimensions.Tatooine.registerDimension();

		SwgScreenTypes.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				                                           dispatcher.register(CommandManager.literal("cdim")
				                                                                             .requires(source -> source.hasPermissionLevel(2) && source.getEntity() != null) // same permission level as tp
				                                                                             .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
				                                                                                                 .executes(context -> {
					                                                                                                 ServerWorld world = DimensionArgumentType.getDimensionArgument(context, "dimension");
					                                                                                                 DimensionTeleporter.teleport(Objects.requireNonNull(context.getSource().getEntity()), world);
					                                                                                                 return 1;
				                                                                                                 }))));

		ArgumentTypes.register("pswg:species", SpeciesArgumentType.class, new ConstantArgumentSerializer<>(SpeciesArgumentType::new));
		ArgumentTypes.register("pswg:variant", SpeciesVariantArgumentType.class, new ConstantArgumentSerializer<>(SpeciesVariantArgumentType::new));
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				                                           dispatcher.register(CommandManager.literal("pswg_species")
				                                                                             .requires(source -> source.hasPermissionLevel(2)) // same permission level as tp
				                                                                             .then(CommandManager.argument("players", EntityArgumentType.players())
				                                                                                                 .then(CommandManager.argument("species", new SpeciesArgumentType())
				                                                                                                                     .then(CommandManager.argument("variant", new SpeciesVariantArgumentType())
				                                                                                                                                         .executes(context -> {
					                                                                                                                                         Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "players");
					                                                                                                                                         Identifier species = context.getArgument("species", Identifier.class);
					                                                                                                                                         String variant = context.getArgument("variant", String.class);

					                                                                                                                                         if (!SwgSpecies.SPECIES.contains(species))
					                                                                                                                                         {
						                                                                                                                                         context.getSource().sendFeedback(new TranslatableText(Resources.command("species.invalid"), species.toString()), false);
						                                                                                                                                         return 0;
					                                                                                                                                         }

					                                                                                                                                         if (!ArrayUtils.contains(SwgSpecies.VARIANTS.get(species), variant))
					                                                                                                                                         {
						                                                                                                                                         context.getSource().sendFeedback(new TranslatableText(Resources.command("species.variant.invalid"), variant, species.toString()), false);
						                                                                                                                                         return 0;
					                                                                                                                                         }

					                                                                                                                                         for (ServerPlayerEntity player : players)
					                                                                                                                                         {
						                                                                                                                                         SwgPersistentComponents pc = SwgEntityComponents.getPersistent(player);
						                                                                                                                                         if (species.equals(SwgSpecies.SPECIES_NONE))
							                                                                                                                                         pc.setSpecies(null);
						                                                                                                                                         else
							                                                                                                                                         pc.setSpecies(new SwgSpeciesInstance(species, variant));
					                                                                                                                                         }

					                                                                                                                                         return 1;
				                                                                                                                                         }))))));

		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketPlayerLeftClickItem, PlayerPacketHandler::handleLeftClickPacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketPlayerLightsaberToggle, PlayerPacketHandler::handleLightsaberTogglePacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipRotation, ShipEntity::handleRotationPacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipControls, ShipEntity::handleControlPacket);
	}
}
