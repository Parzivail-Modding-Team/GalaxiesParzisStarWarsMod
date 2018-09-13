package com.parzivail.swg.registry;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.world.Zone;

import java.util.ArrayList;

public class ZoneRegistry
{
	public static final ArrayList<Zone> zones = new ArrayList<>();
	public static final Zone zoneMosEisley;
	public static final Zone zoneExperimentPaddockA;

	static
	{
		zones.add(zoneMosEisley = new Zone(StarWarsGalaxy.config.getDimIdTatooine(), -63, 63, 59, -44, 75, 83, "The Mos Eisley Spaceport"));
		zones.add(zoneExperimentPaddockA = new Zone(StarWarsGalaxy.config.getDimIdTatooine(), 0, 68, 87, -55, 75, 139, "Experiment Paddock A"));
	}
}
