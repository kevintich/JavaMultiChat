import java.io.*;

public class ReadConfigurations {
	String hostFile = "HostName.cfg";
	String portFile = "PortNumber.cfg";

	public String getHostName() {
		StringBuffer sb = new StringBuffer();
		try {
			File file = new File(hostFile);
			FileReader reader = new FileReader(file);
			BufferedReader in = new BufferedReader(reader);
			String string;

			while ((string = in.readLine()) != null) {
				System.out.println(string);
				sb.append(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public int getPortNumber() {
		StringBuffer sb = new StringBuffer();
		int portNumberInt;
		try {
			File file = new File(portFile);
			FileReader reader = new FileReader(file);
			BufferedReader in = new BufferedReader(reader);
			String string;

			while ((string = in.readLine()) != null) {
				System.out.println(string);
				sb.append(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String portNumberString = sb.toString();
		return portNumberInt = Integer.parseInt(portNumberString);

	}
	public static void main(String []args){
		ReadConfigurations rc = new ReadConfigurations();
		rc.getHostName();
		rc.getPortNumber();
	}
}
