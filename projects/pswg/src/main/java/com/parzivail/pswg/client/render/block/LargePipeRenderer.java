package com.parzivail.pswg.client.render.block;

import com.parzivail.p3d.IP3dBlockRenderer;
import com.parzivail.p3d.P3dBlockRenderTarget;
import com.parzivail.p3d.P3dModel;
import com.parzivail.util.block.connecting.ConnectingNodeBlock;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LargePipeRenderer implements IP3dBlockRenderer
{
	private static final Map<String, Direction> FACING_SUBMODELS = Util.make(new HashMap<>(), m -> {
		m.put("NODE_PY", Direction.EAST);
		m.put("NODE_NY", Direction.WEST);
		m.put("NODE_PX", Direction.SOUTH);
		m.put("NODE_NX", Direction.NORTH);
		m.put("NODE_PZ", Direction.UP);
		m.put("NODE_NZ", Direction.DOWN);
	});

	private static Matrix4f transformBlockState(P3dBlockRenderTarget target, String objectName, float tickDelta)
	{
		var m = new Matrix4f();

		if (!(target instanceof P3dBlockRenderTarget.Block blockRenderTarget))
		{
			// This item uses a sprite
			return null;
		}

		if (objectName.equals("NODE_CENTER"))
			return m;

		if (!blockRenderTarget.getState().get(ConnectingNodeBlock.FACING_PROPERTIES.get(FACING_SUBMODELS.get(objectName))))
			return null;

		return m;
	}

	@Override
	public void renderBlock(MatrixStack matrices, QuadEmitter quadEmitter, P3dBlockRenderTarget target, Supplier<Random> randomSupplier, RenderContext renderContext, P3dModel model, Sprite baseSprite, HashMap<String, Sprite> additionalSprites)
	{
		model.renderBlock(
				matrices,
				quadEmitter,
				target,
				LargePipeRenderer::transformBlockState,
				(target1, objectName) -> baseSprite,
				randomSupplier,
				renderContext
		);
	}
}
