package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import com.parzivail.pswg.entity.debug.KinematicTestEntity;
import com.parzivail.pswg.entity.ship.T65BXwing;
import com.parzivail.util.entity.BucketableFishEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;

public class SwgEntities
{
	public static final ArrayList<EntityType<?>> entityTypes = new ArrayList<>();

	public static class Ship
	{
		public static final EntityType<T65BXwing> T65bXwing = Registry.register(Registry.ENTITY_TYPE, Resources.id("xwing_t65b"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, T65BXwing::new)
				.dimensions(EntityDimensions.fixed(1, 1))
				.trackRangeBlocks(128)
				.build());

		static void register()
		{
			entityTypes.add(T65bXwing);
		}
	}

	public static class Amphibian
	{

		public static final EntityType<WorrtEntity> Worrt = Registry.register(
				Registry.ENTITY_TYPE,
				Resources.id("worrt"),
				FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WorrtEntity::new).dimensions(EntityDimensions.fixed(0.6f, 0.6f)).build()
		);

		static void register()
		{
			entityTypes.add(Worrt);
			FabricDefaultAttributeRegistry.register(Worrt, WorrtEntity.createAttributes());
		}
	}

	public static class Fish
	{
		public static final EntityType<BucketableFishEntity> Faa = Registry.register(
				Registry.ENTITY_TYPE,
				Resources.id("faa"),
				FabricEntityTypeBuilder.create(SpawnGroup.WATER_AMBIENT, (EntityType<BucketableFishEntity> entityType, World world) -> new BucketableFishEntity(entityType, world, SwgItems.MobDrops.FaaBucket))
				                       .dimensions(EntityDimensions.fixed(0.5F, 0.3F))
				                       .build()
		);

		public static final EntityType<BucketableFishEntity> Laa = Registry.register(
				Registry.ENTITY_TYPE,
				Resources.id("laa"),
				FabricEntityTypeBuilder.create(SpawnGroup.WATER_AMBIENT, (EntityType<BucketableFishEntity> entityType, World world) -> new BucketableFishEntity(entityType, world, SwgItems.MobDrops.LaaBucket))
				                       .dimensions(EntityDimensions.fixed(0.5F, 0.3F))
				                       .build()
		);

		static void register()
		{
			entityTypes.add(Faa);
			FabricDefaultAttributeRegistry.register(Faa, BucketableFishEntity.createAttributes());

			entityTypes.add(Laa);
			FabricDefaultAttributeRegistry.register(Laa, BucketableFishEntity.createAttributes());
		}
	}

	public static class Misc
	{
		public static final EntityType<BlasterBoltEntity> BlasterBolt = Registry.register(Registry.ENTITY_TYPE, Resources.id("blaster_bolt"), FabricEntityTypeBuilder
				.<BlasterBoltEntity>create(SpawnGroup.MISC, BlasterBoltEntity::new)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.trackRangeBlocks(40)
				.build());

		public static final EntityType<ThrownLightsaberEntity> ThrownLightsaber = Registry.register(Registry.ENTITY_TYPE, Resources.id("thrown_lightsaber"), FabricEntityTypeBuilder
				.<ThrownLightsaberEntity>create(SpawnGroup.MISC, ThrownLightsaberEntity::new)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.trackRangeBlocks(40)
				.build());

		public static final EntityType<KinematicTestEntity> KinematicTest = Registry.register(Registry.ENTITY_TYPE, Resources.id("kinematic_test"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, KinematicTestEntity::new)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.build());

		static void register()
		{
			entityTypes.add(KinematicTest);
			entityTypes.add(BlasterBolt);
			entityTypes.add(ThrownLightsaber);
		}
	}

	public static void register()
	{
		Ship.register();
		Fish.register();
		Amphibian.register();
		Misc.register();
	}
}
