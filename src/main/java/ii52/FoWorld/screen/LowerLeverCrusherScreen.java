package ii52.FoWorld.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;


public class LowerLeverCrusherScreen extends AbstractContainerScreen<ii52.FoWorld.menu.LowerLeverCrusherMenu> {
    // 确保图片真的在这个路径下
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("foworld", "textures/gui/lower_lever_crusher.png");

    public LowerLeverCrusherScreen(ii52.FoWorld.menu.LowerLeverCrusherMenu menu, Inventory inv, Component title) {
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
        int progressWidth = this.menu.getScaledProgress();

        // 参数：贴图, 屏幕X, 屏幕Y, 贴图U, 贴图V, 绘制宽度, 绘制高度
        guiGraphics.blit(TEXTURE, x + 68, y + 31, 176, 0, progressWidth, 17);
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