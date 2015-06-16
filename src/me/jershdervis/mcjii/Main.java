package me.jershdervis.mcjii;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import me.jershdervis.mcjii.util.JarHandler;

public class Main {
	
	public static ArrayList<String> settings;

	public Main() throws Exception {
		settings = getSettings();
		File inputJar = new File(getSetting(settings, "INPUT_JAR"));
		String mainClass = getSetting(settings, "INPUT_CLASS_MAIN");
		
		//ReplaceAll wasn't working for some reason :/
		StringBuilder sb = new StringBuilder();
		Character[] charObjectArray = toCharacterArray(mainClass);
		for(Character c : charObjectArray) {
			if(c.toString().equals(".")) {
				sb.append("/");
			} else
				sb.append(c.toString());
		}
		
		JarHandler jarHandler = new JarHandler();
		String[] removeFiles = new String[] {"META-INF"}; 
		jarHandler.modifyJarFiles(inputJar, removeFiles);

		ClassPool pool = ClassPool.getDefault();
		ClassPath classPath = pool.insertClassPath(inputJar.getAbsolutePath());

		//Make hack method
		CtClass cc = pool.get(mainClass); 
		CtMethod m = CtNewMethod.make("public static void runClient() {"
				+ "new " + getSetting(Main.settings, "INPUT_CLASS_HACK").replace(".class", "") + "();"
				+ " }", cc);
		cc.addMethod(m); 
		//

		//Invoke method call
		CtMethod method = cc.getDeclaredMethod("main");
		method.insertBefore("runClient();");
		//

		byte[] b = cc.toBytecode(); // convert the new class to bytecode.
		pool.removeClassPath(classPath);   // need to remove the classpath to release connection to JAR file so we can update it.

		jarHandler.replaceJarFile(inputJar.getAbsolutePath(), b, sb.toString() + ".class");
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}

	private ArrayList<String> getSettings() throws FileNotFoundException, IOException {
		File settings = new File(".", "Settings.txt");
		ArrayList<String> temp = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(settings))) {
			for(String line; (line = br.readLine()) != null; ) {
				if(line.startsWith("//") && line.equals(""))
					continue;
				else if(line.contains("=")) {
					temp.add(line);
				}
			}
		}
		return temp;
	}

	public static String getSetting(ArrayList<String> settings, String prefix) {
		for(String s : settings) {
			if(s.startsWith(prefix)) {
				return s.split("\"")[1].split("\"")[0];
			}
		}
		return null;
	}
	
	public static Character[] toCharacterArray(String sourceString) {
	    char[] charArrays = new char[sourceString.length()];
	    charArrays = sourceString.toCharArray();
	    Character[] characterArray = new Character[charArrays.length];
	    for (int i = 0; i < charArrays.length; i++) {
	        characterArray[i] = charArrays[i];
	    }
	    return characterArray;
	}
}
