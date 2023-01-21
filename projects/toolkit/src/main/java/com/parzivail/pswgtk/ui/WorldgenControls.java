package com.parzivail.pswgtk.ui;

import com.parzivail.pswgtk.render.ChunkedWorldMesh;
import net.minecraft.util.math.MathHelper;

import javax.swing.*;
import java.util.Random;

public class WorldgenControls
{
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

		//		EventHelper.action(bGenerate, e -> mesh.scheduleRegererate());
		//
		//		EventHelper.createDependency(cbSliceX, cbReverseX, nudSliceX);
		//		EventHelper.createDependency(cbSliceZ, cbReverseZ, nudSliceZ);

		onWorldResized();

		nudSeed.setModel(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
		//		EventHelper.bindIntValue(nudSeed, mesh::getSeed, mesh::setSeed);
		//		EventHelper.action(bRandomizeSeed, e -> nudSeed.setValue(random.nextInt()));

		var slice = this.mesh.getSlice();
		//		EventHelper.bindSelected(cbSliceX, slice::isActiveX, slice::setActiveX);
		//		EventHelper.bindSelected(cbSliceZ, slice::isActiveZ, slice::setActiveZ);
		//		EventHelper.bindSelected(cbReverseX, slice::isReverseX, slice::setReverseX);
		//		EventHelper.bindSelected(cbReverseZ, slice::isReverseZ, slice::setReverseZ);
		//		EventHelper.bindIntValue(nudSliceX, slice::getValueX, slice::setValueX);
		//		EventHelper.bindIntValue(nudSliceZ, slice::getValueZ, slice::setValueZ);
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
