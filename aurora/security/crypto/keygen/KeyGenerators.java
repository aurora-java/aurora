package aurora.security.crypto.keygen;

public class KeyGenerators {

	public static BytesKeyGenerator secureRandom() {
		return new SecureRandomBytesKeyGenerator();
	}

	public static BytesKeyGenerator secureRandom(int keyLength) {
		return new SecureRandomBytesKeyGenerator(keyLength);
	}

	public static BytesKeyGenerator shared(int keyLength) {
		return new SharedKeyGenerator(secureRandom(keyLength).generateKey());
	}

	public static StringKeyGenerator string() {
		return new HexEncodingStringKeyGenerator(secureRandom());
	}
}
