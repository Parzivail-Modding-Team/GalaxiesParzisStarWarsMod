package com.parzivail.pswgtk.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.parzivail.pswgtk.swing.TextureBackedContentWrapper;

import javax.swing.*;
import java.awt.*;

public class SplitTreeContentView
{

	{
		// GUI initializer generated by IntelliJ IDEA GUI Designer
		// >>> IMPORTANT!! <<<
		// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$()
	{
		rootPanel = new JPanel();
		rootPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JSplitPane splitPane1 = new JSplitPane();
		rootPanel.add(splitPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
		content = new JPanel();
		content.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		splitPane1.setRightComponent(content);
		final JScrollPane scrollPane1 = new JScrollPane();
		splitPane1.setLeftComponent(scrollPane1);
		tree = new JTree();
		scrollPane1.setViewportView(tree);
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return rootPanel;
	}

	private JPanel rootPanel;
	private JTree tree;
	private JPanel content;

	public SplitTreeContentView()
	{
		this.content.setBackground(new Color(TextureBackedContentWrapper.MASK_COLOR));
		tree.setModel(null);
	}

	public JTree getTree()
	{
		return tree;
	}

	public JPanel getRoot()
	{
		return rootPanel;
	}
}
