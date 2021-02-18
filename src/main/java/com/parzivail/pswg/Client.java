package com.parzivail.pswg;

import com.parzivail.pswg.client.ModelLoader;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.render.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.RenderLayerHelper;
import com.parzivail.pswg.client.render.ThrownLightsaberRenderer;
import com.parzivail.pswg.client.render.block.TatooineHomeDoorRenderer;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.client.render.item.LightsaberItemRenderer;
import com.parzivail.pswg.client.render.item.hud.BlasterHudRenderer;
import com.parzivail.pswg.client.render.ship.T65BXwingRenderer;
import com.parzivail.pswg.client.screen.*;
import com.parzivail.pswg.client.texture.remote.RemoteTextureProvider;
import com.parzivail.pswg.client.texture.stacked.StackedTextureProvider;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.data.SwgBlasterManager;
import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.Lumberjack;
import com.parzivail.util.client.model.DynamicBakedModel;
import com.parzivail.util.client.model.ModelRegistry;
import com.parzivail.util.item.ICustomHudRenderer;
import com.parzivail.util.item.ICustomItemRenderer;
import com.parzivail.util.item.ICustomPoseItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Client implements ClientModInitializer
{
	public static final KeyBinding KEY_LIGHTSABER_TOGGLE = new KeyBinding(Resources.keyBinding("lightsaber_toggle"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.category.pswg");
	public static final KeyBinding KEY_SHIP_INPUT_MODE_OVERRIDE = new KeyBinding(Resources.keyBinding("ship_input_mode_override"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.pswg");

	public static final KeyBinding KEY_SPECIES_SELECT = new KeyBinding(Resources.keyBinding("species_select"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.category.pswg");

	public static MinecraftClient minecraft;
	public static RemoteTextureProvider remoteTextureProvider;
	public static StackedTextureProvider stackedTextureProvider;

	private static SwgBlasterManager blasterLoader;

	public static boolean isShipClientControlled(ShipEntity shipEntity)
	{
		if (minecraft == null || minecraft.player == null)
			return false;

		return ShipEntity.getShip(minecraft.player) == shipEntity;
	}

	public static void resetLoaders()
	{
		blasterLoader = new SwgBlasterManager();
	}

	public static SwgBlasterManager getBlasterLoader()
	{
		return blasterLoader;
	}

	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		minecraft = MinecraftClient.getInstance();

		KeyBindingHelper.registerKeyBinding(KEY_LIGHTSABER_TOGGLE);
		KeyBindingHelper.registerKeyBinding(KEY_SHIP_INPUT_MODE_OVERRIDE);
		KeyBindingHelper.registerKeyBinding(KEY_SPECIES_SELECT);

		ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::handle);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);


		BlockEntityRendererRegistry.INSTANCE.register(SwgBlocks.Door.TatooineHomeBlockEntityType, TatooineHomeDoorRenderer::new);

		ModelRegistry.register(SwgBlocks.Barrel.MosEisley, true, ModelLoader.loadPM3D(DynamicBakedModel.Discriminator.RENDER_SEED, Resources.identifier("models/block/barrel/mos_eisley.pm3d"), Resources.identifier("model/barrel/mos_eisley"), new Identifier("block/stone")));

		ScreenRegistry.register(SwgScreenTypes.Crate.Octagon, CrateOctagonScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.MosEisley, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.ImperialCube, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.MoistureVaporator.GX8, MoistureVaporatorScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Blaster, BlasterWorkbenchScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Lightsaber, LightsaberForgeScreen::new);

		ModelRegistry.register(SwgBlocks.Tank.Fusion, true, ModelLoader.loadPM3D(Resources.identifier("models/block/tank/fusion.pm3d"), Resources.identifier("model/tank/fusion"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Crate.OctagonOrange, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/octagon.pm3d"), Resources.identifier("model/crate/octagon_orange"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.OctagonGray, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/octagon.pm3d"), Resources.identifier("model/crate/octagon_gray"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.OctagonBlack, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/octagon.pm3d"), Resources.identifier("model/crate/octagon_black"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.MosEisley, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/mos_eisley.pm3d"), Resources.identifier("model/crate/mos_eisley"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Crate.ImperialCube, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/imperial_cube.pm3d"), Resources.identifier("model/crate/imperial_cube"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Light.FloorWedge, true, ModelLoader.loadPM3D(Resources.identifier("models/block/light/floor_wedge.pm3d"), Resources.identifier("model/light/floor_wedge"), new Identifier("block/stone")));
		ModelRegistry.register(SwgBlocks.Light.WallCluster, true, ModelLoader.loadPM3D(Resources.identifier("models/block/light/wall_cluster.pm3d"), Resources.identifier("model/light/wall_cluster"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadPM3D(Resources.identifier("models/block/machine_spoked.pm3d"), Resources.identifier("model/machine_spoked"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.MoistureVaporator.Gx8, false, ModelLoader.loadPM3D(Resources.identifier("models/block/moisture_vaporator/gx8.pm3d"), Resources.identifier("model/moisture_vaporator_gx8"), new Identifier("block/stone")));

		ModelRegistry.register(SwgBlocks.Pipe.Thick, true, ModelLoader.loadPM3D(Resources.identifier("models/block/pipe_thick.pm3d"), Resources.identifier("model/pipe_thick"), new Identifier("block/stone")));

		RenderLayerHelper.addBlock(SwgBlocks.Plant.FunnelFlower, RenderLayer.getCutout());
		RenderLayerHelper.addBlock(SwgBlocks.Plant.BlossomingFunnelFlower, RenderLayer.getCutout());

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> ModelRegistry.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.T65bXwing, (entityRenderDispatcher, context) -> new T65BXwingRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.BlasterBolt, (entityRenderDispatcher, context) -> new BlasterBoltRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.ThrownLightsaber, (entityRenderDispatcher, context) -> new ThrownLightsaberRenderer(entityRenderDispatcher, context.getItemRenderer()));

		ICustomItemRenderer.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);
		ICustomPoseItem.register(SwgItems.Lightsaber.Lightsaber, LightsaberItemRenderer.INSTANCE);

//		try
//		{
//			AnimationInfo animationInfo = AnimationLoader.loadAnimation(PIO.getStream("assets", Resources.identifier("animations/player/lightsaber/idle.dca")));
//			int i = 0;
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}

		ICustomItemRenderer.register(SwgItems.Blaster.Blaster, BlasterItemRenderer.INSTANCE);
		ICustomHudRenderer.registerCustomHUD(SwgItems.Blaster.Blaster, BlasterHudRenderer.INSTANCE);

		ClientPlayNetworking.registerGlobalReceiver(SwgPackets.S2C.PacketSyncBlasters, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
			if (blasterLoader != null)
				blasterLoader.handlePacket(minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender);
			else
				Lumberjack.error("Attempted to sync blaster descriptors without initializing the client loader!");
		});
	}
}
