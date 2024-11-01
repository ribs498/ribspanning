package net.ribs.ribspanning.init;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ribs.ribspanning.Reference;
import net.ribs.ribspanning.enchantments.GentleShakeEnchantment;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Reference.MOD_ID);

    public static final RegistryObject<Enchantment> GENTLE_SHAKE = REGISTER.register("gentle_shake",
            () -> new GentleShakeEnchantment(
                    Enchantment.Rarity.UNCOMMON,
                    EnchantmentCategory.DIGGER,
                    new EquipmentSlot[] { EquipmentSlot.MAINHAND }
            )
    );
}
