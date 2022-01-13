package com.parzivail.pswg;

import com.parzivail.pswg.client.event.PlayerEvent;
import com.parzivail.pswg.client.event.WorldEvent;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.loader.ModelLoader;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.client.render.block.TatooineHomeDoorRenderer;
import com.parzivail.pswg.client.render.entity.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.entity.BlasterIonBoltRenderer;
import com.parzivail.pswg.client.render.entity.BlasterStunBoltRenderer;
import com.parzivail.pswg.client.render.entity.ThrownLightsaberRenderer;
import com.parzivail.pswg.client.render.entity.amphibian.WorrtEntityRenderer;
import com.parzivail.pswg.client.render.entity.debug.KinematicTestEntityRenderer;
import com.parzivail.pswg.client.render.entity.fish.FaaEntityRenderer;
import com.parzivail.pswg.client.render.entity.fish.LaaEntityRenderer;
import com.parzivail.pswg.client.render.entity.ship.T65BXwingRenderer;
import com.parzivail.pswg.client.render.entity.ship.X34LandspeederRenderer;
import com.parzivail.pswg.client.render.hud.BlasterHudRenderer;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.client.render.item.LightsaberItemRenderer;
import com.parzivail.pswg.client.render.p3d.P3dManager;
import com.parzivail.pswg.client.screen.*;
import com.parzivail.pswg.client.texture.remote.RemoteTextureProvider;
import com.parzivail.pswg.client.texture.stacked.StackedTextureProvider;
import com.parzivail.pswg.client.texture.tinted.stacked.TintedTextureProvider;
import com.parzivail.pswg.client.weapon.RecoilManager;
import com.parzivail.pswg.client.zoom.ZoomHandler;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.data.SwgLightsaberManager;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.mixin.MinecraftClientAccessor;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.block.BlockEntityClientSerializable;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.client.model.ModelRegistry;
import com.parzivail.util.client.render.ICustomHudRenderer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomRegistry;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
import io.github.ennuil.libzoomer.api.transitions.SmoothTransitionMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ReloadableResourceManager;
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

	public static RemoteTextureProvider remoteTextureProvider;
	public static StackedTextureProvider stackedTextureProvider;
	public static TintedTextureProvider tintedTextureProvider;

	public static ZoomInstance blasterZoomInstance;

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

	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		KeyBindingHelper.registerKeyBinding(KEY_PRIMARY_ITEM_ACTION);
		KeyBindingHelper.registerKeyBinding(KEY_SECONDARY_ITEM_ACTION);
		KeyBindingHelper.registerKeyBinding(KEY_SHIP_INPUT_MODE_OVERRIDE);
		KeyBindingHelper.registerKeyBinding(KEY_SPECIES_SELECT);

		ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::tick);
		ClientTickEvents.START_CLIENT_TICK.register(RecoilManager::tick);

		ClientTickEvents.END_CLIENT_TICK.register(ZoomHandler::tick);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xFFFFFF, SwgBlocks.Leaves.Japor);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xFFFFFF, SwgBlocks.Leaves.Japor);

		BlockEntityRendererRegistry.INSTANCE.register(SwgBlocks.Door.TatooineHomeBlockEntityType, TatooineHomeDoorRenderer::new);

		ModelRegistry.register(SwgBlocks.Barrel.Desh, true, ModelLoader.loadPM3D(DynamicBakedModel.Discriminator.RENDER_SEED, Resources.id("models/block/barrel/mos_eisley.pm3d"), Resources.id("model/barrel/mos_eisley"), new Identifier("block/stone")));

		ScreenRegistry.register(SwgScreenTypes.Crate.Octagon, CrateOctagonScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.MosEisley, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.ImperialCube, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.Segmented, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.MoistureVaporator.GX8, MoistureVaporatorScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Blaster, BlasterWorkbenchScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Lightsaber, LightsaberForgeScreen::new);

		ModelRegistry.register(SwgBlocks.Tank.Fusion, true, ModelLoader.loadPM3D(Resources.id("models/block/tank/fusion.pm3d"), Resources.id("model/tank/fusion"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Crate.OrangeKyber, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/octagon.pm3d"), Resources.id("model/crate/octagon_orange"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.GrayKyber, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/octagon.pm3d"), Resources.id("model/crate/octagon_gray"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.BlackKyber, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/octagon.pm3d"), Resources.id("model/crate/octagon_black"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.Toolbox, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/mos_eisley.pm3d"), Resources.id("model/crate/mos_eisley"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.Imperial, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/imperial_cube.pm3d"), Resources.id("model/crate/imperial_cube"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.Segmented, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/segmented.pm3d"), Resources.id("model/crate/segmented"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Light.RedHangar, true, ModelLoader.loadPM3D(Resources.id("models/block/light/hangar_light.pm3d"), Resources.id("model/light/red_hangar_light"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Light.BlueHangar, true, ModelLoader.loadPM3D(Resources.id("models/block/light/hangar_light.pm3d"), Resources.id("model/light/blue_hangar_light"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Light.WallCluster, true, ModelLoader.loadPM3D(Resources.id("models/block/light/wall_cluster.pm3d"), Resources.id("model/light/wall_cluster"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadPM3D(Resources.id("models/block/machine_spoked.pm3d"), Resources.id("model/machine_spoked"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.MoistureVaporator.Gx8, false, ModelLoader.loadPM3D(Resources.id("models/block/moisture_vaporator/gx8.pm3d"), Resources.id("model/moisture_vaporator_gx8"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Pipe.Large, false, ModelLoader.loadPM3D(Resources.id("models/block/pipe_thick.pm3d"), Resources.id("model/pipe_thick"), new Identifier("block/stone")));

		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialLightTall1, false, true, false, Resources.id("block/panel_imperial_base_connected"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialLightTall2, false, true, false, Resources.id("block/panel_imperial_base_connected"));

		ModelRegistry.registerConnected(SwgBlocks.MaterialBlock.Plasteel);

		ModelRegistry.registerConnected(SwgBlocks.Glass.Imperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.WhiteStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.OrangeStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.MagentaStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.LightBlueStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.YellowStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.LimeStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.PinkStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.GrayStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.LightGrayStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.CyanStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.PurpleStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.BlueStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.BrownStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.GreenStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.RedStainedImperial);
		ModelRegistry.registerConnected(SwgBlocks.Glass.BlackStainedImperial);

		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.Imperial, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.WhiteStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.OrangeStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.MagentaStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.LightBlueStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.YellowStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.LimeStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.PinkStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.GrayStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.LightGrayStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.CyanStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.PurpleStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.BlueStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.BrownStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.GreenStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.RedStainedImperial, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Glass.BlackStainedImperial, RenderLayer.getTranslucent());

		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Leaves.Sequoia, RenderLayer.getCutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Leaves.Japor, RenderLayer.getCutoutMipped());

		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.FunnelFlower, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.BlossomingFunnelFlower, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.PoontenGrass, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.DriedPoontenGrass, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.Tuber, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.Chasuka, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.HkakBush, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Plant.MoloShrub, RenderLayer.getCutout());

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> ModelRegistry.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.T65bXwing, T65BXwingRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Speeder.X34, X34LandspeederRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.BlasterBolt, BlasterBoltRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.BlasterStunBolt, BlasterStunBoltRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.BlasterIonBolt, BlasterIonBoltRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.ThrownLightsaber, ThrownLightsaberRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Fish.Faa, FaaEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Fish.Laa, LaaEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Amphibian.Worrt, WorrtEntityRenderer::new);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.KinematicTest, KinematicTestEntityRenderer::new);

		ICustomItemRenderer.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);

		ICustomItemRenderer.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomHudRenderer.register(SwgItems.Blaster.Blaster, BlasterHudRenderer.INSTANCE);

//		ICustomSkyRenderer.register(SwgDimensions.Tatooine.WORLD_KEY.getValue(), new TatooineSkyRenderer());

		SwgParticles.register();

		ResourceManagers.registerPackets();

		PlayerEvent.EVENT_BUS.subscribe(PlayerEvent.ACCUMULATE_RECOIL, RecoilManager::handleAccumulateRecoil);

		WorldEvent.EVENT_BUS.subscribe(WorldEvent.SLUG_FIRED, BlasterUtil::handleSlugFired);
		WorldEvent.EVENT_BUS.subscribe(WorldEvent.BLASTER_BOLT_HIT, BlasterUtil::handleBoltHit);

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketPlayerEvent, (client, handler, buf, responseSender) -> {
			var eventId = buf.readByte();

			if (PlayerEvent.ID_LOOKUP.containsKey(eventId))
			{
				var event = PlayerEvent.ID_LOOKUP.get(eventId);
				PlayerEvent.EVENT_BUS.publish(event, receiver -> receiver.receive(client, handler, buf, responseSender));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketWorldEvent, (client, handler, buf, responseSender) -> {
			var eventId = buf.readByte();

			if (WorldEvent.ID_LOOKUP.containsKey(eventId))
			{
				var event = WorldEvent.ID_LOOKUP.get(eventId);
				WorldEvent.EVENT_BUS.publish(event, receiver -> receiver.receive(client, handler, buf, responseSender));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketClientSync, BlockEntityClientSerializable::handle);
		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketPreciseEntityVelocityUpdate, PreciseEntityVelocityUpdateS2CPacket::handle);

		blasterZoomInstance = ZoomRegistry.registerInstance(new ZoomInstance(
				Resources.id("blaster_zoom"),
				10.0F,
				new SmoothTransitionMode(),
				new ZoomDivisorMouseModifier(),
				null //new SpyglassZoomOverlay(new Identifier("libzoomertest:textures/misc/michael.png"))
		));
	}

	public static class ResourceManagers
	{
		private static NemManager nemManager;
		private static P3dManager p3dManager;
		private static SwgBlasterManager blasterManager;
		private static SwgLightsaberManager lightsaberManager;

		static
		{
			// Will be reset when a world is joined, but is needed
			// because the searchable containers are initialized
			// *before* a world is joined -- although the search
			// container itself will be empty, because the list
			// hasn't been loaded from disk yet
			registerNonreloadableManagers();
		}

		public static SwgBlasterManager getBlasterManager()
		{
			return blasterManager;
		}

		public static SwgLightsaberManager getLightsaberManager()
		{
			return lightsaberManager;
		}

		public static NemManager getNemManager()
		{
			return nemManager;
		}

		public static P3dManager getP3dManager()
		{
			return p3dManager;
		}

		/**
		 * Register managers which are used by the client to provide visuals that can be reloaded
		 */
		public static void registerReloadableManagers(ReloadableResourceManager resourceManager)
		{
			resourceManager.registerReloader(nemManager = new NemManager());
			resourceManager.registerReloader(p3dManager = new P3dManager());
		}

		/**
		 * Register managers which are used by the server to provide content
		 */
		public static void registerNonreloadableManagers()
		{
			blasterManager = new SwgBlasterManager();
			lightsaberManager = new SwgLightsaberManager();
		}

		/**
		 * Register packets used by server resource managers to synchronize content from the client to the server
		 */
		public static void registerPackets()
		{
			ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketSyncBlasters, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
				if (blasterManager != null)
				{
					blasterManager.handlePacket(minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender);
					minecraftClient.execute(() -> {
						((MinecraftClientAccessor)minecraftClient).invokeInitializeSearchableContainers();
						minecraftClient.getSearchableContainer(SearchManager.ITEM_TOOLTIP).reload();
					});
				}
				else
					Lumberjack.error("Attempted to sync blaster descriptors without initializing the client loader!");
			});

			ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketSyncLightsabers, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
				if (lightsaberManager != null)
				{
					lightsaberManager.handlePacket(minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender);
					minecraftClient.execute(() -> {
						((MinecraftClientAccessor)minecraftClient).invokeInitializeSearchableContainers();
						minecraftClient.getSearchableContainer(SearchManager.ITEM_TOOLTIP).reload();
					});
				}
				else
					Lumberjack.error("Attempted to sync lightsaber descriptors without initializing the client loader!");
			});
		}
	}
}
