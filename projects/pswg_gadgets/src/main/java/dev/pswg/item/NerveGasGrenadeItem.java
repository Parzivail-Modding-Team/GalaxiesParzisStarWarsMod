package dev.pswg.item;

import dev.pswg.Gadgets;
import dev.pswg.entity.grenades.GrenadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

public class NerveGasGrenadeItem extends GrenadeItem
{
	public NerveGasGrenadeItem(Item.Settings settings)
	{
		super(settings, Gadgets.NERVE_GAS_GRENADE_ITEM, 80, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public EntityType<? extends GrenadeEntity> getEntityType()
	{
		return Gadgets.NERVE_GAS_GRENADE_ENTITY;
	}
}
