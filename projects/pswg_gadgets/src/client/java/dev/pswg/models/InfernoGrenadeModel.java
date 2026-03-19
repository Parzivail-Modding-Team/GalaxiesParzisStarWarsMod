package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class InfernoGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public InfernoGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -6.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0, 0, 0, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("button", CubeListBuilder.create().texOffs(0, 12).addBox(-0.5F, -6.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0, 0, 0, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("attachment", CubeListBuilder.create().texOffs(12, 4).addBox(-1.75F, -4.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("handle", CubeListBuilder.create()
		                                                 .texOffs(14, 2).addBox(-3.9F, -3.75F - 3.25f, -1.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		                                                 .texOffs(12, 0).addBox(-3.9F, -3.75F - 3.25f, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		                                                 .texOffs(13, 0).addBox(-3.9F, -3.75F - 1.25f, -1.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
		                                                 .texOffs(14, 2).addBox(-3.9F, -3.75F - 3.25f, 1.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
		                       PartPose.offsetAndRotation(-1.25F, -3.75F, 0.0F, 0.0F, 0.0F, 0.1309F + (float)Math.PI));
		modelPartData.addOrReplaceChild("button_guard", CubeListBuilder.create()
		                                                       .texOffs(0, 12).addBox(-0.5F, -6.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		                                                       .texOffs(3, 1).addBox(-1.0F, -6.75F, -1.0F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
		                                                       .texOffs(7, 6).addBox(-1.0F, -6.75F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
		                                                       .texOffs(7, 6).addBox(1.0F, -6.75F, -1.0F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
		                       PartPose.offsetAndRotation(0, 0, 0, 0, 0, (float)Math.PI));


		return LayerDefinition.create(modelData, 32, 32);
	}

	@Override
	public void setupAnim(GrenadeRenderState state)
	{
		super.setupAnim(state);
	}
}
