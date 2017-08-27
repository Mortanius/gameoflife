package integracao;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class IntegracaoHaskell {
	private static String arg1 = "ghci";
	private static String lastLine;
	private static Process interpreterProcess;
	private static Semaphore lineReading; 
	private static Thread reader;
	private static BufferedWriter writer;
	
	public static void start (String file) {
		ProcessBuilder builder = new ProcessBuilder(arg1, file);
		builder.redirectErrorStream(true);
		try {
			interpreterProcess = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lineReading = new Semaphore(0);
		reader = new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner reader = new Scanner(interpreterProcess.getInputStream());
				while (!reader.next().equals("*Main>"));
				while (interpreterProcess.isAlive()) {
						lineReading.release(lastLine != null && lastLine.startsWith("*") ? 1 : 2);
						try {
							lineReading.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						lastLine = reader.next();
				}
				reader.close();
			}
		});
		reader.start();
		writer = new BufferedWriter(
				new OutputStreamWriter(interpreterProcess.getOutputStream()));
	}
	public static String writeArg (String file, String arg2, String arg3, String arg4) throws IOException {
//		Scanner reader = new Scanner(process.getInputStream());
		if (interpreterProcess == null)
			start(file);
		lastLine = null;
		writer.write(arg2+" "+arg3+" "+arg4+"\n");
		writer.flush();
		String s = null;
		while (s == null) {
			try {
				lineReading.acquire();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			s = lastLine;
			lineReading.release();
		}
		return s;
	}
}