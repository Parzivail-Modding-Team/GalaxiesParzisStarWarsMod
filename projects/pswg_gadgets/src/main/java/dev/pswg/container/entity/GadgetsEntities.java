package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.gas.NerveGasEntity;
import dev.pswg.entity.gas.SmokeGasEntity;
import dev.pswg.entity.grenades.FragmentationGrenadeEntity;
import dev.pswg.entity.grenades.NerveGasGrenadeEntity;
import dev.pswg.entity.grenades.SmokeGasGrenadeEntity;
import dev.pswg.entity.grenades.ThermalDetonatorEntity;
import dev.pswg.registry.Registrar;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class GadgetsEntities
{
	public static final EntityType<FragmentationGrenadeEntity> FRAGMENTATION_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("fragmentation_grenade"),
			EntityType.Builder.create(FragmentationGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.4f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);
	public static final EntityType<ThermalDetonatorEntity> THERMAL_DETONATOR_ENTITY = Registrar.entityType(
			Gadgets.id("thermal_detonator"),
			EntityType.Builder.create(ThermalDetonatorEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.2f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()

	);
	public static final EntityType<NerveGasGrenadeEntity> NERVE_GAS_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("nerve_gas_grenade"),
			EntityType.Builder.create(NerveGasGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.4f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);
	public static final EntityType<SmokeGasGrenadeEntity> SMOKE_GAS_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("smoke_grenade"),
			EntityType.Builder.create(SmokeGasGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.4f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);
	public static final EntityType<NerveGasEntity> NERVE_GAS = Registrar.entityType(
			Gadgets.id("nerve_gas"),
			EntityType.Builder.create(NerveGasEntity::new, SpawnGroup.MISC)
			                  .dimensions(2f, 2f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);
	public static final EntityType<SmokeGasEntity> SMOKE_GAS = Registrar.entityType(
			Gadgets.id("smoke"),
			EntityType.Builder.create(SmokeGasEntity::new, SpawnGroup.MISC)
			                  .dimensions(2f, 2f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);


	public static void register()
	{

	}
}
