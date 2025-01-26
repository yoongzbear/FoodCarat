/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Yuna
 */
public class Runner extends User{
    private String runnerFile = "resources/runner.txt";
    private String cusOrderFile = "resources/customerOrder.txt";
    
    // Prompt and validate plate number
    public String promptAndValidatePlateNumber(Component parentComponent, String email) {
        String plateNumber = null;

        while (true) {
            plateNumber = JOptionPane.showInputDialog(
                parentComponent,
                "Enter your plate number (letters and numbers, max 15 characters):",
                "Plate Number",
                JOptionPane.PLAIN_MESSAGE
            );

            if (plateNumber == null) {
                JOptionPane.showMessageDialog(parentComponent, "Plate number is required to proceed.", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            plateNumber = plateNumber.trim();

            if (isValidPlateNumber(plateNumber)) {
                savePlateNumber(email, plateNumber);
                return plateNumber;
            } else {
                JOptionPane.showMessageDialog(parentComponent, "Invalid plate number. Ensure it contains letters, numbers, and is max 15 characters.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Save plate number
    public void savePlateNumber(String email, String plateNumber) {
        List<String> fileContent = new ArrayList<>();
        boolean emailFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(runnerFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] runnerData = line.split(",");
                if (runnerData.length > 0 && runnerData[0].equalsIgnoreCase(email)) {
                    runnerData = new String[]{runnerData[0], plateNumber.toUpperCase(), "unavailable", "0"};
                    
                    emailFound = true;
                }
                fileContent.add(String.join(",", runnerData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (emailFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(runnerFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
    }

    // Validate plate number
    private boolean isValidPlateNumber(String plateNumber) {
        if (plateNumber.length() > 15) {
            return false;
        }

        boolean containsLetter = false;
        boolean containsNumber = false;

        for (char c : plateNumber.toCharArray()) {
            if (Character.isLetter(c)) {
                containsLetter = true;
            } else if (Character.isDigit(c)) {
                containsNumber = true;
            }
        }

        return containsLetter && containsNumber;
    }

    public String[] getRunnerDetails(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(runnerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] runnerData = line.split(",");
                if (runnerData.length >= 1 && runnerData[0].equalsIgnoreCase(email)) {
                    return runnerData;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to update the runner's availability status
    public void updateRunnerStatus(String runnerEmail, String status) {
        List<String> fileContent = new ArrayList<>();
        boolean runnerFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(runnerFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] runnerData = line.split(",");
                if (runnerData[0].equals(runnerEmail)) {
                    runnerData[2] = status;  // Update runner's status (available or unavailable)
                    runnerFound = true;
                }
                fileContent.add(String.join(",", runnerData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (runnerFound) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(runnerFile))) {
                for (String updatedLine : fileContent) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}