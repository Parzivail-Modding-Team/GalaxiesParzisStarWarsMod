package com.parzivail.pswgtk.util;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.ByteBuffer;
import java.util.Optional;

public class DialogUtil
{
	public enum Icon
	{
		INFO("info"),
		WARNING("warning"),
		ERROR("error"),
		QUESTION("question");

		String id;

		Icon(String id)
		{
			this.id = id;
		}

		public String id()
		{
			return id;
		}
	}

	public enum Button
	{
		CANCEL,
		YES,
		NO,
		OK
	}

	public enum ButtonGroup
	{
		OK("ok", null, Button.OK),
		OKCANCEL("okcancel", Button.CANCEL, Button.OK),
		YESNO("yesno", Button.NO, Button.YES),
		YESNOCANCEL("yesnocancel", Button.CANCEL, Button.YES, Button.NO);

		String id;
		Button[] results;

		ButtonGroup(String id, Button... results)
		{
			this.id = id;
			this.results = results;
		}

		public String id()
		{
			return id;
		}

		public Button decodeResult(int result)
		{
			return results[result];
		}
	}

	public static Optional<String[]> openFile(String title, String singleFilterName, boolean allowMultiple, String... filters)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			var patterns = stack.mallocPointer(filters.length);
			for (var filter : filters)
				patterns.put(stack.UTF8(filter));
			patterns.flip();

			var result = TinyFileDialogs.tinyfd_openFileDialog(title, "", patterns, singleFilterName, allowMultiple);
			if (result == null)
				return Optional.empty();
			return Optional.of(StringUtils.split(result, '|'));
		}
	}

	public static Optional<String> saveFile(String title, String... filters)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			var patterns = stack.mallocPointer(filters.length);
			for (var filter : filters)
				patterns.put(stack.UTF8(filter));
			patterns.flip();

			return Optional.ofNullable(TinyFileDialogs.tinyfd_saveFileDialog(title, "", patterns, null));
		}
	}

	public static void notify(String title, String message, Icon icon)
	{
		TinyFileDialogs.tinyfd_notifyPopup(title, message, icon.id());
	}

	public static Optional<Color3b> chooseColor(String title, Color3b def)
	{
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			ByteBuffer color = stack.malloc(3);
			if (TinyFileDialogs.tinyfd_colorChooser(title, def.toHex(), null, color) != null)
				return Optional.of(new Color3b((byte)(color.get(0) & 0xFF), (byte)(color.get(1) & 0xFF), (byte)(color.get(2) & 0xFF)));
		}

		return Optional.empty();
	}

	public static Button notifyChoice(String title, String message, Icon icon, ButtonGroup group, boolean affirmativeDefault)
	{
		MemoryStack stack = MemoryStack.stackGet();
		int stackPointer = stack.getPointer();
		try
		{
			stack.nUTF8Safe(title, true);
			long aTitleEncoded = title == null ? MemoryUtil.NULL : stack.getPointerAddress();
			stack.nUTF8Safe(message, true);
			long aMessageEncoded = message == null ? MemoryUtil.NULL : stack.getPointerAddress();
			stack.nASCII(group.id(), true);
			long aDialogTypeEncoded = stack.getPointerAddress();
			stack.nASCII(icon.id(), true);
			long aIconTypeEncoded = stack.getPointerAddress();
			// We have to do this manually (i.e. can't call the tinyfd_messageBox method) since the library
			// method compiles all answers into positive and negative, which makes no/cancel in yesnocancel
			// indistinguishable
			int result = TinyFileDialogs.ntinyfd_messageBox(aTitleEncoded, aMessageEncoded, aDialogTypeEncoded, aIconTypeEncoded, affirmativeDefault ? 1 : 0);
			return group.decodeResult(result);
		}
		finally
		{
			stack.setPointer(stackPointer);
		}
	}
}
