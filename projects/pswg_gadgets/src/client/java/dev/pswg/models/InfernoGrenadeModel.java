package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class InfernoGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public InfernoGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -6.0F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0, 0, 0, 0, 0, (float)Math.PI));
		modelPartData.addChild("button", ModelPartBuilder.create().uv(0, 12).cuboid(-0.5F, -6.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0, 0, 0, 0, 0, (float)Math.PI));
		modelPartData.addChild("attachment", ModelPartBuilder.create().uv(12, 4).cuboid(-1.75F, -4.0F, -0.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("handle", ModelPartBuilder.create()
		                                                 .uv(14, 2).cuboid(-3.9F, -3.75F - 3.25f, -1.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F))
		                                                 .uv(12, 0).cuboid(-3.9F, -3.75F - 3.25f, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
		                                                 .uv(13, 0).cuboid(-3.9F, -3.75F - 1.25f, -1.0F, 1.0F, 0.0F, 2.0F, new Dilation(0.0F))
		                                                 .uv(14, 2).cuboid(-3.9F, -3.75F - 3.25f, 1.0F, 1.0F, 2.0F, 0.0F, new Dilation(0.0F)),
		                       ModelTransform.of(-1.25F, -3.75F, 0.0F, 0.0F, 0.0F, 0.1309F + (float)Math.PI));
		modelPartData.addChild("button_guard", ModelPartBuilder.create()
		                                                       .uv(0, 12).cuboid(-0.5F, -6.75F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
		                                                       .uv(3, 1).cuboid(-1.0F, -6.75F, -1.0F, 2.0F, 1.0F, 0.0F, new Dilation(0.0F))
		                                                       .uv(7, 6).cuboid(-1.0F, -6.75F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F))
		                                                       .uv(7, 6).cuboid(1.0F, -6.75F, -1.0F, 0.0F, 1.0F, 2.0F, new Dilation(0.0F)),
		                       ModelTransform.of(0, 0, 0, 0, 0, (float)Math.PI));


		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(GrenadeRenderState state)
	{
		super.setAngles(state);
	}
}