package com.parzivail.aurek.imgui;

import com.parzivail.aurek.imgui.toast.ImguiNotify;
import com.parzivail.aurek.imgui.toast.ImguiToast;
import com.parzivail.aurek.imgui.toast.ToastType;
import com.parzivail.util.Lumberjack;

public class Notifier
{
	private final Lumberjack LOGGER;

	public Notifier(String header)
	{
		LOGGER = new Lumberjack(header);
	}

	public void info(String title, String message)
	{
		LOGGER.info(message);
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Info, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void success(String title, String message)
	{
		LOGGER.info(message);
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Success, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void warn(String title, String message)
	{
		LOGGER.warn(message);
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Warning, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void error(String title, String message)
	{
		LOGGER.error(message);
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Error, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void error(String title, String message, Exception cause)
	{
		message = message + "\n\n" + cause.getMessage();
		LOGGER.error(message);
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Error, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}
}
