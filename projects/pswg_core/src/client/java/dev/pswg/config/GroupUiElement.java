package dev.pswg.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GroupUiElement extends UiElement implements MapLike<UiElement>
{
	protected final ArrayList<UiElement> children = new ArrayList<>();

	public List<UiElement> getChildren()
	{
		return children;
	}

	public void addChild(UiElement child)
	{
		children.add(child);
	}

	public void addChild(String key, UiElement value)
	{
		var newValue = value.clone();
		newValue.setKey(key);
		addChild(newValue);
	}

	@Override
	public GroupUiElement clone()
	{
		var element = new GroupUiElement();
		element.children.addAll(children);
		return element;
	}

	@Override
	public UiElement get(UiElement key)
	{
		return children.stream()
		               .filter(e -> e.equals(key))
		               .findFirst()
		               .orElse(null);
	}

	@Override
	public UiElement get(String key)
	{
		return children.stream()
		               .filter(e -> Objects.equals(e.key, key))
		               .findFirst()
		               .orElse(null);
	}

	@Override
	public Stream<Pair<UiElement, UiElement>> entries()
	{
		return children.stream()
		               .map(e -> Pair.of(e, e));
	}
}
