package com.parzivail.swg.gui;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.tile.TileNpcSpawner;
import com.parzivail.swg.transaction.TransactionUpdateNpcSpawner;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class GuiNpcSpawner extends GuiScreen
{
	private final TileNpcSpawner tile;
	private final EntityPlayer player;

	private GuiTextField npcId;
	private GuiButton btnSave;
	private GuiButton btnSpawn;
	private GuiButton btnCancel;

	public GuiNpcSpawner(EntityPlayer player, TileNpcSpawner tile)
	{
		this.tile = tile;
		this.player = player;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(btnSave = new GuiButton(0, width / 2 - 4 - 150, height / 4 + 120 + 12, 100, 20, Resources.guiDot("save")));
		buttonList.add(btnSpawn = new GuiButton(1, width / 2 - 50, height / 4 + 120 + 12, 100, 20, Resources.guiDot("spawn")));
		buttonList.add(btnCancel = new GuiButton(2, width / 2 + 4 + 50, height / 4 + 120 + 12, 100, 20, Resources.guiDot("cancel")));
		npcId = new GuiTextField(fontRendererObj, width / 2 - 150, 50, 300, 20);
		npcId.setMaxStringLength(32767);
		npcId.setFocused(true);

		if (tile.getNpcId() == null || tile.getNpcId().isEmpty())
			npcId.setText("npc");
		else
			npcId.setText(tile.getNpcId());
	}

	public void updateScreen()
	{
		npcId.updateCursorCounter();
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			if (button.id == btnCancel.id)
			{
				player.closeScreen();
			}
			else if (button.id == btnSave.id)
			{
				StarWarsGalaxy.transactionBroker.dispatch(new TransactionUpdateNpcSpawner(tile, npcId.getText(), false));
				player.closeScreen();
			}
			else if (button.id == btnSpawn.id)
			{
				StarWarsGalaxy.transactionBroker.dispatch(new TransactionUpdateNpcSpawner(tile, npcId.getText(), true));
				player.closeScreen();
			}
		}
	}

	/**
	 * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode)
	{
		npcId.textboxKeyTyped(typedChar, keyCode);
		btnSave.enabled = btnSpawn.enabled = !npcId.getText().trim().isEmpty();

		if (keyCode == 28 || keyCode == 156)
			actionPerformed(btnSave);
		else if (keyCode == 1)
			actionPerformed(btnCancel);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		npcId.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, Resources.guiDot("npcSpawner"), width / 2, 20, 16777215);
		drawString(fontRendererObj, Resources.guiDot("npcId"), width / 2 - 150, 37, 10526880);
		npcId.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
