package com.parzivail.pswgtk.ui;

import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolkitHomeScreen extends JComponentScreen implements MouseListener
{
	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));
	}

	@Override
	protected JComponent buildInterface()
	{
		var panel = new JPanel();

		JTextField tb;
		panel.add(tb = new JTextField(50));
		panel.add(new JButton("Button"));

		return panel;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	protected void renderContent()
	{
	}
}