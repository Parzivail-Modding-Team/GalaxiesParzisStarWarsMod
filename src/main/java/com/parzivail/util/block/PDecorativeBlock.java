package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

public class PDecorativeBlock extends Block
{
	public final String name;
	private boolean transparent;
	private boolean passable;
	private boolean connectsToWires;
	private Block[] connectsTo;

	public PDecorativeBlock(String name)
	{
		super(Material.iron);
		this.name = name;
		setUnlocalizedName(Resources.modDot(this.name));
		setHardness(1.5F);
		setResistance(10.0F);
		setHarvestLevel("pickaxe", HarvestLevel.IRON);
		setCreativeTab(StarWarsGalaxy.tab);
	}

	public PDecorativeBlock setBlockBounds(float width, float height)
	{
		return setBlockBounds(width, 0, height);
	}

	public PDecorativeBlock setBlockBounds(float width, float minY, float height)
	{
		width /= 2;
		setBlockBounds(0.5f - width, minY, 0.5f - width, 0.5f + width, minY + height, 0.5f + width);
		return this;
	}

	@Override
	public int getRenderType()
	{
		return name.hashCode();
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
	 */
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return transparent ? 1 : 0;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, int x, int y, int z)
	{
		return passable || super.isPassable(worldIn, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z)
	{
		return passable ? null : super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
	}

	public PDecorativeBlock setTransparent()
	{
		transparent = true;
		return this;
	}

	public PDecorativeBlock setPassible()
	{
		passable = true;
		return this;
	}

	@Override
	public boolean canRenderInPass(int pass)
	{
		return pass == 0 || transparent;
	}

	public void setConnectsTo(Block[] connectsTo)
	{
		this.connectsTo = connectsTo;
	}

	public boolean doesConnectTo(Block other)
	{
		return other == this || ArrayUtils.contains(connectsTo, other);
	}
}
