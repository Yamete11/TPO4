package com.example.serverchannel;

import javafx.application.Application;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Admin{
    static SocketChannel channel = null;
    static Charset charset  = Charset.forName("ISO-8859-2");

    public static void main(String[] args) {
        new Admin();
        Application.launch(GUIAdmin.class, args);

    }

    public Admin() {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 7887));
            System.out.print("Klient: łączę się z serwerem ...");

            while (!channel.finishConnect()) {

            }

        } catch(UnknownHostException exc) {
            System.err.println("Uknown host ");
        } catch(Exception exc) {
            exc.printStackTrace();
        }



    }

    public static void addTopic(String topic, String cmd, String text) throws IOException {
        if(cmd.equals("add")){
            channel.write(charset.encode(cmd + ",," + topic + "\n"));
        } else if(cmd.equals("update")){
            channel.write(charset.encode(cmd + ",," + topic + ",," + text +  "\n"));
        } else if(cmd.equals("remove")){
            channel.write(charset.encode(cmd + ",," + topic + "\n"));
        }
    }

    public static String updateTopic(String topic, String cmd) throws IOException {
        channel.write(charset.encode(cmd + ",," + topic + "\n"));
        String answer = " ";

        ByteBuffer inBuf = ByteBuffer.allocateDirect(1024);
        CharBuffer cbuf = null;

        while(true){
            inBuf.clear();

            int readBytes = channel.read(inBuf);
            if(readBytes == 0){
            } else {
                inBuf.flip();
                cbuf = charset.decode(inBuf);
                answer = cbuf.toString();
                System.out.println("The text I got :" + answer);
                cbuf.clear();

                return answer;
            }

        }

    }

    public static String getTopic() throws IOException {

        channel.write(charset.encode("getTopic,,tak" + "\n"));

        String answer = " ";

        ByteBuffer inBuf = ByteBuffer.allocateDirect(1024);
        CharBuffer cbuf = null;

        while(true){
            inBuf.clear();

            int readBytes = channel.read(inBuf);
            if(readBytes == 0){
            } else {
                inBuf.flip();
                cbuf = charset.decode(inBuf);
                answer = cbuf.toString();
                System.out.println("The text I got :" + answer);
                cbuf.clear();
                System.out.println("Answer + " + answer);
                return answer;
            }
        }
    }







}

