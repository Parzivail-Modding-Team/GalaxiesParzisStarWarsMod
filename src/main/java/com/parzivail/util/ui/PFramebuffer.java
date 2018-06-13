package com.parzivail.util.ui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class PFramebuffer extends Framebuffer
{
	public PFramebuffer(int width, int height, boolean useDepth)
	{
		super(width, height, useDepth);
	}

	@Override
	public void createFramebuffer(int p_147605_1_, int p_147605_2_)
	{
		this.framebufferWidth = p_147605_1_;
		this.framebufferHeight = p_147605_2_;
		this.framebufferTextureWidth = p_147605_1_;
		this.framebufferTextureHeight = p_147605_2_;

		if (!OpenGlHelper.isFramebufferEnabled())
		{
			this.framebufferClear();
		}
		else
		{
			this.framebufferObject = OpenGlHelper.func_153165_e();
			this.framebufferTexture = TextureUtil.glGenTextures();

			if (this.useDepth)
			{
				this.depthBuffer = OpenGlHelper.func_153185_f();
			}

			this.setFramebufferFilter(GL11.GL_LINEAR);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexture);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
			OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.framebufferObject);
			OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, this.framebufferTexture, 0);

			if (this.useDepth)
			{
				OpenGlHelper.func_153176_h(OpenGlHelper.field_153199_f, this.depthBuffer);
				if (net.minecraftforge.client.MinecraftForgeClient.getStencilBits() == 0)
				{
					OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
					OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, OpenGlHelper.field_153201_h, OpenGlHelper.field_153199_f, this.depthBuffer);
				}
				else
				{
					OpenGlHelper.func_153186_a(OpenGlHelper.field_153199_f, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.framebufferTextureWidth, this.framebufferTextureHeight);
					OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
					OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
				}
			}

			this.framebufferClear();
			this.unbindFramebufferTexture();
		}
	}

	//	@Override
	//	public void bindFramebuffer(boolean p_147610_1_)
	//	{
	//		super.bindFramebuffer(p_147610_1_);
	//
	//		if (!(Client.mc.loadingScreen instanceof PLoadingScreenRenderer))
	//			Client.mc.loadingScreen = new PLoadingScreenRenderer(Client.mc);
	//	}

	@Override
	public void framebufferRender(int width, int height)
	{
		if (OpenGlHelper.isFramebufferEnabled())
		{
			GL11.glColorMask(true, true, true, false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, (double)width, (double)height, 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
			GL11.glViewport(0, 0, width, height);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			this.bindFramebufferTexture();
			//ShaderHelper.useShader(ShaderHelper.blur);
			float f = (float)width;
			float f1 = (float)height;
			float f2 = (float)this.framebufferWidth / (float)this.framebufferTextureWidth;
			float f3 = (float)this.framebufferHeight / (float)this.framebufferTextureHeight;
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(-1);
			tessellator.addVertexWithUV(0.0D, (double)f1, 0.0D, 0.0D, 0.0D);
			tessellator.addVertexWithUV((double)f, (double)f1, 0.0D, (double)f2, 0.0D);
			tessellator.addVertexWithUV((double)f, 0.0D, 0.0D, (double)f2, (double)f3);
			tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double)f3);
			tessellator.draw();
			this.unbindFramebufferTexture();
			//ShaderHelper.releaseShader();
			GL11.glDepthMask(true);
			GL11.glColorMask(true, true, true, true);
		}
	}
}
