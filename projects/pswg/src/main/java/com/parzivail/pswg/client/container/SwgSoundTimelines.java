package com.parzivail.pswg.client.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.sound.SoundTimelineEvent;
import com.parzivail.pswg.client.sound.SoundTimelineEvents;
import com.parzivail.pswg.client.sound.timeline.SoundTimelineManager;
import com.parzivail.pswg.container.SwgSounds;
import net.minecraft.util.Identifier;

import java.util.List;

public class SwgSoundTimelines
{
	public static final Identifier THERMAL_DETONATOR_BEEP_ID = Resources.id("beep");

	public static final SoundTimelineEvents THERMAL_DETONATOR_BEEP_TIMELINE = new SoundTimelineEvents(SwgSounds.Explosives.THERMAL_DETONATOR_BEEP, List.of(
			new SoundTimelineEvent(THERMAL_DETONATOR_BEEP_ID, 0.072891f, 0.417551f),
			new SoundTimelineEvent(THERMAL_DETONATOR_BEEP_ID, 0.815305f, 1.159964f),
			new SoundTimelineEvent(THERMAL_DETONATOR_BEEP_ID, 1.579315f, 1.923975f)
	));

	private static void register()
	{
		SoundTimelineManager.register(THERMAL_DETONATOR_BEEP_TIMELINE);
	}
}
