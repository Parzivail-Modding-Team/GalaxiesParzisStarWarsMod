package com.parzivail.swg.handler;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.audio.AmbientSounds;
import com.parzivail.swg.dimension.PlanetDescriptor;
import com.parzivail.swg.entity.EntityCinematicCamera;
import com.parzivail.swg.force.Cron;
import com.parzivail.swg.gui.GuiNowEntering;
import com.parzivail.swg.gui.GuiScreenTrailer;
import com.parzivail.swg.item.IGuiOverlay;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.item.IScreenShader;
import com.parzivail.swg.item.PItem;
import com.parzivail.swg.item.blaster.ItemBlasterRifle;
import com.parzivail.swg.network.MessagePswgWorldDataSync;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.ForceRegistry;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.registry.WorldRegister;
import com.parzivail.swg.render.decal.WorldDecals;
import com.parzivail.swg.render.force.RenderLightning;
import com.parzivail.swg.render.overlay.OverlayHealthBar;
import com.parzivail.swg.render.pipeline.JsonModelRenderer;
import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.swg.world.PswgWorldDataHandler;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.math.RaytraceHit;
import com.parzivail.util.math.RaytraceHitEntity;
import com.parzivail.util.ui.FxMC;
import com.parzivail.util.ui.ShaderHelper;
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
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.lwjgl.opengl.GL11;

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
			JsonModelRenderer.loadTextures(event.map);
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
			event.entity.motionY /= Math.sqrt(planetDescriptor.gravity);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Pre event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			MultipartFlightModel ship = EntityUtils.getShipRiding(event.entity);
			if (ship != null && event.isCancelable())
				event.setCanceled(true);

			RenderLightning.render((EntityPlayer)event.entity);
		}
		//		else if (event.entity instanceof EntityLiving && ClientRenderState.renderState.contains(ClientRenderState.SniperThermal))
		//			ShaderHelper.useShader(ShaderHelper.entityGlow);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(RenderLivingEvent.Post event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			MultipartFlightModel ship = EntityUtils.getShipRiding(event.entity);
			if (ship != null && event.isCancelable())
				event.setCanceled(true);

			Client.renderLightsaberTrail((EntityPlayer)event.entity);
		}
		//		else if (event.entity instanceof EntityLiving && ClientRenderState.renderState.contains(ClientRenderState.SniperThermal))
		//			ShaderHelper.releaseShader();

		if (ItemBlasterRifle.isHoldingBlaster(Client.mc.thePlayer))
		{
			RaytraceHit target = EntityUtils.rayTrace(Client.mc.thePlayer, 30);
			if (target instanceof RaytraceHitEntity)
			{
				Entity hitEntity = ((RaytraceHitEntity)target).entity;
				if (hitEntity == event.entity && hitEntity instanceof EntityLivingBase)
					OverlayHealthBar.instance.render((EntityLivingBase)hitEntity);
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
			Client.renderLightsaberTrail(Client.mc.thePlayer);
		}

		Client.tickLightsaberTrails();
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
		if (Client.mc.thePlayer != null)
		{
			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

			if (heldItem != null && heldItem.getItem() instanceof IGuiOverlay)
			{
				IGuiOverlay overlay = (IGuiOverlay)heldItem.getItem();
				if (overlay.shouldHideHand(Client.mc.thePlayer, heldItem) && event.isCancelable())
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void on(LivingFallEvent event)
	{
		if (disableForceJump(event.entity) && event.isCancelable())
			event.setCanceled(true);
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

		if (Client.mc.thePlayer != null)
		{
			MultipartFlightModel ship = EntityUtils.getShipRiding(Client.mc.thePlayer);
			if (ship == null && Client.mc.renderViewEntity instanceof EntityCinematicCamera)
			{
				FxMC.changeCameraRoll(0);
				Client.mc.renderViewEntity = Client.mc.thePlayer;
			}
			else if (ship != null)
			{
				float r = ship.orientation.getRoll();
				FxMC.changeCameraRoll(r);
				Client.mc.renderViewEntity = ship.camera;
			}

			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();

			Client.resolution = new ScaledResolution(Client.mc, Client.mc.displayWidth, Client.mc.displayHeight);

			if (event.type == ElementType.TEXT)
			{
				GuiNowEntering.draw(Client.mc.thePlayer);

				//				if (Client.mc.objectMouseOver != null && Client.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
				//				{
				//					int damage = Client.mc.theWorld.getBlockMetadata(Client.mc.objectMouseOver.blockX, Client.mc.objectMouseOver.blockY, Client.mc.objectMouseOver.blockZ);
				//					GL.PushMatrix();
				//					GL.Scale(0.5);
				//					Client.mc.fontRendererObj.drawStringWithShadow(String.format("Metadata: %s", damage), Client.resolution.getScaledWidth(), Client.resolution.getScaledHeight() - Client.mc.fontRendererObj.FONT_HEIGHT, GLPalette.WHITE);
				//					GL.PopMatrix();
				//				}
			}

			if (event.type == ElementType.CROSSHAIRS && Client.mc.gameSettings.showDebugInfo)
			{
				float l = Client.resolution.getScaledWidth();
				float i1 = Client.resolution.getScaledHeight();
				GL.PushMatrix();
				GL.Translate((l / 2f), (i1 / 2f), 0);
				Entity entity = Client.mc.renderViewEntity;
				GL.Rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.partialTicks, -1.0F, 0.0F, 0.0F);
				GL.Rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * event.partialTicks, 0.0F, 1.0F, 0.0F);
				GL.Scale(-1.0F, -1.0F, -1.0F);
				FxMC.renderDirections();
				GL.PopMatrix();

				if (event.isCancelable())
					event.setCanceled(true);
			}

			if (heldItem != null && heldItem.getItem() instanceof IScreenShader)
				ShaderHelper.framebufferShader = ((IScreenShader)heldItem.getItem()).requestShader(Client.mc.thePlayer, heldItem);
			else
				ShaderHelper.framebufferShader = 0;

			if (heldItem != null && heldItem.getItem() instanceof IGuiOverlay)
			{
				if (event.type == ElementType.CROSSHAIRS && event.isCancelable())
					event.setCanceled(true);

				if (event.type == ElementType.TEXT)
				{
					GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
					GL11.glPushAttrib(GL11.GL_LINE_BIT);
					GL11.glPushAttrib(GL11.GL_POINT_BIT);

					GL.PushMatrix();
					GL.Translate(Client.resolution.getScaledWidth_double() / 2, Client.resolution.getScaledHeight_double() / 2, 0);
					Client.mc.entityRenderer.disableLightmap(0);
					GL.Disable(EnableCap.Lighting);
					GL.Disable(EnableCap.Texture2D);
					GL.Enable(EnableCap.Blend);
					GL.Enable(EnableCap.PointSmooth);
					GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);

					((IGuiOverlay)heldItem.getItem()).drawOverlay(Client.resolution, Client.mc.thePlayer, heldItem);

					GL.PopMatrix();
					GL11.glColor4f(1, 1, 1, 1);

					GL11.glPopAttrib();
					GL11.glPopAttrib();
					GL11.glPopAttrib();
				}
			}

			KeybindRegistry.keyAttack.setIntercepting(heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor);
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
			if (planetDescriptor.gravity != 1)
				event.player.motionY += 0.08D * 0.9800000190734863D * (1 - planetDescriptor.gravity);
		}

		if (event.player.worldObj.isRemote)
		{
			ItemStack heldItem = event.player.getHeldItem();
			StarWarsGalaxy.proxy.tickLightsaberSounds(event.player, heldItem);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void on(TickEvent.ClientTickEvent event)
	{
		if (event.phase != Phase.START)
			return;

		AmbientSounds.tick(event);
		WorldDecals.tick(Client.getPlayer() == null ? 0 : Client.getPlayer().dimension);

		if (Client.leftClickDelayTimer > 0)
			Client.leftClickDelayTimer--;
		else
			Client.leftClickDelayTimer = 0;

		if (Client.doesPlayerExist())
		{
			ItemStack heldItem = Client.mc.thePlayer.getHeldItem();
			if (heldItem != null && heldItem.getItem() instanceof ILeftClickInterceptor)
				StarWarsGalaxy.proxy.checkLeftClickPressed(true);
		}
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
