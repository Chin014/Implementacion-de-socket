import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

class TCPServer {

    private static List<DataOutputStream> clientes = new ArrayList<>();

    public static void main(String argv[]) throws Exception {

        final int numeroPuerto = 6789;
        System.out.println("Se iniciializa el servidor en el puerto " + numeroPuerto);

        try (ServerSocket servidorSocket = new ServerSocket(numeroPuerto)) {
            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                String nombreCliente = clienteSocket.getInetAddress().getHostName(); // para obtener el nombre del
                                                                                     // cliente
                System.out.println(
                        "Nuevo cliente conectado " + nombreCliente);

                DataOutputStream outToClient = new DataOutputStream(clienteSocket.getOutputStream());
                clientes.add(outToClient);
                new ClienteChatHandler(clienteSocket, nombreCliente).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Clase de hilos para clientes
    static class ClienteChatHandler extends Thread {

        private Socket clienteSocket;
        private String nombreCliente;

        public ClienteChatHandler(Socket socket, String nombreCliente) {
            this.clienteSocket = socket;
            this.nombreCliente = nombreCliente;
        }

        @Override
        public void run() {

            try (
                    BufferedReader inFromClient = new BufferedReader(
                            new InputStreamReader(clienteSocket.getInputStream()))

            ) {
                String mensajeCliente;
                while ((mensajeCliente = inFromClient.readLine()) != null) {

                    enviarMensajeATods(nombreCliente + ": " + mensajeCliente);
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

        private void enviarMensajeATods(String mensaje) {
            for (DataOutputStream cliente : clientes) {
                try {
                    cliente.writeBytes(mensaje + '\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
