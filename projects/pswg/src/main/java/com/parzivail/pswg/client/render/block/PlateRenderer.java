package com.parzivail.pswg.client.render.block;

import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.PlateBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
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
		matrices.translate(0.325f, 0.05f, 0.325f);
		matrices.scale(0.5f, 0.5f, 0.5f);
		for (int i = 0; i < foodList.size(); i += 1)
		{
			var registryKey = foodList.get(i).getItem().getRegistryEntry().getKey().get().getValue().getPath();
			var id = Resources.id("block/food/" + registryKey);
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(145), 0.35f, 0, 0.35f);

			var foodModel = P3dManager.INSTANCE.get(id);
			var foodTexture = new Identifier(id.getNamespace(), "textures/block/model/food/" + registryKey + ".png");
			if (foodModel == null)
			{
				foodModel = FALLBACK_MODEL;
				foodTexture = FALLBACK_TEXTURE;
			}

			Identifier finalFoodTexture = foodTexture;

			foodModel.render(matrices, vertexConsumers, entity, null, (v, tag, obj) -> v.getBuffer(RenderLayer.getEntityCutout(finalFoodTexture)), light, 0, 255, 255, 255, 255);
		}
		matrices.pop();
	}
}
