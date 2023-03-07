import com.mathworks.toolbox.javabuilder.MWException;
import imageprocessor.ImageProcessor;


import org.apache.commons.io.IOUtils;
import sg.edu.ntu.sce.sands.crypto.dcpabe.*;
import sg.edu.ntu.sce.sands.crypto.dcpabe.ac.AccessStructure;
import sg.edu.ntu.sce.sands.crypto.utility.Utility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;


public class Readimage {

    //容器
    public static Frame frame=new Frame("加密处理");
    public Frame frame2=new Frame("解密处理");
    byte[] m1;
    byte[] k1;
    byte[] c0;
    public Readimage(byte[] m1, byte[] k1, byte[] c0){
        this.m1=m1;
        this.k1=k1;
        this.c0=c0;
    }
    Readimage readimage;

    public Button encrypt=new Button("访问加密");
    public Button decrypt4=new Button("访问解密");
    public Button decrypt5=new Button("访问解密");
    public Button send=new Button("图像隐藏");
    Button open=new Button("打开图片");
    Button open2=new Button("打开图片");
    TextField ted=new TextField("",60);
    TextField ted2=new TextField("",60);
    TextArea tree=new TextArea("Au=a,b,c,d\nAccess structure:and a or d and b c",13,40);
    BufferedImage image;



    private class MyCanvas extends Canvas{
            public void paint(Graphics g){
            g.drawImage(image, 0, 0,null);

        }
    }
    MyCanvas drawArea1=new MyCanvas();
    MyCanvas drawArea2=new MyCanvas();
    MyCanvas drawArea3=new MyCanvas();
    MyCanvas drawArea4=new MyCanvas();
    MyCanvas drawArea5=new MyCanvas();
    JLabel jLabel1= new JLabel("原始图像",SwingConstants.CENTER);
    JLabel jLabel2= new JLabel("携带秘密信息的载密图像",SwingConstants.CENTER);
    JLabel jLabel3= new JLabel("                    加密图像                    ",SwingConstants.CENTER);
    JLabel jLabel4= new JLabel("arth2：具有属性a,d",SwingConstants.CENTER);
    JLabel jLabel5= new JLabel("arth3:具有属性b,c",SwingConstants.CENTER);



    static final GlobalParameters gp= DCPABE.globalSetup(160);
    static final AuthorityKeys authority= DCPABE.authoritySetup("auth1", gp, "a", "b", "c", "d");//Au
    static final AccessStructure as= AccessStructure.buildFromPolicy("and a or d and b c");//access structure

    public void init() {

        jLabel1.setVisible(false);
        jLabel2.setVisible(false);


        open.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog(frame, "打开图片", FileDialog.LOAD);
            fileDialog.setVisible(true);

            String dir=fileDialog.getDirectory();
            String fileName=fileDialog.getFile();
            ted.setText(dir+fileName);
//            jLabel1.setIcon(new ImageIcon(dir+fileName));
//            jLabel1.setVerticalTextPosition(SwingConstants.BOTTOM);

            try {//read image
                image = ImageIO.read(new File(dir,fileName));
                drawArea1.repaint();
                jLabel1.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            //System.out.println("ok");
        });//ok


