package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.gas.NerveGasEntity;
import dev.pswg.entity.gas.SmokeGasEntity;
import dev.pswg.entity.grenades.*;
import dev.pswg.entity.mines.PressureMineEntity;
import dev.pswg.entity.mines.TripwireMineEntity;
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
	public static final EntityType<SmokeSignalGrenadeEntity> SMOKE_SIGNAL_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("smoke_grenade"),
			EntityType.Builder.create(SmokeSignalGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.2f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);
	public static final EntityType<ImpactGrenadeEntity> IMPACT_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("impact_grenade"),
			EntityType.Builder.create(ImpactGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.3f, 0.3f)
			                  .spawnBoxScale(0.3f)
			                  .dropsNothing()
	);
	public static final EntityType<InfernoGrenadeEntity> INFERNO_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("inferno_grenade"),
			EntityType.Builder.create(InfernoGrenadeEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.15f, 0.3f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);
	public static final EntityType<PressureMineEntity> PRESSURE_MINE_ENTITY = Registrar.entityType(
			Gadgets.id("pressure_mine"),
			EntityType.Builder.create(PressureMineEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.1f)
			                  .spawnBoxScale(0.2f)
			                  .dropsNothing()
	);

	public static final EntityType<TripwireMineEntity> TRIPWIRE_MINE_ENTITY = Registrar.entityType(
			Gadgets.id("tripwire_mine"),
			EntityType.Builder.create(TripwireMineEntity::new, SpawnGroup.MISC)
			                  .dimensions(0.2f, 0.1f)
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
