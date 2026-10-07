package dev.pswg.datagen;

import dev.pswg.Blasters;
import dev.pswg.BlastersClient;
import dev.pswg.item.BlasterItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The blaster data generator
 */
public class BlasterDataGenerator implements DataGeneratorEntrypoint
{
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator)
	{
		var pack = generator.createPack();

		DataGenResourceHelper.loadResources(PackType.CLIENT_RESOURCES, G3dModelProvider.SOURCES);

		pack.addProvider(LangGenerator::new);
		pack.addProvider(TagGenerator::new);
		pack.addProvider(ModelGenerator::new);
		pack.addProvider((fabricPackOutput, completableFuture) -> new G3dModelProvider(fabricPackOutput, Blasters.MODID));
	}

	/**
	 * The blaster model generator. All models should be added through
	 * this generator.
	 */
	private static class ModelGenerator extends GalaxiesModelProvider
	{
		public ModelGenerator(FabricPackOutput output)
		{
			super(output, Blasters.MODID);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator)
		{
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerator)
		{
			register(itemModelGenerator, Blasters.SMALL_POWER_PACK, ItemModelUtils.plainModel(Blasters.id("item/small_power_pack")));
			register(itemModelGenerator, Blasters.POWER_PACK, ItemModelUtils.plainModel(Blasters.id("item/power_pack")));
			
			// Generic fallback
			register(itemModelGenerator, Blasters.BLASTER_ITEM, ItemModelUtils.plainModel(Blasters.id("item/e11_base")));
		}
	}

	/**
	 * The blaster language file generator. All language entries should be
	 * added through this generator.
	 */
	private static class LangGenerator extends FabricLanguageProvider
	{
		/**
		 * Static translation values for one blaster and its attachment IDs.
		 */
		private record BlasterLang(String name, Map<Identifier, String> attachmentLangs)
		{
		}

		/**
		 * Blaster roster translations.
		 */
		private final Map<Identifier, BlasterLang> _blasterLang = Map.ofEntries(
				Map.entry(Blasters.id("e11"), new BlasterLang("E-11", Map.ofEntries(
						Map.entry(Blasters.id("e11/stock"), "Extensible Stock"),
						Map.entry(Blasters.id("e11/counter"), "E-11 Counter"),
						Map.entry(Blasters.id("e11/flashlight"), "E-11 Flashlight")
				))),
				Map.entry(Blasters.id("dh17"), new BlasterLang("DH-17", Map.of(Blasters.id("dh17/repeater"), "Automatic Firing Configurator"))),
				Map.entry(Blasters.id("a280"), new BlasterLang("A280", Map.ofEntries(
						Map.entry(Blasters.id("a280/burst"), "Three-round Burst Configurator"),
						Map.entry(Blasters.id("a280/standard_barrel"), "A280 Standard Barrel"),
						Map.entry(Blasters.id("a280/dual_barrel"), "A280 Dual Barrel"),
						Map.entry(Blasters.id("a280/standard_scope"), "A280 Standard Scope"),
						Map.entry(Blasters.id("a280/extended_scope"), "A280 Extended Scope"),
						Map.entry(Blasters.id("a280/magazine"), "A280 Cooling Magazine")
				))),
				Map.entry(Blasters.id("se14c"), new BlasterLang("SE-14C", Map.of(Blasters.id("se14c/barrel_extension"), "SE-14C Extended Barrel"))),
				Map.entry(Blasters.id("rk3"), new BlasterLang("RK-3", Map.of())),
				Map.entry(Blasters.id("dl18"), new BlasterLang("DL-18", Map.ofEntries(
						Map.entry(Blasters.id("dl18/repeater"), "Automatic Firing Configurator"),
						Map.entry(Blasters.id("dl18/scope"), "DL-18 Precision Scope")
				))),
				Map.entry(Blasters.id("tusken_cycler"), new BlasterLang("Tusken Cycler", Map.ofEntries(
						Map.entry(Blasters.id("tusken_cycler/scope"), "Tusken Cycler Scope"),
						Map.entry(Blasters.id("tusken_cycler/barrel_extension"), "Tusken Cycler Barrel Extension")
				))),
				Map.entry(Blasters.id("bowcaster"), new BlasterLang("Wookiee Bowcaster", Map.of())),
				Map.entry(Blasters.id("t21"), new BlasterLang("T-21", Map.ofEntries(
						Map.entry(Blasters.id("t21/burst"), "Three-round Burst Configurator"),
						Map.entry(Blasters.id("t21/long_barrel"), "T-21 Long Barrel"),
						Map.entry(Blasters.id("t21/short_barrel"), "T-21 Short Barrel"),
						Map.entry(Blasters.id("t21/scope"), "T-21 Scope")
				))),
				Map.entry(Blasters.id("rt97c"), new BlasterLang("RT-97C", Map.of())),
				Map.entry(Blasters.id("dc15s"), new BlasterLang("DC-15S", Map.of(Blasters.id("dc15s/stock"), "Extensible Stock"))),
				Map.entry(Blasters.id("dc15a"), new BlasterLang("DC-15A", Map.of())),
				Map.entry(Blasters.id("ca87"), new BlasterLang("CA-87 Shock Blaster", Map.of())),
				Map.entry(Blasters.id("jawa_ion"), new BlasterLang("Jawa Ion Blaster", Map.of())),
				Map.entry(Blasters.id("ee3"), new BlasterLang("EE-3R", Map.ofEntries(
						Map.entry(Blasters.id("ee3/vane_barrel"), "EE-3 Vane Barrel"),
						Map.entry(Blasters.id("ee3/smooth_barrel"), "EE-3 Smooth Barrel"),
						Map.entry(Blasters.id("ee3/short_scope"), "EE-3 Short Scope"),
						Map.entry(Blasters.id("ee3/tall_scope"), "EE-3 Tall Scope"),
						Map.entry(Blasters.id("ee3/stock"), "EE-3 Stock")
				))),
				Map.entry(Blasters.id("ee3_esb"), new BlasterLang("EE-3E", Map.of())),
				Map.entry(Blasters.id("e22"), new BlasterLang("E-22", Map.ofEntries(
						Map.entry(Blasters.id("e22/burst"), "Two-round Burst Configurator"),
						Map.entry(Blasters.id("e22/counter"), "E-22 Counter"),
						Map.entry(Blasters.id("e22/flashlight"), "E-22 Flashlight")
				))),
				Map.entry(Blasters.id("e10"), new BlasterLang("E-10", Map.ofEntries(
						Map.entry(Blasters.id("e10/stock"), "Extensible Stock"),
						Map.entry(Blasters.id("e10/range_scope"), "E-10R Scope Package")
				))),
				Map.entry(Blasters.id("e10r"), new BlasterLang("E-10R", Map.of())),
				Map.entry(Blasters.id("blurrg_1120"), new BlasterLang("Blurrg-1120", Map.ofEntries(
						Map.entry(Blasters.id("blurrg_1120/burst4"), "Four-round Burst Configurator"),
						Map.entry(Blasters.id("blurrg_1120/ion"), "Ion Shot Configurator")
				))),
				Map.entry(Blasters.id("relby_v10"), new BlasterLang("Relby V-10", Map.of())),
				Map.entry(Blasters.id("tl50"), new BlasterLang("TL-50", Map.of())),
				Map.entry(Blasters.id("dc17"), new BlasterLang("DC-17", Map.of())),
				Map.entry(Blasters.id("dc17m"), new BlasterLang("DC-17m", Map.of())),
				Map.entry(Blasters.id("a280cfe"), new BlasterLang("A280-CFE", Map.of(Blasters.id("a280cfe/scope"), "Precision Scope"))),
				Map.entry(Blasters.id("a180"), new BlasterLang("A180", Map.of())),
				Map.entry(Blasters.id("a180_sniper"), new BlasterLang("A180 (Sniper)", Map.of(Blasters.id("a180_sniper/scope"), "Precision Scope"))),
				Map.entry(Blasters.id("dlt19x"), new BlasterLang("DLT-19X", Map.of(Blasters.id("dlt19/scope_x"), "DLT-19 X Scope"))),
				Map.entry(Blasters.id("dlt19d"), new BlasterLang("DLT-19D", Map.of(Blasters.id("dlt19/scope_d"), "DLT-19 D Scope"))),
				Map.entry(Blasters.id("dl44"), new BlasterLang("DL-44", Map.of())),
				Map.entry(Blasters.id("e11d"), new BlasterLang("E-11D", Map.of(Blasters.id("e11d/stock"), "Extensible Stock"))),
				Map.entry(Blasters.id("dlt19"), new BlasterLang("DLT-19", Map.ofEntries(
						Map.entry(Blasters.id("dlt19/bipod"), "Folding Bipod"),
						Map.entry(Blasters.id("dlt19/barrel_d"), "DLT-19 D Barrel")
				))),
				Map.entry(Blasters.id("test_blaster"), new BlasterLang(
						"Test Blaster",
						Map.ofEntries(
								Map.entry(Blasters.id("e11/scope_d"), "1.5x Scope"),
								Map.entry(Blasters.id("e11/scope_x"), "2.5x Scope"),
								Map.entry(Blasters.id("e11/bipod"), "Bipod"),
								Map.entry(Blasters.id("e11/barrel_d"), "Extended barrel"),
								Map.entry(Blasters.id("e11/cooling"), "Heat Spreader"),
								Map.entry(Blasters.id("e11/rapidfire"), "Advanced Regeneration")
						)
				))
		);

		protected LangGenerator(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup)
		{
			super(dataOutput, "en_us", registryLookup);
		}

		@Override
		public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder)
		{
			// Tag that contains the blasters
			translationBuilder.add(Blasters.BLASTERS_TAG, "Blasters");

			// Item name
			translationBuilder.add(Blasters.BLASTER_ITEM, "Blaster");
			translationBuilder.add(Blasters.SMALL_POWER_PACK, "Small Power Pack");
			translationBuilder.add(Blasters.POWER_PACK, "Power Pack");

			// Model number of each blaster
			translationBuilder.add(BlasterItem.MISSING_ID, "[unknown model]");

			for (var entry : _blasterLang.entrySet())
			{
				// Add the name of the blaster
				translationBuilder.add(entry.getKey().toLanguageKey(), entry.getValue().name());

				// Add all the attachments
				for (var attachmentEntry : entry.getValue().attachmentLangs().entrySet())
				{
					translationBuilder.add("attachment." + attachmentEntry.getKey().toLanguageKey().replace('/', '.'), attachmentEntry.getValue());
				}
			}

			// Sound subtitles
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.fire"), "Blaster fires");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.reload"), "Blaster reloads");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.dryfire"), "Blaster dry-fires");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.bypass.primary"), "Blaster cools");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.bypass.secondary"), "Blaster overcharges");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.bypass.failed"), "Blaster cooling fails");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.vent"), "Blaster vents");
			LangGenHelper.soundSubtitle(translationBuilder, Blasters.id("blaster.overheat"), "Blaster overheats");

			// Tooltips
			translationBuilder.add(BlastersClient.I18N_VENT_BLASTER, "Vent Heat");
			translationBuilder.add("tooltip.pswg_blasters.unavailable", "Definition unavailable");
			translationBuilder.add("tooltip.pswg_blasters.mode", "Mode: %s");
			translationBuilder.add("tooltip.pswg_blasters.stats", "Damage: %s\nRange: %s blocks\nInterval: %s t\nEffective Range: %s blocks");
			translationBuilder.add("tooltip.pswg_blasters.handling", "Zoom: %sx\nHip spread %s degrees");
			translationBuilder.add("tooltip.pswg_blasters.recoil", "Hip kick (pitch/yaw): %s deg / %s deg\nADS kick: %s deg / %s deg\nRecovery: %s ticks");
			translationBuilder.add("tooltip.pswg_blasters.pack", "Charge: %s / %s units");
			translationBuilder.add("tooltip.pswg_blasters.cooling", "Cooling: %s /t\nVent %s /t");
			translationBuilder.add("tooltip.pswg_blasters.ammo", "Loaded: %s / %s");
			translationBuilder.add("tooltip.pswg_blasters.conversion", "Conversion: %s");
			translationBuilder.add("tooltip.pswg_blasters.deployed", "Deployed");
			translationBuilder.add("tooltip.pswg_blasters.folded", "Folded");
			translationBuilder.add("mode.pswg_blasters.semi", "Semi-automatic");
			translationBuilder.add("mode.pswg_blasters.auto", "Automatic");
			translationBuilder.add("mode.pswg_blasters.burst", "Burst");
			translationBuilder.add("mode.pswg_blasters.charge", "Charged");
			translationBuilder.add("mode.pswg_blasters.stun", "Stun");
			translationBuilder.add("mode.pswg_blasters.ion", "Ion");
			translationBuilder.add("mode.pswg_blasters.training", "Low-energy Training");
			translationBuilder.add("mode.pswg_blasters.burst4", "Four-round Burst");
			translationBuilder.add("text.pswg_blasters.cycle_mode", "Change Firing Mode");
			translationBuilder.add("text.pswg_blasters.reload", "Reload Blaster");
			translationBuilder.add("text.pswg_blasters.reloading", "Reloading...");
			translationBuilder.add("text.pswg_blasters.cannot_reload", "Ammo full or no usable ammunition");
			translationBuilder.add("text.pswg_blasters.reload_cost", "Reloading %s rounds (%s u)");
			translationBuilder.add("text.pswg_blasters.reload_complete", "Loaded %s rounds (%s u)");
			translationBuilder.add("text.pswg_blasters.reload_cancelled", "Reload cancelled");
			translationBuilder.add("text.pswg_blasters.stock_folded", "Stock folded");
			translationBuilder.add("text.pswg_blasters.stock_extended", "Stock extended");
			translationBuilder.add("text.pswg_blasters.bipod_deployed", "Bipod deployed");
			translationBuilder.add("text.pswg_blasters.bipod_stowed", "Bipod stowed");
			translationBuilder.add("text.pswg_blasters.needs_ground", "Bipod deployment requires stable ground");
			translationBuilder.add("text.pswg_blasters.base_form", "Base configuration restored");
			translationBuilder.add("text.pswg_blasters.no_conversion", "No eligible field conversion");
			translationBuilder.add("tooltip.pswg_blasters.heat_cost", "Heat: %s per shot / %s capacity");
			translationBuilder.add("tooltip.pswg_blasters.magazine_cost", "Source cost: %s units/round\nFull magazine: %s units\nCurrent top-up: up to %s units");
			translationBuilder.add("key.pswg_blasters.cycle_mode", "Change Blaster Firing Mode");
			translationBuilder.add("key.pswg_blasters.reload", "Reload Blaster");
			translationBuilder.add("key.pswg_blasters.fold", "Fold / Extend Blaster Stock");
			translationBuilder.add("key.pswg_blasters.deploy", "Deploy / Stow Blaster Bipod");
			translationBuilder.add("key.pswg_blasters.convert", "Toggle Blaster Field Conversion");
		}
	}

	/**
	 * The blaster item tag generator. All item tags should be added
	 * through this generator.
	 */
	private static class TagGenerator extends FabricTagsProvider.ItemTagsProvider
	{
		public TagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
		{
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider wrapperLookup)
		{
			getOrCreateRawBuilder(Blasters.BLASTERS_TAG)
					.addElement(Blasters.BLASTER_ITEM_ID);
		}
	}
}
