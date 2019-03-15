package com.parzivail.swg.render.npc;

import com.parzivail.swg.render.npc.model.PModelBiped;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

public abstract class RenderBiped<T extends EntityLiving> extends RendererLivingEntity
{
	public PModelBiped modelBipedMain;
	public PModelBiped modelArmorChestplate;
	public PModelBiped modelArmor;

	public RenderBiped()
	{
		super(new PModelBiped(0.0F, false), 0.5F);
		modelBipedMain = (PModelBiped)mainModel;
		modelArmorChestplate = new PModelBiped(1.0F, false);
		modelArmor = new PModelBiped(0.5F, false);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		ItemStack itemstack = p_76986_1_.getHeldItem();
		modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = itemstack != null ? 1 : 0;

		modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = p_76986_1_.isSneaking();
		double d3 = p_76986_4_ - (double)p_76986_1_.yOffset;

		if (p_76986_1_.isSneaking())
		{
			d3 -= 0.125D;
		}

		super.doRender(p_76986_1_, p_76986_2_, d3, p_76986_6_, p_76986_8_, p_76986_9_);
		modelArmorChestplate.aimedBow = modelArmor.aimedBow = modelBipedMain.aimedBow = false;
		modelArmorChestplate.isSneak = modelArmor.isSneak = modelBipedMain.isSneak = false;
		modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = modelBipedMain.heldItemRight = 0;
	}

	/**
	 * Test if the entity name must be rendered
	 */
	protected boolean canRenderName(EntityLivingBase targetEntity)
	{
		return false;//Client.mc.gameSettings.showDebugInfo;
	}

	protected void passSpecialRender(EntityLivingBase entity, double x, double y, double z)
	{
		if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Pre(entity, this, x, y, z)))
			return;
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

		T npc = (T)entity;

		if (canRenderName(entity))
		{
			float f = 1.6F;
			float f1 = 0.016666668F * f;
			double d3 = entity.getDistanceSqToEntity(renderManager.livingPlayer);
			float f2 = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

			if (d3 < (double)(f2 * f2))
			{
				String s = "Entity"; //entity.getFormattedCommandSenderName().getFormattedText();

				if (entity.isSneaking())
				{
					FontRenderer fontrenderer = getFontRendererFromRenderManager();
					GL11.glPushMatrix();
					GL11.glTranslatef((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
					GL11.glNormal3f(0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
					GL11.glScalef(-f1, -f1, f1);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
					GL11.glDepthMask(false);
					GL11.glEnable(GL11.GL_BLEND);
					OpenGlHelper.glBlendFunc(770, 771, 1, 0);
					Tessellator tessellator = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator.startDrawingQuads();
					int i = fontrenderer.getStringWidth(s) / 2;
					tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
					tessellator.addVertex((double)(-i - 1), -1.0D, 0.0D);
					tessellator.addVertex((double)(-i - 1), 8.0D, 0.0D);
					tessellator.addVertex((double)(i + 1), 8.0D, 0.0D);
					tessellator.addVertex((double)(i + 1), -1.0D, 0.0D);
					tessellator.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glDepthMask(true);
					fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				}
				else
				{
					renderOffsetLivingLabel(entity, x, y, z, s, f1, d3);
				}
			}
		}
		MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Specials.Post(entity, this, x, y, z));
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	abstract ResourceLocation getEntityTexture(T p_110775_1_);

	protected void renderEquippedItems(T p_77029_1_, float p_77029_2_)
	{
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		super.renderEquippedItems(p_77029_1_, p_77029_2_);
		renderArrowsStuckInEntity(p_77029_1_, p_77029_2_);

		ItemStack itemstack1 = p_77029_1_.getHeldItem();

		if (itemstack1 != null)
		{
			GL11.glPushMatrix();
			modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

			net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(itemstack1, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, itemstack1, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

			float f2;

			if (is3D || itemstack1.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack1.getItem()).getRenderType()))
			{
				f2 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f2 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-f2, -f2, f2);
			}
			else if (itemstack1.getItem() == Items.bow)
			{
				f2 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else if (itemstack1.getItem().isFull3D())
			{
				f2 = 0.625F;

				if (itemstack1.getItem().shouldRotateAroundWhenRendering())
				{
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f2, -f2, f2);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else
			{
				f2 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f2, f2, f2);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			float f3;
			int k;
			float f12;

			if (itemstack1.getItem().requiresMultipleRenderPasses())
			{
				for (k = 0; k < itemstack1.getItem().getRenderPasses(itemstack1.getMetadata()); ++k)
				{
					int i = itemstack1.getItem().getColorFromItemStack(itemstack1, k);
					f12 = (float)(i >> 16 & 255) / 255.0F;
					f3 = (float)(i >> 8 & 255) / 255.0F;
					float f4 = (float)(i & 255) / 255.0F;
					GL11.glColor4f(f12, f3, f4, 1.0F);
					renderManager.itemRenderer.renderItem(p_77029_1_, itemstack1, k);
				}
			}
			else
			{
				k = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
				float f11 = (float)(k >> 16 & 255) / 255.0F;
				f12 = (float)(k >> 8 & 255) / 255.0F;
				f3 = (float)(k & 255) / 255.0F;
				GL11.glColor4f(f11, f12, f3, 1.0F);
				renderManager.itemRenderer.renderItem(p_77029_1_, itemstack1, 0);
			}

			GL11.glPopMatrix();
		}
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	abstract void preRenderCallback(T p_77041_1_, float p_77041_2_);

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
	{
		preRenderCallback((T)p_77041_1_, p_77041_2_);
	}

	protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_)
	{
		renderEquippedItems((T)p_77029_1_, p_77029_2_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		doRender((T)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return getEntityTexture((T)p_110775_1_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		doRender((T)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
}
