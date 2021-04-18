package sample;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.PublicKey;

public class Server {

    private static Socket client;
    private static PrintWriter writer;
    private static BufferedReader reader;
    private static String secret;
    private static KeyPair keyPair;
    private static PublicKey serverKey;

    public static void connect() {
        try {
            client = new Socket("116.203.157.140", 5555);
            System.out.println("Client gesartet");

            OutputStream out = client.getOutputStream();
            writer = new PrintWriter(out);

            InputStream in = client.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));

            keyPair = RSA.generateKeyPair();
            sendPublicKey();

        } catch (IOException ex) {
            if (ex instanceof SocketException) {
                System.out.println("Server closed");
            } else
                ex.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            writer.close();
            reader.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSalt(String username) {
        try {
            writer.write(RSA.encrypt(serverKey, "getsalt:" + username) + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkPassword(String passwordHash, String username) {
        try {
            writer.write(RSA.encrypt(serverKey, "checkpasswd:" + username + ":" + passwordHash) + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);

            if (s.equalsIgnoreCase("NONE")) {
                return false;
            } else {
                secret = s;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasAccount(String username) {
        try {
            writer.write(RSA.encrypt(serverKey, "hasAccount:" + username) + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);

            return Boolean.parseBoolean(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasLoggedIn(String username) {
        try {
            writer.write(RSA.encrypt(serverKey, "hasLoggedIn:" + username) + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);

            return Boolean.parseBoolean(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getSkips(String username) {
        try {
            writer.write(RSA.encrypt(serverKey, "getSkips:" + username + ":" + secret) + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);
            return Integer.parseInt(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void addSkip(String username, String executingStaff) {
        writer.write(RSA.encrypt(serverKey, "addSkip:" + username + ":" + executingStaff + ":" + secret) + "\n");
        writer.flush();
    }

    public static void setPassword(String hashedPassword, String username) {
        writer.write(RSA.encrypt(serverKey, "setPassword:" + hashedPassword + ":" + username + ":") + "\n");
        writer.flush();
    }

    public static void logout() {
        secret = "";

    }


    public static void setSalt(String salt, String username) {
        writer.write(RSA.encrypt(serverKey, "setSalt:" + salt + ":" + username + ":") + "\n");
        writer.flush();
    }

    public static void sendPublicKey() {
        try {
            writer.write("clientPublic:" + RSA.getKeyAsString(keyPair.getPublic()) + "\n");
            writer.flush();
            String s;
            while ((s = reader.readLine()) != null) {
                break;
            }
            serverKey = RSA.getKeyFromString(s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int getMaxSkips() {
        try {
            writer.write(RSA.encrypt(serverKey, "getMaxSkips:") + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);

            return Integer.parseInt(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getBackground(String username) {
        try {
            writer.write(RSA.encrypt(serverKey, "getBackground:" + username) + "\n");
            writer.flush();
            String sraw;
            while ((sraw = reader.readLine()) != null) {
                break;
            }

            String s = RSA.decrypt(keyPair.getPrivate(), sraw);

            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setBackground(String username, String background) {
        writer.write(RSA.encrypt(serverKey, "setBackground:" + username + ":" + background) + "\n");
        writer.flush();
    }

    public static void removeSkip(String username) {
        writer.write(RSA.encrypt(serverKey, "removeSkip:" + username) + "\n");
        writer.flush();
    }



}
