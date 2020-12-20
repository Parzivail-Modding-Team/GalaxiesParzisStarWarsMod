package com.parzivail.pswg;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@me.sargunvohra.mcmods.autoconfig1u.annotation.Config(name = "pswg")
public class Config implements ConfigData
{
	// @Comments are replicated in en_us.json (for i18n)
	public static class Input
	{
		@Comment("Sets whether mouse inputs should be pitch and yaw, or pitch and roll")
		@ConfigEntry.Gui.Tooltip
		public boolean shipRollPriority = false;
	}

	@ConfigEntry.Gui.CollapsibleObject
	@Comment("Here you can change your preferred input methods.")
	@ConfigEntry.Gui.Tooltip
	public Input input = new Input();

	public void reload()
	{
		// TODO: change priority with keybind
	}
}
