package com.lunarini.puzzlekit.item;

import net.minecraft.world.item.Item;
import org.joml.Vector2i;

public class LargeItem extends Item {

    public Vector2i[][] getOccupy() {
        return occupy;
    }

    Vector2i[][] occupy;

    public LargeItem(Properties properties,Vector2i[][] a) {
        super(properties);
        occupy = a;
    }
}