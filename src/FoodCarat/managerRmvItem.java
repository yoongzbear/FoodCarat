/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author User
 */
public class managerRmvItem extends javax.swing.JFrame {
    private String userFile = "resources/user.txt";
    Item item = new Item();
    User user = new User();
    String imagePath = "";

    //can create manager object
    /**
     * Creates new form managerRmvItem
     */
    public managerRmvItem() {
        initComponents();
        setLocationRelativeTo(null);
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        setLocationRelativeTo(null);
        
        //display items in table
        displayItems();
        foodBox.setSelected(true);
        beverageBox.setSelected(true);
        dessertBox.setSelected(true);
        setBox.setSelected(true);
                
        deleteBtn.setEnabled(false);
        populateVendorNames(vendorlistcbx, true);
        
        //set size of photoLabel
        photoLabel.setPreferredSize(new Dimension(175, 164)); 
        photoLabel.setMinimumSize(new Dimension(175, 164));
        photoLabel.setMaximumSize(new Dimension(175, 164));
        getContentPane().setBackground(new Color(252, 204, 196));
    }    
    
    private Map<String, String> vendorNameToEmailMap = new HashMap<>();

    public void populateVendorNames(JComboBox<String> vendorListCbx, boolean isAvailableOnly) {
        // Get all items from the file
        List<String[]> allItems = item.getAllItems(isAvailableOnly);

        // Clear the map and combo box
        vendorNameToEmailMap.clear();
        vendorListCbx.removeAllItems();
        
        vendorListCbx.addItem("Please select a vendor");        

        for (String[] itemData : allItems) {
            if (itemData.length > 5) {
                String vendorEmail = itemData[5]; // Get vendor email
                String[] vendorData = user.getUserInfo(vendorEmail); // Search for vendor data
                if (vendorData != null && vendorData.length > 1) {
                    String vendorName = vendorData[1]; // Get the vendor name
                    if (!vendorNameToEmailMap.containsKey(vendorName)) {
                        vendorNameToEmailMap.put(vendorName, vendorEmail); // Map vendor name to email
                        vendorListCbx.addItem(vendorName); // Add vendor name to the combo box
                    }
                }
            }
        }
    }
    
    //reset details section
    public void resetDetails() {
        //reset details section
        photoLabel.setIcon(null);
        photoLabel.setText("Item Photo");
        idLabel.setText("ID");
        itemNameTxt.setText("");
        typeTxt.setText("");
        itemPriceTxt.setText("");
        deleteBtn.setEnabled(false);
    }
    
    //display all items
    public void displayItems() {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        List<String[]> allItems = item.getAllItems(true); //items with available status
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
        for (String[] itemData : allItems) {
            String itemID = itemData[0];
            String itemName = itemData[1];
            String itemType = itemData[2];
            String itemPrice = itemData[3];
            String itemImgPath = itemData[4];
            String vendorEmail = itemData[5];
            //Get the name of the vendor using the email
            String [] vendorData = user.getUserInfo(vendorEmail);
            String vendorName = vendorData[1];
            //image icon
            ImageIcon itemImage = new ImageIcon(itemImgPath);
            Image img = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            itemImage = new ImageIcon(img);

            model.addRow(
                    new Object[]{index, itemID, itemImage, itemName, itemType, itemPrice, vendorName});
            index++;
        }
        //render image column
        itemTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel((ImageIcon) value);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
    }
    
    //display items based on id
    public void displayItems(int id) {
        //display details
        String[] details = item.itemData(id);        
        idLabel.setText(details[0].trim());
        itemNameTxt.setText(details[1].trim());
        typeTxt.setText(details[2].trim());
        itemPriceTxt.setText(details[3].trim());
        
        String vendorEmail = details[5];
        String[] vendorDetails = user.getUserInfo(vendorEmail);
        String vendorName = vendorDetails[1].trim();
        venNameLabel.setText(vendorName);
        
        //photo
        imagePath = details[4].trim();
        ImageIcon itemImage = new ImageIcon(imagePath);
        Image resizedImage = itemImage.getImage().getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
        photoLabel.setText(""); //clear the label
        photoLabel.setIcon(new ImageIcon(resizedImage));        
    }
    
