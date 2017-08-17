package comp2541.bison.restaurant.data;

import java.util.ArrayList;

import org.json.JSONObject;

/**
 * Class represents the menu with all the available meals.
 * @author Jones Agwata
 */
public class Menu {
	/**
	 * Menu identifier.
	 */
	private int id;
	/**
	 * Menu name.
	 */
	private String name;
	/**
	 * Meals.
	 */
	private ArrayList<Meal> meals;
	
	/**
	 * Create a new menu.
	 * @param pName Menu name.
	 */
	public Menu(String pName) {
		name = pName;
	}
	
	/**
	 * Create a new menu.
	 * @param pName Menu name.
	 * @param pMeal Meals included in the menu.
	 */
	public Menu(String pName, ArrayList<Meal> pMeal){
		name = pName;
		meals = pMeal;
	}
	
	/**
	 * Adds a meal to the menu.
	 * @param meal Meal.
	 */
	public void addMeal(Meal meal){
		meals.add(meal);
	}
	
	/**
	 * Removes the given meal from the menu.
	 * @param meal Meal.
	 */
	public void removeMeal(Meal meal){
		// Find index of meal in meals arraylist
		int mealIndex = meals.indexOf(meal);
		
		meals.remove(mealIndex);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<Meal> getMeals() {
		return meals;
	}
	
	public void setId(int pId) {
		id = pId;
	}
	
	public void setName(String pName) {
		name = pName;
	}
	
	public void setMeals(ArrayList<Meal> pMeals) {
		meals = pMeals;
	}
	
	/**
	 * Convert this instance into a JSON object with all the corresponding data.
	 * @return JSON object.
	 */
	public JSONObject getJSONObject() {
		JSONObject jsonMenu = new JSONObject();
		
		// Add every meal to the JSONObject:
		for (Meal meal : meals) {
			// If the key doesn't exist the method append create it, the value
			// is a JSONArray, which respects the communication protocol.
			jsonMenu.append(meal.getType(), meal.getJSONObject());
		}
		
		return jsonMenu;
	}
}
