import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;


public class LoadBalancer {

	public boolean status = false;

	public static ServerSocket socketServer = null;
	private static ServerSocket socketClient = null;
	private static ServerSocket socketTeste = null;
	ArrayBlockingQueue<String> fila = new ArrayBlockingQueue<String>(5000);
	ArrayBlockingQueue<String> filaClientes = new ArrayBlockingQueue<String>(5000);

	public static void main(String[] args) throws IOException {
		new LoadBalancer().run();
	}


	public void run() {
		try {
			socketClient = new ServerSocket(12345);
			socketServer = new ServerSocket(54321);
			socketTeste = new ServerSocket(9999);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try{
			Socket ss1 = socketServer.accept();
			Socket ss2 = socketServer.accept();
			Socket ss3 = socketServer.accept();

			// Socket teste1 = socketTeste.accept();
			// Socket teste2 = socketTeste.accept();
			// Socket teste3 = socketTeste.accept();


			try{
				new clientThread(socketClient.accept(), fila, filaClientes).start();
				new serverThread(ss1, ss2, ss3,fila, filaClientes, socketServer).start();
				// new testaConexao(teste1,teste2,teste3).start();

			}catch(Exception ex){
				ex.printStackTrace();
			}
		}catch(Exception e){

		}

	}
}

class testaConexao extends Thread{
	Socket teste1 = null;
	Socket teste2 = null;
	Socket teste3 = null;

	private static BufferedReader inTeste1 = null;
	private static PrintWriter outTeste1 = null;

	private static BufferedReader inTeste2 = null;
	private static PrintWriter outTeste2 = null;

	private static BufferedReader inTeste3 = null;
	private static PrintWriter outTeste3 = null;


