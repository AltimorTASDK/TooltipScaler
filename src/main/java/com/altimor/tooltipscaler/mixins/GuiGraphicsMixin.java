package com.altimor.tooltipscaler.mixins;

import java.util.List;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.altimor.tooltipscaler.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.util.Mth;

// We want to apply our popPose mixin after anything that renders from the tail of renderTooltipInternal
@Mixin(value = GuiGraphics.class, priority = Integer.MAX_VALUE)
public abstract class GuiGraphicsMixin {
    @Shadow
    private Minecraft minecraft;

    private float scale;

    @ModifyArg(
        method = "renderTooltipInternal",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"),
        index = 4)
    private int adjustWidth(int guiWidth, int guiHeight, int tooltipX, int tooltipY, int tooltipWidth, int tooltipHeight) {
        final int PADDING = 8;

        int paddedWidth = tooltipWidth + PADDING;
        int paddedHeight = tooltipHeight + PADDING;

        scale = 1f;

        if (paddedWidth > guiWidth) {
            scale = (float)guiWidth / paddedWidth;
        }

        if (paddedHeight > guiHeight) {
            scale = Math.min(scale, (float)guiHeight / paddedHeight);
        }

        if (Config.useIntegerScaling) {
            float guiScale = (float)minecraft.getWindow().getGuiScale();
            if (scale * guiScale > 1f) {
                scale = Mth.floor(scale * guiScale) / guiScale;
            }
        }

        var guiGraphics = (GuiGraphics)(Object)this;
        guiGraphics.pose().scale(scale, scale, 1f);

        return Math.round(tooltipWidth * scale);
    }

    @ModifyArg(
        method = "renderTooltipInternal",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"),
        index = 5)
    private int adjustHeight(int tooltipHeight) {
        // apply extra adjustment for the 3px DefaultTooltipPositioner.positionTooltip adds to the height
        return Math.round(tooltipHeight * scale - 3f * (1f - scale));
    }

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "STORE"))
    private Vector2ic adjustPosition(Vector2ic position) {
        int x = position.x();

        // DefaultTooltipPositioner uses a minimum X of 4, which we mostly want to keep relative to the scale
        // Set to 3px empirically because the left pixel was getting clipped off at extreme scales
        if (x > 3) {
            x = Math.round((x - 3) / scale) + 3;
        }

        int y = Math.round(position.y() / scale);

        return new Vector2i(x, y);
    }

    @Inject(method = "renderTooltipInternal", at = @At("TAIL"))
    private void popPose(Font font, List<ClientTooltipComponent> tooltip, int x, int y, ClientTooltipPositioner positioner, CallbackInfo ci) {
        var guiGraphics = (GuiGraphics)(Object)this;
        guiGraphics.pose().popPose();
    }
}
