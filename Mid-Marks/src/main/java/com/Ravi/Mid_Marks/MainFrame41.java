package com.Ravi.Mid_Marks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class MainFrame41 extends JFrame implements ActionListener {
	private DefaultTableModel model;
	private JTable table;
	private ArrayList<Student> students;
	private Point startPoint, endPoint;
	private JPopupMenu contextMenu;

	@SuppressWarnings("serial")
	public MainFrame41() {
		setTitle("Student Management");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		model = new DefaultTableModel(new String[] { "S.No", "Roll No", "Name", "1(a)", "1(b)", "2(a)", "2(b)", "3(a)",
				"Assignment", "Quiz", "Total Mid 1" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// Make the "Total Mid 1" column (index 10) non-editable
				return column != 10;
			}
		};
		table = new JTable(model) {
			@Override
			public boolean editCellAt(int row, int column, java.util.EventObject e) {
				boolean result = super.editCellAt(row, column, e);
				final JTextComponent editor = (JTextComponent) getEditorComponent();
				if (editor != null) {
					editor.selectAll();
				}
				return result;
			}
		};
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(true);
		add(new JScrollPane(table), BorderLayout.CENTER);

		// Set row height and column width
		table.setRowHeight(30); // Increase the row height
		for (int i = 0; i < table.getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth(100); // Increase the preferred column width
		}

		JPanel buttonPanel = new JPanel();
		JButton addButton = new JButton("Add Student");
		addButton.addActionListener(this);
		JButton removeButton = new JButton("Remove Student");
		removeButton.addActionListener(this);
		JButton exportButton = new JButton("Export to Excel");
		exportButton.addActionListener(this);
		JButton importButton = new JButton("Import from Excel");
		importButton.addActionListener(this);
		JButton moveUpButton = new JButton("Move Up");
		moveUpButton.addActionListener(this);
		JButton moveDownButton = new JButton("Move Down");
		moveDownButton.addActionListener(this);
		JButton importQuizButton = new JButton("Import Quiz Marks");
		importQuizButton.addActionListener(this);
		buttonPanel.add(importQuizButton);

		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(importButton);
		buttonPanel.add(moveUpButton);
		buttonPanel.add(moveDownButton);

		add(buttonPanel, BorderLayout.SOUTH);

		students = new ArrayList<>();

		// Add mouse listeners for context menu
		addTableMouseListeners();

		// Create context menu
		createContextMenu();

		addKeyListenerForTable();

		importDataFromCSV();

		calculateAndUpdateTotalMid1();

		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					int row = e.getFirstRow();
					int column = e.getColumn();
					if (column >= 3 && column <= 9) { // Check if edited column is within range 1(a) to Quiz
						calculateAndUpdateTotalMid1ForRow(row);
					}
				}
			}
		});

		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("Add Student")) {
			addStudent();
		} else if (command.equals("Remove Student")) {
			removeStudent();
		} else if (command.equals("Export to Excel")) {
			exportToExcel();
			showPieChart();
			updateCSVFile();
		} else if (command.equals("Import from Excel")) {
			importFromExcel();
		} else if (command.equals("Move Up")) {
			moveRowUp();
		} else if (command.equals("Move Down")) {
			moveRowDown();
		} else if (command.equals("Import Quiz Marks")) {
			importQuizMarks();
		}
	}

	private void calculateAndUpdateTotalMid1() {
		for (int row = 0; row < model.getRowCount(); row++) {
			calculateAndUpdateTotalMid1ForRow(row);
		}
	}

	// Method to calculate and update Total Mid 1 for a specific row
	private void calculateAndUpdateTotalMid1ForRow(int row) {
		int sum = 0;
		for (int col = 3; col <= 9; col++) { // Sum columns 1(a) to Quiz (columns 3 to 9)
			Object value = model.getValueAt(row, col);
			if (value instanceof Number) {
				sum += ((Number) value).intValue();
			} else if (value instanceof String) {
				try {
					sum += Integer.parseInt((String) value);
				} catch (NumberFormatException ignored) {
					// Handle parsing error if needed
				}
			}
		}
		// Update Total Mid 1 column in the model
		model.setValueAt(sum, row, 10);
	}

	private void addStudent() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow == -1) {
			// No row selected, add to the end
			int insertRow = model.getRowCount();
			int sNo = (insertRow == 0) ? 1 : (int) model.getValueAt(insertRow - 1, 0) + 1;
			model.addRow(new Object[] { sNo, "", "", 0, 0, 0, 0, 0, 0, 0, 0 });
			students.add(new Student(sNo, "", "", 0, 0, 0, 0, 0, 0, 0, 0));
		} else {
			// Insert at the selected row
			int sNo = (int) model.getValueAt(selectedRow, 0);
			model.insertRow(selectedRow, new Object[] { sNo, "", "", 0, 0, 0, 0, 0, 0, 0, 0 });
			students.add(selectedRow, new Student(sNo, "", "", 0, 0, 0, 0, 0, 0, 0, 0));

			// Update the sNo for all subsequent rows
			for (int i = selectedRow + 1; i < model.getRowCount(); i++) {
				int newSNo = (int) model.getValueAt(i - 1, 0) + 1;
				model.setValueAt(newSNo, i, 0);
				students.get(i).setsNo(newSNo);
			}
		}
	}

	private void removeStudent() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			// Remove the selected row
			model.removeRow(selectedRow);
			students.remove(selectedRow);

			// Update the sNo for all subsequent rows
			for (int i = selectedRow; i < model.getRowCount(); i++) {
				int newSNo = i + 1; // Serial numbers are 1-based index
				model.setValueAt(newSNo, i, 0);
				students.get(i).setsNo(newSNo);
			}
		}
	}

	private void exportToExcel() {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Students");

		// Create header row
		Row headerRow = sheet.createRow(0);
		for (int i = 0; i < model.getColumnCount(); i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(model.getColumnName(i));
		}

		// Populate data rows
		for (int i = 0; i < model.getRowCount(); i++) {
			Row row = sheet.createRow(i + 1);
			for (int j = 0; j < model.getColumnCount(); j++) {
				Cell cell = row.createCell(j);
				Object value = model.getValueAt(i, j);

				// Check if the column should be an integer
				if (j == 0) { // S.No
					try {
						cell.setCellValue(Integer.parseInt(value.toString()));
					} catch (NumberFormatException e) {
						cell.setCellValue(value.toString());
					}
				} else if (j >= 3 && j <= 10) { // 1(a), 1(b), 2(a), 2(b), 3, Assignment, Quiz, Total Mid 1
					if (value instanceof Integer) {
						cell.setCellValue((Integer) value);
					} else {
						try {
							cell.setCellValue(Integer.parseInt(value.toString()));
						} catch (NumberFormatException e) {
							cell.setCellValue(value.toString());
						}
					}
				} else { // Name and Roll No
					cell.setCellValue(value.toString());
				}
			}
		}

		// Show file chooser to save the file
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File("4_1 mid 1.xlsx"));
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try (FileOutputStream fileOut = new FileOutputStream(file)) {
				workbook.write(fileOut);
				JOptionPane.showMessageDialog(this, "Data exported to Excel successfully.");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error exporting data to Excel: " + ex.getMessage());
			}
		}
	}

	private void importFromExcel() {
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			try (FileInputStream fis = new FileInputStream(fileChooser.getSelectedFile())) {
				Workbook workbook = new XSSFWorkbook(fis);
				Sheet sheet = workbook.getSheetAt(0);

				students.clear();
				model.setRowCount(0);

				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					Row row = sheet.getRow(i);
					int sNo = (int) row.getCell(0).getNumericCellValue();
					String rollNo = row.getCell(1).getStringCellValue();
					String name = row.getCell(2).getStringCellValue();
					int q1a = (int) row.getCell(3).getNumericCellValue();
					int q1b = (int) row.getCell(4).getNumericCellValue();
					int q2a = (int) row.getCell(5).getNumericCellValue();
					int q2b = (int) row.getCell(6).getNumericCellValue();
					int q3 = (int) row.getCell(7).getNumericCellValue();
					int assignment = (int) row.getCell(8).getNumericCellValue();
					int quiz = (int) row.getCell(9).getNumericCellValue();
					int totalMid1 = (int) row.getCell(10).getNumericCellValue();

					students.add(new Student(sNo, rollNo, name, q1a, q1b, q2a, q2b, q3, assignment, quiz, totalMid1));
					model.addRow(
							new Object[] { sNo, rollNo, name, q1a, q1b, q2a, q2b, q3, assignment, quiz, totalMid1 });
				}

				JOptionPane.showMessageDialog(this, "Data imported successfully!");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error reading from file: " + ex.getMessage());
			}
		}
	}

	private void moveRowUp() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow > 0) {
			model.moveRow(selectedRow, selectedRow, selectedRow - 1);
			table.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
		}
	}

	private void moveRowDown() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < table.getRowCount() - 1) {
			model.moveRow(selectedRow, selectedRow, selectedRow + 1);
			table.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
		}
	}

	private void addTableMouseListeners() {
		table.addMouseListener(new MouseInputAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					contextMenu.show(table, e.getX(), e.getY());
				}
			}
		});
	}

	private void createContextMenu() {
		contextMenu = new JPopupMenu();

		JMenuItem autoFillItem = new JMenuItem("Auto Fill");
		autoFillItem.addActionListener(e -> autoFill());
		contextMenu.add(autoFillItem);

		JMenuItem copySeriesItem = new JMenuItem("Copy Series");
		copySeriesItem.addActionListener(e -> copySeries());
		contextMenu.add(copySeriesItem);
	}

	private void autoFill() {
		int[] selectedRows = table.getSelectedRows();
		int[] selectedCols = table.getSelectedColumns();

		if (selectedRows.length > 0 && selectedCols.length == 1) {
			int startRow = selectedRows[0];
			int endRow = selectedRows[selectedRows.length - 1];
			int col = selectedCols[0];

			Object startValue = model.getValueAt(startRow, col);
			if (startValue != null) {
				try {
					int startInt = Integer.parseInt(startValue.toString());
					for (int i = startRow + 1; i <= endRow; i++) {
						startInt++;
						model.setValueAt(startInt, i, col);
					}
				} catch (NumberFormatException e) {
					// Handle non-integer values gracefully
					System.out.println("Non-integer value encountered");
				}
			}
		}
	}

	private void copySeries() {
		int[] selectedRows = table.getSelectedRows();
		int[] selectedCols = table.getSelectedColumns();

		if (selectedRows.length > 0 && selectedCols.length == 1) {
			int startRow = selectedRows[0];
			int endRow = selectedRows[selectedRows.length - 1];
			int col = selectedCols[0];

			Object startValue = model.getValueAt(startRow, col);
			for (int i = startRow + 1; i <= endRow; i++) {
				model.setValueAt(startValue, i, col);
			}
		}
	}

	private void importDataFromCSV() {
		String csvFile = "resources/3rd year/3rd year Mid 1.csv"; // Update this with your CSV file path
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			String line;
			int sNo = 1; // Starting serial number

			// Skip header line if exists
			br.readLine();

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");

				// Parse CSV fields
				int serialNo = sNo++;
				String rollNo = data[1];
				String name = data[2];
				int q1a = Integer.parseInt(data[3]);
				int q1b = Integer.parseInt(data[4]);
				int q2a = Integer.parseInt(data[5]);
				int q2b = Integer.parseInt(data[6]);
				int q3 = Integer.parseInt(data[7]);
				int assignment = Integer.parseInt(data[8]);
				int quiz = Integer.parseInt(data[9]);
				int totalMid1 = Integer.parseInt(data[10]);

				// Add row to table model
				model.addRow(
						new Object[] { serialNo, rollNo, name, q1a, q1b, q2a, q2b, q3, assignment, quiz, totalMid1 });

				// Add student to ArrayList
				students.add(new Student(serialNo, rollNo, name, q1a, q1b, q2a, q2b, q3, assignment, quiz, totalMid1));
			}

			JOptionPane.showMessageDialog(this, "CSV data imported successfully.");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error reading CSV file: " + e.getMessage());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Error parsing CSV data: " + e.getMessage());
		}
	}

	private void updateCSVFile() {
		String csvFile = "resources/3rd year/3rd year Mid 1.csv"; // Update this with your CSV file path
		try (FileOutputStream fos = new FileOutputStream(csvFile)) {
			StringBuilder csvContent = new StringBuilder();

			// Append header
			for (int i = 0; i < model.getColumnCount(); i++) {
				csvContent.append(model.getColumnName(i));
				if (i < model.getColumnCount() - 1) {
					csvContent.append(",");
				} else {
					csvContent.append("\n");
				}
			}

			// Append data rows
			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 0; j < model.getColumnCount(); j++) {
					Object value = model.getValueAt(i, j);
					csvContent.append(value.toString());
					if (j < model.getColumnCount() - 1) {
						csvContent.append(",");
					} else {
						csvContent.append("\n");
					}
				}
			}

			fos.write(csvContent.toString().getBytes());
			fos.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error updating CSV file: " + e.getMessage());
		}
	}

	private void showPieChart() {
		DefaultPieDataset dataset = new DefaultPieDataset();

		int lessThan16Count = 0;
		int between16And25Count = 0;
		int greaterThan25Count = 0;

		// Iterate through the table model to categorize students
		for (int i = 0; i < model.getRowCount(); i++) {
			String studentName = (String) model.getValueAt(i, 2); // Assuming name is in column 2

			// Retrieve totalMid1 and convert it to Integer if possible
			Object totalMid1Obj = model.getValueAt(i, 10); // Assuming totalMid1 is in column 10
			if (totalMid1Obj instanceof Integer) {
				int totalMid1 = (int) totalMid1Obj;

				// Categorize based on totalMid1 score
				if (totalMid1 < 16) {
					lessThan16Count++;
				} else if (totalMid1 >= 16 && totalMid1 <= 25) {
					between16And25Count++;
				} else if (totalMid1 > 25) {
					greaterThan25Count++;
				}
			} else if (totalMid1Obj instanceof String) {
				try {
					int totalMid1 = Integer.parseInt((String) totalMid1Obj);

					// Categorize based on totalMid1 score
					if (totalMid1 < 16) {
						lessThan16Count++;
					} else if (totalMid1 >= 16 && totalMid1 <= 25) {
						between16And25Count++;
					} else if (totalMid1 > 25) {
						greaterThan25Count++;
					}
				} catch (NumberFormatException e) {
					// Handle the case where the value cannot be parsed as an integer
					System.err.println("Error parsing totalMid1 for student: " + studentName);
				}
			}
		}

		// Add categorized counts to dataset
		if (lessThan16Count > 0) {
			dataset.setValue("Less than 16", lessThan16Count);
		}
		if (between16And25Count > 0) {
			dataset.setValue("16 to 25", between16And25Count);
		}
		if (greaterThan25Count > 0) {
			dataset.setValue("Greater than 25", greaterThan25Count);
		}

		// Create pie chart
		JFreeChart chart = ChartFactory.createPieChart("Student Performance (Total Mid 1)", dataset, true, true, false);

		// Define colors for the categories
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("Less than 16", Color.RED); // Color for less than 16
		plot.setSectionPaint("16 to 25", Color.BLUE); // Color for 16 to 25
		plot.setSectionPaint("Greater than 25", Color.GREEN); // Color for greater than 25

		// Display chart in a new frame
		ChartPanel chartPanel = new ChartPanel(chart);
		JFrame chartFrame = new JFrame();
		chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chartFrame.setLayout(new BorderLayout());
		chartFrame.add(chartPanel, BorderLayout.CENTER);
		chartFrame.pack();
		chartFrame.setVisible(true);
	}

	private void importQuizMarks() {
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try (FileInputStream fis = new FileInputStream(file)) {
				Workbook workbook = new XSSFWorkbook(fis);
				Sheet sheet = workbook.getSheetAt(0);

				ArrayList<String> unmatchedRollNumbers = new ArrayList<>();
				boolean foundMatch = false;

				// Iterate through the rows of the Excel sheet
				for (Row row : sheet) {
					if (row.getRowNum() == 0)
						continue; // Skip header row

					Cell rollNoCell = row.getCell(0);
					Cell marksCell = row.getCell(1);
					String rollNo = rollNoCell.getStringCellValue();
					int marks = (int) marksCell.getNumericCellValue();

					foundMatch = false;

					// Find the corresponding row in the JTable and update the quiz marks
					for (int i = 0; i < table.getRowCount(); i++) {
						String tableRollNo = (String) model.getValueAt(i, 1);
						if (tableRollNo.equals(rollNo)) {
							model.setValueAt(marks, i, 9); // Assuming quiz column is index 9
							foundMatch = true;
							break;
						}
					}

					if (!foundMatch) {
						unmatchedRollNumbers.add(rollNo);
					}
				}

				if (!unmatchedRollNumbers.isEmpty()) {
					JOptionPane.showMessageDialog(this,
							"The following roll numbers were not found: " + String.join(", ", unmatchedRollNumbers),
							"Unmatched Roll Numbers", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this, "Quiz marks imported successfully.");
				}

			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error reading from file: " + e.getMessage());
			}
		}
	}

	private void addKeyListenerForTable() {
		table.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent e) {
				if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE
						|| e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
					int[] selectedRows = table.getSelectedRows();
					int[] selectedColumns = table.getSelectedColumns();

					for (int row : selectedRows) {
						for (int column : selectedColumns) {
							model.setValueAt("0", row, column);
						}
					}
				}
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MainFrame41().setVisible(true));
	}
}
