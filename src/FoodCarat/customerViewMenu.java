/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class customerViewMenu extends javax.swing.JFrame {

    /**
     * Creates new form customerViewMenu
     */
    private int orderID;
    private String orderType;
    private Order currentOrder;
    private String selectedVendorEmail;

    public customerViewMenu(int orderID, String orderType) {
        this.orderID = orderID;
        this.orderType = orderType;
        this.currentOrder = new Order(orderType, "customerEmail"); 
        initComponents();
        lItemName.setText("");
        lUnitPrice.setText("");
        lTotalPrice.setText("");
        bBackVendor.setVisible(false);
        bSave.setEnabled(false);
        bRemove.setEnabled(false);
        bCheckOut.setEnabled(false);
        loadVendors("resources/user.txt");
        tCart.getColumnModel().getColumn(4).setMaxWidth(0);
        tCart.getColumnModel().getColumn(4).setMinWidth(0);
        tCart.getColumnModel().getColumn(4).setPreferredWidth(0);
    }
    
    private void loadVendors(String userFilePath) {
        mainMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 15));
        //
        try (BufferedReader reader = new BufferedReader(new FileReader(userFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String userType = details[3];
                if ("vendor".equals(details[3])) {
                    String vendorEmail = details[0];
                    String vendorName = details[1];
                    
                    //get vendor info
                    Vendor vendor = new Vendor(vendorEmail);
                    String availableMethods = vendor.getAvailableMethod();
                    boolean isAvailableForOrderType;
                    if (availableMethods != null && !availableMethods.isEmpty()) {
                        String methodsWithoutBrackets = availableMethods.substring(1, availableMethods.length() - 1);
                        String[] availableMethodsArray = methodsWithoutBrackets.split(";");

                        isAvailableForOrderType = Arrays.asList(availableMethodsArray).contains(orderType);
                    } else {
                        //if vendor have non available methods
                        isAvailableForOrderType = false;
                    }
                    String logoPath = vendor.getPhotoLink();

                    // Get average rating and random review
                    Review review = new Review();
                    double vendorRating = review.getVendorAverageRating(vendorEmail);  // Get average rating
                    String randomReview = review.getRandomVendorReview(vendorEmail);  // Get random review
                    
                    JPanel vendorPanel = createVendorPanel(vendorName, vendorEmail, logoPath, vendorRating, randomReview, isAvailableForOrderType);
                    mainMenuPanel.add(vendorPanel);
                }
            }
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading the vendor file: " + e.getMessage());
        }
    }

    private JPanel createVendorPanel(String vendorName, String vendorEmail, String logoPath, double vendorRating, String randomReview, boolean isAvailableForOrderType) {
        // Create a new JPanel with a vertical BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setPreferredSize(new java.awt.Dimension(228, 270)); // Standardize size
        panel.setMaximumSize(new java.awt.Dimension(228, 270));
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(java.awt.Color.GRAY, 1), // Outer border
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10) // Inner padding (top, left, bottom, right)
        ));

        // Add the resized logo
        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(logoPath);
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Resize to 150x150
            logo.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            logo.setText("Image not found");
            logo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Center the text
        }
        logo.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT); // Center align

        // Add vendor details
        JLabel lblVendor = new JLabel(vendorName);
        lblVendor.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        JLabel lblRating = new JLabel("Vendor Average Rating: " + vendorRating);
        lblRating.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        JLabel lblReview = new JLabel("Review: " + randomReview);
        lblReview.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        JLabel lblUnavailable = new JLabel("");
        lblUnavailable.setVisible(false);
        
        if (!isAvailableForOrderType) {
            lblUnavailable.setVisible(true);
            lblUnavailable = new JLabel("Unavailable for selected method.");
            lblUnavailable.setForeground(Color.RED);
            lblUnavailable.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            
            lblUnavailable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);  // Ensure it's centered

            // Set the label size to a larger width if needed, ensuring it doesn't wrap
            lblUnavailable.setPreferredSize(new java.awt.Dimension(200, 30)); 

            // Alternatively, you can set a maximum width
            lblUnavailable.setMaximumSize(new java.awt.Dimension(200, 30));
        }

        // Add the "View Menu" button
        JButton btnViewMenu = new JButton("View Menu");
        btnViewMenu.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btnViewMenu.addActionListener(e -> loadVendorMenu(vendorEmail)); // Action to load the vendor's menu

        // Add components to the panel
        panel.add(logo);
        panel.add(lblVendor);
        panel.add(lblRating);
        panel.add(lblReview);
        panel.add(btnViewMenu);
        panel.add(lblUnavailable);

        return panel;
    }
    
    private void loadVendorMenu(String vendorEmail) {
        selectedVendorEmail = vendorEmail;
        bBackVendor.setVisible(true);
        mainMenuPanel.removeAll(); //Clear existing menu items
        mainMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 15));
        
        Vendor vendor = new Vendor(vendorEmail);
        
        String availableMethods = vendor.getAvailableMethod();
        boolean isAvailableForOrderType;

        //check if availableMethods is null
        if (availableMethods != null && !availableMethods.isEmpty()) {
            String methodsWithoutBrackets = availableMethods.substring(1, availableMethods.length() - 1);
            String[] availableMethodsArray = methodsWithoutBrackets.split(";");

            isAvailableForOrderType = Arrays.asList(availableMethodsArray).contains(orderType);
        } else {
            //if vendor have non available methods
            isAvailableForOrderType = false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/item.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 6 && details[5].equals(vendorEmail)) {
                    int itemID = Integer.parseInt(details[0]);
                    String itemName = details[1];
                    String itemType = details[2];
                    String itemPrice = details[3];
                    String imagePath = details[4];

                    JPanel itemPanel = createMenuPanel(itemID, itemName, itemType, itemPrice, imagePath, isAvailableForOrderType);
                    mainMenuPanel.add(itemPanel);
                }
            }
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading the menu file: " + e.getMessage());
        }
    }

    private JPanel createMenuPanel(int itemID, String itemName, String itemType, String itemPrice, String imagePath, boolean isAvailableForOrderType) {
        // Create the main panel
        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout(10, 10)); // BorderLayout for structured layout with gaps

        // Create inner panels for better alignment
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));

        // Add image
        JLabel image = new JLabel();
        if (imagePath != null && !imagePath.isEmpty()) {
            // Define the desired maximum width and height for the image
            int maxWidth = 200; // Desired maximum width
            int maxHeight = 150; // Desired maximum height

            java.awt.Image originalImage = new javax.swing.ImageIcon(imagePath).getImage();

            // Scale the image while preserving its aspect ratio
            int width = originalImage.getWidth(null);
            int height = originalImage.getHeight(null);

            // Calculate the scaling factor to preserve aspect ratio
            double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);

            // Apply the scaling
            int newWidth = (int) (width * scale);
            int newHeight = (int) (height * scale);

            image.setIcon(new javax.swing.ImageIcon(originalImage.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH)));
        }
        image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // Add labels
        JLabel lblName = new JLabel("Name: " + itemName);
        JLabel lblPrice = new JLabel("Price: RM" + itemPrice);
        lblName.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblPrice.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Create Add to Cart button
        JButton btnAddToCart = new JButton("Add to Cart");
        btnAddToCart.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btnAddToCart.setEnabled(isAvailableForOrderType);

        // Attach action listener to the button
        btnAddToCart.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddCartActionPerformed(evt, itemID, itemName, itemPrice); // Pass item details to the handler
                bBackVendor.setEnabled(false); //allow customer to order on current vendor once a item is added to cart
            }
        });

        // Add components to content panel
        contentPanel.add(lblName);
        contentPanel.add(lblPrice);
        contentPanel.add(btnAddToCart);

        // Add image and content panel to main panel
        panel.add(image, java.awt.BorderLayout.NORTH);
        panel.add(contentPanel, java.awt.BorderLayout.CENTER);

        // Set border and spacing
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(java.awt.Color.GRAY, 1),
            javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new java.awt.Dimension(228, 270)); // Standardize size
        panel.setMaximumSize(new java.awt.Dimension(228, 270));

        return panel;
    }
    
    public void populateCartTable() {
        if (currentOrder == null) {
            return; //ensure currentOrder is initialized
        }

        List<String[]> cartItems = currentOrder.getCart();
        DecimalFormat df = new DecimalFormat("0.00");

        DefaultTableModel model = (DefaultTableModel) tCart.getModel();
        model.setRowCount(0); //reset rows

        //displaying each cart item record
        for (String[] item : cartItems) {
            String itemID = item[0];
            String itemName = item[1];
            double unitPrice = Double.parseDouble(item[3]);
            String quantity = item[2];
            double totalPrice = Integer.parseInt(quantity) * unitPrice;

            model.addRow(new Object[]{itemName, "RM" + unitPrice, quantity, "RM" + df.format(totalPrice), itemID});
        }
        
    }
    
    private void addCartTableListener() {
        tCart.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Ensure the event is the final one
                    bSave.setEnabled(true);
                    bRemove.setEnabled(true);
                    int selectedRow = tCart.getSelectedRow();
                    if (selectedRow != -1) { // Check if a row is actually selected
                        try {
                            // Retrieve data from the selected row
                            String itemName = (String) tCart.getValueAt(selectedRow, 0); 
                            String unitPrice = (String) tCart.getValueAt(selectedRow, 1); 
                            String quantityString = (String) tCart.getValueAt(selectedRow, 2); 
                            String totalPrice = (String) tCart.getValueAt(selectedRow, 3); 
                            String itemID = (String) tCart.getValueAt(selectedRow, 4);

                            // Parse the quantity string to an integer
                            int quantityValue = Integer.parseInt(quantityString);

                            // Update the labels with the retrieved data
                            lItemName.setText(itemName);
                            lUnitPrice.setText(unitPrice);
                            sQuantity.setValue(quantityValue);
                            lTotalPrice.setText(totalPrice);

                        } catch (Exception ex) {
                            ex.printStackTrace(); 
                        }
                    } else {
                        //no row selected
                        bSave.setEnabled(false);
                        bRemove.setEnabled(false);
                        lItemName.setText("");
                        lUnitPrice.setText("");
                        sQuantity.setValue(0); 
                        lTotalPrice.setText("");
                    }
                }
            }
        });
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbVendorMenu = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        bBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tCart = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lItemName = new javax.swing.JLabel();
        lTotalPrice = new javax.swing.JLabel();
        sQuantity = new javax.swing.JSpinner();
        bCheckOut = new javax.swing.JButton();
        bSave = new javax.swing.JButton();
        bRemove = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        lUnitPrice = new javax.swing.JLabel();
        lableChooseVendor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mainMenuPanel = new javax.swing.JPanel();
        bBackVendor = new javax.swing.JButton();

        tbVendorMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbVendorMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bBack.setText("Back to Main Page");
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("FoodCarat Food Court");

        tCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Product Price", "Order Quantity", "Total Price", "itemID"
            }
        ));
        tCart.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tCart);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Cart Item");

        jLabel3.setText("Product Name:");

        jLabel6.setText("Order Quantity:");

        jLabel7.setText("Product Total Price:");

        lItemName.setText("itemName");

        lTotalPrice.setText("totalPrice");

        bCheckOut.setText("Submit Order");
        bCheckOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCheckOutActionPerformed(evt);
            }
        });

        bSave.setText("Save Changes");
        bSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSaveActionPerformed(evt);
            }
        });

        bRemove.setText("Remove from Cart");
        bRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRemoveActionPerformed(evt);
            }
        });

        jLabel5.setText("Product Price");

        lUnitPrice.setText("unitPrice");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(bSave, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(bRemove))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel5))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lItemName)
                                    .addComponent(lTotalPrice)
                                    .addComponent(sQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lUnitPrice))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 551, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lItemName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(lUnitPrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(sQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(lTotalPrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bSave)
                            .addComponent(bRemove)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bCheckOut)
                .addContainerGap())
        );

        lableChooseVendor.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        lableChooseVendor.setText("Order");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        mainMenuPanel.setBackground(new java.awt.Color(204, 255, 255));

        javax.swing.GroupLayout mainMenuPanelLayout = new javax.swing.GroupLayout(mainMenuPanel);
        mainMenuPanel.setLayout(mainMenuPanelLayout);
        mainMenuPanelLayout.setHorizontalGroup(
            mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 905, Short.MAX_VALUE)
        );
        mainMenuPanelLayout.setVerticalGroup(
            mainMenuPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(mainMenuPanel);

        bBackVendor.setText("Back to Vendor");
        bBackVendor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackVendorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel1)
                            .addComponent(lableChooseVendor))
                        .addGap(82, 82, 82)
                        .addComponent(bBack)
                        .addGap(9, 9, 9))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(bBackVendor, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lableChooseVendor)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(bBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bBackVendor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(160, Short.MAX_VALUE))
        );

        jScrollPane4.setViewportView(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 777, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bAddCartActionPerformed(java.awt.event.ActionEvent evt, int itemID, String itemName, String itemPrice) {
        // Customer input quantity
        String quantityStr = JOptionPane.showInputDialog(this, 
                "Enter quantity for " + itemName, 
                "Enter Quantity", 
                JOptionPane.QUESTION_MESSAGE);

        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Quantity cannot be empty.");
            
            //allow user to back to vendor menu only if the cart have no item
            if (currentOrder.getCart().isEmpty()) {
                bBackVendor.setEnabled(true);
            } else {
                bBackVendor.setEnabled(false);
            }
            return;
        }

        // Check for valid int
        try {
            int quantity = Integer.parseInt(quantityStr.trim());
            if (quantity > 0) {
                // Create a new Order object and add the item to the cart
                currentOrder.addItemToCart(itemID, itemName, quantity, Double.parseDouble(itemPrice));

                // Show confirmation
                JOptionPane.showMessageDialog(null, "Added " + quantity + " " + itemName + " to cart");
                bCheckOut.setEnabled(true);
                populateCartTable();
                addCartTableListener();
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a valid quantity.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for quantity.");
        }
    }
    
    private void bBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Your order will be discarded. Are you sure to proceed?");
        if (confirm == JOptionPane.YES_OPTION){
            Order order = new Order();
            order.deleteIncompleteOrder(orderID);

            //Back to the main page
            this.dispose();
            customerMain frame = new customerMain();
            frame.setVisible(true);
        }
    }//GEN-LAST:event_bBackActionPerformed

    private void bBackVendorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBackVendorActionPerformed
        mainMenuPanel.removeAll();
        loadVendors("resources/user.txt");
        bBackVendor.setVisible(false);  //hide back button
    }//GEN-LAST:event_bBackVendorActionPerformed

    private void bSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSaveActionPerformed
        int selectedRow = tCart.getSelectedRow();
        if (selectedRow != -1) {
            // Get itemID from the last column (index 4)
            String itemID = (String) tCart.getValueAt(selectedRow, 4);
            int newQuantity = (int) sQuantity.getValue(); // Get new quantity from the spinner

            if (newQuantity > 0) {
                try {
                    //ensure the quantity insert is valid
                    int parsedItemID = Integer.parseInt(itemID);
                    currentOrder.updateItemQuantity(parsedItemID, newQuantity); // Update item quantity
                    JOptionPane.showMessageDialog(null, "Quantity Updated");
                    populateCartTable(); // Refresh the table
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Item ID.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid quantity.");
            }
        }
    }//GEN-LAST:event_bSaveActionPerformed

    private void bRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveActionPerformed
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to remove selected item?");
        if (confirm == JOptionPane.YES_OPTION){    
            int selectedRow = tCart.getSelectedRow();
            if (selectedRow != -1) {
                String itemID = (String) tCart.getValueAt(selectedRow, 4); 
                currentOrder.removeItemFromCart(Integer.parseInt(itemID)); 
                JOptionPane.showMessageDialog(null, "Item removed.");
                
                //disable the checkout button and enable back to vendor button if the cart is empty
                if (currentOrder.getCart().isEmpty()) {
                    bCheckOut.setEnabled(false); 
                    bBackVendor.setEnabled(true);
                } else {
                    bCheckOut.setEnabled(true);
                    bBackVendor.setEnabled(false);
                }
                populateCartTable();
            }
        }
    }//GEN-LAST:event_bRemoveActionPerformed

    private void bCheckOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCheckOutActionPerformed
        List<String[]> cart = currentOrder.getCart(); 

        Order order = new Order(orderType, User.getSessionEmail());
        order.writeOrderDetails(orderID, cart);

        customerPayment frame = new customerPayment(orderID, orderType);
        frame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_bCheckOutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(customerViewMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(customerViewMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(customerViewMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(customerViewMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new customerViewMenu(orderID).setVisible(true);
            }
        });*/
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bBack;
    private javax.swing.JButton bBackVendor;
    private javax.swing.JButton bCheckOut;
    private javax.swing.JButton bRemove;
    private javax.swing.JButton bSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lItemName;
    private javax.swing.JLabel lTotalPrice;
    private javax.swing.JLabel lUnitPrice;
    private javax.swing.JLabel lableChooseVendor;
    private javax.swing.JPanel mainMenuPanel;
    private javax.swing.JSpinner sQuantity;
    private javax.swing.JTable tCart;
    private javax.swing.JTable tbVendorMenu;
    // End of variables declaration//GEN-END:variables
}
