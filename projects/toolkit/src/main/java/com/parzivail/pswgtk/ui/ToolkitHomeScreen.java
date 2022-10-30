package com.parzivail.pswgtk.ui;

import com.parzivail.pswg.Resources;
import com.parzivail.pswgtk.screen.JComponentScreen;
import com.parzivail.pswgtk.swing.NodeTreeModel;
import com.parzivail.pswgtk.util.LangUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.swing.*;
import java.util.function.Function;

public class ToolkitHomeScreen extends JComponentScreen
{
	public static class Tool
	{
		private final String id;
		private final Function<Screen, Screen> screenProvider;

		public Tool(String id, Function<Screen, Screen> screenProvider)
		{
			this.id = id;
			this.screenProvider = screenProvider;
		}

		public String getTitle()
		{
			return LangUtil.translate("tool.title." + id);
		}

		public String getDescription()
		{
			return LangUtil.translate("tool.description." + id);
		}
	}

	private static NodeTreeModel.Node<Tool> createToolNode(String id, Function<Screen, Screen> screenProvider)
	{
		return new NodeTreeModel.Node<Tool>(LangUtil.translate("tool.title." + id), new Tool(id, screenProvider));
	}

	public static final String I18N_TOOLKIT_HOME = Resources.screen("toolkit_home");

	private JPanel root;
	private JTree availableTools;
	private JLabel lToolTitle;
	private JButton bRunTool;
	private JTextArea tbToolDesc;

	public ToolkitHomeScreen(Screen parent)
	{
		super(parent, Text.translatable(I18N_TOOLKIT_HOME));

		lToolTitle.putClientProperty("FlatLaf.styleClass", "h1");

		availableTools.setModel(new NodeTreeModel<>(
				new NodeTreeModel.Node<Tool>(LangUtil.translate("tool.category.tools"), null)
						.child(new NodeTreeModel.Node<Tool>(LangUtil.translate("tool.category.modeling"), null)
								       .child(createToolNode("nemi_compiler", null))
								       .child(createToolNode("p3di_compiler", null))
						)
						.child(new NodeTreeModel.Node<Tool>(LangUtil.translate("tool.category.worldgen"), null)
								       .child(createToolNode("worldgen_visualizer", ToolkitWorldgenScreen::new))
						))
		);

		availableTools.addTreeSelectionListener(e -> {
			@SuppressWarnings("unchecked")
			var tool = (NodeTreeModel.Node<Tool>)availableTools.getLastSelectedPathComponent();

			if (tool.value == null)
			{
				lToolTitle.setText("");
				tbToolDesc.setText("");
			}
			else
			{
				lToolTitle.setText(tool.value.getTitle());
				tbToolDesc.setText(tool.value.getDescription());
			}

			bRunTool.setVisible(tool.value != null);
		});

		availableTools.addSelectionRow(0);
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
