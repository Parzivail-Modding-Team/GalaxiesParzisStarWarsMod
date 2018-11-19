package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.block.*;
import com.parzivail.swg.block.antenna.BlockAntennaThin;
import com.parzivail.swg.block.antenna.BlockSatelliteDish;
import com.parzivail.swg.block.atmosphere.BlockSoundHothTelemetry;
import com.parzivail.swg.block.console.*;
import com.parzivail.swg.block.crate.*;
import com.parzivail.swg.block.light.*;
import com.parzivail.swg.block.machine.BlockMV;
import com.parzivail.swg.block.machine.BlockMV2;
import com.parzivail.swg.block.machine.BlockSpokedMachine;
import com.parzivail.swg.block.machine.BlockTubeMachine;
import com.parzivail.swg.block.pipe.BlockPipeSmallBent;
import com.parzivail.swg.block.pipe.BlockQuadVentPipe;
import com.parzivail.swg.block.pipe.BlockTallVentedPipe;
import com.parzivail.swg.block.pipe.BlockWallPipeLarge;
import com.parzivail.util.block.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Created by colby on 12/26/2017.
 */
public class BlockRegister
{
	public static PBlock fastGrass;

	public static PBlockSand oxidizedSand;
	public static PBlock oxidizedSandStone;
	public static PBlockSand irradiatedSand;
	public static PBlockSand rhodocrositeSand;
	public static PBlockSand tatooineSand;

	public static PBlock hothStone;
	public static PBlock templeStone;
	public static PBlock templeStoneBrick;
	public static PBlock templeStoneBrickFancy;
	public static PBlock templeStoneSlab;
	public static PBlock templeStoneSlabTop;
	public static PBlock templeStoneSlabTopDark;

	public static PBlock concrete;
	public static PBlock lavaRock;
	public static PBlock oldBrick;
	public static PBlockLayer salt;
	public static PBlock bespinElevator;
	public static PBlock pumice;
	public static PBlock crate;
	public static PBlock hardpackSnow;
	public static PBlock hothDoor;
	public static PBlock hothSnowCut;
	public static PBlock hothSandbag;
	public static PBlock mud;

	public static PBlockPillar endorLog;
	public static PBlockPillar endorFallenLog;
	public static PBlockPillar palmLog;

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

	public static PBlock labWall;

	public static PBlock oreChromium;
	public static PBlock oreTitanium;
	public static PBlock oreRubindum;
	public static PBlock oreCortosis;

	public static PBlockContainer blasterWorkbench;
	public static PBlockContainer sabaccTable;

	public static PBlockContainer panelHoth;
	public static PBlockContainer consoleHothCurved1;
	public static PBlockContainer consoleHothCurved2;
	public static PBlockContainer consoleHothCurved3;
	public static PBlockContainer consoleHothMedical1;
	public static PBlockContainer consoleHothMedical2;

	public static PBlockContainer crate1;
	public static PBlockContainer crateHoth1;
	public static PBlockContainer crateHoth2;
	public static PBlockContainer crateMosEspa;
	public static PBlockContainer crateVilla;

	public static PBlockContainer floorLight;
	public static PBlockContainer floorLight2;
	public static PBlockContainer ceilingLight;
	public static PBlockContainer ceilingLight2;
	public static PBlockContainer angledWallLight;
	public static PBlockContainer floorLightDome;
	public static PBlockContainer wallIndicatorLight;
	public static PBlockContainer wallIndicatorLightCluster;

	public static PBlockContainer wallControlPanel;
	public static PBlockContainer wallControlPanelTall;

	public static PBlockContainer gunRack;
	public static PBlockContainer ladder;

	public static PBlockContainer antennaThin;
	public static PBlockContainer satelliteDish;
	public static PBlockContainer airTank;

	public static PBlockContainer moistureVaporator;
	public static PBlockContainer moistureVaporator2;
	public static PBlockContainer spokedMachine;
	public static PBlockContainer tubeMachine;

	public static PBlockContainer pipeSmallBent;
	public static PBlockContainer quadVentPipe;
	public static PBlockContainer tallVentedPipe;
	public static PBlockContainer wallPipeLarge;

	public static PBlockContainer soundHothTelemetry;

