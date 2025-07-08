package euphy.upo.create_cog_hud.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import euphy.upo.create_cog_hud.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import euphy.upo.create_cog_hud.CreateCogHUD;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class HealthBarRenderer {

    private final Minecraft minecraft = Minecraft.getInstance();
    private final ResourceLocation CUSTOM_HEALTH_TEXTURE = ResourceLocation.fromNamespaceAndPath(CreateCogHUD.MODID, "textures/gui/custom_health.png");

    private final int[] ICON_CONTAINER = {0, 0};
    private final int[] ICON_FULL = {18, 0};
    private final int[] ICON_HALF = {36, 0};
    private final int[] ICON_ABSORBING = {54, 0};

    private final int[] ICON_POISON_FULL = {72, 0};
    private final int[] ICON_POISON_HALF = {90, 0};
    private final int[] ICON_WITHER_FULL = {18, 18};
    private final int[] ICON_WITHER_HALF = {36, 18};


    private static final int ICON_DISPLAY_SIZE = 9;
    private static final int ICON_SOURCE_SIZE = 18;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private static final long WAVE_INTERVAL_MS = 12000;
    private static final long WAVE_PROPAGATION_DELAY_MS = 120;
    private static final long ROTATION_DURATION_MS = 1000;

    private boolean isWaveActive = false;
    private long waveStartTime = 0;
    private long nextWaveTime = 0;

    @SubscribeEvent
    public void onRenderGui(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)) {

            if (!Config.ENABLE_CUSTOM_HEALTH.get()) {
                return;
            }

            event.setCanceled(true);
            renderCustomHealthBar(event.getGuiGraphics());
        }
    }

    private void renderCustomHealthBar(GuiGraphics guiGraphics) {

        Player player = this.minecraft.player;


        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }


        int health = Mth.ceil(player.getHealth());
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        float maxHealth = player.getMaxHealth();
        int totalHearts = Mth.ceil(Math.max(maxHealth, health) / 2.0F) + Mth.ceil(absorption / 2.0F);

        boolean hasWither = player.hasEffect(MobEffects.WITHER);
        boolean hasPoison = player.hasEffect(MobEffects.POISON);

        long currentTime = System.currentTimeMillis();

        if (!isWaveActive && currentTime >= nextWaveTime) {
            isWaveActive = true;
            waveStartTime = currentTime;
            nextWaveTime = currentTime + WAVE_INTERVAL_MS;
        }

        if (isWaveActive) {
            long lastHeartStartTime = waveStartTime + (long)(totalHearts - 1) * WAVE_PROPAGATION_DELAY_MS;
            long waveEndTime = lastHeartStartTime + ROTATION_DURATION_MS;
            if (currentTime > waveEndTime) {
                isWaveActive = false;
            }
        }

        int x = guiGraphics.guiWidth() / 2 - 91;
        int y = guiGraphics.guiHeight() - 39;

        RenderSystem.enableBlend();

        for (int i = 0; i < totalHearts; i++) {
            int row = i / 10;
            int col = i % 10;
            int heartX = x + col * 8;
            int heartY = y - row * 10;

            float currentAngle = 0.0f;



            if (isWaveActive) {
                long heartAnimStartTime = waveStartTime + (long)i * WAVE_PROPAGATION_DELAY_MS;
                long heartAnimEndTime = heartAnimStartTime + ROTATION_DURATION_MS;
                if (currentTime >= heartAnimStartTime && currentTime <= heartAnimEndTime) {
                    float progress = (float)(currentTime - heartAnimStartTime) / ROTATION_DURATION_MS;
                    currentAngle = progress * 360.0f;
                }
            }

            int healthValue = i * 2;

            blitWithRotation(guiGraphics, heartX, heartY, ICON_CONTAINER, currentAngle);

            if (healthValue < health) {
                int[] fullIconToUse = hasWither ? ICON_WITHER_FULL : (hasPoison ? ICON_POISON_FULL : ICON_FULL);
                int[] halfIconToUse = hasWither ? ICON_WITHER_HALF : (hasPoison ? ICON_POISON_HALF : ICON_HALF);

                if (healthValue + 1 < health) {
                    blitWithRotation(guiGraphics, heartX, heartY, fullIconToUse, currentAngle);
                } else {
                    blitWithRotation(guiGraphics, heartX, heartY, halfIconToUse, currentAngle);
                }
            }


            if (healthValue < health + absorption) {

                if (healthValue >= health && healthValue < health + absorption) {
                    blitWithRotation(guiGraphics, heartX, heartY, ICON_ABSORBING, currentAngle);
                }
            }
        }
        RenderSystem.disableBlend();
    }

    // 带旋转的绘制方法
    private void blitWithRotation(GuiGraphics guiGraphics, int x, int y, int[] uv, float angle) {
        var poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(x, y, 0);


        poseStack.translate(ICON_DISPLAY_SIZE / 2.0, ICON_DISPLAY_SIZE / 2.0, 0);


        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));


        poseStack.translate(-ICON_DISPLAY_SIZE / 2.0, -ICON_DISPLAY_SIZE / 2.0, 0);


        guiGraphics.blit(
                CUSTOM_HEALTH_TEXTURE,
                0, 0,
                ICON_DISPLAY_SIZE, ICON_DISPLAY_SIZE,
                (float)uv[0], (float)uv[1],
                ICON_SOURCE_SIZE, ICON_SOURCE_SIZE,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        poseStack.popPose();
    }
}
