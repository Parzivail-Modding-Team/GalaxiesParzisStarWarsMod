package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class TripwireMineModel extends EntityModel<TripwireMineRenderState>
{
	public TripwireMineModel(ModelPart root)
	{
		super(root);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0F, 0.0F, 0f, 0f, (float)Math.PI));
		//modelPartData.addChild("crossLaser1", ModelPartBuilder.create().uv(5, 4).cuboid(0.0F, -2F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.7854F, (float)Math.PI));
		//modelPartData.addChild("crossLaser2", ModelPartBuilder.create().uv(1, 4).cuboid(0.0F, -2F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));
		modelPartData.addChild("laser", ModelPartBuilder.create().uv(0, 7).cuboid(-0.5F, -1.75F, -0.5f, 1.0F, 1.0F, 1.0F, new Dilation(-0.25F)), ModelTransform.of(0.0F, -1.25F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(TripwireMineRenderState state)
	{
		super.setAngles(state);
	}
}