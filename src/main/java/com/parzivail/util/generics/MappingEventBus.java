package com.parzivail.util.generics;

import java.util.HashMap;
import java.util.function.Consumer;

public class MappingEventBus<TE, TC>
{
	private final HashMap<TE, TC> EVENT_HANDLERS = new HashMap<>();

	public void subscribe(TE event, TC handler)
	{
		EVENT_HANDLERS.put(event, handler);
	}

	public void unsubscribe(TE event, TC handler)
	{
		EVENT_HANDLERS.remove(event);
	}

	public void publish(TE event, Consumer<TC> contextConsumer)
	{
		var subscriber = EVENT_HANDLERS.get(event);
		if (subscriber == null)
			return;

		contextConsumer.accept(subscriber);
	}
}
