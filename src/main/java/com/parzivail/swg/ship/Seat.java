package com.parzivail.swg.ship;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.MessageFlightModelUpdate;
import com.parzivail.util.common.Lumberjack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/27/2017.
 */
public class Seat extends EntityBase
{
	public BasicFlightModel ship;
	public Vector3f offset;
	public int idx;

	public Seat(World world)
	{
		super(world);
		setSize(0.5F, 0.5F);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound t)
	{
		this.idx = t.getInteger("idx");
		this.offset = new Vector3f(t.getFloat("ox"), t.getFloat("oy"), t.getFloat("oz"));

		if (t.hasKey("ship", 10))
		{
			Entity entity1 = EntityList.createEntityFromNBT(t.getCompoundTag("ship"), this.worldObj);

			if (entity1 != null)
			{
				BasicFlightModel ship = (BasicFlightModel)entity1;
				this.worldObj.spawnEntityInWorld(entity1);
				this.ship = ship;
			}
			else
			{
				Lumberjack.err("Failed to load ship from seat");
			}
		}
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound t)
	{
		t.setInteger("idx", this.idx);
		t.setFloat("ox", this.offset.x);
		t.setFloat("oy", this.offset.y);
		t.setFloat("oz", this.offset.z);
		if (this.ship != null)
		{
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			if (this.ship.writeMountToNBT(nbttagcompound1))
				t.setTag("ship", nbttagcompound1);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize()
	{
		return 0.0F;
	}

	public boolean canBeCollidedWith()
	{
		return true;
	}

	public void applyEntityCollision(Entity other)
	{
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return true;
	}

	/**
	 * First layer of player interaction
	 */
	public boolean interactFirst(EntityPlayer player)
	{
		if (this.riddenByEntity == null)
		{
			if (!this.worldObj.isRemote)
				player.mountEntity(this);
			return true;
		}
		return true;
	}

	@Override
	public void onUpdate()
	{
		if (this.ship != null)
			this.setLocationAndAngles(this.ship);
		if (!this.worldObj.isRemote)
		{
			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
				this.riddenByEntity = null;
		}
	}

	void setLocationAndAngles(BasicFlightModel ship)
	{
		Vector3f o = ship.orientation.findLocalVectorGlobally(offset);
		// TODO: add vertical centering offset to posY here (1.684f for xwing), need to store that in a static somewhere other than render!
		this.setLocationAndAngles(ship.posX + o.x, ship.posY + o.y + 1.684f, ship.posZ + o.z, 0, 0);
	}

	public void attachToShip(BasicFlightModel ship, Vector3f offset, int idx)
	{
		this.ship = ship;
		this.offset = offset;
		this.idx = idx;
	}

	public void acceptInput(ShipInput input)
	{
		// TODO: seats and delegate input to seats
		switch (input)
		{
			case RollLeft:
				this.ship.angularMomentum.z -= 4;
				break;
			case RollRight:
				this.ship.angularMomentum.z += 4;
				break;
			case PitchUp:
				this.ship.angularMomentum.x += 2;
				break;
			case PitchDown:
				this.ship.angularMomentum.x -= 2;
				break;
			case YawLeft:
				break;
			case YawRight:
				break;
			case ThrottleUp:
				this.ship.throttle += this.ship.data.maxThrottle * this.ship.data.acceleration;
				this.ship.throttle = MathHelper.clamp_float(this.ship.throttle, 0, this.ship.data.maxThrottle);
				break;
			case ThrottleDown:
				this.ship.throttle -= this.ship.data.maxThrottle * this.ship.data.acceleration;
				this.ship.throttle = MathHelper.clamp_float(this.ship.throttle, 0, this.ship.data.maxThrottle);
				break;
			case BlasterFire:
				break;
			case SpecialAesthetic:
				break;
			case SpecialWeapon:
				break;
		}
		StarWarsGalaxy.network.sendToServer(new MessageFlightModelUpdate(this.ship));
	}
}
