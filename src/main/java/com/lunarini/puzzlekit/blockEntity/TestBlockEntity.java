package com.lunarini.puzzlekit.blockEntity;

import com.lunarini.puzzlekit.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TestBlockEntity extends BlockEntity {

    public TestBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TEST_BLOCK_ENTITY.get(), pos, blockState);
    }

    private String testInt = "哈哈";

    public String getTestInt() {
        return testInt;
    }

    public void setTestInt(String value) {
        this.testInt = value;
        setChanged(); // 标记已更改，需要保存
    }
}
