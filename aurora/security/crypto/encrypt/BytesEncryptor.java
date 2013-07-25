package aurora.security.crypto.encrypt;

public interface BytesEncryptor {
	public byte[] encrypt(byte[] paramArrayOfByte);
	public byte[] decrypt(byte[] paramArrayOfByte);
}
