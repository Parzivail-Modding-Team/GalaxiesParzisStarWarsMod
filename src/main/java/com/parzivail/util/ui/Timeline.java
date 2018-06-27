package com.parzivail.util.ui;

import com.parzivail.util.common.Enumerable;
import com.parzivail.util.ui.Fx.Util;

import java.util.ArrayList;
import java.util.Iterator;

public class Timeline
{
	public final float length;
	private final ArrayList<TimelineEvent> staticevents;
	private boolean running;
	private long startTime;
	private ArrayList<TimelineEvent> events;

	public Timeline(ArrayList<TimelineEvent> events)
	{
		this.events = staticevents = events;
		length = Enumerable.from(events).max((e) -> (float)e.time);
	}

	public void start()
	{
		events = (ArrayList<TimelineEvent>)staticevents.clone();
		startTime = Util.GetMillis();
		running = true;
	}

	public float getPosition()
	{
		long delta = Util.GetMillis() - startTime;
		return delta / length;
	}

	public void tick()
	{
		if (!running)
			return;
		long delta = Util.GetMillis() - startTime;
		for (Iterator<TimelineEvent> i = events.iterator(); i.hasNext(); )
		{
			TimelineEvent e = i.next();
			if (delta >= e.time)
			{
				e.action.accept(e);
				i.remove();
			}
		}
		if (delta > length)
			running = false;
	}
}
