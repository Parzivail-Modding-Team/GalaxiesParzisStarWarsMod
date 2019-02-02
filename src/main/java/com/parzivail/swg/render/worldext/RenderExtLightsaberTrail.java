package com.parzivail.swg.render.worldext;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class RenderExtLightsaberTrail
{
	private static final HashMap<EntityPlayer, LightsaberTrail> PLAYER_LIGHTSABER_TRAIL_MAP = new HashMap<>();

	public static void addTrailComponent(EntityPlayer player, int bladeColor, int coreColor, float bladeLength, Vector3f pBase, Vector3f pEnd)
	{
		LightsaberTrail trail;
		if (!PLAYER_LIGHTSABER_TRAIL_MAP.containsKey(player))
			PLAYER_LIGHTSABER_TRAIL_MAP.put(player, trail = new LightsaberTrail());
		else
			trail = PLAYER_LIGHTSABER_TRAIL_MAP.get(player);

		trail.addPointSet(bladeColor, coreColor, bladeLength, 60, pBase, pEnd);
	}

	public static void render(EntityPlayer player)
	{
		// TODO: move into static rendering class
		if (!PLAYER_LIGHTSABER_TRAIL_MAP.containsKey(player))
			return;
		LightsaberTrail trail = PLAYER_LIGHTSABER_TRAIL_MAP.get(player);

		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Texture2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL.PushMatrix();
		Vec3 playerPos = Client.mc.renderViewEntity.getPosition(Client.renderPartialTicks);
		GL.Translate(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);

		GL11.glDepthMask(false);
		trail.render();
		GL11.glDepthMask(true);

		GL.PopMatrix();
		GL.PopAttrib();
	}

	public static void tick()
	{
		for (HashMap.Entry<EntityPlayer, LightsaberTrail> entry : PLAYER_LIGHTSABER_TRAIL_MAP.entrySet())
			entry.getValue().tick();
	}
}
