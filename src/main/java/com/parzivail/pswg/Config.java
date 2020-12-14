package com.parzivail.pswg;

import com.google.common.base.CaseFormat;
import de.siphalor.tweed.config.ConfigEnvironment;
import de.siphalor.tweed.config.ConfigScope;
import de.siphalor.tweed.config.annotated.AConfigEntry;
import de.siphalor.tweed.config.annotated.AConfigListener;
import de.siphalor.tweed.config.annotated.ATweedConfig;

@ATweedConfig(
		scope = ConfigScope.SMALLEST,
		environment = ConfigEnvironment.UNIVERSAL,
		casing = CaseFormat.LOWER_UNDERSCORE,
		tailors = "tweed:cloth"
)
public class Config
{
	public static class Input
	{
		@AConfigEntry(comment = "Sets whether mouse inputs should be pitch and yaw, or pitch and roll")
		public boolean shipRollPriority = false;
	}

	@AConfigEntry(comment = "Here you can change your preferred input methods.")
	public static Input input;

	@AConfigListener
	public static void reload()
	{
		// TODO: change priority with keybind
	}
}
