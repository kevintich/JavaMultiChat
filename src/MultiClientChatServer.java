import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class ClientEvent {
        public int character;
}

interface ClientListener {
        public void clientAction(ClientEvent ce, Socket senderClient);
}
/*
 * Inner- class HandleChatClient
 * creates inputstreams and output streams associated with specific sockets
 * Stores usernames for persons who have logged in 
 * Writes to a server log file.
 * 
 */
class HandleChatClient extends Thread {
        String userName;
        ClientListener cl;
        Socket client;
        InputStream is;
        OutputStream os;
        FileWriter logFileWriter;
        File file;
        BufferedWriter bw;
        StringBuffer sb;
        int count = 0;
	    Hashtable handleChatClients;  

        /**
         * @return socket
         */
        public Socket getSocket() {
                return client;
        }

        /**
         * @return username
         */
        public String getUserName() {
                return userName;
        }

        public HandleChatClient(Socket newClient, ClientListener cl, Hashtable handt) {
                this.cl = cl;
                client = newClient; // remember the client connection.
                handleChatClients = handt;
                
                try {

                        is = client.getInputStream();
                        os = client.getOutputStream();

                } catch (IOException i) {
                        System.err
                                        .println("Error while getting inputStream or outputStream from client Socket");
                }
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         * Run method is responsibe for getting input from the Clients via 
         * input streams.send back to the
         * 
         */
        public void run() {
        	try {
        		sleep(100);
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	
                ClientEvent ce;
                ce = new ClientEvent();
                FileWriter fw;
                try {
                        fw = new FileWriter("DAI_CHAT_Server.log");

                        BufferedWriter br = new BufferedWriter(fw);
                        
                        
                        /*
                         * Next 10lines are used to store all usernames immediately client type them
                         *in the Dialog Box.
                         */
                        sb = new StringBuffer();
                        System.err.println("Before the username stage");
                        do {
                                ce.character = is.read();
                                if (ce.character != 10) {
                                        sb.append((char)ce.character);
                                }
                        } while (ce.character != 10);
                        userName = sb.toString();
                        System.err.println("Passed the username stage");
                         
						synchronized (handleChatClients) {
                            handleChatClients.put(userName, client);// add client to list, once accepted...
                            System.out.println(userName + " yuko ndani");
                            OutputStream osp;
                            String usersOnline = "People Online: ";
                            String userNameEntry = userName + " is Online!\n";
                            String currentClient = "You are Online!\n";
                            Socket test;
                            Object[] keyset = handleChatClients.keySet().toArray();
                            
                            for(int j=0; j<keyset.length; j++) {
                           	 	if(j == keyset.length-1) {
                           	 		usersOnline += keyset[j].toString() + "\n";
                           	 	}
                           	 	else {
                           	 		usersOnline += keyset[j].toString() + ", ";
                           	 	}
                            }
                            
                            for(int i=0; i<keyset.length; i++) {
                                 test = (Socket)handleChatClients.get(keyset[i]);
                                 try {
                                     osp = test.getOutputStream();
                            
                                     /*if(test == client) {
                                    	for(int x=0; x<currentClient.length(); x++) {
                                    		osp.write((char)currentClient.charAt(x));
                                    		osp.flush();
                                    	}
                                     }
                                     else {
                                        for(int x=0; x<userNameEntry.length(); x++) {
                            	             osp.write((char)userNameEntry.charAt(x));
                            	             osp.flush();
                                        }
                                     }*/
                                     
                                     for(int x=0; x<usersOnline.length(); x++) {
                                    	 osp.write((char)usersOnline.charAt(x));
                                    	 osp.flush();
                                     }
                                     
                                 } catch(Exception xx) {
                            	       xx.printStackTrace();
                                 }
                            }
                            usersOnline = "";
                        }
				
                        /*
                         * While loop writes all characters to a log file
                         * 
                         */
                        while (true) {
                                try {
                                        ce.character = is.read();
                                        br.write(ce.character);
                                        br.flush();
                                        if (ce.character >= 0) {
                                                cl.clientAction(ce, client); // report back to server,the event and who sends
                                        } else {
                                                client.close();
                                                break;

                                        }
                                } catch (IOException i) {
                                        System.err
                                                        .println("Error while reading character from input socket");
                                        break; // Stop the thread by breaking out of loop.

                                }
                        }
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

        }

}

public class MultiClientChatServer implements ClientListener {
        Hashtable handleChatClients;
        ServerSocket ss;
        int count = 0;

        public static void main(String[] args) {
                MultiClientChatServer mccs = new MultiClientChatServer(9999);
        }

        public MultiClientChatServer(int portNo) {
                handleChatClients = new Hashtable(); // create a vector holding sockets of clients
                CheckStreams cs = new CheckStreams(handleChatClients);
                cs.start();
                
                try {
                        ss = new ServerSocket(portNo); // Make ready for connections from
                        // clients
                } catch (IOException i1) {
                        System.err.println("Could not .accept connection");
                }
                while (true) {
                        try {
                                Socket newClient = ss.accept();
                                // serve the new client on a platter for HandleChatClient.
                                // Make it possible via the ClientListener interface (this) to
                                // call back with events
                                // arising from each client for distribution to other clients.
                                HandleChatClient hcc = new HandleChatClient(newClient, this, handleChatClients);
                                hcc.start(); // start the thread handling the client.
                                hcc.getName();
                                
                        } catch (IOException i2) {
                                System.err.println("Could not .accept connection");
                        }
                } // while (true)
                
        }
        
        public void clientAction(ClientEvent ce, Socket senderClient) {
                Socket sTemp;
                InputStream is;
                OutputStream os;
                
                                
                synchronized (handleChatClients) {
                		int s = handleChatClients.size();
                		Object [] keyset = new Object[s];
                        keyset = handleChatClients.keySet().toArray();
                        
                        for(int i=0; i<keyset.length; i++) {
                                // Activity tracer: System.err.print(".");
                                sTemp = (Socket)handleChatClients.get(keyset[i]); 
                                
                                try {
                                        is = sTemp.getInputStream();
                                        os = sTemp.getOutputStream();
                                        
                                        os.write((char) ce.character); // write the event to the respective socket.(send message to all clients)
                                        os.flush();
//                                      os.close();
                                        
                                } catch (IOException i3) {
                                        i3.printStackTrace();
                                        System.err
                                         			.println("Cannot get inputStream or outputStream from client socket");
                               
                                        handleChatClients.remove(keyset[i]); // removes a connection if errors on it...
                                }
                        }
                } // synchronized on sockets: Send to all clients and remove dormant
                // clients...
        }

}

class CheckStreams extends Thread {
	Hashtable handleChatClients;
	
	public CheckStreams(Hashtable handleChatClients) {
		this.handleChatClients = handleChatClients;
	}
	
	public void run() {
		while (true) {
		    //System.out.println("Running");
	     	try {
	    		sleep(500);
	    	} catch(InterruptedException e) {
		    	e.printStackTrace();
		    }
            //System.out.println("Awake");
        
		    synchronized(handleChatClients) {
			   //System.out.println("In block");
               Object[] keyset = handleChatClients.keySet().toArray();
               String userLogOff;
               String usersOnline = "People Online: ";
               Socket off;
               OutputStream osp;
               
               for(int i=0; i<keyset.length; i++) {
            	  if(((Socket) handleChatClients.get(keyset[i])).isClosed()) {
            		   //System.out.println("Found");
                       handleChatClients.remove(keyset[i]);

                       keyset = handleChatClients.keySet().toArray();
                       
                       //userLogOff = keyset[i].toString() + " is Offline!\n";
                       
                       for(int g=0; g<keyset.length; g++) {
                    	   if(g == keyset.length-1) {
                    		   usersOnline += keyset[g].toString() + "\n";
                    	   }
                    	   else {
                    		   usersOnline += keyset[g].toString() + ", ";
                    	   }
                       }
                    
                       for(int j=0; j<keyset.length; j++) {
                           off = (Socket)handleChatClients.get(keyset[j]);
                           
                           try {
                               osp = off.getOutputStream();
                   
                             
                        	   for(int x=0; x<usersOnline.length(); x++) {
                        		   osp.write((char)usersOnline.charAt(x));
                        		   osp.flush();
                        	   }
                            
                           } catch(Exception xx) {
                   	           xx.printStackTrace();
                           }
                       }
                       usersOnline = "";
                       
            	  } 
               }
		    }	
		}
	}
}