	public testaConexao(Socket s1, Socket s2, Socket s3){
		this.teste1 = s1;
		this.teste2 = s2;
		this.teste3 = s3;

		try{
			inTeste1 = new BufferedReader(new InputStreamReader(teste1.getInputStream()));
			outTeste1 = new PrintWriter(teste1.getOutputStream(), true);

			inTeste2 = new BufferedReader(new InputStreamReader(teste2.getInputStream()));
			outTeste2 = new PrintWriter(teste2.getOutputStream(), true);

			inTeste3 = new BufferedReader(new InputStreamReader(teste3.getInputStream()));
			outTeste3 = new PrintWriter(teste3.getOutputStream(), true);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(){
		String line = null;
		while(true){
			try{

				line = inTeste1.readLine();
				while(!line.equals("vivo")){
					line = inTeste1.readLine();
				}
			}catch(Exception e){
				System.out.println("servidor 1 caiu");
			}

			try{
				line = inTeste2.readLine();
				while(!line.equals("vivo")){
					line = inTeste2.readLine();
				}
			}catch(Exception e){
				System.out.println("servidor 2 caiu");
			}

			try{
				// start = Calendar.getInstance().get(Calendar.MILLISECOND);
				line = inTeste3.readLine();
				while(!line.equals("vivo")){
					line = inTeste3.readLine();
					// long end = Calendar.getInstance().get(Calendar.MILLISECOND);
					// if((end-start) > 1000){
					// 	System.out.println("servidor 3 caiu");
					// 	break;
					// }
				}
			}catch(Exception e){
				System.out.println("servidor 3 caiu");
			}
		}
	}
}

class clientThread extends Thread {

	ArrayBlockingQueue<String> fila = null;
	ArrayBlockingQueue<String> filaClientes = null;
	private BufferedReader in = null;
	private Socket clientSocket = null;

	public clientThread(Socket clientSocket, ArrayBlockingQueue<String> fila, ArrayBlockingQueue<String> filaClientes) {
		this.clientSocket = clientSocket;
		this.fila = fila;
		this.filaClientes = filaClientes;
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new	InputStreamReader(clientSocket.getInputStream()));

			while (true) {
				String line = in.readLine();
				if(line !=null){
					fila.add(line);

				}
				String number = in.readLine();
				if(number!=null){
					filaClientes.add(number);
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
}

class serverThread extends Thread {

	ArrayBlockingQueue<String> fila = null;
	ArrayBlockingQueue<String> filaClientes = null;
	private static BufferedReader in1 = null;
	private static BufferedReader in2 = null;
	private static BufferedReader in3 = null;
	public static Socket serverSocket = null;
	public static Socket serverSocket2 = null;
	public static Socket serverSocket3 = null;
	private String operacao = null;
	private String cliente = null;
	private static PrintWriter out1 = null;
	private static PrintWriter out2= null;
	private static PrintWriter out3 = null;
	private int count = 0;

	public static ServerSocket socketServer = null;

	boolean caiu1 = false;
	boolean caiu2 = false;
	boolean caiu3 = false;



	Random rand = new Random();


	public serverThread(Socket serverSocket, Socket serverSocket2, Socket serverSocket3, ArrayBlockingQueue<String> fila, ArrayBlockingQueue<String> filaClientes, ServerSocket socketServer ) {
		this.serverSocket  = serverSocket;
		this.serverSocket2 = serverSocket2;
		this.serverSocket3 = serverSocket3;
		this.fila = fila;
		this.filaClientes = filaClientes;
		this.socketServer = socketServer;


		try{
			out1 = new PrintWriter(serverSocket.getOutputStream(),  true);
			out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
			out3 = new PrintWriter(serverSocket3.getOutputStream(), true);
			this.in1 = new BufferedReader(new	InputStreamReader(serverSocket.getInputStream()) );
			this.in2 = new BufferedReader(new	InputStreamReader(serverSocket2.getInputStream()));
			this.in3 = new BufferedReader(new	InputStreamReader(serverSocket3.getInputStream()));

		}catch(Exception e){
			// e.printStackTrace();
		}

	}





	Thread reconecta1  = new Thread(){
		@Override
		public void run(){
			try{
				serverSocket.close();
				serverSocket = socketServer.accept();
				out1 = new PrintWriter(serverSocket.getOutputStream(), true);
				in1 = new BufferedReader(new	InputStreamReader(serverSocket.getInputStream()));
			}catch(Exception e){
				System.out.println("nada ainda");
			}
			if(serverSocket.isConnected()){
				System.out.println("conectado1");
				caiu1 = false;
			}

		}
	};

	Thread reconecta2  = new Thread(){
		@Override
		public void run(){
			try{
				serverSocket2.close();
				serverSocket2 = socketServer.accept();
				out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
				in2 = new BufferedReader(new	InputStreamReader(serverSocket2.getInputStream()));


			}catch(Exception e){
				System.out.println("nada ainda");
			}
			if(serverSocket2.isConnected()){
				System.out.println("conectado2");
				caiu2 = false;

			}

		}
	};

	Thread reconecta3  = new Thread(){
		@Override
		public void run(){
			try{
				serverSocket3.close();
				serverSocket3 = socketServer.accept();
				out3 = new PrintWriter(serverSocket3.getOutputStream(), true);
				in3 = new BufferedReader(new	InputStreamReader(serverSocket3.getInputStream()));


			}catch(Exception e){
				System.out.println("nada ainda");
			}
			if(serverSocket3.isConnected()){
				System.out.println("conectado3");
				caiu3 = false;

			}

		}
	};

	// testaConexao.start();

	@Override
	public void run() {
		int num = 0;

		while(true){

			try {

				this.operacao = fila.take();
				this.cliente = filaClientes.take();

				if(!operacao.equals("Leitura")){
					num = rand.nextInt(3)+1;
					System.out.println("ok" + num);

					// System.out.println("Valor " + this.operacao+ " recebido do cliente" + this.cliente + " e direcionada ao Servidor de Dados " + num);
					switch (num){
						case 1:
						if(!caiu1){
							try{
								long inicio1 = System.currentTimeMillis();

								out1.println(this.operacao);
								count = fila.size();
								// System.out.println("Processo de consistência iniciado, enfileirando requisições");
								out3.println("stop");
								out2.println("stop");
								String resposta1 = in1.readLine();
								while(!resposta1.contains("primo")){
									long decorrido1 = System.currentTimeMillis() - inicio1;
									if(decorrido1>1000){
										System.out.println("Tempo estourado");
										break;
									}
									resposta1 = in1.readLine();
								}
								System.out.println(resposta1);
								out3.println(resposta1);
								out2.println(resposta1);
								count = fila.size() - count;
								// System.out.println("Procedimento de consistência finalizado, foram enfileiradas " + count +  " requisições neste período");
								break;

							}catch(Exception e){
								System.out.println("Servidor caiu1");
								caiu1 = true;
								reconecta1.start();
								System.out.println("voltou");
								break;

							}
						}
						break;


						case 2:
						if(!caiu2){
							try{
								long inicio2 = System.currentTimeMillis();
								out2.println(this.operacao);
								count = fila.size();
								// System.out.println("Processo de consistência iniciado, enfileirando requisições");
								out3.println("stop");
								out1.println("stop");
								String resposta2 = in2.readLine();
								while(!resposta2.contains("primo")){

									resposta2 = in2.readLine();
								}
								System.out.println(resposta2);

								out3.println(resposta2);
								out1.println(resposta2);
								count = fila.size() - count;
								// System.out.println("Procedimento de consistência finalizado, foram enfileiradas " + count +  " requisições neste período");
								break;

							}catch(Exception e){
								System.out.println("Servidor caiu2");
								caiu2 = true;
								reconecta2.start();
								System.out.println("voltou");
								break;

							}

						}
						break;



						case 3:
						if(!caiu3){

							try{
								long inicio3 = System.currentTimeMillis();

								out3.println(this.operacao);
								count = fila.size();
								// System.out.println("Processo de consistência iniciado, enfileirando requisições");
								out2.println("stop");
								out1.println("stop");

								String resposta3 = in3.readLine();
								while(!resposta3.contains("primo")){
									long decorrido3 = System.currentTimeMillis() - inicio3;
									if(decorrido3>1000){
										System.out.println("Tempo estourado");
										break;
									}
									resposta3 = in3.readLine();
								}
								System.out.println(resposta3);
								out2.println(resposta3);
								out1.println(resposta3);
								count = fila.size() - count;
								break;

							}catch(Exception e){
								System.out.println("Servidor caiu3");
								caiu3 = true;
								reconecta3.start();
								System.out.println("voltou");
								break;

							}

						}
						break;
						// System.out.println("Procedimento de consistência finalizado, foram enfileiradas " + count +  " requisições neste período");
						default:
						break;
					}
				}else{
					num = rand.nextInt(3)+1;
					// System.out.println("Leitura encaminhada pelo cliente " + this.cliente + " e direcionada ao Servidor de Dados " + num);
					switch (num) {						
						case 1:
						if(!caiu1)
							out1.println(this.operacao);
						break;
						case 2:
						if(!caiu2)
							out2.println(this.operacao);
						break;
						case 3:
						if(!caiu3)
							out3.println(this.operacao);
						break;
						default:
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
