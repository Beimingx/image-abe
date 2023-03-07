import imageprocessor.ImageProcessor;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import sg.edu.ntu.sce.sands.crypto.dcpabe.*;
import sg.edu.ntu.sce.sands.crypto.dcpabe.ac.AccessStructure;
import sg.edu.ntu.sce.sands.crypto.utility.Utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;

public class ImageProcessorTest {

    public void send() throws Exception {
        ImageProcessor processor = new ImageProcessor();
        String testimg="D:\\dcpabe\\test-img\\17.jpg";
        String respath="D:\\dcpabe\\res-img\\";
        String addpath="D:\\dcpabe\\add-img\\";
        String keypath = "d:\\dcpabe\\src\\test\\resources\\key.txt";//加密后的结果覆盖原文件
        Object[] flag=processor.Sender(1,testimg,respath,keypath,addpath);

    }


    @Test
    public void sendandrecive() throws Exception {
        //GlobalParameters GP = DCPABE.globalSetup(160);
        //AccessStructure accessStructure = AccessStructure.buildFromPolicy("E");
        //AuthorityKeys authorityKeys = DCPABE.authoritySetup("auth1", GP, "A", "B", "C", "D");
        send();

        PublicKeys publicKeys = new PublicKeys();

        GlobalParameters gp = DCPABE.globalSetup(160);

        AuthorityKeys authority = DCPABE.authoritySetup("a1", gp, "a", "b","c","d");
        publicKeys.subscribeAuthority(authority.getPublicKeys());

        PersonalKeys pkeys = new PersonalKeys("user");
        pkeys.addKey(DCPABE.keyGen("user", "a", authority.getSecretKeys().get("a"), gp));//b is not satisfying
        pkeys.addKey(DCPABE.keyGen("user", "b", authority.getSecretKeys().get("b"), gp));//c is not satisfying

        AccessStructure as = AccessStructure.buildFromPolicy("and a or d and b c");//?

//        try (
        //InputStream inputStream = getClass().getResourceAsStream("/testfile.txt");！！！这个函数并不能完整读入key中所有的数据！！！
        //InputStream inputStream = getClass().getResourceAsStream("/key.txt");//生成的01bit流
//        ) {
//            assert inputStream != null;
//            fileBytes = IOUtils.toByteArray(inputStream);
//        }
        File keyfile=new File("D:\\dcpabe\\src\\test\\resources\\key.txt");
        byte[] fileBytes=new byte[(int) keyfile.length()];

        FileInputStream fis=new FileInputStream(keyfile);
        fis.read(fileBytes);
        fis.close();

        System.out.println(fileBytes.length);
        String path3 = "D:\\dcpabe\\src\\test\\resources\\key3.txt";//加密后的结果
        try {
            FileOutputStream outputStream  = new FileOutputStream(path3);
            outputStream.write(fileBytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message message = DCPABE.generateRandomMessage(gp);
        byte[] encryptedPayload;
        ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);

        encryptedPayload = Utility.encryptAndDecrypt(message.getM(), true, bais);


  //      String aesres =new String(encryptedPayload);//aes加密bit流的结果


//        //String path = "d:\\dcpabe\\src\\test\\resources\\key.txt";//加密后的结果覆盖原文件
//        String patht = "d:\\dcpabe\\src\\test\\resources\\key1.txt";//加密后的结果覆盖原文件
////        try {
////            FileOutputStream outputStream  = new FileOutputStream(path);
////            outputStream.write(aesres.getBytes());
////            outputStream.close();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//        //Ciphertext ct = DCPABE.encrypt(message, as, gp, publicKeys);
//
//        try {
//            ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(patht)));
//            outputStream.writeObject(encryptedPayload);
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

       // String path = "D:\\dcpabe\\src\\test\\resources\\key.txt";//加密后的结果覆盖原文件
        String path2 = "D:\\dcpabe\\src\\test\\resources\\key1.txt";//加密后的结果
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path2)));
            outputStream.writeObject(encryptedPayload);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Ciphertext ct = DCPABE.encrypt(message, as, gp, publicKeys);
        String path1 = "D:\\dcpabe\\src\\test\\resources\\encryptedMessage.txt";//加密后的message
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path1)));
            outputStream.writeObject(ct);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("send and encrypt success!");

//        try {
//            FileOutputStream outputStream  = new FileOutputStream(path1);
//            outputStream.write(ct.getC0());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("send and encrypt success!");

        Ciphertext ct2=new Ciphertext();
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(Files.newInputStream(new File(path1).toPath()));
            ct2 = (Ciphertext) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertArrayEquals(ct.getC0(), ct2.getC0());
        System.out.println("ct=ct2");

        Message dMessage = DCPABE.decrypt(ct2, pkeys, gp);

        assertArrayEquals(message.getM(), dMessage.getM());
        System.out.println("message=dmessage");

        byte[] fileBytes2 = new byte[0];
        try {
            objectInputStream = new ObjectInputStream(Files.newInputStream(new File(path2).toPath()));
            fileBytes2 = (byte[]) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        byte[] decryptedPayload;
        ByteArrayInputStream bais2 = new ByteArrayInputStream(fileBytes2);

        decryptedPayload =Utility.encryptAndDecrypt(message.getM(), false, bais2);

        assertArrayEquals(fileBytes, decryptedPayload);
        System.out.println("filebytes=decryptpayload");

        try {
            FileOutputStream outputStream  = new FileOutputStream(path2);
            outputStream.write(decryptedPayload);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        recivetest(path2);
        System.out.println("decrypt key success!");
    }


    public void recivetest(String keypath) throws Exception{
        ImageProcessor processor = new ImageProcessor();
        String respath="D:\\dcpabe\\res-img\\";
        String addpath="D:\\dcpabe\\add-img\\";
        Object[] flag1=processor.Reciver(1,respath,keypath,addpath);
        System.out.println(flag1);
    }

}
