package com.parzivail.util.ui.maui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.HashMap;
import java.util.function.Consumer;

public class MauiButton extends GuiButton
{
	private static final NinePatchResource texDefault = new NinePatchResource(Resources.location("textures/maui/button/default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texHover = new NinePatchResource(Resources.location("textures/maui/button/hover.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texActive = new NinePatchResource(Resources.location("textures/maui/button/active.png"), 16, 23, 6, 6, 4, 10);

	private static final NinePatchResource texLeftDefault = new NinePatchResource(Resources.location("textures/maui/buttonConnected/left_default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texLeftHover = new NinePatchResource(Resources.location("textures/maui/buttonConnected/left_hover.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texLeftActive = new NinePatchResource(Resources.location("textures/maui/buttonConnected/left_active.png"), 16, 23, 6, 6, 4, 10);

	private static final NinePatchResource texRightDefault = new NinePatchResource(Resources.location("textures/maui/buttonConnected/right_default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texRightHover = new NinePatchResource(Resources.location("textures/maui/buttonConnected/right_hover.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texRightActive = new NinePatchResource(Resources.location("textures/maui/buttonConnected/right_active.png"), 16, 23, 6, 6, 4, 10);

	private static final NinePatchResource texBothDefault = new NinePatchResource(Resources.location("textures/maui/buttonConnected/both_default.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texBothHover = new NinePatchResource(Resources.location("textures/maui/buttonConnected/both_hover.png"), 16, 23, 6, 6, 4, 10);
	private static final NinePatchResource texBothActive = new NinePatchResource(Resources.location("textures/maui/buttonConnected/both_active.png"), 16, 23, 6, 6, 4, 10);

	private static final HashMap<MauiConnectedState, NinePatchResource[]> connectedStateMap = new HashMap<>();

	static
	{
		connectedStateMap.put(MauiConnectedState.None, new NinePatchResource[] {
				texDefault, texHover, texActive
		});
		connectedStateMap.put(MauiConnectedState.Left, new NinePatchResource[] {
				texLeftDefault, texLeftHover, texLeftActive
		});
		connectedStateMap.put(MauiConnectedState.Right, new NinePatchResource[] {
				texRightDefault, texRightHover, texRightActive
		});
		connectedStateMap.put(MauiConnectedState.Both, new NinePatchResource[] {
				texBothDefault, texBothHover, texBothActive
		});
	}

	public Consumer<MauiButton> onClick;
	public MauiConnectedState connectedState = MauiConnectedState.None;

	public MauiButton(int buttonId, int x, int y, int w, int h, String buttonText)
	{
		super(buttonId, x, y, w, h, buttonText);
		onClick = (dummy) -> {
		};
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
	{
		ScaledResolution sr = Client.resolution;

		NinePatchResource[] states = connectedStateMap.get(connectedState);

		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.CullFace);
		GL.PushMatrix();
		GL.Color(GLPalette.WHITE);
		GL.Translate(xPosition, yPosition, 0);
		boolean hover = Fx.Util.RectangleIntersects(xPosition, yPosition, width, height, mouseX, mouseY);
		boolean active = hover && Mouse.isButtonDown(0);
		NinePatchResource texture = active ? states[2] : hover ? states[1] : states[0];
		texture.draw(width, height);
		float width = Maui.deJaVuSans.getWidth(this.displayString);
		GL.Scale(1f / sr.getScaleFactor());
		GL.Translate(Math.round((this.width * sr.getScaleFactor() - width) / 2f), Math.round((this.height * sr.getScaleFactor() - Maui.deJaVuSans.getHeight()) / 2f), 0);
		TextureImpl.bindNone();
		Maui.deJaVuSans.drawString(0, 0, this.displayString, org.newdawn.slick.Color.black);
		GL.PopMatrix();
		GL.PopAttrib();
	}

	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY)
	{
	}
}
