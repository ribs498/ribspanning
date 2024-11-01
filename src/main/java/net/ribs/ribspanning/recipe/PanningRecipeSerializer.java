package net.ribs.ribspanning.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PanningRecipeSerializer implements RecipeSerializer<PanningRecipe> {

    public @NotNull PanningRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
        Ingredient input = Ingredient.fromJson(json.get("input"));
        List<PanningRecipe.Drop> drops = new ArrayList<>();
        JsonArray dropsArray = GsonHelper.getAsJsonArray(json, "drops");
        for (JsonElement element : dropsArray) {
            JsonObject dropObj = element.getAsJsonObject();
            ItemStack stack = ShapedRecipe.itemStackFromJson(dropObj.getAsJsonObject("item"));
            double chance = GsonHelper.getAsDouble(dropObj, "chance");
            drops.add(new PanningRecipe.Drop(stack, chance));
        }
        float experience = GsonHelper.getAsFloat(json, "experience", 0.0f);
        Fluid requiredFluid = Fluids.WATER;
        if (json.has("fluid")) {
            String fluidName = GsonHelper.getAsString(json, "fluid");
            requiredFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
            if (requiredFluid == null) {
                throw new JsonSyntaxException("Unknown fluid '" + fluidName + "'");
            }
        }
        Ingredient panBlacklist = Ingredient.EMPTY;
        if (json.has("pan_blacklist")) {
            panBlacklist = Ingredient.fromJson(json.get("pan_blacklist"));
        }
        return new PanningRecipe(recipeId, input, drops, experience, requiredFluid, panBlacklist);
    }
    @Override
    public PanningRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Ingredient input = Ingredient.fromNetwork(buffer);

        int dropsSize = buffer.readVarInt();
        List<PanningRecipe.Drop> drops = new ArrayList<>();
        for (int i = 0; i < dropsSize; i++) {
            ItemStack stack = buffer.readItem();
            double chance = buffer.readDouble();
            drops.add(new PanningRecipe.Drop(stack, chance));
        }
        float experience = buffer.readFloat();
        ResourceLocation fluidId = buffer.readResourceLocation();
        Fluid requiredFluid = ForgeRegistries.FLUIDS.getValue(fluidId);
        Ingredient panBlacklist = Ingredient.fromNetwork(buffer);
        return new PanningRecipe(recipeId, input, drops, experience, requiredFluid, panBlacklist);
    }
    @Override
    public void toNetwork(FriendlyByteBuf buffer, PanningRecipe recipe) {
        recipe.getInput().toNetwork(buffer);

        buffer.writeVarInt(recipe.getDrops().size());
        for (PanningRecipe.Drop drop : recipe.getDrops()) {
            buffer.writeItem(drop.itemStack());
            buffer.writeDouble(drop.chance());
        }
        buffer.writeFloat(recipe.getExperience());
        buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.FLUIDS.getKey(recipe.getRequiredFluid())));
        recipe.getPanBlacklist().toNetwork(buffer);
    }

}
