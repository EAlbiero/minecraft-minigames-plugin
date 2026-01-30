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
        Bukkit.resetRecipes();

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
                Bukkit.getLogger().warning(e.toString());
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
        if (original instanceof ShapedRecipe s) {
            ShapedRecipe r = new ShapedRecipe(s.getKey(), newOutput);
            r.shape(s.getShape());
            s.getChoiceMap().forEach((c, choice) -> {
                if (choice != null) r.setIngredient(c, choice);
            });
            return r;
        }
        if (original instanceof ShapelessRecipe s) {
            ShapelessRecipe r = new ShapelessRecipe(s.getKey(), newOutput);
            s.getChoiceList().forEach(choice -> {
                if (choice != null) r.addIngredient(choice);
            });
            return r;
        }
        return null;
    }
}
