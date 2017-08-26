package preset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Scanner;

import javafx.collections.FXCollections;

public class PresetFileRepository {
	private List<Preset> presets;
	private String file;
	public PresetFileRepository (String fileName, Preset defaultPreset) {
			presets = FXCollections.observableArrayList();
			presets.add(defaultPreset);
			file = System.getProperty("user.dir") + fileName;
			try {
				read();
			} catch (FileNotFoundException e) {
				//presets = new ArrayList<Preset>();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
	}
	public List<Preset> getPresets() {
		return presets;
	}
	public void add(Preset p) {
		presets.add(p);
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void remove(int index) {
		presets.remove(index);
		try {
			write();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
	@SuppressWarnings("unchecked")
	private void read() throws Exception {
		FileInputStream fin = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(fin);
		presets = (ObservableList<Preset>) oin.readObject();
		oin.close();
		fin.close();
	}
	*/
	private void read() throws Exception {
		Scanner scanner = new Scanner(new File(file));
		while (scanner.hasNextLine()) {
			String name = scanner.nextLine();
			String points = scanner.nextLine();
			Preset p = new Preset(name, points);
			presets.add(p);
		}
		scanner.close();
	}
	private void write() throws Exception {
		FileOutputStream fout = new FileOutputStream(file);
		for (int c = 1; c < presets.size(); c++) {
			Preset p = presets.get(c);
			String s = p.getNome()+"\n";
			fout.write(s.getBytes() );
			s = p.getStringPontos()+"\n";
			fout.write(s.getBytes() );
		}
		fout.close();
	}
	/*
	private void write() throws Exception {
		FileOutputStream fout = new FileOutputStream(file);
		ObjectOutputStream objout = new ObjectOutputStream(fout);
		objout.writeObject(presets);
		objout.close();
		fout.close();
	}
	*/
}
