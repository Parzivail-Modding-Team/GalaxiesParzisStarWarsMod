package com.parzivail.swg.block;

import com.parzivail.swg.Resources;
import com.parzivail.util.block.PBlockPillar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class BlockGrayLight extends PBlockPillar
{
	public BlockGrayLight()
	{
		super("grayLight");
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg)
	{
		// side icon
		field_150167_a = new IIcon[3];

		// top icon
		field_150166_b = new IIcon[1];

		field_150167_a[0] = reg.registerIcon(Resources.modColon("grayLight"));

		field_150166_b[0] = reg.registerIcon(Resources.modColon("gray"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side)
	{
		int meta = worldIn.getBlockMetadata(x, y, z);
		int k = meta & 12;
		int l = meta & 3;

		if (y <= 0 || meta != 0)
			return k == 0 && (side == 1 || side == 0) ? getTopIcon(l) : (k == 4 && (side == 5 || side == 4) ? getTopIcon(l) : (k == 8 && (side == 2 || side == 3) ? getTopIcon(l) : getSideIcon(l)));

		return k == 0 && (side == 1 || side == 0) ? getTopIcon(l) : (k == 4 && (side == 5 || side == 4) ? getTopIcon(l) : (k == 8 && (side == 2 || side == 3) ? getTopIcon(l) : getSideIcon(l, worldIn, x, y, z)));
	}

	private IIcon getSideIcon(int icon, IBlockAccess worldIn, int x, int y, int z)
	{
		return field_150167_a[0];
	}
}
