package com.parzivail.pswgtk.swing;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;

public class NodeTreeModel<TValue> implements TreeModel
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

	@Override
	public Object getRoot()
	{
		return root;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getChild(Object parent, int index)
	{
		return ((Node<TValue>)parent).children.get(index);
	}

	@Override
	@SuppressWarnings("unchecked")
	public int getChildCount(Object parent)
	{
		return ((Node<TValue>)parent).children.size();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean isLeaf(Object node)
	{
		return ((Node<TValue>)node).children.isEmpty();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{

	}

	@Override
	@SuppressWarnings("unchecked")
	public int getIndexOfChild(Object parent, Object child)
	{
		if (parent == null || child == null)
			return -1;
		return ((Node<TValue>)parent).children.indexOf((Node<TValue>)child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{

	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{

	}
}
