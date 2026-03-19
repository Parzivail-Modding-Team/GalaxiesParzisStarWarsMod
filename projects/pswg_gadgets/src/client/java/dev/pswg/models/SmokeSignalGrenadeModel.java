package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SmokeSignalGrenadeModel extends EntityModel<EntityRenderState>
{
	public SmokeSignalGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static LayerDefinition getTexturedModelData()
	{

		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));

		modelPartData.addOrReplaceChild("trigger", CubeListBuilder.create().texOffs(0, 6).addBox(-0.5F, -3.5F, -0.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void setupAnim(EntityRenderState state)
	{
		super.setupAnim(state);
	}
}
