package euphy.upo.create_cog_hud.client;

import com.mojang.blaze3d.systems.RenderSystem;
import euphy.upo.create_cog_hud.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import euphy.upo.create_cog_hud.CreateCogHUD;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class FoodBarRenderer {

    private final Minecraft minecraft = Minecraft.getInstance();

    private final ResourceLocation CUSTOM_FOOD_TEXTURE = ResourceLocation.fromNamespaceAndPath(CreateCogHUD.MODID, "textures/gui/custom_food.png");

    private final int[] ICON_FOOD_EMPTY = {0, 0};
    private final int[] ICON_FOOD_HALF = {18, 0};
    private final int[] ICON_FOOD_FULL = {36, 0};

    private final int[] ICON_FOOD_EMPTY_HUNGER = {0, 18};
    private final int[] ICON_FOOD_HALF_HUNGER = {18, 18};
    private final int[] ICON_FOOD_FULL_HUNGER = {36, 18};


    private static final int ICON_DISPLAY_SIZE = 9;
    private static final int ICON_SOURCE_SIZE = 18;
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 64;


    @SubscribeEvent
    public void onRenderFood(RenderGuiLayerEvent.Pre event) {
        if (event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) {


            if (!Config.ENABLE_CUSTOM_FOOD.get()) {
                return;
            }

            event.setCanceled(true);

            renderCustomFoodBar(event.getGuiGraphics());
        }
    }

    private void renderCustomFoodBar(GuiGraphics guiGraphics) {
        Player player = this.minecraft.player;
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        FoodData foodData = player.getFoodData();
        int foodLevel = foodData.getFoodLevel();

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int rightX = screenWidth / 2 + 91;
        int y = screenHeight - 39;

        boolean hasHungerEffect = player.hasEffect(MobEffects.HUNGER);

        RenderSystem.enableBlend();

        for (int i = 0; i < 10; i++) {
            int iconX = rightX - i * 8 - 9;

            int[] empty_icon = hasHungerEffect ? ICON_FOOD_EMPTY_HUNGER : ICON_FOOD_EMPTY;
            int[] half_icon = hasHungerEffect ? ICON_FOOD_HALF_HUNGER : ICON_FOOD_HALF;
            int[] full_icon = hasHungerEffect ? ICON_FOOD_FULL_HUNGER : ICON_FOOD_FULL;

            blit(guiGraphics, iconX, y, empty_icon);

            if (i * 2 + 1 < foodLevel) {
                blit(guiGraphics, iconX, y, full_icon);
            } else if (i * 2 + 1 == foodLevel) {
                blit(guiGraphics, iconX, y, half_icon);
            }
        }

        RenderSystem.disableBlend();
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, int[] uv) {
        guiGraphics.blit(
                CUSTOM_FOOD_TEXTURE, x, y,
                ICON_DISPLAY_SIZE, ICON_DISPLAY_SIZE,
                (float)uv[0], (float)uv[1],
                ICON_SOURCE_SIZE, ICON_SOURCE_SIZE,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
    }
}