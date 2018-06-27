import java.net.*;
import java.io.*;
import java.util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class servidor implements Runnable {

    private static Socket socketLoadB = null;
    private static BufferedReader in = null;
    private static PrintWriter out = null;

    public static void main(String args[]) throws UnknownHostException, IOException {

        String host = "172.24.10.27";
        host = "127.0.0.1";
        try {
            socketLoadB = new Socket(host, 54321);

            in = new BufferedReader(new InputStreamReader(socketLoadB.getInputStream()));
            out = new PrintWriter(socketLoadB.getOutputStream(), true);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host " + host);
        }

        if (socketLoadB != null && out != null) {
            try {
                new Thread(new servidor()).start();
            } catch (Exception e) {
                System.err.println("Exception:  " + e);
            }
        }
    }

    public boolean verificaPrimo(int n) {
        int i;
        if (n <= 1 || (n != 2 && n % 2 == 0))
            return (false);

        for (i = 3; i < Math.sqrt(n); i += 2) {
            if (n % i == 0) {
                return (false);
            }
        }
        return (true);
    }

    @Override
    public void run() {

        try {

            File file = new File("saida1.txt");
            if (!file.exists())
                file.createNewFile();

            while (true) {
                String primo = "";

                FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                try {
                    String read = "Leitura";
                    String line = in.readLine();

                    System.out.println(line);

                    if (line.contains(read)) {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        System.out.println("---- Início do arquivo ----");

                        while (reader.ready()) {
                            String linha = reader.readLine();
                            System.out.println(linha);
                        }
                        reader.close();
                        System.out.println("---- Fim do arquivo ----");

                    } else if (line.equals("stop")) {
                        long inicio = System.currentTimeMillis();
                        String resposta = in.readLine();
                        while (!resposta.contains("primo")) {
                            long decorrido = System.currentTimeMillis() - inicio;
                            if (decorrido < 100) {
                                resposta = in.readLine();
                            } else {
                                System.out.println("Tempo estourado");
                                break;
                            }
                        }
                        if (resposta.contains("primo"))
                            bufferedWriter.write(resposta + "\n");
                    } else {
                        int num = Integer.parseInt(line);
                        boolean ehPrimo = verificaPrimo(num);
                        if (ehPrimo == true) {
                            primo = "O valor " + line + " é primo\n";
                        } else {
                            primo = "O valor " + line + " não é primo\n";
                        }
                        try {
                            bufferedWriter.write(primo);
                            out.println(primo);
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bufferedWriter.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }}

    
    
        
    
    
    
        
    
    
    
    
    
    
    
        
    
    
    
    
    
    
        
    
    
    
        
    
