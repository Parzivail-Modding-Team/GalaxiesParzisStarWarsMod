package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.BlasterBoltEntity;
import com.parzivail.pswg.entity.BlasterIonBoltEntity;
import com.parzivail.pswg.entity.BlasterStunBoltEntity;
import com.parzivail.pswg.entity.ThrownLightsaberEntity;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import com.parzivail.pswg.entity.droid.AstromechEntity;
import com.parzivail.pswg.entity.mammal.BanthaEntity;
import com.parzivail.pswg.entity.rodent.SandSkitterEntity;
import com.parzivail.pswg.entity.ship.SpeederEntity;
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

	public static class Speeder
	{
		public static final EntityType<SpeederEntity> X34 = Registry.register(Registry.ENTITY_TYPE, Resources.id("landspeeder_x34"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, SpeederEntity::new)
				.dimensions(EntityDimensions.fixed(1, 1))
				.trackRangeBlocks(128)
				.build());

		static void register()
		{
			entityTypes.add(X34);
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

	public static class Mammal
	{
		public static final EntityType<BanthaEntity> Bantha = Registry.register(Registry.ENTITY_TYPE, Resources.id("bantha"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, BanthaEntity::new)
				.dimensions(EntityDimensions.fixed(3, 3))
				.build());

		static void register()
		{
			entityTypes.add(Bantha);
			FabricDefaultAttributeRegistry.register(Bantha, BanthaEntity.createAttributes());
		}
	}

	public static class Rodent
	{
		public static final EntityType<SandSkitterEntity> SandSkitter = Registry.register(Registry.ENTITY_TYPE, Resources.id("sand_skitter"), FabricEntityTypeBuilder
				.create(SpawnGroup.MISC, SandSkitterEntity::new)
				.dimensions(EntityDimensions.fixed(0.4f, 0.3f))
				.trackRangeBlocks(8)
				.build());

		static void register()
		{
			entityTypes.add(SandSkitter);
			FabricDefaultAttributeRegistry.register(SandSkitter, SandSkitterEntity.createAttributes());
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

	public static class Droid
	{
		public static final EntityType<AstromechEntity> AstroR2 = Registry.register(
				Registry.ENTITY_TYPE,
				Resources.id("artoo"),
				FabricEntityTypeBuilder
						.<AstromechEntity>create(SpawnGroup.MISC, (type, world) -> new AstromechEntity(type, world, new AstromechEntity.AstromechParameters("r2d2")))
						.dimensions(EntityDimensions.fixed(0.75f, 1.2f))
						.trackRangeBlocks(128)
						.build()
		);

		public static final EntityType<AstromechEntity> AstroR2Imperial = Registry.register(
				Registry.ENTITY_TYPE,
				Resources.id("artoo_imperial"),
				FabricEntityTypeBuilder
						.<AstromechEntity>create(SpawnGroup.MISC, (type, world) -> new AstromechEntity(type, world, new AstromechEntity.AstromechParameters("r2q5")))
						.dimensions(EntityDimensions.fixed(0.75f, 1.2f))
						.trackRangeBlocks(128)
						.build()
		);

		static void register()
		{
			entityTypes.add(AstroR2);
			entityTypes.add(AstroR2Imperial);
			FabricDefaultAttributeRegistry.register(AstroR2, AstromechEntity.createAttributes());
			FabricDefaultAttributeRegistry.register(AstroR2Imperial, AstromechEntity.createAttributes());
		}
	}

	public static class Misc
	{
		public static final EntityType<BlasterBoltEntity> BlasterBolt = Registry.register(Registry.ENTITY_TYPE, Resources.id("blaster_bolt"), FabricEntityTypeBuilder
				.<BlasterBoltEntity>create(SpawnGroup.MISC, BlasterBoltEntity::new)
				.forceTrackedVelocityUpdates(false)
				.dimensions(EntityDimensions.fixed(0.1f, 0.1f))
				.trackRangeBlocks(120)
				.build());

		public static final EntityType<BlasterStunBoltEntity> BlasterStunBolt = Registry.register(Registry.ENTITY_TYPE, Resources.id("blaster_stun_bolt"), FabricEntityTypeBuilder
				.<BlasterStunBoltEntity>create(SpawnGroup.MISC, BlasterStunBoltEntity::new)
				.forceTrackedVelocityUpdates(false)
				.dimensions(EntityDimensions.fixed(0.1f, 0.1f))
				.trackRangeBlocks(120)
				.build());

		public static final EntityType<BlasterIonBoltEntity> BlasterIonBolt = Registry.register(Registry.ENTITY_TYPE, Resources.id("blaster_ion_bolt"), FabricEntityTypeBuilder
				.<BlasterIonBoltEntity>create(SpawnGroup.MISC, BlasterIonBoltEntity::new)
				.forceTrackedVelocityUpdates(false)
				.dimensions(EntityDimensions.fixed(0.1f, 0.1f))
				.trackRangeBlocks(120)
				.build());

		public static final EntityType<ThrownLightsaberEntity> ThrownLightsaber = Registry.register(Registry.ENTITY_TYPE, Resources.id("thrown_lightsaber"), FabricEntityTypeBuilder
				.<ThrownLightsaberEntity>create(SpawnGroup.MISC, ThrownLightsaberEntity::new)
				.forceTrackedVelocityUpdates(false)
				.dimensions(EntityDimensions.fixed(0.5f, 0.5f))
				.trackRangeBlocks(50)
				.build());

		static void register()
		{
			entityTypes.add(BlasterBolt);
			entityTypes.add(ThrownLightsaber);
		}
	}

	public static void register()
	{
		Ship.register();
		Speeder.register();
		Fish.register();
		Amphibian.register();
		Mammal.register();
		Rodent.register();
		Droid.register();
		Misc.register();
	}
}
