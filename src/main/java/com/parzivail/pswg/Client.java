package com.parzivail.pswg;

import com.parzivail.pswg.client.ModelLoader;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.model.SimpleModels;
import com.parzivail.pswg.client.remote.RemoteTextureProvider;
import com.parzivail.pswg.client.render.BlasterBoltRenderer;
import com.parzivail.pswg.client.render.EmptyRenderer;
import com.parzivail.pswg.client.render.ship.T65BXwingRenderer;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class Client implements ClientModInitializer
{
	//	private static final FabricKeyBinding KEY_THROTTLE_UP = FabricKeyBinding.Builder.create(Resources.identifier("throttle_up"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_W | GLFW.GLFW_MOD_SHIFT, "category.pswg").build();
	//	private static final FabricKeyBinding KEY_THROTTLE_DOWN = FabricKeyBinding.Builder.create(Resources.identifier("throttle_down"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_S | GLFW.GLFW_MOD_SHIFT, "category.pswg").build();

	public static MinecraftClient minecraft;
	public static RemoteTextureProvider remoteTextureProvider;

	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		minecraft = MinecraftClient.getInstance();

		// TODO: migrate to the new keybind system
		KeyBindingRegistry.INSTANCE.addCategory("category.pswg");

		//		KeyBindingRegistry.INSTANCE.register(KEY_THROTTLE_UP);
		//		KeyBindingRegistry.INSTANCE.register(KEY_THROTTLE_DOWN);

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x8AB534, SwgBlocks.Leaves.Sequoia);

		ClientTickCallback.EVENT.register(KeyHandler::handle);

		SimpleModels.register(SwgBlocks.Crate.OctagonOrange, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon_orange"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.OctagonGray, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon_gray"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.OctagonBlack, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon_black"), new Identifier("block/stone")));
		SimpleModels.register(SwgBlocks.Crate.MosEisley, ModelLoader.loadPM3D(Resources.identifier("models/block/crate_mos_eisley.pm3d"), Resources.identifier("model/crate_mos_eisley"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Machine.Spoked, ModelLoader.loadPM3D(Resources.identifier("models/block/machine_spoked.pm3d"), Resources.identifier("model/machine_spoked"), new Identifier("block/stone")));

		SimpleModels.register(SwgBlocks.Vaporator.Gx8, ModelLoader.loadPM3D(Resources.identifier("models/block/moisture_vaporator_gx8.pm3d"), Resources.identifier("model/moisture_vaporator_gx8"), new Identifier("block/stone")));

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> SimpleModels.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.T65bXwing, (entityRenderDispatcher, context) -> new T65BXwingRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.ChaseCam, (entityRenderDispatcher, context) -> new EmptyRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.BlasterBolt, (entityRenderDispatcher, context) -> new BlasterBoltRenderer(entityRenderDispatcher));
	}
}
