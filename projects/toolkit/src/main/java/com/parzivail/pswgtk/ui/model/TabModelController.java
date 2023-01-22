package com.parzivail.pswgtk.ui.model;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class TabModelController<TModel extends TabModel> implements Collection<TModel>
{
	private final ArrayList<TModel> models = new ArrayList<>();
	private final JTabbedPane tabContainer;

	public TabModelController(JTabbedPane tabContainer, Consumer<TModel> onModelChanged)
	{
		this.tabContainer = tabContainer;
		//		tabContainer.removeAll();
		//
		//		tabContainer.addChangeListener(e -> onModelChanged.accept(getSelected()));
	}

	public TModel getSelected()
	{
		return models.get(tabContainer.getSelectedIndex());
	}

	@Override
	public int size()
	{
		return models.size();
	}

	@Override
	public boolean isEmpty()
	{
		return models.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return models.contains(o);
	}

	@NotNull
	@Override
	public Iterator<TModel> iterator()
	{
		return models.iterator();
	}

	@NotNull
	@Override
	public Object[] toArray()
	{
		return models.toArray();
	}

	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a)
	{
		return models.toArray(a);
	}

	@Override
	public boolean add(TModel tModel)
	{
		models.add(tModel);
		this.tabContainer.add(tModel.getTitle(), createEmptyTabContents());
		return true;
	}

	private JComponent createEmptyTabContents()
	{
		var p = new JPanel();
		var empty = new Dimension(0, 0);
		p.setSize(empty);
		p.setMinimumSize(empty);
		p.setMaximumSize(empty);
		p.setPreferredSize(empty);
		p.setVisible(false);
		p.setBorder(BorderFactory.createEmptyBorder());
		return p;
	}

	@Override
	public boolean remove(Object o)
	{
		var model = models.indexOf(o);
		if (!models.get(model).tryClose())
			return false;
		this.tabContainer.remove(model);
		return models.remove(o);
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c)
	{
		return models.containsAll(c);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends TModel> c)
	{
		var result = models.addAll(c);
		for (var c1 : c)
			this.tabContainer.add(c1.getTitle(), createEmptyTabContents());
		return result;
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void clear()
	{
		var discarded = new ArrayList<TModel>();
		for (int i = 0; i < models.size(); i++)
		{
			TModel model = models.get(i);
			if (!model.tryClose())
				continue;
			this.tabContainer.remove(i);
			discarded.add(model);
		}

		this.models.removeAll(discarded);
	}
}
