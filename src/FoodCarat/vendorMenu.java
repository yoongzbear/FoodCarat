/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package FoodCarat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mastu
 */
public class vendorMenu extends javax.swing.JFrame {

    String imagePath = "";
    File imageFile = null;
    private String email = User.getSessionEmail();
    Vendor vendor = new Vendor(email);
    boolean edit = true;
    
    /**
     * Creates new form vendorMenu
     */
    public vendorMenu() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(186,85,211)); //setting background color of frame
        setLocationRelativeTo(null);
        
        //display items in table
        displayItems();
        foodBox.setSelected(true);
        beverageBox.setSelected(true);
        dessertBox.setSelected(true);
        setBox.setSelected(true);
        
        enableTextField();
        typeBox.setVisible(false); //hide the combo box for type 
        
        //set the placeholder for search box
        GuiUtility.setPlaceholder(searchTxt, "Search item name");
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        
        //set size of photoLabel
        photoLabel.setPreferredSize(new Dimension(175, 164)); 
        photoLabel.setMinimumSize(new Dimension(175, 164));
        photoLabel.setMaximumSize(new Dimension(175, 164));
    }
    
    //reset details section
    public void resetDetails() {
        //reset details section
        photoLabel.setIcon(null);
        photoLabel.setText("Item Photo");
        idLabel.setText("");
        itemNameTxt.setText("");
        typeLabel.setText("");
        itemPriceTxt.setText("");
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
    }

    //display all items
    public void displayItems() {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        List<String[]> allItems = new Item().getAllItems(email, true); //items with available status
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
        for (String[] itemData : allItems) {
            String itemID = itemData[0];
            String itemName = itemData[1];
            String itemType = itemData[2];
            String itemPrice = itemData[3];
            String itemImgPath = itemData[4];

            //image icon
            ImageIcon itemImage = new ImageIcon(itemImgPath);
            Image img = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            itemImage = new ImageIcon(img);

            model.addRow(new Object[]{index, itemID, itemImage, itemName, itemType, itemPrice});
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
        String[] details = new Item().itemData(id);
        idLabel.setText(details[0].trim());
        itemNameTxt.setText(details[1].trim());
        typeBox.setSelectedItem(details[2].trim());
        typeLabel.setText(details[2].trim());
        itemPriceTxt.setText(details[3].trim());

        //photo
        imagePath = details[4].trim();
        ImageIcon itemImage = new ImageIcon(imagePath);
        Image resizedImage = itemImage.getImage().getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
        photoLabel.setText(""); //clear the label
        photoLabel.setIcon(new ImageIcon(resizedImage));

        deleteBtn.setEnabled(true);
        editBtn.setEnabled(true);
    }

    //display items based on check boxes
    public void displayItemsFilter(String[] filter) {      
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        List<String[]> allItems = new Item().getAllItems(email, true); //item is available 
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);
        
        for (String[] itemData : allItems) {
            String itemID = itemData[0];
            String itemName = itemData[1];
            String itemType = itemData[2];
            String itemPrice = itemData[3];
            String itemImgPath = itemData[4];

            //check if item type matches the filter
            boolean isFiltered = false;
            for (String filterType : filter) {
                if (itemType.equals(filterType)) {
                    isFiltered = true;
                    break;  
                }
            }

            if (isFiltered) {
                //image icon
                ImageIcon itemImage = new ImageIcon(itemImgPath);
                Image img = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                itemImage = new ImageIcon(img);

                model.addRow(new Object[]{index, itemID, itemImage, itemName, itemType, itemPrice});
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
        List<String[]> searchedItems = new Item().searchItems(email, searchItem);
        int index = 1;
        model.setRowCount(0);
        itemTable.setRowHeight(100);

        if (searchedItems.isEmpty()) { //if no item matches search
            JOptionPane.showMessageDialog(null, "No items found for '" + searchItem + "'.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            displayItems();
            return; 
        }

        for (String[] itemData : searchedItems) {
            String itemID = itemData[0];
            String itemName = itemData[1];
            String itemType = itemData[2];
            String itemPrice = itemData[3];
            String itemImgPath = itemData[4];

            //image icon
            ImageIcon itemImage = new ImageIcon(itemImgPath);
            Image img = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            itemImage = new ImageIcon(img);

            model.addRow(new Object[]{index, itemID, itemImage, itemName, itemType, itemPrice});
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

    public void enableTextField() { //enable text fields when click edit details
        if (edit == false) {
            //when click to edit
            edit = true;
            itemNameTxt.setEditable(true);
            typeBox.setEnabled(true);
            itemPriceTxt.setEditable(true);
            editImageBtn.setEnabled(true);
            typeLabel.setVisible(false);
            typeBox.setVisible(true);
        } else if (edit == true) {
            edit = false;
            itemNameTxt.setEditable(false);
            typeBox.setEnabled(false);
            itemPriceTxt.setEditable(false);
            editImageBtn.setEnabled(false);
            typeLabel.setVisible(true);
            typeLabel.setText("");
            typeBox.setVisible(false);
        }
    }

    //validate price in text field
    private boolean validatePrice(String priceText) {
        try {
            double price = Double.parseDouble(priceText);
            return price >= 0;
        } catch (NumberFormatException e) {
            return false; 
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        menuBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        viewBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        photoLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        itemNameTxt = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        typeBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        itemPriceTxt = new javax.swing.JTextField();
        editBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        editImageBtn = new javax.swing.JButton();
        idLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        filterBtn = new javax.swing.JButton();
        searchTxt = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        revertBtn = new javax.swing.JButton();
        foodBox = new javax.swing.JCheckBox();
        beverageBox = new javax.swing.JCheckBox();
        dessertBox = new javax.swing.JCheckBox();
        setBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Cooper Black", 0, 36)); // NOI18N
        jLabel1.setText("Menu Items");

        menuBtn.setText("Main Menu");
        menuBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnActionPerformed(evt);
            }
        });

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "ID", "Photo", "Name", "Type", "Price (RM)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(itemTable);
        if (itemTable.getColumnModel().getColumnCount() > 0) {
            itemTable.getColumnModel().getColumn(0).setResizable(false);
            itemTable.getColumnModel().getColumn(0).setPreferredWidth(10);
            itemTable.getColumnModel().getColumn(1).setResizable(false);
            itemTable.getColumnModel().getColumn(1).setPreferredWidth(10);
            itemTable.getColumnModel().getColumn(2).setResizable(false);
            itemTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            itemTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            itemTable.getColumnModel().getColumn(4).setResizable(false);
            itemTable.getColumnModel().getColumn(4).setPreferredWidth(50);
            itemTable.getColumnModel().getColumn(5).setResizable(false);
            itemTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        viewBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        viewBtn.setText("View Details");
        viewBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewBtnActionPerformed(evt);
            }
        });

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

        typeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Food", "Beverage", "Dessert", "Set" }));

        jLabel6.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel6.setText("Item Price (RM):");

        editBtn.setText("Edit Details");
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        deleteBtn.setText("Delete Item");
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Cooper Black", 0, 18)); // NOI18N
        jLabel7.setText("Item ID:");

        editImageBtn.setText("Replace Image");
        editImageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editImageBtnActionPerformed(evt);
            }
        });

        idLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        typeLabel.setFont(new java.awt.Font("Cooper Black", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(photoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(editImageBtn)))
                        .addGap(0, 345, Short.MAX_VALUE))
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
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(typeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(editBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deleteBtn)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(photoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(editImageBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                    .addComponent(typeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLabel))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(itemPriceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editBtn)
                    .addComponent(deleteBtn))
                .addGap(13, 13, 13))
        );

        addBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        addBtn.setText("Add Item");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        filterBtn.setText("Filter");
        filterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBtnActionPerformed(evt);
            }
        });

        searchTxt.setText("Search Item Name");
        searchTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTxtActionPerformed(evt);
            }
        });

        searchBtn.setText("Search");
        searchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBtnActionPerformed(evt);
            }
        });

        revertBtn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        revertBtn.setText("Revert Table");
        revertBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertBtnActionPerformed(evt);
            }
        });

        foodBox.setText("Food");

        beverageBox.setText("Beverage");

        dessertBox.setText("Dessert");

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
                        .addComponent(menuBtn)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 5, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(revertBtn)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(viewBtn))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 712, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(searchBtn)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(397, 397, 397)
                                .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 39, Short.MAX_VALUE))))
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
                        .addGap(0, 18, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(searchTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(searchBtn)
                        .addComponent(addBtn))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterBtn)
                        .addComponent(foodBox)
                        .addComponent(beverageBox)
                        .addComponent(dessertBox)
                        .addComponent(setBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(viewBtn)
                            .addComponent(revertBtn)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 18, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        new vendorAddItem().setVisible(true);
        dispose();
    }//GEN-LAST:event_addBtnActionPerformed

    private void menuBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnActionPerformed
        new vendorMain().setVisible(true);
        dispose();
    }//GEN-LAST:event_menuBtnActionPerformed

    private void viewBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewBtnActionPerformed
        //display selected row of item in the table
        int selectedRow = itemTable.getSelectedRow();
        
        //have validation to "choose an item in the table"
        if (selectedRow >= 0) {            
            Object id = itemTable.getModel().getValueAt(selectedRow, 1);
            int selectID = Integer.parseInt(id.toString());
            displayItems(selectID);
        } else {
            //no row is selected
            JOptionPane.showMessageDialog(null, "Please select a row to view details.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_viewBtnActionPerformed

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed
        //filter table based on the selected ticked box
        String[] selectedFilter = new String[4];
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

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        //enable editing text field
        enableTextField();
        String id = idLabel.getText().trim();
        String name = itemNameTxt.getText().trim();
        String type = typeBox.getSelectedItem().toString().trim();
        String priceText = itemPriceTxt.getText().trim();
        double price;
        
        if (editBtn.getText().equals("Edit Details")) {
            editBtn.setText("Save Details");
        } else {
            enableTextField();
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to edit the item's information?", "Edit Item", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                //validate if fields are empty
                if (name.isEmpty() || type.equals("Select Type") || priceText.isEmpty() || imagePath.equals("")) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Incomplete Submission", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //validate price field
                if (!validatePrice(priceText)) {
                    JOptionPane.showMessageDialog(null, "Price must be a numerical value.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    price = Double.parseDouble(priceText);
                }
   
                //upload image into menu folder
                String newImagePath = new Item().uploadImage(imagePath);
                if (newImagePath == null) {
                    JOptionPane.showMessageDialog(null, "Failed to save the image.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else {
                    //function to update items
                    Item updateItem = new Item(Integer.parseInt(id), name, type, price, newImagePath, email, "available");
                    updateItem.editItem();

                    // Ensure fields and buttons are disabled after saving
                    enableTextField(); // Disable input fields and editImageBtn

                    editBtn.setText("Edit Details");
                    displayItems();
                    resetDetails();
                }
            } if (confirm == JOptionPane.NO_OPTION) {
                enableTextField();
                editBtn.setText("Save Details");
            }
        }
        
    }//GEN-LAST:event_editBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        //delete the selected item off the item text file
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure to delete this item?", "Delete Item", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idLabel.getText());
            boolean isDeleted = new Item().deleteItem(id, "vendor");
            if (isDeleted) {
                JOptionPane.showMessageDialog(null, "Item successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete the item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            displayItems();
            resetDetails();
        }
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void editImageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editImageBtnActionPerformed
        //display file chooser
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("PNG JPG AND JPEG", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(fileFilter);

        int load = fileChooser.showOpenDialog(null);
        if (load == fileChooser.APPROVE_OPTION) {
            imageFile = fileChooser.getSelectedFile();

            //display preview of image using path of local storage
            imagePath = imageFile.getAbsolutePath();
            ImageIcon preview = new ImageIcon(imagePath);
            Image resizedImage = preview.getImage().getScaledInstance(photoLabel.getWidth(), photoLabel.getHeight(), Image.SCALE_SMOOTH);
            photoLabel.setText(""); //clear the label
            photoLabel.setIcon(new ImageIcon(resizedImage));
        }
    }//GEN-LAST:event_editImageBtnActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        //search based on item name
        String searchItem = searchTxt.getText();
        if (searchItem.equals("Search item name") || searchItem.equals("")) { //if vendor doesn't enter any input in the search box
            JOptionPane.showMessageDialog(null, "Please enter item name to search.", "Alert", JOptionPane.WARNING_MESSAGE);
            displayItems();
        } else {
            displayItemsSearch(searchItem);
        }           
    }//GEN-LAST:event_searchBtnActionPerformed

    private void revertBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertBtnActionPerformed
        displayItems();
        GuiUtility.setPlaceholder(searchTxt, "Search item name");
        foodBox.setSelected(true);
        beverageBox.setSelected(true);
        dessertBox.setSelected(true);
        setBox.setSelected(true);
    }//GEN-LAST:event_revertBtnActionPerformed

    private void searchTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTxtActionPerformed
        searchBtn.doClick();
    }//GEN-LAST:event_searchTxtActionPerformed

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
            java.util.logging.Logger.getLogger(vendorMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(vendorMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(vendorMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(vendorMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vendorMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JCheckBox beverageBox;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JCheckBox dessertBox;
    private javax.swing.JButton editBtn;
    private javax.swing.JButton editImageBtn;
    private javax.swing.JButton filterBtn;
    private javax.swing.JCheckBox foodBox;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField itemNameTxt;
    private javax.swing.JTextField itemPriceTxt;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton menuBtn;
    private javax.swing.JLabel photoLabel;
    private javax.swing.JButton revertBtn;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchTxt;
    private javax.swing.JCheckBox setBox;
    private javax.swing.JComboBox<String> typeBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton viewBtn;
    // End of variables declaration//GEN-END:variables
}
