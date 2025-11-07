package com.myz.cobblemonaddonmod.enchantment;

import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.enchantment.custom.DayPowerEnchantmentEffect;
import com.myz.cobblemonaddonmod.enchantment.custom.LightningStrikerEnchantmentEffect;
import com.myz.cobblemonaddonmod.enchantment.custom.NightPowerEnchantmentEffect;
import com.myz.cobblemonaddonmod.enchantment.custom.TeleportingTargetEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {

    // Register the effect type
    public static final MapCodec<LightningStrikerEnchantmentEffect> LIGHTNING_STRIKER_EFFECT =
            registerEntityEffect("lightning_striker", LightningStrikerEnchantmentEffect.CODEC);

    private static <T extends EnchantmentEntityEffect> MapCodec<T> registerEntityEffect(String name,
                                                                                        MapCodec<T> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE,
                Identifier.of(CobblemonAddonMod.MOD_ID, name), codec);
    }

    public static final MapCodec<TeleportingTargetEnchantmentEffect> TARGET_ENCHANTMENT_EFFECT =
            registerEntityEffect("teleport_effect", TeleportingTargetEnchantmentEffect.CODEC);

    public static final MapCodec<DayPowerEnchantmentEffect> DAY_POWER_ENCHANTMENT_EFFECT =
            registerEntityEffect("day_power", DayPowerEnchantmentEffect.CODEC);

    public static final MapCodec<NightPowerEnchantmentEffect> NIGHT_POWER_ENCHANTMENT_EFFECT =
            registerEntityEffect("night_power", NightPowerEnchantmentEffect.CODEC);


    public static void registerEnchantmentEffects() {
        CobblemonAddonMod.LOGGER.info("Registering Mod Enchantment Effects for " + CobblemonAddonMod.MOD_ID);
    }
}