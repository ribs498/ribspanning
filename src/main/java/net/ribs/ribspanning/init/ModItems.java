package net.ribs.ribspanning.init;

import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ribs.ribspanning.RibsPanning;
import net.ribs.ribspanning.items.NetheritePanItem;
import net.ribs.ribspanning.items.PanItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RibsPanning.MOD_ID);

    public static final RegistryObject<Item> CLAY_PAN = ITEMS.register("clay_pan",
            () -> new PanItem(new Item.Properties().durability(32)));
    public static final RegistryObject<Item> COPPER_PAN = ITEMS.register("copper_pan",
            () -> new PanItem(new Item.Properties().durability(128)));
    public static final RegistryObject<Item> IRON_PAN = ITEMS.register("iron_pan",
            () -> new PanItem(new Item.Properties().durability(256)));
    public static final RegistryObject<Item>DIAMOND_PAN = ITEMS.register("diamond_pan",
            () -> new PanItem(new Item.Properties().durability(1561)));
    public static final RegistryObject<Item> NETHERITE_PAN = ITEMS.register("netherite_pan",
            () -> new NetheritePanItem(new Item.Properties().durability(2031)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}