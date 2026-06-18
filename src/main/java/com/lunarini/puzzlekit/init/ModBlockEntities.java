package com.lunarini.puzzlekit.init;

import com.lunarini.puzzlekit.LunarinisPuzzleKit;
import com.lunarini.puzzlekit.blockEntity.TestBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LunarinisPuzzleKit.MODID);

    public static final Supplier<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("test_block_entity",
                    () -> BlockEntityType.Builder.of(
                            TestBlockEntity::new,
                            ModBlocks.TEST_BLOCK.get()  // 关联的方块
                    ).build(null));
}
