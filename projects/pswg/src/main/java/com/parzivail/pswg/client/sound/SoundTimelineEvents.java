package com.parzivail.pswg.client.sound;

import net.minecraft.sound.SoundEvent;

import java.util.List;

/**
 * Manages a list of {@link SoundTimelineEvent}s that raise
 * notifications when a playing sound reaches and leaves
 * certain cues
 *
 * @param source The sound event that these timelines are based upon
 * @param events The events that should generate notifications
 */
public record SoundTimelineEvents(SoundEvent source, List<SoundTimelineEvent> events)
{
}
