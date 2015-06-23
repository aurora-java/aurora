package aurora.security.crypto.keygen;

public interface BytesKeyGenerator {
	public int getKeyLength();
	public byte[] generateKey();
}
