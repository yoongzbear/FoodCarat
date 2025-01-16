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
            
            Order order = new Order();
            String runnerID = order.getRunnerForOrder(orderID);// fetch runnerID
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
    // Method to calculate vendor performance by month
    public List<String[]> getVendorPerformanceByMonth(String selectedMonth) {
        List<String[]> tableData = new ArrayList<>();
        Map<String, VendorPerformance> vendorData = new HashMap<>();
        Set<String> processedOrderIDs = new HashSet<>(); // Track processed order IDs
        
        try {
            // Load customerOrder.txt
            BufferedReader orderReader = new BufferedReader(new FileReader("customerOrder.txt"));
            String orderLine;

            while ((orderLine = orderReader.readLine()) != null) {
                String[] orderData = orderLine.split(",");
                String orderID = orderData[0];
                String orderMonth = getMonthFromOrder(orderData[8]); // Extract month from orderID or date

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

                                // Aggregate vendor data
                                VendorPerformance vendorPerf = vendorData.computeIfAbsent(vendorEmail,
                                k -> new VendorPerformance(getVendorName(vendorEmail)));

                                vendorPerf.addRevenue(revenue);   // Add revenue for completed items
                                uniqueVendorsInOrder.add(vendorEmail);    // Add a single order for the vendor
                            }
                        }
                    }
                    // Increment orders for each unique vendor in this order
                    for (String vendorEmail : uniqueVendorsInOrder) {
                        VendorPerformance vendorPerf = vendorData.get(vendorEmail);
                        if (vendorPerf != null) {
                            vendorPerf.incrementOrders(1); // Add one order to the vendor
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
        for (VendorPerformance vp : vendorData.values()) {
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

    //method to extract month from order data
    private String getMonthFromOrder(String date) {
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

    // Helper method to retrieve item details from item.txt
    private String[] getItemDetails(String itemID) {
        try {
            BufferedReader itemReader = new BufferedReader(new FileReader("item.txt"));
            String itemLine;

            while ((itemLine = itemReader.readLine()) != null) {
                String[] itemData = itemLine.split(",");
                if (itemData[0].equals(itemID)) {
                    itemReader.close();
                    return itemData; // Return matching item details
                }
            }
            itemReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to get vendor name from user.txt
    private String getVendorName(String vendorEmail) {
        try {
            BufferedReader userReader = new BufferedReader(new FileReader("user.txt"));
            String userLine;

            while ((userLine = userReader.readLine()) != null) {
                String[] userData = userLine.split(",");
                if (userData[0].equals(vendorEmail)) {
                    userReader.close();
                    return userData[1]; // Return vendor name
                }
            }
            userReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Vendor";
    }
    
    public class VendorPerformance {
        private String vendorName;
        private double totalRevenue;
        private int totalOrders;

        public VendorPerformance(String vendorName) {
            this.vendorName = vendorName;
            this.totalRevenue = 0.0;
            this.totalOrders = 0;
        }

        public String getVendorName() {
            return vendorName;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public double getAverageValuePerOrder() {
            return totalOrders == 0 ? 0.0 : totalRevenue / totalOrders;
        }

        public void addRevenue(double revenue) {
            this.totalRevenue += revenue; // Increment revenue only
        }

        public void incrementOrders(int count) {
            this.totalOrders += count; // Increment order count only
        }
    }
}
