package aurora.security.crypto.encrypt;

import aurora.security.crypto.codec.Hex;
import aurora.security.crypto.codec.Utf8;

final class HexEncodingTextEncryptor implements TextEncryptor {

	private final BytesEncryptor encryptor;

	public HexEncodingTextEncryptor(BytesEncryptor encryptor) {
		this.encryptor = encryptor;
	}

	public String encrypt(String text) {
		return new String(Hex.encode(this.encryptor.encrypt(Utf8.encode(text))));
	}

	public String decrypt(String encryptedText) {
		return Utf8.decode(this.encryptor.decrypt(Hex.decode(encryptedText)));
	}

}
