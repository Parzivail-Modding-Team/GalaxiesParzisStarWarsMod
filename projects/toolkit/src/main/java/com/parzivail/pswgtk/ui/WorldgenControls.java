package com.parzivail.pswgtk.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.parzivail.pswgtk.render.ChunkedWorldMesh;
import com.parzivail.pswgtk.swing.EventHelper;
import net.minecraft.util.math.MathHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

public class WorldgenControls
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
		root = new JPanel();
		root.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
		root.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel1.setBorder(BorderFactory.createTitledBorder(null, "Generation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		nudSeed = new JSpinner();
		panel1.add(nudSeed, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Seed");
		panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		bGenerate = new JButton();
		bGenerate.setText("Generate");
		panel1.add(bGenerate, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		bRandomizeSeed = new JButton();
		bRandomizeSeed.setText("⚁");
		panel1.add(bRandomizeSeed, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
		root.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel2.setBorder(BorderFactory.createTitledBorder(null, "Slice", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		cbSliceX = new JCheckBox();
		cbSliceX.setText("X");
		panel2.add(cbSliceX, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cbReverseX = new JCheckBox();
		cbReverseX.setText("Invert");
		panel2.add(cbReverseX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nudSliceX = new JSpinner();
		panel2.add(nudSliceX, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cbSliceZ = new JCheckBox();
		cbSliceZ.setText("Z");
		panel2.add(cbSliceZ, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cbReverseZ = new JCheckBox();
		cbReverseZ.setText("Invert");
		panel2.add(cbReverseZ, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		nudSliceZ = new JSpinner();
		nudSliceZ.setEnabled(true);
		panel2.add(nudSliceZ, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		root.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$()
	{
		return root;
	}

	private final Random random = new Random();
	private final ChunkedWorldMesh mesh;

	private JPanel root;
	private JButton bGenerate;
	private JCheckBox cbSliceX;
	private JCheckBox cbReverseX;
	private JSpinner nudSliceX;
	private JCheckBox cbSliceZ;
	private JCheckBox cbReverseZ;
	private JSpinner nudSliceZ;
	private JSpinner nudSeed;
	private JButton bRandomizeSeed;

	public WorldgenControls(ChunkedWorldMesh mesh)
	{
		this.mesh = mesh;

		EventHelper.action(bGenerate, e -> mesh.scheduleRegererate());

		EventHelper.createDependency(cbSliceX, cbReverseX, nudSliceX);
		EventHelper.createDependency(cbSliceZ, cbReverseZ, nudSliceZ);

		onWorldResized();

		nudSeed.setModel(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		EventHelper.bindIntValue(nudSeed, mesh::getSeed, mesh::setSeed);
		EventHelper.action(bRandomizeSeed, e -> nudSeed.setValue(random.nextInt()));

		var slice = this.mesh.getSlice();
		EventHelper.bindSelected(cbSliceX, slice::isActiveX, slice::setActiveX);
		EventHelper.bindSelected(cbSliceZ, slice::isActiveZ, slice::setActiveZ);
		EventHelper.bindSelected(cbReverseX, slice::isReverseX, slice::setReverseX);
		EventHelper.bindSelected(cbReverseZ, slice::isReverseZ, slice::setReverseZ);
		EventHelper.bindIntValue(nudSliceX, slice::getValueX, slice::setValueX);
		EventHelper.bindIntValue(nudSliceZ, slice::getValueZ, slice::setValueZ);
	}

	public void onWorldResized()
	{
		var min = mesh.getMin();
		var max = mesh.getMax();

		nudSliceX.setModel(new SpinnerNumberModel(MathHelper.clamp((int)nudSliceX.getValue(), min.x, max.x), min.x, max.x, 1));
		nudSliceZ.setModel(new SpinnerNumberModel(MathHelper.clamp((int)nudSliceZ.getValue(), min.z, max.z), min.z, max.z, 1));
	}

	public JPanel getRoot()
	{
		return root;
	}
}
