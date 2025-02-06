/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FoodCarat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author User
 */
public class Manager extends User {

    private String userFile = "resources/user.txt";
    private String orderFile = "resources/customerOrder.txt";

    //Monitor Runner Performance
    public Map<String, String> getRunnerPerformance(int selectedMonth, int selectedYear) throws IOException {
        Map<String, Order> orderMap = new HashMap<>();
        Map<Integer, String> orderToRunnerMap = new HashMap<>();

        BufferedReader orderReader = new BufferedReader(new FileReader(orderFile));
        String line;

        while ((line = orderReader.readLine()) != null) {
            String[] orderDetails = line.split(",");
            if (orderDetails.length > 1 && "Delivery".equalsIgnoreCase(orderDetails[1].trim())) {
                Integer orderIDinOrder = Integer.parseInt(orderDetails[0].trim());
                String runnerIDinOrder = orderDetails[5].trim();
                orderToRunnerMap.put(orderIDinOrder, runnerIDinOrder);
            }
        }
        orderReader.close();

        // Get all reviews using the getAllReviews method
        Review review = new Review();
        List<String[]> allReviews = review.getAllReviews();

        for (String[] reviewDetails : allReviews) {
            if (reviewDetails.length > 5) {
                String reviewType = reviewDetails[2].trim();
                Integer orderID = null;

                try {
                    orderID = Integer.parseInt(reviewDetails[1].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                // Only process reviews for runners and valid orders
                if ("runner".equalsIgnoreCase(reviewType) && orderToRunnerMap.containsKey(orderID)) {
                    String runnerID = orderToRunnerMap.get(orderID);
                    try {
                        String[] dateParts = reviewDetails[5].split("-");
                        int reviewYear = Integer.parseInt(dateParts[0]);
                        int reviewMonth = Integer.parseInt(dateParts[1]);

                        if (reviewYear == selectedYear && reviewMonth == selectedMonth) {
                            Order order = orderMap.getOrDefault(runnerID, new Order());
                            order.incrementOrders();
                            orderMap.put(runnerID, order);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        // Prepare the performance data without average rating
        Map<String, String> runnerPerformanceMap = new HashMap<>();
        for (String runnerID : orderMap.keySet()) {
            Order order = orderMap.get(runnerID);
            int totalOrders = order.getTotalOrders();
            String performanceData = totalOrders + ",0.00"; // Default to 0.00 for rating
            runnerPerformanceMap.put(runnerID, performanceData);
        }

        return runnerPerformanceMap;
    }

    // Method to calculate vendor performance by month
    public Map<String, String> getVendorPerformanceByMonth(int selectedMonth, int selectedYear) throws IOException {
        Map<String, Order> vendorOrders = new HashMap<>();
        Set<Integer> processedOrderIDs = new HashSet<>(); // Track processed order IDs

        try (BufferedReader orderReader = new BufferedReader(new FileReader(orderFile))) { // Use try-with-resources
            String orderLine;

            while ((orderLine = orderReader.readLine()) != null) {
                String[] orderData = orderLine.split(",");
                if (orderData.length > 8) {
                    int orderID = Integer.parseInt(orderData[0].trim());
                    int orderMonth = Integer.parseInt(orderData[9].split("-")[1]);
                    int orderYear = Integer.parseInt(orderData[9].split("-")[0]);

                    if (orderMonth == selectedMonth && orderYear == selectedYear && "completed".equals(orderData[3])) {
                        // Skip already processed orders
                        if (processedOrderIDs.contains(orderID)) {
                            continue;
                        }
                        processedOrderIDs.add(orderID);

                        String[] itemsData = orderData[2].replace("[", "").replace("]", "").split("\\|");
                        Set<String> uniqueVendorsInOrder = new HashSet<>();

                        for (String item : itemsData) {
                            String[] itemDetailsArr = item.split(";");
                            if (itemDetailsArr.length == 2) {
                                int itemID = Integer.parseInt(itemDetailsArr[0]);
                                int quantity = Integer.parseInt(itemDetailsArr[1]);

                                Item items = new Item();
                                String[] itemInfo = items.itemData(itemID);
                                if (itemInfo != null) {
                                    double price = Double.parseDouble(itemInfo[3]);
                                    String vendorEmail = itemInfo[5];
                                    double revenue = price * quantity;

                                    // Aggregate vendor data using Order class
                                    Order order = vendorOrders.getOrDefault(vendorEmail, new Order());
                                    // Ensure vendor is counted only once per order
                                    if (!uniqueVendorsInOrder.contains(vendorEmail)) {
                                        order.incrementOrders(); // Count order only once per vendor
                                        uniqueVendorsInOrder.add(vendorEmail);
                                    }
                                    order.incrementRevenue(revenue);
                                    vendorOrders.put(vendorEmail, order);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert vendorOrders to vendor performance map
        Map<String, String> vendorPerformanceMap = new HashMap<>();
        for (Map.Entry<String, Order> entry : vendorOrders.entrySet()) {
            String vendorEmail = entry.getKey();
            Order orderData = entry.getValue();

            int totalOrders = orderData.getTotalOrders();
            double totalRevenue = orderData.getTotalRevenue();
            double avgOrderValue = orderData.getAverageValuePerOrder();

            String performanceData = totalOrders + "," + String.format("%.2f", totalRevenue) + "," + String.format("%.2f", avgOrderValue);
            vendorPerformanceMap.put(vendorEmail, performanceData);
        }

        return vendorPerformanceMap;
    }

    public List<String[]> getFilteredReviews(int selectedMonth) {
        List<String[]> filteredData = new ArrayList<>();
        int recordNumber = 1;

        Review review = new Review();
        List<String[]> allReviews = review.getAllReviews();
        for (String[] fields : allReviews) {
            if (fields.length >= 8) {
                int date = Integer.parseInt(fields[5].split("-")[1]);
                String reviewType = fields[2];
                String complaint = fields[4];
                String email = fields[6];
                String complaintStatus = fields[7];
                String reviewID = fields[0];

                User user = new User();
                String[] userDetails = user.getUserInfo(email);
                String cusName = userDetails != null && userDetails.length > 1 ? userDetails[1] : "Unknown";
                // Check if the month matches and review type is "foodcourt"
                // If selectedMonth is 0, no filtering will be applied (show all reviews)
                if ((selectedMonth == 0 || date == selectedMonth)
                        && reviewType.equalsIgnoreCase("foodcourt") && complaintStatus.equals("unresolved")) {

                    filteredData.add(new String[]{
                        String.valueOf(recordNumber), cusName, email, complaint, reviewID
                    });
                    recordNumber++;
                }
            }
        }
        Collections.reverse(filteredData); // Reverse the list so the latest reviews appear first
        for (int i = 0; i < filteredData.size(); i++) {
            filteredData.get(i)[0] = String.valueOf(i + 1);  // Set record number starting from 1 again
        }

        return filteredData;
    }

    public void updateReviewStatus(int reviewID) {
        Review review = new Review();
        review.updateStatus(reviewID, "resolved");
    }

    public void updateCustomerPoint(String cusEmail) {
        Customer customer = new Customer(cusEmail);
        customer.addPoints(50);
    }
}
