package dev.pswg.feature.brewing;

import net.minecraft.entity.effect.StatusEffectInstance;

public class EffectCell extends BrewingCell
{
	public StatusEffectInstance statusEffect;

	public EffectCell(StatusEffectInstance statusEffectInstance, BrewingCellType cellType)
	{
		super(cellType);
		this.statusEffect = statusEffectInstance;
	}
}
