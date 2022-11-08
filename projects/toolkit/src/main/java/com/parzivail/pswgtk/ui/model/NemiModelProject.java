package com.parzivail.pswgtk.ui.model;

import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.model.nemi.NemiPart;
import com.parzivail.pswgtk.swing.NodeTreeModel;
import com.parzivail.pswgtk.ui.SplitTreeContentView;

import javax.swing.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class NemiModelProject implements TabModel
{
	public enum NodeType
	{
		Root,
		Part,
		Cuboid
	}

	private final String filename;
	private final NemiModel model;

	private final SplitTreeContentView contentView;

	public NemiModelProject(String filename, NemiModel model)
	{
		this.filename = filename;
		this.model = model;

		contentView = new SplitTreeContentView();

		var uniqueNames = new HashMap<NemiPart, String>();
		var node = new NodeTreeModel.Node<>("[root]", NodeType.Root);
		buildNode(uniqueNames, model.parts(), null, node);
		contentView.getTree().setModel(new NodeTreeModel<>(node));
	}

	private static void buildNode(HashMap<NemiPart, String> uniqueNames, HashMap<String, NemiPart> parts, String parentKey, NodeTreeModel.Node<NodeType> parent)
	{
		for (var part : parts.entrySet())
		{
			if (!Objects.equals(part.getValue().parent(), parentKey))
				continue;

			var partName = part.getKey();
			var uniqueName = NemiModel.getUniqueName(uniqueNames, partName, part.getValue());

			var node = new NodeTreeModel.Node<>(partName.equals(uniqueName) ? partName : partName + " → " + uniqueNames, NodeType.Part);
			buildNode(uniqueNames, parts, part.getKey(), node);

			for (var box : part.getValue().boxes())
				node.child(new NodeTreeModel.Node<>(box.size().toIntString(), NodeType.Cuboid));

			parent.child(node);
		}
	}

	public NemiModel getModel()
	{
		return model;
	}

	@Override
	public String getTitle()
	{
		return Paths.get(filename).getFileName().toString();
	}

	@Override
	public boolean tryClose()
	{
		return true;
	}

	@Override
	public JComponent getContents()
	{
		return contentView.getRoot();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		NemiModelProject that = (NemiModelProject)o;
		return filename.equals(that.filename);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(filename);
	}
}
