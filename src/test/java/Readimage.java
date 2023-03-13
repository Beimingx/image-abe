import com.mathworks.toolbox.javabuilder.MWException;
import imageprocessor.ImageProcessor;

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
    public static Frame frame=new Frame("图像拥有者");
    public Frame frame2=new Frame("图像查看者");
    public Frame frame3=new Frame("属性选择");
    byte[] m1;
    byte[] k1;
    byte[] c0;
    public Readimage(byte[] m1, byte[] k1, byte[] c0){
        this.m1=m1;
        this.k1=k1;
        this.c0=c0;
    }
    Readimage readimage;
    public Button encrypt=new Button("属性选择");
    public Button decrypt4=new Button("信息提取与图像恢复");
    public Button decrypt5=new Button("信息提取与图像恢复");
    public Button send=new Button("信息嵌入");
    Button open=new Button("打开图片");
    Button open2=new Button("打开图片");
    TextField ted=new TextField("",60);
    TextField ted2=new TextField("",60);
    TextArea tree=new TextArea("初始属性有四个，分别设定为a,b,c,d\n发送方：用户1\n发送方定义的解密属性策略:\n必须同时具有a，d两种属性才可查看完整图像",13,40);
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
    JLabel jLabel3= new JLabel("                    载密图像                    ",SwingConstants.CENTER);
    JLabel jLabel4= new JLabel("    用户2：具有属性a,d",SwingConstants.CENTER);
    JLabel jLabel5= new JLabel("用户3:具有属性a,c",SwingConstants.CENTER);



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
            try {//read image
                image = ImageIO.read(new File(dir,fileName));
                drawArea1.repaint();
                jLabel1.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            //System.out.println("ok");
        });//ok


        send.addActionListener(e -> {//加入一个选择文件（选择嵌入信息）
            String testimg=ted.getText();
            FileDialog fileDialog = new FileDialog(frame, "选择嵌入信息", FileDialog.LOAD);//fake
            fileDialog.setVisible(true);
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
            domedialog();
        });


        frame3.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                frame3.dispose();
                try {
                    readimage=encryptabe();
                    JOptionPane.showMessageDialog(null, "加密成功！", "提示",JOptionPane.PLAIN_MESSAGE);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
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
                frame.dispose();
            }
        });
    }


    public void deimage(){

        frame2.setFont(new Font("宋体",Font.BOLD,22));
        jLabel3.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel4.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel5.setFont(new Font("仿宋",Font.BOLD,20));
        jLabel4.setBackground(Color.WHITE);
        jLabel5.setBackground(Color.WHITE);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        decrypt4.setVisible(false);
        decrypt5.setVisible(false);
        tree.setVisible(false);
        drawArea4.setVisible(false);
        drawArea5.setVisible(false);

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
                jLabel4.setVisible(true);
                jLabel5.setVisible(true);
                decrypt4.setVisible(true);
                decrypt5.setVisible(true);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            //System.out.println("ok");
        });//ok


        decrypt4.addActionListener(e -> {

            String dir="D:\\dcpabe\\res-img\\";
            String fileName2="recovered.bmp";
            try {
                //decryptabe("a","d",readimage);
                drawArea4.setVisible(true);
                image = ImageIO.read(new File(dir,fileName2));
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
                drawArea5.setVisible(true);
                drawArea5.repaint();
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
        mrbox.add(Box.createHorizontalStrut(60));

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

        splitPane2.setBackground(Color.WHITE);

        frame2.add(p,BorderLayout.NORTH);
        frame2.add(splitPane2,BorderLayout.CENTER);
        frame2.setBounds(100, 100, 1200, 930);

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

    public void domedialog(){
        frame3.setTitle("属性选择");
        frame3.setVisible(true);
        frame3.setLocation(500,300);
        frame3.setSize(500,270);
        frame3.setBackground(Color.WHITE);


        //Container containerPane=frame3.getContentPane();
        JLabel jLabel=new JLabel("  请选择解密所需要具备的属性:",SwingConstants.LEFT);
        Box tbox=Box.createVerticalBox();
        tbox.add(Box.createVerticalStrut(20));
        tbox.add(jLabel);
        frame3.add(tbox,BorderLayout.NORTH);

        JPanel p=new JPanel();
        JCheckBox checkBox1=new JCheckBox("a");
        checkBox1.setSize(30,30);
        JCheckBox checkBox2=new JCheckBox("b");
        JCheckBox checkBox3=new JCheckBox("c");
        JCheckBox checkBox4=new JCheckBox("d");
        p.add(checkBox1);
        p.add(checkBox2);
        p.add(checkBox3);
        p.add(checkBox4);
        p.setBackground(Color.WHITE);
        checkBox1.setBackground(Color.WHITE);
        checkBox2.setBackground(Color.WHITE);
        checkBox3.setBackground(Color.WHITE);
        checkBox4.setBackground(Color.WHITE);

        p.setLayout(new FlowLayout(FlowLayout.LEADING,40,40));
        frame3.add(p,BorderLayout.CENTER);

        Button button=new Button("确定");
        Box box=Box.createVerticalBox();
        Box mbox=Box.createHorizontalBox();
        mbox.add(Box.createHorizontalGlue());
        mbox.add(button);
        mbox.add(Box.createHorizontalGlue());
        box.add(mbox);
        box.add(Box.createVerticalStrut(20));
        box.setBackground(Color.WHITE);
        frame3.add(box,BorderLayout.SOUTH);

        jLabel.setFont(new Font("宋体",Font.BOLD,22));
        checkBox1.setFont(new Font("宋体",Font.BOLD,28));
        checkBox2.setFont(new Font("宋体",Font.BOLD,28));
        checkBox3.setFont(new Font("宋体",Font.BOLD,28));
        checkBox4.setFont(new Font("宋体",Font.BOLD,28));
        button.setFont(new Font("宋体",Font.BOLD,22));

        button.addActionListener(e -> {
            frame3.dispose();
        });

        frame3.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame3.dispose();
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
                JOptionPane.showMessageDialog(null, "属性不满足，图像无法恢复！", "提示", JOptionPane.PLAIN_MESSAGE);
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

            byte[] decryptedPayload;//01bit
            ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
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
