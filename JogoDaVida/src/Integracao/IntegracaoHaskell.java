package Integracao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class IntegracaoHaskell {
	private static String arg1 = "runhaskell";
	
	public static String writeArg (String file, String arg) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(arg1, file, arg);
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String s = reader.readLine();
		reader.close();
		return s;
	}
}
