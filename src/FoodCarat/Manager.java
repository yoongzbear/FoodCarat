/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author User
 */
public class Manager {
    
    //Monitor Runner Performance
    //
    public static Map<String, Runner> getRunnerPerformance(String selectedMonth) throws IOException {
       Map<String, Runner> performanceDataMap = new HashMap<>();
        
       // Read the customer order file to map orderID to runnerID
       BufferedReader orderReader = new BufferedReader(new FileReader("customer order.txt"));
       String line;

       while ((line = orderReader.readLine()) != null) {
           String[] orderDetails = line.split(",");

           // Check if orderDetails[1] contains "Delivery"
           if ("Delivery".equalsIgnoreCase(orderDetails[1].trim())) {
               String orderID = orderDetails[0].trim();
               String runnerID = orderDetails[6].trim();
           }
       }
       orderReader.close();
        
        // Read the review file to get review data for "runner" reviewType
        BufferedReader reviewReader = new BufferedReader(new FileReader("review.txt"));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while ((line = reviewReader.readLine()) != null) {
            String[] reviewDetails = line.split(",");
            String reviewType = reviewDetails[2].trim();
            String orderID = reviewDetails[6].trim();
            
            String runnerID = Order.getRunnerForOrder(orderID);// fetch runnerID
            if ("runner".equals(reviewType)) {
                String rating = reviewDetails[3].trim();
                String reviewDate = reviewDetails[5].trim();
                
                // Check if the review falls in the selected month
                if (isInSelectedMonth(reviewDate, selectedMonth)) {
                    // Add or update performance data for the runner
                    Runner data = performanceDataMap.getOrDefault(runnerID, new Runner());
                    int ratingValue = Integer.parseInt(rating);
                    data.addOrder(orderID);
                    data.addRating(ratingValue);
                    performanceDataMap.put(runnerID, data);
                }
            }
        }
        reviewReader.close();
        return performanceDataMap;
    }
    //check the month selected for the runner perfomance
    private static boolean isInSelectedMonth(String reviewDate, String selectedMonth) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(reviewDate);
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            String month = monthFormat.format(date);
            return month.equals(selectedMonth);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
