package com.lunarini.puzzlekit;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // 背包格子大小
    public static final ModConfigSpec.DoubleValue GRID_SLOT_WIDTH = BUILDER
            .comment("Grid slot width in pixels")
            .defineInRange("gridSlotWidth", 20.0, 1, 100);

    public static final ModConfigSpec.DoubleValue GRID_SLOT_HEIGHT = BUILDER
            .comment("Grid slot height in pixels")
            .defineInRange("gridSlotHeight", 20.0, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
