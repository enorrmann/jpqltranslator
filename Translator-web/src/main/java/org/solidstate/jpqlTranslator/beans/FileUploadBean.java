package org.solidstate.jpqlTranslator.beans;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * @author Ilya Shaikovsky
 * 
 */
public class FileUploadBean {

	private ArrayList<File> files = new ArrayList<File>();
	private int uploadsAvailable = 5;
	private boolean autoUpload = false;
	private boolean useFlash = false;
	//JarFileLoader jfl = new JarFileLoader();

	public int getSize() {
		if (getFiles().size() > 0) {
			return getFiles().size();
		} else {
			return 0;
		}
	}

	public FileUploadBean() {
	}

	public void paint(OutputStream stream, Object object) throws IOException {
		stream.write(getFiles().get((Integer) object).getData());
	}

	public void listener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();
		File file = new File();
		try {

			file.setName(item.getFileName());

			/* Get File Data */
			if (item.isTempFile()) {

				byte[] fileInBytes = new byte[(int) item.getFile().length()]; 
				java.io.File tempFile = item.getFile();
				FileInputStream fileInputStream = new FileInputStream(tempFile);
				fileInputStream.read(fileInBytes);
				fileInputStream.close();

				file.setData(fileInBytes); //

			} else {
				file.setData(item.getData());
				System.out.println("STATUS: " + "Not a temp File!");
			}

			file.setLength(file.getData().length);
			System.out.println("FILE LENGTH: " + file.getData().length / 1024
					+ "kb");

			files.add(file);
			uploadsAvailable--;
		} catch (Exception e) {
			System.out.println("ERROR:[listener()]: " + e);
		}
	}

	public String clearUploadData() {
		files.clear();
		setUploadsAvailable(5);
		return null;
	}

	public long getTimeStamp() {
		return System.currentTimeMillis();
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isUseFlash() {
		return useFlash;
	}

	public void setUseFlash(boolean useFlash) {
		this.useFlash = useFlash;
	}

}
