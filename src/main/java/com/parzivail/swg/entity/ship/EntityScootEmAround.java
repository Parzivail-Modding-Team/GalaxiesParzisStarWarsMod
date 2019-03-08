package com.parzivail.swg.entity.ship;

import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityScootEmAround extends EntityShip
{
	private static final ShipData shipData;
	private static final Vector3f[] SEAT_POSITIONS = {
			new Vector3f(0, 0, 1.25f),
			new Vector3f(0.5f, 0, 0.75f),
			new Vector3f(-0.5f, 0, 0.75f),
			new Vector3f(0.5f, 0, -0.75f),
			new Vector3f(-0.5f, 0, -0.75f)
	};

	static
	{
		shipData = new ShipData();
		shipData.isAirVehicle = false;
		shipData.maxThrottle = 1f;
		shipData.verticalGroundingOffset = 1.5f;
		shipData.numSeats = 5;
	}

	public EntityScootEmAround(World worldIn)
	{
		super(worldIn);
		stepHeight = 5;
	}

	@Override
	public ShipData getData()
	{
		return shipData;
	}

	@Override
	public Vector3f getSeatPosition(int seatIdx)
	{
		return SEAT_POSITIONS[seatIdx];
	}

	@Override
	public boolean doesDrawOverlay(EntityPlayer player, ItemStack item)
	{
		return false;
	}

	@Override
	public void drawOverlay(ScaledResolution sr, EntityPlayer player, ItemStack item)
	{

	}

	@Override
	public boolean shouldHideHand(EntityPlayer player, ItemStack item)
	{
		return false;
	}
}
