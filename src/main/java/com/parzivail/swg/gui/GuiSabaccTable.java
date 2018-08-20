package com.parzivail.swg.gui;

import com.parzivail.swg.container.ContainerSabaccTable;
import com.parzivail.swg.gui.modern.ModernArrowButton;
import com.parzivail.swg.gui.modern.ModernButton;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.registry.QuestRegister;
import com.parzivail.swg.tile.TileSabaccTable;
import com.parzivail.util.binary.ned.NedInteraction;
import com.parzivail.util.binary.ned.NedNode;
import com.parzivail.util.binary.ned.NodeType;
import com.parzivail.util.common.AnimatedValue;
import com.parzivail.util.common.TextUtils;
import com.parzivail.util.ui.Fx.D2;
import com.parzivail.util.ui.GLPalette;
import com.parzivail.util.ui.Timeline;
import com.parzivail.util.ui.TimelineEvent;
import com.parzivail.util.ui.gltk.AttribMask;
import com.parzivail.util.ui.gltk.EnableCap;
import com.parzivail.util.ui.gltk.GL;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.DimensionManager;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.ArrayList;
import java.util.EnumSet;

public class GuiSabaccTable extends GuiContainer
{
	private final TileSabaccTable tile;
	private final EntityPlayer player;
	private final NedInteraction interaction;
	private final Timeline textFadeOut;
	private AnimatedValue textFadeOutValue = new AnimatedValue(0, 500);

	private ModernButton bOpt1;
	private ModernButton bOpt2;
	private ModernButton bOpt3;
	private ModernButton bOpt4;
	private ModernArrowButton bNext;
	private ModernButton bExit;
	private final ModernButton[] options = new ModernButton[4];

	private String npcDialogue = "";

	public GuiSabaccTable(InventoryPlayer inventoryPlayer, TileSabaccTable tile)
	{
		super(new ContainerSabaccTable(inventoryPlayer, tile));
		this.tile = tile;

		// We have to use this awful hack because the EntityPlayer that's provided to
		// the Gui through the InventoryPlayer is a strictly client-based player instance
		// and isn't the real one.
		player = (EntityPlayer)DimensionManager.getWorld(inventoryPlayer.player.dimension).getEntityByID(inventoryPlayer.player.getEntityId());

		interaction = QuestRegister.complexQuest.createInteraction(player);

		ArrayList<TimelineEvent> keyframes = new ArrayList<>();
		keyframes.add(new TimelineEvent(0, timelineEvent -> {
			bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = false;
			bNext.visible = false;

			textFadeOutValue = new AnimatedValue(0, Math.min(npcDialogue.length() * 10, 500));
			textFadeOutValue.queueAnimatingTo(1);
		}));
		keyframes.add(new TimelineEvent(500, timelineEvent -> showButtons()));
		textFadeOut = new Timeline(keyframes);
	}

	private void showButtons()
	{
		if (interaction.getNextNode(0).type == NodeType.NpcDialogue)
		{
			bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = false;
			bNext.visible = true;
		}
		else
			movePastNpcDialogue();
	}

	@Override
	public void initGui()
	{
		super.initGui();

		options[0] = bOpt1 = new ModernButton(0, (width - 150) / 2, 25, 150, 10, "OPT1");
		options[1] = bOpt2 = new ModernButton(1, (width - 150) / 2, 40, 150, 10, "OPT2");
		options[2] = bOpt3 = new ModernButton(2, (width - 150) / 2, 55, 150, 10, "OPT3");
		options[3] = bOpt4 = new ModernButton(3, (width - 150) / 2, 70, 150, 10, "OPT4");

		bNext = new ModernArrowButton(4, (width - 5) / 2, 80);
		bExit = new ModernButton(5, (width - 150) / 2, 95, 150, 10, "Exit");

		buttonList.add(bOpt1);
		buttonList.add(bOpt2);
		buttonList.add(bOpt3);
		buttonList.add(bOpt4);

		buttonList.add(bNext);
		buttonList.add(bExit);

		bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = false;
		bNext.visible = false;

		refreshDisplay();
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		textFadeOut.tick();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		GL.PushAttrib(EnumSet.of(AttribMask.EnableBit, AttribMask.LineBit));
		GL.Enable(EnableCap.Blend);
		GL.Enable(EnableCap.Texture2D);
		GL.PushMatrix();
		int w = Client.brandonReg.getWidth(npcDialogue) / 4;
		GL.Translate((width - w) / 2, 10, 0);
		GL.Scale(0.25f);
		TextureImpl.bindNone();
		Client.brandonReg.drawString(0, 0, TextUtils.scrambleString(npcDialogue, textFadeOutValue.getValue()), Color.white);
		GL.PopMatrix();
		GL.PopAttrib();
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
				break;
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

		bExit.visible = false;

		if (interaction.node.type == NodeType.NpcDialogue)
		{
			npcDialogue = interaction.node.outputs.get(0).text;
			textFadeOut.start();
		}
		else if (interaction.node.type == NodeType.PlayerDialogue)
		{
			bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = false;
			bNext.visible = false;
			NedNode nextNode = interaction.node;

			for (int i = 0; i < nextNode.outputs.size(); i++)
			{
				options[i].setText(nextNode.outputs.get(i).text);
				options[i].visible = true;
			}
		}
		else if (interaction.node.type == NodeType.End)
		{
			bOpt1.visible = bOpt2.visible = bOpt3.visible = bOpt4.visible = bNext.visible = false;
			bExit.visible = true;
		}
	}

	private void takeBranch(int id)
	{
		interaction.advance(id);
		refreshDisplay();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		ScaledResolution sr = Client.resolution;
		GL.PushAttrib(AttribMask.EnableBit);
		GL.Disable(EnableCap.Texture2D);
		GL.Color(GLPalette.ALMOST_BLACK);
		D2.DrawSolidRectangle(0, 0, sr.getScaledWidth(), sr.getScaledHeight());
		GL.PopAttrib();
	}
}
