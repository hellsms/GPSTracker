package fii.licenta.ioana.securebitexchange.protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ProtocolServer implements Runnable{
    private static SecretKey serverSecretKey = null;
    private static Cipher cipher = null;

    private ServerSocket listener = null;

    public static boolean ended = false;
    public static byte xor;

    public ProtocolServer(ServerSocket serverSocket){
        this.listener = serverSocket;
    }

    public void start() throws IOException {
        if (listener != null) {
            System.out.println("The server is running.");
        }
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    resolveClient(socket);
                } catch (IOException e) {

                } finally {
                    socket.close();
                }
            }
        } finally {
            listener.close();
        }
    }

    private static void resolveClient(Socket client) throws IOException {
        System.out.println("Client connected.");

        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        System.out.println("Out stream: Init completed.." + out);
        out.flush();
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());
        System.out.println("In stream: " + in);

        Random random = new Random();
        byte serverByte = (byte) random.nextInt(2);

        System.out.println("Byte: " + serverByte);

        byte[] encryptionBytes = null;
        try {
            serverSecretKey = KeyGenerator.getInstance("DESede").generateKey();
            cipher = Cipher.getInstance("DESede");
            encryptionBytes = encrypt(String.valueOf(serverByte), serverSecretKey, cipher);
            System.out.println("Encrypted: " + new String(encryptionBytes));
            System.out.println("Decrypted: " + decrypt(encryptionBytes, serverSecretKey, cipher));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        byte[] encryptedClientBytes = null;
        try {
            encryptedClientBytes = (byte[]) in.readObject();
            System.out.println("Client sent encrypted bytes: " + new String(encryptedClientBytes));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        out.writeObject(encryptionBytes);
        out.flush();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SecretKey clientSecretKey = null;
        try {
            clientSecretKey = (SecretKey) in.readObject();
            System.out.println("Client sent secrey key: " + new String(clientSecretKey.getEncoded()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Server secret key: " + new String(serverSecretKey.getEncoded()));
        out.writeObject(serverSecretKey);
        out.flush();

        String decryptedClientByte = decrypt(encryptedClientBytes, clientSecretKey, cipher);
        System.out.println("Decrypted client's byte choice: " + decryptedClientByte);

        byte clientByte = Byte.valueOf(decryptedClientByte);
        xor = (byte) (clientByte ^ serverByte);

        System.out.println("XOR : " + xor);

        ended = true;
        in.close();
        out.close();
        client.close();
    }

    private static byte[] encrypt(String input, Key key, Cipher cipher) {
        byte[] inputBytes = null;
        byte[] outputBytes = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            inputBytes = input.getBytes();
            outputBytes = cipher.doFinal(inputBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return outputBytes;
    }

    private static String decrypt(byte[] encryptionBytes, Key key, Cipher cipher) {
        byte[] decrypt = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypt = cipher.doFinal(encryptionBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(decrypt);
    }

    @Override
    public void run() {
        try{
            start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
