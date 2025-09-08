package dev.pswg.feature.brewing;

import net.minecraft.entity.effect.StatusEffectInstance;

public class EffectCell extends BrewingCell
{
	public StatusEffectInstance statusEffect;

	public EffectCell(StatusEffectInstance statusEffectInstance)
	{
		this.statusEffect = statusEffectInstance;
	}
}
