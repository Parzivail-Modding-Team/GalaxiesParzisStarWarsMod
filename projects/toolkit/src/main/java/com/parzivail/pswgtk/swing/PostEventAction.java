package com.parzivail.pswgtk.swing;

import java.awt.*;
import java.security.PrivilegedAction;

public class PostEventAction implements PrivilegedAction<Void>
{
	private AWTEvent event;

	public PostEventAction(AWTEvent event)
	{
		this.event = event;
	}

	@Override
	public Void run()
	{
		EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
		eq.postEvent(event);
		return null;
	}
}
