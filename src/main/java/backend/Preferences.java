package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Preferences {
	private static final String FILE_PATH = Transaction.WORKING_DIR + File.separator + "preferences.txt";
	private static final boolean EXPENSE = true, INCOME = false;
	private static final int NUMBER_OF_PREFERENCES = 4;

	public static void initialize() {
		// ensure file exists
		File file = new File(FILE_PATH);
		file.getParentFile().mkdirs();

		// read file
		try {
			readFile();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static void saveFile() throws Exception {
		BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH));
		String[] lines = new String[NUMBER_OF_PREFERENCES];
		lines[0] = saveCSVString();
		lines[1] = saveCSVKeywords();
		lines[2] = saveCategories(INCOME);
		lines[3] = saveCategories(EXPENSE);
		for (String line : lines) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();

	}

	private static void readFile() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
		String line = "";
		String[] lineArr;
		while ((line = br.readLine()) != null) {
			line = line.toLowerCase().trim();
			if (line.length() < 5 || line.indexOf(':') == -1)
				continue;

			lineArr = line.split(":");
			switch (lineArr[0]) {
				case "theme":
					// TODO: implement theme
					break;
				case "currency":
					// TODO: implement currency
					break;
				case "csv order":
					setCSVOrder(lineArr);
					break;
				case "csv keywords":
					setCSVKeywords(lineArr);
					break;
				case "income categories":
					setCategories(lineArr, INCOME);
					break;
				case "expense categories":
					setCategories(lineArr, EXPENSE);
					break;
				default:
					break;
			}
		}
		br.close();
	}

	private static void setCSVOrder(String[] order) {
		if (order.length != 5)
			return;

		int[] orderArr = new int[4];
		try {
			for (int i = 0; i < orderArr.length; i++) {
				orderArr[i] = Integer.parseInt(order[i + 1]);
			}

			Transaction.setCSVOrder(orderArr);
		} catch (Exception e) {
		}

	}

	private static String saveCSVString() {
		String csvString = "csv order:";
		int[] csvOrder = Transaction.getCSVOrder();
		for (int i = 0; i < csvOrder.length; i++) {
			csvString += csvOrder[i];
			if (i < csvOrder.length - 1)
				csvString += ":";
		}
		return csvString;
	}

	private static void setCSVKeywords(String[] fileLine) {
		if (fileLine.length < 2)
			return;

		String[] keywords = new String[fileLine.length - 1];
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = fileLine[i + 1].toLowerCase().trim();
		}

		Transaction.setIgnoreList(keywords);
	}

	private static String saveCSVKeywords() {
		String csvString = "csv keywords:";
		String[] keywords = Transaction.getIgnoreList();
		for (int i = 0; i < keywords.length; i++) {
			csvString += keywords[i];
			if (i < keywords.length - 1)
				csvString += ":";
		}
		return csvString;
	}

	private static void setCategories(String[] categories, boolean type) {
		if (categories.length < 2)
			return;

		categories[0] = "other";
		Category.setCategories(categories, type);

	}

	private static String saveCategories(boolean type) {
		String csvString = type ? "expense categories:" : "income categories:";
		String[] categories = Category.getCategoryNames(type).toArray(new String[0]);
		for (int i = 0; i < categories.length; i++) {
			csvString += categories[i];
			if (i < categories.length - 1)
				csvString += ":";
		}
		return csvString;
	}

}
