package com.parzivail.swg.gui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import com.parzivail.util.ui.maui.MauiButton;
import com.parzivail.util.ui.maui.MauiSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenModelBuilder extends GuiScreen
{
	private GuiScreen returnScreen;

	private MauiButton testButtonLeft;
	private MauiButton testButtonMiddle;
	private MauiButton testButtonRight;

	private MauiSlider testSlider;

	public GuiScreenModelBuilder()
	{
		this.returnScreen = Client.mc.currentScreen;
	}

	public void initGui()
	{
		testButtonLeft = new MauiButton(10, 10, 10, 15, 5, "Left");
		testButtonLeft.onClick = (button) -> {
			Lumberjack.log("left");
		};
		testButtonLeft.connectedState = MauiButton.ConnectedState.Right;
		this.buttonList.add(testButtonLeft);

		testButtonMiddle = new MauiButton(10, 25, 10, 15, 5, "Middle");
		testButtonMiddle.onClick = (button) -> {
			Lumberjack.log("middle");
		};
		testButtonMiddle.connectedState = MauiButton.ConnectedState.Both;
		this.buttonList.add(testButtonMiddle);

		testButtonRight = new MauiButton(10, 40, 10, 15, 5, "Right");
		testButtonRight.onClick = (button) -> {
			Lumberjack.log("right");
		};
		testButtonRight.connectedState = MauiButton.ConnectedState.Left;
		this.buttonList.add(testButtonRight);

		testSlider = new MauiSlider(10, 40, 50, 15, 5, "Stuff", 0, 1, 0);
		testSlider.onChange = (s) -> {
			Lumberjack.log("value: %s", s.value);
		};
		this.buttonList.add(testSlider);
	}

	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled && button.visible)
		{
			if (button instanceof MauiButton)
			{
				MauiButton mauiButton = ((MauiButton)button);
				mauiButton.onClick.accept(mauiButton);
			}
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		GL.Color(0xFF727272);
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Texture2D);
		GL.Begin(PrimitiveType.Quads);
		GL.Vertex2(0.0D, (double)this.height);
		GL.Vertex2((double)this.width, (double)this.height);
		GL.Vertex2((double)this.width, 0.0);
		GL.Vertex2(0.0D, 0.0D);
		GL.End();
		GL.PopAttrib();
		//this.drawBackground(0);
		//this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title", new Object[0]), this.width / 2, 16, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
