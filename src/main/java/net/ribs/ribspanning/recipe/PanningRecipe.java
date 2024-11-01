package net.ribs.ribspanning.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluid;
import net.ribs.ribspanning.init.ModRecipes;
import org.jetbrains.annotations.NotNull;

public class PanningRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final List<Drop> drops;
    private final float experience;
    private final Fluid requiredFluid;
    private final Ingredient panBlacklist;

    public PanningRecipe(ResourceLocation id, Ingredient input, List<Drop> drops, float experience, Fluid requiredFluid, Ingredient panBlacklist) {
        this.id = id;
        this.input = input;
        this.drops = drops;
        this.experience = experience;
        this.requiredFluid = requiredFluid;
        this.panBlacklist = panBlacklist;
    }

    public Ingredient getInput() {
        return input;
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public int getExperience() {
        return (int) experience;
    }
    public Fluid getRequiredFluid() {
        return requiredFluid;
    }

    public Ingredient getPanBlacklist() {
        return panBlacklist;
    }
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PANNING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.PANNING_RECIPE_TYPE.get();
    }

    public ItemStack getRandomDrop(RandomSource random) {
        double totalWeight = drops.stream().mapToDouble(Drop::chance).sum();
        double r = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (Drop drop : drops) {
            cumulative += drop.chance();
            if (r <= cumulative) {
                return drop.itemStack().copy();
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * @param chance Relative weight
     */
    public record Drop(ItemStack itemStack, double chance) {
    }
}
