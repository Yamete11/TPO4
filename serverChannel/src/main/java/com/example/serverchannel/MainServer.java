package com.example.serverchannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class MainServer {
    Map<String, String> topics;

    public static void main(String[] args) throws IOException, InterruptedException {
        new MainServer();
    }

    MainServer () throws IOException {

        topics = new HashMap<>();

        // Utworzenie kanału gniazda serwera
        // i związanie go z konkretnym adresem (host+port)
        String host = "localhost";
        int port = 7887;
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(host, port));

        // Ustalenie trybu nieblokującego
        // dla kanału serwera gniazda
        serverChannel.configureBlocking(false);

        // Utworzenie selektora
        Selector selector = Selector.open();

        // Rejestracja kanału gniazda serwera u selektora
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Serwer: czekam ... ");


        while (true) {


            selector.select();


            Set<SelectionKey> keys = selector.selectedKeys();


            Iterator<SelectionKey> iter = keys.iterator();

            while(iter.hasNext()) {


                SelectionKey key = iter.next();


                iter.remove();


                if (key.isAcceptable()) {

                    System.out.println("Serwer: ktoś się połączył ..., akceptuję go ... ");

                    SocketChannel cc = serverChannel.accept();


                    cc.configureBlocking(false);


                    cc.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                    continue;
                }

                if (key.isReadable()) {


                    SocketChannel cc = (SocketChannel) key.channel();

                    serviceRequest(cc);


                    continue;
                }

            }
        }

    }



    private static Charset charset  = Charset.forName("ISO-8859-2");
    private static final int BSIZE = 1024;


    private ByteBuffer bbuf = ByteBuffer.allocate(BSIZE);


    private StringBuffer reqString = new StringBuffer();


    private void serviceRequest(SocketChannel sc)  {
        try {


            if (!sc.isOpen()) return;

            System.out.print("Serwer: czytam komunikat od klienta ... ");

            reqString.setLength(0);
            bbuf.clear();

            sc.read(bbuf);
            bbuf.flip();
            CharBuffer cbuf = charset.decode(bbuf);
            while (cbuf.hasRemaining()) {
                char c = cbuf.get();
                if (c == '\r' || c == '\n') {
                    break;
                }
                reqString.append(c);
            }
            String word = reqString.toString();
            String[] arr = word.split(",,");

            if (arr[0].equals("add")) {
                topics.put(arr[1], " ");
            } else if (arr[0].equals("remove")) {
                topics.remove(arr[1]);

            } else if (arr[0].equals("update")) {
                topics.replace(arr[1], arr[2]);

            } else if (arr[0].equals("getText")) {
                System.out.println("Text i am sending : " + " Topic is : " + arr[1] + topics.get(arr[1]));
                sc.write(charset.encode(topics.get(arr[1])));
            } else if (arr[0].equals("getTopic")) {
                String message = "";
                if (topics.size() == 0) {
                    message = ",,";
                } else {
                    for (String str : topics.keySet()) {
                        message += str + ",,";
                    }
                }
                sc.write(charset.encode(message));
            } else if(arr[0].equals("getNews")){
                String message = "";
                if (topics.size() == 0) {
                    message = ",,";
                } else {
                    for (String str : topics.keySet()) {
                        message += str + ",,";
                    }
                }
                sc.write(charset.encode(message));

            } else if(arr[0].equals("getT")){
                String message = "";
                for(int i = 1; i < arr.length; i++){
                    System.out.println(topics.get(arr[i]));
                    message += arr[i] + " : " + topics.get(arr[i]) + "\n\n";
                }
                sc.write(charset.encode(message));
            }

            System.out.println(topics);
        } catch (IOException exc) { // przerwane polączenie?
            exc.printStackTrace();
            try { sc.close();
                sc.socket().close();
            } catch (Exception e) {}
        }

    }




}

