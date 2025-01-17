/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author User
 */
public class Manager extends User{
    private String userFile = "resources/user.txt";
    //Monitor Runner Performance
    //
    public Map<String, Runner> getRunnerPerformance(String selectedMonth) throws IOException {
        Map<String, Runner> performanceDataMap = new HashMap<>();
        Map<String, String> orderToRunnerMap = new HashMap<>();  // Map to store orderID to runnerID mapping

        // Read the customer order file to map orderID to runnerID
        BufferedReader orderReader = new BufferedReader(new FileReader("customerOrder.txt"));
        String line;

        while ((line = orderReader.readLine()) != null) {
            String[] orderDetails = line.split(",");

            // Check if orderDetails[1] contains "Delivery"
            if (orderDetails.length > 1 && "Delivery".equalsIgnoreCase(orderDetails[1].trim())) {
                String orderIDinOrder = orderDetails[0].trim();
                String runnerIDinOrder = orderDetails[5].trim();

                // Store the orderID and runnerID mapping
                orderToRunnerMap.put(orderIDinOrder, runnerIDinOrder);
            }
        }
        orderReader.close();

        // Read the review file to get review data for "runner" reviewType
        BufferedReader reviewReader = new BufferedReader(new FileReader("review.txt"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while ((line = reviewReader.readLine()) != null) {
            String[] reviewDetails = line.split(",");

            if (reviewDetails.length > 5) {
                String reviewType = reviewDetails[2].trim();
                String orderID = reviewDetails[1].trim();

                // Check if the review type is "runner" and if orderID exists in the customer orders
                if ("runner".equalsIgnoreCase(reviewType) && orderToRunnerMap.containsKey(orderID)) {
                    String runnerID = orderToRunnerMap.get(orderID); // Fetch runnerID from the map

                    String rating = reviewDetails[3].trim();
                    String reviewMonth = getMonthFromOrderOrReview(reviewDetails[5]);

                    // Check if the review falls in the selected month
                    if (reviewMonth.equalsIgnoreCase(selectedMonth)) {
                        // Add or update performance data for the runner
                        Runner data = performanceDataMap.getOrDefault(runnerID, new Runner());
                        int ratingValue = Integer.parseInt(rating);
                        data.incrementOrders();
                        data.addRating(ratingValue);
                        performanceDataMap.put(runnerID, data);
                    }
                }
            }
        }
        reviewReader.close();
        return performanceDataMap;
    }

    // Method to calculate vendor performance by month
    public List<String[]> getVendorPerformanceByMonth(String selectedMonth) {
        List<String[]> tableData = new ArrayList<>();
        Map<String, Vendor> vendorData = new HashMap<>();
        Set<String> processedOrderIDs = new HashSet<>(); // Track processed order IDs
        
        try {
            // Load customerOrder.txt
            BufferedReader orderReader = new BufferedReader(new FileReader("customerOrder.txt"));
            String orderLine;

            while ((orderLine = orderReader.readLine()) != null) {
                String[] orderData = orderLine.split(",");
                String orderID = orderData[0];
                String orderMonth = getMonthFromOrderOrReview(orderData[8]); // Extract month from orderID or date

                if (orderMonth.equalsIgnoreCase(selectedMonth) && "Completed".equals(orderData[3])) {
                     // Skip if order ID is already processed
                    if (processedOrderIDs.contains(orderID)) continue;
                    processedOrderIDs.add(orderID); // Mark this order as processed
                    
                    String[] itemsData = orderData[2].replace("[", "").replace("]", "").split("\\|");
                    Set<String> uniqueVendorsInOrder = new HashSet<>(); // To track vendors per order
                    
                    for (String item : itemsData) {
                        String[] itemDetailsArr = item.split(";");
                        if (itemDetailsArr.length == 2) {
                            String itemID = itemDetailsArr[0];
                            int quantity = Integer.parseInt(itemDetailsArr[1]);

                            Item items = new Item();
                            String[] itemInfo = items.itemData(itemID);
                            if (itemInfo != null) {
                                double price = Double.parseDouble(itemInfo[3]); // Item price
                                String vendorEmail = itemInfo[5];
                                double revenue = price * quantity;
                                
                                User user = new User();
                                String [] vendorDetails = user.performSearch(vendorEmail, userFile);
                                String vendorName = vendorDetails[1];
                                // Aggregate vendor data
                                Vendor vendorPerf = vendorData.computeIfAbsent(vendorEmail,
                                k -> new Vendor(vendorName,true));
                                //or this  k -> Vendor.createByName(user.getUserName(vendorEmail)));

                                vendorPerf.addRevenue(revenue);   // Add revenue for completed items
                                uniqueVendorsInOrder.add(vendorEmail);    // Add a single order for the vendor
                            }
                        }
                    }
                    // Increment orders for each unique vendor in this order
                    for (String vendorEmail : uniqueVendorsInOrder) {
                        Vendor vendorPerf = vendorData.get(vendorEmail);
                        if (vendorPerf != null) {
                            vendorPerf.incrementOrders(); // Add one order to the vendor
                        }
                    }
                }
            }
            orderReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert vendorData to tableData
        int no = 1;
        for (Vendor vp : vendorData.values()) {
            tableData.add(new String[]{
                String.valueOf(no++),
                vp.getVendorName(),
                String.format("%.2f", vp.getTotalRevenue()),
                String.valueOf(vp.getTotalOrders()),
                String.format("%.2f", vp.getAverageValuePerOrder()),
            });
        }

        return tableData;
    }

    // method to extract month from order data (ven)/review data(runner)
    private String getMonthFromOrderOrReview(String date) {
        if (date != null && date.contains("-")) {
            String[] dateParts = date.split("-");
            if (dateParts.length >= 2) {
                int monthNumber = Integer.parseInt(dateParts[1]); // Extract month as number
                return getMonthName(monthNumber); // Convert number to month name
            }
        }
        return "Invalid Date"; // Return a default value if the date is invalid
    }

    // method to convert month number to month name
    private String getMonthName(int monthNumber) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };

        if (monthNumber >= 1 && monthNumber <= 12) {
            return monthNames[monthNumber - 1]; // Adjust for 0-based index
        }
        return "Invalid Month";
    }
    
    //Manager notification complain
}
