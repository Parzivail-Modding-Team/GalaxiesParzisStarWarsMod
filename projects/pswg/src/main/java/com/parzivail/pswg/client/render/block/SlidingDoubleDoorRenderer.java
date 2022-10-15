package com.parzivail.pswg.client.render.block;

import com.parzivail.pswg.block.Sliding1x2DoorBlock;
import com.parzivail.pswg.client.render.p3d.IP3DBlockRenderer;
import com.parzivail.pswg.client.render.p3d.P3DBlockRenderTarget;
import com.parzivail.pswg.client.render.p3d.P3dModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.function.Supplier;

public class SlidingDoubleDoorRenderer implements IP3DBlockRenderer
{
	private static Matrix4f transform(P3DBlockRenderTarget target, String objectName, float tickDelta)
	{
		var m = new Matrix4f();
		m.loadIdentity();

		if (!(target instanceof P3DBlockRenderTarget.Block blockRenderTarget))
		{
			if (objectName.equals("door"))
				// Only draw the frame in items
				return null;
			return m;
		}

		if (objectName.equals("door"))
		{
			var state = blockRenderTarget.getState();
			if (Sliding1x2DoorBlock.getDoorColor(state).isEmpty())
				return null;

			if (state.get(Sliding1x2DoorBlock.OPEN))
				m.multiplyByTranslation(1.352f, 0, 0);
		}

		return m;
	}

	@Override
	public void renderBlock(MatrixStack matrices, QuadEmitter quadEmitter, P3DBlockRenderTarget target, Supplier<Random> randomSupplier, RenderContext renderContext, P3dModel model, Sprite baseSprite, HashMap<String, Sprite> additionalSprites)
	{
		if (target instanceof P3DBlockRenderTarget.Block blockRenderTarget && blockRenderTarget.getState().get(Sliding1x2DoorBlock.HALF) != DoubleBlockHalf.LOWER)
			return;

		var doorColor = DyeColor.BROWN;

		if (target instanceof P3DBlockRenderTarget.Block blockRenderTarget)
			doorColor = Sliding1x2DoorBlock.getDoorColor(blockRenderTarget.getState()).orElse(DyeColor.BROWN);

		DyeColor finalDoorColor = doorColor;
		model.renderBlock(
				matrices,
				quadEmitter,
				target,
				SlidingDoubleDoorRenderer::transform,
				(target1, objectName) -> objectName.equals("frame") ? baseSprite : additionalSprites.get("door_" + finalDoorColor.getName()),
				randomSupplier,
				renderContext
		);
	}
}
