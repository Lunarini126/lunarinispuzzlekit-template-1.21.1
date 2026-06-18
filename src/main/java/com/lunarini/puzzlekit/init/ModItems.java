package com.lunarini.puzzlekit.init;

import com.lunarini.puzzlekit.LunarinisPuzzleKit;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LunarinisPuzzleKit.MODID);

    // 为 test_block 注册对应的 BlockItem（名称必须和方块一致）
    public static final DeferredItem<BlockItem> TEST_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.TEST_BLOCK);
}
