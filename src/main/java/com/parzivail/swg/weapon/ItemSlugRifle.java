package com.parzivail.swg.weapon;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.item.PItem;
import com.parzivail.util.audio.SoundHandler;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitBlock;
import com.parzivail.util.math.RaytraceHitEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemSlugRifle extends PItem
{
	public ItemSlugRifle()
	{
		super("slugRifle");
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.maxStackSize = 1;
	}

	@Override
	public boolean isFull3D()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	public boolean capturesLeftClick()
	{
		return true;
	}

	@Override
	public boolean shouldRequestRenderState()
	{
		return true;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			RaytraceHit hit = EntityUtils.rayTrace(50, player, new Entity[0], true);

			Vec3 look = player.getLook(0);
			float s = 2;
			//StarWarsGalaxy.proxy.spawnParticle(world, "flame", player.posX, player.posY + player.getEyeHeight(), player.posZ, look.xCoord * s, look.yCoord * s, look.zCoord * s);

			SoundHandler.playSound((EntityPlayerMP)player, "pswg:swg.fx.rifle", player.posX, player.posY, player.posZ, 1, 1);

			Entity e = new EntityBlasterBolt(world, (float)look.xCoord, (float)look.yCoord, (float)look.zCoord, s, 0x0000FF);
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

		return stack;
	}
}
