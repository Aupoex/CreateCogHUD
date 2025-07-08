package euphy.upo.create_cog_hud;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("general");

        ENABLE_CUSTOM_HEALTH = BUILDER
                .comment("Enable the custom gear health bar. If false, the vanilla health bar will be shown.")
                .define("enableCustomHealth", true);

        ENABLE_CUSTOM_ARMOR = BUILDER
                .comment("Enable the custom armor bar. If false, the vanilla armor bar will be shown.")
                .define("enableCustomArmor", true);

        ENABLE_CUSTOM_FOOD = BUILDER
                .comment("Enable the custom food bar. If false, the vanilla food bar will be shown.")
                .define("enableCustomFood", true);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static ModConfigSpec.BooleanValue ENABLE_CUSTOM_HEALTH;
    public static ModConfigSpec.BooleanValue ENABLE_CUSTOM_ARMOR;
    public static ModConfigSpec.BooleanValue ENABLE_CUSTOM_FOOD;

}