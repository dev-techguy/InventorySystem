package login;

import com.toedter.calendar.JDateChooser;
import dbconnector.DBConnector;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;

import sun.applet.Main;

public class Welcome {

    //getting images
    URL url4 = Main.class.getResource("/img/tech.jpg");
    URL url1 = Main.class.getResource("/img/connection.png");
    URL url5 = Main.class.getResource("/img/logo.png");
    URL urlFile = Main.class.getResource("/SystemTimer/timer.txt");

    //setting images
    ImageIcon image4 = new ImageIcon(url4);
    ImageIcon imagelogo = new ImageIcon(url5);

    //setting ma default image icon to my frames
    final ImageIcon icon2 = new ImageIcon(url1);
    final ImageIcon iconlogo = new ImageIcon(url5);
    Image iconimage = new ImageIcon(url5).getImage();

    //getting components;
    JProgressBar current;
    JDateChooser dateexpry;
    JButton bsetTimer, bpowered;
    JPasswordField developerPass;
    JLabel notify, notify2, notify3, notify4, splashimg, slogan, pwby, limage, lexplain;

    //setting panels
    JPanel panelbar = new JPanel(new GridBagLayout());
    JPanel panelmain = new JPanel(new BorderLayout(0, 0));
    JPanel paneltimer = new JPanel(new GridBagLayout());

    //setting Frame
    JFrame Floadingbar, Ftimer;
    int numWelcome = 0;
    String medate, status;
    /**
     * {@link Calendar}
     */
    Date today = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
    String format;
    final String secretNumber = "Sherman";


    //database connectors
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement prs = null;

    //start xampp application
    DBConnector startdb = new DBConnector();

    //check on starting status of xampp
    private void stateXampp() {
        try {
            con = DBConnector.getConnection();
            if (con != null) {
                //do nothing
                System.out.println("do nothing");
            } else {
                startdb.startXampp();
            }
        } catch (SQLException bv) {
            startdb.startXampp();
        }
    }

