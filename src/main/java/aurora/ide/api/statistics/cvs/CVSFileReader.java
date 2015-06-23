package aurora.ide.api.statistics.cvs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CVSFileReader {

	// the famous CVS meta directory name
	public static final String CVS_DIRNAME = "CVS"; //$NON-NLS-1$

	// CVS meta files located in the CVS subdirectory
	public static final String REPOSITORY = "Repository"; //$NON-NLS-1$
	public static final String ROOT = "Root"; //$NON-NLS-1$
	public static final String STATIC = "Entries.Static"; //$NON-NLS-1$
	public static final String TAG = "Tag"; //$NON-NLS-1$
	public static final String ENTRIES = "Entries"; //$NON-NLS-1$
	//private static final String PERMISSIONS = "Permissions"; //$NON-NLS-1$
	public static final String ENTRIES_LOG = "Entries.Log"; //$NON-NLS-1$
	public static final String NOTIFY = "Notify"; //$NON-NLS-1$
	public static final String BASE_DIRNAME = "Base"; //$NON-NLS-1$
	public static final String BASEREV = "Baserev"; //$NON-NLS-1$

	// the local workspace file that contains pattern for ignored resources
	public static final String IGNORE_FILE = ".cvsignore"; //$NON-NLS-1$


	private static boolean folderExists(File cvsSubDir) {
		return cvsSubDir.isDirectory() && cvsSubDir.exists();
	}

	/**
	 * Reads the CVS/Root, CVS/Repository, CVS/Tag, and CVS/Entries.static files
	 * from the specified folder and returns a FolderSyncInfo instance for the
	 * data stored therein. If the folder does not have a CVS subdirectory then
	 * <code>null</code> is returned.
	 */
	public static FolderSyncInfo readFolderSync(File folder) {
		File cvsSubDir = getCVSSubdirectory(folder);

		if (!folderExists(cvsSubDir)) {
			return null;
		}

		// read CVS/Root
		String root = readFirstLine(new File(cvsSubDir, ROOT));
		if (root == null)
			return null;

		// read CVS/Repository
		String repository = readFirstLine(new File(cvsSubDir, REPOSITORY));
		if (repository == null)
			return null;

		// read CVS/Tag
		String tag = readFirstLine(new File(cvsSubDir, TAG));

		CVSTag cvsTag = (tag != null) ? new CVSEntryLineTag(tag) : null;

		// read Entries.Static
		String staticDir = readFirstLine(new File(cvsSubDir, STATIC));

		boolean isStatic = (staticDir != null);

		// return folder sync
		return new FolderSyncInfo(repository, root, cvsTag, isStatic);
	}

	/**
	 * Returns the CVS subdirectory for this folder.
	 */
	private static File getCVSSubdirectory(File folder) {
		// folder.
		return new File(folder, CVS_DIRNAME);
	}

	protected static boolean existsInFileSystem(File cvsSubDir) {
		return cvsSubDir.exists();
	}

	/*
	 * Reads the first line of the specified file. Returns null if the file does
	 * not exist, or the empty string if it is blank.
	 */
	private static String readFirstLine(File file) {
		try {
			InputStream in = getInputStream(file);
			if (in != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in), 512);
				try {
					String line = reader.readLine();
					if (line == null)
						return ""; //$NON-NLS-1$
					return line;
				} finally {
					reader.close();
				}
			}
			return null;
		} catch (IOException e) {

		}
		return null;
	}

	private static InputStream getInputStream(File file)
			throws FileNotFoundException {
		return new FileInputStream(file);
	}

	/*
	 * Reads all lines of the specified file. Returns null if the file does not
	 * exist.
	 */
	public static String[] readLines(File file) {
		try {
			InputStream in = getInputStream(file);
			if (in != null) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in), 512);
				List<String> fileContentStore = new ArrayList<String>();
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						fileContentStore.add(line);
					}
					return (String[]) fileContentStore
							.toArray(new String[fileContentStore.size()]);
				} finally {
					reader.close();
				}
			}
			return null;
		} catch (IOException e) {

		}
		return null;
	}

	private static File getBaseDirectory(File file) {
		File cvsFolder = getCVSSubdirectory(file.getParentFile());
		File baseFolder = new File(cvsFolder, BASE_DIRNAME);
		return baseFolder;
	}

	/**
	 * Method isEdited.
	 * 
	 * @param resource
	 * @return boolean
	 */
	public static boolean isEdited(File file) {
		File baseFolder = getBaseDirectory(file);
		File baseFile = new File(baseFolder, file.getName());
		return baseFile.exists();
	}

}
