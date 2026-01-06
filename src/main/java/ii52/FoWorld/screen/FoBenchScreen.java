package ii52.FoWorld.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import ii52.FoWorld.menu.FoBenchMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;


public class FoBenchScreen extends AbstractContainerScreen<FoBenchMenu> {
    // 确保图片真的在这个路径下
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("foworld", "textures/gui/fo_bench.png");

    public FoBenchScreen(FoBenchMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        // 原版标准容器大小
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partial, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        // 使用父类 AbstractContainerScreen 自动算好的起始坐标
        // leftPos 等于 (width - imageWidth) / 2
        // topPos 等于 (height - imageHeight) / 2
        int x = this.leftPos;
        int y = this.topPos;

        // 参数含义：贴图资源，起始X，起始Y，贴图UV_X，贴图UV_Y，宽度，高度
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // 1.20.1 现在的背景变暗方法
        this.renderBackground(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, delta);

        // 渲染物品悬浮提示
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}