    //display items based on check boxes
    public void displayItemsFilter(String[] filter) {      
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        List<String[]> allItems = item.getAllItems(true); //items with available status only
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
        
        for (String[] itemData : allItems) {
            String itemID = itemData[0];
            String itemName = itemData[1];
            String itemType = itemData[2];
            String itemPrice = itemData[3];
            String itemImgPath = itemData[4];
            String vendorEmail = itemData[5];
            
            String [] vendorData = user.getUserInfo(vendorEmail);
            String vendorName = vendorData[1];
            
            //check if item type matches the filter
            boolean isFiltered = false;
            for (String filterType : filter) {
                if (itemType.equals(filterType)) {
                    isFiltered = true;
                    break;  // Exit loop once a match is found
                }
            }

            if (isFiltered) {
                //image icon
                ImageIcon itemImage = new ImageIcon(itemImgPath);
                Image img = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                itemImage = new ImageIcon(img);

                model.addRow(
                        new Object[]{index, itemID, itemImage, itemName, itemType, itemPrice, vendorName});
                index++;
            }
        }
        //render image column
        itemTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel((ImageIcon) value);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
    }
    
    //display items based on search bar
    public void displayItemsSearch(String searchItem) {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        List<String[]> allItems = item.getAllItems(searchItem, true); //items with available status
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
  
        for (String[] itemData : allItems) {
            String itemID = itemData[0];
            String itemName = itemData[1];
            String itemType = itemData[2];
            String itemPrice = itemData[3];
            String itemImgPath = itemData[4];

            String [] vendorData = user.getUserInfo(searchItem);
            String vendorName = vendorData[1];
            
            //image icon
            ImageIcon itemImage = new ImageIcon(itemImgPath);
            Image img = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            itemImage = new ImageIcon(img);

            model.addRow(
                    new Object[]{index, itemID, itemImage, itemName, itemType, itemPrice, vendorName});
            index++;

        }
        //render image column
        itemTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                if (value instanceof ImageIcon) {
                    JLabel label = new JLabel((ImageIcon) value);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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

        filterBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        searchBtn = new javax.swing.JButton();
        menuBtn = new javax.swing.JButton();
        revertBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        foodBox = new javax.swing.JCheckBox();
        viewBtn = new javax.swing.JButton();
        beverageBox = new javax.swing.JCheckBox();
        dessertBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        photoLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        itemNameTxt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        itemPriceTxt = new javax.swing.JTextField();
        deleteBtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        idLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        venNameLabel = new javax.swing.JLabel();
        typeTxt = new javax.swing.JLabel();
        setBox = new javax.swing.JCheckBox();
        vendorlistcbx = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        filterBtn.setText("Filter");
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBtnActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Menu Items");

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        revertBtn.setText("Revert Table");
        revertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertBtnActionPerformed(evt);
            }
        });

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "ID", "Photo", "Name", "Type", "Price (RM)", "Vendor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(itemTable);

        foodBox.setText("Food");

        viewBtn.setText("View Details");
        viewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewBtnActionPerformed(evt);
            }
        });

        beverageBox.setText("Beverage");

        dessertBox.setText("Dessert");

        jLabel2.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel2.setText("Item Details");

        photoLabel.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        photoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        photoLabel.setText("Item Photo");
        photoLabel.setMaximumSize(new java.awt.Dimension(175, 164));
        photoLabel.setMinimumSize(new java.awt.Dimension(175, 164));
        photoLabel.setPreferredSize(new java.awt.Dimension(175, 164));

        jLabel3.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel3.setText("Item Name:");

        jLabel5.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel5.setText("Item Type:");

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel6.setText("Item Price (RM):");

        deleteBtn.setText("Delete Item");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel7.setText("Item ID:");

        idLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        idLabel.setText("ID");

        jLabel4.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel4.setText("Vendor:");

        venNameLabel.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        venNameLabel.setText("Name");

        typeTxt.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N
        typeTxt.setText("Type");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(itemNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(itemPriceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(idLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(typeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 26, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(deleteBtn))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(photoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(venNameLabel)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(venNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(photoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(idLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(itemNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(typeTxt))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(itemPriceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(deleteBtn)
                .addGap(13, 13, 13))
        );

        setBox.setText("Set");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(388, 388, 388)
                        .addComponent(menuBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 5, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(revertBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(viewBtn))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(foodBox, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(beverageBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dessertBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(setBox)
                                .addGap(18, 18, 18)
                                .addComponent(filterBtn)
                                .addGap(46, 46, 46)
                                .addComponent(vendorlistcbx, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBtn)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(menuBtn)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 19, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchBtn)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterBtn)
                        .addComponent(foodBox)
                        .addComponent(beverageBox)
                        .addComponent(dessertBox)
                        .addComponent(setBox)
                        .addComponent(vendorlistcbx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(viewBtn)
                            .addComponent(revertBtn)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed
        //filter table based on the selected ticked box
        //get checked boxes
        String[] selectedFilter = new String[3];
        int index = 0;

        if (foodBox.isSelected()) {
            selectedFilter[index++] = "Food";
        }
        if (beverageBox.isSelected()) {
            selectedFilter[index++] = "Beverage";
        }
        if (dessertBox.isSelected()) {
            selectedFilter[index++] = "Dessert";
        }
        if (setBox.isSelected()) {
            selectedFilter[index++] = "Set";
        }

        selectedFilter = Arrays.copyOf(selectedFilter, index); //adjust the size of array
        displayItemsFilter(selectedFilter);
    }//GEN-LAST:event_filterBtnActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        // Get the selected name from the combo box
        String selectedVendorName = (String) vendorlistcbx.getSelectedItem();

        if ("Please select a vendor".equals(selectedVendorName)) {
            JOptionPane.showMessageDialog(null, "Please select a valid vendor.", "Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Retrieve the corresponding email
        if (vendorNameToEmailMap.containsKey(selectedVendorName)) {
            String searchEmail = vendorNameToEmailMap.get(selectedVendorName);

            // Pass the email to the search function
            displayItemsSearch(searchEmail);
        } else {
            JOptionPane.showMessageDialog(null, "Vendor not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_searchBtnActionPerformed

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new managerMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void revertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertBtnActionPerformed
        displayItems();
        foodBox.setSelected(false);
        beverageBox.setSelected(false);
        dessertBox.setSelected(false);
        setBox.setSelected(false);
    }//GEN-LAST:event_revertBtnActionPerformed

    private void viewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBtnActionPerformed
        //display selected row of item in the table
        int selectedRow = itemTable.getSelectedRow();

        //have validation to "choose an item in the table"
        if (selectedRow >= 0) {
            Object id = itemTable.getModel().getValueAt(selectedRow, 1);
            int selectID = Integer.parseInt((String) id);
            displayItems(selectID);
            deleteBtn.setEnabled(true);
        } else {
            //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_viewBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        //delete the selected item off the item text file
        int selectedRow = itemTable.getSelectedRow();

        //have validation to "choose an item in the table"
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to delete this item?", "Delete Item", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(idLabel.getText());
                item.deleteItem(id, "manager"); 
                displayItems();
                resetDetails();
            }
        } else {
            //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to delete item.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

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
            java.util.logging.Logger.getLogger(managerRmvItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(managerRmvItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(managerRmvItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(managerRmvItem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new managerRmvItem().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox beverageBox;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JCheckBox dessertBox;
    private javax.swing.JButton filterBtn;
    private javax.swing.JCheckBox foodBox;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField itemNameTxt;
    private javax.swing.JTextField itemPriceTxt;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel photoLabel;
    private javax.swing.JButton revertBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JCheckBox setBox;
    private javax.swing.JLabel typeTxt;
    private javax.swing.JLabel venNameLabel;
    private javax.swing.JComboBox<String> vendorlistcbx;
    private javax.swing.JButton viewBtn;
    // End of variables declaration//GEN-END:variables
}
