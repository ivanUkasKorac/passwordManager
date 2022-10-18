package managers;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class login {
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

	private static Timestamp timeFormat(String string) {
		String help[] = string.split("\\.");
		String result = null;
		if (help.length > 5) {
			result = help[0] + "-" + help[1] + "-" + help[2] + " " + help[3] + ":" + help[4] + ":" + help[5];
		}
		return Timestamp.valueOf(result);
	}

	private static String hexString(byte[] array) throws NoSuchAlgorithmException {
		BigInteger bi = new BigInteger(1, array);
		String hex = bi.toString(16);

		int paddingLength = (array.length * 2) - hex.length();
		if (paddingLength > 0) {
			return String.format("%0" + paddingLength + "d", 0) + hex;
		} else {
			return hex;
		}
	}

	private static byte[] hexToByte(String hex) throws NoSuchAlgorithmException {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

	private static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	private static String passwordHashing(String password, String saltFromUser)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		int iterations = 1000;
		char[] chars = password.toCharArray();
		byte[] salt = hexToByte(saltFromUser);

		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

		byte[] hash = skf.generateSecret(spec).getEncoded();
		return hexString(hash);
	}

	public static int cntUpperCaseCharacters(String string) {
		int cnt = 0;
		for (int i = 0; i < string.length(); i++) {
			if (Character.isUpperCase(string.charAt(i))) {
				cnt++;
			}
		}

		return cnt;
	}

	public static int cntNumbers(String string) {
		int cnt = 0;
		for (int i = 0; i < string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				cnt++;
			}
		}

		return cnt;
	}

	private static Long calculateDifference(Timestamp date_1, Timestamp date_2, String value) {
		long milliseconds = date_1.getTime() - date_2.getTime();
		if (value.equals("second"))
			return milliseconds / 1000;
		if (value.equals("minute"))
			return milliseconds / 1000 / 60;
		if (value.equals("hours"))
			return milliseconds / 1000 / 3600;
		else
			return Long.MAX_VALUE;
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		File data = new File("data.txt");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		if (data.exists() && args.length == 1) {
			Scanner sc = new Scanner(System.in);
			List<String> help = Files.readAllLines(data.toPath());
			Map<String, String> map = new HashMap<>();
			Map<String, String> map2 = new HashMap<>();
			Map<String, String> map3 = new HashMap<>();
			Map<String, Timestamp> map4 = new HashMap<>();

			String help2[] = null;
			if (help.size() > 0) {
				for (String s : help) {
					help2 = s.split(":");
					if (help2.length > 4) {
						map.put(help2[0], help2[1]);
						map2.put(help2[0], help2[2]);
						map3.put(help2[0], help2[3]);
						map4.put(help2[0], timeFormat(help2[4]));
					}
				}
			}
			help = null;
			help2 = null;

			if (map2.get(args[0]).equals("1") || calculateDifference(timestamp, map4.get(args[0]), "second") > 30) {
				Console console = System.console();
				int cnt = 0;
				while (cnt < 3) {
					char passwd[] = console.readPassword("Password: ");
					String password = new String(passwd);

					String result = passwordHashing(password, map3.get(args[0]));
					if (result.equals(map.get(args[0]))) {
						console = System.console();

						passwd = console.readPassword("New password: ");

						char passwdRep[] = console.readPassword("Repeat new password: ");

						String newPassword = new String(passwd);
						String newPassword2 = new String(passwdRep);
						sc.close();
						if (newPassword.equals(newPassword2) && !newPassword.equals(password)) {
							if (cntUpperCaseCharacters(newPassword) >= 1 && cntNumbers(newPassword) >= 1
									&& newPassword.length() >= 10) {
								String newSalt = hexString(getSalt());
								result = passwordHashing(newPassword, newSalt);
								map.put(args[0], result);
								map2.put(args[0], "0");
								map3.put(args[0], newSalt);
								map4.put(args[0], timestamp);
								String a = "";
								for (Map.Entry<String, String> entry : map.entrySet()) {
									a = a + entry.getKey() + ":" + entry.getValue() + ":" + map2.get(entry.getKey())
											+ ":" + map3.get(entry.getKey()) + ":"
											+ sdf1.format(map4.get(entry.getKey())).toString() + "\n";
								}
								Files.writeString(data.toPath(), a);					
								System.out.println("Login successful.");
								newSalt = null;
								a = null;
							} else {
								System.out.println(
										"Password change failed. Password too weak, must contain at least 10 characters, one upper letter and one number.");
							}
						} else {
							System.out.println("Password mismatch or old password used.");
						}
						newPassword = null;
						newPassword2 = null;
						passwdRep = null;

					} else {
						System.out.println("Username or password incorrect.");
					}
					cnt++;
					password = null;
					passwd = null;
					result = null;
				}
				
			} else {
				Console console = System.console();
				int cnt = 0;
				while (cnt < 3) {
					char passwd[] = console.readPassword("Password: ");
					String password = new String(passwd);
					String result = passwordHashing(password, map3.get(args[0]));
					if (result.equals(map.get(args[0]))) {
						System.out.println("Login successful.");
					} else {
						System.out.println("Username or password incorrect.");
					}
					cnt++;
					password = null;
					result = null;
					passwd = null;
				}
			}

		} else {
			System.out.println("File doesn't exist or incorrect number of arguments.");
		}
	}

}
