package aurora.application.action;

import java.util.HashMap;

public class ResumableInfoStorage {

    //Single instance
    private ResumableInfoStorage() {
    }
    private static ResumableInfoStorage sInstance;

    public static synchronized ResumableInfoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ResumableInfoStorage();
        }
        return sInstance;
    }

    //resumableIdentifier --  ResumableInfo
    private HashMap<String, ResumableInfo> mMap = new HashMap<String, ResumableInfo>();

    /**
     * Get ResumableInfo from mMap or Create a new one.
     * @param resumableChunkSize
     * @param resumableTotalSize
     * @param resumableIdentifier
     * @param resumableFilename
     * @param resumableRelativePath
     * @param resumableFilePath
     * @return
     */
    public synchronized ResumableInfo get(AttachmentResumable attachmentResumable, int resumableChunkSize, long resumableTotalSize,
                             String resumableIdentifier, String resumableFilename,String resumableHashFileName,
                             String resumableFilePath) {

        ResumableInfo info = mMap.get(resumableHashFileName);

        if (info == null) {
            info = new ResumableInfo();
            info.attachmentResumable    = attachmentResumable;
            info.resumableChunkSize     = resumableChunkSize;
            info.resumableTotalSize     = resumableTotalSize;
            info.resumableIdentifier    = resumableIdentifier;
            info.resumableFilename      = resumableFilename;
            info.resumableFilePath      = resumableFilePath;
            info.resumableHashFileName  = resumableHashFileName;
            mMap.put(resumableHashFileName, info);
        }
        return info;
    }

    /**
     * ɾ��ResumableInfo
     * @param info
     */
    public void remove(ResumableInfo info) {
       mMap.remove(info.resumableHashFileName);
    }
}
