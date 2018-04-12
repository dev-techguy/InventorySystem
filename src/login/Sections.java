package login;

import analysis.AnalysisType;
import dbconnector.DBConnector;
import membership.Worker;
import ordering.AddMedicine;
import selling.SellingDrug;
import sellingstore.SellAnalysis;
import store.MedicineStore;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Sections {

    //getting images
    URL url = Main.class.getResource("/img/logo.png");
    URL url2 = Main.class.getResource("/img/sell.png");
    URL url3 = Main.class.getResource("/img/solditems.png");
    URL url4 = Main.class.getResource("/img/view.png");
    URL url5 = Main.class.getResource("/img/store.png");
    URL url6 = Main.class.getResource("/img/addstore.png");
    URL url8 = Main.class.getResource("/img/analysis.png");
    URL url9 = Main.class.getResource("/img/out.png");
    URL url10 = Main.class.getResource("/img/info.png");
    URL url11 = Main.class.getResource("/img/setting.png");
    URL url12 = Main.class.getResource("/img/logout1.png");
    URL url13 = Main.class.getResource("/img/dbconnection.png");
    URL url14 = Main.class.getResource("/img/about.png");
    URL url15 = Main.class.getResource("/img/settingslogo.png");
    URL urltechguy = Main.class.getResource("/img/TechGuy.png");
    URL url1 = Main.class.getResource("/img/connection.png");
    URL urlbackup = Main.class.getResource("/img/backup.png");
    URL urlvinny = Main.class.getResource("/img/TechGuy.jpg");
    URL urlphone = Main.class.getResource("/img/p.png");

    //setting images
    ImageIcon imagelogo = new ImageIcon(url);
    ImageIcon imagesell = new ImageIcon(url2);
    ImageIcon imagepayments = new ImageIcon(url3);
    ImageIcon imageview = new ImageIcon(url4);
    ImageIcon imagestore = new ImageIcon(url5);
    ImageIcon imageadd = new ImageIcon(url6);
    ImageIcon imageanalysis = new ImageIcon(url8);
    ImageIcon imageout = new ImageIcon(url9);
    ImageIcon imageinfo = new ImageIcon(url10);
    ImageIcon imagesetting = new ImageIcon(url11);
    ImageIcon imagelogout = new ImageIcon(url12);
    ImageIcon imagecon = new ImageIcon(url13);
    ImageIcon imageabout = new ImageIcon(url14);
    ImageIcon imagesettimgslogo = new ImageIcon(url15);
    ImageIcon imagebackup = new ImageIcon(urlbackup);

    //setting ma default image icon to my frames
    final ImageIcon iconbup = new ImageIcon(urlbackup);
    final ImageIcon iconLogo = new ImageIcon(url);
    Image iconimage = new ImageIcon(url).getImage();
    Image imagetechguy = new ImageIcon(urltechguy).getImage();
    Image imageguy = new ImageIcon(urlvinny).getImage();
    final ImageIcon iconcon = new ImageIcon(url13);

    //setting components
    JLabel pharmacy, Lhome, Lhome2, lsettings, LSlogo, lnew, lsell, lsells, lstore, lmembers, lanalysis, testdb, readsystem, bup;
    public JButton badd;
    public JButton bstore;
    public JButton bpayments;
    public JButton bargment;
    JButton bselling;
    JButton blogout;
    JButton binfo;
    JButton bexit;
    JButton bsettings, bselling2;
    public JButton banalysis, backup;
    JButton btestconnection;
    JButton babout;
    JButton bback;

    //setting panel
    JPanel panelbuttons = new JPanel(new GridBagLayout());
    JPanel panelsetttings = new JPanel(new GridBagLayout());
    JPanel panelop = new JPanel(new GridBagLayout());
    JPanel panelcard = new JPanel(new CardLayout(0, 0));
    JPanel opmain = new JPanel(new BorderLayout(0, 0));

    //setting Frame
    JFrame Frameop;

    /**
     * {@link Calendar}Dates
     */
    Date today = new Date();
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
    private static final DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
    LocalDateTime now = LocalDateTime.now();
    String fileeditedlast = dtf.format(now);
    String fileeditedlast2 = dtf2.format(now);
    String expiryDate;

    //database connectors
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement prs = null;

    //call class for starting
    DBConnector xampptest = new DBConnector();

    /**
     * Fetch Date to show to client
     */
    private void showExpiryDate() {
        try {
            con = DBConnector.getConnection();
            String fetchTime = "SELECT * FROM systemtimer";
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(fetchTime);
            if (rs.next()) {
                java.util.Date fetcheddate = rs.getDate("Timer");
                expiryDate = rs.getString("SystemLimit");
                /**
                 * Check for date expiry license
                 * */
                if (today.after(fetcheddate)) {
                    Frameop.setVisible(false);
                    Welcome welcome = new Welcome();
                    welcome.checkTimerUI();
                }
                rs.close();
                stmt.close();
                con.close();
            } else {
                expiryDate = "This software has no license expiry";
                rs.close();
                stmt.close();
                con.close();
            }
        } catch (SQLException e) {
            xampptest.getCon();
        }
    }

    public void Operations() {
        Lhome = new JLabel("");
        Lhome.setFont(new Font("Tahoma", Font.BOLD, 20));
        Lhome.setForeground(Color.BLACK);
        Lhome2 = new JLabel(fileeditedlast2);
        Lhome2.setFont(new Font("Tahoma", Font.BOLD, 20));
        Lhome2.setForeground(Color.BLUE);
        LSlogo = new JLabel(imagesettimgslogo);
        //pharmacy = new JLabel("PHARMACY MANAGEMENT SYSTEM");
        pharmacy = new JLabel("INVENTORY MANAGEMENT SYSTEM");
        pharmacy.setFont(new Font("Tahoma", Font.BOLD, 28));
        pharmacy.setForeground(Color.BLACK);
        lsettings = new JLabel("SETTINGS");
        lsettings.setFont(new Font("Tahoma", Font.BOLD, 20));
        lsettings.setForeground(Color.BLACK);

        lnew = new JLabel("New");
        lnew.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lsell = new JLabel("Sell");
        lsell.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lsells = new JLabel("Daily Sells");
        lsells.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lstore = new JLabel("Store");
        lstore.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lmembers = new JLabel("Users");
        lmembers.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lanalysis = new JLabel("Analyze");
        lanalysis.setFont(new Font("Tahoma", Font.PLAIN, 13));
        readsystem = new JLabel("Read Manual");
        readsystem.setFont(new Font("Tahoma", Font.PLAIN, 13));
        testdb = new JLabel("Test Connection");
        testdb.setFont(new Font("Tahoma", Font.PLAIN, 13));
        bup = new JLabel("Get Backup");
        bup.setFont(new Font("Tahoma", Font.PLAIN, 13));

        badd = new JButton(imageadd);
        badd.setToolTipText("Add New Product To The Store");
        badd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bstore = new JButton(imagestore);
        bstore.setToolTipText("Check Product In Store");
        bstore.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bpayments = new JButton(imagepayments);
        bpayments.setToolTipText("View Sold/Payed Stock");
        bpayments.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bargment = new JButton(imageview);
        bargment.setToolTipText("View/Add Worker");
        bargment.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bselling = new JButton(imagesell);
        bselling.setToolTipText("Sell Your Products");
        bselling.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bselling2 = new JButton(imagesell);
        bselling2.setToolTipText("Sell Your Products");
        bselling2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        binfo = new JButton(imageinfo);
        binfo.setToolTipText("System Developer Profile Information");
        binfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        binfo.setBackground(Color.LIGHT_GRAY);
        bexit = new JButton(imageout);
        bexit.setToolTipText("Exit System");
        bexit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bexit.setBackground(Color.GREEN.darker());
        bsettings = new JButton(imagesetting);
        bsettings.setToolTipText("System Settings");
        bsettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bsettings.setBackground(Color.LIGHT_GRAY);
        banalysis = new JButton(imageanalysis);
        banalysis.setToolTipText("Analyze");
        banalysis.setCursor(new Cursor(Cursor.HAND_CURSOR));
        blogout = new JButton(imagelogout);
        blogout.setToolTipText("Logout");
        blogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        blogout.setBackground(Color.LIGHT_GRAY);
        backup = new JButton(imagebackup);
        backup.setToolTipText("Backup your stock Data");
        backup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btestconnection = new JButton(imagecon);
        btestconnection.setToolTipText("Test Database Connection");
        btestconnection.setCursor(new Cursor(Cursor.HAND_CURSOR));
        babout = new JButton(imageabout);
        babout.setToolTipText("Help/Read On How To Use The System");
        babout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bback = new JButton("BACK");
        bback.setFont(new Font("Tahoma", Font.BOLD, 15));
        bback.setBackground(Color.LIGHT_GRAY);
        bback.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //sliding text
        String s = "Powered By TecksolKE";
        marquee mp = new marquee(s, 135);
        //mp.setBackground(Color.lightGray);
        mp.start();

        //adding components to panels panelop
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelop.add(Lhome, v);
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        panelop.add(Lhome2, v);
        v.insets = new Insets(10, 0, 20, 0);
        v.gridy++;
        panelop.add(pharmacy, v);

        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridy = 3;
        v.gridx = 0;
        panelop.add(badd, v);
        v.insets = new Insets(0, 0, 25, 0);
        v.gridy++;
        panelop.add(lnew, v);
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        panelop.add(bstore, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelop.add(lstore, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridy = 3;
        v.gridx = 0;
        panelop.add(bselling, v);
        panelop.add(bselling2, v);
        v.insets = new Insets(0, 0, 25, 0);
        v.gridy++;
        panelop.add(lsell, v);
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        panelop.add(bargment, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelop.add(lmembers, v);

        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridy = 3;
        v.gridx = 0;
        panelop.add(bpayments, v);
        v.insets = new Insets(0, 0, 25, 0);
        v.gridy++;
        panelop.add(lsells, v);
        v.insets = new Insets(15, 0, 0, 0);
        v.gridy++;
        panelop.add(banalysis, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        panelop.add(lanalysis, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(15, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridy = 8;
        v.gridx = 0;
        panelop.add(mp, v);

        //adding components to panels panelbuttons
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 15, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelbuttons.add(blogout, v);
        v.gridy++;
        panelbuttons.add(bsettings, v);
        v.gridy++;
        panelbuttons.add(binfo, v);
        v.gridy++;
        panelbuttons.add(bexit, v);
        panelbuttons.setBorder(new TitledBorder(""));
        panelbuttons.setBackground(Color.LIGHT_GRAY);

        //adding tp panelsettings
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 15, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelsetttings.add(LSlogo, v);
        v.gridy++;
        panelsetttings.add(lsettings, v);

        v.anchor = GridBagConstraints.WEST;
        v.insets = new Insets(20, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 2;
        panelsetttings.add(btestconnection, v);
        v.insets = new Insets(110, 0, 0, 0);
        panelsetttings.add(testdb, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(20, 200, 0, 200);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 2;
        panelsetttings.add(backup, v);
        v.insets = new Insets(110, 0, 0, 0);
        panelsetttings.add(bup, v);

        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(20, 200, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 2;
        panelsetttings.add(babout, v);
        v.insets = new Insets(110, 0, 0, 0);
        panelsetttings.add(readsystem, v);

        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(50, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 3;
        panelsetttings.add(bback, v);

        //adding components to panelcard
        panelcard.add(panelop);
        panelcard.add(panelsetttings);
        java.awt.CardLayout cardLayout = (java.awt.CardLayout) (panelcard.getLayout());
        bback.addActionListener(e -> cardLayout.first(panelcard));
        bsettings.addActionListener(e -> cardLayout.last(panelcard));

        //adding components to panels opmain
        opmain.add(panelbuttons, "West");
        opmain.add(panelcard, "Center");
        opmain.revalidate();

        //action events
        btestconnection.addActionListener((ActionEvent e) -> {
            try {
                con = DBConnector.getConnection();
                if (con != null) {
                    JOptionPane.showMessageDialog(null, "Database Connection Is Well Established", "Connection Establishment", JOptionPane.INFORMATION_MESSAGE, iconcon);
                } else {
                    xampptest.getCon();
                }
            } catch (SQLException err) {
                xampptest.getCon();
            }
        });
        backup.addActionListener(event -> {
            DBConnector bup = new DBConnector();
            bup.getBackup();
        });
        babout.addActionListener(event -> {
            try {
                Desktop.getDesktop().browse(new URL("https://youtu.be/nubuuyAW50c").toURI());
            } catch (Exception h) {
                JOptionPane.showMessageDialog(null, h);
            }
        });
        binfo.addActionListener(event -> {
            String[] reachus = {"Phone", "Mail", "Youtube", "TecksolKE"};
            final ImageIcon iconvinny = new ImageIcon(urlvinny);
            Image newimgvinny = imageguy.getScaledInstance(130, 160, Image.SCALE_SMOOTH);
            ImageIcon newIconvinny = new ImageIcon(newimgvinny);
            //
            final ImageIcon icon = new ImageIcon(urltechguy);
            Image newimg = imagetechguy.getScaledInstance(200, 230, Image.SCALE_SMOOTH);
            ImageIcon newIcon = new ImageIcon(newimg);
            Toolkit.getDefaultToolkit().beep();
            int getus = JOptionPane.showOptionDialog(null, "<---TecksolKE--->" + "\n" + "\b"
                    + "We are glad you are using our system incase of any bug" + "\n"
                    + "or system failure please contact us for assistance." + "\n" + "\b"
                    + "For more Information On how to operate the system Watch" + "\n"
                    + "the Tutorial In Our Youtube Channel" + "\n" + "\b"
                    + "Click the links below\n\n"
                    + "[ System License Expires on ~~:::~~ " + expiryDate + " ]", "About System", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconLogo, reachus, reachus[1]);
            if (getus == 0) {
                ImageIcon newp = new ImageIcon(urlphone);
                JOptionPane.showMessageDialog(null, "SOFTWARE DEVELOPER IN TECKSOLKE" + "\n" + "Name:TechGuy" + "\n"
                        + "Mobile:0713255791", "About System Developer", JOptionPane.INFORMATION_MESSAGE, newp);
            } else if (getus == 1) {
                try {
                    Desktop.getDesktop().browse(new URL("mailto:gridtechideal@gmail.com").toURI());
                } catch (Exception h) {
                    JOptionPane.showMessageDialog(null, "Email Failed");
                }
            } else if (getus == 2) {
                try {
                    Desktop.getDesktop().browse(new URL("https://youtu.be/nubuuyAW50c").toURI());
                } catch (Exception h) {
                    JOptionPane.showMessageDialog(null, "Url can't be reached");
                }
            } else if (getus == 3) {
                try {
                    Desktop.getDesktop().browse(new URL("https://tecksolke.com/").toURI());
                } catch (Exception h) {
                    JOptionPane.showMessageDialog(null, "Url can't be reached");
                }
            }

        });

        //end of actions
        Frameop = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception ignored) {
        }
        Frameop.setUndecorated(true);
        Frameop.setIconImage(iconimage);
        Frameop.add(opmain);
        Frameop.setVisible(true);
        Frameop.setSize(750, 370);
        Frameop.setLocationRelativeTo(null);
        Frameop.revalidate();
        Frameop.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        /**
         * Call this function
         * */
        showExpiryDate();

        //actions
        bpayments.addActionListener(e -> {
            Frameop.setVisible(false);
            SellAnalysis stoday = new SellAnalysis();
            stoday.stocksell();
        });
        bexit.addActionListener(e -> {
            String[] option = {"Yes", "No"};
            Toolkit.getDefaultToolkit().beep();
            int selloption = JOptionPane.showOptionDialog(null, "Are you Sure you want to Exit System", "Exit Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconLogo, option, option[1]);
            if (selloption == 0) {
                xampptest.stopXampp();
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "System still Running Continue With your Work", "Exit Message", JOptionPane.INFORMATION_MESSAGE, iconLogo);
            }

        });
        blogout.addActionListener(e -> {
            String[] option = {"Yes", "No"};
            Toolkit.getDefaultToolkit().beep();
            int selloption = JOptionPane.showOptionDialog(null, "Are you Sure you want to logout", "Logout Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconLogo, option, option[1]);
            if (selloption == 0) {
                Frameop.setVisible(false);
                Login r3 = new Login();
                r3.LoginSection();
            } else {
                JOptionPane.showMessageDialog(null, "Still Logged In Continue With your Work", "Logout Message", JOptionPane.INFORMATION_MESSAGE, iconLogo);
            }

        });
        badd.addActionListener(e -> {
            Frameop.setVisible(false);
            AddMedicine dadd = new AddMedicine();
            dadd.NewDrug();
        });
        bselling.addActionListener(e -> {
            Frameop.setVisible(false);
            SellingDrug dsell = new SellingDrug();
            dsell.DrugNameFound();
            dsell.SellDrug();
            dsell.bbacksell.setVisible(false);
            dsell.bselluser.setVisible(false);
            dsell.bcancel.setVisible(true);
            dsell.bcanceluser.setVisible(false);
            dsell.bsell.setVisible(true);
            dsell.bback.setVisible(true);
        });
//        bselling2.addActionListener(e -> {
//            Frameop.setVisible(false);
//            SellingDrug dsell = new SellingDrug();
//            dsell.DrugNameFound();
//            dsell.SellDrug();
//            dsell.bbacksell.setVisible(true);
//            dsell.bback.setVisible(false);
//        });
        bstore.addActionListener(e -> {
            Frameop.setVisible(false);
            MedicineStore storem = new MedicineStore();
            storem.MStore();
        });
        bargment.addActionListener(e -> {
            Frameop.setVisible(false);
            Worker pw = new Worker();
            pw.WorkersFound();
            pw.workers();
        });
        banalysis.addActionListener(e -> {
            Frameop.setVisible(false);
            AnalysisType analyze = new AnalysisType();
            analyze.ChooseSection();

        });
    }

//    public static void main(String[] args) {
//        Sections sec = new Sections();
//        sec.Operations();
//    }
}
