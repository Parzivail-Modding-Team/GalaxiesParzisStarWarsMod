package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.entity.EntityCamera;
import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.register.KeybindRegister;
import com.parzivail.swg.render.RenderShip;
import com.parzivail.util.jsonpipeline.BlockbenchModelLoader;
import com.parzivail.util.jsonpipeline.BlockbenchWeightedModelLoader;
import com.parzivail.util.jsonpipeline.ModelLocationInformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SwgClientProxy extends SwgProxy
{
	public static Minecraft mc;
	public static boolean shipInputRollMode = false;
	public static EntityCamera entityCamera;

	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		mc = Minecraft.getMinecraft();
		ModelLoaderRegistry.registerLoader(new BlockbenchModelLoader(modelLocation -> (Resources.MODID.equals(modelLocation.getResourceDomain()) && !(modelLocation instanceof ModelResourceLocation))));
		ModelLoaderRegistry.registerLoader(new BlockbenchWeightedModelLoader(modelLocation -> (Resources.MODID.equals(modelLocation.getResourceDomain()) && modelLocation instanceof ModelResourceLocation)));

		RenderingRegistry.registerEntityRenderingHandler(EntityShip.class, RenderShip::new);
	}

	@Override
	public void init(FMLInitializationEvent e)
	{
		super.init(e);
		ModelLocationInformation.init(mc.getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper());
	}

	@Override
	public void postInit(FMLPostInitializationEvent e)
	{
		super.postInit(e);
		KeybindRegister.register();
	}

	@Override
	public void registerItemRenderer(Item item, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Resources.modColon(id), "inventory"));
	}

	public void captureShipInput(EntityPlayer pilot, EntityShip entityShip)
	{
		MouseHelper mouseHelper = mc.mouseHelper;
		float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		float f1 = f * f * f * 8.0F;
		entityShip.setInputsClient(mouseHelper.deltaX * f1, mouseHelper.deltaY * f1, shipInputRollMode, mc.gameSettings.keyBindJump.isKeyDown());
	}

	public MovementInput getMovementInput(EntityPlayer player)
	{
		if (player instanceof EntityPlayerSP)
			return ((EntityPlayerSP)player).movementInput;
		return null;
	}

	@Override
	public void notifyPlayer(ITextComponent message, boolean actionBar)
	{
		mc.player.sendStatusMessage(message, actionBar);
	}

	@Override
	public void createShipCamera(Entity target)
	{
		destroyShipCamera();

		entityCamera = new EntityCamera(target.world);
		entityCamera.setTarget(target);
		entityCamera.copyLocationAndAnglesFrom(target);
		entityCamera.world.spawnEntity(entityCamera);
	}

	@Override
	public void destroyShipCamera()
	{
		if (entityCamera == null)
			return;

		entityCamera.world.removeEntity(entityCamera);
		entityCamera = null;
	}
}
