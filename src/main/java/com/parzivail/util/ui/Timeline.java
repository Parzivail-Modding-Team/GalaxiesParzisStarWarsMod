package com.parzivail.util.ui;

import com.parzivail.util.common.Enumerable;

import java.util.ArrayList;
import java.util.Iterator;

public class Timeline
{
	private boolean running;
	private long startTime;
	private final ArrayList<TimelineEvent> staticevents;
	private ArrayList<TimelineEvent> events;

	public final float length;

	public Timeline(ArrayList<TimelineEvent> events)
	{
		this.events = this.staticevents = events;
		this.length = Enumerable.from(events).max((e) -> (float)e.time);
	}

	public void start()
	{
		this.events = (ArrayList<TimelineEvent>)this.staticevents.clone();
		this.startTime = Fx.Util.GetMillis();
		this.running = true;
	}

	public float getPosition()
	{
		long delta = Fx.Util.GetMillis() - startTime;
		return delta / length;
	}

	public void tick()
	{
		if (!this.running)
			return;
		long delta = Fx.Util.GetMillis() - startTime;
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
			this.running = false;
	}
}
