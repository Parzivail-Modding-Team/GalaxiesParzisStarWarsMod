package com.parzivail.swg.entity.ship;

import com.parzivail.util.math.lwjgl.Vector3f;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityT65 extends EntityShip
{
	private static final ShipData shipData;
	private static final Vector3f SEAT_POS = new Vector3f(0, 1, 0);

	static
	{
		shipData = new ShipData();
	}

	public EntityT65(World worldIn)
	{
		super(worldIn);
	}

	@Override
	public ShipData getData()
	{
		return shipData;
	}

	@Override
	public Vector3f getSeatPosition(int seatIdx)
	{
		return SEAT_POS;
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
		return true;
	}
}
