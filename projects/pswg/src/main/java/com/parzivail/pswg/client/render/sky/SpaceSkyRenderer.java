package com.parzivail.pswg.client.render.sky;

import com.parzivail.pswg.container.SwgDimensions;
import com.parzivail.pswg.mixin.DimensionEffectsAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SpaceSkyRenderer
{
	private static final int SPACE_START = -50;
	private static final int SPACE_TRANSITION = 200;
	private static final int SPACE_END = SPACE_TRANSITION - SPACE_START;

	private static Vec3d mixColorIntoSpace(Vec3d color, Vec3d cameraPos, int dimTopY)
	{
		return color.multiply(getSpaceMixConstant(cameraPos, dimTopY));
	}

	private static double getSpaceMixConstant(Vec3d cameraPos, int dimTopY)
	{
		return 1 - Math.pow(MathHelper.clamp(cameraPos.y - dimTopY + SPACE_START, 0, SPACE_END) / SPACE_END, 2.2);
	}

	public static void clientWorld$getSkyColor(ClientWorld self, Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir)
	{
		var id = self.getDimensionEntry();
		if (id.matchesKey(SwgDimensions.TATOOINE))
		{
			var dimTopY = self.getBottomY() + self.getHeight();
			if (cameraPos.y > dimTopY + SPACE_START)
				cir.setReturnValue(mixColorIntoSpace(cir.getReturnValue(), cameraPos, dimTopY));
		}
	}

	public static void clientWorld$getStarColor(ClientWorld self, float tickDelta, CallbackInfoReturnable<Float> cir)
	{
		var mc = MinecraftClient.getInstance();
		var cameraPos = mc.gameRenderer.getCamera().getPos();
		var id = self.getDimensionEntry();

		if (id.matchesKey(SwgDimensions.TATOOINE))
		{
			var rainAttenuation = 1.0F - self.getRainGradient(tickDelta);
			if (rainAttenuation == 0)
				return;

			var dimTopY = self.getBottomY() + self.getHeight();
			if (cameraPos.y > dimTopY + SPACE_START)
				cir.setReturnValue((float)(1 - getSpaceMixConstant(cameraPos, dimTopY)) / (1.5f * rainAttenuation));
		}
	}

	public static class Effect extends DimensionEffects
	{
		private final DimensionEffects overworldEffect;

		public Effect()
		{
			super(Float.NaN, true, DimensionEffects.SkyType.NORMAL, false, false);
			overworldEffect = DimensionEffectsAccessor.get_BY_IDENTIFIER().get(DimensionTypes.OVERWORLD.getValue());
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight)
		{
			var mc = MinecraftClient.getInstance();
			var cameraPos = mc.gameRenderer.getCamera().getPos();
			var self = mc.world;

			var dimTopY = self.getBottomY() + self.getHeight();
			if (cameraPos.y > dimTopY + SPACE_START)
				return mixColorIntoSpace(color, cameraPos, dimTopY);

			return overworldEffect.adjustFogColor(color, sunHeight);
		}

		@Override
		public boolean useThickFog(int camX, int camY)
		{
			return false;
		}
	}
}
