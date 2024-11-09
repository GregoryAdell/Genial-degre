package fr.cpe.genial_degre;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Changer la couleur de la barre de navigation et de la barre d'état
        getWindow().setNavigationBarColor(Color.parseColor("#fcefdd")); // Couleur de la barre de navigation
        getWindow().setStatusBarColor(Color.parseColor("#fcefdd")); // Couleur de la barre d'état

        Button bouton_Serv = findViewById(R.id.bouton_Serv);
        bouton_Serv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ServeurConfigurationActivity.class);
                startActivity(intent);
            }
        });

        Button boutonInfo = findViewById(R.id.bouton_Info);
        boutonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action à réaliser lors du clic sur le bouton
            }
        });



        RecyclerView recyclerViewLeft = findViewById(R.id.recyclerView_left);
        RecyclerView recyclerViewRight = findViewById(R.id.recyclerView_right);

        // Configuration de la liste déplaçable
        List<String> leftItems = new ArrayList<>();
        Collections.addAll(leftItems, "Lux", "Infra Rouge", "UV", "Temp", "Humidité", "Pression");
        Adapter adapterLeft = new Adapter(leftItems); // Utiliser un adaptateur avec drag-and-drop
        recyclerViewLeft.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLeft.setAdapter(adapterLeft);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(leftItems, fromPosition, toPosition);
                adapterLeft.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Non utilisé
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerViewLeft);

        // Configuration de la liste fixe
        List<String> rightItems = new ArrayList<>();
        Collections.addAll(rightItems, "V1", "V2", "V3", "V4", "V5", "V6");
        Adapter adapterRight = new Adapter(rightItems); // Utiliser un adaptateur sans drag-and-drop
        recyclerViewRight.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRight.setAdapter(adapterRight);
    }
}
