package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.client.screen.widget.LocalTextureButtonWidget;
import com.parzivail.pswg.client.screen.widget.SimpleTooltipSupplier;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import com.parzivail.util.math.Matrix4fUtil;
import com.parzivail.util.math.MatrixStackUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec2f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BlasterWorkbenchScreen extends HandledScreen<BlasterWorkbenchScreenHandler> implements ScreenHandlerListener
{
	private static final Identifier TEXTURE = Resources.id("textures/gui/container/blaster_workbench.png");

	private ItemStack blaster = ItemStack.EMPTY;

	private Vec2f rotation = new Vec2f(0, 0);
	private Vec2f oldMousePos = null;
	private boolean isDragging = false;

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

		this.addDrawableChild(new LocalTextureButtonWidget(x + 51, y + 124, 22, 12, 178, 3, 178, 17, 256, 256, this::onBuildClicked, new SimpleTooltipSupplier(this, this::getBuildTooltip), LiteralText.EMPTY));

		this.addDrawableChild(new LocalTextureButtonWidget(x + 76, y + 124, 22, 12, 203, 3, 203, 17, this::onCancelClicked));

		this.handler.addListener(this);
	}

	private List<? extends OrderedText> getBuildTooltip()
	{
		return null;
	}

	private void onBuildClicked(ButtonWidget sender)
	{

	}

	private void onCancelClicked(ButtonWidget sender)
	{

	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		if (oldMousePos != null && isDragging)
			rotation = rotation.add(new Vec2f((float)mouseX - oldMousePos.x, (float)mouseY - oldMousePos.y));

		oldMousePos = new Vec2f((float)mouseX, (float)mouseY);

		super.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (!isDragging && mouseX >= x + 52 && mouseX <= x + 160 && mouseY >= y + 15 && mouseY <= y + 65)
		{
			isDragging = true;
			return true;
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (isDragging)
		{
			isDragging = false;
			return true;
		}

		return super.mouseReleased(mouseX, mouseY, button);
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

			// TODO: scale based on bounds
			MatrixStackUtil.scalePos(matrices, -60, 60, 1);

			matrices.multiply(new Quaternion(180 - rotation.y, 0, 0, true));
			matrices.multiply(new Quaternion(0, 90 + rotation.x, 0, true));

			DiffuseLighting.enableForLevel(Matrix4fUtil.IDENTITY);

			var immediate = minecraft.getBufferBuilders().getEntityVertexConsumers();
			BlasterItemRenderer.INSTANCE.render(blaster, ModelTransformation.Mode.NONE, false, matrices, immediate, 0xf000f0, OverlayTexture.DEFAULT_UV, null);
			immediate.draw();

			DiffuseLighting.enableGuiDepthLighting();

			matrices.pop();

			RenderSystem.setShaderTexture(0, TEXTURE);

			// damage
			drawStackedStatBar(matrices, 0.25f, 0.3f, 20, 142);

			// accuracy
			drawStackedStatBar(matrices, 0.5f, 0.25f, 103, 142);

			// cooling
			drawStackedStatBar(matrices, 0.5f, 0.75f, 20, 155);

			// speed
			drawStackedStatBar(matrices, 1, 0.75f, 103, 155);
		}
	}

	private void drawStackedStatBar(MatrixStack matrices, float newValue, float oldValue, int targetX, int targetY)
	{
		if (newValue > oldValue)
		{
			drawTexture(matrices, x + targetX, y + targetY, 179, 142, Math.round(67 * newValue), 4, 256, 256);
			drawTexture(matrices, x + targetX, y + targetY, 179, 147, Math.round(67 * oldValue), 4, 256, 256);
		}
		else
		{
			drawTexture(matrices, x + targetX, y + targetY, 179, 147, Math.round(67 * oldValue), 4, 256, 256);
			drawTexture(matrices, x + targetX, y + targetY, 179, 142, Math.round(67 * newValue), 4, 256, 256);
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
