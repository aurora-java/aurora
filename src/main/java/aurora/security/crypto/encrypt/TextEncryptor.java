package aurora.security.crypto.encrypt;

public interface TextEncryptor {
	public String encrypt(String paramString);
	public String decrypt(String paramString);
}
