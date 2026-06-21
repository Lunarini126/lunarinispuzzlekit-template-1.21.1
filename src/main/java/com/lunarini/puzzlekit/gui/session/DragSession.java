package com.lunarini.puzzlekit.gui.session;

import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lunarini.puzzlekit.gui.uiElement.GridBag;
import net.minecraft.world.entity.player.Player;

public class DragSession {
    public boolean success = false;
    public float slotWidth;
    public float slotHeight;
    public float itemWidth;
    public float itemHeight;
    public UIElement currentSourceSlot;
    public int[][] grids;
    public Player player;
    public GridBag gridBag;
}