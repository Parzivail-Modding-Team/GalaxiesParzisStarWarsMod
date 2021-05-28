package com.parzivail.pswg.mixin;

//import com.mojang.blaze3d.platform.FramebufferInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * author: c64cosmin
 * Used with implicit permission
 */
@Mixin(Framebuffer.class)
@Environment(EnvType.CLIENT)
public class FramebufferMixin
{
	@ModifyArg(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", ordinal = 1), index = 1)
	private int initFboStencil(int attachment)
	{
		return GL30.GL_DEPTH_STENCIL_ATTACHMENT;
	}

	@ModifyArg(method = "initFbo", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0), index = 2)
	private int initFboTexImage2D(int internalFormat)
	{
		return GL30.GL_DEPTH24_STENCIL8;
	}
}

