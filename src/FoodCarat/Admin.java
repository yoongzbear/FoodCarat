/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class Admin extends User {

    public Admin() {
        this.email = "";
    }

    //Registration
    public static boolean isEmailRegistered(String email, String filename) { //validate email is registered for Registration
        File file = new File(filename);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(email)) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String registerUser(String email, String username, String role) throws IOException {
        // Generate sample password
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder samplePassword = new StringBuilder(email.split("@")[0]);
        for (int i = 0; i < 3; i++) {
            samplePassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        String password = samplePassword.toString();
        String roleFileName = "resources/" + role + ".txt";

        // Write to user.txt
        try (FileWriter userWriter = new FileWriter(userFile, true)) {
            userWriter.write(email + "," + username + "," + samplePassword + "," + role + ",,\n");
        }

        // Write to role-specific file
        try (FileWriter roleWriter = new FileWriter(roleFileName, true)) {
            if ("customer".equalsIgnoreCase(role)) {
                roleWriter.write(email + ",,0.0,0\n"); //credit amount will be 0.0 after the registration and the point will be 0
            } else if ("vendor".equalsIgnoreCase(role)) {
                roleWriter.write(email + ",,,[],0.0\n");
            } else {
                roleWriter.write(email + ",,\n");
            }
        }

        return password; // Return the generated password
    }

    //Update user info
    public void searchUser(String searchEmail, String selectedRole, adminUpdateUser updateUserForm) { //search for update user info
        // Check the login status, role match, and email match
        String result = checkFirstLogin(searchEmail, selectedRole, userFile);

        // Handle based on the result of checkFirstLogin
        if (result.equals("notFound")) {
            JOptionPane.showMessageDialog(null, "Email not found. Please check the entered email.");
        } else if (result.equals("roleMismatch")) {
            JOptionPane.showMessageDialog(null, "The role does not match the selected role.");
        } else if (result.equals("notCompleted")) {
            JOptionPane.showMessageDialog(null, "User has not completed the first login. Admin can't perform the update for the user.");
        } else if (result.equals("notExisting")) {
            JOptionPane.showMessageDialog(null, "User account does not exist. Please try again.");
        } else {
            //get the customer address by remove the [] and change ; to ,
            if ("customer".equals(selectedRole)) {
                Customer customer = new Customer(searchEmail);
                String customerAddress = customer.getCustomerAddress(searchEmail);
                result = customerAddress;
            }
            // If the user has completed the first login, update the user data
            updateUserForm.setUserData(getEmail(), getName(), getBirth(), getContactNumber(), result);
        }
    }

    public boolean updateFile(String fileName, String email, String[] updatedFields, int[] updateIndices) {
        List<String> fileContent = new ArrayList<>();
        boolean recordUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equalsIgnoreCase(email)) {
                    // Update specified fields based on indices
                    for (int i = 0; i < updateIndices.length; i++) {
                        data[updateIndices[i]] = updatedFields[i];
                    }

                    // Reconstruct the updated line
                    line = String.join(",", data);
                    recordUpdated = true;
                }

                fileContent.add(line);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            return false;
        }

        if (!recordUpdated) {
            JOptionPane.showMessageDialog(null, "No matching record found in " + fileName + "!");
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : fileContent) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error writing to file: " + ex.getMessage());
            return false;
        }

        return true;
    }

    // Remain the email and name at user.txt but remove others info (eg.email,name,,userType,,)- password is empty
    public boolean removeInfoUserFile(String fileName, String email) {
        List<String> fileContent = new ArrayList<>();
        boolean recordUpdated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                if (data[0].equalsIgnoreCase(email)) {
                    recordUpdated = true;
                    // Update the user record with email, name, and empty fields
                    fileContent.add(data[0] + "," + data[1] + ",," + data[3] + ",,");
                } else {
                    fileContent.add(line); // Keep other records unchanged
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            return false;
        }

        if (!recordUpdated) {
            JOptionPane.showMessageDialog(null, "No matching record found in " + fileName + "!");
            return false;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : fileContent) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error writing to file: " + ex.getMessage());
            return false;
        }

        return true;
    }

    public boolean deleteFromFile(String fileName, String email) { //delete the info in role.txt
        List<String> fileContent = new ArrayList<>();
        boolean recordDeleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                // Skip the line with the matching email
                if (!data[0].equalsIgnoreCase(email)) {
                    fileContent.add(line);
                } else {
                    recordDeleted = true; // Mark as deleted
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error reading file: " + ex.getMessage());
            return false;
        }

        if (!recordDeleted) {
            JOptionPane.showMessageDialog(null, "No matching record found in " + fileName + "!");
            return false;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : fileContent) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error writing to file: " + ex.getMessage());
            return false;
        }

        return true;
    }

    //Top up credit    
    public void processChangesCredit(String email, String name, double currentAmount, double changesAmount) throws IOException {
        String transFilePath = transCreditFile;
        double newAmount = currentAmount + changesAmount;

        //get the next transaction id 
        int transactionId = 1; // Default starting value
        File transactionFile = new File(transFilePath);

        // Check if the file exists
        if (transactionFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(transactionFile))) {
                String line;
                int maxId = 0;

                // Loop through each line in the file
                while ((line = reader.readLine()) != null) {
                    // Identify the first delimiter (comma or semicolon)
                    String[] parts = line.split("[,;]");
                    if (parts.length > 0) {
                        try {
                            // Parse the first part as an integer (transaction ID)
                            int currentId = Integer.parseInt(parts[0].trim());
                            maxId = Math.max(maxId, currentId); // Update max ID
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid transaction ID: " + parts[0]);
                        }
                    }
                }

                // Determine the next transaction ID
                transactionId = maxId + 1;
            }
        }

        //get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = formatter.format(new Date());
        // Extract date and time separately
        String[] dateTimeParts = dateTime.split(" ");
        String date = dateTimeParts[0];
        String time = dateTimeParts[1];

        // Prepare transaction details
        String transactionDetails = String.format(
                "%s,%s,%.2f,%.2f,%s,%s\n",
                transactionId, email, currentAmount, changesAmount, date, time
        );

        // Save transaction details to the file
        try (FileWriter writer = new FileWriter(transFilePath, true)) { // Append mode
            writer.write(transactionDetails);
        }

        // Update the credit in the file base on role
        User user = new User();
        String role = user.getRoleByEmail(email, userFile);

        if (role != null) {
            switch (role.toLowerCase()) {
                case "vendor":
                    super.updateCredit(email, newAmount, vendorFile, 4);
                    break;
                case "runner":
                    super.updateCredit(email, newAmount, runnerFile, 3);
                    break;
                case "customer":
                    super.updateCredit(email, newAmount, cusFile, 2);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + role);
            }
        } else {
            throw new IOException("User role not found for email: " + email);
        }

        // Notify the user of success
        javax.swing.JOptionPane.showMessageDialog(null, "Changes completed successfully!\nThe receipt has been sent to the user.", "Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);

        // Show receipt UI
        adminCusReceipt receipt = new adminCusReceipt(transactionId, email, name, currentAmount, changesAmount, date, time);
        receipt.setVisible(true);
    }

    //Notification    
    public ArrayList<String> getTransactionMessages() { // Method to retrieve the list of transaction messages
        ArrayList<String> transactionMessages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(transCreditFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 6) {
                    String transactionId = parts[0];
                    String email = parts[1];
                    double amountChanges = Double.parseDouble(parts[3]);
                    String date = parts[4];
                    String time = parts[5];

                    String message;
                    if (amountChanges >= 0) {
                        message = String.format(
                                "Top-up amount RM%.2f has been successfully credited into %s's account at %s %s (transaction id: %s)",
                                amountChanges, email, date, time, transactionId
                        );
                    } else {
                        message = String.format(
                                "Withdraw amount RM%.2f has been successfully deducted from %s's account at %s %s (transaction id: %s)",
                                Math.abs(amountChanges), email, date, time, transactionId
                        );
                    }
                    // Add message to the list
                    transactionMessages.add(message);
                }
            }
        } catch (IOException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error reading file: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        // Reverse the list so the latest transactions appear first
        Collections.reverse(transactionMessages);

        return transactionMessages;
    }
}
