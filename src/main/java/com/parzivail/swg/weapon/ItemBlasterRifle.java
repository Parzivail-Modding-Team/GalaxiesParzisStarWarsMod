package com.parzivail.swg.weapon;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.item.ICustomCrosshair;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.item.data.BlasterData;
import com.parzivail.util.audio.SoundHandler;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.item.ItemUtils;
import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitBlock;
import com.parzivail.util.math.RaytraceHitEntity;
import com.parzivail.util.ui.AnimatedValue;
import com.parzivail.util.ui.Fx;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class ItemBlasterRifle extends PItem implements ICustomCrosshair, ILeftClickInterceptor
{
	private AnimatedValue avExpansion;
	private final float recoil;

	public ItemBlasterRifle()
	{
		super("slugRifle");
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.maxStackSize = 1;

		avExpansion = new AnimatedValue(-2, 100);
		recoil = 2f;
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public boolean shouldRequestRenderState(ItemStack stack, World world, EntityPlayer player)
	{
		return true;
	}

	@Override
	public boolean shouldUsePrecisionMovement(ItemStack stack, World world, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack.stackTagCompound);
		return bd.isAimingDownSights;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		ItemUtils.ensureNbt(stack);
		BlasterData bd = new BlasterData(stack.stackTagCompound);

		bd.isAimingDownSights = !bd.isAimingDownSights;

		bd.serialize(stack.stackTagCompound);
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public float getZoomLevel(ItemStack stack, World world, EntityPlayer player)
	{
		return 0.5f;
	}

	@Override
	public void drawCrosshair(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	{
		float expansion = 80 * avExpansion.animateTo(getSpreadAmount(stack, player)) - 2;
		BlasterData bd = new BlasterData(stack.stackTagCompound);
		if (bd.isAimingDownSights)
		{
			GL11.glLineWidth(2);
			Fx.D2.DrawWireCircle(0, 0, sr.getScaledHeight() / 3);
			Fx.D2.DrawLine(0, 0, 0, sr.getScaledHeight() / 3);
		}
		else
		{
			GL11.glLineWidth(4);
			GL11.glColor4f(0, 0, 0, 1);
			Fx.D2.DrawLine(0, 5 + expansion, 0, 10 + expansion);
			Fx.D2.DrawLine(0, -5 - expansion, 0, -10 - expansion);
			Fx.D2.DrawLine(5 + expansion, 0, 10 + expansion, 0);
			Fx.D2.DrawLine(-5 - expansion, 0, -10 - expansion, 0);

			GL11.glLineWidth(2);
			GL11.glColor4f(1, 1, 1, 1);
			Fx.D2.DrawLine(0, 5 + expansion, 0, 10 + expansion);
			Fx.D2.DrawLine(0, -5 - expansion, 0, -10 - expansion);
			Fx.D2.DrawLine(5 + expansion, 0, 10 + expansion, 0);
			Fx.D2.DrawLine(-5 - expansion, 0, -10 - expansion, 0);
		}
	}

	private float getSpreadAmount(ItemStack stack, EntityPlayer player)
	{
		BlasterData bd = new BlasterData(stack.stackTagCompound);
		if (bd.isAimingDownSights)
			return 0;

		double movement = Math.sqrt(player.moveForward * player.moveForward + player.moveStrafing * player.moveStrafing);
		return 0.1f * (float)movement + 0.01f;
	}

	@Override
	public void onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			float spread = getSpreadAmount(stack, player);
			Vec3 look = player.getLook(0);
			look.xCoord += world.rand.nextGaussian() * spread;
			look.yCoord += world.rand.nextGaussian() * spread;
			look.zCoord += world.rand.nextGaussian() * spread;
			RaytraceHit hit = EntityUtils.rayTrace(look, 50, player, new Entity[0], true);

			float s = 2;
			//StarWarsGalaxy.proxy.spawnParticle(world, "flame", player.posX, player.posY + player.getEyeHeight(), player.posZ, look.xCoord * s, look.yCoord * s, look.zCoord * s);

			SoundHandler.playSound((EntityPlayerMP)player, "pswg:swg.fx.rifle", player.posX, player.posY, player.posZ, 1, 1);

			Entity e = new EntityBlasterBolt(world, (float)look.xCoord, (float)look.yCoord, (float)look.zCoord, s, 0xFF0000);
			e.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
			world.spawnEntityInWorld(e);

			if (hit instanceof RaytraceHitEntity && ((RaytraceHitEntity)hit).entity instanceof EntityLiving)
			{
				EntityLiving entity = (EntityLiving)((RaytraceHitEntity)hit).entity;
				entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 3);
			}

			if (hit instanceof RaytraceHitBlock)
			{
				RaytraceHitBlock block = (RaytraceHitBlock)hit;
				for (int i = 0; i < 10; i++)
					StarWarsGalaxy.proxy.spawnParticle(world, "smoke", block.hitVec.xCoord + (world.rand.nextDouble() * 0.2 - 0.1), block.hitVec.yCoord + (world.rand.nextDouble() * 0.2 - 0.1), block.hitVec.zCoord + (world.rand.nextDouble() * 0.2 - 0.1), 0, world.rand.nextDouble() * 0.2, 0);
			}
		}

		player.rotationPitch -= recoil * (1 + world.rand.nextGaussian() / 2);
		player.rotationYaw += recoil / 10 * world.rand.nextGaussian();
	}
}
