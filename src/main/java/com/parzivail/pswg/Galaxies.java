package com.parzivail.pswg;

import com.parzivail.pswg.container.*;
import com.parzivail.pswg.entity.ShipEntity;
import com.parzivail.pswg.entity.data.TrackedDataHandlers;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class Galaxies implements ModInitializer
{
	public static final ItemGroup Tab = FabricItemGroupBuilder
			.create(Resources.identifier("blocks"))
			.icon(() -> new ItemStack(Items.APPLE))
			.build();

	@Override
	public void onInitialize()
	{
		Lumberjack.debug("onInitialize");

		TrackedDataHandlers.register();

		SwgEntities.Ship.register();

		SwgStructures.General.register();

		SwgBlocks.register(SwgBlocks.Sand.Tatooine, Resources.identifier("sand_tatooine"));

		SwgBlocks.register(SwgBlocks.Crate.Octagon, Resources.identifier("crate_octagon"));
		SwgBlocks.register(SwgBlocks.Crate.MoistureVaporator, Resources.identifier("moisture_vaporator"));

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

		Registry.register(Registry.ITEM, Resources.identifier("lightsaber"), SwgItems.Lightsaber);

		Registry.register(Registry.ITEM, Resources.identifier("a280"), SwgItems.Blaster.A280);

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

		Registry.register(Registry.ITEM, Resources.identifier("red_death_stick"), SwgItems.Food.RedDeathStick);
		Registry.register(Registry.ITEM, Resources.identifier("yellow_death_stick"), SwgItems.Food.YellowDeathStick);

		Registry.register(Registry.ITEM, Resources.identifier("blue_milk"), SwgItems.Food.BlueMilk);
		Registry.register(Registry.ITEM, Resources.identifier("blue_puff_cube"), SwgItems.Food.BluePuffCube);
		Registry.register(Registry.ITEM, Resources.identifier("blue_yogurt"), SwgItems.Food.BlueYogurt);

		Registry.register(Registry.ITEM, Resources.identifier("qrikki_bread"), SwgItems.Food.QrikkiBread);
		Registry.register(Registry.ITEM, Resources.identifier("qrikki_waffle"), SwgItems.Food.QrikkiWaffle);

		//		SwgDimensions.Tatooine.registerDimension();

		//		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
		//				                                           dispatcher.register(CommandManager.literal("cdim")
		//				                                                                             .requires(source -> source.hasPermissionLevel(2) && source.getEntity() != null) // same permission level as tp
		//				                                                                             .then(CommandManager.argument("dimension", DimensionArgumentType.dimension())
		//				                                                                                                 .executes(context -> {
		//					                                                                                                 DimensionType dimensionType = DimensionArgumentType.getDimensionArgument(context, "dimension");
		//					                                                                                                 FabricDimensions.teleport(Objects.requireNonNull(context.getSource().getEntity()), dimensionType, DefaultEntityPlacer.INSTANCE);
		//					                                                                                                 return 1;
		//				                                                                                                 }))));

		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipRotation, ShipEntity::handleRotationPacket);
		ServerSidePacketRegistry.INSTANCE.register(SwgPackets.C2S.PacketShipControls, ShipEntity::handleControlPacket);
	}
}
