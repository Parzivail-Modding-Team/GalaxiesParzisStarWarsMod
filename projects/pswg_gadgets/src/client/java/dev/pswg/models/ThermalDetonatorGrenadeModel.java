package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class ThermalDetonatorGrenadeModel extends EntityModel<EntityRenderState>
{
	public ThermalDetonatorGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static TexturedModelData getTexturedModelData()
	{

		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));

		modelPartData.addChild("trigger", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5F, -3.5F, -0.75F, 1.0F, 1.0F, 2.0F, new Dilation(0.0F)),
		                       ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(EntityRenderState state)
	{
		super.setAngles(state);
	}
}