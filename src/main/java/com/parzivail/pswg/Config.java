package com.parzivail.pswg;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@me.sargunvohra.mcmods.autoconfig1u.annotation.Config(name = "pswg")
public class Config implements ConfigData
{
	public static class Input
	{
		@Comment("Sets whether mouse inputs should be pitch and yaw, or pitch and roll")
		public boolean shipRollPriority = false;
	}

	@ConfigEntry.Gui.CollapsibleObject
	@Comment("Here you can change your preferred input methods.")
	public Input input = new Input();

	public void reload()
	{
		// TODO: change priority with keybind
	}
}
