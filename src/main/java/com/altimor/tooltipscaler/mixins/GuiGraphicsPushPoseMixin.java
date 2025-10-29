package com.altimor.tooltipscaler.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;

// Our popPose mixin applies as late as possible, so we need to pushPose as early as possible
@Mixin(value = GuiGraphics.class, priority = -Integer.MAX_VALUE)
public abstract class GuiGraphicsPushPoseMixin {
    @Inject(method = "renderTooltipInternal", at = @At("HEAD"))
    private void pushPose(Font font, List<ClientTooltipComponent> tooltip, int x, int y, ClientTooltipPositioner positioner, CallbackInfo ci) {
        var guiGraphics = (GuiGraphics)(Object)this;
        guiGraphics.pose().pushPose();
    }
}
