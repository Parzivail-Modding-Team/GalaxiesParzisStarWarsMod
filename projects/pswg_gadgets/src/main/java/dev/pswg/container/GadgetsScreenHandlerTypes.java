package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.feature.scrapping.table.ScrappingTableScreenHandler;
import dev.pswg.networking.MixerSyncS2CPayload;
import dev.pswg.registry.Registrar;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;

public class GadgetsScreenHandlerTypes
{

	public static final ExtendedMenuType<MixerScreenHandler, MixerSyncS2CPayload> MIXER = new ExtendedMenuType<>((syncId, inventory, data) -> new MixerScreenHandler(syncId, inventory, data.drinkEffects(), data.drinkColors(), data.drinkFoods()), MixerSyncS2CPayload.CODEC);
	public static final MenuType<ScrappingTableScreenHandler> SCRAPPING_TABLE = Registrar.screenHandlerType(Gadgets.id("scrapping_table"), ScrappingTableScreenHandler::new);

	public static void register()
	{
		Registry.register(BuiltInRegistries.MENU, Gadgets.id("mixer"), MIXER);
	}
}
