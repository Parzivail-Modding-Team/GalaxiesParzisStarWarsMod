package com.parzivail.util.block;

import com.parzivail.swg.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class PBlockConnected extends PBlock
{
	protected IIcon[] icons = new IIcon[16];
	private String folder;
	private String texture;

	public PBlockConnected(String name, Material mat)
	{
		this(name, name, name, mat);
	}

	public PBlockConnected(String unlocalName, String name, Material mat)
	{
		this(unlocalName, name, name, mat);
	}

	public PBlockConnected(String unlocalName, String folder, String name, Material mat)
	{
		super(unlocalName, mat);
		this.texture = name;
		this.folder = folder;
	}

	public IIcon getConnectedBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5, IIcon[] icons)
	{
		boolean isOpenUp = false, isOpenDown = false, isOpenLeft = false, isOpenRight = false;

		switch (par5)
		{
			case 0:
			case 1:
				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
					isOpenDown = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
					isOpenUp = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
					isOpenLeft = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
					isOpenRight = true;

				if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
					return icons[15];
				else if (isOpenUp && isOpenDown && isOpenLeft)
					return icons[11];
				else if (isOpenUp && isOpenDown && isOpenRight)
					return icons[12];
				else if (isOpenUp && isOpenLeft && isOpenRight)
					return icons[13];
				else if (isOpenDown && isOpenLeft && isOpenRight)
					return icons[14];
				else if (isOpenDown && isOpenUp)
					return icons[5];
				else if (isOpenLeft && isOpenRight)
					return icons[6];
				else if (isOpenDown && isOpenLeft)
					return icons[8];
				else if (isOpenDown && isOpenRight)
					return icons[10];
				else if (isOpenUp && isOpenLeft)
					return icons[7];
				else if (isOpenUp && isOpenRight)
					return icons[9];
				else if (isOpenDown)
					return icons[3];
				else if (isOpenUp)
					return icons[4];
				else if (isOpenLeft)
					return icons[2];
				else if (isOpenRight)
					return icons[1];
				break;
			case 2:
				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
					isOpenDown = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
					isOpenUp = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
					isOpenLeft = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
					isOpenRight = true;

				if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
					return icons[15];
				else if (isOpenUp && isOpenDown && isOpenLeft)
					return icons[13];
				else if (isOpenUp && isOpenDown && isOpenRight)
					return icons[14];
				else if (isOpenUp && isOpenLeft && isOpenRight)
					return icons[11];
				else if (isOpenDown && isOpenLeft && isOpenRight)
					return icons[12];
				else if (isOpenDown && isOpenUp)
					return icons[6];
				else if (isOpenLeft && isOpenRight)
					return icons[5];
				else if (isOpenDown && isOpenLeft)
					return icons[9];
				else if (isOpenDown && isOpenRight)
					return icons[10];
				else if (isOpenUp && isOpenLeft)
					return icons[7];
				else if (isOpenUp && isOpenRight)
					return icons[8];
				else if (isOpenDown)
					return icons[1];
				else if (isOpenUp)
					return icons[2];
				else if (isOpenLeft)
					return icons[4];
				else if (isOpenRight)
					return icons[3];
				break;
			case 3:
				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
					isOpenDown = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
					isOpenUp = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2 - 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 - 1, par3, par4)))
					isOpenLeft = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2 + 1, par3, par4), par1IBlockAccess.getBlockMetadata(par2 + 1, par3, par4)))
					isOpenRight = true;

				if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
					return icons[15];
				else if (isOpenUp && isOpenDown && isOpenLeft)
					return icons[14];
				else if (isOpenUp && isOpenDown && isOpenRight)
					return icons[13];
				else if (isOpenUp && isOpenLeft && isOpenRight)
					return icons[11];
				else if (isOpenDown && isOpenLeft && isOpenRight)
					return icons[12];
				else if (isOpenDown && isOpenUp)
					return icons[6];
				else if (isOpenLeft && isOpenRight)
					return icons[5];
				else if (isOpenDown && isOpenLeft)
					return icons[10];
				else if (isOpenDown && isOpenRight)
					return icons[9];
				else if (isOpenUp && isOpenLeft)
					return icons[8];
				else if (isOpenUp && isOpenRight)
					return icons[7];
				else if (isOpenDown)
					return icons[1];
				else if (isOpenUp)
					return icons[2];
				else if (isOpenLeft)
					return icons[3];
				else if (isOpenRight)
					return icons[4];
				break;
			case 4:
				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
					isOpenDown = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
					isOpenUp = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
					isOpenLeft = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
					isOpenRight = true;

				if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
					return icons[15];
				else if (isOpenUp && isOpenDown && isOpenLeft)
					return icons[14];
				else if (isOpenUp && isOpenDown && isOpenRight)
					return icons[13];
				else if (isOpenUp && isOpenLeft && isOpenRight)
					return icons[11];
				else if (isOpenDown && isOpenLeft && isOpenRight)
					return icons[12];
				else if (isOpenDown && isOpenUp)
					return icons[6];
				else if (isOpenLeft && isOpenRight)
					return icons[5];
				else if (isOpenDown && isOpenLeft)
					return icons[10];
				else if (isOpenDown && isOpenRight)
					return icons[9];
				else if (isOpenUp && isOpenLeft)
					return icons[8];
				else if (isOpenUp && isOpenRight)
					return icons[7];
				else if (isOpenDown)
					return icons[1];
				else if (isOpenUp)
					return icons[2];
				else if (isOpenLeft)
					return icons[3];
				else if (isOpenRight)
					return icons[4];
				break;
			case 5:
				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 - 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4)))
					isOpenDown = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3 + 1, par4), par1IBlockAccess.getBlockMetadata(par2, par3 + 1, par4)))
					isOpenUp = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3, par4 - 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 - 1)))
					isOpenLeft = true;

				if (this.shouldConnectToBlock(par1IBlockAccess, par2, par3, par4, par1IBlockAccess.getBlock(par2, par3, par4 + 1), par1IBlockAccess.getBlockMetadata(par2, par3, par4 + 1)))
					isOpenRight = true;

				if (isOpenUp && isOpenDown && isOpenLeft && isOpenRight)
					return icons[15];
				else if (isOpenUp && isOpenDown && isOpenLeft)
					return icons[13];
				else if (isOpenUp && isOpenDown && isOpenRight)
					return icons[14];
				else if (isOpenUp && isOpenLeft && isOpenRight)
					return icons[11];
				else if (isOpenDown && isOpenLeft && isOpenRight)
					return icons[12];
				else if (isOpenDown && isOpenUp)
					return icons[6];
				else if (isOpenLeft && isOpenRight)
					return icons[5];
				else if (isOpenDown && isOpenLeft)
					return icons[9];
				else if (isOpenDown && isOpenRight)
					return icons[10];
				else if (isOpenUp && isOpenLeft)
					return icons[7];
				else if (isOpenUp && isOpenRight)
					return icons[8];
				else if (isOpenDown)
					return icons[1];
				else if (isOpenUp)
					return icons[2];
				else if (isOpenLeft)
					return icons[4];
				else if (isOpenRight)
					return icons[3];
				break;
		}

		return icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		return par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 15 ? this.icons[0] : this.getConnectedBlockTexture(par1IBlockAccess, par2, par3, par4, par5, this.icons);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2)
	{
		return this.icons[0];
	}

	@Override
	public void registerIcons(IIconRegister reg)
	{
		this.icons[0] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture);
		this.icons[1] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_1_d");
		this.icons[2] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_1_u");
		this.icons[3] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_1_l");
		this.icons[4] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_1_r");
		this.icons[5] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_2_h");
		this.icons[6] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_2_v");
		this.icons[7] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_2_dl");
		this.icons[8] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_2_dr");
		this.icons[9] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_2_ul");
		this.icons[10] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_2_ur");
		this.icons[11] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_3_d");
		this.icons[12] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_3_u");
		this.icons[13] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_3_l");
		this.icons[14] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_3_r");
		this.icons[15] = reg.registerIcon(Resources.MODID + ":" + "connected/" + this.folder + "/" + this.texture + "_4");
	}

	/**
	 * This is checked to see if the texture should connect to this block
	 *
	 * @param par2 x
	 * @param par3 y
	 * @param par4 z
	 * @param par5 ID this block is asking to connect to (may be 0 if there is no
	 *             block)
	 * @param par6 Metadata of the block this block is trying to connect to
	 * @return true if should connect
	 */
	private boolean shouldConnectToBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, Block par5, int par6)
	{
		return par5 == this;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		Block b = par1IBlockAccess.getBlock(par2, par3, par4);
		return b != this && super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}
}