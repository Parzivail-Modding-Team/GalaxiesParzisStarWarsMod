package com.parzivail.pswg.entity;

import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.container.SwgSpeciesRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MannequinEntity extends ArmorStandEntity
{
	private static final TrackedData<String> SPECIES = DataTracker.registerData(MannequinEntity.class, TrackedDataHandlerRegistry.STRING);

	public MannequinEntity(EntityType<? extends MannequinEntity> entityType, World world)
	{
		super(entityType, world);
	}

	public MannequinEntity(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		getDataTracker().startTracking(SPECIES, SwgSpeciesRegistry.METASPECIES_NONE.toString());
	}

	@Override
	public ItemStack getPickBlockStack()
	{
		return new ItemStack(SwgItems.Spawners.Mannequin);
	}

	@Override
	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand)
	{
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.isMarker() && itemStack.isOf(SwgItems.Spawners.MannequinWizard))
		{
			if (!this.getWorld().isClient)
				SwgSpeciesRegistry.openCharacterCustomizer((ServerPlayerEntity)player, this);
			return ActionResult.success(this.getWorld().isClient);
		}

		return super.interactAt(player, hitPos, hand);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag)
	{
		super.readCustomDataFromNbt(tag);
		setSpecies(tag.getString("species"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag)
	{
		super.writeCustomDataToNbt(tag);
		tag.putString("species", getSpecies());
	}

	public void setSpecies(String speciesString)
	{
		getDataTracker().set(SPECIES, speciesString);
	}

	public String getSpecies()
	{
		return getDataTracker().get(SPECIES);
	}
}
