package com.parzivail.pswg.client.render.block;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.PlateBlockEntity;
import com.parzivail.pswg.container.SwgTags;
import com.parzivail.util.client.render.ICustomItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.List;

public class PlateItemRenderer implements ICustomItemRenderer
{
	@Override
	public void render(LivingEntity player, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		int foodAmount = stack.getNbt().getInt("food_amount");
		List<ItemStack> foodList = new ArrayList<>();
		for (int i = 0; i < foodAmount; i++)
		{
			foodList.add(i, stack);
		}

		matrices.push();
		if (!foodList.isEmpty())
		{
			if (foodList.get(0).isIn(SwgTags.Items.MAIN_COURSE) || foodList.size() <= 1)
				matrices.translate(0.2f, 0f, 0.2f);
			matrices.translate(0.3f, 1f / 16f, 0.3f);
		}

		//matrices.scale(0.5f, 0.5f, 0.5f);
		P3dModel lastModel = null;
		ItemStack lastItem = null;
		for (int i = 0; i < foodList.size(); i += 1)
		{
			var registryKey = foodList.get(i).getItem().getRegistryEntry().getKey().get().getValue().getPath();
			var id = Resources.id("block/food/" + registryKey);
			//matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(145), 0.35f, 0, 0.35f);

			var foodModel = P3dManager.INSTANCE.get(id);
			var foodTexture = new Identifier(id.getNamespace(), "textures/block/model/food/" + registryKey + ".png");

			Identifier finalFoodTexture = foodTexture;
			if (foodList.get(0).isIn(SwgTags.Items.MAIN_COURSE))
			{
				if (i == 1 && foodList.size() != i + 1)
				{
					matrices.translate(-0.2f, 0f, -0.2f);
					//matrices.translate(0, 0, 0.2);
				}
				if (i > 0)
				{
					var tY = lastModel.bounds().getMax(Direction.Axis.Y) - 0.f;
					//var tY = lastModel.bounds().getLengthY();
					if (foodList.get(i - 1).isIn(SwgTags.Items.MAIN_COURSE))
						matrices.translate(0, tY - (1f / 16f), 0);
					else
					{
						matrices.translate(0.2, 0, 0.2);
						matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360f / (foodList.size() - 1)), 0f, 0, 0f);
						matrices.translate(-0.2, 0, -0.2);
						//matrices.translate((0.5f * RotationAxis.POSITIVE_Y.rotationDegrees(90).y) - 0.15, 0, 0.25);
						//matrices.translate(-0.25, 0, -0.25);

					}
				}
			}
			else
			{

				matrices.translate(0.2, 0, 0.2);
				var f = (float)foodList.size();
				//matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360f/foodList.size()), 0f, 0, 0f);
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360f / f), 0f, 0, 0f);
				matrices.translate(-0.2, 0, -0.2);
			}

			lastModel = foodModel;

			foodModel.render(matrices, vertexConsumers, null, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(finalFoodTexture)), light, 0, 255, 255, 255, 255);
		}

		matrices.pop();
	}
}
