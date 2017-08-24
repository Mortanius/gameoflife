package Integracao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IntegracaoHaskell {
	private static String arg1 = "runhaskell";
	
	public static String writeArg (String file, String arg2, String arg3) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(arg1, file, arg2, arg3);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String s = reader.readLine();
		reader.close();
		return s;
	}
}
