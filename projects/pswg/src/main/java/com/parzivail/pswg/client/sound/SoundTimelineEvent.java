package com.parzivail.pswg.client.sound;

import net.minecraft.util.Identifier;

/**
 * Identifies a portion of a sound that contains events
 *
 * @param id    The ID of the event
 * @param start The start time, in seconds, of the event
 * @param end   The end time, in seconds, of the event
 */
public record SoundTimelineEvent(Identifier id, float start, float end)
{
}
