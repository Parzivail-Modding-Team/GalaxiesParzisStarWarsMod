package com.parzivail.pswg.client.render.block;

import com.fusionflux.gravity_api.util.RotationUtil;
import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.PlateBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgTags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class PlateRenderer implements BlockEntityRenderer<PlateBlockEntity>
{
	private static final Identifier MODEL = Resources.id("block/food/plate");
	private static final P3dModel FALLBACK_MODEL = P3dManager.INSTANCE.get(Resources.id("block/food/haroun_bread"));
	private static final Identifier FALLBACK_TEXTURE = Resources.id("textures/block/model/food/haroun_bread.png");
	private static final Identifier TEXTURE = Resources.id("textures/block/model/food/plate.png");

	public PlateRenderer(BlockEntityRendererFactory.Context ctx)
	{
	}


	@Override
	public void render(PlateBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		List<ItemStack> foodList = entity.FOODS;
		var world = entity.getWorld();
		if (world == null)
			return;

		var state = world.getBlockState(entity.getPos());
		if (!state.isOf(SwgBlocks.Misc.Plate))
			return;
		matrices.push();
		if (!foodList.isEmpty())
		{
			if (foodList.get(0).isIn(SwgTags.Items.MAIN_COURSE) || foodList.size() <= 1)
			{
				matrices.translate(0.5f, 1f / 16f, 0.5f);
			}
			else
			{
				matrices.translate(0.3f, 1f / 16f, 0.3f);
			}
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
			if (foodModel == null)
			{
				foodModel = FALLBACK_MODEL;
				foodTexture = FALLBACK_TEXTURE;
			}


			Identifier finalFoodTexture = foodTexture;
			if (foodList.get(0).isIn(SwgTags.Items.MAIN_COURSE))
			{
				if (i == 1)
				{
					matrices.translate(0, 0, 0.2);
				}
				if (i > 0)
				{
					var tY = lastModel.bounds().getMax(Direction.Axis.Y) - 0.f;
					//var tY = lastModel.bounds().getLengthY();
					if (foodList.get(i - 1).isIn(SwgTags.Items.MAIN_COURSE))
						matrices.translate(0, tY, 0);
					else
					{
						//matrices.translate(0.15, 0, 0.15);
						//matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(145), 0f, 0, 0f);
						//matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90), 0f, 0, 0f);
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

			foodModel.render(matrices, vertexConsumers, entity, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(finalFoodTexture)), light, 0, 255, 255, 255, 255);
		}

		matrices.pop();
	}
}
