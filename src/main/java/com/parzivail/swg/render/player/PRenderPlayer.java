package com.parzivail.swg.render.player;

import com.mojang.authlib.GameProfile;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.player.species.SpeciesType;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.render.npc.model.*;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class PRenderPlayer extends RendererLivingEntity
{
	private static final ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");
	private static final HashMap<SpeciesType, PModelBipedBase> speciesModelMap = new HashMap<>();
	public PModelBipedBase modelArmorChestplate;
	public PModelBipedBase modelArmor;

	static
	{
		speciesModelMap.put(SpeciesType.Human, new ModelRefBiped());
		speciesModelMap.put(SpeciesType.Bith_M, new ModelBithM());
		speciesModelMap.put(SpeciesType.Bothan_F, new ModelBothanF());
		speciesModelMap.put(SpeciesType.Bothan_M, new ModelBothanM());
		speciesModelMap.put(SpeciesType.Chagrian_F, new ModelChagrianF());
		speciesModelMap.put(SpeciesType.Chagrian_M, new ModelChagrianM());
		speciesModelMap.put(SpeciesType.Togruta_F, new ModelTogrutaF());
		speciesModelMap.put(SpeciesType.Togruta_M, new ModelTogrutaM());
		speciesModelMap.put(SpeciesType.Twilek_F, new ModelTwilekF());
		speciesModelMap.put(SpeciesType.Twilek_M, new ModelTwilekM());
	}

	public static final PRenderPlayer instance = new PRenderPlayer();

	public PRenderPlayer()
	{
		super(new ModelRefBiped(), 0.5F);
		modelArmorChestplate = new ModelBipedWrapper(new ModelBiped(1.0F));
		modelArmor = new ModelBipedWrapper(new ModelBiped(0.5F));

		renderManager = RenderManager.instance;
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	protected int shouldRenderPass(AbstractClientPlayer p_77032_1_, int p_77032_2_, float p_77032_3_)
	{
		ItemStack itemstack = p_77032_1_.inventory.armorItemInSlot(3 - p_77032_2_);

		if (itemstack != null)
		{
			Item item = itemstack.getItem();

			if (item instanceof ItemArmor)
			{
				ItemArmor itemarmor = (ItemArmor)item;
				bindTexture(RenderBiped.getArmorResource(p_77032_1_, itemstack, p_77032_2_, null));
				PModelBipedBase modelbiped = p_77032_2_ == 2 ? modelArmor : modelArmorChestplate;
				modelbiped.getHead().showModel = p_77032_2_ == 0;
				if (modelbiped.getHeadgear() != null)
					modelbiped.getHeadgear().showModel = p_77032_2_ == 0;
				modelbiped.getBody().showModel = p_77032_2_ == 1 || p_77032_2_ == 2;
				modelbiped.getArmRight().showModel = p_77032_2_ == 1;
				modelbiped.getArmLeft().showModel = p_77032_2_ == 1;
				modelbiped.getLegRight().showModel = p_77032_2_ == 2 || p_77032_2_ == 3;
				modelbiped.getLegLeft().showModel = p_77032_2_ == 2 || p_77032_2_ == 3;
				setRenderPassModel(modelbiped);
				modelbiped.swingProgress = mainModel.swingProgress;
				modelbiped.isRiding = mainModel.isRiding;
				modelbiped.isChild = mainModel.isChild;

				//Move outside if to allow for more then just CLOTH
				int j = itemarmor.getColor(itemstack);
				if (j != -1)
				{
					float f1 = (float)(j >> 16 & 255) / 255.0F;
					float f2 = (float)(j >> 8 & 255) / 255.0F;
					float f3 = (float)(j & 255) / 255.0F;
					GL11.glColor3f(f1, f2, f3);

					if (itemstack.isItemEnchanted())
					{
						return 31;
					}

					return 16;
				}

				GL11.glColor3f(1.0F, 1.0F, 1.0F);

				if (itemstack.isItemEnchanted())
				{
					return 15;
				}

				return 1;
			}
		}

		return -1;
	}

	protected void func_82408_c(AbstractClientPlayer p_82408_1_, int p_82408_2_, float p_82408_3_)
	{
		ItemStack itemstack = p_82408_1_.inventory.armorItemInSlot(3 - p_82408_2_);

		if (itemstack != null)
		{
			Item item = itemstack.getItem();

			if (item instanceof ItemArmor)
			{
				bindTexture(RenderBiped.getArmorResource(p_82408_1_, itemstack, p_82408_2_, "overlay"));
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
			}
		}
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void renderPlayer(AbstractClientPlayer player, double x, double y, double z, float unused, float partialTicks)
	{
		PModelBipedBase playerModel = getPlayerModel(player);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		ItemStack itemstack = player.inventory.getCurrentItem();
		modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = playerModel.heldItemRight = itemstack != null ? 1 : 0;

		if (itemstack != null && player.getItemInUseCount() > 0)
		{
			EnumAction enumaction = itemstack.getItemUseAction();

			if (enumaction == EnumAction.block)
			{
				modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = playerModel.heldItemRight = 3;
			}
			else if (enumaction == EnumAction.bow)
			{
				modelArmorChestplate.aimedBow = modelArmor.aimedBow = playerModel.aimedBow = true;
			}
		}

		modelArmorChestplate.isSneak = modelArmor.isSneak = playerModel.isSneak = player.isSneaking();
		double d3 = y - (double)player.yOffset;

		if (player.isSneaking() && !(player instanceof EntityPlayerSP))
		{
			d3 -= 0.125D;
		}

		super.doRender(player, x, d3, z, unused, partialTicks);
		modelArmorChestplate.aimedBow = modelArmor.aimedBow = playerModel.aimedBow = false;
		modelArmorChestplate.isSneak = modelArmor.isSneak = playerModel.isSneak = false;
		modelArmorChestplate.heldItemRight = modelArmor.heldItemRight = playerModel.heldItemRight = 0;
	}

	private PModelBipedBase getPlayerModel(EntityPlayer player)
	{
		PswgExtProp prop = PswgExtProp.get(player);
		if (prop == null)
			return speciesModelMap.get(SpeciesType.Human);
		SpeciesType species = prop.getSpecies();
		PModelBipedBase model = getSpeciesModel(species);
		if (mainModel != model)
			mainModel = model;
		return model;
	}

	private PModelBipedBase getSpeciesModel(SpeciesType species)
	{
		return speciesModelMap.get(species);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(AbstractClientPlayer player)
	{
		PModelBipedBase playerModel = getPlayerModel(player);
		if (playerModel == null)
			return steveTextures;
		return playerModel.getBaseTexture(player);
	}

	protected void renderEquippedItems(AbstractClientPlayer player, float p_77029_2_)
	{
		PModelBipedBase playerModel = getPlayerModel(player);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		super.renderEquippedItems(player, p_77029_2_);
		renderArrowsStuckInEntity(player, p_77029_2_);
		ItemStack itemstack = player.inventory.armorItemInSlot(3);

		if (itemstack != null)
		{
			GL11.glPushMatrix();
			playerModel.getHead().postRender(0.0625F);
			float f1;

			if (itemstack.getItem() instanceof ItemBlock)
			{
				IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, IItemRenderer.ItemRenderType.EQUIPPED);
				boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, itemstack, IItemRenderer.ItemRendererHelper.BLOCK_3D));

				if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
				{
					f1 = 0.625F;
					GL11.glTranslatef(0.0F, -0.25F, 0.0F);
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glScalef(f1, -f1, -f1);
				}

				renderManager.itemRenderer.renderItem(player, itemstack, 0);
			}
			else if (itemstack.getItem() == Items.skull)
			{
				f1 = 1.0625F;
				GL11.glScalef(f1, -f1, -f1);
				GameProfile gameprofile = null;

				if (itemstack.hasTagCompound())
				{
					NBTTagCompound nbttagcompound = itemstack.getTagCompound();

					if (nbttagcompound.hasKey("SkullOwner", 10))
					{
						gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
					}
					else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkullOwner")))
					{
						gameprofile = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
					}
				}

				TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getMetadata(), gameprofile);
			}

			GL11.glPopMatrix();
		}

		float f2;
		boolean flag = player.hasCape();
		//flag = event.renderCape && flag;
		float f4;

		//		if (flag && !player.isInvisible() && !player.getHideCape())
		//		{
		//			bindTexture(player.getLocationCape());
		//			GL11.glPushMatrix();
		//			GL11.glTranslatef(0.0F, 0.0F, 0.125F);
		//			double d3 = player.field_71091_bM + (player.field_71094_bP - player.field_71091_bM) * (double)p_77029_2_ - (player.prevPosX + (player.posX - player.prevPosX) * (double)p_77029_2_);
		//			double d4 = player.field_71096_bN + (player.field_71095_bQ - player.field_71096_bN) * (double)p_77029_2_ - (player.prevPosY + (player.posY - player.prevPosY) * (double)p_77029_2_);
		//			double d0 = player.field_71097_bO + (player.field_71085_bR - player.field_71097_bO) * (double)p_77029_2_ - (player.prevPosZ + (player.posZ - player.prevPosZ) * (double)p_77029_2_);
		//			f4 = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * p_77029_2_;
		//			double d1 = (double)MathHelper.sin(f4 * (float)Math.PI / 180.0F);
		//			double d2 = (double)(-MathHelper.cos(f4 * (float)Math.PI / 180.0F));
		//			float f5 = (float)d4 * 10.0F;
		//
		//			if (f5 < -6.0F)
		//			{
		//				f5 = -6.0F;
		//			}
		//
		//			if (f5 > 32.0F)
		//			{
		//				f5 = 32.0F;
		//			}
		//
		//			float f6 = (float)(d3 * d1 + d0 * d2) * 100.0F;
		//			float f7 = (float)(d3 * d2 - d0 * d1) * 100.0F;
		//
		//			if (f6 < 0.0F)
		//			{
		//				f6 = 0.0F;
		//			}
		//
		//			float f8 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * p_77029_2_;
		//			f5 += MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * p_77029_2_) * 6.0F) * 32.0F * f8;
		//
		//			if (player.isSneaking())
		//			{
		//				f5 += 25.0F;
		//			}
		//
		//			GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
		//			GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
		//			GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
		//			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		//			modelBipedMain.renderCloak(0.0625F);
		//			GL11.glPopMatrix();
		//		}

		ItemStack itemstack1 = player.inventory.getCurrentItem();

		if (itemstack1 != null)
		{
			GL11.glPushMatrix();
			playerModel.getArmRight().postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

			if (player.fishEntity != null)
			{
				itemstack1 = new ItemStack(Items.stick);
			}

			EnumAction enumaction = null;

			if (player.getItemInUseCount() > 0)
			{
				enumaction = itemstack1.getItemUseAction();
			}

			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack1, IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, itemstack1, IItemRenderer.ItemRendererHelper.BLOCK_3D));

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

				if (player.getItemInUseCount() > 0 && enumaction == EnumAction.block)
				{
					GL11.glTranslatef(0.05F, 0.0F, -0.1F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
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
					f4 = (float)(i & 255) / 255.0F;
					GL11.glColor4f(f12, f3, f4, 1.0F);
					renderManager.itemRenderer.renderItem(player, itemstack1, k);
				}
			}
			else
			{
				k = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
				float f11 = (float)(k >> 16 & 255) / 255.0F;
				f12 = (float)(k >> 8 & 255) / 255.0F;
				f3 = (float)(k & 255) / 255.0F;
				GL11.glColor4f(f11, f12, f3, 1.0F);
				renderManager.itemRenderer.renderItem(player, itemstack1, 0);
			}

			GL11.glPopMatrix();
		}
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(AbstractClientPlayer p_77041_1_, float p_77041_2_)
	{
		float f1 = 0.9375F;
		GL11.glScalef(f1, f1, f1);
	}

	protected void renderOffsetLivingLabel(AbstractClientPlayer p_96449_1_, double p_96449_2_, double p_96449_4_, double p_96449_6_, String p_96449_8_, float p_96449_9_, double p_96449_10_)
	{
		if (p_96449_10_ < 100.0D)
		{
			Scoreboard scoreboard = p_96449_1_.getWorldScoreboard();
			ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);

			if (scoreobjective != null)
			{
				Score score = scoreboard.getValueFromObjective(p_96449_1_.getCommandSenderName(), scoreobjective);

				if (p_96449_1_.isPlayerSleeping())
				{
					renderLivingLabel(p_96449_1_, score.getScorePoints() + " " + scoreobjective.getDisplayName(), p_96449_2_, p_96449_4_ - 1.5D, p_96449_6_, 64);
				}
				else
				{
					renderLivingLabel(p_96449_1_, score.getScorePoints() + " " + scoreobjective.getDisplayName(), p_96449_2_, p_96449_4_, p_96449_6_, 64);
				}

				p_96449_4_ += (double)((float)getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_96449_9_);
			}
		}

		super.renderOffsetLivingLabel(p_96449_1_, p_96449_2_, p_96449_4_, p_96449_6_, p_96449_8_, p_96449_9_, p_96449_10_);
	}

	public void renderFirstPersonArm(EntityPlayer player)
	{
		PModelBipedBase playerModel = getPlayerModel(player);
		float f = 1.0F;
		GL11.glColor3f(f, f, f);
		playerModel.swingProgress = 0.0F;
		playerModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, player);
		playerModel.getArmRight().render(0.0625F);
	}

	public void renderHand(float p_78476_1_, int p_78476_2_)
	{
		Minecraft mc = Client.mc;
		EntityRenderer er = Client.mc.entityRenderer;

		if (er.debugViewDirection <= 0)
		{
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			float f1 = 0.07F;

			if (mc.gameSettings.anaglyph)
			{
				GL11.glTranslatef((float)(-(p_78476_2_ * 2 - 1)) * f1, 0.0F, 0.0F);
			}

			// TODO: fix if this causes issues
			//			if (er.cameraZoom != 1.0D)
			//			{
			//				GL11.glTranslatef((float)er.cameraYaw, (float)(-er.cameraPitch), 0.0F);
			//				GL11.glScaled(er.cameraZoom, er.cameraZoom, 1.0D);
			//			}

			float farPlaneDistance = ReflectionHelper.getPrivateValue(EntityRenderer.class, er, "farPlaneDistance", "field_78530_s", "u");

			Project.gluPerspective(getFOVModifier(p_78476_1_, false), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, farPlaneDistance * 2.0F);

			if (mc.playerController.enableEverythingIsScrewedUpMode())
			{
				float f2 = 0.6666667F;
				GL11.glScalef(1.0F, f2, 1.0F);
			}

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

			if (mc.gameSettings.anaglyph)
			{
				GL11.glTranslatef((float)(p_78476_2_ * 2 - 1) * 0.1F, 0.0F, 0.0F);
			}

			GL11.glPushMatrix();
			hurtCameraEffect(p_78476_1_);

			if (mc.gameSettings.viewBobbing)
			{
				setupViewBobbing(p_78476_1_);
			}

			if (mc.gameSettings.thirdPersonView == 0 && !mc.renderViewEntity.isPlayerSleeping() && !mc.gameSettings.hideGUI && !mc.playerController.enableEverythingIsScrewedUpMode())
			{
				er.enableLightmap((double)p_78476_1_);
				er.itemRenderer.renderItemInFirstPerson(p_78476_1_);
				er.disableLightmap((double)p_78476_1_);
			}

			GL11.glPopMatrix();

			if (mc.gameSettings.thirdPersonView == 0 && !mc.renderViewEntity.isPlayerSleeping())
			{
				er.itemRenderer.renderOverlays(p_78476_1_);
				hurtCameraEffect(p_78476_1_);
			}

			if (mc.gameSettings.viewBobbing)
			{
				setupViewBobbing(p_78476_1_);
			}
		}
	}

	private float getFOVModifier(float p_78481_1_, boolean p_78481_2_)
	{
		Minecraft mc = Client.mc;
		EntityRenderer er = Client.mc.entityRenderer;
		float fovModifierHand = ReflectionHelper.getPrivateValue(EntityRenderer.class, er, "fovModifierHand", "field_78507_R", "W");
		float fovModifierHandPrev = ReflectionHelper.getPrivateValue(EntityRenderer.class, er, "fovModifierHandPrev", "field_78506_S", "X");

		if (er.debugViewDirection > 0)
		{
			return 90.0F;
		}
		else
		{
			EntityLivingBase entityplayer = mc.renderViewEntity;
			float f1 = 70.0F;

			if (p_78481_2_)
			{
				f1 = mc.gameSettings.fovSetting;
				f1 *= fovModifierHandPrev + (fovModifierHand - fovModifierHandPrev) * p_78481_1_;
			}

			if (entityplayer.getHealth() <= 0.0F)
			{
				float f2 = (float)entityplayer.deathTime + p_78481_1_;
				f1 /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
			}

			Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(mc.theWorld, entityplayer, p_78481_1_);

			if (block.getMaterial() == Material.water)
			{
				f1 = f1 * 60.0F / 70.0F;
			}

			//			return f1 + er.prevDebugCamFOV + (er.debugCamFOV - er.prevDebugCamFOV) * p_78481_1_;
			return f1;
		}
	}

	private void setupViewBobbing(float p_78475_1_)
	{
		Minecraft mc = Client.mc;
		if (mc.renderViewEntity instanceof EntityPlayer)
		{
			EntityPlayer entityplayer = (EntityPlayer)mc.renderViewEntity;
			float f1 = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
			float f2 = -(entityplayer.distanceWalkedModified + f1 * p_78475_1_);
			float f3 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * p_78475_1_;
			float f4 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * p_78475_1_;
			GL11.glTranslatef(MathHelper.sin(f2 * (float)Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * (float)Math.PI) * f3), 0.0F);
			GL11.glRotatef(MathHelper.sin(f2 * (float)Math.PI) * f3 * 3.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(Math.abs(MathHelper.cos(f2 * (float)Math.PI - 0.2F) * f3) * 5.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
		}
	}

	private void hurtCameraEffect(float p_78482_1_)
	{
		Minecraft mc = Client.mc;

		EntityLivingBase entitylivingbase = mc.renderViewEntity;
		float f1 = (float)entitylivingbase.hurtTime - p_78482_1_;
		float f2;

		if (entitylivingbase.getHealth() <= 0.0F)
		{
			f2 = (float)entitylivingbase.deathTime + p_78482_1_;
			GL11.glRotatef(40.0F - 8000.0F / (f2 + 200.0F), 0.0F, 0.0F, 1.0F);
		}

		if (f1 >= 0.0F)
		{
			f1 /= (float)entitylivingbase.maxHurtTime;
			f1 = MathHelper.sin(f1 * f1 * f1 * f1 * (float)Math.PI);
			f2 = entitylivingbase.attackedAtYaw;
			GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f1 * 14.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(f2, 0.0F, 1.0F, 0.0F);
		}
	}

	/**
	 * Sets a simple glTranslate on a LivingEntity.
	 */
	protected void renderLivingAt(AbstractClientPlayer p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
	{
		if (p_77039_1_.isEntityAlive() && p_77039_1_.isPlayerSleeping())
		{
			super.renderLivingAt(p_77039_1_, p_77039_2_ + (double)p_77039_1_.field_71079_bU, p_77039_4_ + (double)p_77039_1_.field_71082_cx, p_77039_6_ + (double)p_77039_1_.field_71089_bV);
		}
		else
		{
			super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
		}
	}

	protected void rotateCorpse(AbstractClientPlayer p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
	{
		if (p_77043_1_.isEntityAlive() && p_77043_1_.isPlayerSleeping())
		{
			GL11.glRotatef(p_77043_1_.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
		}
		else
		{
			super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
		}
	}

	protected void renderOffsetLivingLabel(EntityLivingBase p_96449_1_, double p_96449_2_, double p_96449_4_, double p_96449_6_, String p_96449_8_, float p_96449_9_, double p_96449_10_)
	{
		renderOffsetLivingLabel((AbstractClientPlayer)p_96449_1_, p_96449_2_, p_96449_4_, p_96449_6_, p_96449_8_, p_96449_9_, p_96449_10_);
	}

	/**
	 * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
	 * entityLiving, partialTickTime
	 */
	protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
	{
		preRenderCallback((AbstractClientPlayer)p_77041_1_, p_77041_2_);
	}

	protected void func_82408_c(EntityLivingBase p_82408_1_, int p_82408_2_, float p_82408_3_)
	{
		func_82408_c((AbstractClientPlayer)p_82408_1_, p_82408_2_, p_82408_3_);
	}

	/**
	 * Queries whether should render the specified pass or not.
	 */
	protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_)
	{
		return shouldRenderPass((AbstractClientPlayer)p_77032_1_, p_77032_2_, p_77032_3_);
	}

	protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_)
	{
		renderEquippedItems((AbstractClientPlayer)p_77029_1_, p_77029_2_);
	}

	protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
	{
		rotateCorpse((AbstractClientPlayer)p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
	}

	/**
	 * Sets a simple glTranslate on a LivingEntity.
	 */
	protected void renderLivingAt(EntityLivingBase p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
	{
		renderLivingAt((AbstractClientPlayer)p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		renderPlayer((AbstractClientPlayer)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(Entity p_110775_1_)
	{
		return getEntityTexture((AbstractClientPlayer)p_110775_1_);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
	{
		renderPlayer((AbstractClientPlayer)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
	}
}
