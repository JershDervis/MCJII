package me.jershdervis.mcjii.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import me.jershdervis.mcjii.Main;

import org.zeroturnaround.zip.ZipUtil;

public class JarHandler {

    public void modifyJarFiles(File jarFile, String[] filesToRemove)  {
        File tempFolder = new File(jarFile.getParentFile(), "temp/");
        tempFolder.mkdirs();
        
        ZipUtil.unpack(jarFile, tempFolder);

        for (String file : filesToRemove) {
            File f = new File(tempFolder, file);
            if (f.isDirectory()) {
                deleteDir(f);
            } else {
                if (f.exists()) {
                    f.delete();
                }
            }
        }
        
        try {
			unpackHackClass(new File(tempFolder, Main.config.getProperty("INPUT_CLASS_HACK")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        jarFile.delete();
        ZipUtil.pack(tempFolder, jarFile);
        delete(tempFolder);
    }
    
    private void delete(File element) {
        if (element.isDirectory()) {
            for(File f: element.listFiles()) {
                this.delete(f);
            }
        }
        element.delete();
    }
    
    private static void unpackHackClass(File toDir) throws IOException {
    	Files.copy(new File(".", Main.config.getProperty("INPUT_CLASS_HACK")).getAbsoluteFile().toPath(), toDir.getAbsoluteFile().toPath());
    }

	public static boolean deleteDir(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                	deleteDir(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}

	public void replaceJarFile(String jarPathAndName,byte[] fileByteCode,String fileName) throws IOException {
		File jarFile = new File(jarPathAndName);
		File tempJarFile = new File(jarPathAndName + ".tmp");
		JarFile jar = new JarFile(jarFile);
		boolean jarWasUpdated = false;

		try {
			JarOutputStream tempJar = new JarOutputStream(new FileOutputStream(tempJarFile));

			// Allocate a buffer for reading entry data.

			byte[] buffer = new byte[1024];
			int bytesRead;

			try {
				// Open the given file.

				try {
					// Create a jar entry and add it to the temp jar.

					JarEntry entry = new JarEntry(fileName);
					tempJar.putNextEntry(entry);
					tempJar.write(fileByteCode);

				} catch (Exception ex) {
					System.out.println(ex);

					// Add a stub entry here, so that the jar will close without an
					// exception.

					tempJar.putNextEntry(new JarEntry("stub"));
				}


				// Loop through the jar entries and add them to the temp jar,
				// skipping the entry that was added to the temp jar already.
				InputStream entryStream = null;
				for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
					// Get the next entry.

					JarEntry entry = (JarEntry) entries.nextElement();

					// If the entry has not been added already, so add it.

					if (! entry.getName().equals(fileName)) {
						// Get an input stream for the entry.

						entryStream = jar.getInputStream(entry);
						tempJar.putNextEntry(entry);

						while ((bytesRead = entryStream.read(buffer)) != -1) {
							tempJar.write(buffer, 0, bytesRead);
						}
					}
				}
				if(entryStream!=null)
					entryStream.close();
				jarWasUpdated = true;
			}
			catch (Exception ex) {
				System.out.println(ex);

				// IMportant so the jar will close without an
				// exception.

				tempJar.putNextEntry(new JarEntry("stub"));
			}
			finally {
				tempJar.close();
			}
		}
		finally {

			jar.close();

			if (! jarWasUpdated) {
				tempJarFile.delete();
			}
		}


		if (jarWasUpdated) {            
			if(jarFile.delete()){
				tempJarFile.renameTo(jarFile);
				System.out.println(jarPathAndName + " updated.");
			}else
				System.out.println("Could Not Delete JAR File");
		}
	}
}
