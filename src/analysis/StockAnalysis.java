package analysis;

import dbconnector.DBConnector;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StockAnalysis extends Component {

    //dimension setting
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //getting images
    URL url1 = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/stock.png");
    URL url3 = Main.class.getResource("/img/delete.png");
    URL url4 = Main.class.getResource("/img/search.png");
    URL url5 = Main.class.getResource("/img/print3.png");
    URL url6 = Main.class.getResource("/img/reanalyze.png");

    ImageIcon imagestock = new ImageIcon(url2);
    ImageIcon imagedelete = new ImageIcon(url3);
    ImageIcon imagesearch = new ImageIcon(url4);
    ImageIcon imageprint = new ImageIcon(url5);
    ImageIcon imageanalyze = new ImageIcon(url6);

    //setting ma default image icon to my frames
    final ImageIcon icon = new ImageIcon(url3);
    final ImageIcon iconstock = new ImageIcon(url2);
    Image iconimage = new ImageIcon(url1).getImage();

    //components
    JLabel ltstockall, ltstock, ltcostall, ltcost, ltsoldall, ltsold, ltscostall, lname, lserial, ltcostsold, ladvanced, ltitle, lprofitmain, llossmain, lprofit, lloss, ltpayments, llogo1, llogo2, lbrief, llogo3, lstoretaxout, lstoretaxin, ltaxout, ltaxin;
    JTextField ttstockall, ttstock, ttcostall, ttcost, ttsoldall, ttsold, ttscostall, ttcostsold, tname, tserial, tsearch, tadvanced, tprofitmain, tlossmain, tprofit, tloss, ttpayments, tstoretaxout, tstoretaxin, ttaxout, ttaxin;
    JTable table;
    JProgressBar current;
    JButton bdelete, bsearch, bload, bback, bprint, breanalyze;

    //set model
    TableModel model;

    JPanel panelstcok = new JPanel(new GridBagLayout());
    JPanel panelstockitem = new JPanel(new GridBagLayout());
    JPanel paneltable = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));

    JFrame Fstock = new JFrame();

    //set variables
    String[] values = new String[]{"Product Name", "Serial Number", "Stock In", "Tax In", "Stock In Cost", "Stock Out", "Tax Out", "Stock Out Cost", "Total Revenue", "Total Deficit", "Saved On", "Updated On"};
    String bloadText;
    int num = 0;
    String path = null;
    String stockStatus = null;

    //database connectors
    Connection con;
    Statement stmt = null;
    ResultSet rs, rs1, rs2, rs3, rs4, rs5, rs6, rs7, rs8, rs9 = null;
    PreparedStatement prs, prs2, prs3, prs4, prs5 = null;

    //date
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);

    //getting number formart
    //TODO this code will be used later
    // NumberFormat numberFormat = NumberFormat.getInstance();

    //calling class dbconnect
    DBConnector xamppfailure = new DBConnector();

    /**
     * Get the sales of the product being deleted
     */
    private void saveSoldRecords(String productSerial) {
        try {
            con = DBConnector.getConnection();
            String checkRecords = "SELECT * FROM tablecritical WHERE Mserial = '" + productSerial + "'";
            String fetchRecords = "SELECT * FROM tablesell WHERE Mserial = '" + productSerial + "'";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs2 = stmt.executeQuery(checkRecords);

            if(rs2.next()){
                rs = stmt.executeQuery(fetchRecords);
                if (rs.next()) {
                    do {
                        String sqlsave = "INSERT INTO pastsoldrecords(Mname,Mserial,Mtotalquantity,Bcost,Mcost,TotalTax,Tamount,Mtotalcost,CashPaid,ChangePaid,Msolddate,LastEdited,Invoice) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        prs = con.prepareStatement(sqlsave);

                        //setting to database
                        prs.setString(1, rs.getString("Mname"));
                        prs.setString(2, productSerial);
                        prs.setDouble(3, rs.getDouble("Mtotalquantity"));
                        prs.setDouble(4, rs.getDouble("Bcost"));
                        prs.setDouble(5, rs.getDouble("Mcost"));
                        prs.setDouble(6, rs.getDouble("TotalTax"));
                        prs.setDouble(7, rs.getDouble("Tamount"));
                        prs.setDouble(8, rs.getDouble("Mtotalcost"));
                        prs.setDouble(9, rs.getDouble("CashPaid"));
                        prs.setDouble(10, rs.getDouble("ChangePaid"));
                        prs.setString(11, rs.getString("Msolddate"));
                        prs.setString(12, rs.getString("LastEdited"));
                        prs.setString(13, rs.getString("Invoice"));

                        prs.execute();

                    } while (rs.next());
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "No Sold Records Found.", "Notification", JOptionPane.WARNING_MESSAGE);
                }
            }else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "No Such Product.", "Notification", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            xamppfailure.getCon();
        }
    }


    //save stock in csv of excel
    private void exportDataExcel() {
        JFileChooser fc = new JFileChooser();
        //fc.showOpenDialog(this);
        fc.showSaveDialog(this);
        fc.setDialogTitle("Excel Data Export");
        fc.setDialogTitle("Select Path");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //do backup

        File f = fc.getSelectedFile();
        path = f.getAbsolutePath();
        path = path.replace('\\', '/');
        path = "" + path + "_" + date + ".xls" + "";
        System.out.println(path);

        //get database connection
        try {
            con = DBConnector.getConnection();
            //set a counter
            int productNumber = 1;
            int i = 1;
            //do this after getting Connection
//            String filename = "D:/data.xls";
            String filename = path;
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet("Product(s) Data");

            HSSFRow rowhead = sheet.createRow((short) 0);
            rowhead.createCell((short) 0).setCellValue("No.");
            rowhead.createCell((short) 1).setCellValue("Product Name");
            rowhead.createCell((short) 2).setCellValue("Serial Number");
            rowhead.createCell((short) 3).setCellValue("Stock In");
            rowhead.createCell((short) 4).setCellValue("Tax In");
            rowhead.createCell((short) 5).setCellValue("Stock In Cost");
            rowhead.createCell((short) 6).setCellValue("Stock Out");
            rowhead.createCell((short) 7).setCellValue("Tax Out");
            rowhead.createCell((short) 8).setCellValue("Stock Out Cost");
            rowhead.createCell((short) 9).setCellValue("Total Revenue");
            rowhead.createCell((short) 10).setCellValue("Total Deficit");
            rowhead.createCell((short) 11).setCellValue("Saved On");
            rowhead.createCell((short) 12).setCellValue("Updated On");

            //get data from the database
            String excelBackup = "SELECT * FROM tablecritical";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(excelBackup);

            //loop the data
            while (rs.next()) {
                HSSFRow row = sheet.createRow((short) i);
                row.createCell((short) 0).setCellValue(productNumber);
                row.createCell((short) 1).setCellValue(rs.getString("Mname"));
                row.createCell((short) 2).setCellValue(rs.getString("Mserial"));
                row.createCell((short) 3).setCellValue(rs.getDouble("StockIn"));
                row.createCell((short) 4).setCellValue(rs.getDouble("TaxIn"));
                row.createCell((short) 5).setCellValue(rs.getDouble("StockInCost"));
                row.createCell((short) 6).setCellValue(rs.getDouble("StockOut"));
                row.createCell((short) 7).setCellValue(rs.getDouble("TaxOut"));
                row.createCell((short) 8).setCellValue(rs.getDouble("StockOutCost"));
                row.createCell((short) 9).setCellValue(rs.getDouble("Profit"));
                row.createCell((short) 10).setCellValue(rs.getDouble("Loss"));
                row.createCell((short) 11).setCellValue(rs.getString("UpdatedOn"));
                row.createCell((short) 12).setCellValue(rs.getString("LastEdited"));

                //increment this counters
                productNumber++;
                i++;
            }

            FileOutputStream fileOut = new FileOutputStream(filename);

            //write this workbook to an Outputstream.
            hwb.write(fileOut);
            fileOut.flush();
            fileOut.close();

            rs.close();
            stmt.close();
            con.close();

            JOptionPane.showMessageDialog(null, "Your excel file has been generated successfully", "Excel File Generation", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException io) {
            JOptionPane.showMessageDialog(null, "Excel File Failed To Be Generated", "Excel File Generation", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            xamppfailure.getCon();
        }

    }

    //create a thread function
    private void anaylzeData() {
        //start a thread to give a counter
        Thread timer = new Thread(() -> {
            try {
                //give your delay timer here
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                while (num < 2000) {
                    current.setValue(num);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    num += 95;
                    //System.out.println(num);
                    if (num == 95) {
                        //do something
                    }
                    if (num == 1425) {
                        //do something
                        analyzingdata();
                    }
                    if (num == 2090) {
                        //do something
//                        storeanalyzer();
                        uplaod();
                        bload.setText(bloadText);
                        bload.setForeground(Color.black);
                        current.setVisible(false);

                        //notify user
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, stockStatus, "Stock Analysis Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);

                    }
                }
            }
        });
        timer.start();
    }

    //reanalyze data
    private void reAnalyzeData() {
        Fstock.setVisible(false);
        StockAnalysis stockAnalysis = new StockAnalysis();
        stockAnalysis.CriticalAnalysis();
    }

    //setting fields empty
    private void setFieldsEmpty() {
        //setting to null
        tname.setText(null);
        tserial.setText(null);
        ttstock.setText(null);
        ttcost.setText(null);
        ttsold.setText(null);
        ttcostsold.setText(null);
        tprofit.setText(null);
        tloss.setText(null);
        tadvanced.setText(null);
        ttaxin.setText(null);
        ttaxout.setText(null);
    }

    //set main fileds of stock to null if no data is found
    private void setNull() {
        ttstockall.setText("");
        tstoretaxin.setText("");
        ttcostall.setText("");
        ttsoldall.setText("");
        tstoretaxout.setText("");
        ttscostall.setText("");
        tprofitmain.setText("");
        tlossmain.setText("");
        ttpayments.setText("");
    }

    //getting table contents
    private void tablecont() {
        String storename = table.getValueAt(table.getSelectedRow(), 0).toString();
        String storeserial = table.getValueAt(table.getSelectedRow(), 1).toString();
        double storein = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 2)));
        double storetaxin = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 3)));
        double storeincost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 4)));
        double storeout = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 5)));
        double storetaxout = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 6)));
        double storeoutcost = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 7)));
        double storeprofit = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 8)));
        double storeloss = Double.parseDouble(String.valueOf(table.getValueAt(table.getSelectedRow(), 9)));

        //setting values got from table
        tname.setText(storename);
        tserial.setText(storeserial);
        ttstock.setText(String.format("%.2f", storein));
        ttaxin.setText(String.format("%.2f", storetaxin));
        ttcost.setText(String.format("%.2f", storeincost));
        ttsold.setText(String.format("%.2f", storeout));
        ttaxout.setText(String.format("%.2f", storetaxout));
        ttcostsold.setText(String.format("%.2f", storeoutcost));
        tprofit.setText(String.format("%.2f", storeprofit));
        tloss.setText(String.format("%.2f", storeloss));
        tadvanced.setText(storeserial);
    }

    //method for loading and analyzing all data for the whole stock
    private void storeanalyzer() {
        try {
            con = DBConnector.getConnection();
            String stocksql = "SELECT SUM(Mtotalquantity) STORESTOCK FROM  tablestockin";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(stocksql);
            if (rs.next()) {
                double storein = rs.getDouble("STORESTOCK");

                //getting sum BUYING of stock
                String stockcost = "SELECT SUM(Mtotalcost) STORESTOCKCOST FROM  tablestockin";
                prs = con.prepareStatement(stockcost);
                rs1 = prs.executeQuery();
                rs1.next();
                double storeincost = rs1.getDouble("STORESTOCKCOST");

                //getting sum sold of stock
                String sqlout = "SELECT SUM(Mtotalquantity) STOCKOUTSUM FROM tablesell";
                prs = con.prepareStatement(sqlout);
                rs2 = prs.executeQuery();
                rs2.next();
                double storeout = rs2.getDouble("STOCKOUTSUM");
                //JOptionPane.showMessageDialog(null, storeout);

                //getting sum sold for stock cost
                String sqloutcost = "SELECT SUM(Mtotalcost) COSTOUTSUM FROM tablesell";
                prs = con.prepareStatement(sqloutcost);
                rs3 = prs.executeQuery();
                rs3.next();
                double storecostout = rs3.getDouble("COSTOUTSUM");
                //JOptionPane.showMessageDialog(null, storecostout);

                //getting sum of payements from suppliers
                String sqlsumsupply = "SELECT SUM(Payed) SUMPAYED FROM tabledistributers";
                prs = con.prepareStatement(sqlsumsupply);
                rs4 = prs.executeQuery();
                rs4.next();
                double supplypay = rs4.getDouble("SUMPAYED");

                //getting sum of payements from users
                String sqlsumworkers = "SELECT SUM(Payed) SUMPAYED FROM userlogin";
                prs = con.prepareStatement(sqlsumworkers);
                rs5 = prs.executeQuery();
                rs5.next();
                double workerpay = rs5.getDouble("SUMPAYED");

                //getting sum of payements from admin
                String sqlsumworkers2 = "SELECT SUM(Payed) ADMINPAYED FROM adminlogin";
                prs = con.prepareStatement(sqlsumworkers2);
                rs6 = prs.executeQuery();
                rs6.next();
                double workerpay2 = rs6.getDouble("ADMINPAYED");

                //getting sum of total taxin
                String sqltaxin = "SELECT SUM(TotalTax) TAXIN FROM tablestockin";
                prs = con.prepareStatement(sqltaxin);
                rs8 = prs.executeQuery();
                rs8.next();
                double taxinfound = rs8.getDouble("TAXIN");

                //getting sold items tax
                String sqltaxout = "SELECT SUM(TotalTax) TAXOUT FROM tablesell";
                prs = con.prepareStatement(sqltaxout);
                rs9 = prs.executeQuery();
                rs9.next();
                double taxoutfound = rs9.getDouble("TAXOUT");

                //calculate the profit,payments and losses
                double totalpay = supplypay + workerpay + workerpay2;
                double storeprofit = storecostout - (storeincost + totalpay);
                double storeloss = storeincost - storecostout;
                double storevalue = 0;

                tstoretaxin.setText(String.format("%.2f", taxinfound));
                tstoretaxout.setText(String.format("%.2f", taxoutfound));
                ttstockall.setText(String.format("%.2f", storein));
                ttcostall.setText(String.format("%.2f", storeincost));
                ttsoldall.setText(String.format("%.2f", storeout));
                ttscostall.setText(String.format("%.2f", storecostout));
                ttpayments.setText(String.format("%.2f", totalpay));
                if (storeprofit >= 1) {
                    tprofitmain.setText(String.format("%.2f", storeprofit));
                } else {
                    tprofitmain.setText(String.format("%.2f", storevalue));
                }
                if (storeloss >= 1) {
                    tlossmain.setText(String.format("%.2f", storeloss));
                } else {
                    tlossmain.setText(String.format("%.2f", storevalue));
                }

                rs4.close();
                rs2.close();
                rs3.close();
                rs1.close();
                rs.close();
                stmt.close();
                con.close();
            } else {
//                Toolkit.getDefaultToolkit().beep();
//                JOptionPane.showMessageDialog(null, "No Stock In The Store For Analysis", "Notification", JOptionPane.ERROR_MESSAGE);
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }

    //method for searching
    private void searchengine() {
        String valuesearch = tsearch.getText();
        if (valuesearch.equalsIgnoreCase("") || valuesearch.equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Please Enter The Medicine Name/Serial Number For Search", "Search Message", JOptionPane.INFORMATION_MESSAGE, iconstock);
        } else {
            DefaultTableModel pharmacymodel = new DefaultTableModel();
            //dm.addColumn("Medicine Name");
            pharmacymodel.setColumnIdentifiers(values);
            String fetchrecord = "SELECT * FROM tablecritical WHERE Mserial = '" + tsearch.getText() + "' || Mname = '" + tsearch.getText() + "' ORDER BY ID DESC";
            try {
                con = DBConnector.getConnection();
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = stmt.executeQuery(fetchrecord);
                if (rs.next()) {
                    do {
                        String drugname = rs.getString("Mname");
                        String drugserial = rs.getString("Mserial");
                        double druginstock = rs.getDouble("StockIn");
                        String sinstock = String.format("%.2f", druginstock);
                        double drugtaxin = rs.getDouble("TaxIn");
                        String taxin = String.format("%.2f", drugtaxin);
                        double drugincost = rs.getDouble("StockInCost");
                        String sincost = String.format("%.2f", drugincost);
                        double drugoutstock = rs.getDouble("StockOut");
                        String soutstock = String.format("%.2f", drugoutstock);
                        double drugtaxout = rs.getDouble("TaxOut");
                        String taxout = String.format("%.2f", drugtaxout);
                        double drugoutcost = rs.getDouble("StockOutCost");
                        String soutcost = String.format("%.2f", drugoutcost);
                        double drugprofit = rs.getDouble("Profit");
                        String sprofit = String.format("%.2f", drugprofit);
                        double drugloss = rs.getDouble("Loss");
                        String sloss = String.format("%.2f", drugloss);
                        String drugupdate = rs.getString("UpdatedOn");
                        String drugeditedon = rs.getString("LastEdited");

                        pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, taxin, sincost, soutstock, taxout, soutcost, sprofit, sloss, drugeditedon, drugupdate});

                        //setting values got from table
                        tname.setText(drugname);
                        tserial.setText(drugserial);
                        ttstock.setText(String.format("%.2f", druginstock));
                        ttcost.setText(String.format("%.2f", drugincost));
                        ttsold.setText(String.format("%.2f", drugoutstock));
                        ttcostsold.setText(String.format("%.2f", drugoutcost));
                        tprofit.setText(String.format("%.2f", drugprofit));
                        tloss.setText(String.format("%.2f", drugloss));
                        tadvanced.setText(drugserial);

                    } while (rs.next());
                    table.setModel(pharmacymodel);
                    table.setAutoCreateRowSorter(true);
                    table.setFillsViewportHeight(true);
                    table.revalidate();

                    rs.close();
                    stmt.close();
                    con.close();
                } else {
                    table.setModel(pharmacymodel);
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Sorry No Data Found For" + " " + tsearch.getText(), "Notification", JOptionPane.INFORMATION_MESSAGE);
                    uplaod();
                    rs.close();
                    stmt.close();
                    con.close();
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }
        }
    }

    //metod for analysing data and analyzing data for a single stock
    private void analyzingdata() {
        try {
            con = DBConnector.getConnection();
            String fetchserial = "SELECT * FROM  tablestockin";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchserial);
            if (rs.next()) {
                do {
                    String storename = rs.getString("Mname");
                    String storeserial = rs.getString("Mserial");
                    double storein = rs.getDouble("Mtotalquantity");
                    double storeincost = rs.getDouble("Mtotalcost");
                    double storetaxin = rs.getDouble("TotalTax");
                    //JOptionPane.showMessageDialog(null, storename + " " + storeserial);
                    //JOptionPane.showMessageDialog(null, storein + " " + storeincost);

                    //getting sum sold of stock
                    String sqlout = "SELECT SUM(Mtotalquantity) STOCKOUTSUM FROM tablesell WHERE Mserial = '" + storeserial + "'";
                    prs = con.prepareStatement(sqlout);
                    rs2 = prs.executeQuery();
                    while (rs2.next()) {
                        double storeout = rs2.getDouble("STOCKOUTSUM");

                        //getting sum of sold tax items
                        String sqltax = "SELECT SUM(TotalTax) STOCKOUTTAX FROM tablesell WHERE Mserial = '" + storeserial + "'";
                        prs = con.prepareStatement(sqltax);
                        rs7 = prs.executeQuery();
                        while (rs7.next()) {
                            double storetaxout = rs7.getDouble("STOCKOUTTAX");

                            //getting sum sold for stock cost
                            String sqloutcost = "SELECT SUM(Mtotalcost) COSTOUTSUM FROM tablesell WHERE Mserial = '" + storeserial + "'";
                            prs = con.prepareStatement(sqloutcost);
                            rs3 = prs.executeQuery();
                            while (rs3.next()) {
                                double storecostout = rs3.getDouble("COSTOUTSUM");
                                //JOptionPane.showMessageDialog(null, storecostout);

                                //calculate the profit
                                double finalprofit = storecostout - storeincost;
                                double finalloss = storeincost - storecostout;
                                double finalvalue = 0;

                                String nameanalyzed = "SELECT Mserial FROM tablecritical WHERE Mserial = '" + storeserial + "'";
                                prs = con.prepareStatement(nameanalyzed);
                                rs4 = prs.executeQuery();
                                if (rs4.next()) {
                                    if (finalprofit >= 1) {
                                        String updatetablecitical = "UPDATE tablecritical set TaxOut = '" + storetaxout + "', UpdatedOn   = '" + fileeditedlast + "',StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Profit  = '" + finalprofit + "' WHERE Mserial = '" + storeserial + "'";
                                        prs = con.prepareStatement(updatetablecitical);

                                        prs.execute();
                                    } else {
                                        String updatetablecitical = "UPDATE tablecritical set TaxOut = '" + storetaxout + "', UpdatedOn   = '" + fileeditedlast + "',StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Profit  = '" + finalvalue + "' WHERE Mserial = '" + storeserial + "'";
                                        prs = con.prepareStatement(updatetablecitical);

                                        prs.execute();
                                    }
                                    if (finalloss >= 1) {
                                        String updatetablecitical = "UPDATE tablecritical set TaxOut = '" + storetaxout + "', UpdatedOn   = '" + fileeditedlast + "',StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Loss  = '" + finalloss + "' WHERE Mserial = '" + storeserial + "'";
                                        prs = con.prepareStatement(updatetablecitical);

                                        prs.execute();
                                    } else {
                                        String updatetablecitical = "UPDATE tablecritical set TaxOut = '" + storetaxout + "', UpdatedOn   = '" + fileeditedlast + "',StockOut   = '" + storeout + "',StockOutCost  = '" + storecostout + "',Loss  = '" + finalvalue + "' WHERE Mserial = '" + storeserial + "'";
                                        prs = con.prepareStatement(updatetablecitical);

                                        prs.execute();
                                    }

                                } else {
                                    String sqladditem = "INSERT INTO tablecritical(Mname,Mserial,StockIn,TaxIn,StockInCost,StockOut,TaxOut,StockOutCost,Profit,Loss,UpdatedOn,LastEdited) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                                    prs = con.prepareStatement(sqladditem);

                                    //setting to database
                                    prs.setString(1, storename);
                                    prs.setString(2, storeserial);
                                    prs.setDouble(3, storein);
                                    prs.setDouble(4, storetaxin);
                                    prs.setDouble(5, storeincost);
                                    prs.setDouble(6, storeout);
                                    prs.setDouble(7, storetaxout);
                                    prs.setDouble(8, storecostout);
                                    if (finalprofit >= 1) {
                                        prs.setDouble(9, finalprofit);
                                    } else {
                                        prs.setDouble(9, finalvalue);
                                    }
                                    if (finalloss >= 1) {
                                        prs.setDouble(10, finalloss);
                                    } else {
                                        prs.setDouble(10, finalvalue);
                                    }
                                    prs.setString(11, fileeditedlast);
                                    prs.setString(12, fileeditedlast);

                                    prs.execute();

                                }
                            }

                        }


                    }

                } while (rs.next());

                rs4.close();
                rs2.close();
                rs3.close();
                rs.close();
                stmt.close();
                con.close();

                //set button enabled if data exists
                bload.setEnabled(true);
                bsearch.setEnabled(true);
                bdelete.setEnabled(true);
                bprint.setEnabled(true);
                breanalyze.setEnabled(true);

                bloadText = "RELOAD";

                stockStatus = "Stock Analysis Finished Successfully" + "\n\t" + "Your Stock Is up to Date";

            } else {
                rs.close();
                stmt.close();
                con.close();

                //set this
                bloadText = "NO DATA FOUND...";
                stockStatus = "Sorry No Stock In The Store For Analysis";
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
        }
    }

    //method for uploadin data in system
    private void uplaod() {
        DefaultTableModel pharmacymodel = new DefaultTableModel();
        //dm.addColumn("Medicine Name");
        pharmacymodel.setColumnIdentifiers(values);
        String fetchrecord = "SELECT * FROM tablecritical ORDER BY ID DESC";
        try {
            con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchrecord);
            if (rs.next()) {
                do {
                    String drugname = rs.getString("Mname");
                    String drugserial = rs.getString("Mserial");
                    double druginstock = rs.getDouble("StockIn");
                    String sinstock = String.format("%.2f", druginstock);
                    double drugtaxin = rs.getDouble("TaxIn");
                    String taxin = String.format("%.2f", drugtaxin);
                    double drugincost = rs.getDouble("StockInCost");
                    String sincost = String.format("%.2f", drugincost);
                    double drugoutstock = rs.getDouble("StockOut");
                    String soutstock = String.format("%.2f", drugoutstock);
                    double drugtaxout = rs.getDouble("TaxOut");
                    String taxout = String.format("%.2f", drugtaxout);
                    double drugoutcost = rs.getDouble("StockOutCost");
                    String soutcost = String.format("%.2f", drugoutcost);
                    double drugprofit = rs.getDouble("Profit");
                    String sprofit = String.format("%.2f", drugprofit);
                    double drugloss = rs.getDouble("Loss");
                    String sloss = String.format("%.2f", drugloss);
                    String drugupdate = rs.getString("UpdatedOn");
                    String drugeditedon = rs.getString("LastEdited");

                    pharmacymodel.addRow(new String[]{drugname, drugserial, sinstock, taxin, sincost, soutstock, taxout, soutcost, sprofit, sloss, drugeditedon, drugupdate});

                } while (rs.next());
                table.setModel(pharmacymodel);
                table.setAutoCreateRowSorter(true);
                table.setFillsViewportHeight(true);
                table.revalidate();


                //set this model here
                model = table.getModel();

                //call this function
                storeanalyzer();

                //set fields emapty
                setFieldsEmpty();

                rs.close();
                stmt.close();
                con.close();

            } else {
                table.setModel(pharmacymodel);
//                Toolkit.getDefaultToolkit().beep();
//                JOptionPane.showMessageDialog(null, "No Stock In The Store For Analysis", "Notification", JOptionPane.ERROR_MESSAGE);
                rs.close();
                stmt.close();
                con.close();

                //set button disabled if data is missing
                bdelete.setEnabled(false);
                bload.setEnabled(false);
                bsearch.setEnabled(false);
                bprint.setEnabled(false);
                breanalyze.setEnabled(false);

                //call this function
                setFieldsEmpty();
                setNull();
            }
        } catch (SQLException x) {
            xamppfailure.getCon();
            anaylzeData();
            uplaod();
        }
    }

    public void CriticalAnalysis() {
        llogo1 = new JLabel(imagestock);
        llogo2 = new JLabel(imagestock);
        llogo3 = new JLabel(imagestock);
        lbrief = new JLabel("ANALYSIS PER PRODUCT");
        lbrief.setFont(new Font("Tahoma", Font.BOLD, 13));

        table = new JTable();
        // this enables horizontal scroll bar
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Tahoma", Font.PLAIN, 13));
        //table.setAutoCreateRowSorter(true);
        table.setIntercellSpacing(new Dimension(20, 20));
        // table.setPreferredScrollableViewportSize(new Dimension(935, 570));
        table.setPreferredScrollableViewportSize(new Dimension((int) (screenSize.width / 1.45), (int) (screenSize.height / 1.32)));
        table.revalidate();
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.revalidate();

        //for center
        ltitle = new JLabel("ANALYZED STOCK AND CASH FLOW");
        ltitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        tsearch = new JTextField(25);
        tsearch.setFont(new Font("Tahoma", Font.PLAIN, 15));
        tsearch.setToolTipText("Search Product By Name/Serial Number");

        //for left side
        ltstockall = new JLabel("Total Stock In");
        ltstockall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lstoretaxin = new JLabel("[KES] Total Tax In");
        lstoretaxin.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltcostall = new JLabel("[KES]Total Buying Cost");
        ltcostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltsoldall = new JLabel("Total Stock Out");
        ltsoldall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lstoretaxout = new JLabel("[KES] Total Tax Out");
        lstoretaxout.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltscostall = new JLabel("[KES]Total Selling Cost");
        ltscostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ltpayments = new JLabel("[KES]Total Payments");
        ltpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lprofitmain = new JLabel("[KES]Total Revenue");
        lprofitmain.setFont(new Font("Tahoma", Font.PLAIN, 13));
        llossmain = new JLabel("[KES]Total Deficit");
        llossmain.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //for right side
        lname = new JLabel("Product Name");
        lname.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lserial = new JLabel("Serial Number");
        lserial.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltstock = new JLabel("Stock In");
        ltstock.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltaxin = new JLabel("[KES]Total Tax In");
        ltaxin.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltcost = new JLabel("[KES]Total Buying Cost");
        ltcost.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltsold = new JLabel("Stock Out");
        ltsold.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltaxout = new JLabel("[KES]Total Tax Out");
        ltaxout.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ltcostsold = new JLabel("[KES]Total Selling Cost");
        ltcostsold.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lprofit = new JLabel("[KES]Total Revenue");
        lprofit.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lloss = new JLabel("[KES]Total Deficit");
        lloss.setFont(new Font("Tahoma", Font.PLAIN, 15));
        ladvanced = new JLabel("Advanced Deletion");
        ladvanced.setForeground(Color.BLUE.darker());
        ladvanced.setFont(new Font("Tahoma", Font.BOLD, 15));

        //for left side
        ttstockall = new JTextField(10);
        // ttstockall.setEditable(false);
        ttstockall.setBackground(Color.LIGHT_GRAY);
        ttstockall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcostall = new JTextField(10);
        // ttcostall.setEditable(false);
        ttcostall.setBackground(Color.LIGHT_GRAY);
        ttcostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttsoldall = new JTextField(10);
        // ttsoldall.setEditable(false);
        ttsoldall.setBackground(Color.LIGHT_GRAY);
        ttsoldall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttscostall = new JTextField(10);
        // ttscostall.setEditable(false);
        ttscostall.setBackground(Color.LIGHT_GRAY);
        ttscostall.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttpayments = new JTextField(10);
        // ttpayments.setEditable(false);
        ttpayments.setBackground(Color.LIGHT_GRAY);
        ttpayments.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tprofitmain = new JTextField(10);
        // tprofitmain.setEditable(false);
        tprofitmain.setBackground(Color.LIGHT_GRAY);
        tprofitmain.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tlossmain = new JTextField(10);
        // tlossmain.setEditable(false);
        tlossmain.setBackground(Color.LIGHT_GRAY);
        tlossmain.setFont(new Font("Tahoma", Font.PLAIN, 13));

        tstoretaxin = new JTextField(10);
        tstoretaxin.setBackground(Color.LIGHT_GRAY);
        tstoretaxin.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tstoretaxout = new JTextField(10);
        tstoretaxout.setBackground(Color.LIGHT_GRAY);
        tstoretaxout.setFont(new Font("Tahoma", Font.PLAIN, 13));

        //for left side
        tname = new JTextField(15);
        tname.setBackground(Color.LIGHT_GRAY);
        tname.setEditable(false);
        tname.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tserial = new JTextField(15);
        tserial.setBackground(Color.LIGHT_GRAY);
        tserial.setEditable(false);
        tserial.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttstock = new JTextField(15);
        ttstock.setBackground(Color.LIGHT_GRAY);
        ttstock.setEditable(false);
        ttstock.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcost = new JTextField(15);
        ttcost.setBackground(Color.LIGHT_GRAY);
        ttcost.setEditable(false);
        ttcost.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttsold = new JTextField(15);
        ttsold.setEditable(false);
        ttsold.setBackground(Color.LIGHT_GRAY);
        ttsold.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttaxin = new JTextField(15);
        ttaxin.setEditable(false);
        ttaxin.setBackground(Color.LIGHT_GRAY);
        ttaxin.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttaxout = new JTextField(15);
        ttaxout.setEditable(false);
        ttaxout.setBackground(Color.LIGHT_GRAY);
        ttaxout.setFont(new Font("Tahoma", Font.PLAIN, 13));
        ttcostsold = new JTextField(15);
        ttcostsold.setBackground(Color.LIGHT_GRAY);
        ttcostsold.setEditable(false);
        ttcostsold.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tprofit = new JTextField(15);
        tprofit.setBackground(Color.LIGHT_GRAY);
        tprofit.setEditable(false);
        tprofit.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tloss = new JTextField(15);
        tloss.setBackground(Color.LIGHT_GRAY);
        tloss.setEditable(false);
        tloss.setFont(new Font("Tahoma", Font.PLAIN, 13));
        tadvanced = new JTextField(15);
        tadvanced.setFont(new Font("Tahoma", Font.PLAIN, 13));

        bsearch = new JButton(imagesearch);
        bsearch.setBackground(Color.LIGHT_GRAY);
        bsearch.setToolTipText("Search Product");
        bsearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bprint = new JButton(imageprint);
        bprint.setBackground(Color.LIGHT_GRAY);
        bprint.setToolTipText("Generate Excel File For This Data");
        bprint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        breanalyze = new JButton(imageanalyze);
        breanalyze.setBackground(Color.LIGHT_GRAY);
        breanalyze.setToolTipText("Reanalyze Data");
        breanalyze.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload = new JButton("ANALYZING...");
        bload.setBackground(Color.GREEN.darker());
        bload.setForeground(Color.RED.darker());
        bload.setToolTipText("load analyzed data");
        bload.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bload.setFont(new Font("Tahoma", Font.BOLD, 15));
        bdelete = new JButton(imagedelete);
        bdelete.setBackground(Color.RED.brighter());
        bdelete.setToolTipText("Please Dalete Only The Finished Stock(Enter Serial Number To Delete)");
        bdelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));


        //progressbar
        current = new JProgressBar(0, 2000);
        current.setBorder(null);
        current.setBackground(Color.lightGray);
        current.setForeground(Color.GREEN.darker());
        current.setValue(0);
        current.setPreferredSize(new Dimension((int) (screenSize.width / 1.45), 6));
        current.setStringPainted(false);
        //current.setForeground(Color.blue.darker());


        //set button enabled if data exists
        bload.setEnabled(false);
        bsearch.setEnabled(false);
        bdelete.setEnabled(false);
        breanalyze.setEnabled(false);
        bprint.setEnabled(false);

        //adding components to paneltable
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltable.add(ltitle, v);
        v.gridy++;
        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bload, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(breanalyze, v);
        v.insets = new Insets(0, 100, 0, 0);
        paneltable.add(bprint, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 53);
        paneltable.add(tsearch, v);
        v.insets = new Insets(0, 0, 0, 0);
        paneltable.add(bsearch, v);
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(5, 0, 0, 0);
        v.gridy++;
        paneltable.add(current, v);
        v.insets = new Insets(5, 0, 0, 0);
        v.gridy++;
        paneltable.add(scrollPane, v);
        paneltable.setBorder(new TitledBorder(""));
        paneltable.revalidate();

        //adding to panel
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(5, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstcok.add(bback, v);
        v.gridy++;
        panelstcok.add(llogo1, v);
        v.gridy++;
        panelstcok.add(ltstockall, v);
        v.gridy++;
        panelstcok.add(ttstockall, v);
        v.gridy++;
        panelstcok.add(lstoretaxin, v);
        v.gridy++;
        panelstcok.add(tstoretaxin, v);
        v.gridy++;
        panelstcok.add(ltcostall, v);
        v.gridy++;
        panelstcok.add(ttcostall, v);
        v.gridy++;
        panelstcok.add(ltsoldall, v);
        v.gridy++;
        panelstcok.add(ttsoldall, v);
        v.gridy++;
        panelstcok.add(lstoretaxout, v);
        v.gridy++;
        panelstcok.add(tstoretaxout, v);
        v.gridy++;
        panelstcok.add(ltscostall, v);
        v.gridy++;
        panelstcok.add(ttscostall, v);
        v.gridy++;
        panelstcok.add(lprofitmain, v);
        v.gridy++;
        panelstcok.add(tprofitmain, v);
        v.gridy++;
        panelstcok.add(llossmain, v);
        v.gridy++;
        panelstcok.add(tlossmain, v);
        v.gridy++;
        panelstcok.add(ltpayments, v);
        v.gridy++;
        panelstcok.add(ttpayments, v);
        v.gridy++;
        panelstcok.add(llogo2, v);
        panelstcok.setBorder(new TitledBorder(""));

        //adding to panelstockitem
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 10, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelstockitem.add(llogo3, v);
        v.gridy++;
        panelstockitem.add(lbrief, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(lname, v);
        v.gridy++;
        panelstockitem.add(tname, v);
        v.gridy++;
        panelstockitem.add(lserial, v);
        v.gridy++;
        panelstockitem.add(tserial, v);
        v.gridy++;
        panelstockitem.add(ltstock, v);
        v.gridy++;
        panelstockitem.add(ttstock, v);
        v.gridy++;
        panelstockitem.add(ltaxin, v);
        v.gridy++;
        panelstockitem.add(ttaxin, v);
        v.gridy++;
        panelstockitem.add(ltcost, v);
        v.gridy++;
        panelstockitem.add(ttcost, v);
        v.gridy++;
        panelstockitem.add(ltsold, v);
        v.gridy++;
        panelstockitem.add(ttsold, v);
        v.gridy++;
        panelstockitem.add(ltaxout, v);
        v.gridy++;
        panelstockitem.add(ttaxout, v);
        v.gridy++;
        panelstockitem.add(ltcostsold, v);
        v.gridy++;
        panelstockitem.add(ttcostsold, v);
        v.gridy++;
        panelstockitem.add(lprofit, v);
        v.gridy++;
        panelstockitem.add(tprofit, v);
        v.gridy++;
        panelstockitem.add(lloss, v);
        v.gridy++;
        panelstockitem.add(tloss, v);
        v.insets = new Insets(30, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(ladvanced, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(tadvanced, v);
        v.insets = new Insets(10, 0, 0, 0);
        v.gridy++;
        panelstockitem.add(bdelete, v);
        panelstockitem.setBorder(new TitledBorder(""));

        //seeting to main panelmain
        panelmain.add("West", panelstcok);
        panelmain.add("Center", paneltable);
        panelmain.add("East", panelstockitem);
        panelmain.setBorder(new TitledBorder(""));
        panelmain.setBackground(Color.blue.brighter());
        panelmain.revalidate();

        //actions setion
        //action for table to select a specific column
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //getting values from table
                tablecont();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //getting values from table
                tablecont();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        bload.addActionListener(e -> {
            storeanalyzer();
            uplaod();
        });

        breanalyze.addActionListener(e -> {
            String[] option = {"Proceed", "Cancel"};
            int selloption = JOptionPane.showOptionDialog(null, "Are you sure you want to reanalyze the stock.", "Stock Analysis Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconstock, option, option[1]);
            if (selloption == 0) {
                reAnalyzeData();
            } else {
                //notify user
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "You have successfully cancelled.", "Stock Analysis Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);
            }
        });

        bsearch.addActionListener(e -> {
            searchengine();
        });

        bprint.addActionListener(e -> {
            exportDataExcel();
        });
        bdelete.addActionListener(e -> {
            String serialgot = tadvanced.getText();
            try {
                if (serialgot.equalsIgnoreCase("")) {
                    JOptionPane.showMessageDialog(null, "Please Enter The Serial Number", "Notification", JOptionPane.INFORMATION_MESSAGE, iconstock);
                } else {
                    if (serialgot.equalsIgnoreCase("all")) {

                        con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceeding in Deleting Will Delete The Entire Stock" + " \n" + "This should only be done if the stock is never needed" + "\n\n" + "If deleted no saving will be done to the archives", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconstock, option, option[1]);
                        if (selloption == 0) {
                            String sqldelete = "DELETE FROM store";
                            String sqldelete2 = "DELETE FROM tablecritical";
                            String sqldelete3 = "DELETE FROM tablesell";
                            String sqldelete4 = "DELETE FROM tablestockin";
                            prs = con.prepareStatement(sqldelete);
                            prs2 = con.prepareStatement(sqldelete2);
                            prs3 = con.prepareStatement(sqldelete3);
                            prs4 = con.prepareStatement(sqldelete4);
                            prs.execute();
                            prs2.execute();
                            prs3.execute();
                            prs4.execute();
                            if (prs != null && prs2 != null && prs3 != null && prs4 != null) {
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "Entire Stock Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                prs2.close();
                                prs3.close();
                                prs4.close();
                                con.close();

                                //method for reloading details
                                uplaod();
                            } else {
                                JOptionPane.showMessageDialog(null, tname.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Notification", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    } else {

                        con = DBConnector.getConnection();
                        String[] option = {"Yes", "No"};
                        int selloption = JOptionPane.showOptionDialog(null, "Proceeding in Deleting Will Delete The Entire Stock Of The Item" + " " + tname.getText() + " " + "Details with Serial_Number" + " " + serialgot + "\n" + "This should only be done if the product's stock is over" + "\n\n" + "If Deleted The Product Analysis Details Will Be Saved To The Archive \nAnd Product sold sales will be saved in the Past sold records store\nFor Future Reference.", "Deletion Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconstock, option, option[1]);
                        if (selloption == 0) {
                            /**
                             * call the method before deleting
                             * */
                            saveSoldRecords(tadvanced.getText());
                            //then delete
                            String sqldelete = "DELETE FROM store WHERE Mserial = '" + tadvanced.getText() + "'";
                            String sqldelete2 = "DELETE FROM tablecritical WHERE Mserial = '" + tadvanced.getText() + "'";
                            String sqldelete3 = "DELETE FROM tablesell WHERE Mserial = '" + tadvanced.getText() + "'";
                            String sqldelete4 = "DELETE FROM tablestockin WHERE Mserial = '" + tadvanced.getText() + "'";
                            prs = con.prepareStatement(sqldelete);
                            prs2 = con.prepareStatement(sqldelete2);
                            prs3 = con.prepareStatement(sqldelete3);
                            prs4 = con.prepareStatement(sqldelete4);
                            prs.execute();
                            prs2.execute();
                            prs3.execute();
                            prs4.execute();
                            if (prs != null && prs2 != null && prs3 != null && prs4 != null) {
                                //insert into past records
                                String sqladd = "INSERT INTO pastrecords(Mname,Mserial,StockIn,TaxIn,StockInCost,StockOut,TaxOut,StockOutCost,Profit,Loss,LastEdited) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                                prs5 = con.prepareStatement(sqladd);

                                //setting to database
                                prs5.setString(1, tname.getText());
                                prs5.setString(2, tserial.getText());
                                prs5.setDouble(3, Double.parseDouble(ttstock.getText()));
                                prs5.setDouble(4, Double.parseDouble(ttaxin.getText()));
                                prs5.setDouble(5, Double.parseDouble(ttcost.getText()));
                                prs5.setDouble(6, Double.parseDouble(ttsold.getText()));
                                prs5.setDouble(7, Double.parseDouble(ttaxout.getText()));
                                prs5.setDouble(8, Double.parseDouble(ttcostsold.getText()));
                                prs5.setDouble(9, Double.parseDouble(tprofit.getText()));
                                prs5.setDouble(10, Double.parseDouble(tloss.getText()));
                                prs5.setString(11, fileeditedlast);

                                prs5.execute();
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, "Entire Details of " + tname.getText() + " Deleted Successful", "Deletion Confirmation", JOptionPane.INFORMATION_MESSAGE, icon);
                                rs.close();
                                prs.close();
                                prs2.close();
                                prs3.close();
                                prs4.close();
                                prs5.close();
                                con.close();

                                //setting to null
                                tname.setText(null);
                                tserial.setText(null);
                                ttstock.setText(null);
                                ttcost.setText(null);
                                ttsold.setText(null);
                                ttcostsold.setText(null);
                                tprofit.setText(null);
                                tloss.setText(null);
                                tadvanced.setText(null);

                                //method for reloading details
                                uplaod();
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                                JOptionPane.showMessageDialog(null, tname.getText() + " " + "Deletion Failed", "Error Message", JOptionPane.ERROR_MESSAGE);
                                rs.close();
                                prs.close();
                                con.close();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Deletion Cancelled", "Notification", JOptionPane.INFORMATION_MESSAGE, icon);
                            rs.close();
                            prs.close();
                            con.close();
                        }
                    }
                }
            } catch (SQLException x) {
                xamppfailure.getCon();
            }

        });

        /**
         * call this methods here
         * */
        anaylzeData();
        uplaod();
        //end of actions
        //setting frame
        Fstock = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }
        Fstock.setUndecorated(true);
        Fstock.setIconImage(iconimage);
        Fstock.add(panelmain);
        Fstock.setVisible(true);
        //Fstore.setSize(1400, 780);
        Fstock.setSize(screenSize.width, screenSize.height);
        Fstock.revalidate();
        Fstock.pack();
        Fstock.revalidate();
        Fstock.setLocationRelativeTo(null);
        //Fstore.setResizable(false);
        Fstock.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //frame state to make components responsive
        Fstock.addWindowStateListener(e -> {
            // minimized
            if ((e.getNewState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                Fstock.revalidate();
                Fstock.pack();
                Fstock.revalidate();
                Fstock.setLocationRelativeTo(null);
            } // maximized
            else if ((e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
                Fstock.revalidate();
                Fstock.pack();
                Fstock.revalidate();
                Fstock.setLocationRelativeTo(null);
            }
        });

        bback.addActionListener(e -> {
            Fstock.setVisible(false);
            AnalysisType antype = new AnalysisType();
            antype.ChooseSection();
        });

    }
}
