package com.parzivail.swg.gui;

import com.parzivail.swg.container.ContainerDialogue;
import com.parzivail.swg.gui.modern.ModernArrowButton;
import com.parzivail.swg.gui.modern.ModernButton;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.QuestRegister;
import com.parzivail.util.binary.ned.NedInteraction;
import com.parzivail.util.binary.ned.NodeType;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.common.TextUtils;
import com.parzivail.util.ui.Fx;
import com.parzivail.util.ui.TtfUtil;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.DimensionManager;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

public class GuiDialogue extends GuiContainer
{
	private final Entity target;
	private final EntityPlayer player;
	private final NedInteraction interaction;
	private final ModernButton[] options = new ModernButton[4];
	private AnimatedValue textFadeOutValue = new AnimatedValue(0, 500);
	private ModernButton bOpt1;
	private ModernButton bOpt2;
	private ModernButton bOpt3;
	private ModernButton bOpt4;
	private ModernArrowButton bNext;
	private String npcDialogue = "";

	public GuiDialogue(InventoryPlayer inventoryPlayer, Entity target)
	{
		super(new ContainerDialogue(inventoryPlayer, target));
		this.target = target;

		// We have to use this awful hack because the EntityPlayer that's provided to
		// the Gui through the InventoryPlayer is a strictly client-based player instance
		// and isn't the one we want.
		player = (EntityPlayer)DimensionManager.getWorld(inventoryPlayer.player.dimension).getEntityByID(inventoryPlayer.player.getEntityId());

		interaction = QuestRegister.complexQuest.createInteraction(player);
	}

	@Override
	public void initGui()
	{
		super.initGui();

		options[0] = bOpt1 = new ModernButton(0, (width - 150) / 2, height - 95, 150, 10, "OPT1");
		options[1] = bOpt2 = new ModernButton(1, (width - 150) / 2, height - 80, 150, 10, "OPT2");
		options[2] = bOpt3 = new ModernButton(2, (width - 150) / 2, height - 65, 150, 10, "OPT3");
		options[3] = bOpt4 = new ModernButton(3, (width - 150) / 2, height - 50, 150, 10, "OPT4");

		bNext = new ModernArrowButton(4, (width - 150) / 2 + 160, height - 50);

		buttonList.add(bOpt1);
		buttonList.add(bOpt2);
		buttonList.add(bOpt3);
		buttonList.add(bOpt4);

		buttonList.add(bNext);

		refreshDisplay();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (interaction.node.type == NodeType.NpcDialogue)
		{
			GL.PushAttrib(AttribMask.EnableBit);
			GL.Enable(EnableCap.Blend);
			GL.Disable(EnableCap.Lighting);
			GL.Enable(EnableCap.Texture2D);
			GL.PushMatrix();

			int w = TtfUtil.getWidth(Client.brandonReg, npcDialogue) / 4;
			int h = TtfUtil.getHeight(Client.brandonReg, npcDialogue) / 4;
			GL.Translate((int)((width - w) / 2f), height - 100 + (65 - h) / 2f, 0);
			GL.Scale(0.25f);
			TextureImpl.bindNone();
			TtfUtil.drawString(Client.brandonReg, 2, 2, TextUtils.scrambleString(npcDialogue, textFadeOutValue.getValue()), Color.black);
			TtfUtil.drawString(Client.brandonReg, 0, 0, TextUtils.scrambleString(npcDialogue, textFadeOutValue.getValue()), Color.white);
			GL.PopMatrix();
			GL.PopAttrib();
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode)
	{
		// do not allow player to exit until dialogue is done
		if (keyCode != Keyboard.KEY_ESCAPE && keyCode != mc.gameSettings.keyBindInventory.getKeyCode())
			super.keyTyped(typedChar, keyCode);
		if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_SPACE)
		{
			if (interaction.node.type == NodeType.NpcDialogue)
				movePastNpcDialogue();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		switch (button.id)
		{
			case 0:
			case 1:
			case 2:
			case 3:
				takeBranch(button.id);
				break;
			case 4:
				movePastNpcDialogue();
			default:
				break;
		}
	}

	private void movePastNpcDialogue()
	{
		interaction.advance(0);
		refreshDisplay();
	}

	private void refreshDisplay()
	{
		if (interaction.node == null)
			return;

		if (interaction.node.type == NodeType.NpcDialogue)
		{
			npcDialogue = interaction.node.outputs.get(0).text;

			bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = false;
			bNext.visible = true;

			textFadeOutValue = new AnimatedValue(0, Math.min(npcDialogue.length() * 10, 500));
			textFadeOutValue.queueAnimatingTo(1);
		}
		else if (interaction.node.type == NodeType.PlayerDialogue)
		{
			bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = false;
			bNext.visible = false;

			for (int i = 0; i < interaction.node.outputs.size(); i++)
			{
				options[i].setText(interaction.node.outputs.get(i).text);
				options[i].visible = true;
			}
		}
		else if (interaction.node.type == NodeType.End)
			mc.thePlayer.closeScreen();
	}

	private void takeBranch(int id)
	{
		interaction.advance(id);
		refreshDisplay();
	}

	public void drawWorldBackground(int tint)
	{
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Texture2D);
		GL.Color(0xCC000000);
		Fx.D2.DrawSolidRoundRectangle((width - 200) / 2f, height - 100, 200, 65, 2);
		GL.Color(0xFF000000);
		Fx.D2.DrawWireRoundRectangle((width - 200) / 2f, height - 100, 200, 65, 2);
		GL.PopAttrib();
	}
}
