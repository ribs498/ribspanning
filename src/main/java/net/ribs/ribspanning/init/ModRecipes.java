package net.ribs.ribspanning.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.ribs.ribspanning.Reference;
import net.ribs.ribspanning.recipe.PanningRecipe;
import net.ribs.ribspanning.recipe.PanningRecipeSerializer;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Reference.MOD_ID);

    public static final RegistryObject<RecipeSerializer<PanningRecipe>> PANNING_RECIPE_SERIALIZER =
            SERIALIZERS.register("panning", PanningRecipeSerializer::new);

    public static final RegistryObject<RecipeType<PanningRecipe>> PANNING_RECIPE_TYPE =
            TYPES.register("panning", () -> new RecipeType<PanningRecipe>() {
                @Override
                public String toString() {
                    return new ResourceLocation(Reference.MOD_ID, "panning").toString();
                }
            });

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}