package net.ribs.ribspanning.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.ribs.ribspanning.init.ModItems;
import net.ribs.ribspanning.recipe.PanningRecipe;

public class PanningCategory implements IRecipeCategory<PanningRecipe> {
    public static final ResourceLocation UID = new ResourceLocation("ribspanning", "panning");
    public static final RecipeType<PanningRecipe> PANNING_TYPE = RecipeType.create("ribspanning", "panning", PanningRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    public PanningCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(new ResourceLocation("ribspanning", "textures/gui/panning_gui.png"), 0, 0, 150, 60);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.CLAY_PAN.get()));
        this.title = Component.translatable("jei.ribspanning.panning");
    }

    @Override
    public RecipeType<PanningRecipe> getRecipeType() {
        return PANNING_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PanningRecipe recipe, IFocusGroup focuses) {
        int xOffset = 5;
        int yOffset = 5;

        builder.addSlot(RecipeIngredientRole.INPUT, xOffset, yOffset)
                .addIngredients(recipe.getInput())
                .addTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(Component.translatable("jei.ribspanning.input"));
                });
        builder.addSlot(RecipeIngredientRole.CATALYST, xOffset + 25, yOffset)
                .addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(recipe.getRequiredFluid(), 1000))
                .setFluidRenderer(1000, false, 16, 16)
                .addTooltipCallback((recipeSlotView, tooltip) -> {
                    String fluidName = recipe.getRequiredFluid().getFluidType().getDescription().getString();
                    tooltip.add(Component.translatable("jei.ribspanning.fluid", fluidName));
                });
        if (!recipe.getPanBlacklist().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, xOffset + 25, yOffset + 20)
                    .addIngredients(recipe.getPanBlacklist())
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.translatable("jei.ribspanning.pan_blacklist"));
                    });
        }

        int outputX = xOffset + 50;

        double totalWeight = recipe.getDrops().stream().mapToDouble(PanningRecipe.Drop::chance).sum();
        for (int i = 0; i < recipe.getDrops().size(); i++) {
            PanningRecipe.Drop drop = recipe.getDrops().get(i);
            final double chancePercentage = drop.chance() / totalWeight * 100.0;

            builder.addSlot(RecipeIngredientRole.OUTPUT, outputX + (i % 5) * 18, yOffset + (i / 5) * 18)
                    .addItemStack(drop.itemStack())
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.translatable("jei.ribspanning.chance", String.format("%d%%", (int) chancePercentage)));
                    });
        }
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}