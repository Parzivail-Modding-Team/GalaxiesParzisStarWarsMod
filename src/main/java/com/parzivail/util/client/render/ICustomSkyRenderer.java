package com.parzivail.util.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Environment(EnvType.CLIENT)
public interface ICustomSkyRenderer
{
	/**
	 * Do not use this directly.
	 */
	HashMap<Identifier, ICustomSkyRenderer> CUSTOM_SKY_RENDERERS = new HashMap<>();

	static void register(Identifier worldKey, ICustomSkyRenderer renderer)
	{
		CUSTOM_SKY_RENDERERS.put(worldKey, renderer);
	}

	void render(MinecraftClient client, VertexBuffer lightSkyBuffer, VertexBuffer darkSkyBuffer, VertexBuffer starsBuffer, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Runnable fogApplier, CallbackInfo ci);
}
