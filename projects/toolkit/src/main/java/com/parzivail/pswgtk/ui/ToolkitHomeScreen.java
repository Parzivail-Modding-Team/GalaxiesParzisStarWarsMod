package com.parzivail.pswgtk.ui;

import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.NodeTreeModel;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.swing.*;
import java.util.function.Function;

public class ToolkitHomeScreen extends JComponentScreen
{
	public static class Tool
	{
		private final Function<Screen, Screen> screenProvider;

		public Tool(Function<Screen, Screen> screenProvider)
		{
			this.screenProvider = screenProvider;
		}
	}

	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private JPanel root;
	private JTree availableTools;
	private JLabel lToolTitle;
	private JButton bRunTool;
	private JLabel lToolDesc;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		lToolTitle.putClientProperty("FlatLaf.styleClass", "h1");

		availableTools.setModel(new NodeTreeModel<>(
				new NodeTreeModel.Node<Tool>("Tools", null)
						.child(new NodeTreeModel.Node<Tool>("Modeling", null)
								       .child(new NodeTreeModel.Node<>("NEMi Compiler", null))
								       .child(new NodeTreeModel.Node<>("P3Di Compiler", null))
						)
						.child(new NodeTreeModel.Node<Tool>("World Generation", null)
								       .child(new NodeTreeModel.Node<>("Worldgen Visualizer", new Tool(ToolkitWorldgenScreen::new)))
						))
		);
	}

	@Override
	protected JComponent getRootComponent()
	{
		return root;
	}

	@Override
	protected void renderContent(MatrixStack matrices)
	{

	}
}
