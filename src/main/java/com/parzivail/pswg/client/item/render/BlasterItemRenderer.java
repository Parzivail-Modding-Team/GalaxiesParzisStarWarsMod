package com.parzivail.pswg.client.item.render;

import com.parzivail.pswg.client.pm3d.PM3DFile;
import com.parzivail.pswg.client.pm3d.PM3DLod;
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
	private final Lazy<PM3DFile> pm3dModel;
	private final Identifier texture;
	private final Identifier inventoryTexture;

	public BlasterItemRenderer(Lazy<PM3DFile> model, Identifier texture, Identifier inventoryTexture)
	{
		this.pm3dModel = model;
		this.texture = texture;
		this.inventoryTexture = inventoryTexture;
	}

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
			matrices.translate(-0.5f, -1.5f, 0);
			matrices.multiply(new Quaternion(90, 0, 0, true));
			matrices.multiply(new Quaternion(0, 0, -90, true));
			matrices.multiply(new Quaternion(-135, 0, 0, true));

			PM3DLod m = pm3dModel.get().getLevelOfDetail(0);

			float f = 1.7f / (float)m.bounds.getYLength();
			matrices.scale(f, f, f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(inventoryTexture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			m.render(VertexConsumerBuffer.Instance);
		}
		else if (renderMode.isFirstPerson())
		{
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -0.5f, -0.25f);
			matrices.scale(0.9f, 0.9f, 0.9f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			pm3dModel.get().getLevelOfDetail(1).render(VertexConsumerBuffer.Instance);
		}
		else
		{
			matrices.multiply(new Quaternion(0, 180, 0, true));
			matrices.translate(-0.4f, -1, -0.5f);
			matrices.scale(0.9f, 0.9f, 0.9f);

			VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(texture));
			VertexConsumerBuffer.Instance.init(vc, matrices.peek(), 1, 1, 1, 1, overlay, light);
			pm3dModel.get().getLevelOfDetail(1).render(VertexConsumerBuffer.Instance);
		}

		matrices.pop();
	}
}
