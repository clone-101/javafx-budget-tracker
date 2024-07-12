package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.opencsv.CSVWriter;

public class Transaction {
	// instance variables
	private String description;
	private Category category = null;
	private double fundsIn;
	private double fundsOut;
	private boolean isExpense;
	private Date date;

	// pull current transaction from local csv
	public static Transaction[] transactions = new Transaction[1000];
	public static int trCount = 0;
	// date format: MM/dd/yyyy
	private static SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");

	// working directory
	public static final String WORKING_DIR = System.getProperty("user.dir");
	public static final String CSV = "content.csv";

	// constructor
	public Transaction(Date date, String description, double funds, String category) throws Exception {

		// always toLowerCase()
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
		// ordered insertion for new element
		trInsert(trCount++, transactions, this);

		// category
		if (category == null) {
			category = "other";
		}
		this.category = Category.get(category, isExpense);
		this.category.add(this);
	} // constructor

	// PFA deletion for transactions array
	public void delete(Transaction tr) {
		boolean found = false;
		for (int i = 0; i < transactions.length - 1; i++) {
			if (tr.equals(transactions[i])) {
				found = true;
			}
			if (found) {
				transactions[i] = transactions[i + 1];
			}
		}
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
		String catStr;
		if (fundsIn > fundsOut)
			funds = fundsIn;
		else
			funds = fundsOut * -1;
		if (category == null)
			catStr = "other";
		else
			catStr = category.toString();

		return String.format("%s | %.2f | %s", catStr, funds, f.format(date));
	} // toString

	// static methods

	// print all
	// public static void printAll() {
	// for (Transaction tr : transactions) {
	// if (tr != null)
	// System.out.println(tr);
	// }
	// }

	// returns array of all transactions toString
	public static String[] TransactionStrArr() {
		String[] arr = new String[trCount];
		int i = 0;
		for (Transaction tr : transactions) {
			if (tr != null) {
				arr[i++] = tr.toString();
			}
		}
		return arr;
	}

	// file IO

	// default ID order: 13240
	public static void readFile(String filename, int dateID, int desID, int fundOutID, int fundInID)
			throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(filename));
		String tempLine = "";
		String[] lineArr;

		while ((tempLine = br.readLine()) != null) {
			// excludes commas contained in quotes
			lineArr = tempLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

			Date date;
			double fundsIn, fundsOut;
			try {
				date = f.parse(lineArr[dateID].replaceAll("\"", ""));
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

			try {
				// max category length is 15 characters (including quotes)
				if (lineArr[4].length() < 16) {
					String category = lineArr[4].replaceAll("\"", "");
					new Transaction(date, lineArr[desID], fundsIn - fundsOut, category);
				} else
					new Transaction(date, lineArr[desID], fundsIn - fundsOut, null);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		br.close();
	}

	public static void saveToFile() {
		String filePath = WORKING_DIR + File.separator + CSV;
		System.out.println(filePath);

		// Ensure the directory exists
		File file = new File(filePath);
		file.getParentFile().mkdirs();

		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			ArrayList<String[]> data = new ArrayList<>();
			for (Transaction tr : transactions) {
				if (tr != null) {
					String[] temp = new String[5];
					temp[1] = tr.getDescription();
					temp[4] = tr.getCategory().toString();
					temp[3] = Double.toString(tr.getFundsIn());
					temp[2] = Double.toString(tr.getFundsOut());
					temp[0] = f.format(tr.getDate());
					data.add(temp);
				}
			}
			writer.writeAll(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initializeCSV() {
		String filePath = WORKING_DIR + File.separator + CSV;

		File file = new File(filePath);
		file.getParentFile().mkdirs();

		try {
			readFile(filePath, 0, 1, 2, 3);
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

	// returns income/expenses from start date to end date
	public static double getIncome(Date start, Date end) {
		double sum = 0;
		for (int i = 0; i < transactions.length && transactions[i] != null; i++) {
			if (transactions[i].getDate().compareTo(start) > 0 && transactions[i].getDate().compareTo(end) < 0)
				sum += transactions[i].getFundsIn();
		}
		return sum;
	}

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

	public static double getExpenses(Date start, Date end) {
		double sum = 0;
		for (int i = 0; i < transactions.length && transactions[i] != null; i++) {
			if (transactions[i].getDate().compareTo(start) > 0 && transactions[i].getDate().compareTo(end) < 0)
				sum += transactions[i].getFundsOut();
		}
		return sum;
	}

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
	// ************** UI stuff ***************

}
