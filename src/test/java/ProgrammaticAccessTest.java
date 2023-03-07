import org.apache.commons.io.IOUtils;
import org.junit.Test;
import sg.edu.ntu.sce.sands.crypto.dcpabe.*;
import sg.edu.ntu.sce.sands.crypto.dcpabe.ac.AccessStructure;
import sg.edu.ntu.sce.sands.crypto.utility.Utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class ProgrammaticAccessTest {
    @Test
    public void testKeyCorrectlyDecrypted() throws IOException {
        GlobalParameters GP = DCPABE.globalSetup(160);//setup 160
        AccessStructure accessStructure = AccessStructure.buildFromPolicy("A");

        AuthorityKeys authorityKeys = DCPABE.authoritySetup("auth1", GP, "A", "B", "C", "D");

        Message message = DCPABE.generateRandomMessage(GP);//random message

        PublicKeys publicKeys = new PublicKeys();
        publicKeys.subscribeAuthority(authorityKeys.getPublicKeys());

        Ciphertext encryptedMessage = DCPABE.encrypt(message, accessStructure, GP, publicKeys);

        PersonalKeys personalKeys = new PersonalKeys("myID");
        personalKeys.addKey(DCPABE.keyGen("myID", "A", authorityKeys.getSecretKeys().get("A"), GP));
        personalKeys.addKey(DCPABE.keyGen("myID", "B", authorityKeys.getSecretKeys().get("B"), GP));
        personalKeys.addKey(DCPABE.keyGen("myID", "C", authorityKeys.getSecretKeys().get("C"), GP));
        personalKeys.addKey(DCPABE.keyGen("myID", "D", authorityKeys.getSecretKeys().get("D"), GP));

        Message decryptedMessage = DCPABE.decrypt(encryptedMessage, personalKeys, GP);
        assertArrayEquals(message.getM(), decryptedMessage.getM());
        System.out.println("Key correctly decrypted!");

    }

    @Test
    public void testMessageCorrectlyDecrypted() throws IOException {
        GlobalParameters GP = DCPABE.globalSetup(160);
        AccessStructure accessStructure = AccessStructure.buildFromPolicy("E");

        AuthorityKeys authorityKeys = DCPABE.authoritySetup("auth1", GP, "A", "B", "C", "D");

        byte[] fileBytes;
        try (
               //InputStream inputStream = getClass().getResourceAsStream("/testfile.txt");
               InputStream inputStream = getClass().getResourceAsStream("/key.txt");
        ) {
            fileBytes = IOUtils.toByteArray(inputStream);
        }

        //String test =new String(fileBytes);
        //System.out.println("test txt:"+test);

        Message message = DCPABE.generateRandomMessage(GP);//message=key

        byte[] encryptedPayload;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes)) {
            encryptedPayload = Utility.encryptAndDecrypt(message.getM(), true, bais);
            //byte[] encryptAndDecrypt(byte[] key, boolean doEncrypt, InputStream message),aes encrypt
        }

        String aesres =new String(encryptedPayload);
        System.out.println("aes res:"+aesres);

        PublicKeys publicKeys = new PublicKeys();
        publicKeys.subscribeAuthority(authorityKeys.getPublicKeys());

        Ciphertext encryptedMessage = DCPABE.encrypt(message, accessStructure, GP, publicKeys);//cpabe encrypt message
        //Ciphertext encrypt(Message message, AccessStructure arho, GlobalParameters GP, PublicKeys pks)

        PersonalKeys personalKeys = new PersonalKeys("myID");
        personalKeys.addKey(DCPABE.keyGen("myID", "A", authorityKeys.getSecretKeys().get("A"), GP));
        personalKeys.addKey(DCPABE.keyGen("myID", "B", authorityKeys.getSecretKeys().get("B"), GP));
        personalKeys.addKey(DCPABE.keyGen("myID", "C", authorityKeys.getSecretKeys().get("C"), GP));
        personalKeys.addKey(DCPABE.keyGen("myID", "D", authorityKeys.getSecretKeys().get("D"), GP));

        Message decryptedMessage = DCPABE.decrypt(encryptedMessage, personalKeys, GP);

        assertArrayEquals(message.getM(), decryptedMessage.getM());

        byte[] decryptedPayload;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(encryptedPayload)) {
            decryptedPayload =Utility.encryptAndDecrypt(message.getM(), false, bais);
        }

        assertArrayEquals(fileBytes, decryptedPayload);

        String deres =new String(decryptedPayload);
        System.out.println("decrypt:"+deres);

    }
}
