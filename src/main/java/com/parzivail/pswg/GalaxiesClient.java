package com.parzivail.pswg;

import com.parzivail.pswg.client.ModelLoader;
import com.parzivail.pswg.client.model.SimpleModels;
import com.parzivail.pswg.client.render.ShipRenderer;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.util.Lumberjack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalaxiesClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		Lumberjack.debug("onInitializeClient");

		SimpleModels.register(Registry.BLOCK.getId(SwgBlocks.Crate.Octagon), ModelLoader.loadPM3D(Resources.identifier("models/block/crate_octagon.pm3d"), Resources.identifier("model/crate_octagon"), new Identifier("block/stone")));

		ModelLoadingRegistry.INSTANCE.registerVariantProvider(r -> SimpleModels.INSTANCE);

		EntityRendererRegistry.INSTANCE.register(GalaxiesMain.EntityTypeShip, (entityRenderDispatcher, context) -> new ShipRenderer(entityRenderDispatcher));
	}
}
