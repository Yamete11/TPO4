package com.example.serverchannel;

import javafx.application.Application;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Client {
    static SocketChannel channel = null;
    static Charset charset  = Charset.forName("ISO-8859-2");
    static Map<String, String> topics;


    public static void main(String[] args) throws IOException {
        new Client();
        Application.launch(GUIClient.class, args);
    }

    public Client() throws IOException {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 7887));
            System.out.print("Klient: łączę się z serwerem ...");

            while (!channel.finishConnect()) {

            }

        } catch(UnknownHostException exc) {
            channel.close();
            System.err.println("Uknown host ");
        } catch(Exception exc) {
            channel.close();
            exc.printStackTrace();
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

    public static void updateTopic() throws IOException {
        channel.write(charset.encode("getNews,,tak" + "\n"));

        String answer = "";
        ByteBuffer inBuf = ByteBuffer.allocateDirect(1024);
        CharBuffer cbuf = null;

        while(true){


            int readBytes = channel.read(inBuf);
            if(readBytes == 0){
            } else {
                inBuf.flip();
                cbuf = charset.decode(inBuf);
                answer = cbuf.toString();
                System.out.println("The text I got :" + answer);
                cbuf.clear();
                System.out.println("Answer + " + answer);
                List<String> ans = List.of(answer.split(",,"));
                GUIClient.secondList.getItems().retainAll(ans);
                GUIClient.listView.getItems().retainAll(ans);
                List<String> newList = Stream.concat(GUIClient.listView.getItems().stream(), GUIClient.secondList.getItems().stream())
                        .collect(Collectors.toList());

                for(String an : ans){
                    if(!newList.contains(an)) GUIClient.listView.getItems().add(an);
                }

                inBuf.clear();
                break;
            }
        }
        if(GUIClient.secondList.getItems().size() > 0){
            String joined = GUIClient.secondList.getItems().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(",,"));

            System.out.println(joined);
            channel.write(charset.encode("getT,," + joined + "\n"));

            while(true){
                inBuf.clear();

                int readBytes = channel.read(inBuf);
                if(readBytes == 0){

                } else {
                    inBuf.flip();
                    cbuf = charset.decode(inBuf);
                    answer = cbuf.toString();
                    GUIClient.textArea.setText(answer);

                    cbuf.clear();

                    break;
                }
            }
        } else {
            GUIClient.textArea.setText("");
        }


    }
}

