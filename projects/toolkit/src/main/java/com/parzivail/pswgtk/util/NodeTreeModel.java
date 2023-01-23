package com.parzivail.pswgtk.util;

import imgui.flag.ImGuiTreeNodeFlags;
import imgui.internal.ImGui;

import java.util.ArrayList;

public class NodeTreeModel<TValue>
{
	public static class Node<TValue>
	{
		private final String title;

		public final ArrayList<Node<TValue>> children = new ArrayList<>();
		public final TValue value;

		public Node(String title, TValue value)
		{
			this.title = title;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return title;
		}

		public Node<TValue> child(Node<TValue> node)
		{
			this.children.add(node);
			return this;
		}

		public void render()
		{
			if (children.isEmpty())
			{
				ImGui.treeNodeEx(LangUtil.translate(title), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.Bullet | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
				return;
			}

			if (ImGui.treeNodeEx(LangUtil.translate(title), ImGuiTreeNodeFlags.SpanFullWidth))
			{
				children.forEach(Node::render);
				ImGui.treePop();
			}
		}
	}

	private final Node<TValue> root;

	public NodeTreeModel(Node<TValue> root)
	{
		this.root = root;
	}

	public Node<TValue> getRoot()
	{
		return root;
	}

	public Object getChild(Node<TValue> parent, int index)
	{
		return parent.children.get(index);
	}

	public int getChildCount(Node<TValue> parent)
	{
		return parent.children.size();
	}

	public boolean isLeaf(Node<TValue> node)
	{
		return node.children.isEmpty();
	}

	public int getIndexOfChild(Node<TValue> parent, Node<TValue> child)
	{
		if (parent == null || child == null)
			return -1;
		return parent.children.indexOf(child);
	}

	public void render()
	{
		ImGui.setNextItemOpen(true);
		root.render();
	}
}
