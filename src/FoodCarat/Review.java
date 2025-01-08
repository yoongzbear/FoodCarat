/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class Review {
    private int reviewID;
    private int orderID;
    private String orderFeedback;
    private String vendorRating;
    private String vendorFeedback;
    private String runnerRating;
    private String reviewDate;
    
    public Review(int orderID){ //for getting customer order feedback
        this.orderID = orderID;
    }
    
    public Review(int orderID, String orderFeedback, String vendorRating, String vendorFeedback, String runnerRating){ //to save customer order feedback with runner
        this.orderID = orderID;
        this.orderFeedback = orderFeedback;
        this.vendorRating = vendorRating;
        this.vendorFeedback = vendorFeedback;
        this.runnerRating = runnerRating;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderFeedback() {
        return orderFeedback;
    }

    public void setOrderFeedback(String orderFeedback) {
        this.orderFeedback = orderFeedback;
    }

    public String getVendorRating() {
        return vendorRating;
    }

    public void setVendorRating(String vendorRating) {
        this.vendorRating = vendorRating;
    }

    public String getVendorFeedback() {
        return vendorFeedback;
    }

    public void setVendorFeedback(String vendorFeedback) {
        this.vendorFeedback = vendorFeedback;
    }

    public String getRunnerRating() {
        return runnerRating;
    }

    public void setRunnerRating(String runnerRating) {
        this.runnerRating = runnerRating;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public String getFeedback() throws FileNotFoundException, IOException { //get the feedback given by customer for the order
        StringBuilder result = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/review.txt"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reviewID = parts[0];
                    String reviewType = parts[2];  
                    String rating = parts[3];   
                    String review = parts[4];  
                    String date = parts[5];  

                //Check if orderID matches the current order's ID
                if (parts[1].equals(String.valueOf(this.orderID))) {
                    //get splitted data if orderID match
                    
                    
                    //define order rate and review based on reviewType, if null will return null
                    if (reviewType.equals("order")) {
                        orderFeedback = review != null && !review.trim().isEmpty() ? review : "No feedback provided";  
                    } else if (reviewType.equals("vendor")) {
                        vendorRating = rating != null && !rating.trim().isEmpty() ? rating : "No rating";
                        vendorFeedback = review != null && !review.trim().isEmpty() ? review : "No feedback provided"; 
                    } else if (reviewType.equals("runner")) {
                        runnerRating = rating != null && !rating.trim().isEmpty() ? rating : "No rating"; 
                    }
                }
            }
        }

        //Format the result in the requested order
            result.append(orderID).append(",")  // orderID
                  .append(orderFeedback != null ? orderFeedback : "null").append(",") 
                  .append(vendorRating != null ? vendorRating : "null").append(",") 
                  .append(vendorFeedback != null ? vendorFeedback : "null").append(",")
                  .append(runnerRating != null ? runnerRating : "null");
        //Return the formatted result (or empty if nothing found)
        return result.length() > 0 ? result.toString() : "";
    }
    
    //get all reviews 
    public List<String[]> getAllReviews() {
        List<String[]> allReviews = new ArrayList<>();
        
        try {
            FileReader fr = new FileReader("resources/review.txt");
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                String[] reviewData = read.split(",");
                allReviews.add(reviewData);
            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from review file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return allReviews;
    }
    
    
    public void saveOrderFeedback() { //save order feedback when user input
        //generate reviewID
        int lastFeedback = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/review.txt"));
            String line;
            while((line=br.readLine()) != null){
                String[] record = line.split(",");
                lastFeedback = Integer.parseInt(record[0]);
            }
            lastFeedback = lastFeedback + 1;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        
        
        String reviewDate = LocalDate.now().toString(); //Current date for the review (CURRENT NOT WORKING)
        //write the review separately
        try {
            BufferedReader br = new BufferedReader(new FileReader("resources/review.txt"));
            StringBuilder stringBuilder = new StringBuilder(); 
            String line;

            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                int checkBookingID = Integer.parseInt(record[0]);

                if (checkBookingID == orderID) {
                    //1. Write Order Review Record
                    String orderReviewRecord = String.join(",", 
                        String.valueOf(lastFeedback), 
                        String.valueOf(orderID), 
                        "order", 
                        "null",
                        orderFeedback, 
                        reviewDate
                    );
                    stringBuilder.append(orderReviewRecord).append("\n");
                    
                    lastFeedback = lastFeedback + 1;

                    //2. Write Vendor Review Record
                    String vendorReviewRecord = String.join(",", 
                        String.valueOf(lastFeedback), 
                        String.valueOf(orderID), 
                        "vendor", 
                        vendorRating, 
                        vendorFeedback, 
                        reviewDate
                    );
                    stringBuilder.append(vendorReviewRecord).append("\n");

                    //3. Write Runner Review Record if exists
                    if (runnerRating != null) {
                        lastFeedback = lastFeedback + 1;
                        String runnerReviewRecord = String.join(",", 
                            String.valueOf(lastFeedback), 
                            String.valueOf(orderID), 
                            "runner", 
                            runnerRating,
                            "null", 
                            reviewDate
                        );
                        stringBuilder.append(runnerReviewRecord).append("\n");
                    }
                } else {
                    //keep the existing record
                    stringBuilder.append(line).append("\n");
                }
            }
            br.close();

            //Write the updated content back to the file
            BufferedWriter bw = new BufferedWriter(new FileWriter("resources/review.txt"));
            bw.write(stringBuilder.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
