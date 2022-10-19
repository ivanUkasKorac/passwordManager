package managers;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class usermgmt {
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

	private static byte[] getSalt() throws NoSuchAlgorithmException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}

	private static String passwordHashing(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		int iterations = 1000;
		char[] chars = password.toCharArray();
		byte[] salt = getSalt();

		PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

		byte[] hash = skf.generateSecret(spec).getEncoded();
		return hexString(salt) + ":" + hexString(hash);
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

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Creating file if it doesn't exist
		File data = new File("data.txt");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try {
			if (data.createNewFile()) {
				System.out.println("File created.");
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("File could not be created.");
			e.printStackTrace();
		}
		// initializing variables
		// Scanner sc = new Scanner(System.in);
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
					System.out.println(help2[4]);

					map4.put(help2[0], timeFormat(help2[4]));

				}
			}
		}
		// making variables unreadable from memory
		help = null;
		help2 = null;
		if (args[1].length() <= 256) {
			if (args[0].equals("add") && args.length == 2) {
				if (!map.containsKey(args[1])) {
					Console console = System.console();

					char passwd[] = console.readPassword("Password: ");

					char passwdRep[] = console.readPassword("Repeat password: ");

					String password = new String(passwd);
					String password2 = new String(passwdRep);

					if (password.equals(password2)) {
						if (cntUpperCaseCharacters(password) >= 1 && cntNumbers(password) >= 1
								&& password.length() >= 10 && password.length() <= 256) {
							String result[] = passwordHashing(password).split(":");
							Files.writeString(data.toPath(), args[1] + ":" + result[1] + ":" + "0" + ":" + result[0]
									+ ":" + sdf1.format(timestamp).toString() + "\n", StandardOpenOption.APPEND);
							result = null;
							System.out.println("User add successfuly added.");
						} else {
							System.out.println(
									"User add failed. Password too weak, must contain at least 10 and not more than 256 characters, one upper letter and one number.");
						}
					} else {
						System.out.println("User add failed. Password mismatch.");
					}
					password = null;
					password2 = null;
					passwd = null;
					passwdRep = null;
				} else {
					System.out.println("User add failed. User already exists.");
				}

			} else if (args[0].equals("passwd") && args.length == 2) {
				if (map.containsKey(args[1])) {
					Console console = System.console();

					char passwd[] = console.readPassword("Password: ");

					char passwdRep[] = console.readPassword("Repeat password: ");

					String password = new String(passwd);
					String password2 = new String(passwdRep);

					if (password.equals(password2)) {
						if (cntUpperCaseCharacters(password) >= 1 && cntNumbers(password) >= 1
								&& password.length() >= 10 && password.length() <= 256) {
							String result[] = passwordHashing(password).split(":");
							map.put(args[1], result[1]);
							map3.put(args[1], result[0]);
							map4.put(args[1], timestamp);
							String a = "";
							for (Map.Entry<String, String> entry : map.entrySet()) {
								a = a + entry.getKey() + ":" + entry.getValue() + ":" + map2.get(entry.getKey()) + ":"
										+ map3.get(entry.getKey()) + ":"
										+ sdf1.format(map4.get(entry.getKey())).toString() + "\n";
							}
							Files.writeString(data.toPath(), a);
							result = null;
							a = null;
							System.out.println("Password change successful.");
						} else {
							System.out.println(
									"Password change failed. Password too weak, must contain at least 10 and not more than 256 characters, one upper letter and one number.");
						}
					} else {
						System.out.println("Password change failed. Password mismatch.");
					}
					password = null;
					password2 = null;
					passwd = null;
					passwdRep = null;
				} else {
					System.out.println("This user doesn't exist.");
				}

			} else if (args[0].equals("forcepass") && args.length == 2) {
				if (map.containsKey(args[1])) {
					map2.put(args[1], "1");
					String a = "";
					for (Map.Entry<String, String> entry : map.entrySet()) {
						a = a + entry.getKey() + ":" + entry.getValue() + ":" + map2.get(entry.getKey()) + ":"
								+ map3.get(entry.getKey()) + ":" + sdf1.format(map4.get(entry.getKey())).toString()
								+ "\n";
					}
					Files.writeString(data.toPath(), a);
					a = null;

					System.out.println("User will be requested to change password on next login.");
				} else {
					System.out.println("This user doesn't exist.");
				}
			} else if (args[0].equals("del")) {
				if (map.containsKey(args[1])) {
					map.remove(args[1]);
					String a = "";
					for (Map.Entry<String, String> entry : map.entrySet()) {
						a = a + entry.getKey() + ":" + entry.getValue() + ":" + map2.get(entry.getKey()) + ":"
								+ map3.get(entry.getKey()) + ":" + sdf1.format(map4.get(entry.getKey())).toString()
								+ "\n";
					}
					Files.writeString(data.toPath(), a);
					a = null;

					System.out.println("User successfuly removed.");
				} else {
					System.out.println("This user doesn't exist.");
				}
			} else {
				System.out.println("Wrong number of arguments.");
			}
		} else {
			System.out.println("Username too big, limit of 256 characters");
		}
		map = null;
		map2 = null;
		map3 = null;
		map4 = null;
		timestamp = null;
	}

}
