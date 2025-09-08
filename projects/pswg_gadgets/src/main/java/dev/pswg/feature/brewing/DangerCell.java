package dev.pswg.feature.brewing;

import net.minecraft.entity.effect.StatusEffectInstance;

public class DangerCell extends BrewingCell
{
	public StatusEffectInstance statusEffect;

	public DangerCell(StatusEffectInstance statusEffectInstance)
	{
		this.statusEffect = statusEffectInstance;
	}
}
