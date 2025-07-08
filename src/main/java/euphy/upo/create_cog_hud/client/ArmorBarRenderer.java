package euphy.upo.create_cog_hud.client;

import com.mojang.blaze3d.systems.RenderSystem;
import euphy.upo.create_cog_hud.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import euphy.upo.create_cog_hud.CreateCogHUD;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class ArmorBarRenderer {

    private final Minecraft minecraft = Minecraft.getInstance();

    private final ResourceLocation CUSTOM_ARMOR_TEXTURE = ResourceLocation.fromNamespaceAndPath(CreateCogHUD.MODID, "textures/gui/custom_armor.png");

    private final int[] ICON_ARMOR_EMPTY = {0, 0};
    private final int[] ICON_ARMOR_HALF = {18, 0};
    private final int[] ICON_ARMOR_FULL = {36, 0};

    private static final int ICON_DISPLAY_SIZE = 9;
    private static final int ICON_SOURCE_SIZE = 18;

    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 64;


    @SubscribeEvent
    public void onRenderArmor(RenderGuiLayerEvent.Pre event) {

        if (event.getName().equals(VanillaGuiLayers.ARMOR_LEVEL)) {

            if (!Config.ENABLE_CUSTOM_ARMOR.get()) {
                return;
            }

            event.setCanceled(true);
            renderCustomArmorBar(event.getGuiGraphics());
        }
    }

    private void renderCustomArmorBar(GuiGraphics guiGraphics) {
        Player player = this.minecraft.player;


        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        int armorValue = player.getArmorValue();
        if (armorValue <= 0) {
            return;
        }

        int x = guiGraphics.guiWidth() / 2 - 91;

        int y = guiGraphics.guiHeight() - 49;


        if (player.getAbsorptionAmount() > 0) {

            y -= 10;
        }

        RenderSystem.enableBlend();


        for (int i = 0; i < 10; i++) {
            int iconX = x + i * 8;

            if (i * 2 + 1 < armorValue) {

                blit(guiGraphics, iconX, y, ICON_ARMOR_FULL);
            } else if (i * 2 + 1 == armorValue) {

                blit(guiGraphics, iconX, y, ICON_ARMOR_HALF);
            } else {

                blit(guiGraphics, iconX, y, ICON_ARMOR_EMPTY);
            }
        }

        RenderSystem.disableBlend();
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, int[] uv) {
        guiGraphics.blit(
                CUSTOM_ARMOR_TEXTURE, x, y,
                ICON_DISPLAY_SIZE, ICON_DISPLAY_SIZE,
                (float)uv[0], (float)uv[1],
                ICON_SOURCE_SIZE, ICON_SOURCE_SIZE,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }
}