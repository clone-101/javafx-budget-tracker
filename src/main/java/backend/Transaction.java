package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.opencsv.CSVWriter;

public class Transaction {
	// store all transactions
	private static Transaction[] transactions = new Transaction[1000];
	private static int trCount = 0; // count for PFA
	// csv order
	private static int[] CSV_Order = { 0, 1, 2, 3 }; // date, description, fundsOut, fundsIn
	private static ArrayList<String> CSV_Ignore_Keywords = new ArrayList<>();
	// date format: MM/dd/yyyy
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	// working directory
	public static String WORKING_DIR;
	private static final String CSV = "content.csv"; // application data file

	// CSV column order defaults
	private static final int[] DEFAULT_CSV_ORDER = { 0, 1, 2, 3 }; // for content.csv
	private static final String[] CSV_ORDER_STR = { "Date", "Description", "Funds Out", "Funds In" };

	// ***************static methods*****************
	public static void chooseDirectory() {
		if (System.getProperty("os.name").toLowerCase().contains("mac"))
			WORKING_DIR = System.getProperty("user.home") + File.separator + "Budget Tracker" + File.separator;
		else
			WORKING_DIR = System.getProperty("user.dir") + File.separator;
	}

	// returns array of all transactions excluding null (PFA)
	public static Transaction[] getTransactions() {
		Transaction[] excludeNull = new Transaction[trCount];
		for (int i = 0; i < trCount; i++) {
			excludeNull[i] = transactions[i];
		}
		return excludeNull;
	}

	public static int[] getCSVOrder() {
		return CSV_Order;
	}

	public static String[] getCSVOrderStr() {
		return CSV_ORDER_STR;
	}

	public static boolean setCSVOrder(int[] order) {
		if (order.length == 4) {
			CSV_Order = order;
			return true;
		}
		return false;
	}

	public static Transaction[] getTransactions(boolean isExpense, String... search) {
		ArrayList<Transaction> temp = new ArrayList<>();
		for (Transaction tr : transactions) {
			if (tr != null && tr.isExpense() == isExpense) {
				if (search.length == 0 || tr.getDescription().contains(search[0].toLowerCase())) {
					temp.add(tr);
				}
			}
		}
		return temp.toArray(new Transaction[temp.size()]);

	}

	// CSV ignore methods
	public static void setIgnoreList(String[] keywords) {
		CSV_Ignore_Keywords = new ArrayList<>(Arrays.asList(keywords));
	}

	public static String[] getIgnoreList() {
		return CSV_Ignore_Keywords.toArray(new String[CSV_Ignore_Keywords.size()]);
	}

	public static void addIgnoreKeyword(String keyword) {
		CSV_Ignore_Keywords.add(keyword);
	}

	public static void removeIgnoreKeyword(String keyword) {
		CSV_Ignore_Keywords.remove(keyword);
	}

