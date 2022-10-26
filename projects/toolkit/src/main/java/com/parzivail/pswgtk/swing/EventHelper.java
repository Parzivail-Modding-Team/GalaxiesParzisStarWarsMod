package com.parzivail.pswgtk.swing;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class EventHelper
{
	public static void click(JComponent target, Consumer<MouseEvent> handler)
	{
		target.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				handler.accept(e);
			}
		});
	}

	public static void press(JComponent target, Consumer<MouseEvent> handler)
	{
		target.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				handler.accept(e);
			}
		});
	}
}
