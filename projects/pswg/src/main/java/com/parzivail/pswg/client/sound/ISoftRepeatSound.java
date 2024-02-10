package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.client.sound.timeline.SoundTimelineManager;

/**
 * Tag interface to define whether a sound should repeat
 * in software rather than in the OpenAL layer. This is
 * used to catch sound repeat events for use with
 * {@link SoundTimelineManager}.
 */
public interface ISoftRepeatSound
{
}
