package dev.pswg.config;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

import java.util.List;
import java.util.stream.Stream;

public class ConfigUiOps implements DynamicOps<UiElement>
{
	private static final UiElement EMPTY = new GroupUiElement();
	public static final ConfigUiOps INSTANCE = new ConfigUiOps();

	private ConfigUiOps()
	{
	}

	@Override
	public UiElement empty()
	{
		return EMPTY;
	}

	@Override
	public <U> U convertTo(DynamicOps<U> outOps, UiElement input)
	{
		// TODO: necessary?
		throw new RuntimeException("Conversion from UI ops to other ops is not supported");
	}

	@Override
	public DataResult<Number> getNumberValue(UiElement input)
	{
		if (input instanceof NumericUiElement element)
			return DataResult.success(element.getValue());

		return DataResult.error(() -> "Not a number: " + input);
	}

	@Override
	public UiElement createNumeric(Number i)
	{
		return new NumericUiElement(i);
	}

	@Override
	public DataResult<String> getStringValue(UiElement input)
	{
		if (input instanceof StringUiElement element)
			return DataResult.success(element.getValue());

		return DataResult.error(() -> "Not a string: " + input);
	}

	@Override
	public UiElement createString(String value)
	{
		return new StringUiElement(value);
	}

	@Override
	public DataResult<Boolean> getBooleanValue(UiElement input)
	{
		if (input instanceof CheckboxUiElement element)
			return DataResult.success(element.getValue());

		return DataResult.error(() -> "Not a boolean: " + input);
	}

	@Override
	public UiElement createBoolean(boolean value)
	{
		return new CheckboxUiElement(value);
	}

	@Override
	public DataResult<UiElement> mergeToList(UiElement list, UiElement value)
	{
		if (!(list instanceof GroupUiElement group))
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		var newGroup = group.clone();
		newGroup.addChild(value);
		return DataResult.success(newGroup);
	}

	@Override
	public DataResult<UiElement> mergeToList(UiElement list, List<UiElement> values)
	{
		if (!(list instanceof GroupUiElement group))
			return DataResult.error(() -> "mergeToList called with not a list: " + list, list);

		var newGroup = group.clone();
		values.forEach(newGroup::addChild);
		return DataResult.success(newGroup);
	}

	@Override
	public DataResult<UiElement> mergeToMap(UiElement map, UiElement key, UiElement value)
	{
		if (!(map instanceof GroupUiElement group))
			return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		if (!(key instanceof StringUiElement stringKey))
			return DataResult.error(() -> "key is not a string: " + key, map);

		var newMap = group.clone();
		newMap.addChild(stringKey.getValue(), value);
		return DataResult.success(newMap);
	}

	@Override
	public DataResult<UiElement> mergeToMap(UiElement map, MapLike<UiElement> values)
	{
		if (!(map instanceof GroupUiElement group))
			return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);

		var newMap = group.clone();

		final List<UiElement> missed = Lists.newArrayList();

		values.entries().forEach(entry -> {
			var key = entry.getFirst();
			if (!(key instanceof StringUiElement stringKey))
			{
				missed.add(key);
				return;
			}

			newMap.addChild(stringKey.getValue(), entry.getSecond());
		});

		if (!missed.isEmpty())
			return DataResult.error(() -> "some keys are not strings: " + missed, newMap);

		return DataResult.success(newMap);
	}

	@Override
	public DataResult<Stream<Pair<UiElement, UiElement>>> getMapValues(UiElement input)
	{
		if (!(input instanceof GroupUiElement groupUiElement))
			return DataResult.error(() -> "Not a group: " + input);

		return DataResult.success(groupUiElement.getChildren().stream().map(uiElement -> new Pair<>(uiElement, uiElement)));
	}

	@Override
	public DataResult<MapLike<UiElement>> getMap(UiElement input)
	{
		if (!(input instanceof GroupUiElement groupUiElement))
			return DataResult.error(() -> "Not a group: " + input);

		return DataResult.success(groupUiElement);
	}

	@Override
	public UiElement createMap(Stream<Pair<UiElement, UiElement>> map)
	{
		final GroupUiElement element = new GroupUiElement();
		map.forEach(e -> element.addChild(((StringUiElement)e.getFirst()).getValue(), e.getSecond()));
		return element;
	}

	@Override
	public DataResult<Stream<UiElement>> getStream(UiElement input)
	{
		if (!(input instanceof GroupUiElement groupUiElement))
			return DataResult.error(() -> "Not a group: " + input);

		return DataResult.success(groupUiElement.children.stream());
	}

	@Override
	public UiElement createList(Stream<UiElement> input)
	{
		var groupElement = new GroupUiElement();
		input.forEach(groupElement::addChild);
		return groupElement;
	}

	@Override
	public UiElement remove(UiElement input, String key)
	{
		if (!(input instanceof GroupUiElement groupUiElement))
			return input;

		groupUiElement.children.removeIf(e -> e.key.equals(key));
		return groupUiElement;
	}
}
