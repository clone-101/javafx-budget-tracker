package backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Preferences {
	private static final String FILE_PATH = Transaction.WORKING_DIR + File.separator + "preferences.txt";
	private static final boolean EXPENSE = true, INCOME = false;

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

	public static void readFile() throws Exception {
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

	public static void saveFile() {

	}

	public static void setCSVOrder(String[] order) {
		if (order.length < 5)
			return;

		int[] orderArr = new int[4];
		try {
			for (int i = 1; i < orderArr.length; i++) {
				orderArr[i] = Integer.parseInt(order[i]);
			}

			Transaction.setCSVOrder(orderArr);
		} catch (Exception e) {
		}

	}

	public static void setCSVKeywords(String[] fileLine) {
		if (fileLine.length < 2)
			return;

		String[] keywords = new String[fileLine.length - 1];
		for (int i = 0; i < keywords.length; i++) {
			keywords[i] = fileLine[i + 1].toLowerCase().trim();
		}

		Transaction.setCSVIgnoreKeywords(keywords);
	}

	public static void setCategories(String[] categories, boolean type) {
		if (categories.length < 2)
			return;

		categories[0] = "other";
		Category.setCategories(categories, type);

	}

}
