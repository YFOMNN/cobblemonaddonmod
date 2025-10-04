package com.myz.cobblemonaddonmod.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public record TeleportingTargetEnchantmentEffect() implements EnchantmentEntityEffect {

    // This is the required CODEC field
    public static final MapCodec<TeleportingTargetEnchantmentEffect> CODEC =
            MapCodec.unit(TeleportingTargetEnchantmentEffect::new);

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {

    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> getCodec() {
        return CODEC;
    }
}