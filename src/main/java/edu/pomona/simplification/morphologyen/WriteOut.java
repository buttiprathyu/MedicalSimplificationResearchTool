package edu.pomona.simplification.morphologyen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class WriteOut {
	private String path;
	private String data;
	private PrintWriter print_line;
	private FileWriter write;
	
	WriteOut(String filepath, String fileData) throws IOException{
		path = filepath;
		data = fileData;
		write = new FileWriter(path);
		print_line = new PrintWriter(write);
		print_line.print(data);
		print_line.close();
	}
	
	

}
