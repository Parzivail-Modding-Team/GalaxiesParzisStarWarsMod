package com.parzivail.pswg.client.sound.timeline;

import com.google.common.collect.HashMultimap;
import com.parzivail.pswg.client.sound.SoundTimelineEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles registering and raising sound timeline events
 */
public final class SoundTimelineManager
{
	@FunctionalInterface
	public interface SoundTimelineEvent
	{
		void soundTimelineEvent(SoundInstance instance, Identifier timelineEvent);
	}

	private static final Map<SoundInstance, Integer> FIRST_TICKS = new HashMap<>();
	private static final HashMultimap<Identifier, SoundTimelineEvents> EVENTS = HashMultimap.create();

	public static final Event<SoundTimelineEvent> SOUND_EVENT_ENTERED = EventFactory.createArrayBacked(
			SoundTimelineEvent.class,
			(instance, timelineEvent) -> {
			},
			callbacks -> (instance, timelineEvent) -> {
				for (final var callback : callbacks)
					callback.soundTimelineEvent(instance, timelineEvent);
			}
	);

	public static final Event<SoundTimelineEvent> SOUND_EVENT_LEFT = EventFactory.createArrayBacked(
			SoundTimelineEvent.class,
			(instance, timelineEvent) -> {
			},
			callbacks -> (instance, timelineEvent) -> {
				for (final var callback : callbacks)
					callback.soundTimelineEvent(instance, timelineEvent);
			}
	);

	private static float prevFrameTick = 0;

	public static void register(SoundTimelineEvents events)
	{
		EVENTS.put(events.source().getId(), events);
	}

	public static void track(int currentTick, SoundInstance instance)
	{
		FIRST_TICKS.put(instance, currentTick);
	}

	public static void tick(int currentTick)
	{
		var mc = MinecraftClient.getInstance();
		var tickDelta = mc.getTickDelta();

		var currentFrameTick = currentTick + tickDelta;
		if (currentFrameTick <= prevFrameTick)
			return;

		var it = FIRST_TICKS.entrySet().iterator();
		while (it.hasNext())
		{
			var startTickPair = it.next();

			var instance = startTickPair.getKey();
			var eventsSet = EVENTS.get(instance.getId());
			if (eventsSet.isEmpty())
			{
				it.remove();
				continue;
			}

			var startTick = (int)startTickPair.getValue();
			var prevSecond = (prevFrameTick - startTick) / 20;
			var currentSecond = (currentFrameTick - startTick) / 20;

			var lastEnd = 0f;

			for (var events : eventsSet)
			{
				for (var event : events.events())
				{
					var start = event.start();
					if (prevSecond < start && currentSecond >= start)
						SOUND_EVENT_ENTERED.invoker().soundTimelineEvent(instance, event.id());

					var end = event.end();
					if (prevSecond < end && currentSecond >= end)
						SOUND_EVENT_LEFT.invoker().soundTimelineEvent(instance, event.id());

					lastEnd = Math.max(lastEnd, end);
				}
			}

			if (currentSecond >= lastEnd)
				it.remove();
		}

		prevFrameTick = currentFrameTick;
	}
}
