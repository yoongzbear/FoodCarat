/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.FlowLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author ASUS
 */
public class customerViewMenu extends javax.swing.JFrame {

    /**
     * Creates new form customerViewMenu
     */
    private int orderID;
    
    public customerViewMenu(int orderID) {
        this.orderID = orderID;
        initComponents();
        loadVendors("resources/vendor.txt");
    }
    
    private void loadVendors(String vendorFilePath) {
        mainMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 15));
        try (BufferedReader reader = new BufferedReader(new FileReader(vendorFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 5) {
                    String email = details[0];
                    String cuisine = details[1];
                    String logoPath = details[2];
                    String orderTypes = details[3];
                    String credit = details[4];

                    JPanel vendorPanel = createVendorPanel(email, cuisine, logoPath, orderTypes, credit);
                    mainMenuPanel.add(vendorPanel);
                }
            }
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading the vendor file: " + e.getMessage());
        }
    }

    private JPanel createVendorPanel(String email, String cuisine, String logoPath, String orderTypes, String credit) {
        // Create a new JPanel with a vertical BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setPreferredSize(new java.awt.Dimension(228, 250)); // Standardize size
        panel.setMaximumSize(new java.awt.Dimension(228, 250));
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
        JLabel lblCuisine = new JLabel("Cuisine: " + cuisine);
        lblCuisine.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel lblOrderTypes = new JLabel("Available: " + orderTypes);
        lblOrderTypes.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel lblCredit = new JLabel("Credit: " + credit);
        lblCredit.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Add the "View Menu" button
        JButton btnViewMenu = new JButton("View Menu");
        btnViewMenu.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        btnViewMenu.addActionListener(e -> loadVendorMenu(email)); // Action to load the vendor's menu

        // Add components to the panel
        panel.add(logo);
        panel.add(lblCuisine);
        panel.add(lblOrderTypes);
        panel.add(lblCredit);
        panel.add(btnViewMenu);

        return panel;
    }
    
    private void loadVendorMenu(String vendorEmail) {
        mainMenuPanel.removeAll(); // Clear existing menu items
        mainMenuPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 15));

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/item.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length >= 6 && details[5].equals(vendorEmail)) {
                    String itemName = details[1];
                    String itemType = details[2];
                    String itemPrice = details[3];
                    String imagePath = details[4];

                    JPanel itemPanel = createMenuPanel(itemName, itemType, itemPrice, imagePath);
                    mainMenuPanel.add(itemPanel);
                }
            }
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading the menu file: " + e.getMessage());
        }
    }

    private JPanel createMenuPanel(String itemName, String itemType, String itemPrice, String imagePath) {
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
        JLabel lblType = new JLabel("Type: " + itemType);
        JLabel lblPrice = new JLabel("Price: RM" + itemPrice);
        lblName.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblType.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblPrice.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Add button
        JButton btnAddToCart = new JButton("Add to Cart");
        btnAddToCart.addActionListener(e -> addToCart(itemName, itemPrice));
        btnAddToCart.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        // Add components to content panel
        contentPanel.add(lblName);
        contentPanel.add(lblType);
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

    private void addToCart(String itemName, String itemPrice) {
        // Logic for adding the item to the cart
        JOptionPane.showMessageDialog(this, "Added " + itemName + " to cart for $" + itemPrice);
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
        bAction = new javax.swing.JButton();
        bCart = new javax.swing.JButton();
        bBack = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lableChooseVendor = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        mainMenuPanel = new javax.swing.JPanel();

        tbVendorMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbVendorMenu);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bAction.setText("View Vendor");

        bCart.setText("View Cart");

        bBack.setText("Back to Main Page");
        bBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBackActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("FoodCarat Food Court");

        lableChooseVendor.setFont(new java.awt.Font("Cooper Black", 0, 24)); // NOI18N
        lableChooseVendor.setText("Choose Vendor");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(250, 250, 250)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel1)
                            .addComponent(lableChooseVendor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bBack))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 31, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(bCart, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bAction, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 886, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(bBack)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lableChooseVendor)
                .addGap(17, 17, 17)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bAction)
                    .addComponent(bCart))
                .addGap(8, 8, 8))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JButton bAction;
    private javax.swing.JButton bBack;
    private javax.swing.JButton bCart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lableChooseVendor;
    private javax.swing.JPanel mainMenuPanel;
    private javax.swing.JTable tbVendorMenu;
    // End of variables declaration//GEN-END:variables
}
