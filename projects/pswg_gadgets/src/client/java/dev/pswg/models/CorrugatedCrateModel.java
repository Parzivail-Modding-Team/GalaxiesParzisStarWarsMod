package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import java.util.function.Function;

public class CorrugatedCrateModel extends Model
{
	public CorrugatedCrateModel(ModelPart root, Function<Identifier, RenderType> layerFactory)
	{
		super(root, layerFactory);
	}

	public static LayerDefinition getTexturedModelData()
	{
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();

		modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(1F, 0F, 1F, 14.0F, 16.0F, 14.0F, new CubeDeformation(0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0, 0, 0));

		return LayerDefinition.create(modelData, 64, 64);
	}
}
