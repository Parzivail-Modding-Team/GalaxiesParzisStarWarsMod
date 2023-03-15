package com.parzivail.pswg.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.render.item.BlasterItemRenderer;
import com.parzivail.pswg.container.SwgPackets;
import com.parzivail.pswg.item.blaster.BlasterItem;
import com.parzivail.pswg.item.blaster.data.BlasterAttachmentDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterDescriptor;
import com.parzivail.pswg.item.blaster.data.BlasterTag;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.screen.AreaButtonWidget;
import com.parzivail.util.client.screen.LocalTextureButtonWidget;
import com.parzivail.util.math.MathUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class BlasterWorkbenchScreen extends HandledScreen<BlasterWorkbenchScreenHandler> implements ScreenHandlerListener
{
	@TarkinLang
	public static final String I18N_INCOMPAT_ATTACHMENT = Resources.screen("blaster.incompatible_attachment");
	@TarkinLang
	public static final String I18N_STAT_MULT_ENTRY = Resources.screen("blaster.stat_multiplier_entry");
	@TarkinLang
	public static final String I18N_DAMAGE_RATIO = Resources.screen("blaster.damage_ratio");
	@TarkinLang
	public static final String I18N_COOLING_RATIO = Resources.screen("blaster.cooling_ratio");
	@TarkinLang
	public static final String I18N_RATE_RATIO = Resources.screen("blaster.rate_ratio");
	@TarkinLang
	public static final String I18N_ACCURACY_RATIO = Resources.screen("blaster.accuracy_ratio");

	private static final Identifier TEXTURE = Resources.id("textures/gui/container/blaster_workbench.png");
	private static final int NUM_VISIBLE_ATTACHMENT_ROWS = 3;
	private static final float SCROLL_THUMB_HEIGHT = 15f;
	private static final float SCROLL_THUMB_HALF_HEIGHT = SCROLL_THUMB_HEIGHT / 2;

	private static final int ROW_STATE_EMPTY = -1;
	private static final int ROW_STATE_DISABLED = 0;
	private static final int ROW_STATE_NORMAL = 1;
	private static final int ROW_STATE_HOVER = 2;

	private static final int BLASTER_VIEWPORT_WIDTH = 108;
	private static final int BLASTER_VIEWPORT_HEIGHT = 50;

	private int originalBitmask;
	private ItemStack blaster = ItemStack.EMPTY;
	private BlasterDescriptor blasterDescriptor = null;
	private Identifier blasterModel = null;

	private Vec2f blasterViewportRotation = new Vec2f(0, 0);
	private boolean isDraggingScrollThumb = false;
	private boolean isDraggingBlasterViewport = false;

	private float scrollPosition = 0;

	private List<BlasterAttachmentDescriptor> attachmentList = new ArrayList<>();
	private LocalTextureButtonWidget buildButton;
	private LocalTextureButtonWidget cancelButton;

	public BlasterWorkbenchScreen(BlasterWorkbenchScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
		backgroundWidth = 176;
		backgroundHeight = 256;

		this.titleY -= 1;
	}

	@Override
	protected void init()
	{
		super.init();

		this.playerInventoryTitleX = 7;
		this.playerInventoryTitleY = this.backgroundHeight - 92;
		this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;

		this.addDrawableChild(buildButton = new LocalTextureButtonWidget(x + 51, y + 124, 22, 12, 178, 3, 178, 17, 256, 256, this::onBuildClicked, Text.empty()));
		this.addDrawableChild(cancelButton = new LocalTextureButtonWidget(x + 76, y + 124, 22, 12, 203, 3, 203, 17, this::onCancelClicked));

		this.addDrawableChild(new AreaButtonWidget(x + 52, y + 70, 93, 17, button -> attachmentList.size() > 0, button -> onRowClicked(0)));
		this.addDrawableChild(new AreaButtonWidget(x + 52, y + 87, 93, 17, button -> attachmentList.size() > 1, button -> onRowClicked(1)));
		this.addDrawableChild(new AreaButtonWidget(x + 52, y + 104, 93, 17, button -> attachmentList.size() > 2, button -> onRowClicked(2)));

		this.handler.addListener(this);

		onBlasterChanged();
	}

	private List<? extends OrderedText> getBuildTooltip()
	{
		return null;
	}

	private void onBuildClicked(ButtonWidget sender)
	{
		// TODO: Subtract material cost for new attachments, and give back for removed ones
		// TODO: move this to backend, don't allow client to set arbitrary tag data

		var passedData = new PacketByteBuf(Unpooled.buffer());
		passedData.writeNbt(getBlasterTag().toTag());
		ClientPlayNetworking.send(SwgPackets.C2S.BlasterWorkbenchApply, passedData);
	}

	private void onCancelClicked(ButtonWidget sender)
	{
		var bt = getBlasterTag();
		bt.attachmentBitmask = originalBitmask;
		bt.serializeAsSubtag(blaster);
	}

	private void onRowClicked(int row)
	{
		var topRow = getAttachmentListTopRowIdx();
		var rowIdx = topRow + row;

		if (rowIdx >= attachmentList.size())
			return;

		var attachment = attachmentList.get(rowIdx);

		var bt = getBlasterTag();

		var incompat = getIncompatibleAttachments(bt, attachment);

		// TODO: move this to backend (use property delegates for bitmasks?)

		// remove incompatible attachments
		for (var a : incompat)
			bt.attachmentBitmask ^= a.bit;

		// apply new attachment
		bt.attachmentBitmask ^= attachment.bit;

		provideDefaultAttachments(bt);

		bt.serializeAsSubtag(blaster);
	}

	private void provideDefaultAttachments(BlasterTag bt)
	{
		for (var attachment : attachmentList)
		{
			// If no attachment is attached in the bitmask category that this
			// attachment belongs to, revert to the default attachment (which
			// may also be zero, but at least we checked). This only works
			// if the mutex for each attachment is set to the bitfield
			// that spans all attachments. Example:
			//
			// Attachment 1: bit 0b01
			// Attachment 2: bit 0b10
			// Mutex for both:   0b11
			//
			// This also means that the "minimum" attachments in a
			// descriptor should only be set to the attachments
			// that form the default in a required set.

			if ((attachment.mutex & bt.attachmentBitmask) == 0)
				bt.attachmentBitmask |= (blasterDescriptor.attachmentMinimum & attachment.mutex);
		}
	}

	private List<BlasterAttachmentDescriptor> getIncompatibleAttachments(BlasterTag bt, BlasterAttachmentDescriptor query)
	{
		var list = new ArrayList<BlasterAttachmentDescriptor>();
		for (var attachment : attachmentList)
		{
			if (attachment == query)
				continue;

			// check if this attachment is both attached, and conflicts with the query attachment
			if ((bt.attachmentBitmask & attachment.bit) != 0 && (attachment.mutex & query.mutex) != 0)
				list.add(attachment);
		}
		return list;
	}

	private List<Text> getAttachmentError(BlasterTag bt, BlasterAttachmentDescriptor attachment)
	{
		var incompat = getIncompatibleAttachments(bt, attachment);
		if (incompat.isEmpty())
			return null;

		var text = new ArrayList<Text>();
		text.add(Text.translatable(I18N_INCOMPAT_ATTACHMENT));

		for (var a : incompat)
			text.add(MutableText.of(BlasterItem.getAttachmentTranslation(blasterModel, a)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xA0A0A0))));

		return text;
	}

	private boolean attachmentListContains(double mouseX, double mouseY)
	{
		return MathUtil.rectContains(x + 51, y + 69, 110, 53, mouseX, mouseY) && canScroll();
	}

	private boolean scrollbarContains(double mouseX, double mouseY)
	{
		return MathUtil.rectContains(x + 148, y + 70, 12, 51, mouseX, mouseY) && canScroll();
	}

	private boolean blasterViewportContains(double mouseX, double mouseY)
	{
		return MathUtil.rectContains(x + 52, y + 15, BLASTER_VIEWPORT_WIDTH, BLASTER_VIEWPORT_HEIGHT, mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (attachmentListContains(mouseX, mouseY))
		{
			var i = attachmentList.size() - NUM_VISIBLE_ATTACHMENT_ROWS;
			this.scrollPosition = MathHelper.clamp((float)(this.scrollPosition - amount / i), 0, 1);
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if (this.isDraggingScrollThumb)
		{
			var trackTop = y + 70;
			var trackBottom = trackTop + 51;
			this.scrollPosition = MathHelper.clamp((float)(mouseY - trackTop - SCROLL_THUMB_HALF_HEIGHT) / (trackBottom - trackTop - SCROLL_THUMB_HEIGHT), 0, 1);
			return true;
		}
		else
		{
			if (isDraggingBlasterViewport)
				blasterViewportRotation = new Vec2f(blasterViewportRotation.x + (float)deltaX, MathHelper.clamp(blasterViewportRotation.y + (float)deltaY, -20, 20));

			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			if (!isDraggingBlasterViewport && blasterViewportContains(mouseX, mouseY))
			{

				isDraggingBlasterViewport = true;
				return true;
			}
			else if (this.scrollbarContains(mouseX, mouseY))
			{
				this.isDraggingScrollThumb = true;
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			this.isDraggingScrollThumb = false;
			this.isDraggingBlasterViewport = false;
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	private BlasterTag getBlasterTag()
	{
		return new BlasterTag(blaster.getOrCreateNbt());
	}

	@Override
	public void removed()
	{
		super.removed();
		this.handler.removeListener(this);
	}

	private boolean canScroll()
	{
		return attachmentList.size() > 3;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		var minecraft = MinecraftClient.getInstance();
		Mutable<List<Text>> tooltip = new MutableObject<>();

		if (blasterModel != null && blaster.getItem() instanceof BlasterItem)
		{
			var bt = new BlasterTag(blaster.getOrCreateNbt());

			matrices.push();

			matrices.translate(x + 105, y + 48, 10);

			DiffuseLighting.enableForLevel(MathUtil.MAT4_IDENTITY);

			var immediate = minecraft.getBufferBuilders().getEntityVertexConsumers();
			var modelEntry = BlasterItemRenderer.INSTANCE.getModel(blasterModel);
			var model = modelEntry.model();

			var ratio = (float)Math.max(model.bounds().getZLength() / BLASTER_VIEWPORT_WIDTH, model.bounds().getYLength() / (BLASTER_VIEWPORT_HEIGHT * 0.6));

			MathUtil.scalePos(matrices, -1 / ratio, 1 / ratio, 1);

			matrices.multiply(new Quaternionf().rotationX(MathUtil.toRadians(180 - blasterViewportRotation.y)));
			matrices.multiply(new Quaternionf().rotationY(MathUtil.toRadians(90 + blasterViewportRotation.x)));

			matrices.translate(0, 0, -model.bounds().maxZ + model.bounds().getZLength() / 2);

			MathUtil.scalePos(matrices, 5, 5, 5);

			BlasterItemRenderer.INSTANCE.render(blaster, ModelTransformation.Mode.NONE, false, matrices, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, null);
			immediate.draw();

			DiffuseLighting.enableGuiDepthLighting();

			matrices.pop();

			RenderSystem.setShaderTexture(0, TEXTURE);

			// damage
			var oldDamage = BlasterItem.getDamageMultiplier(blasterDescriptor, originalBitmask);
			var newDamage = BlasterItem.getDamageMultiplier(blasterDescriptor, bt.attachmentBitmask);
			drawStackedStatBar(matrices, newDamage, oldDamage, 20, 142);

			if (MathUtil.rectContains(x + 20, y + 142, 67, 4, mouseX, mouseY))
				tooltip.setValue(List.of(Text.translatable(
						I18N_DAMAGE_RATIO,
						Text.translatable(I18N_STAT_MULT_ENTRY, oldDamage).formatted(Formatting.BLUE),
						Text.translatable(I18N_STAT_MULT_ENTRY, newDamage).formatted(Formatting.GOLD)
				)));

			// accuracy
			var oldAccuracy = BlasterItem.getAccuracyStatistic(blasterDescriptor, originalBitmask);
			var newAccuracy = BlasterItem.getAccuracyStatistic(blasterDescriptor, bt.attachmentBitmask);
			drawStackedStatBar(matrices, newAccuracy, oldAccuracy, 103, 142);

			if (MathUtil.rectContains(x + 103, y + 142, 67, 4, mouseX, mouseY))
				tooltip.setValue(List.of(Text.translatable(
						I18N_ACCURACY_RATIO,
						Text.translatable(I18N_STAT_MULT_ENTRY, oldAccuracy).formatted(Formatting.BLUE),
						Text.translatable(I18N_STAT_MULT_ENTRY, newAccuracy).formatted(Formatting.GOLD)
				)));

			// cooling
			var oldCooling = BlasterItem.getCoolingMultiplier(blasterDescriptor, originalBitmask);
			var newCooling = BlasterItem.getCoolingMultiplier(blasterDescriptor, bt.attachmentBitmask);
			drawStackedStatBar(matrices, newCooling, oldCooling, 20, 155);

			if (MathUtil.rectContains(x + 20, y + 155, 67, 4, mouseX, mouseY))
				tooltip.setValue(List.of(Text.translatable(
						I18N_COOLING_RATIO,
						Text.translatable(I18N_STAT_MULT_ENTRY, newCooling).formatted(Formatting.BLUE),
						Text.translatable(I18N_STAT_MULT_ENTRY, newCooling).formatted(Formatting.GOLD)
				)));

			// speed
			var oldRate = 1 / BlasterItem.getShotTimerMultiplier(blasterDescriptor, originalBitmask);
			var newRate = 1 / BlasterItem.getShotTimerMultiplier(blasterDescriptor, bt.attachmentBitmask);
			drawStackedStatBar(matrices, newRate, oldRate, 103, 155);

			if (MathUtil.rectContains(x + 103, y + 155, 67, 4, mouseX, mouseY))
				tooltip.setValue(List.of(Text.translatable(
						I18N_RATE_RATIO,
						Text.translatable(I18N_STAT_MULT_ENTRY, oldRate).formatted(Formatting.BLUE),
						Text.translatable(I18N_STAT_MULT_ENTRY, newRate).formatted(Formatting.GOLD)
				)));

			drawAttachmentList(matrices, blasterModel, bt, attachmentList, this::getAttachmentError, tooltip::setValue, mouseX, mouseY);
		}

		if (tooltip.getValue() != null)
			this.renderTooltip(matrices, tooltip.getValue(), mouseX, mouseY);

		this.drawMouseoverTooltip(matrices, mouseX, mouseY);
	}

	private void drawAttachmentList(MatrixStack matrices, Identifier blasterModel, BlasterTag bt, List<BlasterAttachmentDescriptor> attachments, BiFunction<BlasterTag, BlasterAttachmentDescriptor, List<Text>> errorProvider, Consumer<List<Text>> tooltipPropogator, double mouseX, double mouseY)
	{
		drawScrollbar(matrices, canScroll(), scrollPosition);

		var topRow = getAttachmentListTopRowIdx();

		for (var i = 0; i < NUM_VISIBLE_ATTACHMENT_ROWS; i++)
		{
			var rowIdx = topRow + i;

			if (rowIdx >= attachmentList.size())
				drawAttachmentRow(matrices, i, 0, 0, ROW_STATE_EMPTY, Text.empty());
			else
			{
				var attachment = attachments.get(rowIdx);
				var rowState = ROW_STATE_NORMAL;

				var hovering = MathUtil.rectContains(x + 52, y + 70 + i * 17, 94, 16, mouseX, mouseY);

				// TODO: check if the attachment is attached, and provide a way to disable it
				if ((bt.attachmentBitmask & attachment.bit) != 0)
					rowState = ROW_STATE_HOVER;
				else
				{
					var validityError = errorProvider.apply(bt, attachment);
					if (validityError != null)
					{
						var oldTexture = RenderSystem.getShaderTexture(0);
						if (hovering)
							tooltipPropogator.accept(validityError);
						RenderSystem.setShaderTexture(0, oldTexture);
					}
				}

				var iconU = attachment.category.getId() / 3;
				var iconV = attachment.category.getId() % 3;

				drawAttachmentRow(matrices, i, iconU, iconV, rowState, MutableText.of(BlasterItem.getAttachmentTranslation(blasterModel, attachment)));
			}
		}
	}

	private int getAttachmentListTopRowIdx()
	{
		return Math.max(Math.round(scrollPosition * (float)(attachmentList.size() - NUM_VISIBLE_ATTACHMENT_ROWS)), 0);
	}

	private void drawScrollbar(MatrixStack matrices, boolean enabled, float percent)
	{
		if (!enabled)
			percent = 0;

		drawTexture(matrices, x + 148, y + 70 + Math.round(36 * percent), enabled ? 228 : 243, 3, 12, 15, 256, 256);
	}

	private void drawAttachmentRow(MatrixStack matrices, int row, int iconUi, int iconVi, int state, Text attachmentText)
	{
		if (state == -1)
			return;

		RenderSystem.setShaderTexture(0, TEXTURE);
		drawTexture(matrices, x + 68, y + 70 + row * 17, 178, 31 + state * 17, 77, 17, 256, 256);
		drawTexture(matrices, x + 51, y + 70 + row * 17, 178, 85 + state * 17, 17, 17, 256, 256);

		if (state == 0)
			RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1);
		drawTexture(matrices, x + 52, y + 71 + row * 17, 199 + iconUi * 17, 86 + iconVi * 17, 15, 15, 256, 256);
		RenderSystem.setShaderColor(1, 1, 1, 1);

		this.textRenderer.drawWithShadow(matrices, attachmentText, x + 71, y + 74 + row * 17, state > 0 ? 0xFFFFFF : 0xA0A0A0);
	}

	private void drawStackedStatBar(MatrixStack matrices, float newValue, float oldValue, int targetX, int targetY)
	{
		var divisor = Math.max(2, Math.max(newValue, oldValue));

		newValue /= divisor;
		oldValue /= divisor;

		if (newValue < oldValue)
		{
			drawTexture(matrices, x + targetX, y + targetY, 179, 142, Math.round(67 * oldValue), 4, 256, 256);
			drawTexture(matrices, x + targetX, y + targetY, 179, 147, Math.round(67 * newValue), 4, 256, 256);
		}
		else
		{
			drawTexture(matrices, x + targetX, y + targetY, 179, 147, Math.round(67 * newValue), 4, 256, 256);
			drawTexture(matrices, x + targetX, y + targetY, 179, 142, Math.round(67 * oldValue), 4, 256, 256);
		}
	}

	@Override
	public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack)
	{
		switch (slotId)
		{
			case 0 ->
			{
				blaster = stack.copy();
				onBlasterChanged();
			}
		}
	}

	private void onBlasterChanged()
	{
		scrollPosition = 0;

		if (blaster.getItem() instanceof BlasterItem)
		{
			var bd = BlasterItem.getBlasterDescriptor(blaster);
			attachmentList = bd.attachmentMap.values().stream().toList();
			blasterModel = BlasterItem.getBlasterModel(blaster);
			blasterDescriptor = bd;

			var bt = new BlasterTag(blaster.getOrCreateNbt());
			originalBitmask = bt.attachmentBitmask;

			buildButton.visible = true;
			cancelButton.visible = true;
		}
		else
		{
			attachmentList = new ArrayList<>();
			blasterModel = null;
			blasterDescriptor = null;
			originalBitmask = 0;

			buildButton.visible = false;
			cancelButton.visible = false;
		}
	}

	@Override
	public void onPropertyUpdate(ScreenHandler handler, int property, int value)
	{
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		var i = (this.width - this.backgroundWidth) / 2;
		var j = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
	}
}
