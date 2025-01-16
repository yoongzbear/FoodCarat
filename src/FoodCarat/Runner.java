/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Yuna
 */
public class Runner extends User{
    private String runnerFile = "resources/runner.txt";
    
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
            System.out.println("Error reading runner.txt: " + e.getMessage());
        }
        return null;
    }
}
