package com.parzivail.pswg.client.render.block;

import com.parzivail.p3d.P3dBlockRenderTarget;
import com.parzivail.p3d.P3dManager;
import com.parzivail.p3d.P3dModel;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.blockentity.PlateBlockEntity;
import com.parzivail.pswg.container.SwgBlocks;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
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
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class PlateRenderer implements BlockEntityRenderer<PlateBlockEntity>
{
	private static final Identifier MODEL = Resources.id("block/food/plate");
	private static final Identifier TEXTURE = Resources.id("textures/block/model/food/plate");

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

		var vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
		for (int i = 0; i < foodList.size(); i += 1)
		{
			var id = Resources.id("blocks/food/" + foodList.get(i).getItem().getRegistryEntry().getKey().get().getValue().getPath());
			//MinecraftClient.getInstance().player.sendMessage(Text.of("blocks/food/"+foodList.get(i).getItem().getRegistryEntry().getKey().get().getValue().getPath()));
			matrices.translate((float)(i + 1) / 10f, (float)(i + 1) / 10f, (float)(i + 1) / 10f);
			P3dManager.INSTANCE.get(id).render(matrices, vertexConsumer, entity, null, light, tickDelta, 255, 255, 255, 255);
		}
		matrices.pop();
	}
}
