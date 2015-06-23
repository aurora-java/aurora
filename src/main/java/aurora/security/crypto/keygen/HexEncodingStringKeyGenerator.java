package aurora.security.crypto.keygen;

import aurora.security.crypto.codec.Hex;

final class HexEncodingStringKeyGenerator implements StringKeyGenerator {
	private final BytesKeyGenerator keyGenerator;

	public HexEncodingStringKeyGenerator(BytesKeyGenerator keyGenerator) {
		this.keyGenerator = keyGenerator;
	}

	public String generateKey() {
		return new String(Hex.encode(this.keyGenerator.generateKey()));
	}
}