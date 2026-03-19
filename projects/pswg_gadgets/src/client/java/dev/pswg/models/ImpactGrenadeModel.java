package dev.pswg.models;

import dev.pswg.models.GrenadeRenderState;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class ImpactGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public ImpactGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(0, 9).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("top", CubeListBuilder.create().texOffs(6, 6).addBox(-0.5F, -4.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("pin", CubeListBuilder.create()
		                                              .texOffs(6, 13).addBox(1F, -5F, -1.25F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.025F))
		                                              .texOffs(6, 13).addBox(-1.0F, -5F, -1.25F, 0.0F, 1.0F, 2.0F, new CubeDeformation(0.025F))
		                                              .texOffs(2, 15).addBox(-1.0F, -5.F, -1.25F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.026F))
		                                              .texOffs(12, 15).addBox(-1.0F, -5.F, 0.75F, 2.0F, 1.0F, 0.0F, new CubeDeformation(0.026F)),
		                       PartPose.offsetAndRotation(0F, 0.25F, 0, 0.1745F, 0, (float)Math.PI));


		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void setupAnim(GrenadeRenderState state)
	{
		super.setupAnim(state);
	}
}
