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

    //Manager Monitor Runner Performance
    private int totalRating = 0;
    
    public void addRating(int rating) {
        this.totalRating += rating;
    }

    public double getAverageRating() {
        if (getTotalOrders() == 0) return 0; // Avoid division by zero
        return (double) totalRating / getTotalOrders();
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
    
    public boolean assignRunnerTask(String[] runnerData, String[] orderData) {
        String runnerEmail = runnerData[0];
        String runnerStatus = runnerData[2];
        
        if (!"available".equalsIgnoreCase(runnerStatus)) {
            return false; // Skip if the runner is not available
        }
        
        //if no runner, make cancel or if all runner reject, canceled

        // Display task details for the runner
        runnerViewTask viewTask = new runnerViewTask();
        viewTask.displayOrderForRunner(orderData);

        // Show the JFrame
        viewTask.setVisible(true);
        
        // Wait for user interaction (accept/decline)
        while (!viewTask.isDecisionMade()) {
            try {
                Thread.sleep(100); // Polling to check if the decision has been made
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Check decision after button click
        boolean taskAccepted = viewTask.isTaskAccepted();

        if (taskAccepted) {
            updateRunnerStatus(runnerEmail, "unavailable"); // Update runner status to unavailable
        }
        // Close the view after decision
        viewTask.dispose();

        return taskAccepted;
    }
    
    /**
    public void assignNextRunner(String orderId) {
        boolean runnerAssigned = false;
        String[] orderData = getOrderDeta(orderId); // Get order details

        try (BufferedReader runnerReader = new BufferedReader(new FileReader("resources/runner.txt"))) {
            String runnerLine;

            while ((runnerLine = runnerReader.readLine()) != null) {
                String[] runnerData = runnerLine.split(",");

                if (runnerData.length > 2 && "available".equalsIgnoreCase(runnerData[2])) {
                    // Display the task to the next available runner
                    return displayOrderForRunner();
                    runnerAssigned = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!runnerAssigned) {
            // If no runners are available or all decline, cancel the order
            new Order().updateStatus(Integer.parseInt(orderId), "Canceled", "runner");
                JOptionPane.showMessageDialog(null,"No runners accepted the task. Order canceled.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    **/

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