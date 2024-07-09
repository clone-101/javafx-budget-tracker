package test;

public class Test {
	public static void main(String[] args) throws Exception {
		initializeCSV();
		// Category cat = new Category("food");
		// backend.Transaction.printAll();

		// SimpleDateFormat f = new SimpleDateFormat("MM/dd/yyyy");
		// Date end = new Date();
		// Date start = f.parse("06/02/2024");
		// double expenses = backend.Transaction.getExpenses(start, end);
		// System.out.println(expenses);

	}

	public static void initializeCSV() {
		String windowsPath = "src\\main\\resources\\application\\SIMPLII.csv";
		String path = "";
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			path = windowsPath;
		}
		try {
			backend.Transaction.readFile(path, 1, 3, 2, 4, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
