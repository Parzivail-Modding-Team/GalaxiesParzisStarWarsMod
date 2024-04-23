package com.parzivail.pswg.client.render.block;

import com.parzivail.p3d.IP3dBlockRenderer;
import com.parzivail.p3d.P3dBlockRenderTarget;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.PlateBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class PlateRenderer implements IP3dBlockRenderer, BlockEntityRenderer<PlateBlockEntity>
{
	private static final Identifier MODEL = Resources.id("block/food/plate");

	public PlateRenderer()
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
	}

	@Override
	public void renderBlock(MatrixStack matrices, QuadEmitter quadEmitter, P3dBlockRenderTarget target, Supplier<Random> randomSupplier, RenderContext renderContext, P3dModel model, Sprite baseSprite, HashMap<String, Sprite> additionalSprites)
	{

	}
}
