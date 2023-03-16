package com.parzivail.aurek.imgui.toast;

import com.parzivail.aurek.imgui.ImGuiHelper;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;

import java.util.ArrayList;

public class ImguiNotify
{
	public static final float NOTIFY_PADDING_X = 20;
	public static final float NOTIFY_PADDING_Y = 20;
	public static final float NOTIFY_PADDING_MESSAGE_Y = 10;
	public static final int NOTIFY_TOAST_FLAGS =
			ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoInputs
			| ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoFocusOnAppearing
			| ImGuiHelper.ImGuiWindowFlags_Tooltip;
	public static final boolean NOTIFY_USE_SEPARATOR = true;

	private static final ArrayList<ImguiToast> notifications = new ArrayList<>();

	public static void insertNotification(ImguiToast toast)
	{
		notifications.add(toast);
	}

	public static void render()
	{
		ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 5); // Round borders
		ImGui.pushStyleColor(ImGuiCol.WindowBg, 43 / 255f, 43 / 255f, 43 / 255f, 100 / 255f); // Background color
		renderNotifications();
		ImGui.popStyleVar();
		ImGui.popStyleColor();
	}

	private static void renderNotifications()
	{
		var vp_size = ImGui.getMainViewport().getSize();
		var height = 0f;

		var removalQueue = new ArrayList<ImguiToast>();

		for (var i = 0; i < notifications.size(); i++)
		{
			var current_toast = notifications.get(i);
			var elapsed = current_toast.getElapsedTime();

			// Remove toast if expired
			if (current_toast.getPhase(elapsed) == ToastPhase.Expired)
			{
				removalQueue.add(current_toast);
				continue;
			}

			// Get icon, title and other data
			var icon = current_toast.getIcon();
			var title = current_toast.getTitle();
			var content = current_toast.getContent();
			var default_title = current_toast.getDefaultTitle();
			var opacity = current_toast.getFadePercent(); // Get opacity based of the current phase

			// Window rendering
			var text_color = current_toast.getColor();
			text_color &= 0x00FFFFFF;
			text_color |= (byte)(0xFF * opacity) << 24;

			ImGui.pushStyleColor(ImGuiCol.Text, 1f, 1f, 1f, opacity);
			ImGui.pushStyleColor(ImGuiCol.Border, 0.43f, 0.43f, 0.5f, 0.5f * opacity);
			ImGui.pushStyleColor(ImGuiCol.Separator, 0.43f, 0.43f, 0.5f, 0.5f * opacity);

			// Generate new unique name for this toast
			var window_name = String.format("##TOAST%d", i);

			//PushStyleColor(ImGuiCol_Text, text_color);
			ImGui.setNextWindowBgAlpha(opacity);
			ImGui.setNextWindowPos(vp_size.x - NOTIFY_PADDING_X, vp_size.y - NOTIFY_PADDING_Y - height, ImGuiCond.Always, 1, 1);
			ImGui.begin(window_name, NOTIFY_TOAST_FLAGS);

			// Here we render the toast content
			{
				ImGui.pushTextWrapPos(vp_size.x / 3); // We want to support multi-line text, this will wrap the text after 1/3 of the screen width

				var was_title_rendered = false;

				// If an icon is set
				if (!nullOrEmpty(icon))
				{
					ImGui.textColored(text_color, icon);

					was_title_rendered = true;
				}

				// If a title is set
				if (!nullOrEmpty(title))
				{
					// If a title and an icon is set, we want to render on same line
					if (!nullOrEmpty(icon))
						ImGui.sameLine();

					ImGui.text(title); // Render title text
					was_title_rendered = true;
				}
				else if (!nullOrEmpty(default_title))
				{
					if (!nullOrEmpty(icon))
						ImGui.sameLine();

					ImGui.text(default_title); // Render default title text (ImGuiToastType_Success . "Success", etc...)
					was_title_rendered = true;
				}

				// In case ANYTHING was rendered in the top, we want to add a small padding so the text (or icon) looks centered vertically
				if (was_title_rendered && !nullOrEmpty(content))
					ImGui.setCursorPosY(ImGui.getCursorPosY() + 5); // Must be a better way to do this!!!!

				// If a content is set
				if (!nullOrEmpty(content))
				{
					if (was_title_rendered && NOTIFY_USE_SEPARATOR)
						ImGui.separator();

					ImGui.text(content); // Render content text
				}

				ImGui.popTextWrapPos();
			}

			ImGui.popStyleColor(3);

			// Save height for next toasts
			height += (ImGui.getWindowHeight() + NOTIFY_PADDING_MESSAGE_Y) * opacity;

			// End
			ImGui.end();
		}

		notifications.removeAll(removalQueue);
	}

	private static boolean nullOrEmpty(String str)
	{
		return str == null || str.isEmpty();
	}
}
