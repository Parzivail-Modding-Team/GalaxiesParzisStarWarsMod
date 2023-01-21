package com.parzivail.pswgtk.ui.model;

import com.parzivail.pswg.client.loader.NemManager;
import com.parzivail.pswgtk.model.nemi.NemiModel;
import com.parzivail.pswgtk.util.NodeTreeModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class NemiModelProject implements TabModel
{
	private record PartHash<T>(String name, T value)
	{
	}

	public enum NodeType
	{
		Root,
		Part,
		Cuboid
	}

	private final String filename;
	private final NbtCompound compiledModel;
	private final ModelPart modelPart;

	private final NodeTreeModel<NodeType> treeModel;

	public NemiModelProject(String filename, NemiModel model)
	{
		this(filename, model.createNem());
	}

	public NemiModelProject(String filename, NbtCompound nem)
	{
		this.filename = filename;
		this.compiledModel = nem;
		this.modelPart = NemManager.buildModel(this.compiledModel).createModel();

		var uniqueNames = new HashMap<PartHash<NbtCompound>, String>();
		var node = new NodeTreeModel.Node<>("[root]", NodeType.Root);
		buildNode(uniqueNames, nem, "parts", node);
		this.treeModel = new NodeTreeModel<>(node);
	}

	private static void buildNode(HashMap<PartHash<NbtCompound>, String> uniqueNames, NbtCompound root, String childKey, NodeTreeModel.Node<NodeType> parent)
	{
		var children = root.getCompound(childKey);
		for (var childName : children.getKeys())
		{
			var child = children.getCompound(childName);
			var uniqueName = NemiModel.getUniqueName(uniqueNames, childName, new PartHash<>(childName, child));

			var node = new NodeTreeModel.Node<>(childName.equals(uniqueName) ? childName : childName + " → " + uniqueName, NodeType.Part);
			buildNode(uniqueNames, child, "children", node);

			for (var boxElement : child.getList("cuboids", NbtElement.COMPOUND_TYPE))
			{
				var box = (NbtCompound)boxElement;
				var size = box.getCompound("size");
				var x = size.getInt("x");
				var y = size.getInt("y");
				var z = size.getInt("z");
				node.child(new NodeTreeModel.Node<>("(" + x + ", " + y + ", " + z + ")", NodeType.Cuboid));
			}

			parent.child(node);
		}
	}

	public NodeTreeModel<NodeType> getTreeModel()
	{
		return treeModel;
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
