package com.tompkins_development.autominers.datagen;

import com.tompkins_development.autominers.AutoMiners;
import com.tompkins_development.autominers.block.ModBlocks;
import com.tompkins_development.autominers.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        //Ferrulite
        List<ItemLike> FERRULITE_SMELTABLES = List.of(ModBlocks.FERRULITE_ORE, ModItems.RAW_FERRULITE);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FERRULITE_INGOT.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.FERRULITE_NUGGET.get())
                .unlockedBy("has_ferrulite_nugget", has(ModItems.FERRULITE_NUGGET))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FERRULITE_NUGGET.get(), 9)
                .requires(ModItems.FERRULITE_INGOT)
                .unlockedBy("has_ferrulite_ingot", has(ModItems.FERRULITE_INGOT))
                .save(recipeOutput);

        oreSmelting(recipeOutput, FERRULITE_SMELTABLES, RecipeCategory.MISC, ModItems.FERRULITE_INGOT.get(), 0.25f, 200, "ferrulite");
        oreBlasting(recipeOutput, FERRULITE_SMELTABLES, RecipeCategory.MISC, ModItems.FERRULITE_INGOT.get(), 0.3f, 100, "ferrulite");

        //Upgrade Cards
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLANK_UPGRADE_CARD.get())
                .pattern("FBF")
                .pattern("FEF")
                .pattern("FRF")
                .define('F', ModItems.FERRULITE_INGOT.get())
                .define('B', Items.BLUE_DYE)
                .define('E', Items.END_CRYSTAL)
                .define('R', Items.RED_DYE)
                .unlockedBy("has_ferrulite_ingot", has(ModItems.FERRULITE_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FORTUNE_UPGRADE_CARD.get())
                .pattern("FBF")
                .pattern("FEF")
                .pattern("FRF")
                .define('F', ModItems.FERRULITE_INGOT.get())
                .define('B', Items.DIAMOND_PICKAXE)
                .define('E', ModItems.BLANK_UPGRADE_CARD.get())
                .define('R', Items.DIAMOND_BLOCK)
                .unlockedBy("has_upgrade_card", has(ModItems.BLANK_UPGRADE_CARD))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPEED_UPGRADE_CARD.get())
                .pattern("FBF")
                .pattern("FEF")
                .pattern("FRF")
                .define('F', ModItems.FERRULITE_INGOT.get())
                .define('B', Items.SUGAR)
                .define('E', ModItems.BLANK_UPGRADE_CARD.get())
                .define('R', Items.NETHERITE_BLOCK)
                .unlockedBy("has_upgrade_card", has(ModItems.BLANK_UPGRADE_CARD))
                .save(recipeOutput);

    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory recipeCategory, ItemLike result, float xp, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, recipeCategory, result, xp, cookingTime, group, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory recipeCategory, ItemLike result, float xp, int cookingTime, String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, recipeCategory, result, xp, cookingTime, group, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> serializer, AbstractCookingRecipe.Factory<T> recipeFactory, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String pRecipeName) {
        for(ItemLike itemLike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemLike), category, result, experience, cookingTime, serializer, recipeFactory).group(group).unlockedBy(getHasName(itemLike), has(itemLike))
                    .save(recipeOutput, AutoMiners.MOD_ID + ":" + getItemName(result) + pRecipeName + "_" + getItemName(itemLike));
        }
    }
}
