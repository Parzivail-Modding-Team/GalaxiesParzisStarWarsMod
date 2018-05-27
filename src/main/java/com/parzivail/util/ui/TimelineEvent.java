package com.parzivail.util.ui;

import java.util.function.Consumer;

public class TimelineEvent
{
	public final long time;
	public final Consumer<TimelineEvent> action;

	public TimelineEvent(long time, Consumer<TimelineEvent> action)
	{
		this.time = time;
		this.action = action;
	}
}
