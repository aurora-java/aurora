package aurora.security.crypto.encrypt;

import aurora.security.crypto.keygen.KeyGenerators;

public class AESEncryptor implements TextEncryptor{
	public AESEncryptor(){
		
	}
	public static TextEncryptor queryableText(CharSequence password,
			CharSequence salt) {
		return new HexEncodingTextEncryptor(new AesBytesEncryptor(
				password.toString(), salt));
	}

	public static TextEncryptor queryableText(CharSequence password,
			CharSequence salt, int keysize) {
		return new HexEncodingTextEncryptor(new AesBytesEncryptor(
				password.toString(), salt, keysize));
	}

	public static BytesEncryptor standard(CharSequence password,
			CharSequence salt) {
		return new AesBytesEncryptor(password.toString(), salt,
				KeyGenerators.secureRandom(16));
	}

	public static BytesEncryptor standard(CharSequence password,
			CharSequence salt, int keysize) {
		return new AesBytesEncryptor(password.toString(), salt,
				KeyGenerators.secureRandom(16), keysize);
	}

	public static TextEncryptor text(CharSequence password, CharSequence salt,
			int keysize) {
		return new HexEncodingTextEncryptor(standard(password, salt, keysize));
	}

	public static void main(String args[]) {
		TextEncryptor textEncryptor = queryableText("myPassword", "abc123", 128);
		String rawPassword = "123";
		String encodedPassword;
		encodedPassword = textEncryptor.encrypt(rawPassword);
		rawPassword = textEncryptor.decrypt(encodedPassword);
		System.out.println(encodedPassword);
		System.out.println(rawPassword);
	}

	@Override
	public String encrypt(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decrypt(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}
}
