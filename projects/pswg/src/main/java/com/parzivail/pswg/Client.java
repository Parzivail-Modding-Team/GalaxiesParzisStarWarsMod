package com.parzivail.pswg;

import com.parzivail.nem.NemManager;
import com.parzivail.p3d.P3dBlockRendererRegistry;
import com.parzivail.p3d.P3dManager;
import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.event.PlayerEvent;
import com.parzivail.pswg.client.event.WorldEvent;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.loader.ModelLoader;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswg.client.render.block.*;
import com.parzivail.pswg.client.render.entity.ThrownLightsaberRenderer;
import com.parzivail.pswg.client.render.entity.amphibian.WorrtEntityRenderer;
import com.parzivail.pswg.client.render.entity.droid.AstromechRenderer;
import com.parzivail.pswg.client.render.entity.fish.FaaEntityRenderer;
import com.parzivail.pswg.client.render.entity.fish.LaaEntityRenderer;
import com.parzivail.pswg.client.render.entity.mammal.BanthaEntityRenderer;
import com.parzivail.pswg.client.render.entity.rodent.SandSkitterEntityRenderer;
import com.parzivail.pswg.client.render.entity.ship.T65BXwingRenderer;
import com.parzivail.pswg.client.render.entity.ship.X34LandspeederRenderer;
import com.parzivail.pswg.client.render.entity.ship.ZephyrJRenderer;
import com.parzivail.pswg.client.render.sky.SpaceSkyRenderer;
import com.parzivail.pswg.client.screen.CrateGenericSmallScreen;
import com.parzivail.pswg.client.screen.CrateOctagonScreen;
import com.parzivail.pswg.client.screen.MoistureVaporatorScreen;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgSpeciesManager;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.features.blasters.BlasterItem;
import com.parzivail.pswg.features.blasters.BlasterUtil;
import com.parzivail.pswg.features.blasters.client.BlasterHudRenderer;
import com.parzivail.pswg.features.blasters.client.BlasterItemRenderer;
import com.parzivail.pswg.features.blasters.client.BlasterRecoilManager;
import com.parzivail.pswg.features.blasters.client.BlasterZoomHandler;
import com.parzivail.pswg.features.blasters.client.entity.BlasterBoltRenderer;
import com.parzivail.pswg.features.blasters.client.entity.BlasterStunBoltRenderer;
import com.parzivail.pswg.features.blasters.client.workbench.BlasterWorkbenchScreen;
import com.parzivail.pswg.features.lightsabers.LightsaberItem;
import com.parzivail.pswg.features.lightsabers.client.LightsaberItemRenderer;
import com.parzivail.pswg.features.lightsabers.client.forge.LightsaberForgeScreen;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import com.parzivail.pswg.mixin.BufferBuilderStorageAccessor;
import com.parzivail.pswg.mixin.DimensionEffectsAccessor;
import com.parzivail.pswg.mixin.MinecraftClientAccessor;
import com.parzivail.pswg.network.OpenEntityInventoryS2CPacket;
import com.parzivail.util.block.BlockEntityClientSerializable;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.client.model.ModelRegistry;
import com.parzivail.util.client.render.ICustomHudRenderer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.client.render.StatelessWaterRenderer;
import com.parzivail.util.client.texture.remote.RemoteTextureProvider;
import com.parzivail.util.client.texture.stacked.StackedTextureProvider;
import com.parzivail.util.client.texture.tinted.TintedTextureProvider;
import com.parzivail.util.item.TrinketUtil;
import com.parzivail.util.network.PreciseEntitySpawnS2CPacket;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import com.parzivail.util.registry.ClientBlockRegistryData;
import com.parzivail.util.registry.DyedBlocks;
import com.parzivail.util.registry.NumberedBlocks;
import com.parzivail.util.registry.RegistryHelper;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.overlays.SpyglassZoomOverlay;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class Client implements ClientModInitializer
{
	public static final KeyBinding KEY_PRIMARY_ITEM_ACTION = new KeyBinding(Resources.keyBinding("primary_item_action"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "key.category.pswg");
	public static final KeyBinding KEY_SECONDARY_ITEM_ACTION = new KeyBinding(Resources.keyBinding("secondary_item_action"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.category.pswg");
	public static final KeyBinding KEY_SHIP_INPUT_MODE_OVERRIDE = new KeyBinding(Resources.keyBinding("ship_input_mode_override"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.pswg");

	public static final KeyBinding KEY_SPECIES_SELECT = new KeyBinding(Resources.keyBinding("species_select"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.category.pswg");

	public static final Identifier TEX_TRANSPARENT = Resources.id("textures/effect/transparent.png");

	public static final NemManager NEM_MANAGER = new NemManager(Resources.id("nem_manager"));

	public static RemoteTextureProvider remoteSkinTextureProvider;
	public static StackedTextureProvider stackedTextureProvider;
	public static TintedTextureProvider tintedTextureProvider;

	public static ZoomInstance blasterZoomInstance;
	public static ZoomOverlay blasterZoomOverlayDefault = new SpyglassZoomOverlay(Resources.id("textures/gui/overlay/default.png"));
	public static ZoomOverlay blasterZoomOverlaySniper = new SpyglassZoomOverlay(Resources.id("textures/gui/overlay/sniper.png"));

	public static boolean isShipClientControlled(ShipEntity shipEntity)
	{
		var minecraft = MinecraftClient.getInstance();
		if (minecraft == null || minecraft.player == null)
			return false;

		return ShipEntity.getShip(minecraft.player) == shipEntity;
	}

	public static void getRightDebugText(List<String> strings)
	{
	}

	public static float getTickDelta()
	{
		var mc = MinecraftClient.getInstance();
		if (mc.isPaused())
			return ((MinecraftClientAccessor)mc).getPausedTickDelta();
		return mc.getTickDelta();
	}

	public static Identifier tintTexture(Identifier texture, int color)
	{
		var textureId = texture.getNamespace() + "/" + texture.getPath() + "/" + Integer.toHexString(color);
		return Client.tintedTextureProvider.tint(textureId, texture, color);
	}

	@Override
	public void onInitializeClient()
	{
		Galaxies.LOG.debug("onInitializeClient");

		KeyBindingHelper.registerKeyBinding(KEY_PRIMARY_ITEM_ACTION);
		KeyBindingHelper.registerKeyBinding(KEY_SECONDARY_ITEM_ACTION);
		KeyBindingHelper.registerKeyBinding(KEY_SHIP_INPUT_MODE_OVERRIDE);
		KeyBindingHelper.registerKeyBinding(KEY_SPECIES_SELECT);

		ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::tick);
		ClientTickEvents.START_CLIENT_TICK.register(BlasterRecoilManager::tick);

		ClientTickEvents.END_CLIENT_TICK.register(BlasterZoomHandler::tick);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x8AB534, SwgBlocks.Tree.SequoiaLeaves);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x8AB534, SwgBlocks.Tree.SequoiaLeaves);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xFFFFFF, SwgBlocks.Tree.JaporLeaves);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xFFFFFF, SwgBlocks.Tree.JaporLeaves);

		HandledScreens.register(SwgScreenTypes.Crate.Octagon, CrateOctagonScreen::new);
		HandledScreens.register(SwgScreenTypes.Crate.MosEisley, CrateGenericSmallScreen::new);
		HandledScreens.register(SwgScreenTypes.Crate.Corrugated, CrateGenericSmallScreen::new);
		HandledScreens.register(SwgScreenTypes.Crate.Segmented, CrateGenericSmallScreen::new);
		HandledScreens.register(SwgScreenTypes.MoistureVaporator.GX8, MoistureVaporatorScreen::new);
		HandledScreens.register(SwgScreenTypes.Workbench.Blaster, BlasterWorkbenchScreen::new);
		HandledScreens.register(SwgScreenTypes.Workbench.Lightsaber, LightsaberForgeScreen::new);

		BlockEntityRendererFactories.register(SwgBlocks.Workbench.BlasterBlockEntityType, BlasterWorkbenchWeaponRenderer::new);
		BlockEntityRendererFactories.register(SwgBlocks.Power.CouplingBlockEntityType, PowerCouplingCableRenderer::new);
		BlockEntityRendererFactories.register(SwgBlocks.Cage.CreatureCageBlockEntityType, TerrariumRenderer::new);

		ModelRegistry.register(
				SwgBlocks.Door.Sliding1x2,
				true,
				ModelLoader.withDyeColors(
						ModelLoader.loadP3D(
								DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY,
								Resources.id("block/tatooine_home_door"),
								Resources.id("block/model/door/sliding_1x2/frame"),
								new Identifier("block/stone")
						),
						(model, color) -> model.withTexture("door_" + color.getName(), Resources.id("block/model/door/sliding_1x2/door_" + color.getName()))
				)
		);
		var slidingDoorRenderer = new SlidingDoorRenderer();
		P3dBlockRendererRegistry.register(SwgBlocks.Door.Sliding1x2, slidingDoorRenderer);
		BlockEntityRendererFactories.register(SwgBlocks.Door.SlidingBlockEntityType, ctx -> slidingDoorRenderer);

		ModelRegistry.register(SwgBlocks.Workbench.Blaster, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/blaster_workbench"), Resources.id("block/model/blaster_workbench"), Resources.id("block/model/workbench_particle")));
		ModelRegistry.register(SwgBlocks.Workbench.Lightsaber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/lightsaber_forge"), Resources.id("block/model/lightsaber_forge"), Resources.id("block/model/workbench_particle")));

		ModelRegistry.register(SwgBlocks.Light.RedHangar, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/light/hangar_light"), Resources.id("block/model/light/red_hangar_light"), Resources.id("block/model/light/hangar_light_particle")));
		ModelRegistry.register(SwgBlocks.Light.BlueHangar, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/light/hangar_light"), Resources.id("block/model/light/blue_hangar_light"), Resources.id("block/model/light/hangar_light_particle")));

		ModelRegistry.register(SwgBlocks.MoistureVaporator.Gx8, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/gx8"), Resources.id("block/model/gx8"), Resources.id("block/model/gx8_particle")));

		ModelRegistry.register(SwgBlocks.Power.Coupling, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/power_coupling"), Resources.id("block/model/power_coupling"), Resources.id("block/model/power_coupling_particle")));

		ModelRegistry.register(SwgBlocks.Scaffold.Scaffold, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.SINGLETON, Resources.id("block/scaffold"), Resources.id("block/model/scaffold"), Resources.id("block/model/scaffold_particle")));
		ModelRegistry.register(SwgBlocks.Scaffold.ScaffoldStairs, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/scaffold_stairs"), Resources.id("block/model/scaffold_stairs"), Resources.id("block/model/scaffold_particle")));

		ModelRegistry.register(SwgBlocks.Light.WallCluster, true, ModelLoader.loadPicklingP3D(Resources.id("block/model/light/cluster"), Resources.id("block/model/light/cluster_particle"), Resources.id("block/light/cluster_light_1"), Resources.id("block/light/cluster_light_2"), Resources.id("block/light/cluster_light_3")));
		ModelRegistry.register(SwgBlocks.Light.TallLamp, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.SINGLETON, Resources.id("block/light/tall_lamp"), Resources.id("block/model/light/tall_lamp"), Resources.id("block/model/light/tall_lamp_particle")));

		ModelRegistry.register(SwgBlocks.Barrel.Desh, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.RENDER_SEED_KEY, Resources.id("block/desh_barrel"), Resources.id("block/model/desh_barrel"), Resources.id("block/model/desh_barrel_particle")));

		ModelRegistry.register(SwgBlocks.Tank.FusionFuel, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/fusion_fuel_tank"), Resources.id("block/model/fusion_fuel_tank"), Resources.id("block/model/fusion_fuel_tank_particle")));
		ModelRegistry.register(SwgBlocks.Tank.StarshipFuel, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/starship_fuel_tank"), Resources.id("block/model/starship_fuel_tank"), Resources.id("block/model/starship_fuel_tank_particle")));

		ModelRegistry.register(SwgBlocks.Crate.OrangeKyber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/crate/kyber"), Resources.id("block/model/crate/kyber_orange"), Resources.id("block/model/crate/kyber_orange_particle")));
		ModelRegistry.register(SwgBlocks.Crate.GrayKyber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/crate/kyber"), Resources.id("block/model/crate/kyber_gray"), Resources.id("block/model/crate/kyber_gray_particle")));
		ModelRegistry.register(SwgBlocks.Crate.BlackKyber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/crate/kyber"), Resources.id("block/model/crate/kyber_black"), Resources.id("block/model/crate/kyber_black_particle")));
		ModelRegistry.register(SwgBlocks.Crate.Toolbox, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/stool"), Resources.id("block/model/stool"), Resources.id("block/model/stool_particle")));

		ModelRegistry.register(SwgBlocks.Crate.BrownSegmented, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/segmented_crate"), Resources.id("block/model/segmented_crate/brown"), Resources.id("block/model/segmented_crate/brown_particle")));
		ModelRegistry.register(SwgBlocks.Crate.GraySegmented, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/segmented_crate"), Resources.id("block/model/segmented_crate/gray"), Resources.id("block/model/segmented_crate/gray_particle")));
		ModelRegistry.register(SwgBlocks.Crate.GrayPanel, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/segmented_crate"), Resources.id("block/model/segmented_crate/gray_panel"), Resources.id("block/model/segmented_crate/gray_panel_particle")));

		ModelRegistry.register(SwgBlocks.Crate.ImperialCorrugatedCrate, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/corrugated_crate"), Resources.id("block/model/corrugated_crate/imperial"), Resources.id("block/model/corrugated_crate/imperial_particle")));
		for (var color : DyeColor.values())
			ModelRegistry.register(SwgBlocks.Crate.CorrugatedCrate.get(color), true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/corrugated_crate"), Resources.id("block/model/corrugated_crate/" + color.getName()), Resources.id("block/model/corrugated_crate/" + color.getName() + "_particle")));

		ModelRegistry.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.SINGLETON, Resources.id("block/spoked_machine"), Resources.id("block/model/spoked_machine"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Machine.ElectrostaticRepeller, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/electrostatic_repeller"), Resources.id("block/model/electrostatic_repeller"), Resources.id("block/model/electrostatic_repeller_particle")));

		ModelRegistry.register(SwgBlocks.Pipe.Large, false, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/pipe_thick"), Resources.id("block/model/pipe_thick"), new Identifier("block/stone")));
		P3dBlockRendererRegistry.register(SwgBlocks.Pipe.Large, new LargePipeRenderer());

		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialPanelTall1, false, true, false, Resources.id("block/gray_imperial_panel_pattern_3"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialPanelTall2, false, true, false, Resources.id("block/gray_imperial_panel_pattern_3"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialLightTall1, false, true, false, Resources.id("block/gray_imperial_panel_pattern_3"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialLightTall2, false, true, false, Resources.id("block/gray_imperial_panel_pattern_3"));

		//		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialCutout, true, false, true, Resources.id("block/imperial_cutout_face"), Resources.id("block/imperial_cutout_face"), Resources.id("block/imperial_cutout_border"), EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
		//		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialCutoutPipes, true, false, true, Resources.id("block/imperial_cutout_pipes_face"), Resources.id("block/imperial_cutout_pipes_face"), Resources.id("block/imperial_cutout_pipes_border"), EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));

		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelSectional, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelSectional1, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelSectional2, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.GrayImperialPanelSectional, true, true, true, null, Resources.id("block/gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.GrayImperialPanelSectional1, true, true, true, null, Resources.id("block/gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.GrayImperialPanelSectional2, true, true, true, null, Resources.id("block/gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.LightGrayImperialPanelSectional, true, true, true, null, Resources.id("block/light_gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.LightGrayImperialPanelSectional1, true, true, true, null, Resources.id("block/light_gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.LightGrayImperialPanelSectional2, true, true, true, null, Resources.id("block/light_gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.WhiteImperialPanelSectional, true, true, true, null, Resources.id("block/white_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.WhiteImperialPanelSectional1, true, true, true, null, Resources.id("block/white_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.WhiteImperialPanelSectional2, true, true, true, null, Resources.id("block/white_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelBordered, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelSplit, true, true, true, null, Resources.id("block/black_imperial_panel_split_center"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelThinBordered, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));

		RegistryHelper.register(SwgBlocks.class, ClientBlockRegistryData.class, Block.class, Client::registerBlockData);
		RegistryHelper.register(SwgBlocks.class, ClientBlockRegistryData.class, DyedBlocks.class, Client::registerDyedBlockData);
		RegistryHelper.register(SwgBlocks.class, ClientBlockRegistryData.class, NumberedBlocks.class, Client::registerNumberedBlockData);

		DimensionEffectsAccessor.get_BY_IDENTIFIER().put(SwgDimensions.TATOOINE.getValue(), new SpaceSkyRenderer.Effect());

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(NEM_MANAGER);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(P3dManager.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(BlasterItemRenderer.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(StatelessWaterRenderer.INSTANCE);

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> ModelRegistry.INSTANCE);

		EntityRendererRegistry.register(SwgEntities.Ship.T65bXwing, T65BXwingRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Speeder.X34, X34LandspeederRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Speeder.ZephyrJ, ZephyrJRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterBolt, BlasterBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterStunBolt, BlasterStunBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterIonBolt, BlasterBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.ThrownLightsaber, ThrownLightsaberRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Fish.Faa, FaaEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Fish.Laa, LaaEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Amphibian.Worrt, WorrtEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Mammal.Bantha, BanthaEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Rodent.SandSkitter, SandSkitterEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroR2D2, AstromechRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroR2Q5, AstromechRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroR2KP, AstromechRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroR2R7, AstromechRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroR2Y10, AstromechRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroQTKT, AstromechRenderer::new);

		ICustomItemRenderer.register(LightsaberItem.class, LightsaberItemRenderer.INSTANCE);
		ICustomPoseItem.register(LightsaberItem.class, LightsaberItemRenderer.INSTANCE);

		ICustomItemRenderer.register(BlasterItem.class, BlasterItemRenderer.INSTANCE);
		ICustomPoseItem.register(BlasterItem.class, BlasterItemRenderer.INSTANCE);
		ICustomHudRenderer.register(BlasterItem.class, BlasterHudRenderer.INSTANCE);

		//		TODO: ICustomSkyRenderer.register(SwgDimensions.Tatooine.WORLD_KEY.getValue(), new TatooineSkyRenderer());

		registerArmor();

		SwgParticles.register();

		PlayerEvent.EVENT_BUS.subscribe(PlayerEvent.ACCUMULATE_RECOIL, BlasterRecoilManager::handleAccumulateRecoil);

		WorldEvent.EVENT_BUS.subscribe(WorldEvent.BLASTER_BOLT_HIT, BlasterUtil::handleBoltHit);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null)
			{
				var config = Resources.CONFIG.get();

				if (!config.disableUpdateCheck && Resources.REMOTE_VERSION != null)
				{
					Text versionText = Text.literal(Resources.REMOTE_VERSION.name)
					                       .styled((style) -> style
							                       .withItalic(true)
					                       );

					var url = "https://modrinth.com/mod/pswg/version/" + Resources.REMOTE_VERSION;

					Text urlText = Text.literal(url)
					                   .styled((style) -> style
							                   .withColor(TextColor.fromRgb(0x5bc0de))
							                   .withUnderline(true)
							                   .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
							                   .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("PSWG on Modrinth")))
					                   );
					client.player.sendMessage(Text.translatable("msg.pswg.update", versionText, urlText), false);
				}

				if (config.client.showCharacterCustomizeTip)
				{
					client.player.sendMessage(Text.translatable("msg.pswg.tip.customize_character", TextUtil.stylizeKeybind(Client.KEY_SPECIES_SELECT.getBoundKeyLocalizedText())), false);
					config.client.showCharacterCustomizeTip = false;
					Resources.CONFIG.save();
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.SyncSpecies, SwgSpeciesManager.INSTANCE::handlePacket);

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PlayerEvent, (client, handler, buf, responseSender) -> {
			var eventId = buf.readByte();

			if (PlayerEvent.ID_LOOKUP.containsKey(eventId))
			{
				var event = PlayerEvent.ID_LOOKUP.get(eventId);
				PlayerEvent.EVENT_BUS.publish(event, receiver -> receiver.receive(client, handler, buf, responseSender));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.WorldEvent, (client, handler, buf, responseSender) -> {
			var eventId = buf.readByte();

			if (WorldEvent.ID_LOOKUP.containsKey(eventId))
			{
				var event = WorldEvent.ID_LOOKUP.get(eventId);
				WorldEvent.EVENT_BUS.publish(event, receiver -> receiver.receive(client, handler, buf, responseSender));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.SyncBlockToClient, BlockEntityClientSerializable::handle);
		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PreciseEntityVelocityUpdate, PreciseEntityVelocityUpdateS2CPacket::handle);
		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PreciseEntitySpawn, PreciseEntitySpawnS2CPacket::handle);
		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.OpenEntityInventory, OpenEntityInventoryS2CPacket::handle);

		blasterZoomInstance = new ZoomInstance(
				Resources.id("blaster_zoom"),
				10.0F,
				new SmoothTransitionMode(),
				new ZoomDivisorMouseModifier(),
				null
		);

		Galaxies.LOG.info("Loading PSWG addons via pswg-client-addon");
		EntrypointUtils.invoke("pswg-client-addon", PswgClientAddon.class, PswgClientAddon::onPswgClientReady);
	}

	private static void registerArmor()
	{
		ArmorRenderer.register(
				SwgItems.Armor.RebelForest,
				Resources.id("rebel_forest"),
				new ArmorRenderer.Assets(Resources.id("armor/rebel_light"),
				                         Resources.id("textures/armor/rebel_forest.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.register(
				SwgItems.Armor.RebelTropical,
				Resources.id("rebel_tropical"),
				new ArmorRenderer.Assets(Resources.id("armor/rebel_light"),
				                         Resources.id("textures/armor/rebel_tropical.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.register(
				SwgItems.Armor.BlackImperialOfficer,
				Resources.id("black_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_black.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.register(
				SwgItems.Armor.GrayImperialOfficer,
				Resources.id("gray_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_gray.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.register(
				SwgItems.Armor.LightGrayImperialOfficer,
				Resources.id("light_gray_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_light_gray.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.register(
				SwgItems.Armor.KhakiImperialOfficer,
				Resources.id("khaki_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_khaki.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.register(
				SwgItems.Armor.TanGogglesCap,
				Resources.id("tan_goggles_cap"),
				new ArmorRenderer.Assets(Resources.id("armor/goggles_cap"),
				                         Resources.id("textures/armor/tan_goggles_cap.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.register(
				SwgItems.Armor.GrayGogglesCap,
				Resources.id("gray_goggles_cap"),
				new ArmorRenderer.Assets(Resources.id("armor/goggles_cap"),
				                         Resources.id("textures/armor/gray_goggles_cap.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.register(
				SwgItems.Armor.BrownGogglesCap,
				Resources.id("brown_goggles_cap"),
				new ArmorRenderer.Assets(Resources.id("armor/goggles_cap"),
				                         Resources.id("textures/armor/brown_goggles_cap.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		var beachInsurgenceHatId = Resources.id("beach_insurgence_hat");
		ArmorRenderer.register(
				SwgItems.Armor.BeachInsurgenceHat,
				beachInsurgenceHatId,
				new ArmorRenderer.Assets(Resources.id("armor/beach_insurgence_hat"),
				                         Resources.id("textures/armor/beach_insurgence_hat.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.registerTransformer(beachInsurgenceHatId, (entity, slim, model, opt) -> {
			model.head.getChild("goggles_up").visible = true;
			model.head.getChild("goggles_down").visible = false;
		});

		var desertInsurgenceHatId = Resources.id("desert_insurgence_hat");
		ArmorRenderer.register(
				SwgItems.Armor.DesertInsurgenceHat,
				desertInsurgenceHatId,
				new ArmorRenderer.Assets(Resources.id("armor/desert_insurgence_hat"),
				                         Resources.id("textures/armor/desert_insurgence_hat.png")),
				ArmorRenderer.Metadata.NO_CHANGE
		);
		ArmorRenderer.registerTransformer(desertInsurgenceHatId, (entity, slim, model, opt) -> {
			model.head.getChild("visor_up").visible = true;
			model.head.getChild("visor_down").visible = false;
		});

		var stormtrooperId = Resources.id("stormtrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Stormtrooper,
				stormtrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/stormtrooper"),
				                         Resources.id("textures/armor/stormtrooper.png")),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		ArmorRenderer.registerTransformer(stormtrooperId, (entity, slim, model, opt) -> {
			model.leftArm.getChild("shoulder_pouch").visible = false;
			model.body.getChild("pauldron").visible = false;
		});

		var shocktrooperId = Resources.id("shocktrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Shocktrooper,
				shocktrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/stormtrooper"),
				                         Resources.id("textures/armor/shocktrooper.png")),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		ArmorRenderer.registerTransformer(shocktrooperId, (entity, slim, model, opt) -> {
			model.leftArm.getChild("shoulder_pouch").visible = false;
			model.body.getChild("pauldron").visible = false;
		});

		ArmorRenderer.register(
				SwgItems.Armor.Purgetrooper,
				Resources.id("purgetrooper"),
				new ArmorRenderer.Assets(Resources.id("armor/purgetrooper"),
				                         Resources.id("textures/armor/purgetrooper.png")),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);

		var sandtrooperId = Resources.id("sandtrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Sandtrooper,
				sandtrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/sandtrooper"),
				                         Resources.id("textures/armor/sandtrooper.png")),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		var sandtrooperBackpackId = Resources.id("sandtrooper_backpack");
		ArmorRenderer.registerExtra(
				SwgItems.Armor.SandtrooperBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.SandtrooperBackpack),
				sandtrooperBackpackId,
				sandtrooperId,
				EquipmentSlot.CHEST
		);
		ArmorRenderer.registerTransformer(sandtrooperId, (entity, slim, model, opt) -> {
			var modArmor = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
			var renderChestplate = opt == null && modArmor != null && modArmor.getLeft().equals(sandtrooperId);

			model.leftArm.visible = model.leftArm.visible && renderChestplate;
			model.rightArm.visible = model.rightArm.visible && renderChestplate;
			model.body.getChild("chestplate").visible = renderChestplate;
			model.leftArm.getChild("shoulder_pouch").visible = renderChestplate;
			model.body.getChild("pauldron").visible = renderChestplate;

			model.body.getChild("backpack").visible = sandtrooperBackpackId.equals(opt);
		});

		var artillerytrooperId = Resources.id("artillerytrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Artillerytrooper,
				artillerytrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/artillerytrooper"),
				                         Resources.id("textures/armor/artillerytrooper.png")),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		var artillerytrooperBackpackId = Resources.id("artillerytrooper_backpack");
		ArmorRenderer.registerExtra(
				SwgItems.Armor.ArtillerytrooperBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.ArtillerytrooperBackpack),
				artillerytrooperBackpackId,
				artillerytrooperId,
				EquipmentSlot.CHEST
		);
		ArmorRenderer.registerTransformer(artillerytrooperId, (entity, slim, model, opt) -> {
			var modArmor = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
			var renderChestplate = opt == null && modArmor != null && modArmor.getLeft().equals(artillerytrooperId);

			model.leftArm.visible = model.leftArm.visible && renderChestplate;
			model.rightArm.visible = model.rightArm.visible && renderChestplate;
			model.body.getChild("chestplate").visible = renderChestplate;
			model.body.getChild("pauldron").visible = renderChestplate;

			model.body.getChild("backpack").visible = artillerytrooperBackpackId.equals(opt);
		});

		var incineratortrooperId = Resources.id("incineratortrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Incineratortrooper,
				incineratortrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/incineratortrooper"),
				                         Resources.id("textures/armor/incineratortrooper.png")),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		var incineratortrooperTankId = Resources.id("incineratortrooper_tank");
		ArmorRenderer.registerExtra(
				SwgItems.Armor.IncineratortrooperTank,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.IncineratortrooperTank),
				incineratortrooperTankId,
				incineratortrooperId,
				EquipmentSlot.CHEST
		);
		ArmorRenderer.registerTransformer(incineratortrooperId, (entity, slim, model, opt) -> {
			var modArmor = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
			var renderChestplate = opt == null && modArmor != null && modArmor.getLeft().equals(incineratortrooperId);

			model.leftArm.visible = model.leftArm.visible && renderChestplate;
			model.rightArm.visible = model.rightArm.visible && renderChestplate;
			model.body.getChild("chestplate").visible = renderChestplate;
			model.body.getChild("pauldron").visible = renderChestplate;

			model.body.getChild("backpack").visible = incineratortrooperTankId.equals(opt);
		});

		ArmorRenderer.register(
				SwgItems.Armor.Deathtrooper,
				Resources.id("deathtrooper"),
				new ArmorRenderer.Assets(
						Resources.id("armor/deathtrooper"),
						Resources.id("textures/armor/deathtrooper.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);

		ArmorRenderer.register(
				SwgItems.Armor.Scouttrooper,
				Resources.id("scouttrooper"),
				new ArmorRenderer.Assets(
						Resources.id("armor/scouttrooper"),
						Resources.id("textures/armor/scouttrooper.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);

		ArmorRenderer.register(
				SwgItems.Armor.HovertankPilot,
				Resources.id("hovertankpilot"),
				new ArmorRenderer.Assets(
						Resources.id("armor/hovertankpilot"),
						Resources.id("textures/armor/hovertankpilot.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);

		var jumptrooperId = Resources.id("jumptrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Jumptrooper,
				jumptrooperId,
				new ArmorRenderer.Assets(
						Resources.id("armor/jumptrooper"),
						Resources.id("textures/armor/jumptrooper.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		var jumptrooperJetpackId = Resources.id("jumptrooper_jetpack");
		ArmorRenderer.registerExtra(
				SwgItems.Armor.JumptrooperJetpack,
				JetpackItem::getEquippedJetpack,
				jumptrooperJetpackId,
				jumptrooperId,
				EquipmentSlot.CHEST
		);
		ArmorRenderer.registerTransformer(jumptrooperId, (entity, slim, model, opt) -> {
			var modArmor = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
			var renderChestplate = opt == null && modArmor != null && modArmor.getLeft().equals(jumptrooperId);
			var renderPack = jumptrooperJetpackId.equals(opt);

			model.leftArm.visible = model.leftArm.visible && renderChestplate;
			model.rightArm.visible = model.rightArm.visible && renderChestplate;
			model.body.getChild("chest").visible = renderChestplate;

			model.body.getChild("jetpack").visible = renderPack;
		});

		var rebelPilotId = Resources.id("rebel_pilot");
		ArmorRenderer.register(
				SwgItems.Armor.RebelPilotHelmet,
				SwgItems.Armor.RebelPilotKit,
				rebelPilotId,
				new ArmorRenderer.Assets(
						Resources.id("armor/rebel_pilot"),
						Resources.id("textures/armor/rebel_pilot.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		ArmorRenderer.registerTransformer(rebelPilotId, (entity, slim, model, opt) -> {
			model.leftLeg.visible = model.body.visible;
			model.rightLeg.visible = model.body.visible;
		});

		ArmorRenderer.register(
				SwgItems.Armor.ImperialPilotHelmet,
				SwgItems.Armor.ImperialPilotKit,
				Resources.id("imperial_pilot"),
				new ArmorRenderer.Assets(
						Resources.id("armor/imperial_pilot"),
						Resources.id("textures/armor/imperial_pilot.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);

		var shoretrooperId = Resources.id("shoretrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Shoretrooper,
				shoretrooperId,
				new ArmorRenderer.Assets(
						Resources.id("armor/shoretrooper"),
						Resources.id("textures/armor/shoretrooper.png")
				),
				ArmorRenderer.Metadata.HIDE_CHEST_HIDE_HAIR
		);
		ArmorRenderer.registerTransformer(shoretrooperId, (entity, slim, model, opt) -> {
			model.body.getChild("kama").visible = false;
		});

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.GreenSpecialistBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.GreenSpecialistBackpack),
				Resources.id("green_specialist_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/specialist_backpack"),
						Resources.id("textures/armor/green_specialist_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.BrownSpecialistBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.BrownSpecialistBackpack),
				Resources.id("brown_specialist_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/specialist_backpack"),
						Resources.id("textures/armor/brown_specialist_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.WhiteLightBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.WhiteLightBackpack),
				Resources.id("white_light_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/light_backpack"),
						Resources.id("textures/armor/white_light_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.GreenLightBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.GreenLightBackpack),
				Resources.id("green_light_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/light_backpack"),
						Resources.id("textures/armor/green_light_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.TanLightBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.TanLightBackpack),
				Resources.id("tan_light_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/light_backpack"),
						Resources.id("textures/armor/tan_light_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.WhiteHeavyBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.WhiteHeavyBackpack),
				Resources.id("white_heavy_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/heavy_backpack"),
						Resources.id("textures/armor/white_heavy_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.GreenHeavyBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.GreenHeavyBackpack),
				Resources.id("green_heavy_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/heavy_backpack"),
						Resources.id("textures/armor/green_heavy_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);

		ArmorRenderer.registerAccessory(
				SwgItems.Armor.TanHeavyBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.TanHeavyBackpack),
				Resources.id("tan_heavy_backpack"),
				EquipmentSlot.CHEST,
				model -> model.body.getChild("backpack"),
				new ArmorRenderer.Assets(
						Resources.id("armor/heavy_backpack"),
						Resources.id("textures/armor/tan_heavy_backpack.png")
				),
				ArmorRenderer.Metadata.NO_CHANGE
		);
	}

	private static void registerBlockData(ClientBlockRegistryData data, Block block)
	{
		switch (data.renderLayer())
		{
			case CUTOUT -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
			case CUTOUT_MIPPED -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutoutMipped());
			case TRANSLUCENT -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
		}

		if (data.isConnected())
		{
			if (block instanceof ConnectingBlock cb)
				ModelRegistry.registerConnected(cb);
			else
				Galaxies.LOG.error("Attempted to auto-register connecting model for non-connecting block %s", block);
		}
	}

	private static void registerDyedBlockData(ClientBlockRegistryData data, DyedBlocks blocks)
	{
		for (var block : blocks.values())
			registerBlockData(data, block);
	}

	private static void registerNumberedBlockData(ClientBlockRegistryData data, NumberedBlocks blocks)
	{
		for (var block : blocks)
			registerBlockData(data, block);
	}

	public static void registerRenderLayer(RenderLayer layer)
	{
		((BufferBuilderStorageAccessor)MinecraftClient.getInstance().getBufferBuilders()).entityBuilders().put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
	}
}
