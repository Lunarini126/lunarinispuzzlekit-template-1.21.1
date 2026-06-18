package com.lunarini.puzzlekit.init;

import com.lunarini.puzzlekit.LunarinisPuzzleKit;
import com.lunarini.puzzlekit.block.TestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModBlocks {
    // 创建一个 DeferredRegister 实例
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LunarinisPuzzleKit.MODID);

    // 注册方块
    public static final DeferredBlock<Block> TEST_BLOCK =
            BLOCKS.register("test_block",
                    () -> new TestBlock(BlockBehaviour.Properties.of()
                            .strength(2.0F, 6.0F)
                            .requiresCorrectToolForDrops()));
}
