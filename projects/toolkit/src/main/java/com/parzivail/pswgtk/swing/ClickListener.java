package com.parzivail.pswgtk.swing;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class ClickListener implements MouseListener
{
	public static void add(JComponent target, Consumer<MouseEvent> handler)
	{
		target.addMouseListener(new ClickListener(handler));
	}

	private final Consumer<MouseEvent> handler;

	public ClickListener(Consumer<MouseEvent> handler)
	{
		this.handler = handler;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		handler.accept(e);
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
