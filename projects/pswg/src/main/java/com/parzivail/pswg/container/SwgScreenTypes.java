package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.features.blasters.workbench.BlasterWorkbenchScreenHandler;
import com.parzivail.pswg.features.lightsabers.forge.LightsaberForgeScreenHandler;
import com.parzivail.pswg.screen.CrateGenericSmallScreenHandler;
import com.parzivail.pswg.screen.CrateOctagonScreenHandler;
import com.parzivail.pswg.screen.MoistureVaporatorScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SwgScreenTypes
{
	public static class Crate
	{
		public static final ScreenHandlerType<CrateOctagonScreenHandler> Kyber = Registry.register(Registries.SCREEN_HANDLER, Resources.id("kyber_crate"), new ScreenHandlerType<>(CrateOctagonScreenHandler::new, FeatureSet.empty()));
		public static final ScreenHandlerType<CrateGenericSmallScreenHandler> Toolbox = Registry.register(Registries.SCREEN_HANDLER, Resources.id("toolbox"), makeScreenHandler(() -> Crate.Toolbox));
		public static final ScreenHandlerType<CrateGenericSmallScreenHandler> Corrugated = Registry.register(Registries.SCREEN_HANDLER, Resources.id("corrugated_crate"), makeScreenHandler(() -> Crate.Corrugated));
		public static final ScreenHandlerType<CrateGenericSmallScreenHandler> Segmented = Registry.register(Registries.SCREEN_HANDLER, Resources.id("segmented_crate"), makeScreenHandler(() -> Crate.Segmented));

		@NotNull
		private static ScreenHandlerType<CrateGenericSmallScreenHandler> makeScreenHandler(Supplier<ScreenHandlerType<CrateGenericSmallScreenHandler>> handlerType)
		{
			return new ScreenHandlerType<>((sId, inv) -> new CrateGenericSmallScreenHandler(handlerType.get(), sId, inv), FeatureSet.empty());
		}

		static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static class MoistureVaporator
	{
		public static final ScreenHandlerType<MoistureVaporatorScreenHandler> GX8 = Registry.register(Registries.SCREEN_HANDLER, Resources.id("gx8_moisture_vaporator"), new ScreenHandlerType<>(MoistureVaporatorScreenHandler::new, FeatureSet.empty()));

		static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static class Workbench
	{
		public static final ScreenHandlerType<BlasterWorkbenchScreenHandler> Blaster = Registry.register(Registries.SCREEN_HANDLER, Resources.id("blaster_workbench"), new ScreenHandlerType<>(BlasterWorkbenchScreenHandler::new, FeatureSet.empty()));
		public static final ScreenHandlerType<LightsaberForgeScreenHandler> Lightsaber = Registry.register(Registries.SCREEN_HANDLER, Resources.id("lightsaber_forge"), new ScreenHandlerType<>(LightsaberForgeScreenHandler::new, FeatureSet.empty()));

		static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static void register()
	{
		Crate.register();
		MoistureVaporator.register();
		Workbench.register();
	}
}
