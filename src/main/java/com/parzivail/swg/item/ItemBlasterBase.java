package com.parzivail.swg.item;

import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.item.data.BlasterData;
import com.parzivail.swg.item.data.BlasterDescriptor;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.item.ILeftClickInterceptor;
import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitEntity;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlasterBase extends SwgItem implements ILeftClickInterceptor
{
	private final BlasterDescriptor descriptor;

	private final AnimatedValue avExpansion;
	private final AnimatedValue avAds;
	private final AnimatedValue avHeatup;
	private final AnimatedValue avCooldown;

	public ItemBlasterBase(BlasterDescriptor descriptor)
	{
		super("rifle." + descriptor.name);
		this.descriptor = descriptor;
		maxStackSize = 1;

		avExpansion = new AnimatedValue(-2, 100);
		avAds = new AnimatedValue(0, 100);

		avHeatup = new AnimatedValue(0, 50);
		avCooldown = new AnimatedValue(0, 50);
	}

	public static boolean isHoldingBlaster(EntityPlayer player)
	{
		return player.getHeldItemMainhand().getItem() instanceof ItemBlasterBase;
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag)
	{
		//		BlasterData bd = new BlasterData(stack);
		//
		//		if (bd.getScope() != null)
		//			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("scope")), I18n.format(bd.getScope().name)));
		//		if (bd.getBarrel() != null)
		//			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("barrel")), I18n.format(bd.getBarrel().name)));
		//		if (bd.getGrip() != null)
		//			text.add(String.format("%s: %s", I18n.format(Resources.guiDot("grip")), I18n.format(bd.getGrip().name)));
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
		{
			BlasterData bd = new BlasterData(stack);

			bd.isAimingDownSights = !bd.isAimingDownSights;

			bd.serialize(stack);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public float getZoomLevel(ItemStack stack, World world, EntityPlayer player)
	{
		//		BlasterData bd = new BlasterData(stack);
		//		return bd.getScope().getZoomLevel();
		return super.getZoomLevel(stack, world, player);
	}

	//	public void drawOverlay(ScaledResolution sr, EntityPlayer player, ItemStack stack)
	//	{
	//		float spread = getSpreadAmount(stack, player);
	//		float expansionPerc = avExpansion.animateTo(spread, Ease::outQuad);
	//		float expansion = 32 * expansionPerc + 5;
	//		BlasterData bd = new BlasterData(stack);
	//		Minecraft mc = Minecraft.getMinecraft();
	//
	//		float size = 4;
	//
	//		if (bd.isAimingDownSights && bd.getScope() != BlasterAttachments.scopeIronsights)
	//		{
	//			GL.Enable(EnableCap.LineSmooth);
	//			GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
	//			bd.getScope().draw(sr, player, stack);
	//		}
	//		else
	//		{
	//			float onePixel = 1f / sr.getScaleFactor();
	//
	//			GL11.glLineWidth(4);
	//			GL11.glColor4f(0, 0, 0, 1);
	//			D2.DrawLine(0, expansion - onePixel, 0, size + expansion + onePixel);
	//			D2.DrawLine(0, -expansion + onePixel, 0, -size - expansion - onePixel);
	//			D2.DrawLine(expansion - onePixel, 0, size + expansion + onePixel, 0);
	//			D2.DrawLine(-expansion + onePixel, 0, -size - expansion - onePixel, 0);
	//			if (bd.isAimingDownSights)
	//				D2.DrawSolidCircle(0, 0, 3 * onePixel);
	//
	//			GL11.glLineWidth(2);
	//			GL11.glColor4f(1, 1, 1, 1);
	//			D2.DrawLine(0, expansion, 0, size + expansion);
	//			D2.DrawLine(0, -expansion, 0, -size - expansion);
	//			D2.DrawLine(expansion, 0, size + expansion, 0);
	//			D2.DrawLine(-expansion, 0, -size - expansion, 0);
	//			if (bd.isAimingDownSights)
	//				D2.DrawSolidCircle(0, 0, 2 * onePixel);
	//		}
	//
	//		GL.PushMatrix();
	//		GL.Translate(0, 50, 0);
	//		if (bd.isCoolingDown())
	//		{
	//			float cooldown = avCooldown.animateTo(60 * bd.cooldownTimer / (float)descriptor.cooldownTimeTicks);
	//			float red = Math.min(10, cooldown);
	//			float yellow = Math.min(red + 10, cooldown);
	//			float blue = Math.min(yellow + 40, cooldown);
	//
	//			GL11.glColor4f(0, 0, 0, 0.5f);
	//			Fx.D2.DrawSolidRectangle(-30, 0, 60, 1.5f);
	//
	//			GL.Color(GLPalette.ELECTRIC_BLUE);
	//			Fx.D2.DrawSolidRectangle(-30, 0, blue, 1.5f);
	//
	//			GL.Color(GLPalette.SW_YELLOW);
	//			Fx.D2.DrawSolidRectangle(-30, 0, yellow, 1.5f);
	//
	//			GL.Color(GLPalette.ANALOG_RED);
	//			Fx.D2.DrawSolidRectangle(-30, 0, red, 1.5f);
	//
	//			GL.Color(GLPalette.WHITE);
	//			Fx.D2.DrawSolidTriangle(-30 + cooldown, 3, 2);
	//			Fx.D2.DrawSolidTriangle(-30 + cooldown, -1.25f, -2);
	//
	//			GL11.glLineWidth(1);
	//			Fx.D2.DrawLine(-30f + cooldown, 3, -30f + cooldown, -1.25f);
	//		}
	//		else if (bd.heat != 0)
	//		{
	//			GL11.glColor4f(0, 0, 0, 0.5f);
	//			Fx.D2.DrawSolidRectangle(-30, 0, 60, 1.5f);
	//
	//			GL11.glColor4f(1, 1, 1, 1);
	//			Fx.D2.DrawSolidRectangle(29, 0, 1, 1.5f);
	//
	//			Fx.D2.DrawSolidRectangle(-30, 0, avHeatup.animateTo(60 * bd.heat / (float)(10 * descriptor.roundsBeforeOverheat)), 1.5f);
	//		}
	//		GL.PopMatrix();
	//
	//		GL.Enable(EnableCap.Texture2D);
	//		GL.PushMatrix();
	//		GL.Translate(sr.getScaledWidth_double() / 2, sr.getScaledHeight_double() / 2, 0);
	//
	//		String remaining = String.format("Rounds: %s", bd.shotsRemaining);
	//		int w = mc.fontRendererObj.getStringWidth(remaining);
	//		int h = mc.fontRendererObj.FONT_HEIGHT;
	//
	//		GL.Translate(-w - 1, -h - 1, 0);
	//		mc.fontRendererObj.drawString(remaining, 0, 0, 0xFFFFFF);
	//
	//		GL.PopMatrix();
	//	}

	//	@Override
	//	public boolean shouldHideHand(EntityPlayer player, ItemStack item)
	//	{
	//		return false;
	//	}

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
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem)
	{
		BlasterData bd = new BlasterData(stack);

		if (!worldIn.isRemote)
		{
			if (bd.isCoolingDown())
				bd.cooldownTimer--;
			else
				bd.cooldownTimer = 0;

			if (bd.heat > 0)
				bd.heat--;
			else
				bd.heat = 0;

			if (bd.shotTimer > 0)
				bd.shotTimer--;
			else
				bd.shotTimer = 0;

			bd.serialize(stack);
		}
	}

	@Override
	public boolean isLeftClickRepeatable()
	{
		return true;
	}

	//	@Override
	//	public boolean onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
	//	{
	//		BlasterData bd = new BlasterData(stack);
	//
	//		if (!bd.isReady() || bd.isCoolingDown())
	//			return false;
	//
	//		if (!player.capabilities.isCreativeMode)
	//		{
	//			if (bd.shotsRemaining <= 0)
	//			{
	//				Pair<Integer, BlasterPowerPack> nextPack = getAnotherPack(player);
	//
	//				if (nextPack == null)
	//				{
	//					if (!world.isRemote)
	//						Sfx.play(player, Resources.modColon("swg.fx.rifleDryfire"), 1, 1);
	//					return false;
	//				}
	//				else if (!world.isRemote)
	//				{
	//					bd.shotsRemaining = nextPack.right.getNumShots();
	//					player.inventory.decrStackSize(nextPack.left, 1);
	//					Sfx.play(player, Resources.modColon("swg.fx.rifleReload"), 1, 1);
	//				}
	//			}
	//
	//			bd.shotsRemaining--;
	//		}
	//
	//		if (!world.isRemote)
	//		{
	//			float spread = getSpreadAmount(stack, player);
	//			RotatedAxes ra = new RotatedAxes(270 - player.rotationYaw, -player.rotationPitch, 0);
	//
	//			float hS = (world.rand.nextFloat() * 2 - 1) * spread;
	//			float vS = (world.rand.nextFloat() * 2 - 1) * spread;
	//
	//			float hSR = 1 - bd.getBarrel().getHorizontalSpreadReduction();
	//			float vSR = 1 - bd.getBarrel().getVerticalSpreadReduction();
	//
	//			ra.rotateGlobalYaw(hS * hSR);
	//			ra.rotateGlobalPitch(vS * vSR);
	//
	//			Vec3 look = Vec3.createVectorHelper(Math.cos(ra.getPitch() / 180f * Math.PI) * Math.cos(ra.getYaw() / 180f * Math.PI), Math.sin(ra.getPitch() / 180f * Math.PI), Math.cos(ra.getPitch() / 180f * Math.PI) * Math.sin(-ra.getYaw() / 180f * Math.PI));
	//			RaytraceHit hit = EntityRegUtil.rayTrace(look, descriptor.range + descriptor.range * bd.getBarrel().getRangeIncrease(), player, new Entity[0], true);
	//
	//			Sfx.play(player, Resources.modColon("swg.fx." + name), 1 + (float)world.rand.nextGaussian() / 10, 1 - bd.getBarrel().getNoiseReduction());
	//
	//			Entity e = new EntityBlasterBolt(world, (float)look.xCoord, (float)look.yCoord, (float)look.zCoord, descriptor.damage, descriptor.boltColor);
	//			e.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
	//			world.spawnEntityInWorld(e);
	//
	//			if (hit instanceof RaytraceHitEntity && ((RaytraceHitEntity)hit).entity instanceof EntityLiving)
	//			{
	//				EntityLiving entity = (EntityLiving)((RaytraceHitEntity)hit).entity;
	//				entity.attackEntityFrom(DamageSource.causePlayerDamage(player), descriptor.damage);
	//			}
	//
	//			if (hit instanceof RaytraceHitBlock)
	//			{
	//				RaytraceHitBlock block = (RaytraceHitBlock)hit;
	//				for (int i = 0; i < 10; i++)
	//					StarWarsGalaxy.proxy.spawnParticle(world, "smoke", block.hitVec.xCoord + (world.rand.nextDouble() * 0.2 - 0.1), block.hitVec.yCoord + (world.rand.nextDouble() * 0.2 - 0.1), block.hitVec.zCoord + (world.rand.nextDouble() * 0.2 - 0.1), 0, world.rand.nextDouble() * 0.2, 0);
	//				StarWarsGalaxy.proxy.createDecal(world, Decal.BULLET_IMPACT, block.blockX, block.blockY, block.blockZ, (float)block.hitVec.xCoord, (float)block.hitVec.yCoord, (float)block.hitVec.zCoord, 1, block.sideHitFace);
	//			}
	//
	//			bd.shotTimer += descriptor.autofireTimeTicks;
	//			bd.heat += 10;
	//			if (bd.heat >= 10 * descriptor.roundsBeforeOverheat)
	//			{
	//				bd.heat = 0;
	//				bd.cooldownTimer = descriptor.cooldownTimeTicks;
	//			}
	//
	//			bd.serialize(stack.stackTagCompound);
	//		}
	//
	//		// TODO: implement recoil in a smooth and cross-sided manner
	//		// Recoil
	//		//player.rotationPitch -= (descriptor.damage / 4) * (1 - bd.getBarrel().getVerticalRecoilReduction()) * (1 - bd.getGrip().getVerticalRecoilReduction());
	//		//player.rotationYaw += (descriptor.damage / 7 * world.rand.nextGaussian()) * (1 - bd.getBarrel().getHorizontalRecoilReduction()) * (1 - bd.getGrip().getHorizontalRecoilReduction());
	//
	//		return true;
	//	}

	//	private Pair<Integer, BlasterPowerPack> getAnotherPack(EntityPlayer player)
	//	{
	//		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
	//		{
	//			ItemStack s = player.inventory.getStackInSlot(i);
	//			BlasterPowerPack a = ItemBlasterPowerPack.getPackType(s);
	//			if (a == null)
	//				continue;
	//
	//			return new Pair<>(i, a);
	//		}
	//		return null;
	//	}

	@Override
	public boolean onItemLeftClick(ItemStack stack, World world, EntityPlayer player)
	{
		Vec3d look = player.getLookVec();
		float rangeIncrease = 0; //bd.getBarrel().getRangeIncrease();
		RaytraceHit hit = EntityUtils.rayTrace(look, descriptor.range + descriptor.range * rangeIncrease, player, new Entity[0], true);

		//		Sfx.play(player, Resources.modColon("swg.fx." + name), 1 + (float)world.rand.nextGaussian() / 10, 1 - bd.getBarrel().getNoiseReduction());

		Entity e = new EntityBlasterBolt(world, look, descriptor.damage, descriptor.boltColor);
		e.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		world.spawnEntity(e);

		if (hit instanceof RaytraceHitEntity && ((RaytraceHitEntity)hit).entity instanceof EntityLiving)
		{
			EntityLiving entity = (EntityLiving)((RaytraceHitEntity)hit).entity;
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), descriptor.damage);
		}
		return true;
	}
}
