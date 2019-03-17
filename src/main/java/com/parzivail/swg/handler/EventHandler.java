package com.parzivail.swg.handler;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.dimension.PlanetDescriptor;
import com.parzivail.swg.entity.EntityCinematicCamera;
import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.entity.ship.ShipData;
import com.parzivail.swg.force.Cron;
import com.parzivail.swg.force.ForcePowerDescriptor;
import com.parzivail.swg.gui.GuiNowEntering;
import com.parzivail.swg.gui.GuiScreenTrailer;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.item.blaster.ItemBlasterRifle;
import com.parzivail.swg.network.MessagePswgWorldDataSync;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.ForceRegistry;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.swg.render.force.RenderLightning;
import com.parzivail.swg.render.player.PRenderPlayer;
import com.parzivail.swg.render.worldext.RenderExtHealthBar;
import com.parzivail.swg.render.worldext.RenderExtLightsaberTrail;
import com.parzivail.swg.util.SwgEntityUtil;
import com.parzivail.swg.world.PswgWorldDataHandler;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.item.IGuiOverlay;
import com.parzivail.util.item.ILeftClickInterceptor;
import com.parzivail.util.item.IScreenShader;
import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitEntity;
import com.parzivail.util.render.decal.WorldDecals;
import com.parzivail.util.render.pipeline.JsonModelRenderer;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.FxMC;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.ShaderHelper;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

import java.util.EnumSet;

/**
 * Created by colby on 9/13/2017.
 */
