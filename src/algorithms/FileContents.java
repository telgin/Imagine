package algorithms;

import java.io.File;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import hibernate.Metadata;

public class FileContents {
	private boolean fragment;
	private long fragmentNumber;
	private long remainingData;
	private File extractedFile;
	private Metadata metadata;

	/**
	 * @return the fragment
	 */
	public boolean isFragment() {
		return fragment;
	}

	/**
	 * @param fragment the fragment to set
	 */
	private void setFragment(boolean fragment) {
		this.fragment = fragment;
	}

	/**
	 * @return the fragmentNumber
	 */
	public long getFragmentNumber() {
		return fragmentNumber;
	}

	/**
	 * @param fragmentNumber the fragmentNumber to set
	 */
	public void setFragmentNumber(long fragmentNumber) {
		this.fragmentNumber = fragmentNumber;
		setFragment(fragmentNumber != 0);
	}

	/**
	 * @return the remainingData
	 */
	public long getRemainingData() {
		return remainingData;
	}

	/**
	 * @param remainingData the remainingData to set
	 */
	public void setRemainingData(long remainingData) {
		this.remainingData = remainingData;
	}
	
	/**
	 * @return the extractedFile
	 */
	public File getExtractedFile() {
		return extractedFile;
	}

	/**
	 * @param extractedFile the extractedFile to set
	 */
	public void setExtractedFile(File extractedFile) {
		this.extractedFile = extractedFile;
	}
	
	/**
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

}
