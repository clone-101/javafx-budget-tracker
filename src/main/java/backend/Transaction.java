package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
	// instance variables
	private String description;
	private Category category = null;
	private double fundsIn;
	private double fundsOut;
	private Date date;
	// private String account;

	// static variables
	// pull current transaction from local csv
	public static Transaction[] transactions = new Transaction[1000];
	public static int trCount = 0;
	// date format: MM/dd/yyyy
	private static SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");

	// constructor
	public Transaction(String description, String category, double funds, String account,
			Date date)
			throws Exception {

		// always toLowerCase()
		this.description = description.trim().toLowerCase();

		// funds in/out
		if (funds > 0) {
			this.fundsIn = funds;
			this.fundsOut = 0;
		} else {
			this.fundsIn = 0;
			this.fundsOut = funds * -1;
		}
		// no use for account number
		// this.account = account;

		// date
		this.date = date;

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
		this.category = Category.get(category);
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
	public static void readFile(String filename, int desID, int fundInID, int fundOutID, int accID, int dateID)
			throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(filename));
		String tempLine = "";
		String[] lineArr;
		// br.readLine();

		while ((tempLine = br.readLine()) != null) {
			// excludes commas contained in quotes
			lineArr = tempLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

			Date date;
			double fundsIn, fundsOut;
			try {
				date = f.parse(lineArr[dateID]);
			} catch (Exception e) {
				continue;
			}
			try {
				fundsIn = Double.parseDouble(lineArr[fundInID]);
			} catch (Exception e) {
				fundsIn = 0;
			}
			try {
				fundsOut = Double.parseDouble(lineArr[fundOutID]);
			} catch (Exception e) {
				fundsOut = 0;
			}
			try {
				new Transaction(lineArr[desID], null, fundsIn - fundsOut, lineArr[accID], date);
			} catch (Exception e) {
			}

		}
		br.close();
	}

	// default ID order: 13240
	public static void writeFile() {
		// String filePath = "../../resources/application/content.csv";
		// if (System.getProperty("os.name").indexOf("Windows") != -1) {
		// filePath = "..\\..\\resources\\application\\content.csv";
		// }

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

	public static double getExpenses(Date start, Date end) {
		double sum = 0;
		for (int i = 0; i < transactions.length && transactions[i] != null; i++) {
			if (transactions[i].getDate().compareTo(start) > 0 && transactions[i].getDate().compareTo(end) < 0)
				sum += transactions[i].getFundsOut();
		}
		return sum;
	}
	// ************** UI stuff ***************

}