        send.addActionListener(e -> {
            String testimg=ted.getText();

            try {
                send(testimg);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            //String testimg="D:\\dcpabe\\test-img\\15.jpg";
            String dir2= "D:\\dcpabe\\res-img\\";
            String fileName2 = "face.bmp";
            try {
                image = ImageIO.read(new File(dir2, fileName2));
                drawArea2.repaint();
                jLabel2.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        encrypt.addActionListener(e -> {
            try {
                readimage=encryptabe();
                JOptionPane.showMessageDialog(null, "加密成功！", "提示",JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });


        //组装面板
        frame.setFont(new Font("宋体",Font.BOLD,22));
        jLabel1.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel2.setFont(new Font("仿宋",Font.BOLD,20));

        Box tbox = Box.createHorizontalBox();
        tbox.add(Box.createHorizontalGlue());
        tbox.add(ted);
        tbox.add(Box.createHorizontalGlue());
        tbox.add(open);
        tbox.add(Box.createHorizontalGlue());
        tbox.add(send);
        tbox.add(Box.createHorizontalGlue());
        tbox.add(encrypt);
        tbox.add(Box.createHorizontalGlue());
        frame.add(tbox, BorderLayout.NORTH);

        Panel l = new Panel();
        l.setLayout(new BorderLayout());
        l.add(jLabel1, BorderLayout.NORTH);
        l.add(Box.createHorizontalStrut(60),BorderLayout.WEST);
        l.add(drawArea1,BorderLayout.CENTER);

        Panel r = new Panel();
        r.setLayout(new BorderLayout());
        r.add(jLabel2, BorderLayout.NORTH);
        r.add(Box.createHorizontalStrut(20),BorderLayout.WEST);
        r.add(drawArea2,BorderLayout.CENTER);
        r.add(Box.createHorizontalStrut(60),BorderLayout.EAST);

        JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,l,r);
        frame.add(splitPane,BorderLayout.CENTER);

        frame.setBounds(200, 100, 1200, 500);

        splitPane.setDividerSize(1);
        splitPane.setDividerLocation(600);
        //frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                    System.exit(1);
                frame.dispose();
            }
        });
    }


    public void deimage(){

        frame2.setFont(new Font("宋体",Font.BOLD,22));
        jLabel3.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel4.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel5.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel3.setVisible(false);
        tree.setVisible(false);
        //写入监听
        open2.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog(frame2, "打开图片", FileDialog.LOAD);
            fileDialog.setVisible(true);

            String dir=fileDialog.getDirectory();
            String fileName=fileDialog.getFile();
            ted2.setText(dir+fileName);

            try {//read image
                image = ImageIO.read(new File(dir,fileName));
                drawArea3.repaint();
                jLabel3.setVisible(true);
                tree.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            //System.out.println("ok");
        });//ok


        decrypt4.addActionListener(e -> {

            String dir="D:\\dcpabe\\res-img\\";
            String fileName="recovered.bmp";
            try {
                decryptabe("a","d",readimage);
                image = ImageIO.read(new File(dir,fileName));
                drawArea4.repaint();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        decrypt5.addActionListener(e -> {

            String dir="D:\\dcpabe\\res-img\\";
            String fileName="recovered.bmp";
            try {
                decryptabe("b","c",readimage);
                image = ImageIO.read(new File(dir,fileName));
                drawArea4.repaint();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        //组装面板
        Box tbox= Box.createHorizontalBox();
        tbox.add(Box.createHorizontalGlue());
        tbox.add(ted2);
        tbox.add(Box.createHorizontalGlue());
        tbox.add(open2);
        tbox.add(Box.createHorizontalGlue());

        Box ttbox=Box.createVerticalBox();
        ttbox.add(tbox,BorderLayout.NORTH);
        ttbox.add(Box.createVerticalStrut(6));

        Box mlbox=Box.createVerticalBox();
        mlbox.add(jLabel3);
        mlbox.add(drawArea3);
        Box mbox=Box.createHorizontalBox();
        mbox.add(Box.createHorizontalStrut(67));
        mbox.add(mlbox);

        Box mrbox=Box.createHorizontalBox();
        mrbox.add(tree);
        mrbox.add(Box.createHorizontalStrut(67));

        Panel p = new Panel();
        p.setLayout(new BorderLayout());
        p.add(ttbox, BorderLayout.NORTH);
        p.add(mbox,BorderLayout.WEST);
        p.add(mrbox,BorderLayout.EAST);
        p.add(Box.createVerticalStrut(20),BorderLayout.SOUTH);


        Panel bl = new Panel();
        bl.setLayout(new BorderLayout());
        Box bltbox=Box.createHorizontalBox();
        bltbox.add(Box.createHorizontalGlue());
        bltbox.add(jLabel4);
        bltbox.add(Box.createHorizontalGlue());
        bltbox.add(decrypt4);
        bltbox.add(Box.createHorizontalGlue());
        bl.add(bltbox,BorderLayout.NORTH);

        bl.add(Box.createHorizontalStrut(60),BorderLayout.WEST);
        bl.add(drawArea4,BorderLayout.CENTER);


        Panel br = new Panel();
        br.setLayout(new BorderLayout());

        Box brtbox=Box.createHorizontalBox();
        brtbox.add(Box.createHorizontalGlue());
        brtbox.add(jLabel5);
        brtbox.add(Box.createHorizontalGlue());
        brtbox.add(decrypt5);
        brtbox.add(Box.createHorizontalGlue());
        br.add(brtbox,BorderLayout.NORTH);
        br.add(Box.createHorizontalStrut(6),BorderLayout.WEST);
        br.add(drawArea5,BorderLayout.CENTER);
        br.add(Box.createHorizontalStrut(60),BorderLayout.EAST);

        JSplitPane splitPane2= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,bl,br);

        frame2.add(p,BorderLayout.NORTH);
        frame2.add(splitPane2,BorderLayout.CENTER);
        frame2.setBounds(100, 100, 1200, 900);

        splitPane2.setDividerSize(1);
        splitPane2.setDividerLocation(600);
        frame2.setVisible(true);
        p.setVisible(true);
        splitPane2.setVisible(true);
        frame2.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);

            }
        });


    }

        public void send(String testimg) throws Exception {
            ImageProcessor processor = null;
            try {
                processor = new ImageProcessor();
            } catch (MWException ex) {
                throw new RuntimeException(ex);
            }
            String respath = "D:\\dcpabe\\res-img\\";
            String addpath = "D:\\dcpabe\\add-img\\";
            String keypath = "d:\\dcpabe\\src\\test\\resources\\key.txt";
            try {
                Object[] flag = processor.Sender(1, testimg, respath, keypath,addpath);
            } catch (MWException ex) {
                throw new RuntimeException(ex);
            }
        }


        public Readimage encryptabe() throws Exception {
            //GlobalParameters gp = DCPABE.globalSetup(160);
            //AccessStructure accessStructure = AccessStructure.buildFromPolicy("E");
            //AuthorityKeys authorityKeys = DCPABE.authoritySetup("auth1", GP, "A", "B", "C", "D");
            //send("d:\\dcpabe\\test-img\\15.jpg");

            PublicKeys publicKeys = new PublicKeys();
            publicKeys.subscribeAuthority(authority.getPublicKeys());//get pubkey
            //AccessStructure as = AccessStructure.buildFromPolicy("and a or d and b c");//access structure

//            byte[] fileBytes;
//            try (
//                    InputStream inputStream = getClass().getResourceAsStream("/key.txt")//生成的01bit流
//            ) {
//                assert inputStream != null;
//                fileBytes = IOUtils.toByteArray(inputStream);
//            }


            String path = "D:\\dcpabe\\src\\test\\resources\\key.txt";//加密后的结果覆盖原文件
            File keyfile=new File(path);
            byte[] fileBytes=new byte[(int) keyfile.length()];

            FileInputStream fis=new FileInputStream(keyfile);
            fis.read(fileBytes);
            fis.close();

            Message message = DCPABE.generateRandomMessage(gp);
            byte[] encryptedPayload;
            try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes)) {
                encryptedPayload = Utility.encryptAndDecrypt(message.getM(), true, bais);
                //byte[] encryptAndDecrypt(byte[] key, boolean doEncrypt, InputStream message),aes encrypt
            }

//            try {
//                FileOutputStream outputStream = new FileOutputStream(path);
//               // outputStream.write(Base64.getEncoder().encode(encryptedPayload));//加密后的key
//                outputStream.write(encryptedPayload);//
//                outputStream.flush();
//                outputStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)));
                outputStream.writeObject(encryptedPayload);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("ok");
            Ciphertext ct = DCPABE.encrypt(message, as, gp, publicKeys);


