package com.parzivail.util.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.function.Function;

public class StringInteropAdapter<T> extends TypeAdapter<T>
{
	private final Function<T, String> serializer;
	private final Function<String, T> deserializer;

	public StringInteropAdapter(Function<T, String> serializer, Function<String, T> deserializer)
	{
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public void write(JsonWriter out, T value) throws IOException
	{
		out.value(serializer.apply(value));
	}

	@Override
	public T read(JsonReader in) throws IOException
	{
		return deserializer.apply(in.nextString());
	}
}
