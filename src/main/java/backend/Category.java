package backend;

import java.util.ArrayList;
import java.util.Arrays;

public class Category {
	// ArrayList of all categories
	private static ArrayList<Category> expenseCategories = new ArrayList<>();
	private static ArrayList<Category> incomeCategories = new ArrayList<>();
	private static ArrayList<String> expenseNames = new ArrayList<>();
	private static ArrayList<String> incomeNames = new ArrayList<>();

	// provide true for expense category, false for income category
	// private static final boolean EXPENSE = true, INCOME = false;

	// default categories
	private static final String[] BASE_EXPENSE_CATEGORIES = { "other", "food", "bills", "transportation", "shopping" };
	private static final String[] BASE_INCOME_CATEGORIES = { "other", "salary", "gifts", "investments", "odd jobs" };
	private static final String DEFAULT = "other";

	// default categories can be changed in settings
	private static ArrayList<String> EXPENSE_CATEGORIES = new ArrayList<String>(Arrays.asList(BASE_EXPENSE_CATEGORIES));
	private static ArrayList<String> INCOME_CATEGORIES = new ArrayList<String>(Arrays.asList(BASE_INCOME_CATEGORIES));

	// static methods

	public static void initialize() {
		for (String s : EXPENSE_CATEGORIES) {
			if (!expenseNames.contains(s))
				new Category(s, true);
		}
		for (String s : INCOME_CATEGORIES) {
			if (!incomeNames.contains(s))
				new Category(s, false);
		}
	}

	// static getters

	// eventually faze in getCategories(boolean type)
	public static ArrayList<Category> getCategories(boolean type) {
		return type ? expenseCategories : incomeCategories;
	}

	public static ArrayList<String> getCategoryNames(boolean type) {
		return type ? expenseNames : incomeNames;
	}

	// creates category if it doesn't exist
	public static Category get(String name, boolean type) {
		name = name.toLowerCase().trim();
		if (type) { // if expense
			for (Category c : expenseCategories) {
				if (c.name.equals(name))
					return c;
			}
		} else { // if income
			for (Category c : incomeCategories) {
				if (c.name.equals(name))
					return c;
			}

		}

		return new Category(name, type);
	}

	public static void setCategories(String[] expense, boolean type) {
		if (type) {

			EXPENSE_CATEGORIES = new ArrayList<String>(Arrays.asList(expense));
		} else {
			INCOME_CATEGORIES = new ArrayList<String>(Arrays.asList(expense));
		}
		initialize();
	}

	// returns all descriptions for a category
	public static String[] getDescriptions(boolean type) {
		ArrayList<String> descriptions = new ArrayList<>();
		for (Transaction tr : Transaction.getTransactions()) {
			if (tr == null)
				continue;
			if (tr.isExpense() == type && !descriptions.contains(tr.getDescription())) {
				descriptions.add(tr.getDescription());
			}
		}
		return descriptions.toArray(new String[descriptions.size()]);
	}

	// delete a category
	public static void delete(String name, boolean type) {
		// gets category and default category
		Category deleting = get(name, type), standard = get(DEFAULT, type);

		// sets all transactions with this category to default
		for (String description : deleting.descriptions) {
			for (Transaction tr : Transaction.getTransactions()) {
				if (tr != null && tr.getCategory().equals(deleting) && tr.getDescription().equals(description)) {
					tr.setCategory(standard);
				}
			}
		}
		// removes category from lists
		if (type) { // if expense
			expenseCategories.remove(deleting);
			expenseNames.remove(name);
		} else { // if income
			incomeCategories.remove(deleting);
			incomeNames.remove(name);
		}
	} // delete()

	// bulk reassign
	public static void bulkReassign(String description, Category category, boolean type) {
		for (Transaction tr : Transaction.getTransactions()) {
			if (tr != null && tr.getDescription().equals(description) && tr.isExpense() == type) {
				tr.setCategory(category);
			}
		}
	}

	// tests if a name belongs to a category (income/expense specific)
	public static boolean isCategory(String name, boolean type) {
		boolean isCategory = false;
		name = name.toLowerCase().trim();
		// type is true if expense, false if income
		// what in JS syntax is this btw :D
		ArrayList<Category> categories = type ? expenseCategories : incomeCategories;
		for (Category c : categories) {
			if (c.name.equals(name))
				isCategory = true;
		}
		return isCategory;
	}

	// instance variables
	private String name;
	private boolean type; // true if expense, false if income
	private ArrayList<String> descriptions = new ArrayList<>(); // list of all descriptions with this category

	// constructor
	public Category(String name, boolean type) {
		this.name = name.toLowerCase().trim();
		this.type = type;
		// if type is true then it is an expense category
		if (type) {
			if (!expenseCategories.contains(this)) {
				expenseCategories.add(this);
				expenseNames.add(name);
				expenseCategories.sort((a, b) -> a.compareTo(b));
				expenseNames.sort((a, b) -> a.compareTo(b));
			}
			// else it is an income category
		} else {
			if (!incomeCategories.contains(this)) {
				incomeCategories.add(this);
				incomeNames.add(name);
				incomeCategories.sort((a, b) -> a.compareTo(b));
				incomeNames.sort((a, b) -> a.compareTo(b));
			}
		}
	}

	// add description to category
	public void addDescription(String description) {
		if (!descriptions.contains(description)) {
			descriptions.add(description);
		}
	}

	// getters
	public String getName() {
		return name;
	}

	public boolean getType() {
		return type;
	}

	// equal based on name
	public boolean equals(Category other) {
		return name.equals(other.name) && type == other.type;
	}

	public int compareTo(Category other) {
		return name.compareTo(other.name);
	}

	// :)
	public String toString() {
		return name;
	}

}
