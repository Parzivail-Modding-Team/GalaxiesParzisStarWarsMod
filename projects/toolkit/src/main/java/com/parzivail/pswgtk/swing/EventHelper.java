package com.parzivail.pswgtk.swing;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class EventHelper
{
	public static <T extends Component> T click(T target, Consumer<MouseEvent> handler)
	{
		target.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				handler.accept(e);
			}
		});
		return target;
	}

	public static <T extends Component> T press(T target, Consumer<MouseEvent> handler)
	{
		target.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				handler.accept(e);
			}
		});
		return target;
	}

	public static <T extends Component> T keyPressed(T target, Consumer<KeyEvent> handler)
	{
		target.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				handler.accept(e);
			}
		});
		return target;
	}
}
