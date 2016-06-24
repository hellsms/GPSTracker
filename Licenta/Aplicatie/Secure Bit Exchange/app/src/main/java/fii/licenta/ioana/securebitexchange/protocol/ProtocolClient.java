package fii.licenta.ioana.securebitexchange.protocol;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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

public class ProtocolClient implements Runnable{
    private static SecretKey clientSecretKey = null;
    private static Cipher cipher = null;

    private InetAddress address = null;
    private int port = -1;

    public boolean ended = false;
    public byte xor;

    public ProtocolClient(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }

    public void connect() throws IOException {
        System.out.println("The client is running.");
        Socket server = new Socket(address, port);
        System.out.println("Connected to server " + address + ".");

        Random random = new Random();
        byte clientByte = (byte) random.nextInt(2);

        System.out.println("Byte: " + clientByte);

        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        System.out.println("Out stream: Init completed.");
        out.flush();
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        System.out.println("In stream: " + in);

        byte[] encryptionBytes = null;
        try {
            clientSecretKey = KeyGenerator.getInstance("DESede").generateKey();
            cipher = Cipher.getInstance("DESede");
            encryptionBytes = encrypt(String.valueOf(clientByte), clientSecretKey, cipher);
            System.out.println("Encrypted: " + new String(encryptionBytes));
            System.out.println("Decrypted: " + decrypt(encryptionBytes, clientSecretKey, cipher));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        out.writeObject(encryptionBytes);
        out.flush();

        System.out.println("Out stream: Am trimis.." + new String(encryptionBytes));

        byte[] encryptedServerBytes = null;
        try {
            encryptedServerBytes = (byte[]) in.readObject();
            System.out.println("Server sent encrypted bytes: " + new String(encryptedServerBytes));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Client secret key: " + new String(clientSecretKey.getEncoded()));
        out.writeObject(clientSecretKey);
        out.flush();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SecretKey serverSecretKey = null;
        try {
            serverSecretKey = (SecretKey) in.readObject();
            System.out.println("Server sent secrey key: " + new String(serverSecretKey.getEncoded()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String decryptedServerByte = decrypt(encryptedServerBytes, serverSecretKey, cipher);
        System.out.println("Decrypted client's byte choice: " + decryptedServerByte);

        byte serverByte = Byte.valueOf(decryptedServerByte);
        xor = (byte) (clientByte ^ serverByte);

        System.out.println("XOR : " + xor);

        ended = true;
        out.close();
        in.close();
        server.close();
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
            connect();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
