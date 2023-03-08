package com.parzivail.util.client.render;

import com.parzivail.util.ParziUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix4f;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class StatelessWaterRenderer implements SimpleResourceReloadListener<Void>
{
	public static final Identifier ID = ParziUtil.id("stateless_water_renderer");
	public static final StatelessWaterRenderer INSTANCE = new StatelessWaterRenderer();

	private final Sprite[] waterSprites = new Sprite[2];
	private Sprite waterOverlaySprite;

	private StatelessWaterRenderer()
	{
	}

	@Override
	public Identifier getFabricId()
	{
		return ID;
	}

	@Override
	public CompletableFuture<Void> reload(ResourceReloader.Synchronizer helper, ResourceManager manager, Profiler loadProfiler, Profiler applyProfiler, Executor loadExecutor, Executor applyExecutor)
	{
		return load(manager, loadProfiler, loadExecutor).thenCompose(helper::whenPrepared).thenCompose(
				(o) -> apply(o, manager, applyProfiler, applyExecutor)
		);
	}

	@Override
	public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor)
	{
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor)
	{
		this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getParticleSprite();
		this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
		this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();

		return CompletableFuture.completedFuture(null);
	}

	public boolean render(BlockRenderView world, int waterColor, VertexConsumer vertexConsumer, Matrix4f mat, boolean useOverlaySprite,
	                      boolean renderUp, boolean renderUpInner, boolean renderDown, boolean renderN, boolean renderS, boolean renderE, boolean renderW,
	                      float levelNE, float levelNW, float levelSE, float levelSW,
	                      Vec3d fluidVelocity, int lightmap, int lightmapDown)
	{
		if (!renderUp && !renderDown && !renderE && !renderW && !renderN && !renderS)
			return false;
		else
		{
			var sprites = this.waterSprites;

			var colorR = (float)(waterColor >> 16 & 0xFF) / 255.0F;
			var colorG = (float)(waterColor >> 8 & 0xFF) / 255.0F;
			var colorB = (float)(waterColor & 0xFF) / 255.0F;

			var renderedAnything = false;

			var lightDown = world.getBrightness(Direction.DOWN, true);
			var lightUp = world.getBrightness(Direction.UP, true);
			var lightNorth = world.getBrightness(Direction.NORTH, true);
			var lightWest = world.getBrightness(Direction.WEST, true);

			var bottomY = renderDown ? 0.001F : 0.0F;

			if (renderUp)
			{
				renderedAnything = true;

				levelNW -= 0.001F;
				levelSW -= 0.001F;
				levelSE -= 0.001F;
				levelNE -= 0.001F;

				float u0;
				float u1;
				float u2;
				float u3;
				float v0;
				float v1;
				float v2;
				float v3;

				if (fluidVelocity.x == 0.0 && fluidVelocity.z == 0.0)
				{
					var sprite = sprites[0];
					u0 = sprite.getFrameU(0.0);
					v0 = sprite.getFrameV(0.0);
					u1 = u0;
					v1 = sprite.getFrameV(16.0);
					u2 = sprite.getFrameU(16.0);
					v2 = v1;
					u3 = u2;
					v3 = v0;
				}
				else
				{
					var sprite = sprites[1];
					var fluidAngle = (float)MathHelper.atan2(fluidVelocity.z, fluidVelocity.x) - (float)(Math.PI / 2);
					var fluidDy = MathHelper.sin(fluidAngle) * 0.25F;
					var fluidDx = MathHelper.cos(fluidAngle) * 0.25F;
					var offset = 8.0F;
					u0 = sprite.getFrameU(offset + (-fluidDx - fluidDy) * 16.0F);
					v0 = sprite.getFrameV(offset + (-fluidDx + fluidDy) * 16.0F);
					u1 = sprite.getFrameU(offset + (-fluidDx + fluidDy) * 16.0F);
					v1 = sprite.getFrameV(offset + (fluidDx + fluidDy) * 16.0F);
					u2 = sprite.getFrameU(offset + (fluidDx + fluidDy) * 16.0F);
					v2 = sprite.getFrameV(offset + (fluidDx - fluidDy) * 16.0F);
					u3 = sprite.getFrameU(offset + (fluidDx - fluidDy) * 16.0F);
					v3 = sprite.getFrameV(offset + (-fluidDx - fluidDy) * 16.0F);
				}

				var avgU = (u0 + u1 + u2 + u3) / 4.0F;
				var avgV = (v0 + v1 + v2 + v3) / 4.0F;
				float coverageProportion = sprites[0].getAnimationFrameDelta();

				u0 = MathHelper.lerp(coverageProportion, u0, avgU);
				u1 = MathHelper.lerp(coverageProportion, u1, avgU);
				u2 = MathHelper.lerp(coverageProportion, u2, avgU);
				u3 = MathHelper.lerp(coverageProportion, u3, avgU);
				v0 = MathHelper.lerp(coverageProportion, v0, avgV);
				v1 = MathHelper.lerp(coverageProportion, v1, avgV);
				v2 = MathHelper.lerp(coverageProportion, v2, avgV);
				v3 = MathHelper.lerp(coverageProportion, v3, avgV);

				var r = lightUp * colorR;
				var g = lightUp * colorG;
				var b = lightUp * colorB;

				this.vertex(mat, vertexConsumer, 0.0, levelNW, 0.0, r, g, b, u0, v0, lightmap);
				this.vertex(mat, vertexConsumer, 0.0, levelSW, 1.0, r, g, b, u1, v1, lightmap);
				this.vertex(mat, vertexConsumer, 1.0, levelSE, 1.0, r, g, b, u2, v2, lightmap);
				this.vertex(mat, vertexConsumer, 1.0, levelNE, 0.0, r, g, b, u3, v3, lightmap);

				if (renderUpInner)
				{
					this.vertex(mat, vertexConsumer, 0.0, levelNW, 0.0, r, g, b, u0, v0, lightmap);
					this.vertex(mat, vertexConsumer, 1.0, levelNE, 0.0, r, g, b, u3, v3, lightmap);
					this.vertex(mat, vertexConsumer, 1.0, levelSE, 1.0, r, g, b, u2, v2, lightmap);
					this.vertex(mat, vertexConsumer, 0.0, levelSW, 1.0, r, g, b, u1, v1, lightmap);
				}
			}

			if (renderDown)
			{
				var minU = sprites[0].getMinU();
				var maxU = sprites[0].getMaxU();
				var minV = sprites[0].getMinV();
				var maxV = sprites[0].getMaxV();

				var r = lightDown * colorR;
				var g = lightDown * colorG;
				var b = lightDown * colorB;

				this.vertex(mat, vertexConsumer, 0, bottomY, 1.0, r, g, b, minU, maxV, lightmapDown);
				this.vertex(mat, vertexConsumer, 0, bottomY, 0, r, g, b, minU, minV, lightmapDown);
				this.vertex(mat, vertexConsumer, 1.0, bottomY, 0, r, g, b, maxU, minV, lightmapDown);
				this.vertex(mat, vertexConsumer, 1.0, bottomY, 1.0, r, g, b, maxU, maxV, lightmapDown);

				renderedAnything = true;
			}

			for (var direction : Direction.Type.HORIZONTAL)
			{
				float y1;
				float y2;
				double x1;
				double z1;
				double x2;
				double z2;
				boolean renderThisSide;

				switch (direction)
				{
					case NORTH ->
					{
						y1 = levelNW;
						y2 = levelNE;
						x1 = 0;
						x2 = 1.0;
						z1 = 0.001f;
						z2 = 0.001f;
						renderThisSide = renderN;
					}
					case SOUTH ->
					{
						y1 = levelSE;
						y2 = levelSW;
						x1 = 1.0;
						x2 = 0;
						z1 = 1.0 - 0.001F;
						z2 = 1.0 - 0.001F;
						renderThisSide = renderS;
					}
					case WEST ->
					{
						y1 = levelSW;
						y2 = levelNW;
						x1 = 0.001F;
						x2 = 0.001F;
						z1 = 1.0;
						z2 = 0;
						renderThisSide = renderW;
					}
					default ->
					{
						y1 = levelNE;
						y2 = levelSE;
						x1 = 1.0 - 0.001F;
						x2 = 1.0 - 0.001F;
						z1 = 0;
						z2 = 1.0;
						renderThisSide = renderE;
					}
				}

				if (renderThisSide)
				{
					renderedAnything = true;

					var sprite2 = useOverlaySprite ? this.waterOverlaySprite : sprites[1];

					var u1 = sprite2.getFrameU(0.0);
					var u2 = sprite2.getFrameU(8.0);
					var v1 = sprite2.getFrameV((1.0F - y1) * 16.0F * 0.5F);
					var v2 = sprite2.getFrameV((1.0F - y2) * 16.0F * 0.5F);
					var v3 = sprite2.getFrameV(8.0);

					var dirLighting = direction.getAxis() == Direction.Axis.Z ? lightNorth : lightWest;
					var r = lightUp * dirLighting * colorR;
					var g = lightUp * dirLighting * colorG;
					var b = lightUp * dirLighting * colorB;

					this.vertex(mat, vertexConsumer, x1, y1, z1, r, g, b, u1, v1, lightmap);
					this.vertex(mat, vertexConsumer, x2, y2, z2, r, g, b, u2, v2, lightmap);
					this.vertex(mat, vertexConsumer, x2, bottomY, z2, r, g, b, u2, v3, lightmap);
					this.vertex(mat, vertexConsumer, x1, bottomY, z1, r, g, b, u1, v3, lightmap);

					if (sprite2 != this.waterOverlaySprite)
					{
						this.vertex(mat, vertexConsumer, x1, bottomY, z1, r, g, b, u1, v3, lightmap);
						this.vertex(mat, vertexConsumer, x2, bottomY, z2, r, g, b, u2, v3, lightmap);
						this.vertex(mat, vertexConsumer, x2, y2, z2, r, g, b, u2, v2, lightmap);
						this.vertex(mat, vertexConsumer, x1, y1, z1, r, g, b, u1, v1, lightmap);
					}
				}
			}

			return renderedAnything;
		}
	}

	private void vertex(Matrix4f mat, VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light)
	{
		vertexConsumer.vertex(mat, (float)x, (float)y, (float)z).color(red, green, blue, 1.0F).texture(u, v).light(light).normal(0.0F, 1.0F, 0.0F).next();
	}
}
