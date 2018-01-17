package keyEncrypt;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.json.simple.JSONObject;

public class RSA {

	private static  PublicKey publicKey ;
	private static  PrivateKey privateKey;
	static Cipher decryptCipher;
	static Cipher encryptCipher;

	public static void generateKeyPair(int length) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(length, new SecureRandom());
		KeyPair pair = generator.generateKeyPair();
		publicKey = pair.getPublic();
		privateKey =pair.getPrivate();
		//dumpKeyPair(pair);
		//		SaveKeyPair(String publicName,String privateName);
		//		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		//		kpg.initialize(2048);
		//
		//		KeyPair kp = kpg.genKeyPair();
		//
		//		KeyFactory fact = KeyFactory.getInstance("RSA");
		//
		//		publicKey = fact.getKeySpec(kp.getPublic(),
		//				RSAPublicKeySpec.class);
		//		saveKeys("Public", publicKey.getModulus(), publicKey.getPublicExponent());
		//
		//		privateKey = fact.getKeySpec(kp.getPrivate(),
		//				RSAPrivateKeySpec.class);
		//		saveKeys("Private", privateKey.getModulus(), privateKey.getPrivateExponent());

		System.out.println("Success to generate Key");
		//		return pair;
	}

	//	private static void saveKeys(String fileName,
	//			BigInteger mod, BigInteger exp) throws FileNotFoundException, IOException 
	//	{
	//		ObjectOutputStream oout = new ObjectOutputStream(
	//				new BufferedOutputStream(new FileOutputStream(fileName)));
	//		oout.writeObject(mod);
	//		oout.writeObject(exp);
	//	}
	public static String encrypt(String plainText) throws Exception {
		encryptCipher.init(Cipher.ENCRYPT_MODE,  publicKey);
		byte[] cipherText = encryptCipher.doFinal(plainText.getBytes("UTF-8"));

		return Base64.getEncoder().encodeToString(cipherText);
	}

	public static String decrypt(String cipherText) throws Exception {
		byte[] bytes = Base64.getDecoder().decode(cipherText);

		decryptCipher.init(Cipher.DECRYPT_MODE, (Key) privateKey);

		return new String(decryptCipher.doFinal(bytes), "UTF-8");
	}

	public void setMode(String mode) throws NoSuchAlgorithmException, NoSuchPaddingException
	{
		encryptCipher = Cipher.getInstance(mode);
		decryptCipher = Cipher.getInstance(mode);
	}

	public static void main(String[] args) throws Exception{
		//First generate a public/private key pair
		RSA rsa = new RSA();
		rsa.setMode("RSA");
		rsa.generateKeyPair(1024);
		rsa.SaveKeyPair("public", "private","Its RSA");

		//Our secret message
		String message = "the answer to life the universe and everything~~~ HAHAHAHAHAHAH!!";

		//Encrypt the message
		String encryptedText = encrypt(message);
		System.out.println(encryptedText);
		//Now decrypt it
		String decipheredMessage = decrypt(encryptedText);
		System.out.println(decipheredMessage);
	}

	private static void dumpKeyPair(KeyPair keyPair) {
		PublicKey pub = keyPair.getPublic();
		System.out.println("Public Key: " + getHexString(pub.getEncoded()));

		PrivateKey priv = keyPair.getPrivate();
		System.out.println("Private Key: " + getHexString(priv.getEncoded()));
	}
	private static String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static void SaveKeyPair(String publicName,String privateName,String description) throws IOException {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		FileOutputStream fos = new FileOutputStream("./KEY/RSA/"+publicName+".rsa");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
 
		// Store Private Key.
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				privateKey.getEncoded());
		fos = new FileOutputStream("./KEY/RSA/"+privateName+".rsa");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
	}

	public static PublicKey getPublicKey(File file)
			throws Exception {
		byte[] keyBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

		X509EncodedKeySpec spec =
				new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public static PrivateKey getPrivateKey(File file)
			throws Exception {
		byte[] keyBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		PKCS8EncodedKeySpec spec =
				new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	public PublicKey LoadPublicKey(File file)
			throws IOException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		// Read Public Key.
		File filePublicKey = file;
		FileInputStream fis = new FileInputStream(file.getAbsoluteFile());
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		publicKey = keyFactory.generatePublic(publicKeySpec);
		
		return publicKey;
	}
	
	public PrivateKey LoadPrivateKey(File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Read Private Key.
		File filePrivateKey = file;
		FileInputStream fis = new FileInputStream(file.getAbsoluteFile());

		//fis = new FileInputStream(file.getAbsoluteFile());
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
		// Generate KeyPair.

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		privateKey = keyFactory.generatePrivate(privateKeySpec);

		return privateKey;
		//return new KeyPair(publicKey, privateKey);
	}

	
	public static boolean verify(String plainText, String signature, PublicKey publicKey,String hmode) throws Exception {
	    Signature publicSignature = Signature.getInstance(hmode+"withRSA");
	    publicSignature.initVerify(publicKey);
	    publicSignature.update(plainText.getBytes("UTF-8"));

	    byte[] signatureBytes = Base64.getDecoder().decode(signature);

	    return publicSignature.verify(signatureBytes);
	}
	
	public static String sign(String hmode, String plainText, PrivateKey privateKey,String filename) throws Exception {
	    Signature privateSignature = Signature.getInstance(hmode+"withRSA");
	    privateSignature.initSign(privateKey);
	    privateSignature.update(plainText.getBytes("UTF-8"));

	    byte[] signature = privateSignature.sign();
	    String output = Base64.getEncoder().encodeToString(signature);
	    JSONObject obj = new JSONObject();
		obj.put("signature",output);
		obj.put("hmode", hmode);

		try (FileWriter writer = new FileWriter("./signature/"+filename+".rsa"))
		{
			writer.write(obj.toString());
			writer.flush();
		}catch (IOException ex)
		{
			ex.printStackTrace();
		}
		System.out.println(obj);
	    return output;
	    //write file
	}
	
}