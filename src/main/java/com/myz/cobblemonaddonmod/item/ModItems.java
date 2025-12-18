package com.myz.cobblemonaddonmod.item;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.item.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item BURGER = registerItem("burger", new BurgerItem(new Item.Settings()));
    public static final Item FRIES = registerItem("fries", new FriesItem(new Item.Settings()));
    public static final Item BEN_AND_HADI = registerItem("ben_and_hadi", new BenAndHadiItem(new Item.Settings()));
    public static final Item TIME_SODA = registerItem("time_soda", new TimeSoda(new Item.Settings()));
    public static final Item BOTTLE_CAP = registerItem("bottle_cap", new BottleCapItem(new Item.Settings().maxCount(12)));
    public static final Item PASSPORT = registerItem("passport", new PassportItem(new Item.Settings().maxCount(12)));
    public static final Item WEATHER_MILK = registerItem("weather_milk",new WeatherMilkItem(new Item.Settings(), WeatherMilkItem.Variant.BASE));
    public static final Item WEATHER_MILK_RAIN = registerItem("weather_milk_rain",new WeatherMilkItem(new Item.Settings(), WeatherMilkItem.Variant.RAIN));
    public static final Item WEATHER_MILK_THUNDER = registerItem("weather_milk_thunder",new WeatherMilkItem(new Item.Settings(), WeatherMilkItem.Variant.THUNDER));
    public static final Item WEATHER_MILK_CLEAR = registerItem("weather_milk_clear",new WeatherMilkItem(new Item.Settings(), WeatherMilkItem.Variant.CLEAR));

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, Identifier.of(CobblemonAddonMod.MOD_ID, name),item);
    }


    public static void registerModItems()
    {
        CobblemonAddonMod.LOGGER.info("Registering mod items for:"  + CobblemonAddonMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(BURGER);
            fabricItemGroupEntries.add(FRIES);
            fabricItemGroupEntries.add(BEN_AND_HADI);
            fabricItemGroupEntries.add(WEATHER_MILK);
            fabricItemGroupEntries.add(WEATHER_MILK_CLEAR);
            fabricItemGroupEntries.add(WEATHER_MILK_THUNDER);
            fabricItemGroupEntries.add(WEATHER_MILK_RAIN);
            fabricItemGroupEntries.add(TIME_SODA);
            fabricItemGroupEntries.add(BOTTLE_CAP);
            fabricItemGroupEntries.add(PASSPORT);
        });
    }
}