            String path1 = "D:\\dcpabe\\src\\test\\resources\\encryptedMessage.txt";//加密后的message
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path1)));
                outputStream.writeObject(ct);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                FileOutputStream outputStream = new FileOutputStream(path);
//               // outputStream.write(Base64.getEncoder().encode(encryptedPayload));
//                outputStream.write(ct.getC0());
//                outputStream.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            Readimage readimage=new Readimage(message.getM(),fileBytes,ct.getC0());
            System.out.println("send and encrypt success!");
            return readimage;
        }


        public void decryptabe(String att1,String att2,Readimage readimage) throws Exception {

            PersonalKeys pkeys = new PersonalKeys("user");
            pkeys.addKey(DCPABE.keyGen("user", att1, authority.getSecretKeys().get(att1), gp));//b is not satisfying
            pkeys.addKey(DCPABE.keyGen("user", att2, authority.getSecretKeys().get(att2), gp));//c is not satisfying

            //先解密message  abe
            Ciphertext ct2 = new Ciphertext();
            ObjectInputStream objectInputStream;

            String path1 = "D:\\dcpabe\\src\\test\\resources\\encryptedMessage.txt";//加密后的message
            //   String path2="D://dcpabe//src//test//resources//encryptedMessage.txt";
            try {
                objectInputStream = new ObjectInputStream(Files.newInputStream(new File(path1).toPath()));
                ct2 = (Ciphertext) objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }//ct==ct

//            assertArrayEquals(ct2.getC0(), readimage.c0);
//            System.out.println("ct=ct");
            Message dMessage = null;
            try {
                dMessage = DCPABE.decrypt(ct2, pkeys, gp);

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "解密错误！", "提示", JOptionPane.PLAIN_MESSAGE);
            }

            //再用message解密key aes算法
            String path = "D:\\dcpabe\\src\\test\\resources\\key.txt";
            byte[] fileBytes = new byte[0];
            try {
                objectInputStream = new ObjectInputStream(Files.newInputStream(new File(path).toPath()));
                fileBytes = (byte[]) objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }//filebytes==encryptedpayload

            System.out.println("ok");
            byte[] decryptedPayload;//01bit
            ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
            System.out.println("ok1");
            decryptedPayload = Utility.encryptAndDecrypt(dMessage.getM(), false, bais);//wrong

//            byte[] k2=decryptedPayload;
//            assertArrayEquals(readimage.k1,k2);
//            System.out.println("key=key");

            try {
                FileOutputStream outputStream = new FileOutputStream(path);
                outputStream.write(decryptedPayload);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("ok4");
            recivetest(path);
            System.out.println("decrypt key success!");
        }

        public void recivetest(String keypath) throws Exception{
            ImageProcessor processor = new ImageProcessor();
            String respath="D:\\dcpabe\\res-img\\";
            String addpath="D:\\dcpabe\\add-img\\";
            Object[] flag1=processor.Reciver(1,respath,keypath,addpath);
            System.out.println("recive ok");
        }



    public static void main(String[] args)  {
        byte[] m1 = new byte[0];
        byte[] k1=new byte[0];;
        byte[] c0=new byte[0];
        Readimage rg=new Readimage(m1,k1,c0);
        rg.init();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                rg.deimage();
            }
        });

    }


}
