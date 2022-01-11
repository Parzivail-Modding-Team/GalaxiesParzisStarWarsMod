package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class BlasterWorkbenchScreen extends HandledScreen<BlasterWorkbenchScreenHandler> implements ScreenHandlerListener
{
	private static final Identifier TEXTURE = Resources.id("textures/gui/container/blaster_workbench.png");

	private ItemStack blaster = ItemStack.EMPTY;

	public BlasterWorkbenchScreen(BlasterWorkbenchScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 176;
		backgroundHeight = 256;

		this.titleY -= 1;
	}

	protected void init()
	{
		super.init();

		this.playerInventoryTitleX = 7;
		this.playerInventoryTitleY = this.backgroundHeight - 92;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

		//		var passedData = new PacketByteBuf(Unpooled.buffer());
		//		passedData.writeNbt(getBlasterTag().toTag());
		//		ClientPlayNetworking.send(SwgPackets.C2S.PacketBlasterWorkbenchApply, passedData);

		this.handler.addListener(this);
	}

	private BlasterTag getBlasterTag()
	{
		return new BlasterTag(blaster.getOrCreateNbt());
	}

	public void removed()
	{
		super.removed();
		this.handler.removeListener(this);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrices, mouseX, mouseY);

		var minecraft = MinecraftClient.getInstance();

		if (blaster.getItem() instanceof BlasterItem)
		{
			var bd = BlasterItem.getBlasterDescriptor(MinecraftClient.getInstance().world, blaster);
			var bt = new BlasterTag(blaster.getOrCreateNbt());

			matrices.push();

			matrices.translate(x + 90, y + 45, 10);
			matrices.scale(-70, 70, 70);

			matrices.multiply(new Quaternion(0, -90, 0, true));
			matrices.multiply(new Quaternion(180, 0, 0, true));

			var immediate = minecraft.getBufferBuilders().getEntityVertexConsumers();
			BlasterItemRenderer.INSTANCE.render(blaster, ModelTransformation.Mode.NONE, false, matrices, immediate, 0xFFFFFF, OverlayTexture.DEFAULT_UV, null);
			immediate.draw();

			matrices.pop();
		}
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
		switch (slotId)
		{
			case 0 -> {
				blaster = stack.copy();
				onBlasterChanged();
			}
		}
	}

	private void onBlasterChanged()
	{
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{
	}

	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
