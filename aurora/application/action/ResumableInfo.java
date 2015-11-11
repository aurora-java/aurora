package aurora.application.action;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class ResumableInfo {
	public AttachmentResumable attachmentResumable;
    public int      resumableChunkSize;
    public long     resumableTotalSize;
    public String   resumableIdentifier;
    public String   resumableFilename;
    public String   resumableHashFileName;

    public static class ResumableChunkNumber {
        public ResumableChunkNumber(int number) {
            this.number = number;
        }

        public int number;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ResumableChunkNumber
                    ? ((ResumableChunkNumber)obj).number == this.number : false;
        }

        @Override
        public int hashCode() {
            return number;
        }
    }

    //Chunks uploaded
    public HashSet<ResumableChunkNumber> uploadedChunks = new HashSet<ResumableChunkNumber>();

    public String resumableFilePath;

    public boolean vaild(){
        if (resumableChunkSize < 0 || resumableTotalSize < 0
                || HttpUtils.isEmpty(resumableIdentifier)
                || HttpUtils.isEmpty(resumableFilename)
                || HttpUtils.isEmpty(resumableHashFileName)) {
            return false;
        } else {
        	List fts = Arrays.asList(attachmentResumable.getFileType().split(";"));
			List fsz = Arrays.asList(attachmentResumable.getFileSize().split(";"));
			String name = resumableFilename.toLowerCase(); 
			String ft = name.substring(name.lastIndexOf(".")+1,name.length());
			int index = fts.indexOf("*."+ft);
			if("*.*".equals(attachmentResumable.getFileType()) || index !=-1){
				String fl = "";
				if(fsz.size()!=fts.size()){
					fl = (String)fsz.get(0);
				}else if(index!=-1){
					fl = (String)fsz.get(index);
				}
				if(!"".equals(fl) && resumableTotalSize > 1024*Integer.valueOf(fl)){
					return false;
				}					
			}else {
				return false;
			}
			return true;
        }
    }
    public boolean checkIfUploadFinished() {
        //check if upload finished
        int count = (int) Math.ceil(((double) resumableTotalSize) / ((double) resumableChunkSize));
        for(int i = 1; i < count; i ++) {
            if (!uploadedChunks.contains(new ResumableChunkNumber(i))) {
                return false;
            }
        }
//        //Upload finished, change filename.
//        File file = new File(resumableFilePath);
////        String new_path = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - ".temp".length());
//        file.renameTo(new File(file.getParent(),resumableFilename));
//        if(file.exists())file.delete();
        return true;
    }
}
