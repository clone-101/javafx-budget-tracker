package backend;

import java.util.ArrayList;

public class Category {
	// ArrayList of all categories
	private static ArrayList<Category> expenseCategories = new ArrayList<>();
	private static ArrayList<Category> incomeCategories = new ArrayList<>();
	private static ArrayList<String> expenseNames = new ArrayList<>();
	private static ArrayList<String> incomeNames = new ArrayList<>();

	// provide true for expense category, false for income category
	private static final boolean EXPENSE = true, INCOME = false;

	// default categories can be changed in settings
	private static String[] BASE_EXPENSE_CATEGORIES = { "other", "food", "bills", "transportation", "shopping" };
	private static String[] BASE_INCOME_CATEGORIES = { "other", "salary", "gifts", "investments", "odd jobs" };

	// static methods

	public static void initialize() {
		for (String s : BASE_EXPENSE_CATEGORIES) {
			new Category(s, true);
		}
		for (String s : BASE_INCOME_CATEGORIES) {
			new Category(s, false);
		}
	}

	// static getters
	public static ArrayList<Category> getExpenseCategories() {
		return expenseCategories;
	}

	public static ArrayList<Category> getIncomeCategories() {
		return incomeCategories;
	}

	public static ArrayList<String> getExpenseNames() {
		return expenseNames;
	}

	public static ArrayList<String> getIncomeNames() {
		return incomeNames;
	}

	public static void addAllIncome(String[] names) {
		for (String s : incomeNames) {
			new Category(s, INCOME);
		}
	}

	public static void addAllExpense(String[] names) {
		for (String s : expenseNames) {
			new Category(s, EXPENSE);
		}
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

	public static void delete(String name, boolean type) {
		Category temp = get(name, type);
		Category standard = get("other", type);

		// sets all transactions with this category to default
		for (String s : temp.descriptions) {
			for (Transaction tr : Transaction.transactions) {
				if (tr == null)
					break;
				if (tr.getDescription().equals(s)) {
					tr.setCategory(standard);
				}
			}
		}
		// removes category from lists
		if (type) { // if expense
			expenseCategories.remove(temp);
			expenseNames.remove(name);
		} else { // if income
			incomeCategories.remove(temp);
			incomeNames.remove(name);
		}
	} // delete()

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
			}
			// else it is an income category
		} else {
			if (!incomeCategories.contains(this)) {
				incomeCategories.add(this);
				incomeNames.add(name);
			}
		}
	}

	// getters
	public boolean getType() {
		return type;
	}

	// adds all similar transaction to the category
	public void add(Transaction adding) {
		String description = adding.getDescription();
		for (Transaction tr : Transaction.transactions) {
			if (tr == null) // skip null transactions (PFA)
				continue;
			if (tr.getDescription().equals(description)) {
				if (tr.getCategory() == null || !tr.getCategory().equals(this)) {
					tr.setCategory(this);
				}
			}
		}
	}

	// equal based on name
	public boolean equals(Category other) {
		return name.equals(other.name) && type == other.type;
	}

	// :)
	public String toString() {
		return name;
	}

}
