package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.*;
import com.parzivail.swg.entity.fx.ParticleSmoke;
import com.parzivail.swg.gui.GuiQuestNotification;
import com.parzivail.swg.handler.KeyHandler;
import com.parzivail.swg.item.lightsaber.ItemLightsaber;
import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.mob.MobGizka;
import com.parzivail.swg.npc.NpcJawa;
import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.antenna.RenderSatelliteDish;
import com.parzivail.swg.render.binoculars.RenderMacrobinoculars;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderDebug;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.render.gunrack.RenderGunRack;
import com.parzivail.swg.render.lightsaber.RenderLightsaber;
import com.parzivail.swg.render.mob.RenderGizka;
import com.parzivail.swg.render.npc.RenderJawa;
import com.parzivail.swg.render.npc.RenderMerchant;
import com.parzivail.swg.render.weapon.*;
import com.parzivail.swg.render.weapon.grenades.RenderSmokeGrenade;
import com.parzivail.swg.render.weapon.grenades.RenderThermalDetonator;
import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.swg.tile.TileGunRack;
import com.parzivail.swg.tile.antenna.TileSatelliteDish;
import com.parzivail.swg.util.SwgEntityUtil;
import com.parzivail.swg.world.PswgWorldDataHandler;
import com.parzivail.util.audio.ClientSoundHandler;
import com.parzivail.util.block.INameProvider;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.item.ILeftClickInterceptor;
import com.parzivail.util.math.BufferMatrix;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.network.MessageItemLeftClick;
import com.parzivail.util.render.EntityRenderDroppedItem;
import com.parzivail.util.render.PEntityRenderer;
import com.parzivail.util.render.RenderBasicTileItem;
import com.parzivail.util.render.pipeline.JsonBlockRenderer;
import com.parzivail.util.ui.PFramebuffer;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	public static Minecraft mc;
	public static FontRenderer frNaboo;
	public static FontRenderer frAurebesh;
	public static FontRenderer frDroid;
	public static FontRenderer frEwok;
	public static FontRenderer frHuttese;
	public static FontRenderer frMassassi;

	public static int leftClickDelayTimer;

	public static float renderPartialTicks;

	public static ScaledResolution resolution;

	public static GuiQuestNotification guiQuestNotification;

	private static final FloatBuffer l2WTempInputBuffer1 = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer l2WTempInputBuffer2 = GLAllocation.createDirectFloatBuffer(16);
	private static final float[] l2WTempMatrixArray = new float[16];
	private static final FloatBuffer l2WTempOutputBuffer = FloatBuffer.allocate(16);

	/**
	 * Creates a Minecraft FontRenderer for the given font asset name
	 *
	 * @param file Font asset name
	 *
	 * @return A new FontRenderer
	 */
	@SideOnly(Side.CLIENT)
	private static FontRenderer createFont(String file)
	{
		FontRenderer renderer = new FontRenderer(mc.gameSettings, Resources.location(String.format("textures/font/%s.png", file)), mc.getTextureManager(), false);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(renderer);
		return renderer;
	}

	/**
	 * Gets the current absolute world position represented by the local GL model matrix
	 *
	 * @return Absolute world position
	 */
	@SideOnly(Side.CLIENT)
	public static Vector3f getLocalToWorldPos()
	{
		l2WTempInputBuffer1.clear();
		l2WTempInputBuffer2.clear();
		l2WTempOutputBuffer.clear();

		FloatBuffer camMat = ObfuscationReflectionHelper.getPrivateValue(ActiveRenderInfo.class, null, "modelview", "field_74594_j", "j");
		BufferMatrix.invertMatrix(camMat, l2WTempInputBuffer2);

		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, l2WTempInputBuffer1);
		BufferMatrix.multiply(l2WTempInputBuffer1, l2WTempInputBuffer2, l2WTempOutputBuffer);
		l2WTempOutputBuffer.get(l2WTempMatrixArray);
		Vec3 playerPos = mc.renderViewEntity.getPosition(renderPartialTicks);
		return new Vector3f(l2WTempMatrixArray[12] + (float)playerPos.xCoord, l2WTempMatrixArray[13] + (float)playerPos.yCoord, l2WTempMatrixArray[14] + (float)playerPos.zCoord);
	}

	/**
	 * Tests if the player exists
	 * @return True if the client player has been initialized
	 */
	@SideOnly(Side.CLIENT)
	public static boolean doesPlayerExist()
	{
		return mc != null && mc.thePlayer != null;
	}

	/**
	 * Gets the client player or null if Minecraft or the player does not exist
	 * @return A client player
	 */
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getPlayer()
	{
		if (!doesPlayerExist())
			return null;
		return mc.thePlayer;
	}

	/**
	 * Queries the "attack" keybind and processes items who consume the associated action
	 *
	 * @param passive False if the event was triggered by a keybind or mouse click, true if it was triggered by the tick handler
	 */
	public void checkLeftClickPressed(boolean passive)
	{
		if (leftClickDelayTimer > 0)
			return;

		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if (heldItem == null || !(heldItem.getItem() instanceof ILeftClickInterceptor))
			return;
		ILeftClickInterceptor item = ((ILeftClickInterceptor)heldItem.getItem());

		boolean risingEdge = KeybindRegistry.keyAttack.interceptedIsPressed();
		boolean holding = KeybindRegistry.keyAttack.getInterceptedIsKeyPressed();

		boolean pressed = item.isLeftClickRepeatable() ? (passive && (risingEdge || holding)) : risingEdge;

		if (item.isLeftClickRepeatable())
			while (KeybindRegistry.keyAttack.interceptedIsPressed())
				;

		if (pressed)
		{
			leftClickDelayTimer = 2;
			item.onItemLeftClick(heldItem, mc.thePlayer.worldObj, mc.thePlayer);
			StarWarsGalaxy.network.sendToServer(new MessageItemLeftClick(mc.thePlayer));
		}
	}

	@Override
	public void init()
	{
		mc = Minecraft.getMinecraft();

		ShaderHelper.initShaders();

		mc.entityRenderer = new PEntityRenderer(mc, mc.getResourceManager());

		ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, new PFramebuffer(mc.displayWidth, mc.displayHeight, true), "framebufferMc", "field_147124_at", "au");

		guiQuestNotification = new GuiQuestNotification();

		frNaboo = createFont("naboo");
		frAurebesh = createFont("aurebesh");
		frDroid = createFont("droid");
		frEwok = createFont("ewok");
		frHuttese = createFont("huttese");
		frMassassi = createFont("massassi");

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		//RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlasterBolt.class, new RenderBlasterBolt());

		RenderingRegistry.registerEntityRenderingHandler(EntityChair.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityShipParentTest.class, new RenderDebug());

		RenderingRegistry.registerEntityRenderingHandler(NpcMerchant.class, new RenderMerchant());
		RenderingRegistry.registerEntityRenderingHandler(NpcJawa.class, new RenderJawa());

		RenderingRegistry.registerEntityRenderingHandler(MobGizka.class, new RenderGizka());

		RenderingRegistry.registerEntityRenderingHandler(EntitySmokeGrenade.class, new EntityRenderDroppedItem(new RenderSmokeGrenade(), new ItemStack(ItemRegister.grenadeSmoke)));
		RenderingRegistry.registerEntityRenderingHandler(EntityThermalDetonator.class, new EntityRenderDroppedItem(new RenderThermalDetonator(), new ItemStack(ItemRegister.grenadeThermalDetonator)));

		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleA280, new RenderA280());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleBowcaster, new RenderBowcaster());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleCycler, new RenderCycler());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDefender, new RenderDefender());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDh17, new RenderDH17());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDl18, new RenderDL18());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDl21, new RenderDL21());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleDlt19, new RenderDlt19());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleE11, new RenderE11());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleIonization, new RenderIonization());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleRt97c, new RenderRT97C());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleScout, new RenderScout());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleSe14c, new RenderSE14C());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.rifleT21, new RenderT21());

		MinecraftForgeClient.registerItemRenderer(ItemRegister.grenadeSmoke, new RenderSmokeGrenade());
		MinecraftForgeClient.registerItemRenderer(ItemRegister.grenadeThermalDetonator, new RenderThermalDetonator());

		MinecraftForgeClient.registerItemRenderer(ItemRegister.lightsaber, new RenderLightsaber(Resources.location("models/item/lightsaber/luke.json")));

		MinecraftForgeClient.registerItemRenderer(ItemRegister.binocularsMb450, new RenderMacrobinoculars());

		ClientRegistry.bindTileEntitySpecialRenderer(TileGunRack.class, new RenderGunRack());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSatelliteDish.class, new RenderSatelliteDish());

		registerBasicTileItemRenderer(BlockRegister.gunRack, 0.8f);
		registerBasicTileItemRenderer(BlockRegister.satelliteDish, 0.4f);

		Lumberjack.log("Client proxy loaded!");
	}

	@Override
	public void tickLightsaberSounds(EntityPlayer player, ItemStack heldItem)
	{
		if (heldItem != null && heldItem.getItem() instanceof ItemLightsaber)
		{
			LightsaberData ld = new LightsaberData(heldItem);
			if (ld.isOpen)
				ClientSoundHandler.playLightsaberSound(player);
			else
				ClientSoundHandler.stopLightsaberSound(player);
		}
		else
			ClientSoundHandler.stopLightsaberSound(player);
	}

	@Override
	public <T extends Block & INameProvider> void registerBlockModel(T block)
	{
		RenderingRegistry.registerBlockHandler(new JsonBlockRenderer(block, Resources.location(String.format("models/block/%s.json", block.getName()))));
	}

	@SideOnly(Side.CLIENT)
	private void registerBasicTileItemRenderer(Block block, float scale)
	{
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), new RenderBasicTileItem(block, scale));
	}

	@Override
	public void postInit()
	{
		KeybindRegistry.registerAll();
	}

	@Override
	public boolean isServer()
	{
		return false;
	}

	@Override
	public Entity getEntityById(int dim, int id)
	{
		return Minecraft.getMinecraft().theWorld.getEntityByID(id);
	}

	@Override
	public void spawnSmokeParticle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
	{
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSmoke(world, x, y, z, velocityX, velocityY, velocityZ));
	}

	@Override
	public void handleWorldDataSync(NBTTagCompound worldData)
	{
		PswgWorldDataHandler.get(mc.theWorld).readFromNBT(worldData);
	}

	@Override
	public void handleVehicleMovement()
	{
		KeyHandler.handleVehicleMovement();
	}

	@Override
	public void handlePlayerDataSync(int entityId, NBTTagCompound ieep)
	{
		World w = mc.theWorld;
		if (w == null)
		{
			Lumberjack.warn("Recieved null world for MessagePswgExtPropSync::handleMessage");
			return;
		}
		Entity e = w.getEntityByID(entityId);
		PswgExtProp props = PswgExtProp.get(e);
		props.loadNBTData(ieep);
	}

	@Override
	public boolean isClientControlled(MultipartFlightModel query)
	{
		MultipartFlightModel ship = SwgEntityUtil.getShipRiding(mc.thePlayer);
		return ship != null && ship.equals(query) && ship.isControlling(mc.thePlayer);
	}
}
