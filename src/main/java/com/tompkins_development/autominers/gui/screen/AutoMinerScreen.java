package com.tompkins_development.autominers.gui.screen;

import com.tompkins_development.autominers.AutoMiners;
import com.tompkins_development.autominers.gui.menu.AutoMinerMenu;
import com.tompkins_development.autominers.utils.BlockInfo;
import com.tompkins_development.autominers.utils.StringUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tompkins_development.autominers.utils.StringUtils.formatCount;

public class AutoMinerScreen extends AbstractContainerScreen<AutoMinerMenu> {

    public static int X_OFFSET = -15;
    private static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath(AutoMiners.MOD_ID, "textures/gui/container/autominer.png");
    private List<ItemRenderInfo> renderedItems = new ArrayList<>();

    private float scrollAmount = 0.0F; // Scroll amount between 0.0 and 1.0
    private boolean isScrolling = false; // Whether the scrollbar is being dragged
    private boolean canScroll = false; // Whether scrolling is possible (more items than visible)
    private int maxScroll = 0; // Maximum scroll position

    private int scrollbarX;
    private int scrollbarY;
    private final int scrollbarWidth = 12;
    private final int scrollbarHeight = 70; // Adjust based on GUI height



    public AutoMinerScreen(AutoMinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelY += 17;
        this.inventoryLabelX += X_OFFSET;
        this.titleLabelX += X_OFFSET;

        // Initialize scrollbar positions here
        this.scrollbarX = this.leftPos + X_OFFSET + 136; // Adjust this value based on your GUI layout
        this.scrollbarY = this.topPos + 16;              // Adjust this value based on your GUI layout
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int scrollbarStartY = scrollbarY;
            int scrollbarEndY = scrollbarY + scrollbarHeight;
            if (mouseX >= scrollbarX && mouseX < scrollbarX + scrollbarWidth && mouseY >= scrollbarStartY && mouseY < scrollbarEndY) {
                this.isScrolling = this.canScroll;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isScrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.canScroll) {
            int maxScrollRows = maxScroll;
            this.scrollAmount = (float) (this.scrollAmount - (scrollY / maxScrollRows));
            this.scrollAmount = Math.max(0.0F, Math.min(this.scrollAmount, 1.0F));

            // Snap to the nearest row
            this.scrollAmount = Math.round(this.scrollAmount * maxScrollRows) / (float) maxScrollRows;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }




    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isScrolling && this.canScroll) {
            int scrollbarStartY = this.scrollbarY;
            int scrollbarEndY = scrollbarY + scrollbarHeight - 15; // Subtract the handle height
            float scrollRange = scrollbarEndY - scrollbarStartY;
            this.scrollAmount = ((float) mouseY - scrollbarStartY) / scrollRange;
            this.scrollAmount = Math.max(0.0F, Math.min(this.scrollAmount, 1.0F));

            // Snap to the nearest row
            this.scrollAmount = Math.round(this.scrollAmount * maxScroll) / (float) maxScroll;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }






    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(SPRITE, this.leftPos + X_OFFSET, this.topPos, 0, 0, 197, 182);



        // Calculate scrollbar handle position
        int scrollbarHandleHeight = 15;
        int scrollbarTrackHeight = scrollbarHeight - scrollbarHandleHeight;
        int scrollbarHandleY = scrollbarY + (int) (this.scrollAmount * scrollbarTrackHeight);


        // Draw the scrollbar handle
        guiGraphics.blit(SPRITE, scrollbarX, scrollbarHandleY, 244, 0, scrollbarWidth, scrollbarHandleHeight);

        // ???
        //guiGraphics.blit(SPRITE, scrollbarX, scrollbarHandleY, 244, 15, scrollbarWidth, scrollbarHandleHeight);
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics,mouseX,mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderItems(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        for (ItemRenderInfo renderInfo : renderedItems) {
            if (renderInfo.isMouseOver(mouseX, mouseY)) {
                // Render the tooltip for the item
                guiGraphics.renderTooltip(this.font, renderInfo.itemStack, mouseX, mouseY);
                break; // Assuming only one tooltip at a time
            }
        }

    }

    private void renderItems(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Map<Block, Integer> blockCounts = this.menu.blockEntity.getBlockCounts();
        List<Map.Entry<Block, Integer>> blockEntries = new ArrayList<>(blockCounts.entrySet());

        // Clear the list of rendered items
        renderedItems.clear();

        // Define the starting position and spacing
        int startX = this.leftPos + 2;
        int startY = this.topPos + 17;
        int x = startX;
        int y = startY;

        float itemScale = 0.75f; // Item scale factor
        float textScale = 0.5f;  // Text scale factor

        int itemSize = (int) (16 * itemScale);
        int spacingX = itemSize + 8; // Adjust as needed
        int spacingY = itemSize + 2;  // Adjust as needed

        int itemsPerRow = 6; // Number of items per row
        int rowsVisible = 5; // Number of rows visible at once

        // Calculate total rows and max scroll
        int totalItems = blockEntries.size();
        int totalRows = (int) Math.ceil((double) totalItems / itemsPerRow);
        this.canScroll = totalRows > rowsVisible;
        this.maxScroll = Math.max(0, totalRows - rowsVisible);

        // Calculate scroll offset
        int scrollRow = (int) (this.scrollAmount * this.maxScroll);
        int startIndex = scrollRow * itemsPerRow;
        int endIndex = Math.min(startIndex + (rowsVisible * itemsPerRow), totalItems);

        // Render items
        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<Block, Integer> entry = blockEntries.get(i);
            Block block = entry.getKey();
            int count = entry.getValue();

            ItemStack itemStack = new ItemStack(block);

            guiGraphics.pose().pushPose();

            // Translate to the position where the item should be rendered
            guiGraphics.pose().translate(x, y, 0);

            // Apply item scaling
            guiGraphics.pose().scale(itemScale, itemScale, itemScale);

            // Render the item at (0,0)
            guiGraphics.renderItem(itemStack, 0, 0);

            renderedItems.add(new ItemRenderInfo(x, y, itemSize, itemStack));

            // Now render the count text over the item
            String countText = formatCount(count);

            guiGraphics.pose().pushPose();

            // Calculate the position for the text within the item space
            float textX = 16 - (this.font.width(countText) * textScale) - 1; // Adjust -1 as needed
            float textY = 16 - (this.font.lineHeight * textScale) - 1;       // Adjust -1 as needed

            // Translate to the position where the text should be rendered
            guiGraphics.pose().translate(textX, textY, 200.0f);

            // Apply text scaling
            guiGraphics.pose().scale(textScale, textScale, textScale);

            // Render the text at (0,0)
            guiGraphics.drawString(this.font, countText, 0, 0, 0xFFFFFF, true);

            guiGraphics.pose().popPose(); // Restore pose after text rendering

            guiGraphics.pose().popPose(); // Restore pose after item rendering

            // Update positions for the next item
            x += spacingX;
            if ((i - startIndex + 1) % itemsPerRow == 0) {
                x = startX;
                y += spacingY;
            }
        }
    }






    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }


    private static class ItemRenderInfo {
        int x;
        int y;
        int size;
        ItemStack itemStack;

        ItemRenderInfo(int x, int y, int size, ItemStack itemStack) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.itemStack = itemStack;
        }

        boolean isMouseOver(double mouseX, double mouseY) {
            return mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size;
        }
    }

}
