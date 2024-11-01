package net.ribs.ribspanning.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.ribs.ribspanning.init.ModEnchantments;
import net.ribs.ribspanning.init.ModRecipes;
import net.ribs.ribspanning.recipe.PanningRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class PanItem extends Item {

    public PanItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BLOCK_FORTUNE || enchantment == Enchantments.UNBREAKING ||
                enchantment == Enchantments.BLOCK_EFFICIENCY || enchantment == Enchantments.MENDING ||
                enchantment == ModEnchantments.GENTLE_SHAKE.get();
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        PanningRecipe recipe = getPanningRecipe(offhandItem, world.getRecipeManager());
        if (recipe == null) {
            if (world.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ribspanning.no_pannable_item"), true);
            }
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (recipe.getPanBlacklist().test(player.getItemInHand(hand))) {
            if (world.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ribspanning.pan_blacklisted"), true);
            }
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (!isPlayerInFluid(player, recipe.getRequiredFluid())) {
            if (world.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ribspanning.wrong_fluid"), true);
            }
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        return ItemUtils.startUsingInstantly(world, player, hand);
    }
    private boolean isPlayerInFluid(Player player, Fluid fluid) {
        AABB boundingBox = player.getBoundingBox();
        Level level = player.level();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (double x = boundingBox.minX; x <= boundingBox.maxX; x += 0.1) {
            for (double y = boundingBox.minY; y <= boundingBox.maxY; y += 0.1) {
                for (double z = boundingBox.minZ; z <= boundingBox.maxZ; z += 0.1) {
                    mutablePos.set(Mth.floor(x), Mth.floor(y), Mth.floor(z));
                    FluidState fluidState = level.getFluidState(mutablePos);
                    if (fluidState.getType().isSame(fluid)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (entityLiving instanceof ServerPlayer player && !world.isClientSide) {
            ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
            PanningRecipe recipe = getPanningRecipe(offhandItem, world.getRecipeManager());
            if (recipe != null) {
                int gentleShakeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.GENTLE_SHAKE.get(), stack);
                boolean consumeOffhandItem = true;

                if (gentleShakeLevel > 0) {
                    float chanceToNotConsume = gentleShakeLevel * 0.15f;
                    if (world.getRandom().nextFloat() < chanceToNotConsume) {
                        consumeOffhandItem = false;
                    }
                }

                if (consumeOffhandItem) {
                    offhandItem.shrink(1);
                }

                int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
                int dropsCount = 1;
                for (int i = 0; i < fortuneLevel; i++) {
                    if (world.getRandom().nextFloat() < 0.25f) {
                        dropsCount++;
                    }
                }
                for (int i = 0; i < dropsCount; i++) {
                    ItemStack drop = recipe.getRandomDrop(world.getRandom());
                    if (!drop.isEmpty()) {
                        if (!player.addItem(drop)) {
                            player.drop(drop, false);
                        }
                    }
                }
                if (recipe.getExperience() > 0) {
                    float experience = recipe.getExperience();
                    int experienceWhole = (int) experience;
                    float experienceFraction = experience - experienceWhole;

                    if (experienceFraction > 0 && world.getRandom().nextFloat() < experienceFraction) {
                        experienceWhole += 1;
                    }
                    if (experienceWhole > 0) {
                        ExperienceOrb.award((ServerLevel) world, player.position(), experienceWhole);
                    }
                }
                stack.hurtAndBreak(1, player, (p) -> {
                    p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                });
            }
            world.playSound(null, player.getBlockX(), player.getBlockY(), player.getBlockZ(),
                    SoundEvents.BRUSH_GRAVEL_COMPLETED, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return stack;
    }



    @Override
    public int getUseDuration(ItemStack stack) {
        int baseDuration = 64;
        int efficiencyLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack);
        if (efficiencyLevel > 0) {
            baseDuration -= efficiencyLevel * 10;
            baseDuration = Math.max(baseDuration, 20);
        }
        return baseDuration;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }
    @Override
    public void onUseTick(Level world, @NotNull LivingEntity livingEntity, ItemStack stack, int count) {
        if (world.isClientSide && livingEntity instanceof Player player) {
            ItemStack offhandItem = player.getItemInHand(InteractionHand.OFF_HAND);
            PanningRecipe recipe = getPanningRecipe(offhandItem, world.getRecipeManager());
            if (recipe != null && recipe.getInput().getItems().length > 0) {
                double x = player.getX() + (world.random.nextDouble() - 0.5) * 0.5;
                double y = player.getY() + player.getEyeHeight() - 0.5;
                double z = player.getZ() + (world.random.nextDouble() - 0.5) * 0.5;

                if (player.tickCount % 12 == 0) {
                    world.playSound(player, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.BRUSH_GRAVEL, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
                ParticleOptions particleData;
                Item inputItem = recipe.getInput().getItems()[0].getItem();
                if (inputItem instanceof BlockItem blockItem) {
                    particleData = new BlockParticleOption(ParticleTypes.BLOCK, blockItem.getBlock().defaultBlockState());
                } else {
                    particleData = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(inputItem));
                }

                world.addParticle(particleData, x, y, z, 0, 0.1, 0);
            }
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.ribspanning.panitem.instructions").withStyle(ChatFormatting.GRAY));
    }

    private PanningRecipe getPanningRecipe(ItemStack stack, RecipeManager manager) {
        return manager.getAllRecipesFor(ModRecipes.PANNING_RECIPE_TYPE.get()).stream()
                .filter(recipe -> recipe.getInput().test(stack))
                .findFirst()
                .orElse(null);
    }
}