public class EventHandler
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(InitGuiEvent.Pre event)
	{
		if (event.gui instanceof GuiMainMenu && !StarWarsGalaxy.config.getHasSeenIntroCrawl())
		{
			Lumberjack.debug("Showing intro crawl");
			Client.mc.displayGuiScreen(new GuiScreenTrailer());
			StarWarsGalaxy.config.setHasSeenIntroCrawl(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TextureStitchEvent.Post event)
	{
		//Client.saveTextureAtlas(event.map);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TextureStitchEvent.Pre event)
	{
		if (event.map.getTextureType() == 0)
		{
			JsonModelRenderer.loadTextures(event.map);
			Lumberjack.debug("Reloaded JSON texture map");
		}
	}

	@SubscribeEvent
	public void on(WorldEvent.Load loadEvent)
	{
		try
		{
			FileHandler.saveNbtMappings(loadEvent.world);
		}
		catch (Exception ignored)
		{
		}
	}

	@SubscribeEvent
	public void on(PlayerEvent.PlayerLoggedInEvent event)
	{
		StarWarsGalaxy.network.sendToAll(new MessagePswgWorldDataSync(PswgWorldDataHandler.get(event.player.worldObj)));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderTickEvent event)
	{
		if (event.phase != Phase.START)
			return;

		Client.renderPartialTicks = event.renderTickTime;
	}

	@SubscribeEvent
	public void on(LivingJumpEvent event)
	{
		if (WorldRegister.planetDescriptorHashMap.containsKey(event.entity.worldObj.provider.dimensionId))
		{
			PlanetDescriptor planetDescriptor = WorldRegister.planetDescriptorHashMap.get(event.entity.worldObj.provider.dimensionId);
			// TODO: make gravity just re-add some delta-Y to players on each tick
			//event.entity.motionY /= Math.sqrt(planetDescriptor.gravity);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Pre event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			EntityShip ship = SwgEntityUtil.getShipRiding(event.entity);
			if (ship != null)
			{
				ShipData data = ship.getData();
				if (data.isAirVehicle && event.isCancelable())
					event.setCanceled(true);
			}

			if (!event.isCanceled())
			{
				RenderExtLightsaberTrail.render((EntityPlayer)event.entity);
				RenderLightning.render((EntityPlayer)event.entity);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Post event)
	{
		if (ItemBlasterRifle.isHoldingBlaster(Client.mc.thePlayer))
		{
			RaytraceHit target = EntityUtils.rayTrace(Client.mc.thePlayer, 30);
			if (target instanceof RaytraceHitEntity)
			{
				Entity hitEntity = ((RaytraceHitEntity)target).entity;
				if (hitEntity == event.entity && hitEntity instanceof EntityLivingBase)
					RenderExtHealthBar.render((EntityLivingBase)hitEntity);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderWorldLastEvent event)
	{
		WorldDecals.render(Minecraft.getMinecraft().thePlayer.dimension, event.partialTicks);
		if (Client.mc.gameSettings.thirdPersonView == 0)
		{
			// RenderLivingEvent.Pre doesn't get called for the player in first person so we have to call some things manually
			RenderLightning.render(Client.mc.thePlayer);
			RenderExtLightsaberTrail.render(Client.mc.thePlayer);
		}

		RenderExtLightsaberTrail.tick();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(FOVUpdateEvent event)
	{
		ItemStack stack = event.entity.getHeldItem();
		if (stack != null && stack.getItem() instanceof PItem)
		{
			PItem i = ((PItem)stack.getItem());
			if (i.shouldUsePrecisionMovement(stack, event.entity.worldObj, event.entity))
				event.newfov = event.fov /* * 1.334f*/ * i.getZoomLevel(stack, event.entity.worldObj, event.entity);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderHandEvent event)
	{
		EntityPlayer player = Client.getPlayer();
		if (player != null)
		{
			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

			if (heldItem != null && heldItem.getItem() instanceof IGuiOverlay)
			{
				IGuiOverlay overlay = (IGuiOverlay)heldItem.getItem();
				if (overlay.shouldHideHand(Client.mc.thePlayer, heldItem) && event.isCancelable())
					event.setCanceled(true);
			}

			EntityShip ship = SwgEntityUtil.getShipRiding(player);
			if (ship != null && ship.shouldHideHand(player, null))
				event.setCanceled(true);
		}

		if (!event.isCanceled())
		{
			PRenderPlayer.instance.renderHand(event.partialTicks, event.renderPass);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void on(LivingFallEvent event)
	{
		if (disableForceJump(event.entity) && event.isCancelable())
			event.setCanceled(true);
	}

	@SubscribeEvent
	public void on(TickEvent.WorldTickEvent event)
	{
		if (event.world.provider.dimensionId == StarWarsGalaxy.config.getDimIdHoth())
			event.world.rainingStrength = 1;
	}

	@SubscribeEvent
	public void on(PlayerFlyableFallEvent event)
	{
		// No need to cancel, you're in Creative, so you're not taking fall damage
		disableForceJump(event.entity);
	}

	private boolean disableForceJump(Entity e)
	{
		if (e instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)e;
			if (Cron.isActive(player, ForceRegistry.fpJump))
			{
				Cron.deactivate(player, ForceRegistry.fpJump);
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderGameOverlayEvent.Pre event)
	{
		Client.guiQuestNotification.update();

		if (Client.getPlayer() != null)
		{
			EntityShip ship = SwgEntityUtil.getShipRiding(Client.mc.thePlayer);
			if (ship == null && Client.mc.renderViewEntity instanceof EntityCinematicCamera)
			{
				FxMC.changeCameraRoll(0);
				FxMC.changeCameraDist(4);
				Client.mc.renderViewEntity = Client.mc.thePlayer;
			}
			else if (ship != null)
			{
				ShipData data = ship.getData();

				float r = ship.orientation.getRoll();
				float pR = ship.previousOrientation.getRoll();
				while (r - pR < -180.0F)
					pR -= 360.0F;
				while (r - pR >= 180.0F)
					pR += 360.0F;
				FxMC.changeCameraRoll(r);
				FxMC.changePrevCameraRoll(pR);

				if (data.isAirVehicle)
				{
					Client.mc.renderViewEntity = ship.camera;
					FxMC.changeCameraDist(0);
				}
			}

			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

			Client.resolution = new ScaledResolution(Client.mc, Client.mc.displayWidth, Client.mc.displayHeight);

			if (event.type == ElementType.TEXT)
			{
				GuiNowEntering.draw(Client.mc.thePlayer);

				Client.hudLog.render();

				PswgExtProp props = PswgExtProp.get(Client.mc.thePlayer);
				ForcePowerDescriptor[] descs = props.getPowers();
				if (descs != null)
				{
					float h = Client.resolution.getScaledHeight();
					GL.PushMatrix();
					GL.PushAttrib(AttribMask.EnableBit);

					int n = 0;
					for (ForcePowerDescriptor desc : descs)
					{
						long nowTime = System.currentTimeMillis();
						long endTime = desc.getCooldownTime();
						long lenTime = ForceRegistry.fpJump.getCooldownLength(Client.mc.thePlayer.worldObj, Client.mc.thePlayer);
						double through = (endTime - nowTime) / (double)lenTime;

						if (!desc.isActive() && through <= 0)
							continue;

						n++;
						float r = (float)(10 * MathHelper.clamp_double(desc.isActive() ? 1 : through, 0, 1));

						GL.Disable(EnableCap.Texture2D);
						GL.Color(GLPalette.ALMOST_BLACK);
						Fx.D2.DrawSolidCircle(20, h - 20 * n, r + 1);
						GL.Color(GLPalette.WHITE);
						Fx.D2.DrawSolidCircle(20, h - 20 * n, r);

						GL.Enable(EnableCap.Texture2D);
						GL.PushMatrix();
						GL.Translate(25 + r, h - 20 * n - Client.mc.fontRendererObj.FONT_HEIGHT / 2f, 0);
						Client.mc.fontRendererObj.drawString(I18n.format(Resources.forceDot(desc.getId())), 0, 0, GLPalette.ALMOST_BLACK);
						GL.PopMatrix();
					}

					GL.PopAttrib();
					GL.PopMatrix();
				}
			}

			if (event.type == ElementType.CROSSHAIRS && Client.mc.gameSettings.showDebugInfo)
			{
				float l = Client.resolution.getScaledWidth();
				float i1 = Client.resolution.getScaledHeight();
				GL.PushMatrix();
				GL.Translate((l / 2f), (i1 / 2f), 0);
				Entity entity = Client.mc.renderViewEntity;
				int scalar = Client.mc.gameSettings.thirdPersonView == 2 ? -1 : 1;
				GL.Rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.partialTicks, -1.0F, 0.0F, 0.0F);
				GL.Rotate(scalar * (entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialTicks), 0.0F, 1.0F, 0.0F);
				if (scalar == -1)
					GL.Rotate(180, 0, 1, 0);
				GL.Scale(-1.0F, -1.0F, -1.0F);
				FxMC.renderDirections();
				GL.PopMatrix();

				if (event.isCancelable())
					event.setCanceled(true);
			}

			if (Client.mc.gameSettings.thirdPersonView == 0)
			{
				if (heldItem != null && heldItem.getItem() instanceof IScreenShader)
					ShaderHelper.framebufferShader = ((IScreenShader)heldItem.getItem()).requestShader(Client.mc.thePlayer, heldItem);
				else
					ShaderHelper.framebufferShader = 0;

				if (heldItem != null && heldItem.getItem() instanceof IGuiOverlay)
				{
					IGuiOverlay overlayProvider = (IGuiOverlay)heldItem.getItem();
					drawOverlay(event, heldItem, overlayProvider);
				}
			}
			else
				ShaderHelper.framebufferShader = 0;

			KeybindRegistry.keyAttack.setIntercepting(heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor);

			if (ship != null)
				drawOverlay(event, heldItem, ship);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void drawOverlay(RenderGameOverlayEvent.Pre event, ItemStack heldItem, IGuiOverlay overlayProvider)
	{
		if (event.type == ElementType.CROSSHAIRS && event.isCancelable())
			event.setCanceled(true);

		if (event.type == ElementType.TEXT)
		{
			GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit, AttribMask.PointBit));

			GL.PushMatrix();
			GL.Translate(Client.resolution.getScaledWidth_double() / 2, Client.resolution.getScaledHeight_double() / 2, 0);
			Client.mc.entityRenderer.disableLightmap(0);
			GL.Disable(EnableCap.Lighting);
			GL.Disable(EnableCap.Texture2D);
			GL.Enable(EnableCap.Blend);
			GL.Enable(EnableCap.PointSmooth);
			GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);

			overlayProvider.drawOverlay(Client.resolution, Client.mc.thePlayer, heldItem);

			GL.PopMatrix();
			GL11.glColor4f(1, 1, 1, 1);

			GL.PopAttrib();
		}
	}

	@SubscribeEvent
	public void on(PlayerTickEvent event)
	{
		if (event.phase != Phase.START)
			return;

		ItemStack stack = event.player.getHeldItem();
		if (stack != null && stack.getItem() instanceof PItem)
		{
			PItem i = ((PItem)stack.getItem());
			PItem.applyPrecisionMovement(event.player, i.shouldUsePrecisionMovement(stack, event.player.worldObj, event.player));
		}
		else
			PItem.applyPrecisionMovement(event.player, false);

		if (WorldRegister.planetDescriptorHashMap.containsKey(event.player.worldObj.provider.dimensionId) && !event.player.onGround && event.player.motionY < 0)
		{
			PlanetDescriptor planetDescriptor = WorldRegister.planetDescriptorHashMap.get(event.player.worldObj.provider.dimensionId);
			// TODO: make gravity just re-add some delta-Y to players on each tick
			//			if (planetDescriptor.gravity != 1)
			//				event.player.motionY += 0.08D * 0.98D * (1 - planetDescriptor.gravity);
		}

		if (event.player.worldObj.isRemote)
		{
			ItemStack heldItem = event.player.getHeldItem();
			StarWarsGalaxy.proxy.tickSounds(event.player, heldItem);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TickEvent.ClientTickEvent event)
	{
		if (event.phase != Phase.START)
			return;

		SoundHandler.tick(event);
		WorldDecals.tick(Client.getPlayer() == null ? 0 : Client.getPlayer().dimension);

		if (Client.leftClickDelayTimer > 0)
			Client.leftClickDelayTimer--;
		else
			Client.leftClickDelayTimer = 0;

		EntityPlayer player = Client.getPlayer();
		if (player != null)
		{
			ItemStack heldItem = player.getHeldItem();
			if (heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor)
				StarWarsGalaxy.proxy.checkLeftClickPressed(true);

			//			EntityShip ship = SwgEntityUtil.getShipRiding(player);
			//			if (ship != null)
			//			{
			//				StarWarsGalaxy.proxy.hudDebug("motionX", ship.motionX);
			//				StarWarsGalaxy.proxy.hudDebug("motionY", ship.motionY);
			//				StarWarsGalaxy.proxy.hudDebug("motionZ", ship.motionZ);
			//			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderPlayerEvent.Pre event)
	{
		if (event.isCancelable())
			event.setCanceled(true);

		//		// TODO: get updated models
		//		ModelTogrutaM.instance.inheritAngles(event.renderer.modelBipedMain);
		//
		//		event.renderer.modelBipedMain.bipedHead.isHidden = true;
		//		event.renderer.modelBipedMain.bipedHeadwear.isHidden = true;
		//		event.renderer.modelBipedMain.bipedBody.isHidden = true;
		//		event.renderer.modelBipedMain.bipedRightArm.isHidden = true;
		//		event.renderer.modelBipedMain.bipedLeftArm.isHidden = true;
		//		event.renderer.modelBipedMain.bipedRightLeg.isHidden = true;
		//		event.renderer.modelBipedMain.bipedLeftLeg.isHidden = true;
		//
		//		GL.PushMatrix();
		//		GL.Rotate(180, 1, 0, 0);
		//		GL.Rotate(MathUtil.interpolateRotation(event.entityPlayer.prevRenderYawOffset, event.entityPlayer.renderYawOffset, event.partialRenderTick), 0, 1, 0);
		//		GL.Translate(0, 0.2f, 0);
		//		ModelTogrutaM.instance.render(event.entityPlayer, 0, 0, 0, 0, 0, 1 / 16f);
		//		GL.PopMatrix();

		Vec3 viewPos = Client.mc.renderViewEntity.getPosition(event.partialRenderTick);
		Vec3 entityPos = event.entityPlayer.getPosition(event.partialRenderTick);
		PRenderPlayer.instance.renderPlayer((AbstractClientPlayer)event.entityPlayer, entityPos.xCoord - viewPos.xCoord, entityPos.yCoord - viewPos.yCoord, entityPos.zCoord - viewPos.zCoord, 0, event.partialRenderTick);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(KeyInputEvent event)
	{
		KeyHandler.onInput(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(MouseInputEvent event)
	{
		KeyHandler.onInput(event);
	}
}
