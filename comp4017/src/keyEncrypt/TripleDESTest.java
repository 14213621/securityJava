package keyEncrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.json.simple.JSONObject;

public class TripleDESTest {
//	private static Cipher cipher = null;
	Cipher ecipher;
	Cipher dcipher;
	SecretKey secretKey;
	private boolean DecryptSuccess =false;
	IvParameterSpec iv = null;

	public static  SecretKey readKey(byte[] rawkey) throws Exception
	{
		System.out.println("rawkey.length:" + rawkey.length);
		DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		SecretKey key = keyfactory.generateSecret(keyspec);
		return key;
	}


	public void setMode(String mode,String operation) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IOException
	{
		if (mode.equals("ECB"))
		{
			System.out.println("AES MODE:"+mode);
			ecipher = Cipher.getInstance("DESede/"+mode+"/PKCS5Padding");
			dcipher = Cipher.getInstance("DESede/"+mode+"/PKCS5Padding");

			ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
			dcipher.init(Cipher.DECRYPT_MODE, secretKey);
			System.out.println("Seting done");
		}else if (mode.equals("CFB"))
		{
			System.out.println("CBC MODE");
			if (operation.equals("encrypt"))
			{
				final SecureRandom rng = new SecureRandom();
				ecipher = Cipher.getInstance("DESede/"+mode+"/PKCS5PADDING");
				dcipher = Cipher.getInstance("DESede/"+mode+"/PKCS5PADDING");

				 iv = createIV(ecipher.getBlockSize(), Optional.of(rng));
				ecipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
				dcipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
				System.out.println("Seting done");
			}else 
			{
				ecipher = Cipher.getInstance("DESede/"+mode+"/NoPadding");
				dcipher = Cipher.getInstance("DESede/"+mode+"/NoPadding");
				iv = readIV(operation.getBytes("ISO-8859-1"));
				ecipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
				dcipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
				System.out.println("Seting done");
			}
		}else
		{
			final SecureRandom rng = new SecureRandom();
			if (operation.equals("encrypt"))
			{
				ecipher = Cipher.getInstance("DESede/"+mode+"/PKCS5Padding");
				dcipher = Cipher.getInstance("DESede/"+mode+"/PKCS5Padding");

				iv = createIV(ecipher.getBlockSize(), Optional.of(rng));
				System.out.println("Created IV.getbyte().length: "+ iv.getIV().length);
				ecipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
				dcipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
				System.out.println("Seting done");
			}else 
			{
				ecipher = Cipher.getInstance("DESede/"+mode+"/PKCS5Padding");
				dcipher = Cipher.getInstance("DESede/"+mode+"/PKCS5Padding");

				iv = readIV(operation.getBytes("ISO-8859-1"));
				ecipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
				dcipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
				System.out.println("Seting done");
			}

		}
	}
	
	public String getIV() throws UnsupportedEncodingException
	{
		if(iv !=null)
		{
			System.out.println("original byte.length:" + iv.getIV().length);
			System.out.println("IV Not null");
			System.out.println("new byte.length:" +new String(iv.getIV(), "ISO-8859-1").getBytes("ISO-8859-1").length);
			return new String(iv.getIV(), "ISO-8859-1");
		}
		System.out.println("IV is null");
		return "";
	}

	
	public static IvParameterSpec createIV(final int ivSizeBytes, final Optional<SecureRandom> rng) {
		final byte[] iv = new byte[ivSizeBytes];
		final SecureRandom theRNG = rng.orElse(new SecureRandom());
		theRNG.nextBytes(iv);
		return new IvParameterSpec(iv);
	}
	
	public TripleDESTest(SecretKey secretKey)
	{
			this.secretKey = secretKey;

	}

	public static IvParameterSpec readIV(final byte[] oldIVByte) throws IOException {
		final byte[] iv = new byte[oldIVByte.length];
		for(int i =0;i<iv.length;i++)
		{
			iv[i] = oldIVByte[i];
		}
		return new IvParameterSpec(iv);
	}
	
	public void saveKey(String filename,String description) throws IOException
	{
		File keyfile = new File("KEY/"+filename+"desede");
		if (keyfile.getParentFile().mkdir()) {
			keyfile.createNewFile();
			System.out.println("new dir");
		} else {
			System.out.println("exited");
		}
		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
		System.out.println("encodeToString 2nd key.getEncoded().toString():"+encodedKey);

		JSONObject obj = new JSONObject();
		obj.put("key",encodedKey);
		obj.put("description", description);
		JSONObject testV=new JSONObject();

		try (FileWriter writer = new FileWriter("KEY/"+filename+".desede"))
		{
			writer.write(obj.toString());
			writer.flush();
		}catch (IOException ex)
		{
			ex.printStackTrace();
		}
		System.out.println(obj);
	}
	public String encrypt(String str) throws Exception {
		// Encode the string into bytes using utf-8
		byte[] utf8 = str.getBytes("UTF16");

		// Encrypt
		
		byte[] enc = ecipher.doFinal(utf8);
		System.out.println("dec.length:"+enc.length);

		// Encode bytes to base64 to get a string
		return new sun.misc.BASE64Encoder().encode(enc);
	}

	public String decrypt(String str) {
		// Decode base64 to get bytes
		byte[] dec;
		try {
			dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
			System.out.println("dec.length:"+dec.length);
			byte[] utf8 = dcipher.doFinal(dec);
			this.DecryptSuccess = true;
			return new String(utf8, "UTF16");
		} catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("Not this key");
			this.DecryptSuccess=false;
			e.printStackTrace();

		}
		return null;
	}
	public boolean decrptSuccess()
	{
		return this.DecryptSuccess;
	}
}