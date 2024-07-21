package com.parzivail.pswg.client.sound.timeline;

import com.google.common.collect.HashMultimap;
import com.parzivail.pswg.client.sound.SoundTimelineEvents;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Identifier;

/**
 * Handles registering and raising sound timeline events
 */
public final class SoundTimelineManager
{
	@FunctionalInterface
	public interface SoundTimelineEvent
	{
		void soundTimelineEvent(SoundInstance instance, Identifier timelineEvent, float delta);
	}

	private static final Object2IntMap<SoundInstance> FIRST_TICKS = new Object2IntOpenHashMap<>();
	private static final HashMultimap<Identifier, SoundTimelineEvents> EVENTS = HashMultimap.create();

	public static final Event<SoundTimelineEvent> SOUND_EVENT_ENTERED = EventFactory.createArrayBacked(
			SoundTimelineEvent.class,
			(instance, timelineEvent, delta) -> {
			},
			callbacks -> (instance, timelineEvent, delta) -> {
				for (final var callback : callbacks)
					callback.soundTimelineEvent(instance, timelineEvent, delta);
			}
	);

	public static final Event<SoundTimelineEvent> SOUND_EVENT_LEFT = EventFactory.createArrayBacked(
			SoundTimelineEvent.class,
			(instance, timelineEvent, delta) -> {
			},
			callbacks -> (instance, timelineEvent, delta) -> {
				for (final var callback : callbacks)
					callback.soundTimelineEvent(instance, timelineEvent, delta);
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

		var it = FIRST_TICKS.object2IntEntrySet().iterator();
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
					var end = event.end();

					var delta = (end == start) ? 1 : (currentSecond - start) / (end - start);

					if (prevSecond < start && currentSecond >= start)
						SOUND_EVENT_ENTERED.invoker().soundTimelineEvent(instance, event.id(), delta);

					if (prevSecond < end && currentSecond >= end)
						SOUND_EVENT_LEFT.invoker().soundTimelineEvent(instance, event.id(), delta);

					lastEnd = Math.max(lastEnd, end);
				}
			}

			if (currentSecond >= lastEnd)
				it.remove();
		}

		prevFrameTick = currentFrameTick;
	}
}
