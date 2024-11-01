package net.ribs.ribspanning.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.ribs.ribspanning.Reference;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);

    public static final RegistryObject<CreativeModeTab> PANNING_TAB = CREATIVE_MODE_TABS.register("panning_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CLAY_PAN.get()))
                    .title(Component.translatable("creativetab.panning_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.CLAY_PAN.get());
                        pOutput.accept(ModItems.COPPER_PAN.get());
                        pOutput.accept(ModItems.IRON_PAN.get());
                        pOutput.accept(ModItems.DIAMOND_PAN.get());
                        pOutput.accept(ModItems.NETHERITE_PAN.get());



                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}