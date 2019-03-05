package com.parzivail.util.render;

import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Skybox
{
	public static void render(Minecraft mc, ResourceLocation texture)
	{
		GL.PushMatrix();
		GL.PushAttrib(AttribMask.EnableBit);

		GL.Enable(EnableCap.Texture2D);
		mc.renderEngine.bindTexture(texture);

		float thrd = 1 / 3f;
		float frth = 1 / 4f;

		/*

		Mapping
		*---*---*---*---*
		|   | U |   |   |
		*---*---*---*---*
		| N | E | S | W |
		*---*---*---*---*
		|   | D |   |   |
		*---*---*---*---*

		Winding
		0---3
		|   |
		1---2
		 */

		//GL.Rotate(Fx.Util.HzPercent(0.5f) * 360, 0, 0, 1);
		GL.Scale(256);

		GL.Begin(PrimitiveType.Quads);

		// North
		GL.TexCoord2(0, thrd);
		GL.Vertex3(-1, 1, -1);
		GL.TexCoord2(0, 2 * thrd);
		GL.Vertex3(-1, -1, -1);
		GL.TexCoord2(frth, 2 * thrd);
		GL.Vertex3(1, -1, -1);
		GL.TexCoord2(frth, thrd);
		GL.Vertex3(1, 1, -1);

		// East
		GL.TexCoord2(frth, thrd);
		GL.Vertex3(1, 1, -1);
		GL.TexCoord2(frth, 2 * thrd);
		GL.Vertex3(1, -1, -1);
		GL.TexCoord2(2 * frth, 2 * thrd);
		GL.Vertex3(1, -1, 1);
		GL.TexCoord2(2 * frth, thrd);
		GL.Vertex3(1, 1, 1);

		// South
		GL.TexCoord2(2 * frth, thrd);
		GL.Vertex3(1, 1, 1);
		GL.TexCoord2(2 * frth, 2 * thrd);
		GL.Vertex3(1, -1, 1);
		GL.TexCoord2(3 * frth, 2 * thrd);
		GL.Vertex3(-1, -1, 1);
		GL.TexCoord2(3 * frth, thrd);
		GL.Vertex3(-1, 1, 1);

		// West
		GL.TexCoord2(3 * frth, thrd);
		GL.Vertex3(-1, 1, 1);
		GL.TexCoord2(3 * frth, 2 * thrd);
		GL.Vertex3(-1, -1, 1);
		GL.TexCoord2(4 * frth, 2 * thrd);
		GL.Vertex3(-1, -1, -1);
		GL.TexCoord2(4 * frth, thrd);
		GL.Vertex3(-1, 1, -1);

		// Up
		GL.TexCoord2(frth, 0);
		GL.Vertex3(-1, 1, -1);
		GL.TexCoord2(frth, thrd);
		GL.Vertex3(1, 1, -1);
		GL.TexCoord2(2 * frth, thrd);
		GL.Vertex3(1, 1, 1);
		GL.TexCoord2(2 * frth, 0);
		GL.Vertex3(-1, 1, 1);

		// Down
		GL.TexCoord2(frth, 2 * thrd);
		GL.Vertex3(1, -1, -1);
		GL.TexCoord2(frth, 1);
		GL.Vertex3(-1, -1, -1);
		GL.TexCoord2(2 * frth, 1);
		GL.Vertex3(-1, -1, 1);
		GL.TexCoord2(2 * frth, 2 * thrd);
		GL.Vertex3(1, -1, 1);

		GL.End();

		GL.PopAttrib();
		GL.PopMatrix();
	}
}
