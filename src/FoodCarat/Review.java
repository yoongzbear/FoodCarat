/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
    private String customerEmail;
    private String foodCourtComplaint;
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
        this.customerEmail = User.getSessionEmail();
        this.orderFeedback = orderFeedback;
        this.vendorRating = vendorRating;
        this.vendorFeedback = vendorFeedback;
        this.runnerRating = runnerRating;
    }
    
    public Review(String foodCourtComplaint){
        this.orderID = -1;
        this.foodCourtComplaint = foodCourtComplaint;
        this.customerEmail = User.getSessionEmail();
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
    
    //get all reviews for vendor/runner
    public List<String[]> getAllReviews(String email, String type) {
        List<String[]> reviews = new ArrayList<>();
        
        Order orders = new Order();
        
        try {
            FileReader fr = new FileReader(reviewFileName);
            BufferedReader br = new BufferedReader(fr);
            String read;
            
            while ((read = br.readLine()) != null) {
                String[] reviewData = read.split(",");
                //vendor
                if (type.equals("vendor")) {
                    String reviewType = reviewData[2];
                    if (reviewType.equalsIgnoreCase("vendor")) {
                        List<Integer> vendorOrderIDs = orders.getOrderIDsReview(email, type);
                        int orderID = Integer.parseInt(reviewData[1]);
                        if (vendorOrderIDs.contains(orderID)) {
                            //add all info of review into the list
                            reviews.add(reviewData);
                        }
                    }
                } else if (type.equals("runner")) {
                    String reviewType = reviewData[2];
                    if (reviewType.equalsIgnoreCase("runner")) {
                        List<Integer> runnerOrderIDs = orders.getOrderIDsReview(email, type);
                        int orderIDs = Integer.parseInt(reviewData[1]);
                        if (runnerOrderIDs.contains(orderIDs)) {
                            //add all info of review into the list
                            reviews.add(reviewData);
                        }
                    }
                } else if (type.equals("order")) {
                    String reviewType = reviewData[2];
                    if (reviewType.equalsIgnoreCase("order")) {
                        List<Integer> vendorOrderIDs = orders.getOrderIDsReview(email, "vendor");
                        int orderIDs = Integer.parseInt(reviewData[1]);
                        if (vendorOrderIDs.contains(orderIDs)) {
                            //add all info of review into the list
                            reviews.add(reviewData);
                        }
                    }
                }

            }
        } catch(IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from review file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return reviews;
    }
    
    //get review, order and item info based on review ID
    public String[] getReview(int id) {
        String[] reviewInfo = null;
        try {
            FileReader fr = new FileReader(reviewFileName);
            BufferedReader br = new BufferedReader(fr);
            String read;

            while ((read = br.readLine()) != null) {
                String[] reviewData = read.split(",");
                int reviewID = Integer.parseInt(reviewData[0]);
                if (reviewID == id) {
                    //get order info
                    //0reviewID,1orderID,2reviewType,3rating,4review,5date,6customerEmail
                    int orderID = Integer.parseInt(reviewData[1]);
                    Order order = new Order();
                    String[] orderData = order.getOrder(orderID);
                    //combine reviewData and orderData
                    if (orderData != null) {
                        reviewInfo = new String[reviewData.length + orderData.length];
                        System.arraycopy(reviewData, 0, reviewInfo, 0, reviewData.length);
                        System.arraycopy(orderData, 0, reviewInfo, reviewData.length, orderData.length);
                    } else {
                        reviewInfo = reviewData; 
                    }
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to read from the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return reviewInfo;
    }
    
    public void saveOrderFeedback() { 
        //Generate reviewID
        int lastFeedback = 0;
        //Read the existing file to get the last feedback ID
        try {
            BufferedReader br = new BufferedReader(new FileReader(reviewFileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] record = line.split(",");
                lastFeedback = Integer.parseInt(record[0]);
            }
            lastFeedback = lastFeedback + 1; // Increment to generate new feedback ID
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Get review date
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String reviewDate1 = sdf.format(today.getTime());

        //Use a StringBuilder to accumulate the review records
        StringBuilder stringBuilder = new StringBuilder(); 

        //review from order
        if (orderID > 0) {
            //1. Write Order Review Record
            stringBuilder.append(String.join(",", 
                String.valueOf(lastFeedback), 
                String.valueOf(orderID), 
                "order", 
                "null", 
                orderFeedback, 
                reviewDate1, 
                customerEmail,
                "null"
            )).append("\n");

            lastFeedback++;

            //2. Write Vendor Review Record
            stringBuilder.append(String.join(",", 
                String.valueOf(lastFeedback), 
                String.valueOf(orderID), 
                "vendor", 
                vendorRating, 
                vendorFeedback, 
                reviewDate1, 
                customerEmail,
                "null"
            )).append("\n");

            lastFeedback++;

            //3. Write Runner Review Record if it exists
            if (runnerRating != null && !runnerRating.isEmpty()) {
                stringBuilder.append(String.join(",", 
                    String.valueOf(lastFeedback), 
                    String.valueOf(orderID), 
                    "runner", 
                    runnerRating, 
                    "null", 
                    reviewDate1, 
                    customerEmail,
                    "null"
                )).append("\n");
            }
        } 
        //foodcourt complaint
        else {
            stringBuilder.append(String.join(",", 
                String.valueOf(lastFeedback), 
                "null",  
                "foodcourt", 
                "null", 
                foodCourtComplaint, 
                reviewDate1, 
                customerEmail,
                "unresolved"
            )).append("\n");
        }

        // ppend the feedback to the file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(reviewFileName, true));
            bw.write(stringBuilder.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //get count of ratings of vendor/runner
    public int[] ratingCount(String email, String role, String type, String timeRange) { //type = weekly/monthly
        int[] totalCount = new int[5]; //{1 star,2 stars, ..., 5 stars}
        List<String[]> reviews = getAllReviews(email, role);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //get start and end date
        LocalDate startDate = null, endDate = null;
        String[] timeRangeParts = timeRange.split(",");
        try {
            if (type.equalsIgnoreCase("weekly")) {
                startDate = LocalDate.parse(timeRangeParts[0], dateFormat); 
                endDate = LocalDate.parse(timeRangeParts[1], dateFormat);   
            } else if (type.equalsIgnoreCase("monthly")) {
                int month = Integer.parseInt(timeRangeParts[0]); 
                int year = Integer.parseInt(timeRangeParts[1]);  
                startDate = LocalDate.of(year, month, 1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            } else if (type.equalsIgnoreCase("quarterly")) {
                startDate = LocalDate.parse(timeRangeParts[0], dateFormat);
                endDate = LocalDate.parse(timeRangeParts[1], dateFormat);
            } else if (type.equalsIgnoreCase("yearly")) {
                int year = Integer.parseInt(timeRange); 
                startDate = LocalDate.of(year, 1, 1); //year-01-01
                endDate = LocalDate.of(year, 12, 31); //year-12-31
            }
        } catch (Exception e) {
            System.err.println("Invalid time range format: " + timeRange);
            return totalCount;
        }

        //count ratings based on timeRange
        for (String[] review : reviews) {
            try {
                int rating = Integer.parseInt(review[3]); 
                LocalDate reviewDate = LocalDate.parse(review[5], dateFormat); 
                
                if ((reviewDate.isEqual(startDate) || reviewDate.isAfter(startDate))
                        && (reviewDate.isEqual(endDate) || reviewDate.isBefore(endDate))) {
                    if (rating >= 1 && rating <= 5) {
                        totalCount[rating - 1]++; //increase total count at the rating's index
                    }
                }
            } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
                System.err.println("Invalid data in review: " + e.getMessage());
            }
        }

        return totalCount;
    }
    
    // Method to calculate average vendor rating based on vendorEmail
    public double getAverageRating(String email, String role) {
        Order orders = new Order();
        List<Integer> orderIDs = orders.getOrderIDsReview(email, role);
        if (orderIDs.isEmpty()) {
            return 0;  // No orders for the vendor, so return 0
        }
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
                    int orderID1 = Integer.parseInt(parts[1]);
                    if (orderIDs.contains(orderID1)) {
                        if (rating != null && !rating.isEmpty()) {
                            totalRating += Integer.parseInt(rating);
                            ratingCount++;
                        }
                    }
                } else if ("runner".equals(reviewType)) {
                    int orderID1 = Integer.parseInt(parts[1]);
                    if (orderIDs.contains(orderID1)) {
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
        Order orders = new Order();
        List<Integer> orderIDs = orders.getOrderIDsReview(vendorEmail, "vendor");
        if (orderIDs.isEmpty()) return "No reviews available";  // No orders for the vendor, so return "No reviews available"

        List<String> vendorReviews = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reviewType = parts[2];
                String review = parts[4];
                String orderID = parts[1];
                
                if ("null".equals(orderID)) {
                    continue; 
                }
                
                int orderID1 = Integer.parseInt(orderID);

                // If the review is for a vendor and matches the orderID
                if ("vendor".equals(reviewType) && orderIDs.contains(orderID1)) {
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
        Order orders = new Order();
        List<Integer> orderIDs = orders.getOrderIDsReview(runnerEmail, "runner");
        if (orderIDs.isEmpty()) return "No rating";  // If no orders found for the runner, return "No rating"

        try (BufferedReader reader = new BufferedReader(new FileReader(reviewFileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String reviewType = parts[2];
                String rating = parts[3];
                String orderID = parts[1];
                
                if ("null".equals(orderID)) {
                    continue; 
                }
                
                int orderID1 = Integer.parseInt(orderID);

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
    public void updateStatus(int reviewID, String newStatus) {
        File file = new File("review.txt");
        List<String> fileContent = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                try {
                    if (parts.length > 1 && Integer.parseInt(parts[0]) == reviewID) {
                        parts[7] = newStatus;
                        updated = true;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Handle invalid data in the file, e.g., skip this line
                    continue;
                }
                fileContent.add(String.join(",", parts));
            }

            if (updated) {
                // Write updated content back to the file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    for (String content : fileContent) {
                        bw.write(content);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
