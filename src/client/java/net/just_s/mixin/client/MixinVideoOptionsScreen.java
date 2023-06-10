package net.just_s.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.just_s.WorldDaysModClient;
import net.just_s.util.ClothConfigIntegration;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.just_s.WorldDaysModClient.CONFIG;

@Mixin(VideoOptionsScreen.class)
public class MixinVideoOptionsScreen extends Screen {
	@Shadow private ButtonListWidget list;

	protected MixinVideoOptionsScreen(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonListWidget;addAll([Lnet/minecraft/client/option/Option;)V", shift = At.Shift.AFTER))
	private void run(CallbackInfo info) {
		if (isLoaded("modmenu") && isLoaded("cloth-config")) return;

		if (isLoaded("cloth-config")) {
			ButtonWidget btn = new ButtonWidget(
					5, 5,
					150, 20,
					new TranslatableText("config.world-days.title"),
					(button) -> WorldDaysModClient.MC.setScreen(
							ClothConfigIntegration.generateScreen((VideoOptionsScreen)(Object)this)
					)
			);
			this.addDrawableChild(btn);
		} else {
			CyclingOption<Boolean> simpleOption = CyclingOption.create(
					"config.world-days.enable",
					(gameOptions) -> CONFIG.enable,
					(gameOptions, option, value) -> CONFIG.enable = value
			);
			list.addSingleOptionEntry(simpleOption);
		}
	}

	private boolean isLoaded(String id) {
		return FabricLoader.getInstance().isModLoaded(id);
	}
}