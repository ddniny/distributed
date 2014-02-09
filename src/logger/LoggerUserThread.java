package logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggerUserThread extends Thread {
	BufferedReader in = null;
	String cmdInput = null;
	
	public void run() {
		while (true) {
			System.out.println("Enter 1 to dump the log file.");
			in = new BufferedReader(new InputStreamReader(System.in));
			try {
				cmdInput = in.readLine();
				if (cmdInput.equals("1")) {
					Logger.getInstance().writeLogToFile();
				}
			} catch (IOException e) {
				System.err.println("ERROR: Reader corrput");
				e.printStackTrace();
			}
			
		}
	}
}
