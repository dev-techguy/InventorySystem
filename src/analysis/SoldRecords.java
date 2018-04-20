package analysis;

import com.toedter.calendar.JDateChooser;
import dbconnector.DBConnector;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.net.URL;
import java.sql.*;

public class SoldRecords {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/search.png");
    URL url3 = Main.class.getResource("/img/solditems.png");
    URL url4 = Main.class.getResource("/img/delete.png");

    //setting images
    ImageIcon imagesearch = new ImageIcon(url2);
    ImageIcon imageitems = new ImageIcon(url3);
    ImageIcon imagedelete = new ImageIcon(url4);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url1).getImage();
    ImageIcon iconSold = new ImageIcon(url3);
    ImageIcon iconDelete = new ImageIcon(url4);

    JLabel llogo1, llogo2, llogo3, ltotalsale, ltitle, ltotalamount, ltitle2, ldelete, ltitleDelete;
    JTextField tsearch, ttotalsale, ttotalamount, tdelete;
    JTable table;
    JDateChooser dateexpry;
    JButton bsearch, bload, bback, bdelete;

    JPanel panellogo = new JPanel(new GridBagLayout());
    JPanel panelshow = new JPanel(new GridBagLayout());
    JPanel paneldelete = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));
    JPanel panelSell = new JPanel(new BorderLayout(0, 0));

    JFrame FSoldFrame = new JFrame();

    //set variables
    String[] values2 = new String[]{"Product_Name", "Serial_Number", "Sold_Quantity", "Buying_Price", "Selling_Price", "Total Tax", "Amount", "Total", "Cash Paid", "Change Given", "Invoice_NO", "Sold On"};

    //database connectors
    Connection con;
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6 = null;
    PreparedStatement prs, prs2 = null;

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    /**
     * Function For deleting item from store
     */
    private void getProductInvoice() {
        if (tdelete.getText().equalsIgnoreCase("")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Please Enter Invoice Of the Product\nTo Delete its Record.", "Deletion Status", JOptionPane.WARNING_MESSAGE);
        } else {
            String[] option = {"Proceed", "Cancel"};
            int selloption = JOptionPane.showOptionDialog(null, "Are you sure you want to delete this record", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconDelete, option, option[1]);
            if (selloption == 0) {
                //get invoice of product
                deleteRecord(tdelete.getText());
            } else {
                //do nothing
            }
        }
    }

    /**
     * Proceed in deleting the product
     */
    private void deleteRecord(String invoice) {
        try {
            con = DBConnector.getConnection();
            String sqldelete = "DELETE FROM pastsoldrecords WHERE Invoice = '" + invoice + "'";
            prs2 = con.prepareStatement(sqldelete);
            prs2.execute();

            if (prs2 != null) {
                prs2.close();
                con.close();

                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Product Deleted Successfully", "Product Information", JOptionPane.INFORMATION_MESSAGE, iconDelete);
                tdelete.setText("");
                uploadsells();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Deletion Failed", "Deletion Status", JOptionPane.WARNING_MESSAGE);
                tdelete.setText("");

                prs2.close();
                con.close();
            }
        } catch (SQLException e) {
            xamppfailure.getCon();
        }
    }

    /**
     * Function for calculating the sum sold
     */
    private void receiveSearchData(String searchDate, String searchDetails) {
        try {
            //Database Connection
            con = DBConnector.getConnection();
            String sqlqsum1 = null;
            if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty() && tsearch.getText().equalsIgnoreCase(searchDetails) && !tsearch.getText().equalsIgnoreCase("")) {
                //sql for getting sum sold for the item search
                sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ,SUM(Tamount) STORETC FROM pastsoldrecords WHERE Mname = '" + searchDetails + "' || Mserial = '" + searchDetails + "' || Invoice ='" + searchDetails + "'";

                //Execute the sql here
                prs = con.prepareStatement(sqlqsum1);
                rs1 = prs.executeQuery();
                //JOptionPane.showMessageDialog(null, searchDate + " SEARCH ONLY " + searchDetails);
            }
            if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(searchDate) && tsearch.getText().equalsIgnoreCase("") && !((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty()) {
                //sql for getting sum sold for the item search
                sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ,SUM(Tamount) STORETC FROM pastsoldrecords WHERE Msolddate  = '" + searchDate + "'";

                //Execute the sql here
                prs = con.prepareStatement(sqlqsum1);
                rs1 = prs.executeQuery();
                // JOptionPane.showMessageDialog(null, searchDate + " DATE ONLY " + searchDetails);
            }
            if ((!((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty() && !tsearch.getText().equalsIgnoreCase("")) && (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(searchDate) && tsearch.getText().equalsIgnoreCase(searchDetails))) {
                //sql for getting sum sold for the item search
                sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ,SUM(Tamount) STORETC FROM pastsoldrecords WHERE (Mname = '" + searchDetails + "' || Mserial = '" + searchDetails + "' || Invoice ='" + searchDetails + "') && (Msolddate  = '" + searchDate + "')";

                //Execute the sql here
                prs = con.prepareStatement(sqlqsum1);
                rs1 = prs.executeQuery();
                //JOptionPane.showMessageDialog(null, searchDate + " AND " + searchDetails);
            }
            /**
             * Check if execution takes place
             * */
            if (rs1.next()) {
                double costfound = rs1.getDouble("STOREQ");
                String sstock = String.format("%.2f", costfound);
                double paymentsfound = rs1.getDouble("STORETC");
                String sspayments = String.format("%.2f", paymentsfound);

                ttotalsale.setText(sspayments);
                ttotalamount.setText(sstock);
                rs1.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, x);
            xamppfailure.getCon();
        }
    }

    /**
     * Function process and show in table
     */
    private void getToTable(String soldDate) {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        pharmacymodel.setColumnIdentifiers(values2);
        String fetchrecordOne = "SELECT * FROM pastsoldrecords WHERE Mname  = '" + tsearch.getText() + "' || Mserial  = '" + tsearch.getText() + "' || Msolddate  = '" + soldDate + "' || Invoice ='" + tsearch.getText() + "' ORDER BY ID DESC ";
        String fetchrecordTwo = "SELECT * FROM pastsoldrecords WHERE (Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' || Invoice ='" + tsearch.getText() + "') && (Msolddate  = '" + soldDate + "') ORDER BY ID DESC";
        try {
            con = DBConnector.getConnection();
            if ((!((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty() && !tsearch.getText().equalsIgnoreCase("")) && (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(soldDate) && tsearch.getText().equalsIgnoreCase(tsearch.getText()))) {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecordTwo);
                //rs = stmt.executeQuery(fetchrecordTwo);
            } else {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecordOne);
                //rs = stmt.executeQuery(fetchrecordTwo);
            }
            if (rs.next()) {
                do {
                    String cname = rs.getString("Mname");
                    String cidentity = rs.getString("Mserial");
                    double tcost = rs.getDouble("Mtotalquantity");
                    String ccost = String.format("%.2f", tcost);
                    double cpay = rs.getDouble("Bcost");
                    String spay = String.format("%.2f", cpay);
                    double stax = rs.getDouble("TotalTax");
                    String taxsum = String.format("%.2f", stax);
                    double samount = rs.getDouble("Tamount");
                    String pamount = String.format("%.2f", samount);
                    double balance = rs.getDouble("Mcost");
                    String sbalance = String.format("%.2f", balance);
                    double balance1 = rs.getDouble("Mtotalcost");
                    String sbalance1 = String.format("%.2f", balance1);
                    double bpaid = rs.getDouble("CashPaid");
                    String sbpaid = String.format("%.2f", bpaid);
                    double change = rs.getDouble("ChangePaid");
                    String schange = String.format("%.2f", change);
                    String pinvoice = rs.getString("Invoice");
                    String cedited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{cname, cidentity, ccost, spay, sbalance, taxsum, pamount, sbalance1, sbpaid, schange, pinvoice, cedited});

                } while (rs.next());
                table.setModel(pharmacymodel);
                table.setAutoCreateRowSorter(true);
                table.setFillsViewportHeight(true);
                table.revalidate();

                rs.close();
                stmt.close();
                con.close();

                /**
                 * {@link com.sun.xml.internal.bind.v2.TODO}Call the function to show sold products details
                 * */
                receiveSearchData(soldDate, tsearch.getText());
            } else {
                Toolkit.getDefaultToolkit().beep();
                table.setModel(pharmacymodel);
                JOptionPane.showMessageDialog(null, "Sorry No Data Found For" + " " + tsearch.getText() + " on date " + soldDate, "Notification", JOptionPane.INFORMATION_MESSAGE);
                uploadsells();
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }


    /**
     * Search product function
     */
    private void searchsell() {
        String medate = ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText();
        String valuesearch = tsearch.getText();
        if (valuesearch.equalsIgnoreCase("") && ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please Enter The Product ::\nTax Invoice Number\nProduct Name\nProduct Serial\nProduct Sold Date\n :: Use Both For Search", "Selling Message", JOptionPane.INFORMATION_MESSAGE, iconSold);
        } else {
            if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(medate) && tsearch.getText().equalsIgnoreCase("") && !((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase("")) {
                //pass date to the table model
                getToTable(medate);
            } else if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty() && tsearch.getText().equalsIgnoreCase(valuesearch) && !valuesearch.equalsIgnoreCase("")) {
                //pass date to the table model
                getToTable(medate);
            } else if ((((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(medate) && tsearch.getText().equalsIgnoreCase(valuesearch)) && (!valuesearch.equalsIgnoreCase("") && !((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty())) {
                //pass date to the table model
                getToTable(medate);
            }
        }
    }

    //method to upload all sold items
    private void uploadsells() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values2);
        String fetchrecord = "SELECT * FROM pastsoldrecords ORDER BY ID DESC";
        try {
            con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String cname = rs.getString("Mname");
                    String cidentity = rs.getString("Mserial");
                    double tcost = rs.getDouble("Mtotalquantity");
                    String ccost = String.format("%.2f", tcost);
                    double cpay = rs.getDouble("Bcost");
                    String spay = String.format("%.2f", cpay);
                    double stax = rs.getDouble("TotalTax");
                    String taxsum = String.format("%.2f", stax);
                    double samount = rs.getDouble("Tamount");
                    String pamount = String.format("%.2f", samount);
                    double balance = rs.getDouble("Mcost");
                    String sbalance = String.format("%.2f", balance);
                    double balance1 = rs.getDouble("Mtotalcost");
                    String sbalance1 = String.format("%.2f", balance1);
                    double bpaid = rs.getDouble("CashPaid");
                    String sbpaid = String.format("%.2f", bpaid);
                    double change = rs.getDouble("ChangePaid");
                    String schange = String.format("%.2f", change);
                    String pinvoice = rs.getString("Invoice");
                    String cedited = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{cname, cidentity, ccost, spay, sbalance, taxsum, pamount, sbalance1, sbpaid, schange, pinvoice, cedited});

                } while (rs.next());
                table.setModel(pharmacymodel);
                table.setAutoCreateRowSorter(true);
                table.setFillsViewportHeight(true);
                table.revalidate();

                rs.close();
                stmt.close();
                con.close();

                //do this
                bload.setEnabled(true);
                bsearch.setEnabled(true);
                bdelete.setEnabled(true);
            } else {
                Toolkit.getDefaultToolkit().beep();
                table.setModel(pharmacymodel);
                JOptionPane.showMessageDialog(null, "No Saved Past Sold Records\n In The Store For Analysis", "Notification", JOptionPane.ERROR_MESSAGE);
                rs.close();
                stmt.close();
                con.close();

                //do this
                bload.setEnabled(false);
                bsearch.setEnabled(false);
                bdelete.setEnabled(false);
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
            uploadsells();
        }

        //code for fetching all the sum of stock in paymentrecords
        try {
            con = DBConnector.getConnection();
            String sqlqsumall = "SELECT SUM(Mtotalquantity) STOREQ FROM pastsoldrecords";
            prs = con.prepareStatement(sqlqsumall);
            rs = prs.executeQuery();
            if (rs.next()) {
                double costfound = rs.getDouble("STOREQ");
                String sstock = String.format("%.2f", costfound);

                ttotalamount.setText(sstock);
                rs.close();
                prs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }

        //code for fetching all the sum of cost in paymentrecords
        try {
            con = DBConnector.getConnection();
            String sqlcsumall2 = "SELECT SUM(Tamount) STORETC FROM pastsoldrecords";
            prs = con.prepareStatement(sqlcsumall2);
            rs2 = prs.executeQuery();
            if (rs2.next()) {
                double paymentsfound = rs2.getDouble("STORETC");
                String sspayments = String.format("%.2f", paymentsfound);

                ttotalsale.setText(sspayments);
                rs2.close();
                prs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            JOptionPane.showMessageDialog(null, "Database Connection Failure", "Error Message", JOptionPane.ERROR_MESSAGE);
            //System.exit(0);
        }
    }

    public void pastSoldRecords() {
        //date code
        dateexpry = new JDateChooser();
        dateexpry.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateexpry.setPreferredSize(new Dimension(200, 35));
        dateexpry.setDateFormatString("yyyy-MM-dd");
        dateexpry.setFont(new Font("Tahoma", Font.PLAIN, 12));

        //tables
        table = new JTable();
        // this enables horizontal scroll barz
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        //table.setPreferredScrollableViewportSize(new Dimension(960, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.45), (int) (screenSize.height / 1.40)));
        table.revalidate();
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        llogo1 = new JLabel(imageitems);
        llogo2 = new JLabel(imageitems);
        llogo3 = new JLabel(imageitems);
        ltitle2 = new JLabel("PAST SALES ANALYSIS");
        ltitle2.setFont(new Font("Tahoma", Font.BOLD, 20));
        ltitle = new JLabel("PAST SALES ANALYSIS");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 8));
        ltitleDelete = new JLabel("DELETE RECORD");
        ltitleDelete.setFont(new Font("Tahoma", Font.BOLD, 12));
        ltotalamount = new JLabel("Quantity Sold[Numbers]");
        ltotalamount.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalamount = new JTextField(15);
        ttotalamount.setBackground(Color.LIGHT_GRAY);
        ttotalamount.setFont(new Font("Tahoma", Font.PLAIN, 13));
        // ttotalamount.setEditable(false);
        ltotalsale = new JLabel("Total Sales[KES]");
        ltotalsale.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ttotalsale = new JTextField(15);
        ttotalsale.setBackground(Color.LIGHT_GRAY);
        ttotalsale.setFont(new Font("Tahoma", Font.PLAIN, 13));
        // ttotalsale.setEditable(false);
        ldelete = new JLabel("Enter Invoice Number");
        ldelete.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tdelete = new JTextField(15);
        tdelete.setFont(new Font("Tahoma", Font.PLAIN, 13));
        // ttotalsale.setEditable(false);
        tsearch = new JTextField(20);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Record By Name/Serial Number/Tax Invoice Number");

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Past Record");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("RELOAD PAST SALES");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("reload past sales");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bdelete = new JButton(imagedelete);
        bdelete.setBackground(Color.RED.brighter());
        bdelete.setToolTipText("delete sold product record");
        bdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bdelete.setFont(new Font("Tahoma", Font.BOLD, 13));
        bback = new JButton("BACK");
        bback.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));

        //setting paneltable
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 50, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panellogo.add(bback, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 0, 0);
        panellogo.add(llogo1, v);
        v.gridy++;
        panellogo.add(ltitle, v);
        panellogo.setBorder(new TitledBorder(""));

        //paneltable
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltable.add(ltitle2, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bload, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 326);
        paneltable.add(dateexpry, v);
        v.insets = new Insets(0, 0, 0, 53);
        paneltable.add(tsearch, v);
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bsearch, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        paneltable.add(scrollPane, v);
        paneltable.setBorder(new TitledBorder(""));
        paneltable.revalidate();

        //panel show
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelshow.add(llogo2, v);
        v.gridy++;
        panelshow.add(ltotalamount, v);
        v.gridy++;
        panelshow.add(ttotalamount, v);
        v.gridy++;
        panelshow.add(ltotalsale, v);
        v.gridy++;
        panelshow.add(ttotalsale, v);
        panelshow.setBorder(new TitledBorder("Past Sales Analysis"));
        panelshow.revalidate();

        //paneldelete
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 20, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneldelete.add(ltitleDelete, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 10, 0);
        paneldelete.add(ldelete, v);
        v.gridy++;
        paneldelete.add(tdelete, v);
        v.gridy++;
        paneldelete.add(bdelete, v);
        v.gridy++;
        paneldelete.add(llogo3, v);
        paneldelete.setBorder(new TitledBorder("Delete Record"));
        paneldelete.revalidate();

        //panelSell
        panelSell.add("North", panelshow);
        panelSell.add("Center", paneldelete);
        panelSell.setBorder(new TitledBorder(""));
        panelSell.revalidate();


        //panelmain
        panelmain.add("West", panellogo);
        panelmain.add("Center", paneltable);
        panelmain.add("East", panelSell);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //method for uploading
        uploadsells();

        //setting frame
        FSoldFrame = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        FSoldFrame.setUndecorated(true);
        FSoldFrame.setIconImage(iconimage);
        FSoldFrame.add(panelmain);
        FSoldFrame.setVisible(true);
        //Fstore.setSize(1400, 780);
        FSoldFrame.setSize(screenSize.width, screenSize.height);
        FSoldFrame.revalidate();
        FSoldFrame.pack();
        FSoldFrame.revalidate();
        FSoldFrame.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        FSoldFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        FSoldFrame.addWindowStateListener(e -> {
            // minimized
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                FSoldFrame.revalidate();
                FSoldFrame.pack();
                FSoldFrame.revalidate();
                FSoldFrame.setLocationRelativeTo(null);
            } // maximized
            else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                FSoldFrame.revalidate();
                FSoldFrame.pack();
                FSoldFrame.revalidate();
                FSoldFrame.setLocationRelativeTo(null);
            }
        });

        //methods start
        bdelete.addActionListener(e -> {
            getProductInvoice();
        });
        bsearch.addActionListener(e -> {
            searchsell();
        });
        bload.addActionListener(e -> {
            uploadsells();
        });
        bback.addActionListener(e -> {
            FSoldFrame.setVisible(false);
            AnalysisType antype = new AnalysisType();
            antype.ChooseSection();
        });

    }
}
