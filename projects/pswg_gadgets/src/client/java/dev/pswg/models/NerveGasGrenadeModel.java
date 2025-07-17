package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class NerveGasGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public NerveGasGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("rod", ModelPartBuilder.create().uv(0, 7).cuboid(-0.5F, -4F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0F, 0.0f, 0.0f, 0.0f, (float)Math.PI));
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -6F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));
		modelPartData.addChild("disc", ModelPartBuilder.create().uv(0, 4).cuboid(-1.0F, -3F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));

		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(GrenadeRenderState state)
	{
		super.setAngles(state);
	}
}