	public static void register()
	{
		register(fastGrass = new BlockFastGrass());

		register(oxidizedSand = new PBlockSand("oxidizedSand"));
		register(oxidizedSandStone = new PBlock("oxidizedSandStone"));
		register(irradiatedSand = new PBlockSand("irradiatedSand"));
		register(rhodocrositeSand = new PBlockSand("rhodocrositeSand"));
		register(hothStone = new PBlock("hothStone"));
		register(concrete = new PBlock("concrete"));
		register(lavaRock = (PBlock)new PBlock("lavaRock").setLightLevel(0.625f));
		register(oldBrick = new PBlock("oldBrick"));
		register(salt = new PBlockLayer("salt", ItemRegister.saltPile));
		register(bespinElevator = new PBlock("bespinElevator"));
		register(pumice = new PBlock("pumice"));
		register(crate = new PBlock("crate"));
		register(hardpackSnow = new PBlock("hardpackSnow"));
		register(hothDoor = new PBlock("hothDoor"));
		register(hothSnowCut = new PBlock("hothSnowCut"));
		register(hothSandbag = new PBlockConnected("hothSandbag", "hothSandbag", Material.craftedSnow));
		register(mud = new PBlock("mud").withHarvestLevel("shovel", HarvestLevel.STONE));

		register(white = (PBlock)new PBlock("metalWhite", "white").setStepSound(Block.soundTypeMetal));
		register(gray = (PBlock)new PBlock("metalGray", "gray").setStepSound(Block.soundTypeMetal));
		register(grayBevel = (PBlock)new PBlock("metalGrayBevel", "grayBevel").setStepSound(Block.soundTypeMetal));
		register(darkGray = (PBlock)new PBlock("metalDarkGray", "darkGray").setStepSound(Block.soundTypeMetal));
		register(black = (PBlock)new PBlock("metalBlack", "black").setStepSound(Block.soundTypeMetal));
		register(caution = (PBlock)new PBlock("metalCaution", "caution").setStepSound(Block.soundTypeMetal));

		register(grayGrate = (PBlockConnected)new PBlockConnected("grateLightGray", "grateLightGray", "grate", Material.iron).setStepSound(Block.soundTypeMetal));
		register(darkGrayGrate = (PBlockConnected)new PBlockConnected("grateDarkGray", "grateDarkGray", "grate", Material.iron).setStepSound(Block.soundTypeMetal));
		register(blackGrate = (PBlockConnected)new PBlockConnected("grateBlack", "grateBlack", "grate", Material.iron).setStepSound(Block.soundTypeMetal));

		register(grayLight = new BlockGrayLight());
		register(darkGrayLight = new BlockDarkGrayLight());
		register(grayLightVert = new BlockGrayLightVertical());
		register(darkGrayLightVert = new BlockDarkGrayLightVertical());

		register(templeStone = new PBlock("templeStone"));
		register(templeStoneBrick = new PBlock("templeStoneBrick"));
		register(templeStoneBrickFancy = new PBlock("templeStoneBrickFancy"));
		register(templeStoneSlab = new PBlock("templeStoneSlab"));
		register(templeStoneSlabTop = new PBlock("templeStoneSlabTop"));
		register(templeStoneSlabTopDark = new PBlock("templeStoneSlabTopDark"));

		register(endorLog = new BlockEndorLog());
		register(endorFallenLog = new BlockEndorFallenLog());
		register(palmLog = new BlockPalmLog());

		register(labWall = new PBlock("labWall"));

		register(oreChromium = new PBlock("chromium"));
		register(oreTitanium = new PBlock("titanium"));
		register(oreRubindum = new PBlock("rubindum"));
		register(oreCortosis = new PBlock("cortosis"));

		register(tatooineSand = new PBlockSand("tatooineSand"));

		register(blasterWorkbench = new BlockBlasterWorkbench().withPlaceholderTexture());
		register(sabaccTable = new BlockSabaccTable().withPlaceholderTexture());

		register(panelHoth = new BlockConsoleHoth1());
		register(consoleHothCurved1 = new BlockConsoleHothCurved1());
		register(consoleHothCurved2 = new BlockConsoleHothCurved2());
		register(consoleHothCurved3 = new BlockConsoleHothCurved3());
		register(consoleHothMedical1 = new BlockMedicalConsole());
		register(consoleHothMedical2 = new BlockMedicalConsole2());
		register(wallControlPanel = new BlockWallControlPanel());
		register(wallControlPanelTall = new BlockWallControlPanelTall());

		register(crate1 = new BlockCrate1());
		register(crateHoth1 = new BlockCrateHoth1());
		register(crateHoth2 = new BlockCrateHoth2());
		register(crateMosEspa = new BlockCrateMosEspa());
		register(crateVilla = new BlockCrateVilla());
		register(airTank = new BlockAirTank());

		register(floorLight = new BlockFloorLight());
		register(floorLight2 = new BlockFloorLight2());
		register(ceilingLight = new BlockHothCeilingLight());
		register(ceilingLight2 = new BlockHothCeilingLight2());
		register(angledWallLight = new BlockAngledWallLight());
		register(floorLightDome = new BlockFloorLightDome());
		register(wallIndicatorLight = new BlockWallIndicator());
		register(wallIndicatorLightCluster = new BlockWallIndicatorCluster());

		register(gunRack = new BlockGunRack());
		register(ladder = new BlockLadder());

		register(antennaThin = new BlockAntennaThin());
		register(satelliteDish = new BlockSatelliteDish());

		register(moistureVaporator = new BlockMV());
		register(moistureVaporator2 = new BlockMV2());
		register(spokedMachine = new BlockSpokedMachine());
		register(tubeMachine = new BlockTubeMachine());

		register(pipeSmallBent = new BlockPipeSmallBent());
		register(quadVentPipe = new BlockQuadVentPipe());
		register(tallVentedPipe = new BlockTallVentedPipe());
		register(wallPipeLarge = new BlockWallPipeLarge());

		register(soundHothTelemetry = new BlockSoundHothTelemetry());
	}

	private static void register(PBlock item)
	{
		GameRegistry.registerBlock(item, item.name);
	}

	private static void register(PBlockSand item)
	{
		GameRegistry.registerBlock(item, item.name);
	}

	private static void register(PBlockLayer item)
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
