package com.parzivail.pswg;

import com.parzivail.pswg.api.PswgClientAddon;
import com.parzivail.pswg.client.species.SwgSpeciesIcons;
import com.parzivail.pswg.client.species.SwgSpeciesRenderer;
import com.parzivail.pswg.container.SwgSpeciesRegistry;

public class BaseClientContent implements PswgClientAddon
{
	@Override
	public void onPswgClientReady()
	{
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_AQUALISH, Resources.id("textures/gui/character/icons.png"), 8);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_AQUALISH, SwgSpeciesRenderer.humanoidBase(Resources.id("species/aqualish")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_BITH, Resources.id("textures/gui/character/icons.png"), 12);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_BITH, SwgSpeciesRenderer.humanoidBase(Resources.id("species/bith")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, Resources.id("textures/gui/character/icons.png"), 9);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHAGRIAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/chagrian")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_CHISS, Resources.id("textures/gui/character/icons.png"), 3);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_CHISS, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_KAMINOAN, Resources.id("textures/gui/character/icons.png"), 15);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_KAMINOAN, SwgSpeciesRenderer.fullModel(Resources.id("species/kaminoan")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_JAWA, Resources.id("textures/gui/character/icons.png"), 1);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_JAWA, SwgSpeciesRenderer.humanoidBase(Resources.id("species/jawa")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TOGRUTA, Resources.id("textures/gui/character/icons.png"), 10);
		SwgSpeciesRenderer.register(
				SwgSpeciesRegistry.SPECIES_TOGRUTA,
				SwgSpeciesRenderer.humanoidBase(Resources.id("species/togruta_m")),
				SwgSpeciesRenderer.humanoidBase(Resources.id("species/togruta_f")),
				null
		);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TWILEK, Resources.id("textures/gui/character/icons.png"), 6);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_TWILEK, SwgSpeciesRenderer.humanoidBase(Resources.id("species/twilek")), SwgSpeciesRenderer::animateTwilek);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_HUMAN, Resources.id("textures/gui/character/icons.png"), 2);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_HUMAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_PANTORAN, Resources.id("textures/gui/character/icons.png"), 4);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_PANTORAN, SwgSpeciesRenderer.humanoidBase(Resources.id("species/human")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_WOOKIEE, Resources.id("textures/gui/character/icons.png"), 13);
		SwgSpeciesRenderer.register(SwgSpeciesRegistry.SPECIES_WOOKIEE, SwgSpeciesRenderer.fullModel(Resources.id("species/wookiee")), null);

		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_DEVARONIAN, Resources.id("textures/gui/character/icons.png"), 5);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_DUROS, Resources.id("textures/gui/character/icons.png"), 7);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_RODIAN, Resources.id("textures/gui/character/icons.png"), 11);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_TRANDOSHAN, Resources.id("textures/gui/character/icons.png"), 14);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_MON_CALAMARI, Resources.id("textures/gui/character/icons.png"), 16);
		SwgSpeciesIcons.register(SwgSpeciesRegistry.SPECIES_IKTOTCHI, Resources.id("textures/gui/character/icons.png"), 17);
	}
}
