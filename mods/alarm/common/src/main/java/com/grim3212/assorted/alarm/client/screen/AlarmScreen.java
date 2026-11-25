package com.grim3212.assorted.alarm.client.screen;

import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.alarm.Constants;
import com.grim3212.assorted.alarm.common.block.blockentity.AlarmBlockEntity;
import com.grim3212.assorted.alarm.common.network.AlarmUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * The alarm screen. The panel is drawn in {@code renderBackground} so it stays behind the buttons.
 */
public class AlarmScreen extends Screen {

    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/alarm.png");
    private static final int TEXT_TOP = 30;

    private final AlarmBlockEntity alarmBlockEntity;
    private int alarmType = 0;

    public AlarmScreen(AlarmBlockEntity alarmEntity) {
        super(Component.translatable("alarm.screen"));
        this.alarmBlockEntity = alarmEntity;
        this.alarmType = alarmEntity.getAlarmType();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.translatable("alarm.screen.done"), btn -> {
            this.alarmBlockEntity.setAlarmType(alarmType);
            Services.NETWORK.sendToServer(new AlarmUpdatePacket(this.alarmBlockEntity.getBlockPos(), this.alarmType));
            this.close();
        }).bounds(this.width / 2 - 25, 210, 50, 20).build());
        this.addRenderableWidget(Button.builder(alarmName(this.alarmType), btn -> {
            this.alarmType += 1;
            if (this.alarmType > 13) {
                this.alarmType = 0;
            }

            btn.setMessage(alarmName(this.alarmType));
        }).bounds(this.width / 2 - 50, 100, 100, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("alarm.screen.test"), btn -> {
            Minecraft.getInstance().player.playSound(AlarmBlockEntity.getSound(this.alarmType).get(), 1.0F, 1.0F);
        }).bounds(this.width / 2 - 25, 120, 50, 20).build());
    }

    // String.valueOf, not the bare char: TranslatableContents only accepts a Component, Number,
    // Boolean or String, and a Character throws while the component is built - which killed the whole
    // screen before it could open.
    private static Component alarmName(int alarmType) {
        return Component.translatable("alarm.screen.name", String.valueOf((char) ('A' + alarmType)));
    }

    private void close() {
        this.minecraft.setScreen(null);
    }

    @Override
    public void onClose() {
        this.close();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(graphics, mouseX, mouseY, partialTicks);

        int posX = (this.width - 256) / 2;
        // The texture is 256x230, but this blit assumes the 256x256 default, so it samples
        // 0..230/256 of the sheet into a 230px tall quad, as the panel always looked.
        graphics.blit(LOCATION, posX, 5, 0, 0, 256, 230);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int posX = (this.width - 256) / 2;

        // The panel texture is a busy pastel swirl. Dark text on it - even on a light plate - stays hard
        // to read at GUI scale, so the body is white, drop-shadowed text on an opaque dark backdrop.
        Component title = Component.translatable("alarm.screen");
        graphics.drawString(this.font, title, this.width / 2 - this.font.width(title) / 2, 10, 0xFFFF1010, true);

        Component description = Component.translatable("alarm.screen.description");
        int textBorder = 5;
        int textX = posX + textBorder;
        int textWidth = 256 - textBorder * 2;
        int textHeight = this.font.wordWrapHeight(description, textWidth);

        graphics.fill(textX - 3, TEXT_TOP - 3, textX + textWidth + 3, TEXT_TOP + textHeight + 3, 0xF01A1A22);
        // GuiGraphics#drawWordWrap draws without a shadow, so the wrapped lines are drawn one by one.
        int y = TEXT_TOP;
        for (FormattedCharSequence line : this.font.split(description, textWidth)) {
            graphics.drawString(this.font, line, textX, y, 0xFFFFFFFF, true);
            y += this.font.lineHeight;
        }
    }
}
