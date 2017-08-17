package comp2541.bison.restaurant.data;

import org.json.JSONObject;

/**
 * Class implementing the tables of the restaurant.
 * @author Michele Cipriano
 */
public class Table {
	/**
	 * Unique table identifier.
	 */
	private int id;
	/**
	 * Table description.
	 */
	private String description;
	/**
	 * Table size.
	 */
	private int size;
	
	/**
	 * Create a new table.
	 * @param pId Table identifier.
	 */
	public Table(int pId) {
		id = pId;
	}
	
	/**
	 * Create a new table.
	 * @param pDescription Table description.
	 * @param pSize Table size.
	 */
	public Table(String pDescription, int pSize) {
		description = pDescription;
		size = pSize;
	}
	
	/**
	 * Create a new table.
	 * @param pId Table identifier.
	 * @param pDescription Table description.
	 * @param pSize Table size.
	 */
	public Table(int pId, String pDescription, int pSize) {
		id = pId;
		description = pDescription;
		size = pSize;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	/**
	 * Convert this instance into a JSON object with all the corresponding data.
	 * @return JSON object.
	 */
	public JSONObject getJSONObject() {
		JSONObject jsonTable = new JSONObject();
		
		jsonTable.put("id", id);
		jsonTable.put("description", description);
		jsonTable.put("size", size);
		
		return jsonTable;
	}
	
}
