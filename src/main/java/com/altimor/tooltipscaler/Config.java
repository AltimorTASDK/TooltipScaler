package com.altimor.tooltipscaler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TooltipScaler.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue USE_INTEGER_SCALING = BUILDER
            .comment("Whether to round down to the nearest integer GUI scale")
            .define("useIntegerScaling", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean useIntegerScaling;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        useIntegerScaling = USE_INTEGER_SCALING.get();
    }
}
