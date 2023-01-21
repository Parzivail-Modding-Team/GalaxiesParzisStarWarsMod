package com.parzivail.pswgtk.util;

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
	}

	private final Node<TValue> root;

	public NodeTreeModel(Node<TValue> root)
	{
		this.root = root;
	}

	public Object getRoot()
	{
		return root;
	}

	@SuppressWarnings("unchecked")
	public Object getChild(Object parent, int index)
	{
		return ((Node<TValue>)parent).children.get(index);
	}

	@SuppressWarnings("unchecked")
	public int getChildCount(Object parent)
	{
		return ((Node<TValue>)parent).children.size();
	}

	@SuppressWarnings("unchecked")
	public boolean isLeaf(Object node)
	{
		return ((Node<TValue>)node).children.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public int getIndexOfChild(Object parent, Object child)
	{
		if (parent == null || child == null)
			return -1;
		return ((Node<TValue>)parent).children.indexOf((Node<TValue>)child);
	}
}
