package fr.cpe.genial_degre;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liaison avec le Button
        Button bt = findViewById(R.id.bouton);

        // Action lorsque le bouton est cliqué
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lancer l'activité ServeurConfigurationActivity
                Intent intent = new Intent(MainActivity.this, ServeurConfigurationActivity.class);
                startActivity(intent);
            }
        });
    }
}
