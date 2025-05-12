import java.io.*;
import java.net.*;

import javax.xml.crypto.Data;

class TCPServer {
    public static void main(String argv[]) throws Exception {

        final int numeroPuerto = 6789;
        System.out.println("Se iniciializa el servidor en el puerto " + numeroPuerto);

        try (ServerSocket servidorSocket = new ServerSocket(numeroPuerto)) {
            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                System.out.println(
                        "Cliente conectado desde " + clienteSocket.getInetAddress() + ":" + clienteSocket.getPort());

                new ClienteHandler(clienteSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Clase de hilos para clientes
class ClienteHandler extends Thread {

    private Socket clienteSocket;

    public ClienteHandler(Socket socket) {
        this.clienteSocket = socket;
    }

    @Override
    public void run() {

        try (
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(clienteSocket.getOutputStream())

        ) {
            String mensajeCliente;
            while ((mensajeCliente = inFromClient.readLine()) != null) {

                String respuesta = mensajeCliente.toUpperCase() + '\n';
                outToClient.writeBytes(respuesta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
