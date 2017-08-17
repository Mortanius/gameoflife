package Integracao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class Teste {
	public static void main(String[] args) {
		String s = null;
		try {
			s = IntegracaoHaskell.writeArg("F:\\test.hs", "[(1,1),(1,4),(1,2),(1,3)]");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(s);
		Scanner scanner = new Scanner(s);
		while (scanner.hasNextInt()) {
		int i = scanner.nextInt();
		System.out.println("int "+ i);
		}
		scanner.close();
	}
}
