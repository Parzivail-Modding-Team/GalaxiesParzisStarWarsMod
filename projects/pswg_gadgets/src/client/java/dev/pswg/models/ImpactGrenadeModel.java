package dev.pswg.models;

import dev.pswg.models.GrenadeRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class ImpactGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public ImpactGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("middle", ModelPartBuilder.create().uv(0, 9).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("top", ModelPartBuilder.create().uv(6, 6).cuboid(-0.5F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("pin_p1", ModelPartBuilder.create().uv(6, 13).cuboid(1F, -5F, -1.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.025F)), ModelTransform.of(0F, 0.25F, 0, 0.1745F, 0, (float)Math.PI));
		modelPartData.addChild("pin_p2", ModelPartBuilder.create().uv(6, 13).cuboid(-1.0F, -5F, -1.25F, 0.0F, 1.0F, 2.0F, new Dilation(0.025F)), ModelTransform.of(0F, 0.25F, 0, 0.1745F, 0, (float)Math.PI));
		modelPartData.addChild("pin_p3", ModelPartBuilder.create().uv(2, 15).cuboid(-1.0F, -5.F, -1.25F, 2.0F, 1.0F, 0.0F, new Dilation(0.026F)), ModelTransform.of(0F, 0.25F, 0, 0.1745F, 0, (float)Math.PI));
		modelPartData.addChild("pin_p4", ModelPartBuilder.create().uv(12, 15).cuboid(-1.0F, -5.F, 0.75F, 2.0F, 1.0F, 0.0F, new Dilation(0.026F)), ModelTransform.of(0F, 0.25F, 0, 0.1745F, 0, (float)Math.PI));

		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(GrenadeRenderState state)
	{
		super.setAngles(state);
	}
}