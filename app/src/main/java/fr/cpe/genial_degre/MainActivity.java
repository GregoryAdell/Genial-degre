package fr.cpe.genial_degre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyThreadEventListener {

    private MyThread myThread;

    private void saveListOrder(List<String> list) {
        SharedPreferences prefs = getSharedPreferences("list_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("list_order", json);
        editor.apply();
    }

    private List<String> loadListOrder() {
        SharedPreferences prefs = getSharedPreferences("list_prefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("list_order", null);

        if (json != null) {
            return gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
        } else {
            List<String> defaultList = new ArrayList<>();
            Collections.addAll(defaultList, "Lux", "Infra Rouge", "UV", "Temp", "Humidité", "Pression");
            return defaultList;
        }
    }

    @Override
    public void onEventInMyThread(String data) {
        runOnUiThread(() -> Toast.makeText(this, data, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myThread != null) {
            myThread.stopThread(); // Arrête le thread proprement
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myThread = new MyThread(ServeurConfigurationActivity.IP, ServeurConfigurationActivity.PORT, this);
        myThread.start();

        // Changer la couleur de la barre de navigation et de la barre d'état
        getWindow().setNavigationBarColor(Color.parseColor("#fcefdd"));
        getWindow().setStatusBarColor(Color.parseColor("#fcefdd"));

        Button bouton_Serv = findViewById(R.id.bouton_Serv);
        bouton_Serv.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ServeurConfigurationActivity.class);
            startActivity(intent);
        });

        Button boutonInfo = findViewById(R.id.bouton_Info);
        boutonInfo.setOnClickListener(v -> myThread.sendMessage("getValues()"));

        RecyclerView recyclerViewLeft = findViewById(R.id.recyclerView_left);
        RecyclerView recyclerViewRight = findViewById(R.id.recyclerView_right);

        // Charger l'ordre sauvegardé ou utiliser l'ordre par défaut
        List<String> leftItems = loadListOrder();

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
                saveListOrder(leftItems);
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
        Adapter adapterRight = new Adapter(rightItems);
        recyclerViewRight.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRight.setAdapter(adapterRight);
    }
}