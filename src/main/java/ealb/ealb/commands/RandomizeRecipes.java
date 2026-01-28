package ealb.ealb.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class RandomizeRecipes implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Bukkit.broadcastMessage("Starting recipe randomization...");

        try {
            randomizeAllRecipes();
            Bukkit.broadcastMessage("Recipes randomized!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void randomizeAllRecipes() {

        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        List<Recipe> recipes = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();

        List<Recipe> cursedRecipes = new ArrayList<>();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (!recipeShouldBeAdded(recipe)) {
                cursedRecipes.add(recipe);
                continue;
            }
            recipes.add(recipe);
            outputs.add(recipe.getResult().clone());
        }
        if (recipes.size() <= 1)
            return;

        Collections.shuffle(outputs, new Random());

        // Remove all default recipes
        getServer().clearRecipes();

        for (Recipe r : cursedRecipes) {
            getServer().addRecipe(r);
        }
        for (int i = 0; i < recipes.size(); i++) {
            Recipe original = recipes.get(i);
            ItemStack newOutput = outputs.get(i);

            try {
                Recipe shuffledRecipe = createRecipeWithNewOutput(original, newOutput);
                if (shuffledRecipe != null) {
                    getServer().addRecipe(shuffledRecipe);
                }
            } catch (Exception e) {
              continue;
            }
        }
    }

    private boolean recipeShouldBeAdded(Recipe r){
        if (r instanceof ShapedRecipe)
           return true;
        if (r instanceof ShapelessRecipe)
           return true;

        return false;
    }

    private Recipe createRecipeWithNewOutput(Recipe original, ItemStack newOutput) {

        if (original instanceof ShapedRecipe) {
            ShapedRecipe shaped = (ShapedRecipe) original;
            ShapedRecipe newRecipe = new ShapedRecipe(shaped.getKey(), newOutput);
            newRecipe.shape(shaped.getShape());
            shaped.getIngredientMap().forEach((c, choice) -> newRecipe.setIngredient(c.charValue(), choice.getData()));
            return newRecipe;
        }

        if (original instanceof ShapelessRecipe) {
            ShapelessRecipe shapeless = (ShapelessRecipe) original;
            ShapelessRecipe newRecipe = new ShapelessRecipe(shapeless.getKey(), newOutput);
            shapeless.getIngredientList().forEach(choice -> newRecipe.addIngredient(choice.getData()));
            return newRecipe;
        }


        return null;
    }
}
