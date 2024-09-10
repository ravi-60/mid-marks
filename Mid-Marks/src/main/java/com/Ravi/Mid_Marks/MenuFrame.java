package com.Ravi.Mid_Marks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuFrame extends JFrame implements ActionListener {
    private JButton firstYearButton, secondYearButton, thirdYearButton, fourthYearButton;

    public MenuFrame() {
        // Setting up the frame
        setTitle("Menu Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Create and set background color
        getContentPane().setBackground(new Color(60, 63, 65));

        // Creating main year buttons
        firstYearButton = createYearButton("1st Year");
        secondYearButton = createYearButton("2nd Year");
        thirdYearButton = createYearButton("3rd Year");
        fourthYearButton = createYearButton("4th Year");

        // Setting up layout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Adding padding

        // Adding buttons to frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(firstYearButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(secondYearButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(thirdYearButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(fourthYearButton, gbc);
    }

    private JButton createYearButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(75, 110, 175));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 50));
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == firstYearButton) {
            openYearFrame1();
        } else if (e.getSource() == secondYearButton) {
            openYearFrame2();
        } else if (e.getSource() == thirdYearButton) {
            openYearFrame3();
        } else if (e.getSource() == fourthYearButton) {
            openYearFrame4();
        }
    }

    private void openYearFrame1() {
        YearFrame yearFrame1 = new YearFrame("First Year");
        yearFrame1.setVisible(true);
        this.dispose();
    }

    private void openYearFrame2() {
        YearFrame yearFrame2 = new YearFrame("Second Year");
        yearFrame2.setVisible(true);
        this.dispose();
    }

    private void openYearFrame3() {
        YearFrame yearFrame3 = new YearFrame("Third Year");
        yearFrame3.setVisible(true);
        this.dispose();
    }

    private void openYearFrame4() {
        YearFrame yearFrame4 = new YearFrame("Fourth Year");
        yearFrame4.setVisible(true);
        this.dispose();
    }
}
