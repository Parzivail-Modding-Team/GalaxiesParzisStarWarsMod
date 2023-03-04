package com.parzivail.aurek.util;

import com.parzivail.aurek.imgui.ImGuiHelper;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.internal.ImGui;

import java.util.ArrayList;

public class NodeTreeModel<TValue>
{
	public static class Node<TValue>
	{
		private final String icon;
		private final String title;

		public final ArrayList<Node<TValue>> children = new ArrayList<>();
		public final TValue value;

		public Node(String icon, String title, TValue value)
		{
			this.icon = icon;
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
			ImGui.pushID(title);

			if (children.isEmpty())
			{
				if (icon == null)
				{
					ImGui.treeNodeEx(LangUtil.translate(title), ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
				}
				else
				{
					ImGui.pushFont(ImGuiHelper.getIconFont());
					ImGui.treeNodeEx(icon, ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen | ImGuiTreeNodeFlags.SpanFullWidth);
					ImGui.popFont();
					ImGui.sameLine();
					ImGui.text(LangUtil.translate(title));
				}

				ImGui.popID();
				return;
			}

			boolean isOpen;
			if (icon == null)
			{
				isOpen = ImGui.treeNodeEx(LangUtil.translate(title), ImGuiTreeNodeFlags.SpanFullWidth);
			}
			else
			{
				ImGui.pushFont(ImGuiHelper.getIconFont());
				isOpen = ImGui.treeNodeEx(icon, ImGuiTreeNodeFlags.SpanFullWidth);
				ImGui.popFont();
				ImGui.sameLine();
				ImGui.text(LangUtil.translate(title));
			}

			if (isOpen)
			{
				children.forEach(Node::render);
				ImGui.treePop();
			}

			ImGui.popID();
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
