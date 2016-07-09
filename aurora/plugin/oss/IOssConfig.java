package aurora.plugin.oss;

public interface IOssConfig {

	public String getOssEndpoint();

	public String getAccessKeyId();

	public String getAccessKeySecret();
	
	public String getBucketName();
}
