package com.parzivail.swg.item.blaster;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.item.IGuiOverlay;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.ItemBlasterPowerPack;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.item.blaster.data.BlasterAttachments;
import com.parzivail.swg.item.blaster.data.BlasterData;
import com.parzivail.swg.item.blaster.data.BlasterDescriptor;
import com.parzivail.swg.item.blaster.data.powerpack.BlasterPowerPack;
import com.parzivail.swg.render.decal.Decal;
import com.parzivail.util.audio.SoundHandler;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.common.Pair;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.*;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ItemBlasterRifle extends PItem implements IGuiOverlay, ILeftClickInterceptor
{
	private final BlasterDescriptor descriptor;

	private final AnimatedValue avExpansion;
	private final AnimatedValue avAds;

	public ItemBlasterRifle(BlasterDescriptor descriptor)
	{
		super("rifle." + descriptor.name);
		this.descriptor = descriptor;
		maxStackSize = 1;

		avExpansion = new AnimatedValue(-2, 100);
		avAds = new AnimatedValue(0, 100);
	}

	public static boolean isHoldingBlaster(EntityPlayer player)
	{
		return player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemBlasterRifle;
	}

	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
	}

	public float getAdsLerp(ItemStack stack, World world, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack);
		return avAds.animateTo(bd.isAimingDownSights ? 1 : 0);
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public boolean shouldUsePrecisionMovement(ItemStack stack, World world, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack);
		return bd.isAimingDownSights;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List text, boolean advancedItemTooltips)
	{
		BlasterData bd = new BlasterData(stack);

		if (bd.getScope() != null)
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("scope")), I18n.format(bd.getScope().name)));
		if (bd.getBarrel() != null)
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("barrel")), I18n.format(bd.getBarrel().name)));
		if (bd.getGrip() != null)
			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("grip")), I18n.format(bd.getGrip().name)));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack);

		bd.isAimingDownSights = !bd.isAimingDownSights;

		bd.serialize(stack.stackTagCompound);
		return stack;
	}

	@Override
	public float getZoomLevel(ItemStack stack, World world, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack);
		return bd.getScope().getZoomLevel();
	}

	@Override
	public void drawOverlay(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		float expansion = 32 * avExpansion.animateTo(getSpreadAmount(stack, player), Ease::outQuad) + 5;
		BlasterData bd = new BlasterData(stack);
		Minecraft mc = Minecraft.getMinecraft();

		float size = 2;

		if (bd.isAimingDownSights && bd.getScope() != BlasterAttachments.scopeIronsights)
		{
			GL.Enable(EnableCap.LineSmooth);
			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
			bd.getScope().draw(sr, player, stack);
		}
		else
		{
			float onePixel = 1f / sr.getScaleFactor();

			GL11.glLineWidth(4);
			GL11.glColor4f(0, 0, 0, 1);
			D2.DrawLine(0, expansion - onePixel, 0, size + expansion + onePixel);
			D2.DrawLine(0, -expansion + onePixel, 0, -size - expansion - onePixel);
			D2.DrawLine(expansion - onePixel, 0, size + expansion + onePixel, 0);
			D2.DrawLine(-expansion + onePixel, 0, -size - expansion - onePixel, 0);

			GL11.glLineWidth(2);
			GL11.glColor4f(1, 1, 1, 1);
			D2.DrawLine(0, expansion, 0, size + expansion);
			D2.DrawLine(0, -expansion, 0, -size - expansion);
			D2.DrawLine(expansion, 0, size + expansion, 0);
			D2.DrawLine(-expansion, 0, -size - expansion, 0);
		}

		GL.Enable(EnableCap.Texture2D);
		GL.PushMatrix();
		GL.Translate(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2, 0);

		String remaining = String.format("%s", bd.shotsRemaining);
		int w = mc.fontRendererObj.getStringWidth(remaining);
		int h = mc.fontRendererObj.FONT_HEIGHT;

		GL.Translate(-w - 1, -h - 1, 0);
		mc.fontRendererObj.drawString(remaining, 0, 0, 0xFFFFFF);

		GL.PopMatrix();
	}

	@Override
	public boolean shouldHideHand(EntityPlayer player, ItemStack item)
	{
		return false;
	}

	private float getSpreadAmount(ItemStack stack, EntityPlayer player)
	{
		if (descriptor.spread == 0)
			return 0;

		BlasterData bd = new BlasterData(stack);
		if (bd.isAimingDownSights)
			return 0;

		double movement = Math.sqrt(player.moveForward * player.moveForward + player.moveStrafing * player.moveStrafing);
		return descriptor.spread * (0.5f * (float)movement + 0.05f) * 2;
	}

	@Override
	public void onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack);

		if (bd.shotsRemaining <= 0 && !player.capabilities.isCreativeMode)
		{
			Pair<Integer, BlasterPowerPack> nextPack = getAnotherPack(player);

			if (nextPack == null)
			{
				if (!world.isRemote)
					SoundHandler.playSound((EntityPlayerMP)player, Resources.modColon("swg.fx.rifleDryfire"), player.posX, player.posY, player.posZ, 1, 1);
				return;
			}
			else if (!world.isRemote)
			{
				bd.shotsRemaining = nextPack.right.getNumShots();
				player.inventory.decrStackSize(nextPack.left, 1);
				SoundHandler.playSound((EntityPlayerMP)player, Resources.modColon("swg.fx.rifleReload"), player.posX, player.posY, player.posZ, 1, 1);
			}
		}

		if (!world.isRemote)
		{
			float spread = getSpreadAmount(stack, player);
			RotatedAxes ra = new RotatedAxes(270 - player.rotationYaw, -player.rotationPitch, 0);

			float hS = (world.rand.nextFloat() * 2 - 1) * spread;
			float vS = (world.rand.nextFloat() * 2 - 1) * spread;

			float hSR = 1 - bd.getBarrel().getHorizontalSpreadReduction();
			float vSR = 1 - bd.getBarrel().getVerticalSpreadReduction();

			ra.rotateGlobalYaw(hS * hSR);
			ra.rotateGlobalPitch(vS * vSR);

			Vec3 look = Vec3.createVectorHelper(Math.cos(ra.getPitch() / 180f * Math.PI) * Math.cos(ra.getYaw() / 180f * Math.PI), Math.sin(ra.getPitch() / 180f * Math.PI), Math.cos(ra.getPitch() / 180f * Math.PI) * Math.sin(-ra.getYaw() / 180f * Math.PI));
			RaytraceHit hit = EntityUtils.rayTrace(look, descriptor.range + descriptor.range * bd.getBarrel().getRangeIncrease(), player, new Entity[0], true);

			SoundHandler.playSound((EntityPlayerMP)player, Resources.modColon("swg.fx." + name), player.posX, player.posY, player.posZ, 1 + (float)world.rand.nextGaussian() / 10, 1 - bd.getBarrel().getNoiseReduction());

			Entity e = new EntityBlasterBolt(world, (float)look.xCoord, (float)look.yCoord, (float)look.zCoord, descriptor.damage, descriptor.boltColor);
			e.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
			world.spawnEntityInWorld(e);

			if (hit instanceof RaytraceHitEntity && ((RaytraceHitEntity)hit).entity instanceof EntityLiving)
			{
				EntityLiving entity = (EntityLiving)((RaytraceHitEntity)hit).entity;
				entity.attackEntityFrom(DamageSource.causePlayerDamage(player), descriptor.damage);
			}

			if (hit instanceof RaytraceHitBlock)
			{
				RaytraceHitBlock block = (RaytraceHitBlock)hit;
				for (int i = 0; i < 10; i++)
					StarWarsGalaxy.proxy.spawnParticle(world, "smoke", block.hitVec.xCoord + (world.rand.nextDouble() * 0.2 - 0.1), block.hitVec.yCoord + (world.rand.nextDouble() * 0.2 - 0.1), block.hitVec.zCoord + (world.rand.nextDouble() * 0.2 - 0.1), 0, world.rand.nextDouble() * 0.2, 0);
				StarWarsGalaxy.proxy.createDecal(world, Decal.BULLET_IMPACT, (float)block.hitVec.xCoord, (float)block.hitVec.yCoord, (float)block.hitVec.zCoord, 1, block.sideHitFace);
			}

			if (!player.capabilities.isCreativeMode)
				bd.shotsRemaining--;

			bd.serialize(stack.stackTagCompound);
		}

		// Recoil
		player.rotationPitch -= (descriptor.damage / 2) * (1 - bd.getBarrel().getVerticalRecoilReduction()) * (1 - bd.getGrip().getVerticalRecoilReduction());
		player.rotationYaw += (descriptor.damage / 5 * world.rand.nextGaussian()) * (1 - bd.getBarrel().getHorizontalRecoilReduction()) * (1 - bd.getGrip().getHorizontalRecoilReduction());
	}

	private Pair<Integer, BlasterPowerPack> getAnotherPack(EntityPlayer player)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack s = player.inventory.getStackInSlot(i);
			BlasterPowerPack a = ItemBlasterPowerPack.getPackType(s);
			if (a == null)
				continue;

			return new Pair<>(i, a);
		}
		return null;
	}
}
