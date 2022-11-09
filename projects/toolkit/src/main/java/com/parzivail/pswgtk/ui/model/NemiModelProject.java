package com.parzivail.pswgtk.ui.model;

import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.model.nemi.NemiPart;
import com.parzivail.pswgtk.swing.NodeTreeModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.nbt.NbtCompound;

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
	private final NbtCompound compiledModel;
	private final ModelPart modelPart;

	private final NodeTreeModel<NodeType> treeModel;

	public NemiModelProject(String filename, NemiModel model)
	{
		this.filename = filename;
		this.model = model;
		this.compiledModel = model.createNem();
		this.modelPart = NemManager.buildModel(this.compiledModel).createModel();

		var uniqueNames = new HashMap<NemiPart, String>();
		var node = new NodeTreeModel.Node<>("[root]", NodeType.Root);
		buildNode(uniqueNames, model.parts(), null, node);
		this.treeModel = new NodeTreeModel<>(node);
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

	public NodeTreeModel<NodeType> getTreeModel()
	{
		return treeModel;
	}

	public NemiModel getModel()
	{
		return model;
	}

	public NbtCompound getCompiledModel()
	{
		return compiledModel;
	}

	public ModelPart getModelPart()
	{
		return modelPart;
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
