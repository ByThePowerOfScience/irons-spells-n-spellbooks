package io.redspace.ironsspellbooks.gui.overlays;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class RecastOverlay extends GuiComponent {
    public final static ResourceLocation TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/gui/icons.png");
    static final int IMAGE_WIDTH = 54;
    static final int COMPLETION_BAR_WIDTH = 44;
    static final int IMAGE_HEIGHT = 21;

    static final int CONNECTOR_WIDTH = 6;
    static final int ORB_WIDTH = 10;
    static final int ORB_TEXTURE_OFFSET_X = 99;
    static final int ORB_TEXTURE_OFFSET_Y = 5;
    static final int CONNECTOR_TEXTURE_OFFSET_X = 109;
    static final int CONNECTOR_TEXTURE_OFFSET_Y = 8;

    public static void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        if (!ClientMagicData.getRecasts().hasRecastsActive())
            return;
        int totalHeightPerBar = 18;
        int screenTopBuffer = 6;
        var activeRecasts = ClientMagicData.getRecasts().getActiveRecasts();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        for (int castIndex = 0; castIndex < activeRecasts.size(); castIndex++) {
            var recastInstance = activeRecasts.get(castIndex);
            var spell = SpellRegistry.getSpell(recastInstance.getSpellId());
            int total = recastInstance.getTotalRecasts();
            int remaining = recastInstance.getRemainingRecasts();
            int totalWidth = total * ORB_WIDTH + (total - 1) * CONNECTOR_WIDTH;
            int barX = (screenWidth - totalWidth) / 2;
            int barY = screenTopBuffer + totalHeightPerBar * castIndex;

            poseStack.pushPose();
            poseStack.translate(barX - 18, barY - 2, 0);
            poseStack.scale(0.85f, 0.85f, 0.85f);
            RenderSystem.setShaderTexture(0, spell.getSpellIconResource());
            blit(poseStack, 0, 0, 0, 0, 16, 16, 16, 16);
            RenderSystem.setShaderTexture(0, TEXTURE);
            blit(poseStack, -2, -2, 116, 0, 20, 20, 256, 256);
            poseStack.popPose();

            for (int i = 0; i < total; i++) {
                int orbX = barX + (ORB_WIDTH + CONNECTOR_WIDTH) * i;
                int connectorX = orbX + ORB_WIDTH;
                if (i + 1 < total) {
                    // connector
                    blit(poseStack, connectorX, barY + 3, CONNECTOR_TEXTURE_OFFSET_X, CONNECTOR_TEXTURE_OFFSET_Y, 6, 4, 256, 256);
                }
                //orb filling
                boolean charged = i < remaining;
                blit(poseStack, orbX, barY, ORB_TEXTURE_OFFSET_X + (charged ? 0 : 10), ORB_TEXTURE_OFFSET_Y + 21, ORB_WIDTH, ORB_WIDTH, 256, 256);
                if (charged) {
//                    RenderSystem.setShader(GameRenderer::getRendertypeEyesShader);
//                    Vector3f color = SpellRegistry.getSpell(recastInstance.getKey()).getSchoolType().getTargetingColor();
//                    RenderSystem.setShaderColor(color.x(), color.y(), color.z(), 1f);
//                    blit(poseStack, orbX, barY, ORB_TEXTURE_OFFSET_X + 10, ORB_TEXTURE_OFFSET_Y + 21, ORB_WIDTH, ORB_WIDTH, 256, 256);
//                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
//                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    Vector3f color = spell.getSchoolType().getTargetingColor();
                    RenderSystem.setShaderColor(color.x(), color.y(), color.z(), 1f);
                    blit(poseStack, orbX, barY, ORB_TEXTURE_OFFSET_X + (charged ? 0 : 10), ORB_TEXTURE_OFFSET_Y + 21, ORB_WIDTH, ORB_WIDTH, 256, 256);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    //int color = SpellRegistry.getSpell(recastInstance.getSpellId()).getSchoolType().getDisplayName().getStyle().getColor().getValue();
                    //fillGradient(poseStack, orbX + 3, barY + 3, orbX + 7, barY + 7, color, color, 0);
                }
                //orb
                blit(poseStack, orbX, barY, ORB_TEXTURE_OFFSET_X, ORB_TEXTURE_OFFSET_Y, ORB_WIDTH, ORB_WIDTH, 256, 256);
            }

            int textX = (barX + (ORB_WIDTH + CONNECTOR_WIDTH) * total);
            gui.getFont().draw(poseStack,formatTime(recastInstance.getTicksRemaining(), recastInstance.getTicksToLive()), textX, barY + (ORB_WIDTH - gui.getFont().lineHeight) / 2, ChatFormatting.WHITE.getColor());
        }
    }

    private static String formatTime(int ticksRemaining, int totalTicks) {
        var totalSeconds = totalTicks / 20;
        var remainingSeconds = ticksRemaining / 20;
        String time = "";
        if (totalSeconds > 60) {
            time += String.format("%s:", remainingSeconds / 60);
            remainingSeconds %= 60;
        }
        if (totalSeconds >= 10) {
            time += remainingSeconds / 10;
        }
        time += remainingSeconds % 10;
        return time +"s";
    }
}
