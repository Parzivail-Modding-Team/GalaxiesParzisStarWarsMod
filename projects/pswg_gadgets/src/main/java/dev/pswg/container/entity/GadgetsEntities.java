package dev.pswg.container.entity;

import dev.pswg.Gadgets;
import dev.pswg.entity.gas.NerveGasEntity;
import dev.pswg.entity.gas.SmokeGasEntity;
import dev.pswg.entity.grenades.*;
import dev.pswg.entity.mines.PressureMineEntity;
import dev.pswg.entity.mines.TripwireMineEntity;
import dev.pswg.registry.Registrar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class GadgetsEntities
{
	public static final EntityType<FragmentationGrenadeEntity> FRAGMENTATION_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("fragmentation_grenade"),
			EntityType.Builder.of(FragmentationGrenadeEntity::new, MobCategory.MISC)
			                  .sized(0.2f, 0.4f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);
	public static final EntityType<ThermalDetonatorEntity> THERMAL_DETONATOR_ENTITY = Registrar.entityType(
			Gadgets.id("thermal_detonator"),
			EntityType.Builder.of(ThermalDetonatorEntity::new, MobCategory.MISC)
			                  .sized(0.2f, 0.2f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()

	);
	public static final EntityType<NerveGasGrenadeEntity> NERVE_GAS_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("nerve_gas_grenade"),
			EntityType.Builder.of(NerveGasGrenadeEntity::new, MobCategory.MISC)
			                  .sized(0.2f, 0.4f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);
	public static final EntityType<SmokeSignalGrenadeEntity> SMOKE_SIGNAL_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("smoke_grenade"),
			EntityType.Builder.of(SmokeSignalGrenadeEntity::new, MobCategory.MISC)
			                  .sized(0.2f, 0.2f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);
	public static final EntityType<ImpactGrenadeEntity> IMPACT_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("impact_grenade"),
			EntityType.Builder.of(ImpactGrenadeEntity::new, MobCategory.MISC)
			                  .sized(0.3f, 0.3f)
			                  .spawnDimensionsScale(0.3f)
			                  .noLootTable()
	);
	public static final EntityType<InfernoGrenadeEntity> INFERNO_GRENADE_ENTITY = Registrar.entityType(
			Gadgets.id("inferno_grenade"),
			EntityType.Builder.of(InfernoGrenadeEntity::new, MobCategory.MISC)
			                  .sized(0.15f, 0.3f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);
	public static final EntityType<PressureMineEntity> PRESSURE_MINE_ENTITY = Registrar.entityType(
			Gadgets.id("pressure_mine"),
			EntityType.Builder.of(PressureMineEntity::new, MobCategory.MISC)
			                  .sized(0.2f, 0.1f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);

	public static final EntityType<TripwireMineEntity> TRIPWIRE_MINE_ENTITY = Registrar.entityType(
			Gadgets.id("tripwire_mine"),
			EntityType.Builder.of(TripwireMineEntity::new, MobCategory.MISC)
			                  .sized(0.2f, 0.1f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);
	public static final EntityType<NerveGasEntity> NERVE_GAS = Registrar.entityType(
			Gadgets.id("nerve_gas"),
			EntityType.Builder.of(NerveGasEntity::new, MobCategory.MISC)
			                  .sized(2f, 2f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);
	public static final EntityType<SmokeGasEntity> SMOKE_GAS = Registrar.entityType(
			Gadgets.id("smoke"),
			EntityType.Builder.of(SmokeGasEntity::new, MobCategory.MISC)
			                  .sized(2f, 2f)
			                  .spawnDimensionsScale(0.2f)
			                  .noLootTable()
	);


	public static void register()
	{

	}
}
