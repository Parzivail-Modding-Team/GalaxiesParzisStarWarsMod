package com.parzivail.pswgtk.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolkitHomeInterface extends JPanel implements MouseListener
{
	public ToolkitHomeInterface()
	{
		setBackground(Color.MAGENTA);

		var b = new JButton("Button");
		b.addMouseListener(this);

		add(b);
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
}
