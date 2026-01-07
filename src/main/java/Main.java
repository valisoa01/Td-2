import Connection.DataRetriever;
import classes.Dish;
import classes.Ingredient;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        DataRetriever retriever = new DataRetriever();

//        try {
//            Dish dish = retriever.findDishById(1);
//
//            System.out.println("Dish trouvé :");
//            System.out.println("ID = " + dish.getId());
//            System.out.println("Nom = " + dish.getName());
//            System.out.println("Type = " + dish.getDishType());
//
//            System.out.println("\nIngrédients :");
//            for (Ingredient ing : dish.getIngredients()) {
//                System.out.println("- " + ing.getName() + "   (" + ing.getCategory() + ")");
//            }
//
//        } catch (Exception e) {
//            System.out.println("Erreur : " + e.getMessage());
//            e.printStackTrace();
//        }


//        try {
//            int dishId = 1;
//
//            List<Ingredient> ingredients = retriever.findIngredientsByDishId(dishId);
//
//            System.out.println("Ingrédients du plat id = " + dishId + " :");
//            for (Ingredient ing : ingredients) {
//                System.out.println(
//                        "- " + ing.getName()
//                                + " | prix = " + ing.getPrice()
//                                + " | catégorie = " + ing.getCategory()
//                );
//            }
//
//            if (ingredients.isEmpty()) {
//                System.out.println("⚠ Aucun ingrédient trouvé pour ce plat.");
//            }
//
//        } catch (Exception e) {
//            System.out.println("❌ Erreur : " + e.getMessage());
//            e.printStackTrace();
//        }


//        try {
//            int page = 1;
//            int size = 5;
//
//            List<Ingredient> ingredients = retriever.findIngredients(page, size);
//
//            System.out.println("📌 Page " + page + " — " + ingredients.size() + " ingrédients trouvés :");
//
//            for (Ingredient ing : ingredients) {
//
//                String dishName = (ing.getDish() != null)
//                        ? ing.getDish().getName()
//                        : "(aucun plat)";
//
//                System.out.println(
//                        "- " + ing.getName()
//                                + " | prix = " + ing.getPrice()
//                                + " | catégorie = " + ing.getCategory()
//                                + " | plat = " + dishName
//                );
//            }
//
//            if (ingredients.isEmpty()) {
//                System.out.println("⚠ Aucun ingrédient trouvé pour cette page.");
//            }
//
//        } catch (Exception e) {
//            System.out.println("❌ Erreur : " + e.getMessage());
//            e.printStackTrace();
//        }

        Dish dish = retriever.findDishById(1);

        System.out.println("Plat : " + dish.getName());
        System.out.println("Coût ingrédients = " + dish.getDishCost());

        try {
            System.out.println("Marge brute = " + dish.getGrossMargin());
        } catch (RuntimeException e) {
            System.out.println("⚠ " + e.getMessage());
        }


    }
}
