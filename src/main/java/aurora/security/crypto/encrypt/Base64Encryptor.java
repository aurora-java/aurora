package aurora.security.crypto.encrypt;

import aurora.security.crypto.codec.Base64;

public class Base64Encryptor implements TextEncryptor{
	public String decrypt(String encryptedText) {
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

	public String encrypt(String text) {
		return new String(Base64.encode(text.getBytes()));
	}

	public static void main(String[] args) {
		Base64Encryptor encryptor=new Base64Encryptor();
		String a=encryptor.encrypt("hec2test");
		String b=encryptor.decrypt(a);
		System.out.println(a);
		System.out.println(b);
	}
}
