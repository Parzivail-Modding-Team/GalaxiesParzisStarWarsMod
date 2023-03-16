package com.parzivail.aurek.imgui;

import com.parzivail.aurek.imgui.toast.ImguiNotify;
import com.parzivail.aurek.imgui.toast.ImguiToast;
import com.parzivail.aurek.imgui.toast.ToastType;

public class Notifier
{
	public void info(String title, String message)
	{
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Info, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void success(String title, String message)
	{
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Success, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void warn(String title, String message)
	{
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Warning, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void error(String title, String message)
	{
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Error, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}

	public void error(String title, String message, Exception cause)
	{
		ImguiNotify.insertNotification(new ImguiToast(ToastType.Error, title, message, ImguiToast.NOTIFY_DEFAULT_DISMISS));
	}
}
