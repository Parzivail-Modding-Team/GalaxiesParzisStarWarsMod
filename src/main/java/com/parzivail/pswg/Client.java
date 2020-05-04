package com.parzivail.pswg;

import com.parzivail.pswg.client.ModelLoader;
import com.parzivail.pswg.client.input.KeyHandler;
import com.parzivail.pswg.client.model.SimpleModels;
import com.parzivail.pswg.client.render.EmptyRenderer;
import com.parzivail.pswg.client.render.ShipRenderer;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Client implements ClientModInitializer
{
	//	private static final FabricKeyBinding KEY_THROTTLE_UP = FabricKeyBinding.Builder.create(Resources.identifier("throttle_up"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_W | GLFW.GLFW_MOD_SHIFT, "category.pswg").build();
	//	private static final FabricKeyBinding KEY_THROTTLE_DOWN = FabricKeyBinding.Builder.create(Resources.identifier("throttle_down"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_S | GLFW.GLFW_MOD_SHIFT, "category.pswg").build();

	public static MinecraftClient minecraft;

	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		minecraft = MinecraftClient.getInstance();

		KeyBindingRegistry.INSTANCE.addCategory("category.pswg");

		//		KeyBindingRegistry.INSTANCE.register(KEY_THROTTLE_UP);
		//		KeyBindingRegistry.INSTANCE.register(KEY_THROTTLE_DOWN);

		SimpleModels.register(Registry.BLOCK.getId(SwgBlocks.Crate.Octagon), ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon"), new Identifier("block/stone")));

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> SimpleModels.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.T65bXwing, (entityRenderDispatcher, context) -> new ShipRenderer(entityRenderDispatcher));
		EntityRendererRegistry.INSTANCE.register(SwgEntities.Ship.ChaseCam, (entityRenderDispatcher, context) -> new EmptyRenderer(entityRenderDispatcher));

		ClientTickCallback.EVENT.register(KeyHandler::handle);
	}
}
