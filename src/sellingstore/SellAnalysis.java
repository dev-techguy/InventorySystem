package sellingstore;

import com.toedter.calendar.JDateChooser;
import dbconnector.DBConnector;
import jdk.nashorn.internal.scripts.JO;
import login.Sections;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.net.URL;
import java.sql.*;
import javax.swing.text.JTextComponent;

public class SellAnalysis {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/search.png");
    URL url3 = Main.class.getResource("/img/solditems.png");

    //setting images
    ImageIcon imagesearch = new ImageIcon(url2);
    ImageIcon imageitems = new ImageIcon(url3);

    //setting ma default image icon to my frames
    Image iconimage = new ImageIcon(url1).getImage();
    ImageIcon iconSold = new ImageIcon(url3);
    ImageIcon iconsell = new ImageIcon(url3);

    JLabel llogo1, llogo2, llogo3, ltotalsale, ltitle, ltotalamount, ltitle2, lreverse, ltitleReverse;
    JTextField tsearch, ttotalsale, ttotalamount, treverse;
    JTable table;
    JDateChooser dateexpry;
    JButton bsearch, bload, bback, breverse;

    JPanel panellogo = new JPanel(new GridBagLayout());
    JPanel panelshow = new JPanel(new GridBagLayout());
    JPanel panelReverse = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));
    JPanel panelSell = new JPanel(new BorderLayout(0, 0));

    JFrame Fsells = new JFrame();

    //set variables
    String[] values2 = new String[]{"Product_Name", "Serial_Number", "Sold_Quantity", "Buying_Price", "Selling_Price", "Total Tax", "Amount", "Total", "Cash Paid", "Change Given", "Invoice_NO", "Sold On"};
    String productSerial;
    double productQuantity, productTax, productAmount;

    //database connectors
    Connection con;
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6 = null;
    PreparedStatement prs, prs2 = null;

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    /**
     * Receive Invoice fro ui
     */
    private void getInvoice() {
        if (treverse.getText().equalsIgnoreCase("")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Please Enter Invoice Of the Product\nTo reverse it's transaction.", "Transaction Status", JOptionPane.WARNING_MESSAGE);
        } else {
            String[] option = {"Proceed", "Cancel"};
            int selloption = JOptionPane.showOptionDialog(null, "Are you sure you want to reverse the\ntransaction.  ", "Transaction Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconsell, option, option[1]);
            if (selloption == 0) {
                //get invoice of product
                reverseTransaction(treverse.getText());
            } else {
                //do nothing
            }
        }
    }

    /**
     * Function for fetching sells sold
     */
    private void reverseTransaction(String invoice) {
        try {
            con = DBConnector.getConnection();
            String fetchDetails = "SELECT Mserial,Mtotalquantity,TotalTax,Mtotalcost FROM tablesell WHERE Invoice = '" + invoice + "'";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchDetails);
            if (rs.next()) {
                productSerial = rs.getString("Mserial");
                productQuantity = rs.getDouble("Mtotalquantity");
                productTax = rs.getDouble("TotalTax");
                productAmount = rs.getDouble("Mtotalcost");

                /**
                 * pass to this method to update details of the product in the store
                 * */
                updateProductInStore(productSerial, invoice, productQuantity, productTax, productAmount);

                rs.close();
                stmt.close();
                con.close();
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No such Invoice Number\nPlease Check and try again", "Product Information", JOptionPane.WARNING_MESSAGE);
                treverse.setText("");
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException e) {
            xamppfailure.getCon();
        }
    }

    /**
     * Add the items fetched to store
     */
    private void updateProductInStore(String serialNumber, String gotInvoice, double gotQuantity, double gotTax,
                                      double gotAmount) {
        try {
            con = DBConnector.getConnection();
            String fetchStore = "SELECT Mquantity,TotalTax,Mtotalcost FROM store WHERE Mserial = '" + serialNumber + "'";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchStore);
            if (rs.next()) {
                double storeQuantity = rs.getDouble("Mquantity");
                double storeTax = rs.getDouble("TotalTax");
                double storeAmount = rs.getDouble("Mtotalcost");

                double reversedQuantity = storeQuantity + gotQuantity;
                double reversedTax = storeTax + gotTax;
                double reversedAmount = storeAmount + gotAmount;

                /**
                 * Perform an update
                 * */
                String sqlupdate = "UPDATE store set Mquantity = '" + reversedQuantity + "',TotalTax = '" + reversedTax + "',Mtotalcost = '" + reversedAmount + "'WHERE  Mserial = '" + serialNumber + "'";
                prs = con.prepareStatement(sqlupdate);
                prs.execute();

                if (prs != null) {
                    /**
                     * Delete product from tablesell
                     * */
                    String sqldelete3 = "DELETE FROM tablesell WHERE Invoice = '" + gotInvoice + "'";
                    prs2 = con.prepareStatement(sqldelete3);
                    prs2.execute();

                    if (prs2 != null) {
                        prs.close();
                        prs2.close();
                        con.close();

                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Transaction Reversed Successfully", "Product Information", JOptionPane.INFORMATION_MESSAGE);
                        treverse.setText("");
                        uploadsells();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Transaction Reversal Failed", "Transaction Status", JOptionPane.WARNING_MESSAGE);
                        treverse.setText("");

                        prs.close();
                        prs2.close();
                        con.close();
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Product Updates Details\n Not Committed In the Store", "Transaction status", JOptionPane.WARNING_MESSAGE);
                    treverse.setText("");
                    prs.close();
                    con.close();
                }

            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Product Missing in the store", "Product Information", JOptionPane.WARNING_MESSAGE);
                treverse.setText("");
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
            Connection con = DBConnector.getConnection();
            String sqlqsum1 = null;
            if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty() && tsearch.getText().equalsIgnoreCase(searchDetails) && !tsearch.getText().equalsIgnoreCase("")) {
                //sql for getting sum sold for the item search
                sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ,SUM(Tamount) STORETC FROM tablesell WHERE Mname = '" + searchDetails + "' || Mserial = '" + searchDetails + "' || Invoice ='" + searchDetails + "'";

                //Execute the sql here
                prs = con.prepareStatement(sqlqsum1);
                rs1 = prs.executeQuery();
                //JOptionPane.showMessageDialog(null, searchDate + " SEARCH ONLY " + searchDetails);
            }
            if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(searchDate) && tsearch.getText().equalsIgnoreCase("") && !((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty()) {
                //sql for getting sum sold for the item search
                sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ,SUM(Tamount) STORETC FROM tablesell WHERE Msolddate  = '" + searchDate + "'";

                //Execute the sql here
                prs = con.prepareStatement(sqlqsum1);
                rs1 = prs.executeQuery();
                // JOptionPane.showMessageDialog(null, searchDate + " DATE ONLY " + searchDetails);
            }
            if ((!((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty() && !tsearch.getText().equalsIgnoreCase("")) && (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().equalsIgnoreCase(searchDate) && tsearch.getText().equalsIgnoreCase(searchDetails))) {
                //sql for getting sum sold for the item search
                sqlqsum1 = "SELECT SUM(Mtotalquantity) STOREQ,SUM(Tamount) STORETC FROM tablesell WHERE (Mname = '" + searchDetails + "' || Mserial = '" + searchDetails + "' || Invoice ='" + searchDetails + "') && (Msolddate  = '" + searchDate + "')";

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
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values2);
        String fetchrecordOne = "SELECT * FROM tablesell WHERE Mname  = '" + tsearch.getText() + "' || Mserial  = '" + tsearch.getText() + "' || Msolddate  = '" + soldDate + "' || Invoice ='" + tsearch.getText() + "' ORDER BY ID DESC ";
        String fetchrecordTwo = "SELECT * FROM tablesell WHERE (Mname = '" + tsearch.getText() + "' || Mserial = '" + tsearch.getText() + "' || Invoice ='" + tsearch.getText() + "') && (Msolddate  = '" + soldDate + "') ORDER BY ID DESC";
        try {
            Connection con = DBConnector.getConnection();
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
        String fetchrecord = "SELECT * FROM tablesell ORDER BY ID DESC";
        try {
            Connection con = DBConnector.getConnection();
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

            } else {
                Toolkit.getDefaultToolkit().beep();
                table.setModel(pharmacymodel);
                JOptionPane.showMessageDialog(null, "No Sold Records In The Store For Analysis", "Notification", JOptionPane.ERROR_MESSAGE);
                rs.close();
                stmt.close();
                con.close();

                //do this
                bload.setEnabled(false);
                bsearch.setEnabled(false);
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
            uploadsells();
        }

        //code for fetching all the sum of stock in paymentrecords
        try {
            Connection con = DBConnector.getConnection();
            String sqlqsumall = "SELECT SUM(Mtotalquantity) STOREQ FROM tablesell";
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
            Connection con = DBConnector.getConnection();
            String sqlcsumall2 = "SELECT SUM(Tamount) STORETC FROM tablesell";
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

    public void stocksell() {
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
        ltitle2 = new JLabel("DAILY SALES");
        ltitle2.setFont(new Font("Tahoma", Font.BOLD, 20));
        ltitle = new JLabel("SALES ANALYSIS");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 12));
        ltitleReverse = new JLabel("TRANSACTION REVERSAL");
        ltitleReverse.setFont(new Font("Tahoma", Font.BOLD, 12));
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
        lreverse = new JLabel("Enter Invoice Number");
        lreverse.setFont(new Font("Tahoma", Font.PLAIN, 15));
        treverse = new JTextField(15);
        treverse.setFont(new Font("Tahoma", Font.PLAIN, 13));
        // ttotalsale.setEditable(false);
        tsearch = new JTextField(20);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Record By Name/Serial Number/Tax Invoice Number");

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Past Record");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("RELOAD SOLD ITEMS");
        bload.setBackground(Color.GREEN.darker());
        bload.setToolTipText("reload sells");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        breverse = new JButton("REVERSE TRANSACTION");
        breverse.setBackground(Color.GREEN.darker());
        breverse.setToolTipText("reverse sold items");
        breverse.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breverse.setFont(new Font("Tahoma", Font.BOLD, 13));
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
        panelshow.setBorder(new TitledBorder("Sales Analysis"));
        panelshow.revalidate();

        //panelReverse
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 20, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelReverse.add(ltitleReverse, v);
        v.gridy++;
        v.insets = new Insets(0, 0, 10, 0);
        panelReverse.add(lreverse, v);
        v.gridy++;
        panelReverse.add(treverse, v);
        v.gridy++;
        panelReverse.add(breverse, v);
        v.gridy++;
        panelReverse.add(llogo3, v);
        panelReverse.setBorder(new TitledBorder("Reverse Transaction"));
        panelReverse.revalidate();

        //panelSell
        panelSell.add("North", panelshow);
        panelSell.add("Center", panelReverse);
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
        Fsells = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fsells.setUndecorated(true);
        Fsells.setIconImage(iconimage);
        Fsells.add(panelmain);
        Fsells.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fsells.setSize(screenSize.width, screenSize.height);
        Fsells.revalidate();
        Fsells.pack();
        Fsells.revalidate();
        Fsells.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        Fsells.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fsells.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                // minimized
                if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                    Fsells.revalidate();
                    Fsells.pack();
                    Fsells.revalidate();
                    Fsells.setLocationRelativeTo(null);
                } // maximized
                else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                    Fsells.revalidate();
                    Fsells.pack();
                    Fsells.revalidate();
                    Fsells.setLocationRelativeTo(null);
                }
            }
        });

        //methods start
        breverse.addActionListener(e -> {
            getInvoice();
        });
        bsearch.addActionListener(e -> {
            searchsell();
        });
        bload.addActionListener(e -> {
            uploadsells();
        });
        bback.addActionListener(e -> {
            Fsells.setVisible(false);
            Sections ds = new Sections();
            ds.Operations();
        });

    }
}
