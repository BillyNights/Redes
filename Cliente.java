import java.io.*;
import java.net.*;


/**
 *
 * @author Diego
 */
public class Cliente {

	private int QTD_Pacote = 100;

	public final String ENDERECO_SERVIDOR;
	public final int PORTO_SERVIDOR;

	private Socket soqueteCliente;
	private DataOutputStream saida;
	private DataInputStream entrada;

	public Cliente() {
		ENDERECO_SERVIDOR = "localhost";
		PORTO_SERVIDOR = 5001;
	}

	public Cliente(String enderecoDoServidor, int portoDoServidor) {
		ENDERECO_SERVIDOR = enderecoDoServidor;
		PORTO_SERVIDOR = portoDoServidor;
	}

	public void executar() {

		try {

			// 1- ETAPA DE ABERTURA DA CONEXaO COM O SERVIDOR
			conectarAoServidor();
			obtemFluxosComunicacao();

			// 2- ETAPADA DE COMUNICAcaO ENTRE CLIENTE E SERVIDOR
			// envia e recebe mensagens
		    rtt();
			
			bb();

			// 3 - ETAPA DE FECHAMENTO DA CONEXaO COM O SERVIDOR
			fechaConexao();

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
			// Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE,
			// ex.getMessage(), ex);
		}
	}

	private void conectarAoServidor() throws IOException {
		soqueteCliente = new Socket(InetAddress.getByName(ENDERECO_SERVIDOR), PORTO_SERVIDOR);
		//soqueteCliente.setReceiveBufferSize(64000);
		//soqueteCliente.setSendBufferSize(64000);

		System.out.println("CLIENTE \t[INFO]  conectado ao servidor de IP " + soqueteCliente.getInetAddress()
				+ " ( o porto local e " + soqueteCliente.getLocalPort() + " ).");
	}

	private void obtemFluxosComunicacao() throws IOException {
		ObjectOutputStream fluxoSaida = new ObjectOutputStream(soqueteCliente.getOutputStream());
		saida = new DataOutputStream(fluxoSaida);

		// descarrega o buffer enviando informacoes de cabecalho
		saida.flush();

		ObjectInputStream fluxoEntrada = new ObjectInputStream(soqueteCliente.getInputStream());
		entrada = new DataInputStream(fluxoEntrada);
	}

	private void rtt() throws IOException {
	

		long tempoInicio;
		long somatotal = 0;

		long rttmax = Long.MIN_VALUE;
		long rttmin = Long.MAX_VALUE;
		long rttatual = 0;

		
		String mensagemRecebida = "";
		
		byte buffer[] = new byte[1422];

		for (int i = 0; i < QTD_Pacote; i++) {
			
			tempoInicio = System.currentTimeMillis();
			
			
			// recebe uma mensagem do servidor e exibe para o cliente
			mensagemRecebida = entrada.readUTF();
			//System.out.println("CLIENTE ouve:\t" + mensagemRecebida);

			//System.out.print("CLIENTE diz: \t" + mensagemParaEnviar);
			saida.write(buffer);
			saida.flush();
			
			rttatual=(System.currentTimeMillis()-tempoInicio);
			
			if(rttatual > rttmax){
				  rttmax = rttatual;
			}
			if (rttatual !=0 ){ 
				if(rttatual < rttmin){
				rttmin = rttatual;
			 }
			}
			//System.out.println("\nTempo parcial: "+ (double)(System.currentTimeMillis()-tempoInicio) +" ms");
			somatotal += rttatual;
			
		}
		System.out.println("\n"+QTD_Pacote +" packets transmitted.");
		System.out.println("rtt min/avg/max = "+(double)rttmin +"/"+ (double)somatotal/QTD_Pacote +"/"+ (double)rttmax +" ms\n");

		saida.writeUTF("Sair");
		saida.flush();

	}

    private void bb() throws IOException {

	   // Buffer temporario para armazenar bytes
        byte[] buffer = new byte[1422]; // tamanho do Buffer 1422 bytes
        byte[] sair = new byte[1];
 		double BB = 0;	

 		
 		long tempoInicio, tempoAtual;
        int bytes = 0;
        double totalBytes = 0;
        
        try {
            entrada.readUTF(); //CONECTADO
        	
        	// medição    
            tempoInicio = System.currentTimeMillis();
            for (int i = 0; i < 1000;i++){            // envia durante 1000 unidade de tempo
            	saida.write(buffer);
            	totalBytes += buffer.length;            

            }
            tempoAtual = System.currentTimeMillis();     // recebe tempo atual
            
            System.out.println();
            BB 	=  (((double)totalBytes)/(1000*1000) ) / ( ((double)(tempoAtual - tempoInicio))/1000); // Mbit/s = Mbps
            
            System.out.println(totalBytes + " bytes in "+(tempoAtual - tempoInicio)+" ms");
            System.out.println(BB*8 +" Mbit/s");

            System.out.println("FPS = " + (100 / (BB*8)));
                    
           saida.write('0');
        }
        catch(Exception e){


        } 
	}

	private void fechaConexao() throws IOException {
		saida.close();
		entrada.close();
		soqueteCliente.close();

		System.out.println("\nCLIENTE \t[INFO]  conexao fechada com o servidor!");
	}

	public static void main(String[] args) {

		Cliente cliente = new Cliente();
		cliente.executar();
	}

}
