package com.parzivail.swg.render.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * Created by colby on 9/12/2017.
 */
public class RenderSkyTatooine extends IRenderHandler
{
	private static int glSkyList;
	private static int glSkyList2;
	private static int starGLCallList;

	private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
	private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");

	static
	{
		starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(starGLCallList, GL11.GL_COMPILE);
		renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		Tessellator tessellator = Tessellator.instance;
		glSkyList = starGLCallList + 1;
		GL11.glNewList(glSkyList, GL11.GL_COMPILE);
		byte b2 = 64;
		int i = 256 / b2 + 2;
		float f = 16.0F;
		int j;
		int k;

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				tessellator.startDrawingQuads();
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
				tessellator.draw();
			}
		}

		GL11.glEndList();
		glSkyList2 = starGLCallList + 2;
		GL11.glNewList(glSkyList2, GL11.GL_COMPILE);
		f = -16.0F;
		tessellator.startDrawingQuads();

		for (j = -b2 * i; j <= b2 * i; j += b2)
		{
			for (k = -b2 * i; k <= b2 * i; k += b2)
			{
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + 0));
				tessellator.addVertex((double)(j + 0), (double)f, (double)(k + b2));
				tessellator.addVertex((double)(j + b2), (double)f, (double)(k + b2));
			}
		}

		tessellator.draw();
		GL11.glEndList();
	}

	private static void renderStars()
	{
		Random random = new Random(10842L);
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_POINTS);

		float[][] colorBank = new float[256][3];
		for (int i = 0; i < 256; i++)
			colorBank[i] = new float[] {
					i / 255f * 214 / 255f, i / 255f * 223 / 255f, i / 255f * 255 / 255f
			};

		for (int i = 0; i < 15000; ++i)
		{
			double randX = (double)(random.nextFloat() * 2.0F - 1.0F);
			double randY = (double)(random.nextFloat() * 2.0F - 1.0F);
			double randZ = (double)(random.nextFloat() * 2.0F - 1.0F);
			int randColorIdx = (int)(random.nextFloat() * 255);
			double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
			double d4 = randX * randX + randY * randY + randZ * randZ;

			if (d4 < 1.0D && d4 > 0.01D)
			{
				d4 = 1.0D / Math.sqrt(d4);
				randX *= d4;
				randY *= d4;
				randZ *= d4;
				double d5 = randX * 100.0D;
				double d6 = randY * 100.0D;
				double d7 = randZ * 100.0D;
				double d8 = Math.atan2(randX, randZ);
				double d9 = Math.sin(d8);
				double d10 = Math.cos(d8);
				double d11 = Math.atan2(Math.sqrt(randX * randX + randZ * randZ), randY);
				double d12 = Math.sin(d11);
				double d13 = Math.cos(d11);
				double d14 = random.nextDouble() * Math.PI * 2.0D;
				double d15 = Math.sin(d14);
				double d16 = Math.cos(d14);

				GL11.glColor4f(colorBank[randColorIdx][0], colorBank[randColorIdx][1], colorBank[randColorIdx][2], 1);
				GL11.glVertex3d(d5, d6, d7);

				//				for (int j = 0; j < 4; ++j)
				//				{
				//					double d17 = 0.0D;
				//					double d18 = (double)((j & 2) - 1) * d3;
				//					double d19 = (double)((j + 1 & 2) - 1) * d3;
				//					double d20 = d18 * d16 - d19 * d15;
				//					double d21 = d19 * d16 + d18 * d15;
				//					double d22 = d20 * d12 + d17 * d13;
				//					double d23 = d17 * d12 - d20 * d13;
				//					double d24 = d23 * d9 - d21 * d10;
				//					double d25 = d21 * d9 + d23 * d10;
				//					tessellator.addVertex(d5 + d24, d6 + d22, d7 + d25);
				//				}
			}
		}

		GL11.glEnd();
		GL11.glPopMatrix();
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Vec3 vec3 = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float skyR = (float)vec3.xCoord;
		float skyG = (float)vec3.yCoord;
		float skyB = (float)vec3.zCoord;
		float sunsetR;

		if (mc.gameSettings.anaglyph)
		{
			float f4 = (skyR * 30.0F + skyG * 59.0F + skyB * 11.0F) / 100.0F;
			float f5 = (skyR * 30.0F + skyG * 70.0F) / 100.0F;
			sunsetR = (skyR * 30.0F + skyB * 70.0F) / 100.0F;
			skyR = f4;
			skyG = f5;
			skyB = sunsetR;
		}

		GL11.glColor3f(skyR, skyG, skyB);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glColor3f(skyR, skyG, skyB);
		GL11.glCallList(glSkyList);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		RenderHelper.disableStandardItemLighting();
		float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
		float sunsetG;
		float sunsetB;
		float tempA;
		float tempB;

		if (afloat != null)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glPushMatrix();
			GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
			sunsetR = afloat[0];
			sunsetG = afloat[1];
			sunsetB = afloat[2];
			float f11;

			if (mc.gameSettings.anaglyph)
			{
				tempA = (sunsetR * 30.0F + sunsetG * 59.0F + sunsetB * 11.0F) / 100.0F;
				tempB = (sunsetR * 30.0F + sunsetG * 70.0F) / 100.0F;
				f11 = (sunsetR * 30.0F + sunsetB * 70.0F) / 100.0F;
				sunsetR = tempA;
				sunsetG = tempB;
				sunsetB = f11;
			}

			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor4f(sunsetR, sunsetG, sunsetB, afloat[3]);
			GL11.glVertex3d(0.0D, 100.0D, 0.0D);
			byte b0 = 16;
			GL11.glColor4f(afloat[0], afloat[1], afloat[2], 0.0F);

			for (int j = 0; j <= b0; ++j)
			{
				f11 = (float)j * (float)Math.PI * 2.0F / (float)b0;
				float f12 = MathHelper.sin(f11);
				float f13 = MathHelper.cos(f11);
				GL11.glVertex3d((double)(f12 * 120.0F), (double)(f13 * 120.0F), (double)(-f13 * 40.0F * afloat[3]));
			}

			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glShadeModel(GL11.GL_FLAT);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.glBlendFunc(770, 1, 1, 0);
		GL11.glPushMatrix();
		sunsetR = 1.0F - world.getRainStrength(partialTicks);
		sunsetG = 0.0F;
		sunsetB = 0.0F;
		tempA = 0.0F;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, sunsetR);
		GL11.glTranslatef(sunsetG, sunsetB, tempA);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
		tempB = 30.0F;
		mc.renderEngine.bindTexture(locationSunPng);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0.0D, 0.0D);
		GL11.glVertex3d((double)(-tempB), 100.0D, (double)(-tempB));
		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex3d((double)tempB, 100.0D, (double)(-tempB));
		GL11.glTexCoord2d(1.0D, 1.0D);
		GL11.glVertex3d((double)tempB, 100.0D, (double)tempB);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex3d((double)(-tempB), 100.0D, (double)tempB);
		GL11.glEnd();
		tempB = 20.0F;
		mc.renderEngine.bindTexture(locationMoonPhasesPng);
		int moonPhase = world.getMoonPhase();
		int moonPhaseIdx = moonPhase % 4;
		int moonHalfPhaseIdx = moonPhase / 4 % 2;
		float leftU = (float)(moonPhaseIdx + 0) / 4.0F;
		float leftV = (float)(moonHalfPhaseIdx + 0) / 2.0F;
		float rightU = (float)(moonPhaseIdx + 1) / 4.0F;
		float rightV = (float)(moonHalfPhaseIdx + 1) / 2.0F;
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d((double)rightU, (double)rightV);
		GL11.glVertex3d((double)(-tempB), -100.0D, (double)tempB);
		GL11.glTexCoord2d((double)leftU, (double)rightV);
		GL11.glVertex3d((double)tempB, -100.0D, (double)tempB);
		GL11.glTexCoord2d((double)leftU, (double)leftV);
		GL11.glVertex3d((double)tempB, -100.0D, (double)(-tempB));
		GL11.glTexCoord2d((double)rightU, (double)leftV);
		GL11.glVertex3d((double)(-tempB), -100.0D, (double)(-tempB));
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float starBrightness = world.getStarBrightness(partialTicks) * sunsetR;

		if (starBrightness > 0.3F)
		{
			GL11.glPointSize(1);
			GL11.glEnable(GL11.GL_POINT_SMOOTH);
			GL11.glCallList(starGLCallList);
			//renderStars(starBrightness); // SLOW! find way to mult alpha from list.
			//                                Done. just upp'd when it stops rendering. good enough.
			GL11.glDisable(GL11.GL_POINT_SMOOTH);
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(0.0F, 0.0F, 0.0F);
		double playerYAboveHorizon = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();

		if (playerYAboveHorizon < 0.0D)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 12.0F, 0.0F);
			GL11.glCallList(glSkyList2);
			GL11.glPopMatrix();
			sunsetB = 1.0F;
			tempA = -((float)(playerYAboveHorizon + 65.0D));
			tempB = -sunsetB;
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glColor4f(0, 0, 0, 1);
			GL11.glVertex3d((double)(-sunsetB), (double)tempA, (double)sunsetB);
			GL11.glVertex3d((double)sunsetB, (double)tempA, (double)sunsetB);
			GL11.glVertex3d((double)sunsetB, (double)tempB, (double)sunsetB);
			GL11.glVertex3d((double)(-sunsetB), (double)tempB, (double)sunsetB);
			GL11.glVertex3d((double)(-sunsetB), (double)tempB, (double)(-sunsetB));
			GL11.glVertex3d((double)sunsetB, (double)tempB, (double)(-sunsetB));
			GL11.glVertex3d((double)sunsetB, (double)tempA, (double)(-sunsetB));
			GL11.glVertex3d((double)(-sunsetB), (double)tempA, (double)(-sunsetB));
			GL11.glVertex3d((double)sunsetB, (double)tempB, (double)(-sunsetB));
			GL11.glVertex3d((double)sunsetB, (double)tempB, (double)sunsetB);
			GL11.glVertex3d((double)sunsetB, (double)tempA, (double)sunsetB);
			GL11.glVertex3d((double)sunsetB, (double)tempA, (double)(-sunsetB));
			GL11.glVertex3d((double)(-sunsetB), (double)tempA, (double)(-sunsetB));
			GL11.glVertex3d((double)(-sunsetB), (double)tempA, (double)sunsetB);
			GL11.glVertex3d((double)(-sunsetB), (double)tempB, (double)sunsetB);
			GL11.glVertex3d((double)(-sunsetB), (double)tempB, (double)(-sunsetB));
			GL11.glVertex3d((double)(-sunsetB), (double)tempB, (double)(-sunsetB));
			GL11.glVertex3d((double)(-sunsetB), (double)tempB, (double)sunsetB);
			GL11.glVertex3d((double)sunsetB, (double)tempB, (double)sunsetB);
			GL11.glVertex3d((double)sunsetB, (double)tempB, (double)(-sunsetB));
			GL11.glEnd();
		}

		if (world.provider.isSkyColored())
		{
			GL11.glColor3f(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F);
		}
		else
		{
			GL11.glColor3f(skyR, skyG, skyB);
		}

		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, -((float)(playerYAboveHorizon - 16.0D)), 0.0F);
		GL11.glCallList(glSkyList2);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
	}
}
