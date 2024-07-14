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
		while ((line = br.readLine().toLowerCase().trim()) != null) {
			if (line.length() < 5 || line.indexOf(':') == -1)
				continue;

			lineArr = line.split(":");
			switch (lineArr[0]) {
				case "theme":
					break;
				case "currency":
					break;
				case "csv order":
					break;
				case "income categories":
					break;
				case "expense categories":
					break;
				default:
					break;
			}
		}
	}

	public static void saveFile() {

	}

	public static void setTheme(String[] theme) {
		if (theme.length != 3)
			return;
	}

	public static void setCategories(String[] categories, boolean type) {
		if (categories.length < 2)
			return;

		for (String category : categories) {
			Category.get(category, type);
		}

	}

}
