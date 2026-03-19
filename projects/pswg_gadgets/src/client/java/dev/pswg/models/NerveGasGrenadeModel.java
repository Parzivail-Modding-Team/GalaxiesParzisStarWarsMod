package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class NerveGasGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public NerveGasGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("rod", CubeListBuilder.create().texOffs(0, 7).addBox(-0.5F, -4F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0F, 0.0f, 0.0f, 0.0f, (float)Math.PI));
		modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -6F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));
		modelPartData.addOrReplaceChild("disc", CubeListBuilder.create().texOffs(0, 4).addBox(-1.0F, -3F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));

		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void setupAnim(GrenadeRenderState state)
	{
		super.setupAnim(state);
	}
}
