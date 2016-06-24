package fii.licenta.ioana.securebitexchange.protocol;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ProtocolClient implements Runnable {
    private static SecretKey clientSecretKey = null;
    private static Cipher cipher = null;
    private static byte[] encryptionBytes = null;
    private static byte[] encryptedServerBytes = null;

    private InetAddress address = null;
    private int port = -1;

    private int random = -1;
    public boolean ok = false;
    public boolean ended = false;
    public byte xor;

    private TextView firstTextView;
    private Button button;
    private TextView secondTextView;

    public ProtocolClient(InetAddress address, int port, TextView one, TextView two, Button button) {
        this.address = address;
        this.port = port;
        this.firstTextView = one;
        this.secondTextView = two;
        this.button = button;
    }

    public void connect() throws IOException {
        firstTextView.post(new Runnable() {
            public void run() {
                firstTextView.setText("");
            }
        });
        System.out.println("The client is running.");
        Socket server = new Socket(address, port);
        System.out.println("Connected to server " + address + ".");

        generateRandom();

        final byte clientByte = (byte) random;

        firstTextView.post(new Runnable() {
            public void run() {
                firstTextView.append("Am ales " + clientByte + ".\n");
            }
        });

        System.out.println("Byte: " + clientByte);

        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
        System.out.println("Out stream: Init completed.");
        out.flush();
        ObjectInputStream in = new ObjectInputStream(server.getInputStream());
        System.out.println("In stream: " + in);

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

        firstTextView.post(new Runnable() {
            public void run() {
                firstTextView.append("Cheia mea este " + new String(clientSecretKey.getEncoded()) + ".\n");
                firstTextView.append("Bitul meu criptat este " + new String(encryptionBytes) + ".\n");
            }
        });

        out.writeObject(encryptionBytes);
        out.flush();

        System.out.println("Out stream: Am trimis.." + new String(encryptionBytes));

        try {
            encryptedServerBytes = (byte[]) in.readObject();
            System.out.println("Server sent encrypted bytes: " + new String(encryptedServerBytes));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        firstTextView.post(new Runnable() {
            public void run() {
                firstTextView.append("Am primit de la server bitul criptat: " + new String(encryptedServerBytes) + ".\n");
            }
        });

        button.post(new Runnable() {
            @Override
            public void run() {
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ok = true;
                        secondTextView.setVisibility(View.VISIBLE);
                        secondTextView.setText("");
                    }
                });
            }
        });

        try {
            while(!ok){
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
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

        final String decryptedServerByte = decrypt(encryptedServerBytes, serverSecretKey, cipher);
        System.out.println("Decrypted client's byte choice: " + decryptedServerByte);

        secondTextView.post(new Runnable() {
            public void run() {
                secondTextView.append("Serverul a ales: " + decryptedServerByte + ".\n");
            }
        });

        final byte serverByte = Byte.valueOf(decryptedServerByte);
        xor = (byte) (clientByte ^ serverByte);

        System.out.println("XOR : " + xor);

        secondTextView.post(new Runnable() {
            public void run() {
                secondTextView.append(clientByte + " xor " + serverByte + " = " + xor + ".\n");
            }
        });

        if(xor == 0){
            secondTextView.post(new Runnable() {
                public void run() {
                    secondTextView.append("Serverul incepe.\n");
                }
            });
        }else{
            secondTextView.post(new Runnable() {
                public void run() {
                    secondTextView.append("Clientul incepe.\n");
                }
            });
        }

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

    public void generateRandom() {
        HttpRequest request = (HttpRequest) new HttpRequest().execute();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        random = request.getIntResult();
        System.out.println(" randomul " + random);
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
