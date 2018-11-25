package com.parzivail.swg.render.overlay;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class OverlayHealthBar
{
	public static final OverlayHealthBar instance = new OverlayHealthBar();

	public void render(EntityLivingBase e)
	{
		Minecraft m = Minecraft.getMinecraft();

		/*
			Setup
		 */
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glPushAttrib(GL11.GL_LINE_BIT);

		GL.PushMatrix();
		GL11.glColor4f(1, 1, 1, 1);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		m.entityRenderer.disableLightmap(0);

		GL.Disable(EnableCap.Lighting);
		GL.Disable(EnableCap.Blend);
		GL.Disable(EnableCap.Texture2D);

		Vec3 playerPos = m.thePlayer.getPosition(Client.renderPartialTicks);
		GL.Translate(e.posX - playerPos.xCoord, e.posY - playerPos.yCoord, e.posZ - playerPos.zCoord);

		/*
			AABB render
		 */
		GL11.glLineWidth(4);
		GL11.glColor4f(0, 0, 0, 1);

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

		/*
			2D info render
		 */
		GL11.glRotatef(-RenderManager.instance.playerViewY, 0, 1, 0);

		GL11.glLineWidth(2);

		GL11.glColor4f(1, 0, 0, 1);
		Fx.D2.DrawSolidRectangle(-e.width / 4, e.height + 0.2f, (e.width / 2) * e.getHealth() / e.getMaxHealth(), 0.1f);

		GL11.glColor4f(0, 0, 0, 1);
		Fx.D2.DrawWireRectangle(-e.width / 4, e.height + 0.2f, e.width / 2, 0.1f);

		GL.PopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		m.entityRenderer.enableLightmap(0);
	}
}
