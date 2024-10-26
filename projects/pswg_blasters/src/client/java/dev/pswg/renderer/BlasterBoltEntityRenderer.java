package dev.pswg.renderer;

import dev.pswg.entity.BlasterBoltEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class BlasterBoltEntityRenderer extends EntityRenderer<BlasterBoltEntity, BlasterBoltEntityRenderer.State>
{
	//	public static final EntityModelLayer LAYER = new EntityModelLayer(Blasters.id("blaster_bolt_entity"), "main");
	//
	//	public static TexturedModelData getTexturedModelData()
	//	{
	//		return null;
	//	}

	public static class State extends EntityRenderState
	{
		public State()
		{
		}
	}

	public BlasterBoltEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context);
	}

	@Override
	public State createRenderState()
	{
		return new State();
	}
}
