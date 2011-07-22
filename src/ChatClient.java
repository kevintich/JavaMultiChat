
import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import static java.lang.System.out;

public class ChatClient extends JFrame implements KeyListener, ActionListener {
        /**
         * 
         */
        private static final long serialVersionUID = -1789035028737334958L;
        JFrame jf;
        String uname;
        PrintWriter pw, pw2;
        BufferedReader br;
        JTextArea taMessages;
        JTextField tfInput;
        JButton btnSend, btnExit;
        Socket client;
        static String name;
        GregorianCalendar gc = new GregorianCalendar();
        DateFormat dfdate = DateFormat.getDateInstance(DateFormat.MEDIUM);
        DateFormat dftime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        String datenow = dfdate.format(gc.getTime());
        String timenow = dftime.format(gc.getTime());
        static ReadConfigurations rc = new ReadConfigurations();
        

        public ChatClient(String uname, String servername) throws Exception {
                // super(uname); // set title for frame
                jf = new JFrame(uname);// set title for frame
                
                jf.addWindowListener(new WindowAdapter() {
                	public void windowClosing(WindowEvent e) {
                		System.exit(0);
                	}
                });
                
                this.uname = uname;
                client = new Socket(servername, rc.getPortNumber());
                System.err.println("Port number: " + servername + ":"
                                + "9999");
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                pw = new PrintWriter(client.getOutputStream(), true);

                pw.println(uname.toUpperCase());
                buildInterface();
                new MessagesThread().start(); // create thread and starts thread for
                                                                                // listening for displaying all messages
                
        }

        /**
         * buildInterface method is used to set-up the UI for clients communication
         */
        public void buildInterface() {
                btnSend = new JButton("Send");
                btnExit = new JButton("Exit");
                taMessages = new JTextArea();
                taMessages.setRows(10); 
                taMessages.setColumns(50);
                taMessages.setEditable(false);
                taMessages.setWrapStyleWord(true);
                taMessages.setLineWrap(true);
                taMessages.setAutoscrolls(true);
                tfInput = new JTextField(50);
                JScrollPane sp = new JScrollPane(taMessages,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jf.add(sp, "Center");
                JPanel bp = new JPanel(new FlowLayout());
                jf.add(onlineusers, "North");
                tfInput.addKeyListener(this);
                bp.add(tfInput);
                // bp.add(btnSend);
                // bp.add(btnExit);
                jf.add(bp, "South");
                // btnSend.addActionListener(this);
                btnExit.addActionListener(this);
                jf.setSize(500, 300);
                jf.setPreferredSize(new Dimension(800, 500));
                jf.setLocationRelativeTo(null);
                jf.setVisible(true);
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.pack();
        }

        JLabel onlineusers = new JLabel("Online Users: ");
        /*
         * (non-Javadoc)
         * 
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent evt) {
                GregorianCalendar gc = new GregorianCalendar();
                DateFormat dfdate = DateFormat.getDateInstance(DateFormat.MEDIUM);
                DateFormat dftime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
                String datenow = dfdate.format(gc.getTime());
                String timenow = dftime.format(gc.getTime());
                if (evt.getSource() == btnExit) {
                        pw.println("\n" + name.toUpperCase() + " has logged out @ "
                                        + timenow); // send end to server so that server know about
                        // the termination
                        // pw.println(name);
                        System.exit(0);
                }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent) used
         * to send chat messages when the Return key is pressed also displays what
         * the client has typed on the textfield back to the textarea
         */
        public void keyPressed(KeyEvent ke) {
                char c = ke.getKeyChar();
                if (c == (char) 10) {
                        GregorianCalendar gc = new GregorianCalendar();
                        DateFormat dfdate = DateFormat.getDateInstance(DateFormat.MEDIUM);
                        DateFormat dftime = DateFormat.getTimeInstance(DateFormat.MEDIUM);
                        String datenow = dfdate.format(gc.getTime());
                        String timenow = dftime.format(gc.getTime());
                        pw.println(datenow + ", " + timenow + ":  " + name.toUpperCase() + ": "
                                        + tfInput.getText());

                        //taMessages.append(timenow + ":  " + name.toUpperCase() + ": "
                         //               + tfInput.getText() + "\n");
                        //taMessages.getAutoscrolls();
                        taMessages.setCaretPosition(taMessages.getText().length());
                        tfInput.setText("");

                }
        }

        public void keyTyped(KeyEvent ke) {
        }

        public void keyReleased(KeyEvent ke) {
        }

        public static void main(String... args) {

                // take username from user
                name = JOptionPane.showInputDialog(null, "Enter your name :",
                                "Username", JOptionPane.PLAIN_MESSAGE);
                // gun.getNames(name);

                if (name.equals("")) {
                        JOptionPane.showMessageDialog(null, "Name should not be Empty");
                        System.exit(0);
                }
                // gets the hostname from a file via class readConfigurations
                String servername = rc.getHostName();
                try {
                        // creates a new instance of ChatClien2 with the client's username
                        // and host address/servername
                        new ChatClient(name.toUpperCase(), servername);
                } catch (Exception ex) {
                        ex.printStackTrace();
                        out.println("Error --> " + ex.getMessage());
                        
                }

        } // end of main

        // inner class for Messages Thread. Thread responsible for displaying client
        // messsages
        class MessagesThread extends Thread {
                public void run() {
                        String line;
                        try {
                                while (true) {
                                        line = br.readLine();
                                        taMessages.append(line + "\n");
                                        taMessages.setCaretPosition(taMessages.getText().length());

                                } // end of while
                        } catch (Exception ex) {
                        }
                }
        }

       
} // end of client

