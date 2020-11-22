package com.parzivail.pswg.client.item.render;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.util.client.VertexConsumerBuffer;
import com.parzivail.util.item.CustomItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.Quaternion;

public class BlasterItemRenderer implements CustomItemRenderer
{
	private static final Lazy<PM3DFile> a280c = new Lazy<>(() -> PM3DFile.tryLoad(Resources.identifier("models/blaster/a280c.pm3d")));
	private static final Identifier a280c_texture = Resources.identifier("textures/blaster/a280c.png");
	private static final Identifier a280c_inventory_texture = Resources.identifier("textures/blaster/a280c_inventory.png");

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
	{
		matrices.push();

		model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);

		if (renderMode == ModelTransformation.Mode.FIXED)
		{
			matrices.translate(-0.18f, 0, 0);
			matrices.scale(1.8f, 1.8f, 1.8f);
			matrices.multiply(new Quaternion(0, 0, 45, true));
			matrices.translate(0, 0.18f, 0);
		}
		else if (renderMode != ModelTransformation.Mode.GUI)
			matrices.translate(0, 0.18f, 0);

		matrices.scale(0.2f, 0.2f, 0.2f);

		if (renderMode == ModelTransformation.Mode.GUI)
		{
			matrices.translate(-0.5f, -2.2f, 0);
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, -90, true));
			matrices.multiply(new Quaternion(-135, 0, 0, true));
			matrices.scale(0.7f, 0.7f, 0.7f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(a280c_inventory_texture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			a280c.get().getLevelOfDetail(0).render(VertexConsumerBuffer.Instance);
		}
		else
		{
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.5f, -1, -0.5f);
			matrices.scale(0.9f, 0.9f, 0.9f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(a280c_texture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			a280c.get().getLevelOfDetail(1).render(VertexConsumerBuffer.Instance);
		}

		matrices.pop();
	}
}
