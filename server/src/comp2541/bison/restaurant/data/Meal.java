package comp2541.bison.restaurant.data;

import org.json.JSONObject;

/**
 * Meal class represents the a single meal in the menu.
 * @author Jones Agwata
 */
public class Meal {
	/**
	 * Unique identifier.
	 */
	private int id;
	/**
	 * Name of the meal.
	 */
	private String name;
	/**
	 * Description of the meal.
	 */
	private String description;
	/**
	 * Price of the meal.
	 */
	private int price;
	/**
	 * Type of the meal.
	 */
	private String type;
	
	/**
	 * Create a new meal.
	 * @param pName Meal name.
	 * @param pDescription Meal description.
	 * @param pPrice Meal price.
	 * @param pType Meal type.
	 */
	public Meal(int pID, String pName, String pDescription, int pPrice, String pType){
		id = pID;
		name = pName;
		description = pDescription;
		price = pPrice;
		type = pType;
		
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getPrice() {
		return price;
	}
	
	public String getType() {
		return type;
	}
	
	public void setId(int pId) {
		id = pId;
	}
	
	public void setName(String pMealName) {
		name = pMealName;
	}
	
	public void setDescription(String pDescription) {
		description = pDescription;
	}
	
	public void setPrice(int pPrice) {
		price = pPrice;
	}
	
	public void setType(String pType) {
		type = pType;
	}
	
	/**
	 * Convert this instance into a JSON object with all the corresponding data.
	 * @return JSON object.
	 */
	public JSONObject getJSONObject() {
		JSONObject jsonMeal = new JSONObject();
		
		jsonMeal.put("id", id);
		jsonMeal.put("name", name);
		jsonMeal.put("description", description);
		jsonMeal.put("price", price);
		jsonMeal.put("category", type);
		
		return jsonMeal;
	}
}
