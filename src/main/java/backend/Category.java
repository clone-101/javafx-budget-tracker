package backend;

import java.util.ArrayList;

public class Category {
	// ArrayList of all categories
	private static ArrayList<Category> categories = new ArrayList<>();

	// instance variables
	private String name;
	private ArrayList<String> descriptions = new ArrayList<>();

	// constructor
	public Category(String name) {
		this.name = name.toLowerCase().trim();
		if (!categories.contains(this))
			categories.add(this);
	}

	// adds all similar transaction to the category
	public void add(Transaction adding) {
		String description = adding.getDescription();
		for (Transaction tr : Transaction.transactions) {
			if (tr == null)
				break;
			if (tr.getDescription().equals(description)) {
				if (tr.getCategory() == null || !tr.getCategory().equals(this)) {
					tr.setCategory(this);
				}
			}
		}
	}

	// equal based on name
	public boolean equals(Category other) {
		return name.equals(other.name);
	}

	// :)
	public String toString() {
		return name;
	}

	// static methods
	// tests if a name belongs to a category
	public static boolean isCategory(String name) {
		name = name.toLowerCase().trim();
		for (Category c : categories) {
			if (c.name.equals(name))
				return true;
		}
		return false;
	}

	// creates category if it doesn't exist
	public static Category get(String name) {
		name = name.toLowerCase().trim();
		for (Category c : categories) {
			if (c.name.equals(name))
				return c;
		}
		return new Category(name);
	}

	// deletes the category and nullfies the category instance of transactions
	public static void delete(String name) {
		Category temp = get(name);
		// nullifies all associated transaction categories
		for (String s : temp.descriptions) {
			for (Transaction tr : Transaction.transactions) {
				if (tr == null)
					break;
				if (tr.getDescription().equals(s)) {
					tr.setCategory(null);
				}
			}
		}
		categories.remove(temp);
	}
}
