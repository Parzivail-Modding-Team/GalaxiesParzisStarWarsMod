package com.parzivail.pswg;

import com.parzivail.pswg.client.ModelLoader;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.item.render.BlasterItemRenderer;
import com.parzivail.pswg.client.item.render.LightsaberItemRenderer;
import com.parzivail.pswg.client.item.render.hud.BlasterHudRenderer;
import com.parzivail.pswg.client.model.SimpleModel;
import com.parzivail.pswg.client.model.SimpleModels;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.render.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.ThrownLightsaberRenderer;
import com.parzivail.pswg.client.render.ship.T65BXwingRenderer;
import com.parzivail.pswg.client.screen.*;
import com.parzivail.pswg.client.texture.remote.RemoteTextureProvider;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.pswg.util.Lumberjack;
import com.parzivail.util.item.CustomItemRenderer;
import com.parzivail.util.item.ICustomHudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class Client implements ClientModInitializer
{
	public static final KeyBinding KEY_LIGHTSABER_TOGGLE = new KeyBinding(Resources.keyBinding("lightsaber_toggle"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "key.category.pswg");
	public static final KeyBinding KEY_SHIP_INPUT_MODE_OVERRIDE = new KeyBinding(Resources.keyBinding("ship_input_mode_override"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.pswg");

	public static MinecraftClient minecraft;
	public static RemoteTextureProvider remoteTextureProvider;

	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		minecraft = MinecraftClient.getInstance();

		KeyBindingHelper.registerKeyBinding(KEY_LIGHTSABER_TOGGLE);
		KeyBindingHelper.registerKeyBinding(KEY_SHIP_INPUT_MODE_OVERRIDE);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);

		ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::handle);

		SimpleModels.register(SwgBlocks.Barrel.MosEisley, true, ModelLoader.loadPM3D(SimpleModel.Discriminator.RENDER_SEED, Resources.identifier("models/block/barrel/mos_eisley.pm3d"), Resources.identifier("model/barrel/mos_eisley"), new Identifier("block/stone")));

		ScreenRegistry.register(SwgScreenTypes.Crate.Octagon, CrateOctagonScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.MosEisley, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.ImperialCube, CrateGenericSmallScreen::new);
		ScreenRegistry.register(SwgScreenTypes.MoistureVaporator.GX8, MoistureVaporatorScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Blaster, BlasterWorkbenchScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Lightsaber, LightsaberForgeScreen::new);

		SimpleModels.register(SwgBlocks.Tank.Fusion, true, ModelLoader.loadPM3D(Resources.identifier("models/block/tank/fusion.pm3d"), Resources.identifier("model/tank/fusion"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Crate.OctagonOrange, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/octagon.pm3d"), Resources.identifier("model/crate/octagon_orange"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.OctagonGray, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/octagon.pm3d"), Resources.identifier("model/crate/octagon_gray"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.OctagonBlack, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/octagon.pm3d"), Resources.identifier("model/crate/octagon_black"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.MosEisley, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/mos_eisley.pm3d"), Resources.identifier("model/crate/mos_eisley"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.ImperialCube, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate/imperial_cube.pm3d"), Resources.identifier("model/crate/imperial_cube"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Light.FloorWedge, true, ModelLoader.loadPM3D(Resources.identifier("models/block/light/floor_wedge.pm3d"), Resources.identifier("model/light/floor_wedge"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Light.WallCluster, true, ModelLoader.loadPM3D(Resources.identifier("models/block/light/wall_cluster.pm3d"), Resources.identifier("model/light/wall_cluster"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadPM3D(Resources.identifier("models/block/machine_spoked.pm3d"), Resources.identifier("model/machine_spoked"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.MoistureVaporator.Gx8, false, ModelLoader.loadPM3D(Resources.identifier("models/block/moisture_vaporator_gx8.pm3d"), Resources.identifier("model/moisture_vaporator_gx8"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Pipe.Thick, true, ModelLoader.loadPM3D(Resources.identifier("models/block/pipe_thick.pm3d"), Resources.identifier("model/pipe_thick"), new Identifier("block/stone")));

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> SimpleModels.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.T65bXwing, (entityRenderDispatcher, context) -> new T65BXwingRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.BlasterBolt, (entityRenderDispatcher, context) -> new BlasterBoltRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.ThrownLightsaber, (entityRenderDispatcher, context) -> new ThrownLightsaberRenderer(entityRenderDispatcher, context.getItemRenderer()));

		CustomItemRenderer.register(SwgItems.Lightsaber.Lightsaber, new LightsaberItemRenderer());

		CustomItemRenderer.register(SwgItems.Blaster.A280, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/blaster/a280.pm3d"))), Resources.identifier("textures/model/blaster/a280.png"), Resources.identifier("textures/model/blaster/a280_inventory.png")));
		ICustomHudRenderer.registerCustomHUD(SwgItems.Blaster.A280, BlasterHudRenderer.INSTANCE);

		CustomItemRenderer.register(SwgItems.Blaster.DH17, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/blaster/dh17.pm3d"))), Resources.identifier("textures/model/blaster/dh17.png"), Resources.identifier("textures/model/blaster/dh17_inventory.png")));
		ICustomHudRenderer.registerCustomHUD(SwgItems.Blaster.DH17, BlasterHudRenderer.INSTANCE);

		CustomItemRenderer.register(SwgItems.Blaster.E11, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/blaster/e11.pm3d"))), Resources.identifier("textures/model/blaster/e11.png"), Resources.identifier("textures/model/blaster/e11_inventory.png")));
		ICustomHudRenderer.registerCustomHUD(SwgItems.Blaster.E11, BlasterHudRenderer.INSTANCE);

		CustomItemRenderer.register(SwgItems.Blaster.EE3, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/item/blaster/ee3.pm3d"))), Resources.identifier("textures/model/blaster/ee3.png"), Resources.identifier("textures/model/blaster/ee3_inventory.png")));
		ICustomHudRenderer.registerCustomHUD(SwgItems.Blaster.EE3, BlasterHudRenderer.INSTANCE);
	}
}
