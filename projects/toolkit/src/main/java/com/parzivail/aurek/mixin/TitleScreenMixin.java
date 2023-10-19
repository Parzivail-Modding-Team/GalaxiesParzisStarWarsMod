package com.parzivail.aurek.mixin;

import com.parzivail.aurek.ToolkitClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
@Environment(EnvType.CLIENT)
public class TitleScreenMixin extends Screen
{
	protected TitleScreenMixin()
	{
		super(Text.empty());
	}

	@Inject(method = "init()V", at = @At("TAIL"))
	void init(CallbackInfo ci)
	{
		int l = this.height / 4 + 48;

		this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 124, l + 24 * 2, 20, 20, ToolkitClient.TEX_TOOLKIT_BUTTON, (button) -> {
			assert this.client != null;
			this.client.setScreen(ToolkitClient.createHomeScreen(this));
		}, Text.translatable(ToolkitClient.I18N_TOOLKIT)));
	}
}
