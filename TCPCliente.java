import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TCPCliente {

    private static JTextField mensajeCliente;
    private static JTextArea mostrarMensaje;
    private static DataOutputStream outToServer;
    private static BufferedReader inFromServer;

    public static void main(String argv[]) throws Exception {

        // configuración de la ventana
        JFrame ventana = new JFrame("Chat Grupal");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(400, 300); // tamaño ventana
        ventana.setLocationRelativeTo(null); // centrar ventana
        ventana.setVisible(true); // hacerla visible

        // confurguración del chat
        mostrarMensaje = new JTextArea();
        mostrarMensaje.setEditable(false);
        ventana.add(new JScrollPane(mostrarMensaje), BorderLayout.CENTER);

        // configuración del mensaje de cliente
        mensajeCliente = new JTextField();
        JButton enviarmensaje = new JButton("Enviar");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mensajeCliente, BorderLayout.CENTER);
        panel.add(enviarmensaje, BorderLayout.EAST);
        ventana.add(panel, BorderLayout.SOUTH);

        enviarmensaje.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    enviarMensaje();
                } catch (IOException ex) {
                    mostrarMensaje.append("Se ha producido un error al enviar el mensaje: " + ex.getMessage() + "\n");
                }
            }
        });

        ventana.setVisible(true);

        // configuración del socket
        try {

            // creando el socket
            Socket Socketcliente = new Socket("localhost", 6789);
            outToServer = new DataOutputStream(Socketcliente.getOutputStream());
            inFromServer = new BufferedReader(new InputStreamReader(Socketcliente.getInputStream()));

            // creando el hilo para recibir mensajes del servidor
            Thread hiloRecibir = new Thread(() -> {

                try {
                    while (true) {
                        String respuesta = inFromServer.readLine();
                        if (respuesta != null) {
                            mostrarMensaje.append(respuesta + "\n");
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mostrarMensaje.append("Error al conectar con el servidor: " + ex.getMessage() + "\n");
                }
            });
            hiloRecibir.start(); // iniciar el hilo para recibir mensajes
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    // Método para enviar el mensaje al servidor y limpiar el campo de texto
    private static void enviarMensaje() throws IOException {
        try {
            String mensaje = mensajeCliente.getText();
            outToServer.writeBytes(mensaje + '\n');
            mensajeCliente.setText(""); // aquí limpia
        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarMensaje.append("Error al enviar el mensaje: " + ex.getMessage() + "\n");
        }
    }
}
