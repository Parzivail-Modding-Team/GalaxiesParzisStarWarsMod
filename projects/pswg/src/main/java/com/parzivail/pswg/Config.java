package com.parzivail.pswg;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@me.shedaniel.autoconfig.annotation.Config(name = "pswg")
public class Config implements ConfigData
{
	// @Comments are replicated in en_us.json (for i18n)
	public static class Input
	{
		@Comment("Sets whether mouse inputs should be pitch and yaw, or pitch and roll")
		@ConfigEntry.Gui.Tooltip
		public boolean shipRollPriority = false;
	}

	public static class View
	{
		@Comment("Sets the stiffness of ships' third person cameras")
		@ConfigEntry.Gui.Tooltip
		public float shipCameraStiffness = 0.4f;

		@Comment("Scales the base distance of ships' third person cameras")
		@ConfigEntry.Gui.Tooltip
		public float shipCameraBaseDistance = 1;

		@Comment("Scales the speed-varying distance of ships' third person cameras")
		@ConfigEntry.Gui.Tooltip
		public float shipCameraSpeedDistance = 1;

		@Comment("Enables or disables screen shake")
		@ConfigEntry.Gui.Tooltip
		public boolean enableScreenShake = true;

		@Comment("Force the perspective to first person when aiming down sights")
		@ConfigEntry.Gui.Tooltip
		public boolean forceFirstPersonAds = true;

		@Comment("The ambient brightness of an ignited lightsaber when shaders are installed and active")
		@ConfigEntry.BoundedDiscrete(min = 0, max = 15)
		@ConfigEntry.Gui.Tooltip
		public int lightsaberShaderBrightness = 11;
	}

	public static class Client
	{
		@Comment("Show the character customization tip at next world join")
		@ConfigEntry.Gui.Tooltip
		public boolean showCharacterCustomizeTip = true;
	}

	public static class Server
	{
		@Comment("Prevents players under a certain permission level from accessing vehicles")
		@ConfigEntry.Gui.Tooltip
		public int vehiclePermissionLevel = 0;

		@Comment("Allows blasters to damage entities that are non-living (item frames, armor stands, etc.)")
		@ConfigEntry.Gui.Tooltip
		public boolean allowBlasterNonlivingDamage = false;

		@Comment("Enable complex collision calculation for large entities")
		@ConfigEntry.Gui.Tooltip
		public boolean allowComplexCollisions = true;

		@Comment("Allow block destruction (from blasters, explosives, etc.)")
		@ConfigEntry.Gui.Tooltip
		public boolean allowDestruction = true;
	}

	@ConfigEntry.Gui.CollapsibleObject
	@Comment("Here you can change your preferred input methods.")
	@ConfigEntry.Gui.Tooltip
	public Input input = new Input();

	@ConfigEntry.Gui.CollapsibleObject
	@Comment("Here you can change your rendering preferences.")
	@ConfigEntry.Gui.Tooltip
	public View view = new View();

	@ConfigEntry.Gui.CollapsibleObject
	@Comment("Here you can change general client options.")
	@ConfigEntry.Gui.Tooltip
	public Client client = new Client();

	@ConfigEntry.Gui.CollapsibleObject
	@Comment("Here you can change general server options.")
	@ConfigEntry.Gui.Tooltip
	public Server server = new Server();

	@ConfigEntry.Gui.Excluded
	@Comment("Disable update checker")
	public boolean disableUpdateCheck = false;

	@Comment("Ask to send PSWG crash reports to the developers")
	@ConfigEntry.Gui.Tooltip
	public boolean askToSendCrashReports = true;

	public void reload()
	{
		// TODO: change priority with keybind
	}
}
