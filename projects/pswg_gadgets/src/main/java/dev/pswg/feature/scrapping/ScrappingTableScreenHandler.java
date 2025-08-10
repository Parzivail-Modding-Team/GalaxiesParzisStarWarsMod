package dev.pswg.feature.scrapping;

import dev.pswg.container.GadgetsScreenHandlerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ScrappingTableScreenHandler extends AbstractRecipeScreenHandler
{
	private final Inventory inventory;
	private final PlayerInventory playerInventory;
	private final PropertyDelegate propertyDelegate;
	protected final World world;

	public ScrappingTableScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(8), new ArrayPropertyDelegate(3));
	}

	public ScrappingTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(GadgetsScreenHandlerTypes.SCRAPPING_TABLE, syncId);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.propertyDelegate = propertyDelegate;
		///  Cutter
		this.addSlot(new Slot(inventory, 0, 6, 46));
		///  Spanner
		this.addSlot(new Slot(inventory, 1, 6, 68));
		///  Calibrator
		this.addSlot(new Slot(inventory, 2, 6, 90));
		///  Input
		this.addSlot(new Slot(inventory, 3, 81, 28));
		/// Output
		this.addSlot(new Slot(inventory, 4, 129, 54));
		this.addSlot(new Slot(inventory, 5, 129, 80));
		this.addSlot(new Slot(inventory, 6, 150, 54));
		this.addSlot(new Slot(inventory, 7, 150, 80));

		this.addPlayerSlots(playerInventory, 8, 132);
	}

	public int getCutterProgress()
	{
		return propertyDelegate.get(0);
	}

	public int getSpannerProgress()
	{
		return propertyDelegate.get(1);
	}

	public int getCalibratorProgress()
	{
		return propertyDelegate.get(2);
	}

	@Override
	public PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory)
	{
		return PostFillAction.NOTHING;
	}

	@Override
	public void populateRecipeFinder(RecipeFinder finder)
	{

	}

	@Override
	public RecipeBookType getCategory()
	{
		return RecipeBookType.CRAFTING;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}
}
