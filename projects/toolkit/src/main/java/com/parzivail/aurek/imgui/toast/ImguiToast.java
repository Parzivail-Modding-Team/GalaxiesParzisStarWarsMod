package com.parzivail.aurek.imgui.toast;

import com.parzivail.aurek.imgui.AurekIconFont;

/*
	Based on https://github.com/patrickcjk/imgui-notify/blob/v2/example/src/imgui_notify.h
 */
public class ImguiToast
{
	public static final int NOTIFY_FADE_IN_OUT_TIME = 150;
	public static final int NOTIFY_DEFAULT_DISMISS = 3000;
	public static final float NOTIFY_OPACITY = 1;

	public static final int NOTIFY_NONE_COLOR = 0xFFFFFFFF;
	public static final int NOTIFY_SUCCESS_COLOR = 0x00FF00FF;
	public static final int NOTIFY_WARNING_COLOR = 0xFFFF00FF;
	public static final int NOTIFY_ERROR_COLOR = 0xFF0000FF;
	public static final int NOTIFY_INFO_COLOR = 0x009DFFFF;

	private ToastType type;
	private String title;
	private String content;
	private int dismissTime;
	private long creationTime;

	public ImguiToast(ToastType type, String title, String content, int dismissTime)
	{
		this.type = type;
		this.title = title;
		this.content = content;
		this.dismissTime = dismissTime;
		this.creationTime = System.currentTimeMillis();
	}

	public String getTitle()
	{
		return title;
	}

	public String getContent()
	{
		return content;
	}

	public String getDefaultTitle()
	{
		if (!title.isEmpty())
			return title;

		return switch (type)
				{
					case None -> null;
					case Success -> "Success";
					case Warning -> "Warning";
					case Error -> "Error";
					case Info -> "Info";
				};
	}

	public int getColor()
	{
		return switch (type)
				{
					case None -> NOTIFY_NONE_COLOR;
					case Success -> NOTIFY_SUCCESS_COLOR;
					case Warning -> NOTIFY_WARNING_COLOR;
					case Error -> NOTIFY_ERROR_COLOR;
					case Info -> NOTIFY_INFO_COLOR;
				};
	}

	public String getIcon()
	{
		return switch (type)
				{

					case None -> null;
					case Success -> AurekIconFont.checkmark;
					case Warning -> AurekIconFont.error;
					case Error -> AurekIconFont.cancel;
					case Info -> AurekIconFont.info;
				};
	}

	public long getElapsedTime()
	{
		return System.currentTimeMillis() - creationTime;
	}

	public ToastPhase getPhase(long elapsed)
	{
		if (elapsed > NOTIFY_FADE_IN_OUT_TIME + dismissTime + NOTIFY_FADE_IN_OUT_TIME)
			return ToastPhase.Expired;
		else if (elapsed > NOTIFY_FADE_IN_OUT_TIME + dismissTime)
			return ToastPhase.FadeOut;
		else if (elapsed > NOTIFY_FADE_IN_OUT_TIME)
			return ToastPhase.Wait;
		else
			return ToastPhase.FadeIn;
	}

	public float getFadePercent()
	{
		var elapsed = getElapsedTime();
		var phase = getPhase(elapsed);

		if (phase == ToastPhase.FadeIn)
			return ((float)elapsed / NOTIFY_FADE_IN_OUT_TIME) * NOTIFY_OPACITY;
		else if (phase == ToastPhase.FadeOut)
			return (1 - (((float)elapsed - NOTIFY_FADE_IN_OUT_TIME - dismissTime) / NOTIFY_FADE_IN_OUT_TIME)) * NOTIFY_OPACITY;

		return NOTIFY_OPACITY;
	}
}
