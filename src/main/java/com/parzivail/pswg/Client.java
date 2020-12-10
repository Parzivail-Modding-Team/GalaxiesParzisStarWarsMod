package com.parzivail.pswg;

import com.parzivail.pswg.client.ModelLoader;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.item.render.BlasterItemRenderer;
import com.parzivail.pswg.client.item.render.LightsaberItemRenderer;
import com.parzivail.pswg.client.model.SimpleModels;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.render.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.EmptyRenderer;
import com.parzivail.pswg.client.render.ThrownLightsaberRenderer;
import com.parzivail.pswg.client.render.ship.T65BXwingRenderer;
import com.parzivail.pswg.client.screen.*;
import com.parzivail.pswg.client.texture.remote.RemoteTextureProvider;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgScreenTypes;
import com.parzivail.pswg.util.Lumberjack;
import com.parzivail.util.item.CustomHUDRenderer;
import com.parzivail.util.item.CustomItemRenderer;
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

	public static MinecraftClient minecraft;
	public static RemoteTextureProvider remoteTextureProvider;

	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		minecraft = MinecraftClient.getInstance();

		KeyBindingHelper.registerKeyBinding(KEY_LIGHTSABER_TOGGLE);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);

		ClientTickEvents.START_CLIENT_TICK.register(KeyHandler::handle);

		ScreenRegistry.register(SwgScreenTypes.Crate.Octagon, CrateOctagonScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.MosEisley, CrateMosEisleyScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Crate.ImperialCube, CrateImperialCubeScreen::new);
		ScreenRegistry.register(SwgScreenTypes.MoistureVaporator.GX8, MoistureVaporatorScreen::new);
		ScreenRegistry.register(SwgScreenTypes.Workbench.Blaster, BlasterWorkbenchScreen::new);

		SimpleModels.register(SwgBlocks.Crate.OctagonOrange, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon_orange"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.OctagonGray, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon_gray"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.OctagonBlack, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon_black"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.MosEisley, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_mos_eisley.pm3d"), Resources.identifier("model/crate_mos_eisley"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.ImperialCube, true, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_imperial_cube.pm3d"), Resources.identifier("model/crate_imperial_cube"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Machine.Spoked, true, ModelLoader.loadPM3D(Resources.identifier("models/block/machine_spoked.pm3d"), Resources.identifier("model/machine_spoked"), new Identifier("block/stone")));
		//		SimpleModels.register(SwgBlocks.Machine.Spoked, true, new DebugModel.Unbaked(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/stone"))));

		SimpleModels.register(SwgBlocks.MoistureVaporator.Gx8, false, ModelLoader.loadPM3D(Resources.identifier("models/block/moisture_vaporator_gx8.pm3d"), Resources.identifier("model/moisture_vaporator_gx8"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Pipe.Thick, true, ModelLoader.loadPM3D(Resources.identifier("models/block/pipe_thick.pm3d"), Resources.identifier("model/pipe_thick"), new Identifier("block/stone")));

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> SimpleModels.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.T65bXwing, (entityRenderDispatcher, context) -> new T65BXwingRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.ChaseCam, (entityRenderDispatcher, context) -> new EmptyRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.BlasterBolt, (entityRenderDispatcher, context) -> new BlasterBoltRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Misc.ThrownLightsaber, (entityRenderDispatcher, context) -> new ThrownLightsaberRenderer(entityRenderDispatcher, context.getItemRenderer()));

		CustomItemRenderer.register(SwgItems.Lightsaber.Lightsaber, new LightsaberItemRenderer());

		CustomItemRenderer.register(SwgItems.Blaster.A280, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/blaster/a280c.pm3d"))), Resources.identifier("textures/blaster/a280c.png"), Resources.identifier("textures/blaster/a280c_inventory.png")));
		CustomHUDRenderer.registerCustomHUD(SwgItems.Blaster.A280, (itemStack, matrixStack) -> {
		});

		CustomItemRenderer.register(SwgItems.Blaster.DH17, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/blaster/dh17.pm3d"))), Resources.identifier("textures/blaster/dh17.png"), Resources.identifier("textures/blaster/dh17_inventory.png")));
		CustomHUDRenderer.registerCustomHUD(SwgItems.Blaster.DH17, (itemStack, matrixStack) -> {
		});

		CustomItemRenderer.register(SwgItems.Blaster.E11, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/blaster/e11.pm3d"))), Resources.identifier("textures/blaster/e11.png"), Resources.identifier("textures/blaster/e11_inventory.png")));
		CustomHUDRenderer.registerCustomHUD(SwgItems.Blaster.E11, (itemStack, matrixStack) -> {
		});

		CustomItemRenderer.register(SwgItems.Blaster.EE3, new BlasterItemRenderer(new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/blaster/ee3.pm3d"))), Resources.identifier("textures/blaster/ee3.png"), Resources.identifier("textures/blaster/ee3_inventory.png")));
		CustomHUDRenderer.registerCustomHUD(SwgItems.Blaster.EE3, (itemStack, matrixStack) -> {
		});
	}
}