    //try connetion
    private void trycon() {
        notify4.setForeground(Color.red.darker());
        Toolkit.getDefaultToolkit().beep();
        String[] option = {"Retry", "Exit"};
        int dbstate = JOptionPane.showOptionDialog(null, "Connection Failure\nRetry Starting The Database Application", "Database Connection Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, option, option[1]);
        if (dbstate == 0) {
            //start xampp
            startdb.startXampp();
            current.setForeground(Color.BLUE);
        }
        if (dbstate == 1) {
            System.exit(0);
        }
    }

    /**
     * Load timer interface
     */
    private void loadTimer() {
        Floadingbar.setVisible(false);
        Welcome wl = new Welcome();
        wl.checkTimerUI();
        numWelcome = 0;
    }

    /**
     * Load System interface
     */
    private void loadSystem() {
        Ftimer.setVisible(false);
        Welcome wl = new Welcome();
        wl.LoadingBar();
    }

    /**
     * Code for Welcome
     */
    private void LoadingBar() {
        notify = new JLabel("Loading System Modules...");
        notify.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify.setForeground(Color.BLUE);
        notify2 = new JLabel("Establishing Database Connection...");
        notify2.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify2.setForeground(Color.BLUE);
        notify3 = new JLabel("Connection Established...");
        notify3.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify3.setForeground(Color.BLUE);
        notify4 = new JLabel("Connection Failure...");
        notify4.setFont(new Font("Tahoma", Font.BOLD + Font.ITALIC, 12));
        notify4.setForeground(Color.BLUE);
        slogan = new JLabel("Inventory Management System");
        slogan.setFont(new Font("Tahoma", Font.PLAIN, 13));
        slogan.setForeground(Color.BLACK);
        pwby = new JLabel("Powered By TecksolKE");
        pwby.setFont(new Font("Tahoma", Font.BOLD, 10));
        pwby.setForeground(Color.BLACK);
        splashimg = new JLabel(image4);

        //progressbar
        current = new JProgressBar(0, 2000);
        current.setBorder(null);
        current.setBackground(Color.lightGray);
        current.setForeground(Color.BLUE);
        current.setValue(0);
        current.setPreferredSize(new Dimension(500, 5));
        current.setStringPainted(false);
        //current.setForeground(Color.blue.darker());

        //adding components to panels pMain
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 0, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        panelbar.add(splashimg, v);
//        v.insets = new Insets(10, 0, 0, 0);
//        v.gridy++;
//        panelbar.add(notify, v);
//        panelbar.add(notify2, v);
//        panelbar.add(notify3, v);
//        panelbar.add(notify4, v);
        v.insets = new Insets(0, 0, 0, 0);
        v.gridy++;
        v.anchor = GridBagConstraints.SOUTH;
        panelbar.add(current, v);
        v.anchor = GridBagConstraints.EAST;
        v.insets = new Insets(12, 0, 0, 10);
        v.gridy++;
        panelbar.add(slogan, v);
        v.insets = new Insets(0, 0, 2, 10);
        v.gridy++;
        panelbar.add(pwby, v);
        panelbar.setBackground(Color.lightGray);

        while (numWelcome < 2000) {
            current.setValue(numWelcome);
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
            }
            numWelcome += 95;
            //System.out.println(num);
            if (numWelcome == 95) {
                //frame code
                Floadingbar = new JFrame("Pharmacy System");
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    UIManager.put("nimbusBase", Color.blue);
                } catch (Exception c) {
                }
                Floadingbar.setUndecorated(true);
                Floadingbar.setIconImage(iconimage);
                Floadingbar.add(panelbar);
                Floadingbar.setVisible(true);
                Floadingbar.setSize(500, 380);
                Floadingbar.setLocationRelativeTo(null);
                Floadingbar.revalidate();
                Floadingbar.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                Floadingbar.setBackground(Color.black);
                //end of frame code
            }
            if (numWelcome == 190) {
                stateXampp();
            }
            if (numWelcome == 380) {
                stateXampp();
            }
            if (numWelcome == 950) {
                stateXampp();
            }
            if (numWelcome == 1425) {
                try {
                    try {
                        con = DBConnector.getConnection();
                    } catch (SQLException e) {
                        startdb.startXampp();
                        try {
                            con = DBConnector.getConnection();
                        } catch (SQLException ee) {
                            startdb.startXampp();
                            stateXampp();
                        }
                    }
                } catch (Exception trydb) {
                    current.setForeground(Color.red.darker());
                    trycon();
                }
            }
            if (numWelcome == 1615) {
                validateUI();
                if (numWelcome == 0) {
                    break;
                }
            }
            if (numWelcome == 2090) {
                try {
                    con = DBConnector.getConnection();
                    if (con != null) {
                        Floadingbar.setVisible(false);
                        Login r2 = new Login();
                        r2.LoginSection();
                        con.close();
                    } else {
                        current.setForeground(Color.red.darker());
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "Connection Failed\nPlease Exit the System and Start\nThe Database Application Manually", "Database Notification", JOptionPane.ERROR_MESSAGE, icon2);
                        //startdb.stopXampp();
                        System.exit(0);
                    }

                } catch (SQLException e) {
                    current.setForeground(Color.red.darker());
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Connection Failed\nPlease Exit the System and Start\nThe Database Application Manually", "Database Notification", JOptionPane.ERROR_MESSAGE, icon2);
                    startdb.stopXampp();
                    System.exit(0);
                }
            }
        }

    }

    /**
     * Code To check if Timer has expired
     */
    void checkTimerUI() {
        //date code
        dateexpry = new JDateChooser();
        dateexpry.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dateexpry.setPreferredSize(new Dimension(205, 35));
        dateexpry.setDateFormatString("yyyy-MM-dd");
        dateexpry.setFont(new Font("Tahoma", Font.PLAIN, 12));

        //add button
        bsetTimer = new JButton("RENEW");
        bsetTimer.setToolTipText("set new timer");
        bsetTimer.setFont(new Font("Tahoma", Font.BOLD, 15));
        bsetTimer.setBackground(Color.GREEN.darker());
        bsetTimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bpowered = new JButton("Powered By TecksolKE");
        bpowered.setToolTipText("check on https://tecksolke.com/");
        bpowered.setFont(new Font("Tahoma", Font.BOLD, 12));
        bpowered.setBackground(Color.LIGHT_GRAY);
        bpowered.setCursor(new Cursor(Cursor.HAND_CURSOR));

        developerPass = new JPasswordField(15);
        developerPass.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lexplain = new JLabel("Sorry...Your System Licensed Period has expired.");
        lexplain.setFont(new Font("Tahoma", Font.PLAIN, 13));
        limage = new JLabel(imagelogo);

        /**
         * Add components in a panel
         * */
        //panel panelcomp1
        GridBagConstraints v = new GridBagConstraints();
        v.anchor = GridBagConstraints.CENTER;
        v.insets = new Insets(0, 0, 10, 0);
        v.ipadx = 0;
        v.ipady = 0;
        v.gridx = 0;
        v.gridy = 0;
        paneltimer.add(limage, v);
        v.gridy++;
        paneltimer.add(lexplain, v);
        v.gridy++;
        paneltimer.add(dateexpry, v);
        v.gridy++;
        paneltimer.add(developerPass, v);
        v.gridy++;
        paneltimer.add(bsetTimer, v);
        v.insets = new Insets(0, 0, 30, 0);
        v.gridy++;
        paneltimer.add(bpowered, v);
        paneltimer.setBorder(new TitledBorder(""));

        /**
         * Add this to panelmain
         * */
        panelmain.add(paneltimer, "Center");
        panelmain.setBackground(Color.blue.brighter());
        panelmain.setBorder(new TitledBorder(""));


        /**
         * Perform an action here
         * */
        bsetTimer.addActionListener(e -> {
            if (((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Please Choose Date", "Notification", JOptionPane.WARNING_MESSAGE);
            } else if (developerPass.getText().equalsIgnoreCase("")) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Please Enter Password", "Validation", JOptionPane.WARNING_MESSAGE);
            } else {
                if (developerPass.getText().equalsIgnoreCase("{{|j@bvinny|}}")) {
                    //get date from
                    medate = ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText();
                    format = ((JTextComponent) dateexpry.getDateEditor().getUiComponent()).getText();
                    //proceed/not
                    String[] option = {"Yes", "No"};
                    int selloption = JOptionPane.showOptionDialog(null, "Are you sure you want to set the timer to\n" + medate, "Notification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, iconlogo, option, option[1]);
                    if (selloption == 0) {
                        //pass time to this method
                        timerChecker(medate, medate);
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, status, "System Timer Confirmation", JOptionPane.INFORMATION_MESSAGE);
                        /**
                         * Reload the system here
                         * */
                        loadSystem();
                    } else {
                        Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null, "You Cancelled", "System Timer Confirmation", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Wrong password", "Validation", JOptionPane.ERROR_MESSAGE);
                    developerPass.setText("");
                }
            }
        });

        /**
         * Direct client to tecksolke.com website
         * */
        bpowered.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URL("https://tecksolke.com/").toURI());
            } catch (Exception h) {
                JOptionPane.showMessageDialog(null, "Url can't be reached");
            }
        });

        //setting frame
        Ftimer = new JFrame("Pharmacy System");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("nimbusBase", Color.blue);
        } catch (Exception c) {
        }

        Ftimer.setUndecorated(false);
        Ftimer.setIconImage(iconimage);
        Ftimer.add(panelmain);
        Ftimer.setVisible(true);
        Ftimer.setSize(400, 500);
        Ftimer.setLocationRelativeTo(null);
        Ftimer.setResizable(false);
        Ftimer.revalidate();
        Ftimer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Ftimer.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                startdb.stopXampp();
            }
        });

    }


    /**
     * Method For Checking Timer
     *
     * @param dateSet
     */
    private void timerChecker(String dateSet, String secretCode) {
        /**
         * Encrypt Date
         * */
        String finalEncoding = secretCode + secretNumber;
        String generatedDate = null;
        // Create MessageDigest instance for SHA-384
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        //Add password bytes to digest
        md.update(finalEncoding.getBytes());
        //Get the hash's bytes
        byte[] bytes = md.digest();
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //Get complete hashed date in hex format
        generatedDate = sb.toString();

        try {
            con = DBConnector.getConnection();
//            String sqladd = "INSERT INTO systemtimer(Timer,SystemLimit,SecretCode) VALUES (?,?,?)";
//            prs = con.prepareStatement(sqladd);
//
//            //setting to database
//            prs.setString(1, dateSet);
//            prs.setString(2, format);
//            prs.setString(3, generatedDate);

            String sqlupdate = "UPDATE systemtimer set Timer = '" + dateSet + "',SecretCode = '" + generatedDate + "',SystemLimit = '" + format + "' WHERE ID = 1";
            prs = con.prepareStatement(sqlupdate);

            prs.execute();
            prs.close();
            con.close();

            status = "Timer set Successfully";
        } catch (SQLException x) {
            startdb.getCon();
            status = "Please Check Your Database Connection.";
        }
    }

    /**
     * Function Controlling user interface
     */
    private void validateUI() {
        try {
            con = DBConnector.getConnection();
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sqldatereached = "SELECT * FROM systemtimer WHERE ID = 1";
            rs = stmt.executeQuery(sqldatereached);
            rs.next();
            //store date in variable
            java.util.Date fetcheddate = rs.getDate("Timer");
            String checkCode = String.valueOf(fetcheddate);
            String fetechedCode = rs.getString("SecretCode");

            /**
             * Encrypt Date
             * */
            String finalDecoding = checkCode + secretNumber;
            String generatedCode = null;
            // Create MessageDigest instance for SHA-384
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA-512");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            //Add password bytes to digest
            md.update(finalDecoding.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed date in hex format
            generatedCode = sb.toString();

            //print here for confirmation
//            System.out.println(generatedCode);
//            System.out.println(fetechedCode);
//            System.out.println(checkCode);
//            System.out.println(medate);

            if (generatedCode.equals(fetechedCode)) {
                if (today.after(fetcheddate)) {
                    loadTimer();

                    rs.close();
                    stmt.close();
                    con.close();
                } else {
                    rs.close();
                    stmt.close();
                    con.close();
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "The system has stopped encountered an error\nPlease Contact an expert for assistance", "System Notification", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } catch (SQLException e) {
            trycon();
        }
    }

//    public static void main(String[] args) {
//        Welcome wl = new Welcome();
//        wl.LoadingBar();
//    }
}
