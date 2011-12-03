package rocksaw;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryLoader {
	private transient final Logger log = LoggerFactory.getLogger(LibraryLoader.class);

	public void loadLibrary(String libname) throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");
		URL classPathUrl = getClass().getResource(String.format("/%s", libname));
		if (classPathUrl == null) { throw new RuntimeException("Could not locate library in classpath."); }
		
		File embeddedFile = new File(classPathUrl.getFile());
		BufferedInputStream bIn = null;
		BufferedOutputStream bOut = null;
		log.debug(embeddedFile.getAbsolutePath());
		log.debug(embeddedFile.exists()+" "+embeddedFile.canRead());

		if (embeddedFile.exists() && embeddedFile.canRead()) {
			bIn = new BufferedInputStream(new FileInputStream(embeddedFile));
		} else {
			bIn = new BufferedInputStream(getClass().getResourceAsStream(String.format("/%s", libname)));
		}
		if (bIn != null) {
			File exportedLib = new File(tmpDir, libname);
			
			try {
				bOut = new BufferedOutputStream(new FileOutputStream(exportedLib));
				
				int b;
				while ((b = bIn.read()) != -1) {
					bOut.write(b);
				}
			} finally {
				if (bOut != null) {
					bOut.flush();
					bOut.close();
				}
				
				if (bIn != null) {
					bIn.close();
				}
			}
			
			log.debug(exportedLib.getAbsolutePath());
			Runtime.getRuntime().load(exportedLib.getAbsolutePath());
		}
	}
}
