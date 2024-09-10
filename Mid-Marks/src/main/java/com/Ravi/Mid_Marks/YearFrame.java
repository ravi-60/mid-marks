package com.Ravi.Mid_Marks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class YearFrame extends JFrame implements ActionListener {
	private String year;
	private String savedFilePath;
	private JButton mid1Button, mid2Button, backButton, exportButton;

	public YearFrame(String year) {
		this.year = year;

		// Setting up the frame
		setTitle(year + " - Menu");
		setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(new Color(60, 63, 65));
		setLayout(new BorderLayout());

		// Create the back button with an icon
		ImageIcon backIcon = new ImageIcon("resources/left.png"); // Ensure this path is correct
		backButton = new JButton(backIcon);
		backButton.setBackground(new Color(75, 110, 175));
		backButton.setPreferredSize(new Dimension(50, 50));
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new MenuFrame().setVisible(true);
					}
				});
				YearFrame.this.dispose();
			}
		});

		// Create a top panel and add the back button to it
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBackground(new Color(60, 63, 65)); // Match frame background color
		topPanel.add(backButton, BorderLayout.WEST);
		add(topPanel, BorderLayout.NORTH);

		// Create a main content panel for mid buttons
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(new Color(60, 63, 65)); // Match frame background color

		// Creating mid buttons
		mid1Button = createMidButton("Mid 1");
		mid2Button = createMidButton("Mid 2");

		// Setting up layout constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(20, 20, 20, 20); // Adding padding

		// Adding buttons to main panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		mainPanel.add(mid1Button, gbc);

		gbc.gridx = 1;
		mainPanel.add(mid2Button, gbc);

		// Creating export button
		exportButton = createExportButton("Export");

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		mainPanel.add(exportButton, gbc);

		// Adding main panel to the center of the frame
		add(mainPanel, BorderLayout.CENTER);
	}

	private JButton createMidButton(String text) {
		JButton button = new JButton(text);
		button.setBackground(new Color(75, 110, 175));
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(200, 50));
		button.addActionListener(this);
		return button;
	}

	private JButton createExportButton(String text) {
		JButton button = new JButton(text);
		button.setBackground(new Color(75, 110, 175));
		button.setForeground(Color.WHITE);
		button.setPreferredSize(new Dimension(200, 50));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportData();
				finalPieChart();
			}
		});
		return button;
	}

	private void exportData() {
	    // Update these paths according to your file locations
	    String path1 = "resources/3rd year/3rd year Mid 1.csv"; // Path to your first CSV file
	    String path2 = "resources/3rd year/3rd year Mid 2.csv"; // Path to your second CSV file

	    try {
	        List<String[]> data1 = readCSV(path1);
	        List<String[]> data2 = readCSV(path2);

	        Workbook workbook = new XSSFWorkbook();
	        Sheet sheet = workbook.createSheet("Combined Data");

	        // Write headers
	        Row headerRow = sheet.createRow(0);
	        String[] headers1 = data1.get(0);
	        String[] headers2 = data2.get(0);

	        int colIndex = 0;
	        // Write common headers (S.No, Name, Roll Number)
	        for (int j = 0; j < headers1.length; j++) {
	            if (headers1[j].equalsIgnoreCase("S.No") || headers1[j].equalsIgnoreCase("Name")
	                    || headers1[j].equalsIgnoreCase("Roll Number")) {
	                headerRow.createCell(colIndex++).setCellValue(headers1[j]);
	            }
	        }

	        // Write remaining headers from the first CSV
	        for (int j = 0; j < headers1.length; j++) {
	            if (!headers1[j].equalsIgnoreCase("S.No") && !headers1[j].equalsIgnoreCase("Name")
	                    && !headers1[j].equalsIgnoreCase("Roll Number")) {
	                headerRow.createCell(colIndex++).setCellValue(headers1[j]);
	            }
	        }

	        // Write relevant headers from the second CSV (starting from the 4th column)
	        for (int j = 3; j < headers2.length; j++) {
	            headerRow.createCell(colIndex++).setCellValue(headers2[j]);
	        }

	        // Add Total Marks header
	        headerRow.createCell(colIndex).setCellValue("Total Marks");

	        // Write data rows
	        for (int i = 1; i < data1.size(); i++) {
	            Row row = sheet.createRow(i);
	            String[] rowData1 = data1.get(i);
	            String[] rowData2 = data2.get(i);

	            int cellIndex = 0;
	            // Write common data (S.No, Name, Roll Number)
	            row.createCell(cellIndex++).setCellValue(Integer.parseInt(rowData1[0])); // S.No
	            row.createCell(cellIndex++).setCellValue(rowData1[1]); // Name
	            row.createCell(cellIndex++).setCellValue(rowData1[2]); // Roll Number

	            // Write remaining data from the first CSV
	            for (int j = 3; j < rowData1.length; j++) {
	                row.createCell(cellIndex++).setCellValue(Integer.parseInt(rowData1[j]));
	            }

	            // Write relevant data from the second CSV (starting from the 4th column)
	            for (int j = 3; j < rowData2.length; j++) {
	                row.createCell(cellIndex++).setCellValue(Integer.parseInt(rowData2[j]));
	            }

	            // Calculate and write Total Marks
	            double totalMid1 = Double.parseDouble(rowData1[rowData1.length - 1]); // Assuming last column for Total mid 1
	            double totalMid2 = Double.parseDouble(rowData2[rowData2.length - 1]); // Assuming last column for Total mid 2
	            double totalMarks = Math.ceil((totalMid1 > totalMid2) ? (0.8 * totalMid1 + 0.2 * totalMid2)
	                    : (0.8 * totalMid2 + 0.2 * totalMid1));
	            row.createCell(cellIndex).setCellValue((int) totalMarks);
	        }

	        // Create a file chooser
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setDialogTitle("Specify a file to save");   

	        // Set default filename
	        fileChooser.setSelectedFile(new File("Total Mid Marks.xlsx"));

	        int userSelection = fileChooser.showSaveDialog(this);

	        if (userSelection == JFileChooser.APPROVE_OPTION) {
	            File fileToSave = fileChooser.getSelectedFile();
	            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
	                workbook.write(fileOut);
	                savedFilePath = fileToSave.getAbsolutePath();
	            }

	            workbook.close();

	            // Show success message
	            JOptionPane.showMessageDialog(this, "Data exported successfully!", "Success",
	                    JOptionPane.INFORMATION_MESSAGE);
	        }

	    } catch (Exception ex) {
	    	JOptionPane.showMessageDialog(this, "Data export failed", "Not successful",
                    JOptionPane.INFORMATION_MESSAGE);
	        ex.printStackTrace();
	    }
	}
	
	private void finalPieChart() {
	    if (savedFilePath == null) {
	        System.err.println("No file path found. Please export data first.");
	        return;
	    }

	    DefaultPieDataset dataset = new DefaultPieDataset();

	    int lessThan16Count = 0;
	    int between16And25Count = 0;
	    int greaterThan25Count = 0;

	    try (FileInputStream fis = new FileInputStream(savedFilePath);
	         Workbook workbook = new XSSFWorkbook(fis)) {

	        Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
	        int totalMarksColumnIndex = -1;

	        // Find the "Total Marks" column index
	        Row headerRow = sheet.getRow(0);
	        for (Cell cell : headerRow) {
	            if ("Total Marks".equalsIgnoreCase(cell.getStringCellValue())) {
	                totalMarksColumnIndex = cell.getColumnIndex();
	                break;
	            }
	        }

	        if (totalMarksColumnIndex == -1) {
	            System.err.println("Total Marks column not found");
	            return;
	        }

	        // Iterate through the rows to categorize students based on "Total Marks"
	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            if (row == null) continue; // Skip empty rows

	            Cell totalMarksCell = row.getCell(totalMarksColumnIndex);
	            if (totalMarksCell == null || totalMarksCell.getCellType() != CellType.NUMERIC) {
	                continue; // Skip if the cell is empty or not numeric
	            }

	            int totalMarks = (int) totalMarksCell.getNumericCellValue();

	            // Categorize based on totalMarks score
	            if (totalMarks < 16) {
	                lessThan16Count++;
	            } else if (totalMarks >= 16 && totalMarks <= 25) {
	                between16And25Count++;
	            } else if (totalMarks > 25) {
	                greaterThan25Count++;
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
	        JFreeChart chart = ChartFactory.createPieChart("Student Performance (Total Marks)", dataset, true, true, false);

	        // Define colors for the categories
	        PiePlot plot = (PiePlot) chart.getPlot();
	        plot.setSectionPaint("Less than 16", Color.RED); // Color for less than 16
	        plot.setSectionPaint("16 to 25", Color.BLUE); // Color for 16 to 25
	        plot.setSectionPaint("Greater than 25", Color.GREEN); // Color for greater than 25

	        // Display chart in a new frame on the EDT
	        SwingUtilities.invokeLater(() -> {
	            ChartPanel chartPanel = new ChartPanel(chart);
	            JFrame chartFrame = new JFrame();
	            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	            chartFrame.setLayout(new BorderLayout());
	            chartFrame.add(chartPanel, BorderLayout.CENTER);
	            chartFrame.pack();
	            chartFrame.setVisible(true);
	        });

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}


	private List<String[]> readCSV(String filePath) {
		List<String[]> data = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				data.add(values);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return data;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		if (source == mid1Button) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new MainFrame41().setVisible(true);
				}
			});
			//this.dispose();

		} else if (source == mid2Button) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new MainFrame42().setVisible(true);
				}
			});
			//this.dispose();
		}
	}
}
