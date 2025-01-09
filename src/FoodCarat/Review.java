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
import java.util.Random;
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
    
    private String reviewFileName = "resources/review.txt";
    private String vendorFileName = "resources/vendor.txt";
    private String runnerFileName = "resources/runner.txt";
    private String orderFileName = "resources/customerOrder.txt";
    
    public Review(){ 
        
    }
    
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

        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFileName))) {
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
            FileReader fr = new FileReader(reviewFileName);
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
            BufferedReader br = new BufferedReader(new FileReader(reviewFileName));
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
            BufferedReader br = new BufferedReader(new FileReader(reviewFileName));
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
            BufferedWriter bw = new BufferedWriter(new FileWriter(reviewFileName));
            bw.write(stringBuilder.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Method to get orderIDs associated with a specific vendorEmail
    private List<Integer> getOrderIDsByVendorEmail(String vendorEmail) {
        List<Integer> orderIDs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(orderFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                    if (parts.length > 5) {  // Check for sufficient columns
                    String currentVendorEmail = parts[5];
                    if (currentVendorEmail.equals(vendorEmail)) {
                        int orderID = Integer.parseInt(parts[0]);  // Order ID is in position 0
                        orderIDs.add(orderID);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "VendorEmail: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return orderIDs;
    }

    // Method to calculate average vendor rating based on vendorEmail
    public double getVendorAverageRating(String vendorEmail) {
        List<Integer> orderIDs = getOrderIDsByVendorEmail(vendorEmail);
        if (orderIDs.isEmpty()) return 0;  // No orders for the vendor, so return 0

        double totalRating = 0;
        int ratingCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reviewType = parts[2];
                String rating = parts[3];

                // If the review is for a vendor
                if ("vendor".equals(reviewType)) {
                    int orderID = Integer.parseInt(parts[1]);
                    if (orderIDs.contains(orderID)) {
                        if (rating != null && !rating.isEmpty()) {
                            totalRating += Integer.parseInt(rating);
                            ratingCount++;
                        }
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Rate: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // If no reviews or ratings were found, return 0 or any appropriate message
        return ratingCount > 0 ? totalRating / ratingCount : 0;  // Return 0 if no ratings found
    }

    // Method to get a random vendor review based on vendorEmail
    public String getRandomVendorReview(String vendorEmail) {
        List<Integer> orderIDs = getOrderIDsByVendorEmail(vendorEmail);
        if (orderIDs.isEmpty()) return "No reviews available";  // No orders for the vendor, so return "No reviews available"

        List<String> vendorReviews = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reviewType = parts[2];
                String review = parts[4];
                int orderID = Integer.parseInt(parts[1]);

                // If the review is for a vendor and matches the orderID
                if ("vendor".equals(reviewType) && orderIDs.contains(orderID)) {
                    vendorReviews.add(review);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Review: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Select a random review from the list, if any
        if (!vendorReviews.isEmpty()) {
            Random random = new Random();
            return vendorReviews.get(random.nextInt(vendorReviews.size()));
        }

        return "No reviews available";  // If no vendor reviews found
    }

    // Method to get the runner rating based on the runner's email
    public String getRunnerRatingForRunner(String runnerEmail) {
        List<Integer> orderIDs = getOrderIDsByRunnerEmail(runnerEmail);
        if (orderIDs.isEmpty()) return "No rating";  // If no orders found for the runner, return "No rating"

        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reviewType = parts[2];
                String rating = parts[3];
                int orderID = Integer.parseInt(parts[1]);

                // If the review is for a runner and matches the orderID
                if ("runner".equals(reviewType) && orderIDs.contains(orderID)) {
                    return rating != null ? rating : "No rating";
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "a: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return "No rating";  // If no runner review found
    }

    // Method to get orderIDs associated with a specific runnerEmail
    private List<Integer> getOrderIDsByRunnerEmail(String runnerEmail) {
        List<Integer> orderIDs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(orderFileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String currentRunnerEmail = parts[6];  // Assuming runner email is in position 6 (adjust as needed)

                if (currentRunnerEmail.equals(runnerEmail)) {
                    int orderID = Integer.parseInt(parts[0]);  // Order ID is in position 0
                    orderIDs.add(orderID);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "as: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return orderIDs;
    }
}
