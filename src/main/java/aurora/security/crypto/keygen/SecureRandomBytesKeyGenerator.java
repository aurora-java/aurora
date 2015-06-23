package aurora.security.crypto.keygen;

import java.security.SecureRandom;

final class SecureRandomBytesKeyGenerator implements BytesKeyGenerator {
	private final SecureRandom random;
	private final int keyLength;
	private static final int DEFAULT_KEY_LENGTH = 8;

	public SecureRandomBytesKeyGenerator() {
		this(DEFAULT_KEY_LENGTH);
	}

	public SecureRandomBytesKeyGenerator(int keyLength) {
		this.random = new SecureRandom();
		this.keyLength = keyLength;
	}

	public int getKeyLength() {
		return this.keyLength;
	}

	public byte[] generateKey() {
		byte[] bytes = new byte[this.keyLength];
		this.random.nextBytes(bytes);
		return bytes;
	}
}
