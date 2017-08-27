package integracao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;

public class IntegracaoHaskell {
	private static String arg1 = "ghci";
	private static String lastLine;
	private static Process interpreterProcess;
	private static Semaphore lineReading; 
	private static Thread reader;
	private static BufferedWriter writer;
	
	public static void start (String file) throws ProcessNotInitializedException {
		ProcessBuilder builder = new ProcessBuilder(arg1, file);
		builder.redirectErrorStream(true);
		try {
			interpreterProcess = builder.start();
		} catch (IOException e) {
			throw new ProcessNotInitializedException(arg1, e);
		}
		lineReading = new Semaphore(0);
		reader = new Thread( () -> {
				Scanner reader = new Scanner(interpreterProcess.getInputStream());
				while (!reader.nextLine().contains("modules loaded"));
				String s;
				while (interpreterProcess.isAlive()) {
					s = reader.next();
					if (!s.startsWith("*") && !s.endsWith(">")) {
						lastLine = s;
						lineReading.release();
					}
				}
				reader.close();
		});
		reader.start();
		writer = new BufferedWriter(
				new OutputStreamWriter(interpreterProcess.getOutputStream()));
	}
	public static void writeArg (UtilizadorIntegracao user, String arg2, String arg3, String arg4) throws IOException, ProcessNotInitializedException {
//		Scanner reader = new Scanner(process.getInputStream());
		lastLine = null;
		try {
			writer.write(arg2+" "+arg3+" "+arg4+"\n");
			writer.flush();
		} catch (NullPointerException e) {
				throw new ProcessNotInitializedException(arg1, e);
		} catch (IOException e) {
			throw e;
		}
		Thread waiter = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					lineReading.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String s = lastLine;
				Platform.runLater( () -> {
					user.programOutput(s); });
			}
		});
		waiter.start();
	}
}