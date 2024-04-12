package com.parzivail.util.client.model;

import com.parzivail.pswg.block.InteractableConnectingInvertedLampBlock;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.EnumSet;
import java.util.function.Supplier;

public class ConnectedLampModel extends ConnectedTextureModel
{
	Sprite litBorderSprite;
	private static final Vector3f[] ORIGINS = new Vector3f[6];
	private static final Vector3f[] DELTAU = new Vector3f[6];
	private static final Vector3f[] DELTAV = new Vector3f[6];

	static
	{
		ORIGINS[Direction.UP.getId()] = new Vector3f(0, 1, 0);
		ORIGINS[Direction.DOWN.getId()] = new Vector3f(1, 0, 0);
		ORIGINS[Direction.WEST.getId()] = new Vector3f(0, 1, 0);
		ORIGINS[Direction.EAST.getId()] = new Vector3f(1, 1, 1);
		ORIGINS[Direction.NORTH.getId()] = new Vector3f(1, 1, 0);
		ORIGINS[Direction.SOUTH.getId()] = new Vector3f(0, 1, 1);

		DELTAU[Direction.UP.getId()] = MathUtil.V3F_POS_X;
		DELTAV[Direction.UP.getId()] = MathUtil.V3F_POS_Z;

		DELTAU[Direction.DOWN.getId()] = MathUtil.V3F_NEG_X;
		DELTAV[Direction.DOWN.getId()] = MathUtil.V3F_POS_Z;

		DELTAU[Direction.SOUTH.getId()] = MathUtil.V3F_POS_X;
		DELTAV[Direction.SOUTH.getId()] = MathUtil.V3F_NEG_Y;

		DELTAU[Direction.NORTH.getId()] = MathUtil.V3F_NEG_X;
		DELTAV[Direction.NORTH.getId()] = MathUtil.V3F_NEG_Y;

		DELTAU[Direction.WEST.getId()] = MathUtil.V3F_POS_Z;
		DELTAV[Direction.WEST.getId()] = MathUtil.V3F_NEG_Y;

		DELTAU[Direction.EAST.getId()] = MathUtil.V3F_NEG_Z;
		DELTAV[Direction.EAST.getId()] = MathUtil.V3F_NEG_Y;
	}

	private final boolean hConnect;
	private final boolean vConnect;
	private final boolean lConnect;
	private final EnumSet<Direction> capDirections;
	private final Sprite borderSprite;
	private final Sprite capSprite;

	public ConnectedLampModel(boolean hConnect, boolean vConnect, boolean lConnect, EnumSet<Direction> capDirections, Sprite blankSprite, Sprite borderSprite, Sprite capSprite, Sprite litBorderSprite)
	{
		super(hConnect, vConnect, lConnect, capDirections, blankSprite, borderSprite, capSprite);
		this.litBorderSprite = litBorderSprite;
		this.hConnect = hConnect;
		this.vConnect = vConnect;
		this.lConnect = lConnect;
		this.capDirections = capDirections;
		this.borderSprite = borderSprite;
		this.capSprite = capSprite;
	}

	@Override
	protected Mesh createBlockMesh(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context, Matrix4f transformation)
	{
		var meshBuilder = createMeshBuilder();
		var quadEmitter = meshBuilder.getEmitter();

		for (var i = 0; i < ModelHelper.NULL_FACE_ID; i++)
		{
			if (state != null && !(state.getBlock() instanceof ConnectingBlock))
				continue;

			final var faceDirection = ModelHelper.faceFromIndex(i);
			if (faceDirection != null)
			{
				final var facingProp = ConnectingBlock.FACING_PROPERTIES.get(faceDirection);

				if (state != null && state.get(facingProp))
				{
					quadEmitter.emit();
					continue;
				}
			}

			var sprite = modelSprite;

			if (capSprite != null && capDirections.contains(faceDirection))
				sprite = capSprite;

			Vector3f min = ORIGINS[faceDirection.getId()];
			Vector3f dU = DELTAU[faceDirection.getId()];
			Vector3f dV = DELTAV[faceDirection.getId()];

			if (MinecraftClient.getInstance().player != null)
			{
				MinecraftClient.getInstance().player.sendMessage(Text.of("No"));
			}
			emitTopQuad(quadEmitter, sprite, borderSprite, blockView, state, pos, faceDirection, min, dU, dV);
			if (state != null)
			{
				if (state.getBlock() instanceof InteractableConnectingInvertedLampBlock && state.get(InteractableConnectingInvertedLampBlock.LIT))
				{
					emitTopQuad(quadEmitter, sprite, litBorderSprite, blockView, state, pos, faceDirection, min, dU, dV);
					if (MinecraftClient.getInstance().player != null)
					{
						MinecraftClient.getInstance().player.sendMessage(Text.of("Something!"));
					}
				}
			}
		}

		return meshBuilder.build();
	}
}
