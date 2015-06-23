package aurora.security.crypto.keygen;

final class SharedKeyGenerator implements BytesKeyGenerator {
	private byte[] sharedKey;

	public SharedKeyGenerator(byte[] sharedKey) {
		this.sharedKey = sharedKey;
	}

	public int getKeyLength() {
		return this.sharedKey.length;
	}

	public byte[] generateKey() {
		return this.sharedKey;
	}
}