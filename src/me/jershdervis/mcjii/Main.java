package me.jershdervis.mcjii;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import me.jershdervis.mcjii.util.JarHandler;

public class Main {

	public static Properties config = new Properties();

	public Main() throws Exception {
		this.loadProperties(config);
		File inputJar = new File(config.getProperty("INPUT_JAR"));
		String mainClass = config.getProperty("INPUT_CLASS_MAIN");
		String injectionMethod = config.getProperty("INJECTION_METHOD");

		//ReplaceAll wasn't working for some reason :/
		StringBuilder sb = new StringBuilder();
		char[] charObjectArray = mainClass.toCharArray();
		for(char c : charObjectArray)
			sb.append(c == '.' ? "/" : c);

		JarHandler jarHandler = new JarHandler();
		String[] removeFiles = new String[] {"META-INF"}; 
		jarHandler.modifyJarFiles(inputJar, removeFiles);

		ClassPool pool = ClassPool.getDefault();
		ClassPath classPath = pool.insertClassPath(inputJar.getAbsolutePath());

		//Make hack method
		CtClass cc = pool.get(mainClass); 
		CtMethod m = CtNewMethod.make("public static void runClient() {"
				+ "new " + config.getProperty("INPUT_CLASS_HACK").replace(".class", "") + "();"
				+ " }", cc);
		cc.addMethod(m); 
		//

		//Invoke method call
		CtMethod method = cc.getDeclaredMethod(injectionMethod);
		method.insertBefore("runClient();");
		//

		byte[] b = cc.toBytecode(); // convert the new class to bytecode.
		pool.removeClassPath(classPath);   // need to remove the classpath to release connection to JAR file so we can update it.

		jarHandler.replaceJarFile(inputJar.getAbsolutePath(), b, sb.toString() + ".class");
	}

	public static void main(String[] args) throws Exception {
		new Main();
	}

	private void loadProperties(Properties props) {
		InputStream input = null;
		try {
			input = new FileInputStream("config.properties");
			props.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
