package fr.cpe.genial_degre;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServeurConfigurationActivity extends AppCompatActivity {

    public static String IP = "192.168.1.181"; // Remplacer par l'IP de votre interlocuteur
    public static int PORT = 10000; // Constante arbitraire du sujet
    private InetAddress address; // Structure Java décrivant une adresse résolue
    private DatagramSocket UDPSocket; // Structure Java permettant d'accéder au réseau (UDP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Utiliser le layout serveurconfiguration.xml
        setContentView(R.layout.serveurconfiguration);

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
            }
        });
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
