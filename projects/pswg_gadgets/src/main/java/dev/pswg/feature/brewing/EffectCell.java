package dev.pswg.feature.brewing;

import net.minecraft.world.effect.MobEffectInstance;

public class EffectCell extends BrewingCell
{
	public MobEffectInstance statusEffect;

	public EffectCell(MobEffectInstance statusEffectInstance, BrewingCellType cellType)
	{
		super(cellType);
		this.statusEffect = statusEffectInstance;
	}
}
