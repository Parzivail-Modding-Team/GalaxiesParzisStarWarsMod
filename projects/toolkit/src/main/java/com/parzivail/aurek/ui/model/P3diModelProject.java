package com.parzivail.aurek.ui.model;

import com.parzivail.aurek.imgui.AurekIconFont;
import com.parzivail.aurek.model.p3di.P3diMesh;
import com.parzivail.aurek.model.p3di.P3diModel;
import com.parzivail.aurek.util.NodeTreeModel;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

public class P3diModelProject implements TabModel
{
	public enum NodeType
	{
		Root,
		Mesh,
		Socket
	}

	private final String id;
	private final String filename;
	private final P3diModel model;

	private final NodeTreeModel<NodeType> treeModel;

	public P3diModelProject(String filename, P3diModel model)
	{
		this.id = UUID.randomUUID().toString();
		this.filename = filename;
		this.model = model;

		var node = new NodeTreeModel.Node<>(AurekIconFont.file_3d, "[root]", NodeType.Root);

		for (var socket : model.sockets())
			if (socket.parent() == null)
				node.child(new NodeTreeModel.Node<>(AurekIconFont.empty_arrows, socket.name(), NodeType.Socket));

		buildNode(model, model.meshes(), node);
		this.treeModel = new NodeTreeModel<>(node);
	}

	private void buildNode(P3diModel model, P3diMesh[] objects, NodeTreeModel.Node<NodeType> parent)
	{
		for (var object : objects)
		{
			var node = new NodeTreeModel.Node<>(AurekIconFont.mesh_data, object.name(), NodeType.Mesh);

			for (var socket : model.sockets())
				if (socket.parent() != null && socket.parent().equals(object.name()))
					node.child(new NodeTreeModel.Node<>(AurekIconFont.empty_arrows, socket.name(), NodeType.Socket));

			buildNode(model, object.children(), parent);

			parent.child(node);
		}
	}

	@Override
	public String getId()
	{
		return id;
	}

	public String getFilename()
	{
		return filename;
	}

	public P3diModel getModel()
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
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		P3diModelProject that = (P3diModelProject)o;
		return filename.equals(that.filename);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(filename);
	}

	public NodeTreeModel<NodeType> getTreeModel()
	{
		return treeModel;
	}
}
