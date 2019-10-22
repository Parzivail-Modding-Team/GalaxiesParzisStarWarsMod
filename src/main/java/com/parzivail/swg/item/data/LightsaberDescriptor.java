package com.parzivail.swg.item.data;

public class LightsaberDescriptor
{
	public static final LightsaberDescriptor DEFAULT = new LightsaberDescriptor("DEFAULT", false, false, new String[0], Blade.DEFAULT, Sounds.DEFAULT);

	public final String name;
	public final boolean waterproof;
	public final boolean unstable;
	public final String[] model;
	public final Blade blade;
	public final Sounds sounds;

	public LightsaberDescriptor(String name, boolean waterproof, boolean unstable, String[] model, Blade blade, Sounds sounds)
	{
		this.name = name;
		this.waterproof = waterproof;
		this.unstable = unstable;
		this.model = model;
		this.blade = blade;
		this.sounds = sounds;
	}

	public static class Blade
	{
		public static final Blade DEFAULT = new Blade(1, 0x00FF00, 0xFFFFFF);

		public final float length;
		public final int glowColor;
		public final int coreColor;

		public Blade(float length, int glowColor, int coreColor)
		{
			this.length = length;
			this.glowColor = glowColor;
			this.coreColor = coreColor;
		}
	}

	public static class Sounds
	{
		public static final Sounds DEFAULT = new Sounds("default", "default", "default", "default");

		public final String start;
		public final String idle;
		public final String swing;
		public final String stop;

		public Sounds(String start, String idle, String swing, String stop)
		{
			this.start = start;
			this.idle = idle;
			this.swing = swing;
			this.stop = stop;
		}
	}
}