	// reads both content.csv and user csv
	public static void readFile(File filename, int[] order) throws Exception {

		int dateID = order[0], desID = order[1], fundOutID = order[2], fundInID = order[3];

		BufferedReader br = new BufferedReader(new FileReader(filename));
		String tempLine = "";
		String[] lineArr;

		while ((tempLine = br.readLine()) != null) {
			// excludes commas contained in quotes
			lineArr = tempLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

			Date date;
			double fundsIn, fundsOut;
			boolean keywordFound = false;
			try {
				date = FORMAT.parse(lineArr[dateID].replaceAll("\"", ""));
			} catch (Exception e) {
				continue;
			}
			try {
				fundsIn = Double.parseDouble(lineArr[fundInID].replaceAll("\"", ""));
			} catch (Exception e) {
				fundsIn = 0;
			}
			try {
				fundsOut = Double.parseDouble(lineArr[fundOutID].replaceAll("\"", ""));
			} catch (Exception e) {
				fundsOut = 0;
			}
			// ignore keywords
			for (String keyword : CSV_Ignore_Keywords) {

				if (lineArr[desID] != null && lineArr[desID].toLowerCase().trim().contains(keyword)) {
					keywordFound = true;
					break;
				} else
					keywordFound = false;
			}

			if (keywordFound)
				continue;
			try {
				// max category length is 15 characters (including quotes)
				if (lineArr.length >= 5 && lineArr[4].indexOf("*") == -1) {
					String category = lineArr[4].replaceAll("\"", "");
					new Transaction(date, lineArr[desID], fundsIn - fundsOut, category);
				} else
					new Transaction(date, lineArr[desID], fundsIn - fundsOut, "other");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		br.close();
	} // readFile()

	// save to content.csv
	public static void saveToFile() {
		String filePath = WORKING_DIR + File.separator + CSV;

		File file = new File(filePath);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			ArrayList<String[]> data = new ArrayList<>();
			for (Transaction tr : transactions) {
				if (tr != null) {
					String[] temp = new String[5];
					temp[1] = tr.getDescription();
					temp[4] = tr.getCategory().toString();
					temp[3] = Double.toString(tr.getFundsIn());
					temp[2] = Double.toString(tr.getFundsOut());
					temp[0] = FORMAT.format(tr.getDate());
					data.add(temp);
				}
			}
			writer.writeAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// initialize from content.csv
	public static void initializeCSV() {
		String filePath = WORKING_DIR + File.separator + CSV;

		File file = new File(filePath);
		file.getParentFile().mkdirs();

		try {
			readFile(file, DEFAULT_CSV_ORDER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ordered insertion method for transaction PFA
	private static void trInsert(int trCount, Transaction[] tr, Transaction element) {
		int index = trCount - 1;
		while (index >= 0) {
			if (tr[index].compareTo(element) < 0) {
				tr[index + 1] = tr[index--];
			} else
				break;
		}
		tr[index + 1] = element;
	}

	// returns total expenses for date range
	public static double getExpenses(Date start, Date end) {
		double sum = 0;
		for (int i = 0; i < transactions.length && transactions[i] != null; i++) {
			if (transactions[i].getDate().compareTo(start) > 0 && transactions[i].getDate().compareTo(end) < 0)
				sum += transactions[i].getFundsOut();
		}
		return sum;
	}

	// returns total income for date range
	public static double getIncome(Date start, Date end) {
		double sum = 0;
		for (int i = 0; i < transactions.length && transactions[i] != null; i++) {
			if (transactions[i].getDate().compareTo(start) > 0 && transactions[i].getDate().compareTo(end) < 0)
				sum += transactions[i].getFundsIn();
		}
		return sum;
	}

	// returns expenses by category for date range
	public static double getExpensesByCategory(Date start, Date end, Category category) {
		double sum = 0;
		for (Transaction tr : transactions) {
			if (tr != null && tr.getDate().compareTo(start) > 0 && tr.getDate().compareTo(end) < 0
					&& tr.getCategory().equals(category)) {
				sum += tr.getFundsOut();
			}
		}
		return sum;
	}

	// returns income by category for date range
	public static double getIncomeByCategory(Date start, Date end, Category category) {
		double sum = 0;
		for (Transaction tr : transactions) {
			if (tr != null && tr.getDate().compareTo(start) > 0 && tr.getDate().compareTo(end) < 0
					&& tr.getCategory().equals(category)) {
				sum += tr.getFundsIn();
			}
		}
		return sum;
	}
	// ***************instance methods*****************

	// instance variables
	private String description = "blank_description";
	private Category category;
	private double fundsIn;
	private double fundsOut;
	private boolean isExpense;
	private Date date;

	// constructor
	public Transaction(Date date, String description, double funds, String category) throws Exception {

		// always toLowerCase()
		if (description != null && description.length() != 0)
			this.description = description.trim().toLowerCase().replaceAll("\"", "");

		// funds in/out
		if (funds > 0) {
			this.fundsIn = funds;
			this.fundsOut = 0;
		} else {
			this.fundsIn = 0;
			this.fundsOut = funds * -1;
		}

		isExpense = funds < 0; // true if expense, false if income

		this.date = date; // date

		// resize transaction PFA
		if (trCount >= transactions.length - 1) {
			Transaction[] temp = new Transaction[transactions.length * 2];
			for (int i = 0; i < transactions.length; i++) {
				temp[i] = transactions[i];
			}
			transactions = temp;
		}

		// category
		if (category == null) {
			category = "other";
		}
		this.category = Category.get(category, isExpense);
		this.category.addDescription(this.description);
		for (Transaction tr : transactions) {
			if (tr != null && tr.equals(this)) {
				return;
			}
		}
		// ordered insertion for new element
		trInsert(trCount++, transactions, this);
	} // constructor

	// PFA deletion for transactions array
	public void delete(Transaction tr) {
		boolean found = false;
		for (int i = 0; i < transactions.length - 1; i++) {
			if (transactions[i] == null) {
				continue;
			}
			if (tr.equals(transactions[i])) {
				found = true;
			}
			if (found) {
				transactions[i] = transactions[i + 1];
			}
		}
		trCount--;
	}

	// getters

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public Category getCategory() {
		return category;
	}

	public double getFundsIn() {
		return fundsIn;
	}

	public double getFundsOut() {
		return fundsOut;
	}

	public boolean isExpense() {
		return isExpense;
	}

	// setters
	public void setCategory(Category category) {
		this.category = category;
	}

	// comparable by date
	public int compareTo(Transaction tr) {
		return getDate().compareTo(tr.getDate());
	}

	// equal if same description, date funds in/out
	public boolean equals(Transaction tr) {
		double threshold = 0.001d;
		return this.description.equals(tr.description) && this.date.equals(tr.date)
				&& Math.abs(this.fundsIn - tr.fundsIn) < threshold && Math.abs(this.fundsOut - tr.fundsOut) < threshold;
	}

	// toString method
	public String toString() {
		double funds;
		String catStr, descStr = description;
		// String dateStr = FORMAT.format(date);
		if (fundsIn > fundsOut)
			funds = fundsIn;
		else
			funds = fundsOut * -1;

		catStr = category.toString();
		if (description.length() > 24) {
			descStr = description.substring(0, 23) + "...";
		}

		return String.format("%s | %.2f | %s", catStr, funds, descStr);
	} // toString

}
