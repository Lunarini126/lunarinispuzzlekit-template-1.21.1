package com.lunarini.puzzlekit.gui;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemScaleConfig {

    // 单例
    private static final ItemScaleConfig INSTANCE = new ItemScaleConfig();
    public static ItemScaleConfig getInstance() { return INSTANCE; }

    // 数据类
    public record ScaleParams(float width, float height, float scale) {}

    // 映射表：注册名 → 显示参数
    private final Map<ResourceLocation, ScaleParams> paramsMap = new HashMap<>();

    private ItemScaleConfig() {
        // 初始化默认配置
        registerDefaults();
    }

    private void registerDefaults() {
        // TACZ 枪械：大一点
        put("tacz:modern_kinetic_gun", 5, 2, 1.0f);

        // 原版物品：默认大小
        put("minecraft:diamond", 2, 1, 0.8f);
        put("minecraft:gold_block", 2, 2, 0.8f);

        // 某些特殊物品：小一点
        put("minecraft:stick", 16, 16, 1);
    }

    public void put(String id, float width, float height, float scale) {
        paramsMap.put(ResourceLocation.parse(id), new ScaleParams(width, height, scale));
    }

    public void put(ResourceLocation id, float width, float height, float scale) {
        paramsMap.put(id, new ScaleParams(width, height, scale));
    }

    public ScaleParams get(ResourceLocation itemId) {
        return paramsMap.getOrDefault(itemId, new ScaleParams(1, 1, 1.0f));  // 默认
    }

    public ScaleParams get(String itemId) {
        return get(ResourceLocation.parse(itemId));
    }

    // 根据 ItemStack 获取
    public ScaleParams get(ItemStack stack) {
        if (stack.isEmpty()) return new ScaleParams(1, 1, 1.0f);
        return get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }
}