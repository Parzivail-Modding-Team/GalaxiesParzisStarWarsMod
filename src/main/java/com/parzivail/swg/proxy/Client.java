package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.EntityBlasterBolt;
import com.parzivail.swg.entity.EntitySmokeGrenade;
import com.parzivail.swg.entity.EntityThermalDetonator;
import com.parzivail.swg.entity.fx.ParticleSmoke;
import com.parzivail.swg.gui.GuiQuestNotification;
import com.parzivail.swg.item.ILeftClickInterceptor;
import com.parzivail.swg.mob.MobGizka;
import com.parzivail.swg.network.MessageItemLeftClick;
import com.parzivail.swg.npc.NpcJawa;
import com.parzivail.swg.npc.NpcMerchant;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.ItemRegister;
import com.parzivail.swg.registry.KeybindRegistry;
import com.parzivail.swg.render.PEntityRenderer;
import com.parzivail.swg.render.RenderBasicTileItem;
import com.parzivail.swg.render.antenna.RenderSatelliteDish;
import com.parzivail.swg.render.binoculars.RenderMacrobinoculars;
import com.parzivail.swg.render.entity.RenderBlasterBolt;
import com.parzivail.swg.render.entity.RenderNothing;
import com.parzivail.swg.render.entity.RenderT65;
import com.parzivail.swg.render.gunrack.RenderGunRack;
import com.parzivail.swg.render.mob.RenderGizka;
import com.parzivail.swg.render.npc.RenderJawa;
import com.parzivail.swg.render.npc.RenderMerchant;
import com.parzivail.swg.render.pipeline.JsonBlockRenderer;
import com.parzivail.swg.render.util.EntityRenderDroppedItem;
import com.parzivail.swg.render.weapon.*;
import com.parzivail.swg.render.weapon.grenades.RenderSmokeGrenade;
import com.parzivail.swg.render.weapon.grenades.RenderThermalDetonator;
import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.swg.ship.VehicleT65;
import com.parzivail.swg.tile.TileGunRack;
import com.parzivail.swg.tile.antenna.TileSatelliteDish;
import com.parzivail.swg.world.PswgWorldDataHandler;
import com.parzivail.util.block.INameProvider;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.entity.EntityUtils;
import com.parzivail.util.ui.PFramebuffer;
import com.parzivail.util.ui.ShaderHelper;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.commons.io.FileUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	//public static FontRenderer frSansSerif;
	//public static FontRenderer frSerif;
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

	private static FontRenderer createFont(String file)
	{
		FontRenderer renderer = new FontRenderer(mc.gameSettings, Resources.location(String.format("textures/font/%s.png", file)), mc.getTextureManager(), false);
		((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(renderer);
		return renderer;
	}

	public static boolean doesPlayerExist()
	{
		return mc != null && mc.thePlayer != null;
	}

	public void checkLeftClickPressed(boolean selfReported)
	{
		if (leftClickDelayTimer > 0)
			return;

		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if (heldItem == null || !(heldItem.getItem() instanceof ILeftClickInterceptor))
			return;
		ILeftClickInterceptor item = ((ILeftClickInterceptor)heldItem.getItem());

		boolean risingEdge = KeybindRegistry.keyAttack.interceptedIsPressed();
		boolean holding = KeybindRegistry.keyAttack.getInterceptedIsKeyPressed();

		boolean pressed = item.isLeftClickRepeatable() ? (selfReported && (risingEdge || holding)) : risingEdge;

		if (item.isLeftClickRepeatable())
			while (KeybindRegistry.keyAttack.interceptedIsPressed())
				; // tick down the press count, if there is any

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

		ReflectionHelper.setPrivateValue(Minecraft.class, mc, new PFramebuffer(mc.displayWidth, mc.displayHeight, true), "framebufferMc", "field_147124_at", "au");

		guiQuestNotification = new GuiQuestNotification();

		//frSansSerif = createFont("sansserif");
		//frSerif = createFont("serif");

		frNaboo = createFont("naboo");
		frAurebesh = createFont("aurebesh");
		frDroid = createFont("droid");
		frEwok = createFont("ewok");
		frHuttese = createFont("huttese");
		frMassassi = createFont("massassi");

		RenderingRegistry.registerEntityRenderingHandler(VehicleT65.class, new RenderT65());
		//RenderingRegistry.registerEntityRenderingHandler(Seat.class, new RenderNothing());
		RenderingRegistry.registerEntityRenderingHandler(EntityBlasterBolt.class, new RenderBlasterBolt());

		RenderingRegistry.registerEntityRenderingHandler(MultipartFlightModel.class, new RenderNothing());

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

		MinecraftForgeClient.registerItemRenderer(ItemRegister.binocularsMb450, new RenderMacrobinoculars());

		ClientRegistry.bindTileEntitySpecialRenderer(TileGunRack.class, new RenderGunRack());
		ClientRegistry.bindTileEntitySpecialRenderer(TileSatelliteDish.class, new RenderSatelliteDish());

		registerBasicTileItem(BlockRegister.gunRack, 0.8f);
		registerBasicTileItem(BlockRegister.satelliteDish, 0.4f);

		Lumberjack.log("Client proxy loaded!");
	}

	@Override
	public <T extends Block & INameProvider> void registerModel(T block)
	{
		RenderingRegistry.registerBlockHandler(new JsonBlockRenderer(block, Resources.location(String.format("models/block/%s.json", block.getName()))));
	}

	public static void saveTextureAtlas(TextureMap map)
	{
		// this.displayWidth, this.displayHeight, this.framebufferMc
		try
		{
			int texId = ReflectionHelper.getPrivateValue(AbstractTexture.class, map, "glTextureId", "field_110553_a", "a");

			int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
			int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

			File file2 = new File(mc.mcDataDir, "atlases");
			if (!file2.mkdir() && map.getTextureType() == 0)
				FileUtils.cleanDirectory(file2);

			int k = width * height;

			IntBuffer pixelBuffer = BufferUtils.createIntBuffer(k);
			int[] pixelValues = new int[k];

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

			pixelBuffer.get(pixelValues);
			TextureUtil.func_147953_a(pixelValues, width, height);
			BufferedImage bufferedimage = null;

			bufferedimage = new BufferedImage(width, height, 1);
			bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);

			File file3;

			file3 = getTimestampedPNGFileForDirectory(file2);

			ImageIO.write(bufferedimage, "png", file3);
		}
		catch (Exception exception)
		{
		}
	}

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private static File getTimestampedPNGFileForDirectory(File p_74290_0_)
	{
		String s = dateFormat.format(new Date());
		int i = 1;

		while (true)
		{
			File file2 = new File(p_74290_0_, s + (i == 1 ? "" : "_" + i) + ".png");

			if (!file2.exists())
			{
				return file2;
			}

			++i;
		}
	}

	private void registerBasicTileItem(Block block, float scale)
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
	public void handlePlayerDataSync(int entityId, NBTTagCompound ieep)
	{
		World w = mc.theWorld;
		if (w == null)
		{
			Lumberjack.warn("Recieved null world for MessagePswgExtPropSync::handleMessage");
			return;
		}
		else
			Lumberjack.info("Created player properties");
		Entity e = w.getEntityByID(entityId);
		PswgExtProp.get(e).loadNBTData(ieep);
	}

	@Override
	public boolean isClientControlled(MultipartFlightModel query)
	{
		MultipartFlightModel ship = EntityUtils.getShipRiding(mc.thePlayer);
		return ship != null && ship.equals(query) && ship.isControlling(mc.thePlayer);
	}
}
