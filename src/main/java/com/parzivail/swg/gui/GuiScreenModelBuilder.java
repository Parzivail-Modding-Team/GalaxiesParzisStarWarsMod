package com.parzivail.swg.gui;

import com.parzivail.swg.proxy.Client;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import com.parzivail.util.ui.gltk.PrimitiveType;
import com.parzivail.util.ui.maui.MauiButton;
import com.parzivail.util.ui.maui.MauiConnectedState;
import com.parzivail.util.ui.maui.MauiSlider;
import com.parzivail.util.ui.maui.MauiTextbox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenModelBuilder extends GuiScreen
{
	private GuiScreen returnScreen;

	private MauiButton testButtonLeft;
	private MauiButton testButtonMiddle;
	private MauiButton testButtonRight;

	private MauiSlider testSlider;

	private MauiTextbox testTextbox;

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
		testButtonLeft.connectedState = MauiConnectedState.Right;
		this.buttonList.add(testButtonLeft);


		testButtonMiddle = new MauiButton(10, 25, 10, 15, 5, "Middle");
		testButtonMiddle.onClick = (button) -> {
			Lumberjack.log("middle");
		};
		testButtonMiddle.connectedState = MauiConnectedState.Both;
		this.buttonList.add(testButtonMiddle);

		testButtonRight = new MauiButton(10, 40, 10, 15, 5, "Right");
		testButtonRight.onClick = (button) -> {
			Lumberjack.log("right");
		};
		testButtonRight.connectedState = MauiConnectedState.Left;
		this.buttonList.add(testButtonRight);

		testSlider = new MauiSlider(10, 40, 30, 45, 5, "Slider: %s", 0f, 1f, 0f, 1);
		this.buttonList.add(testSlider);

		testTextbox = new MauiTextbox(40, 60, 45, 5);
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

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		this.testTextbox.updateCursorCounter();
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode)
	{
		if (keyCode == 1)
		{
			this.mc.displayGuiScreen(null);
			this.mc.setIngameFocus();
		}
		else if (keyCode != 28 && keyCode != 156)
		{
			this.testTextbox.textboxKeyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.testTextbox.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		this.testTextbox.drawTextBox();

		GL.Enable(EnableCap.Blend);

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
