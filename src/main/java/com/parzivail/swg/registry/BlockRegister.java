package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.block.*;
import com.parzivail.swg.block.console.*;
import com.parzivail.util.block.PBlock;
import com.parzivail.util.block.PBlockConnected;
import com.parzivail.util.block.PBlockContainer;
import com.parzivail.util.block.PBlockPillar;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Created by colby on 12/26/2017.
 */
public class BlockRegister
{
	public static PBlock fastGrass;

	public static PBlock oxidizedSand;
	public static PBlock oxidizedSandStone;
	public static PBlock hothStone;

	public static PBlockPillar endorLog;

	public static PBlock labWall;

	public static PBlockContainer blasterWorkbench;
	public static PBlockContainer sabaccTable;

	public static PBlockContainer consoleHoth1;
	public static PBlockContainer consoleHothCurved1;
	public static PBlockContainer consoleHothCurved2;
	public static PBlockContainer consoleHothCurved3;
	public static PBlockContainer consoleHothMedical1;
	public static PBlockContainer consoleHothMedical2;

	public static PBlock white;
	public static PBlock gray;
	public static PBlock grayBevel;
	public static PBlockConnected grayGrate;
	public static PBlockPillar grayLight;
	public static PBlockPillar grayLightVert;
	public static PBlock darkGray;
	public static PBlockConnected darkGrayGrate;
	public static PBlockPillar darkGrayLight;
	public static PBlockPillar darkGrayLightVert;
	public static PBlock black;
	public static PBlockConnected blackGrate;
	public static PBlock caution;

	public static PBlock crate;
	public static PBlock hardpackSnow;
	public static PBlock hothDoor;
	public static PBlock hothSnowCut;
	public static PBlock mud;
	public static PBlock tatooineSand;
	public static PBlock templeStone;
	public static PBlock templeStoneBrick;
	public static PBlock templeStoneBrickFancy;
	public static PBlock templeStoneSlab;
	public static PBlock templeStoneSlabTop;
	public static PBlock templeStoneSlabTopDark;

	public static void register()
	{
		register(fastGrass = new BlockFastGrass());

		register(oxidizedSand = new PBlock("oxidizedSand"));
		register(oxidizedSandStone = new PBlock("oxidizedSandStone"));
		register(hothStone = new PBlock("hothStone"));

		register(white = (PBlock)new PBlock("metalWhite", "white").setStepSound(Block.soundTypeMetal));
		register(gray = (PBlock)new PBlock("metalGray", "gray").setStepSound(Block.soundTypeMetal));
		register(grayBevel = (PBlock)new PBlock("metalGrayBevel", "grayBevel").setStepSound(Block.soundTypeMetal));
		register(darkGray = (PBlock)new PBlock("metalDarkGray", "darkGray").setStepSound(Block.soundTypeMetal));
		register(black = (PBlock)new PBlock("metalBlack", "black").setStepSound(Block.soundTypeMetal));
		register(caution = (PBlock)new PBlock("metalCaution", "caution").setStepSound(Block.soundTypeMetal));

		register(grayGrate = (PBlockConnected)new PBlockConnected("grateLightGray", "grateLightGray", "grate", Material.iron).setStepSound(Block.soundTypeMetal));
		register(darkGrayGrate = (PBlockConnected)new PBlockConnected("grateDarkGray", "grateDarkGray", "grate", Material.iron).setStepSound(Block.soundTypeMetal));
		register(blackGrate = (PBlockConnected)new PBlockConnected("grateBlack", "grateBlack", "grate", Material.iron).setStepSound(Block.soundTypeMetal));

		register(grayLight = (PBlockPillar)new BlockGrayLight().setStepSound(Block.soundTypeMetal));
		register(darkGrayLight = (PBlockPillar)new BlockDarkGrayLight().setStepSound(Block.soundTypeMetal));
		register(grayLightVert = (PBlockPillar)new BlockGrayLightVertical().setStepSound(Block.soundTypeMetal));
		register(darkGrayLightVert = (PBlockPillar)new BlockDarkGrayLightVertical().setStepSound(Block.soundTypeMetal));

		register(crate = new PBlock("crate"));
		register(hardpackSnow = new PBlock("hardpackSnow"));
		register(hothDoor = new PBlock("hothDoor"));
		register(hothSnowCut = new PBlock("hothSnowCut"));
		register(mud = new PBlock("mud"));

		register(templeStone = new PBlock("templeStone"));
		register(templeStoneBrick = new PBlock("templeStoneBrick"));
		register(templeStoneBrickFancy = new PBlock("templeStoneBrickFancy"));
		register(templeStoneSlab = new PBlock("templeStoneSlab"));
		register(templeStoneSlabTop = new PBlock("templeStoneSlabTop"));
		register(templeStoneSlabTopDark = new PBlock("templeStoneSlabTopDark"));

		register(endorLog = new BlockEndorLog());

		register(labWall = new PBlock("labWall"));

		register(tatooineSand = new PBlock("tatooineSand"));

		register(blasterWorkbench = new BlockBlasterWorkbench().setAlpha());
		register(sabaccTable = new BlockSabaccTable().setAlpha());

		register(consoleHoth1 = new BlockPanelHoth());
		register(consoleHothCurved1 = new BlockConsoleHoth1());
		register(consoleHothCurved2 = new BlockConsoleHoth2());
		register(consoleHothCurved3 = new BlockConsoleHoth3());
		register(consoleHothMedical1 = new BlockMedicalConsole());
		register(consoleHothMedical2 = new BlockMedicalConsole2());
	}

	private static void register(PBlock item)
	{
		GameRegistry.registerBlock(item, item.name);
	}

	private static void register(PBlockPillar item)
	{
		GameRegistry.registerBlock(item, item.name);
	}

	private static void register(PBlockContainer item)
	{
		GameRegistry.registerBlock(item, item.name);
		GameRegistry.registerTileEntity(item.createNewTileEntity(null, 0).getClass(), Resources.tileDot(item.name));
	}
}
