package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class TripwireMineModel extends EntityModel<TripwireMineRenderState>
{
	private final ModelPart root;
	public TripwireMineModel(ModelPart root)
	{
		super(root);
		this.root = root.getChild("body");
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0F, 0.0F, 0f, 0f, (float)Math.PI));
		//modelPartData.addChild("crossLaser1", ModelPartBuilder.create().uv(5, 4).cuboid(0.0F, -2F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, 0.7854F, (float)Math.PI));
		//modelPartData.addChild("crossLaser2", ModelPartBuilder.create().uv(1, 4).cuboid(0.0F, -2F, -0.5F, 0.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.5F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));
		modelPartData.addOrReplaceChild("laser", CubeListBuilder.create().texOffs(0, 7).addBox(-0.5F, -1.75F, -0.5f, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, -1.25F, 0.0F, 0.0F, -0.7854F, (float)Math.PI));
		return LayerDefinition.create(modelData, 16, 16);
	}

	@Override
	public void setupAnim(TripwireMineRenderState state)
	{
		this.root.setRotation(state.pitch, state.yaw, 0);
		super.setupAnim(state);
	}
}
