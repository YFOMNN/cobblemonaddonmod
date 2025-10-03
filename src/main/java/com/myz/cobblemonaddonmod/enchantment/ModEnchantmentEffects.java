package com.myz.cobblemonaddonmod.enchantment;

import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.enchantment.custom.LightningStrikerEnchantmentEffect;
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

    public static void registerEnchantmentEffects() {
        CobblemonAddonMod.LOGGER.info("Registering Mod Enchantment Effects for " + CobblemonAddonMod.MOD_ID);
    }
}