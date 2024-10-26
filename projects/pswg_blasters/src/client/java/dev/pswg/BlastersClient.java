package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.renderer.BlasterBoltEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/**
 * The main entrypoint for PSWG client-side blaster features
 */
public class BlastersClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		EntityRendererRegistry.register(Blasters.BLASTER_BOLT_ENTITY, BlasterBoltEntityRenderer::new);
		//		EntityModelLayerRegistry.registerModelLayer(BlasterBoltEntityRenderer.LAYER, BlasterBoltEntityRenderer::getTexturedModelData);

		Blasters.LOGGER.info("Client module initialized");
	}
}
