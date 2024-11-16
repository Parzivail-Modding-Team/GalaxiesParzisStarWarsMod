package dev.pswg;

import dev.pswg.api.GalaxiesClientAddon;
import dev.pswg.renderer.ThermalDetonatorEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;

/**
 * The main entrypoint for PSWG client-side gadget features
 */
public class GadgetsClient implements GalaxiesClientAddon
{
	@Override
	public void onGalaxiesClientReady()
	{
		EntityRendererRegistry.register(Gadgets.THERMAL_DETONATOR_ENTITY, ThermalDetonatorEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(ThermalDetonatorEntityRenderer.MODEL_LAYER, ThermalDetonatorEntityRenderer.Model::getTexturedModelData);

		Gadgets.LOGGER.info("Client module initialized");
	}
}
