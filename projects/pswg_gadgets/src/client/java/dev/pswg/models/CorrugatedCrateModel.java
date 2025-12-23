package dev.pswg.models;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class CorrugatedCrateModel extends Model
{
	public CorrugatedCrateModel(ModelPart root, Function<Identifier, RenderLayer> layerFactory)
	{
		super(root, layerFactory);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(1F, 0F, 1F, 14.0F, 16.0F, 14.0F, new Dilation(0F)), ModelTransform.of(0F, 0F, 0F, 0, 0, 0));

		return TexturedModelData.of(modelData, 64, 64);
	}
}
