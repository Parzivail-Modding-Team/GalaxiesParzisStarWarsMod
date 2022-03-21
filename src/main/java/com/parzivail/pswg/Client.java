package com.parzivail.pswg;

import com.parzivail.pswg.client.event.PlayerEvent;
import com.parzivail.pswg.client.event.WorldEvent;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.loader.ModelLoader;
import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswg.client.render.block.BlasterWorkbenchWeaponRenderer;
import com.parzivail.pswg.client.render.block.TatooineHomeDoorRenderer;
import com.parzivail.pswg.client.render.entity.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.entity.BlasterIonBoltRenderer;
import com.parzivail.pswg.client.render.entity.BlasterStunBoltRenderer;
import com.parzivail.pswg.client.render.entity.ThrownLightsaberRenderer;
import com.parzivail.pswg.client.render.entity.amphibian.WorrtEntityRenderer;
import com.parzivail.pswg.client.render.entity.debug.KinematicTestEntityRenderer;
import com.parzivail.pswg.client.render.entity.droid.AstromechRenderer;
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
import com.parzivail.pswg.client.texture.tinted.TintedTextureProvider;
import com.parzivail.pswg.client.weapon.RecoilManager;
import com.parzivail.pswg.client.zoom.ZoomHandler;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.data.SwgLightsaberManager;
import com.parzivail.pswg.data.SwgSpeciesManager;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.pswg.mixin.BufferBuilderStorageAccessor;
import com.parzivail.pswg.mixin.MinecraftClientAccessor;
import com.parzivail.pswg.util.BlasterUtil;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.block.BlockEntityClientSerializable;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.client.model.ModelRegistry;
import com.parzivail.util.client.render.ICustomHudRenderer;
import com.parzivail.util.client.render.ICustomItemRenderer;
import com.parzivail.util.client.render.ICustomPoseItem;
import com.parzivail.util.network.OpenEntityInventoryS2CPacket;
import com.parzivail.util.network.PreciseEntitySpawnS2CPacket;
import com.parzivail.util.network.PreciseEntityVelocityUpdateS2CPacket;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.modifiers.ZoomDivisorMouseModifier;
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
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.*;
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

		ScreenRegistry.register(SwgScreenTypes.Crate.Octagon, CrateOctagonScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.MosEisley, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.ImperialCube, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.Segmented, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.MoistureVaporator.GX8, MoistureVaporatorScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Blaster, BlasterWorkbenchScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Lightsaber, LightsaberForgeScreen::new);

		BlockEntityRendererRegistry.register(SwgBlocks.Door.TatooineHomeBlockEntityType, TatooineHomeDoorRenderer::new);
		BlockEntityRendererRegistry.register(SwgBlocks.Workbench.BlasterBlockEntityType, BlasterWorkbenchWeaponRenderer::new);

		ModelRegistry.register(SwgBlocks.Workbench.Blaster, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/blaster_workbench"), Resources.id("model/blaster_workbench"), Resources.id("model/blaster_workbench_particle")));

		ModelRegistry.register(SwgBlocks.Light.RedHangar, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/light/hangar_light"), Resources.id("model/light/red_hangar_light"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Light.BlueHangar, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/light/hangar_light"), Resources.id("model/light/blue_hangar_light"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.MoistureVaporator.Gx8, true, ModelLoader.loadP3D(DynamicBakedModel.CacheMethod.BLOCKSTATE_KEY, Resources.id("block/gx8"), Resources.id("model/gx8"), Resources.id("model/gx8_particle")));

		ModelRegistry.register(SwgBlocks.Light.WallCluster, true, ModelLoader.loadPicklingP3D(Resources.id("model/light/cluster"), Resources.id("model/light/cluster_particle"), Resources.id("block/light/cluster_light_1"), Resources.id("block/light/cluster_light_2"), Resources.id("block/light/cluster_light_3")));

		ModelRegistry.register(SwgBlocks.Barrel.Desh, true, ModelLoader.loadPM3D(DynamicBakedModel.CacheMethod.RENDER_SEED_KEY, Resources.id("models/block/barrel/mos_eisley.pm3d"), Resources.id("model/barrel/mos_eisley"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Tank.Fusion, true, ModelLoader.loadPM3D(Resources.id("models/block/tank/fusion.pm3d"), Resources.id("model/tank/fusion"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Crate.OrangeKyber, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/octagon.pm3d"), Resources.id("model/crate/octagon_orange"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.GrayKyber, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/octagon.pm3d"), Resources.id("model/crate/octagon_gray"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.BlackKyber, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/octagon.pm3d"), Resources.id("model/crate/octagon_black"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.Toolbox, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/mos_eisley.pm3d"), Resources.id("model/crate/mos_eisley"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.Imperial, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/imperial_cube.pm3d"), Resources.id("model/crate/imperial_cube"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.Segmented, true, ModelLoader.loadPM3D(Resources.id("models/block/crate/segmented.pm3d"), Resources.id("model/crate/segmented"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadPM3D(Resources.id("models/block/machine_spoked.pm3d"), Resources.id("model/machine_spoked"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Pipe.Large, false, ModelLoader.loadPM3D(Resources.id("models/block/pipe_thick.pm3d"), Resources.id("model/pipe_thick"), new Identifier("block/stone")));

		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialPanelTall1, false, true, false, Resources.id("block/gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialPanelTall2, false, true, false, Resources.id("block/gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialLightTall1, false, true, false, Resources.id("block/gray_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialLightTall2, false, true, false, Resources.id("block/gray_imperial_panel_blank"));

//		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialCutout, true, false, true, Resources.id("block/imperial_cutout_face"), Resources.id("block/imperial_cutout_face"), Resources.id("block/imperial_cutout_border"), EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
//		ModelRegistry.registerConnected(SwgBlocks.Panel.ImperialCutoutPipes, true, false, true, Resources.id("block/imperial_cutout_pipes_face"), Resources.id("block/imperial_cutout_pipes_face"), Resources.id("block/imperial_cutout_pipes_border"), EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));

		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelBordered, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelSplit, true, true, true, null, Resources.id("block/black_imperial_panel_split_center"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.BlackImperialPanelThinBordered, true, true, true, null, Resources.id("block/black_imperial_panel_blank"));
		ModelRegistry.registerConnected(SwgBlocks.Panel.ExternalImperialPlatingConnected);
		ModelRegistry.registerConnected(SwgBlocks.Panel.LargeImperialPlatingConnected);
		ModelRegistry.registerConnected(SwgBlocks.Panel.RustedLargeImperialPlatingConnected);
		ModelRegistry.registerConnected(SwgBlocks.Panel.MossyLargeImperialPlatingConnected);

		ModelRegistry.registerConnected(SwgBlocks.Panel.RustedMetal);

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

		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Vent.Imperial, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SwgBlocks.Vent.ImperialGrated, RenderLayer.getCutout());

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

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(NemManager.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(P3dManager.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(BlasterItemRenderer.INSTANCE);

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> ModelRegistry.INSTANCE);

		EntityRendererRegistry.register(SwgEntities.Ship.T65bXwing, T65BXwingRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Speeder.X34, X34LandspeederRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterBolt, BlasterBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterStunBolt, BlasterStunBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.BlasterIonBolt, BlasterIonBoltRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Misc.ThrownLightsaber, ThrownLightsaberRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Fish.Faa, FaaEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Fish.Laa, LaaEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Amphibian.Worrt, WorrtEntityRenderer::new);
		EntityRendererRegistry.register(SwgEntities.Droid.AstroR2, AstromechRenderer::new);

		EntityRendererRegistry.register(SwgEntities.Misc.KinematicTest, KinematicTestEntityRenderer::new);

		ICustomItemRenderer.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);

		ICustomItemRenderer.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomHudRenderer.register(SwgItems.Blaster.Blaster, BlasterHudRenderer.INSTANCE);

		for (var i : SwgItems.Door.TatooineHome)
			ICustomItemRenderer.register(i, TatooineHomeDoorRenderer.ITEM_RENDERER_INSTANCE);

		//		ICustomSkyRenderer.register(SwgDimensions.Tatooine.WORLD_KEY.getValue(), new TatooineSkyRenderer());

		SwgParticles.register();

		PlayerEvent.EVENT_BUS.subscribe(PlayerEvent.ACCUMULATE_RECOIL, RecoilManager::handleAccumulateRecoil);

		WorldEvent.EVENT_BUS.subscribe(WorldEvent.SLUG_FIRED, BlasterUtil::handleSlugFired);
		WorldEvent.EVENT_BUS.subscribe(WorldEvent.BLASTER_BOLT_HIT, BlasterUtil::handleBoltHit);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (client.player != null)
			{
				if (Resources.REMOTE_VERSION != null)
				{
					Text versionText = new LiteralText(Resources.REMOTE_VERSION.name)
							.styled((style) -> style
									.withItalic(true)
							);
					Text urlText = new LiteralText("https://www.curseforge.com/minecraft/mc-mods/pswg")
							.styled((style) -> style
									.withColor(TextColor.fromRgb(0x5bc0de))
									.withUnderline(true)
									.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/pswg"))
									.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("PSWG on CurseForge")))
							);
					client.player.sendMessage(new TranslatableText("msg.pswg.update", versionText, urlText), false);
				}

				var config = Resources.CONFIG.get();
				if (config.client.showCharacterCustomizeTip)
				{
					client.player.sendMessage(new TranslatableText("msg.pswg.tip.customize_character", TextUtil.stylizeKeybind(Client.KEY_SPECIES_SELECT.getBoundKeyLocalizedText())), false);
					config.client.showCharacterCustomizeTip = false;
					Resources.CONFIG.save();
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.SyncBlasters, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
			SwgBlasterManager.INSTANCE.handlePacket(minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender);
			minecraftClient.execute(() -> {
				((MinecraftClientAccessor)minecraftClient).invokeInitializeSearchableContainers();
				minecraftClient.getSearchableContainer(SearchManager.ITEM_TOOLTIP).reload();
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.SyncLightsabers, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
			SwgLightsaberManager.INSTANCE.handlePacket(minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender);
			minecraftClient.execute(() -> {
				((MinecraftClientAccessor)minecraftClient).invokeInitializeSearchableContainers();
				minecraftClient.getSearchableContainer(SearchManager.ITEM_TOOLTIP).reload();
			});
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
				null //new SpyglassZoomOverlay(new Identifier("libzoomertest:textures/misc/michael.png"))
		);
	}

	public static void registerRenderLayer(RenderLayer layer)
	{
		((BufferBuilderStorageAccessor)MinecraftClient.getInstance().getBufferBuilders()).entityBuilders().put(layer, new BufferBuilder(layer.getExpectedBufferSize()));
	}
}
