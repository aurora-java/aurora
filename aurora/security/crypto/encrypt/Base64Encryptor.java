package aurora.security.crypto.encrypt;

import aurora.security.crypto.codec.Base64;

public class Base64Encryptor {
	public static String decrypt(String encryptedText) {
		String text = null;
		try {
			byte[] b = encryptedText.getBytes();
			if (Base64.isBase64(b)) {
				text = new String(Base64.decode(b));
				return text;
			} else
				return encryptedText;
		} catch (Exception e) {
			return encryptedText;
		}
	}

	public static String encrypt(String text) {
		return new String(Base64.encode(text.getBytes()));
	}

	public static void main(String[] args) {
		String a=Base64Encryptor.encrypt("123");
		String b=Base64Encryptor.decrypt(a);
		System.out.println(a);
		System.out.println(b);
	}
}
