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

public class FragmentationGrenadeModel extends EntityModel<GrenadeRenderState>
{
	public FragmentationGrenadeModel(ModelPart root)
	{
		super(root);
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("cube1", CubeListBuilder.create().texOffs(10, 5).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("cube2", CubeListBuilder.create().texOffs(6, 8).addBox(0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("cube3", CubeListBuilder.create().texOffs(6, 8).addBox(-1.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("cube4", CubeListBuilder.create().texOffs(2, 0).mirror().addBox(-3.5F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));
		modelPartData.addOrReplaceChild("cube5", CubeListBuilder.create().texOffs(0, 4).addBox(1.5F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, (float)Math.PI));

		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void setupAnim(GrenadeRenderState state)
	{
		super.setupAnim(state);
	}
}
