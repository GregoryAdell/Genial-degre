package fr.cpe.genial_degre;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
public class MainActivity extends AppCompatActivity {
    private final String IP="192.168.1.184"; // Remplacer par l'IP de votre interlocuteur
    private final int PORT=10000; // Constante arbitraire du sujet
    private InetAddress address; // Structure Java décrivant une adresse résolue
    private DatagramSocket UDPSocket; // Structure Java permettant d'accéder au réseau (UDP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button bt = findViewById(R.id.bouton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyThread myThread = new MyThread("Bonjour c'est Greg et Léna", IP, PORT);
                myThread.start();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            UDPSocket = new DatagramSocket();
            address = InetAddress.getByName("192.168.1.184");
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