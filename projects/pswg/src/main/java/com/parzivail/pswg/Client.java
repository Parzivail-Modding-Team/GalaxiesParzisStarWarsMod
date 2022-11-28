package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.event.PlayerEvent;
import com.parzivail.pswg.client.event.WorldEvent;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.loader.ModelLoader;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.client.render.armor.ArmorRenderer;
import com.parzivail.pswg.client.render.block.BlasterWorkbenchWeaponRenderer;
import com.parzivail.pswg.client.render.block.PowerCouplingCableRenderer;
import com.parzivail.pswg.client.render.block.SlidingDoorRenderer;
import com.parzivail.pswg.client.render.block.TerrariumRenderer;
import com.parzivail.pswg.client.render.entity.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.entity.BlasterIonBoltRenderer;
import com.parzivail.pswg.client.render.entity.BlasterStunBoltRenderer;
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
import com.parzivail.pswg.client.render.hud.BlasterHudRenderer;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.client.render.item.LightsaberItemRenderer;
import com.parzivail.pswg.client.render.p3d.P3DBlockRendererRegistry;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.pswg.client.render.sky.SpaceSkyRenderer;
import com.parzivail.pswg.client.screen.*;
import com.parzivail.pswg.client.weapon.RecoilManager;
import com.parzivail.pswg.client.zoom.ZoomHandler;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgSpeciesManager;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.item.jetpack.JetpackItem;
import com.parzivail.pswg.mixin.BufferBuilderStorageAccessor;
import com.parzivail.pswg.mixin.DimensionEffectsAccessor;
import com.parzivail.pswg.mixin.MinecraftClientAccessor;
import com.parzivail.pswg.network.OpenEntityInventoryS2CPacket;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.util.block.BlockEntityClientSerializable;
import com.parzivail.util.client.StatelessWaterRenderer;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.client.model.ModelRegistry;
import com.parzivail.util.client.render.ICustomHudRenderer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
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
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
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
		BlasterItemRenderer.getDebugInfo(strings);
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
		ClientTickEvents.START_CLIENT_TICK.register(RecoilManager::tick);

		ClientTickEvents.END_CLIENT_TICK.register(ZoomHandler::tick);

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

		BlockEntityRendererRegistry.register(SwgBlocks.Workbench.BlasterBlockEntityType, BlasterWorkbenchWeaponRenderer::new);
		BlockEntityRendererRegistry.register(SwgBlocks.Power.CouplingBlockEntityType, PowerCouplingCableRenderer::new);
		BlockEntityRendererRegistry.register(SwgBlocks.Cage.CreatureCageBlockEntityType, TerrariumRenderer::new);

		ModelRegistry.register(
				SwgBlocks.Door.Sliding1x2,
				true,
				ModelLoader.withDyeColors(
						ModelLoader.loadP3D(
								DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY,
								Resources.id("block/tatooine_home_door"),
								Resources.id("model/door/sliding_1x2/frame"),
								Resources.id("model/workbench_particle")
						),
						(model, color) -> model.withTexture("door_" + color.getName(), Resources.id("model/door/sliding_1x2/door_" + color.getName()))
				)
		);
		var slidingDoorRenderer = new SlidingDoorRenderer();
		P3DBlockRendererRegistry.register(SwgBlocks.Door.Sliding1x2, slidingDoorRenderer);
		BlockEntityRendererRegistry.register(SwgBlocks.Door.SlidingBlockEntityType, ctx -> slidingDoorRenderer);

		ModelRegistry.register(SwgBlocks.Workbench.Blaster, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/blaster_workbench"), Resources.id("model/blaster_workbench"), Resources.id("model/workbench_particle")));
		ModelRegistry.register(SwgBlocks.Workbench.Lightsaber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/lightsaber_forge"), Resources.id("model/lightsaber_forge"), Resources.id("model/workbench_particle")));

		ModelRegistry.register(SwgBlocks.Light.RedHangar, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/light/hangar_light"), Resources.id("model/light/red_hangar_light"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Light.BlueHangar, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/light/hangar_light"), Resources.id("model/light/blue_hangar_light"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.MoistureVaporator.Gx8, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/gx8"), Resources.id("model/gx8"), Resources.id("model/gx8_particle")));

		ModelRegistry.register(SwgBlocks.Power.Coupling, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/power_coupling"), Resources.id("model/power_coupling"), Resources.id("model/power_coupling_particle")));

		ModelRegistry.register(SwgBlocks.Scaffold.Scaffold, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.SINGLETON, Resources.id("block/scaffold"), Resources.id("model/scaffold"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Scaffold.ScaffoldStairs, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/scaffold_stairs"), Resources.id("model/scaffold_stairs"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Light.WallCluster, true, ModelLoader.loadPicklingP3D(Resources.id("model/light/cluster"), Resources.id("model/light/cluster_particle"), Resources.id("block/light/cluster_light_1"), Resources.id("block/light/cluster_light_2"), Resources.id("block/light/cluster_light_3")));
		ModelRegistry.register(SwgBlocks.Light.TallLamp, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.SINGLETON, Resources.id("block/light/tall_lamp"), Resources.id("model/light/tall_lamp"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Barrel.Desh, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.RENDER_SEED_KEY, Resources.id("block/desh_barrel"), Resources.id("model/desh_barrel"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Tank.Fusion, true, ModelLoader.loadPM3D(Resources.id("models/block/tank/fusion.pm3d"), Resources.id("model/tank/fusion"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Crate.OrangeKyber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/crate/kyber"), Resources.id("model/crate/kyber_orange"), Resources.id("model/crate/kyber_orange_particle")));
		ModelRegistry.register(SwgBlocks.Crate.GrayKyber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/crate/kyber"), Resources.id("model/crate/kyber_gray"), Resources.id("model/crate/kyber_gray_particle")));
		ModelRegistry.register(SwgBlocks.Crate.BlackKyber, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/crate/kyber"), Resources.id("model/crate/kyber_black"), Resources.id("model/crate/kyber_black_particle")));
		ModelRegistry.register(SwgBlocks.Crate.Toolbox, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/mos_eisley.pm3d"), Resources.id("model/crate/mos_eisley"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Crate.BrownSegmented, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/segmented_crate"), Resources.id("model/segmented_crate/brown"), Resources.id("model/segmented_crate/brown_particle")));
		ModelRegistry.register(SwgBlocks.Crate.GraySegmented, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/segmented_crate"), Resources.id("model/segmented_crate/gray"), Resources.id("model/segmented_crate/gray_particle")));
		ModelRegistry.register(SwgBlocks.Crate.GrayPanel, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/segmented_crate"), Resources.id("model/segmented_crate/gray_panel"), Resources.id("model/segmented_crate/gray_panel_particle")));

		ModelRegistry.register(SwgBlocks.Crate.ImperialCorrugatedCrate, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/corrugated_crate"), Resources.id("model/corrugated_crate/imperial"), Resources.id("model/corrugated_crate/imperial_particle")));
		for (var color : DyeColor.values())
			ModelRegistry.register(SwgBlocks.Crate.CorrugatedCrate.get(color), true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/corrugated_crate"), Resources.id("model/corrugated_crate/" + color.getName()), Resources.id("model/corrugated_crate/" + color.getName() + "_particle")));

		ModelRegistry.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadPM3D(Resources.id("models/block/machine_spoked.pm3d"), Resources.id("model/machine_spoked"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Pipe.Large, false, ModelLoader.loadPM3D(Resources.id("models/block/pipe_thick.pm3d"), Resources.id("model/pipe_thick"), new Identifier("block/stone")));

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

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(NemManager.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(P3dManager.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(BlasterItemRenderer.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(StatelessWaterRenderer.INSTANCE);

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> ModelRegistry.INSTANCE);

		EntityRendererRegistry.register(SwgEntities.Ship.T65bXwing, T65BXwingRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Speeder.X34, X34LandspeederRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Speeder.ZephyrJ, ZephyrJRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterBolt, BlasterBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterStunBolt, BlasterStunBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterIonBolt, BlasterIonBoltRenderer::new);
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

		ArmorRenderer.register(
				SwgItems.Armor.Stormtrooper,
				Resources.id("stormtrooper"),
				new ArmorRenderer.Assets(Resources.id("armor/stormtrooper_slim"),
				                         Resources.id("armor/stormtrooper_default"),
				                         Resources.id("textures/armor/stormtrooper.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.Purgetrooper,
				Resources.id("purgetrooper"),
				new ArmorRenderer.Assets(Resources.id("armor/purgetrooper"),
				                         Resources.id("textures/armor/purgetrooper.png")),
				ArmorRenderer.Metadata.AUTO_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.ImperialPilotHelmet,
				SwgItems.Armor.ImperialPilotKit,
				Resources.id("imperial_pilot"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_pilot"),
				                         Resources.id("textures/armor/imperial_pilot.png")),
				ArmorRenderer.Metadata.AUTO_ARMS_HIDE_CHEST
		);
		var sandtrooperId = Resources.id("sandtrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Sandtrooper,
				sandtrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/sandtrooper_slim"),
				                         Resources.id("textures/armor/sandtrooper_slim.png"),
				                         Resources.id("armor/sandtrooper_default"),
				                         Resources.id("textures/armor/sandtrooper_default.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.registerExtra(
				SwgItems.Armor.SandtrooperBackpack,
				entity -> TrinketUtil.getEquipped(entity, SwgItems.Armor.SandtrooperBackpack),
				sandtrooperId,
				EquipmentSlot.CHEST
		);
		ArmorRenderer.registerTransformer(sandtrooperId, (entity, slim, model) -> {
			var modArmor = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
			var hasChestplate = modArmor != null && modArmor.getLeft().equals(sandtrooperId);
			var hasPack = !TrinketUtil.getEquipped(entity, SwgItems.Armor.SandtrooperBackpack).isEmpty();

			model.leftArm.visible = model.leftArm.visible && hasChestplate;
			model.rightArm.visible = model.rightArm.visible && hasChestplate;
			model.body.getChild("chest").visible = hasChestplate;
			model.body.getChild("pauldron").visible = hasChestplate;

			model.body.getChild("backpack").visible = hasPack;
		});

		ArmorRenderer.register(
				SwgItems.Armor.Deathtrooper,
				Resources.id("deathtrooper"),
				new ArmorRenderer.Assets(Resources.id("armor/deathtrooper_slim"),
				                         Resources.id("textures/armor/deathtrooper_slim.png"),
				                         Resources.id("armor/deathtrooper_default"),
				                         Resources.id("textures/armor/deathtrooper_default.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.Scouttrooper,
				Resources.id("scouttrooper"),
				new ArmorRenderer.Assets(Resources.id("armor/scouttrooper"),
				                         Resources.id("textures/armor/scouttrooper.png")),
				ArmorRenderer.Metadata.AUTO_ARMS_HIDE_CHEST
		);

		var jumptrooperId = Resources.id("jumptrooper");
		ArmorRenderer.register(
				SwgItems.Armor.Jumptrooper,
				jumptrooperId,
				new ArmorRenderer.Assets(Resources.id("armor/jumptrooper"),
				                         Resources.id("textures/armor/jumptrooper.png")),
				ArmorRenderer.Metadata.AUTO_ARMS_HIDE_CHEST
		);
		ArmorRenderer.registerExtra(
				SwgItems.Armor.JumptrooperJetpack,
				JetpackItem::getEquippedJetpack,
				jumptrooperId,
				EquipmentSlot.CHEST
		);
		ArmorRenderer.registerTransformer(jumptrooperId, (entity, slim, model) -> {
			var modArmor = ArmorRenderer.getModArmor(entity, EquipmentSlot.CHEST);
			var hasChestplate = modArmor != null && modArmor.getLeft().equals(jumptrooperId);
			var hasPack = !TrinketUtil.getEquipped(entity, SwgItems.Armor.JumptrooperJetpack).isEmpty();

			model.leftArm.visible = model.leftArm.visible && hasChestplate;
			model.rightArm.visible = model.rightArm.visible && hasChestplate;
			model.body.getChild("chest").visible = hasChestplate;

			model.body.getChild("jetpack").visible = hasPack;
			model.head.getChild("helmet5").visible = hasPack;
			model.head.getChild("helmet6").visible = hasPack;
			model.head.getChild("helmet7").visible = hasPack;
			model.head.getChild("helmet8").visible = hasPack;
			model.head.getChild("helmet9").visible = hasPack;
			model.head.getChild("helmet10").visible = hasPack;
		});
		ArmorRenderer.register(
				SwgItems.Armor.RebelPilot,
				Resources.id("rebel_pilot"),
				new ArmorRenderer.Assets(Resources.id("armor/rebel_pilot"),
				                         Resources.id("textures/armor/rebel_pilot_visor_up.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.RebelForest,
				Resources.id("rebel_forest"),
				new ArmorRenderer.Assets(Resources.id("armor/rebel_light"),
				                         Resources.id("textures/armor/rebel_forest.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.RebelTropical,
				Resources.id("rebel_tropical"),
				new ArmorRenderer.Assets(Resources.id("armor/rebel_light"),
				                         Resources.id("textures/armor/rebel_tropical.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.BlackImperialOfficer,
				Resources.id("black_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_black.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.GrayImperialOfficer,
				Resources.id("gray_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_gray.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.LightGrayImperialOfficer,
				Resources.id("light_gray_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_light_gray.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);
		ArmorRenderer.register(
				SwgItems.Armor.KhakiImperialOfficer,
				Resources.id("khaki_imperial_officer_hat"),
				new ArmorRenderer.Assets(Resources.id("armor/imperial_officer_hat"),
				                         Resources.id("textures/armor/imperial_officer_hat_khaki.png")),
				ArmorRenderer.Metadata.MANUAL_ARMS_HIDE_CHEST
		);

		ICustomItemRenderer.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);

		ICustomItemRenderer.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomHudRenderer.register(SwgItems.Blaster.Blaster, BlasterHudRenderer.INSTANCE);

		//		TODO: ICustomSkyRenderer.register(SwgDimensions.Tatooine.WORLD_KEY.getValue(), new TatooineSkyRenderer());

		SwgParticles.register();

		PlayerEvent.EVENT_BUS.subscribe(PlayerEvent.ACCUMULATE_RECOIL, RecoilManager::handleAccumulateRecoil);

		WorldEvent.EVENT_BUS.subscribe(WorldEvent.SLUG_FIRED, BlasterUtil::handleSlugFired);
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
					Text urlText = Text.literal("https://www.curseforge.com/minecraft/mc-mods/pswg")
					                   .styled((style) -> style
							                   .withColor(TextColor.fromRgb(0x5bc0de))
							                   .withUnderline(true)
							                   .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/pswg"))
							                   .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("PSWG on CurseForge")))
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
