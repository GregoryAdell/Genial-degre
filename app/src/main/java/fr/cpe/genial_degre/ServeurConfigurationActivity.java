package fr.cpe.genial_degre;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServeurConfigurationActivity extends AppCompatActivity {

    public static String IP = "192.168.1.181"; // IP par défaut mais possibilité de remplacer
    public static int PORT = 10000; // Port arbitraire par défaut mais possibilité de remplacer
    private InetAddress address; // Structure Java décrivant une adresse résolue
    private DatagramSocket UDPSocket; // Structure Java permettant d'accéder au réseau (UDP)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Utiliser le layout serveurconfiguration.xml
        setContentView(R.layout.serveurconfiguration);

        // Initialisation des champs EditText pour IP et Port
        EditText txtIp = findViewById(R.id.txt_ip);
        EditText txtPort = findViewById(R.id.txt_port);

        // Bouton pour retourner à la MainActivity
        Button btRetour = findViewById(R.id.retourPagePrincipale);
        btRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServeurConfigurationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Appliquer les insets système pour une meilleure gestion de l'affichage
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bt_validate_serveur), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bouton pour valider la configuration du serveur
        Button btValidateServeur = findViewById(R.id.bt_validate_serveur);
        btValidateServeur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupération des valeurs saisies
                String ipInput = txtIp.getText().toString();
                String portInput = txtPort.getText().toString();

                // Vérification du format de l'adresse IP (utilisation des regular expression (Regex))
                if (!ipInput.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
                    txtIp.setError("Veuillez entrer une adresse IP valide");
                    return;
                }

                // Vérification du numéro de port
                int port;
                port = Integer.parseInt(portInput);
                if (port < 1 || port > 65535) {
                    txtPort.setError("Veuillez entrer un numéro de Port entre 1 et 65535");
                    return;
                }

                // Mise à jour des variables IP et PORT si les valeurs sont valides
                IP = ipInput;
                PORT = port;

                // Retour à l'activité principale
                Intent intent = new Intent(ServeurConfigurationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        // Mettre à jour les TextView avec l'IP et le Port
        TextView textViewIP = findViewById(R.id.textViewIP);
        TextView textViewPort = findViewById(R.id.textViewPort);
        textViewIP.setText("IP : " + IP);
        textViewPort.setText("Port : " + PORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            UDPSocket = new DatagramSocket();
            address = InetAddress.getByName(IP);
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (UDPSocket != null && !UDPSocket.isClosed()) {
            UDPSocket.close();
        }
    }
}
