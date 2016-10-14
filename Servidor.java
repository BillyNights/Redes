
import java.io.*;
import java.net.*;

/**
 *
 * @author Diego
 */
public class Servidor {

	public final int PORTO_SERVIDOR;

	private ServerSocket soqueteServidor;

	private Socket conexaoCliente;
	private DataOutputStream saida;
	private DataInputStream entrada;

	public Servidor() {
		PORTO_SERVIDOR = 5001;
	}

	public Servidor(int portoDoServico) {
		PORTO_SERVIDOR = portoDoServico;
	}

	public void executar() {

		try {

			// 1- ETAPA DE ABERTURA DO SOQUETE QUE RECEBERa AS CONEXoES
			// abre o soquete onde os clientes conectarao
			abreSoqueteServidor();

			// executa eternamente, atendendo um cliente por vez
			while (true) {

				// 1- ETAPA DE ESPERA E ABERTURA DA CONEXaO COM O CLIENTE
				esperaConexaoCliente();
				obtemFluxosComunicacao();

				// 2- ETAPADA DE COMUNICAcaO ENTRE SERVIDOR E CLIENTE
				// envia e recebe mensagens
				comunicaComCliente();

				// 3 - ETAPA DE FECHAMENTO DA CONEXaO COM O SERVIDOR
				fechaConexao();
			}

		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}

	private void abreSoqueteServidor() throws IOException {
		soqueteServidor = new ServerSocket(PORTO_SERVIDOR);

		System.out.println("SERVIDOR\t[INFO]  soquete aberto no PORTO " + soqueteServidor.getLocalPort());
	}

	private void esperaConexaoCliente() throws IOException {
		System.out.println("SERVIDOR\t[INFO]  aguardando algum cliente se conectar.");

		conexaoCliente = soqueteServidor.accept();
		//conexaoCliente.setReceiveBufferSize(64*1000);
		//conexaoCliente.setSendBufferSize(64*1000);

		System.out.println("SERVIDOR\t[INFO]  conectado ao cliente de IP " + conexaoCliente.getInetAddress()
				+ " no PORTO " + conexaoCliente.getPort());
	}

	private void obtemFluxosComunicacao() throws IOException {
		ObjectOutputStream fluxoSaida = new ObjectOutputStream(conexaoCliente.getOutputStream());
		saida = new DataOutputStream( new BufferedOutputStream(fluxoSaida) );

		ObjectInputStream fluxoEntrada = new ObjectInputStream(conexaoCliente.getInputStream());
		entrada = new DataInputStream( new BufferedInputStream(fluxoEntrada) );

		System.out.println("SERVIDOR\t[DEBUG] fluxos de comunicacao obtidos.");
	}

	private void comunicaComCliente() throws IOException {
		System.out.println("SERVIDOR\t[DEBUG] pronto para comunicar com o cliente.");

	
		String mensagemRecebida = "";

		byte buffer[] = new byte[1422];
		
		// OBS.: � necess�rio algu�m enviar a primeira mensagem sen�o os dois
		// lados ficar�o esperando no comando entrada.readUTF()
		saida.writeUTF("CONECTADO");
		saida.flush();

		do {

			// recebe e exibe uma mensagem do cliente para o servidor
			mensagemRecebida = entrada.readUTF();

			// l� e envia uma mensagem do servidor para o cliente
			saida.write(buffer);
			saida.flush();

		} while (!mensagemRecebida.contains("0"));

	}

	private void fechaConexao() throws IOException {
		saida.close();
		entrada.close();
		conexaoCliente.close();

		System.out.println("SERVIDOR\t[INFO]  conexao fechada com o cliente!");
	}

	public static void main(String[] args) {

		Servidor servidor = new Servidor();
		servidor.executar();
	}

}
