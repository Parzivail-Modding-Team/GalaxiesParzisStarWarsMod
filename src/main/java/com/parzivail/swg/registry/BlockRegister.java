package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.block.*;
import com.parzivail.swg.block.antenna.BlockSatelliteDish;
import com.parzivail.swg.block.atmosphere.BlockSoundHothTelemetry;
import com.parzivail.util.block.*;
import com.parzivail.util.item.PItemBlockDecoration;
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
	public static PBlockContainer soundHothTelemetry;
	public static PBlockContainer gunRack;
	public static PBlockContainer satelliteDish;

	public static PDecorativeBlock panelHoth;
	public static PDecorativeBlock consoleHothCurved1;
	public static PDecorativeBlock consoleHothCurved2;
	public static PDecorativeBlock consoleHothCurved3;
	public static PDecorativeBlock consoleHothMedical1;
	public static PDecorativeBlock consoleHothMedical2;

	public static PDecorativeBlock crate1;
	public static PDecorativeBlock crateHoth1;
	public static PDecorativeBlock crateHoth2;
	public static PDecorativeBlock crateMosEspa;
	public static PDecorativeBlock crateVilla;

	public static PDecorativeBlock floorLight;
	public static PDecorativeBlock floorLight2;
	public static PDecorativeBlock ceilingLight;
	public static PDecorativeBlock ceilingLight2;
	public static PDecorativeBlock angledWallLight;
	public static PDecorativeBlock floorLightDome;
	public static PDecorativeBlock wallIndicatorLight;
	public static PDecorativeBlock wallIndicatorLightCluster;

	public static PDecorativeBlock wallControlPanel;
	public static PDecorativeBlock wallControlPanelTall;

	public static PBlock ladder;

	public static PDecorativeBlock antennaThin;
	public static PDecorativeBlock airTank;

	public static PDecorativeBlock moistureVaporator;
	public static PDecorativeBlock moistureVaporator2;
	public static PDecorativeBlock spokedMachine;
	public static PDecorativeBlock tubeMachine;

	public static PDecorativeBlock pipeSmallBent;
	public static PDecorativeBlock quadVentPipe;
	public static PDecorativeBlock tallVentedPipe;
	public static PDecorativeBlock wallPipeLarge;

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

		// Normal tile entities
		register(blasterWorkbench = new BlockBlasterWorkbench().withPlaceholderTexture());
		register(sabaccTable = new BlockSabaccTable().withPlaceholderTexture());
		register(soundHothTelemetry = new BlockSoundHothTelemetry());

		// Tile entities that need to keep a TESR
		register(gunRack = new BlockGunRack());
		register(satelliteDish = new BlockSatelliteDish());

		// Specialty decoration blocks
		register(ladder = new BlockLadder());

		// Decoration blocks
		registerDecoration("blockPanelHoth");
		registerDecoration("blockConsoleHoth1");
		registerDecoration("blockConsoleHoth2");
		registerDecoration("blockConsoleHoth3");
		registerDecoration("medicalConsole").setBlockBounds(0.8f, 1);
		registerDecoration("medicalConsole2");

		registerDecoration("crate1").setBlockBounds(0.9f, 1);
		registerDecoration("crateHoth1");
		registerDecoration("crateHoth2");
		registerDecoration("crateMosEspa").setBlockBounds(0.8f, 1);
		registerDecoration("crateVilla").setBlockBounds(0.5f, 1);

		registerDecoration("wallIndicator").setBlockBounds(0.5f, 0.25f, 0.5f);
		registerDecoration("wallIndicatorCluster").setBlockBounds(0.5f, 0.25f, 0.5f);
		registerDecoration("wallControlPanel").setBlockBounds(0.5f, 0.25f, 0.5f);
		registerDecoration("wallControlPanelTall").setBlockBounds(0.5f, 0.25f, 0.5f);

		registerDecoration("floorLight").setBlockBounds(0.5f, 0.25f).setLightLevel(1);
		registerDecoration("floorLight2").setBlockBounds(0.7f, 0.4f).setLightLevel(1);
		registerDecoration("hothCeilingLight").setBlockBounds(0.25f, 0.85f, 0.15f).setLightLevel(1);
		registerDecoration("hothCeilingLight2").setLightLevel(1);
		registerDecoration("angledWallLight").setBlockBounds(0.5f, 0.25f, 0.5f).setLightLevel(1);
		registerDecoration("floorLightDome").setBlockBounds(0.5f, 0.5f).setTransparent().setLightLevel(1);

		registerDecoration("antennaThin").setBlockBounds(0.125f, 1);
		registerDecoration("airTank").setBlockBounds(0.8f, 1);

		registerDecoration("moistureVaporator");
		registerDecoration("moistureVaporator2");
		registerDecoration("spokedMachine");
		registerDecoration("tubeMachine");

		registerDecoration("pipeSmallBent").setBlockBounds(0.5f, 0.5f);
		registerDecoration("quadVentPipe");
		registerDecoration("tallVentedPipe");
		registerDecoration("wallPipeLarge").setBlockBounds(0.5f, 0.5f);

		//registerDecoration("clampedCable");
		registerDecoration("yavinGenerator");

		registerConnectingDecoration("groundCable").setBlockBounds(1, 0.2f);
	}

	private static PDecorativeBlock registerDecoration(String name)
	{
		PDecorativeBlock block = new PDecorativeBlock(name);
		StarWarsGalaxy.proxy.registerModel(block);
		GameRegistry.registerBlock(block, PItemBlockDecoration.class, block.name);
		return block;
	}

	private static PDecorativeBlock registerConnectingDecoration(String name, Block... connectsTo)
	{
		PDecorativeBlock block = new PDecorativeBlock(name);
		block.setConnectsTo(connectsTo);
		StarWarsGalaxy.proxy.registerModel(block);
		GameRegistry.registerBlock(block, block.name);
		return block;
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
