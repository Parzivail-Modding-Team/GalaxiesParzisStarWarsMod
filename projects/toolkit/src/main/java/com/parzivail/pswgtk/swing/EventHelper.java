package com.parzivail.pswgtk.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EventHelper
{
	public static KeyStroke ctrl(int key)
	{
		return KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK);
	}

	public static <T extends AbstractButton> T action(T target, Consumer<ActionEvent> handler)
	{
		target.addActionListener(handler::accept);
		return target;
	}

	public static <T extends JMenuItem> T action(T target, KeyStroke bind, Consumer<ActionEvent> handler)
	{
		target.addActionListener(handler::accept);
		target.setAccelerator(bind);
		return target;
	}

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

	public static void createDependency(JCheckBox source, Component... components)
	{
		ActionListener l = e -> {
			for (var c : components)
				c.setEnabled(source.isSelected());
		};
		source.addActionListener(l);
		l.actionPerformed(null);
	}

	public static void bindSelected(JCheckBox source, Supplier<Boolean> initializer, Consumer<Boolean> destination)
	{
		source.setSelected(initializer.get());
		source.addActionListener(e -> destination.accept(source.isSelected()));
	}

	public static void bindIntValue(JSpinner source, Supplier<Integer> initializer, Consumer<Integer> destination)
	{
		source.setValue(initializer.get());
		source.addChangeListener(e -> destination.accept((int)source.getValue()));
	}
}
