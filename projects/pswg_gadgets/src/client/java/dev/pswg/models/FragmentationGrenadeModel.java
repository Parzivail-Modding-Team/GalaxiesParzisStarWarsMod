package dev.pswg.models;

import dev.pswg.models.GrenadeRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;

public class FragmentationGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public FragmentationGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("cube1", ModelPartBuilder.create().uv(10, 5).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.4F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("cube2", ModelPartBuilder.create().uv(6, 8).cuboid(0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("cube3", ModelPartBuilder.create().uv(6, 8).cuboid(-1.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new Dilation(0.2F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("cube4", ModelPartBuilder.create().uv(2, 0).mirrored().cuboid(-3.5F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addChild("cube5", ModelPartBuilder.create().uv(0, 4).cuboid(1.5F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0F, 0F, 0F, 0, 0, (float)Math.PI));

		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void setAngles(GrenadeRenderState state)
	{
		super.setAngles(state);
	}
}