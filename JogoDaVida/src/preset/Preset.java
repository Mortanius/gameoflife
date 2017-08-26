package preset;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Preset {
	private String nome;
	private List<int[]> pontos;
	private Comparator<int[]> pointComp = new Comparator<int[]>() {
		@Override
		public int compare(int[] p1, int[] p2) {
			for (int c = p1.length-1; c > 0; c--) {
				try {
					if (p1[c] > p2[c])
						return 1;
					else if (p1[c] < p2[c])
						return -1;
				}catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			return 0;
		}
	};
	public Preset (String nome, String pontos) {
		this.nome = nome;
		this.pontos = new ArrayList<int[]>();
		Scanner s = new Scanner(pontos);
		while (s.hasNextInt()) {
			int[] ponto = new int[2];
			ponto[0] = s.nextInt();
			ponto[1] = s.nextInt();
			this.pontos.add(ponto);
		}
		s.close();
		this.pontos.sort(pointComp);
	}
	public Preset (int[][] pontos) {
		this.pontos = new ArrayList<int[]>();
		for (int[] p : pontos) {
			this.pontos.add(p);
		}
		this.pontos.sort(pointComp);
	}
	public String getNome() {
		return nome;
	}
	public String getStringPontos() {
		String s = "";
		for (int[] p : pontos) {
			s = s + " "+ p[0] + " " + p[1];
		}
		return s;
	}
	public List<int[]> getPontos() {
		return pontos;
	}
	public String toString() {
		return nome;
	}
}