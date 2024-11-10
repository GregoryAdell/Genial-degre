package fr.cpe.genial_degre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    // Variables pour les valeurs des mesures avec des valeurs par défaut
    private String Temp = "22°C"; // Température par défaut
    private String Lx = "300 lx"; // Luminosité par défaut
    private String Hum = "45%"; // Humidité par défaut
    private String Press = "1013 hPa"; // Pression par défaut

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
            return gson.fromJson(json, new TypeToken<List<String>>() {
            }.getType());
        } else {
            List<String> defaultList = new ArrayList<>();
            Collections.addAll(defaultList, "Lux", "Température", "Humidité", "Pression");
            saveListOrder(defaultList);
            return defaultList;
        }
    }

    private List<String> getRightItems(List<String> leftItems) {
        List<String> rightItems = new ArrayList<>();
        for (String item : leftItems) {
            switch (item) {
                case "Température":
                    rightItems.add(Temp);
                    break;
                case "Lux":
                    rightItems.add(Lx);
                    break;
                case "Humidité":
                    rightItems.add(Hum);
                    break;
                case "Pression":
                    rightItems.add(Press);
                    break;
                default:
                    rightItems.add("N/A");
                    break;
            }
        }
        return rightItems;
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

        List<String> leftItems = loadListOrder();
        List<String> rightItems = getRightItems(leftItems); // Obtenir les valeurs correspondantes

        Adapter adapterLeft = new Adapter(leftItems);
        Adapter adapterRight = new Adapter(rightItems);
        recyclerViewLeft.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLeft.setAdapter(adapterLeft);
        recyclerViewRight.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRight.setAdapter(adapterRight);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                // Swap items in both lists to keep the order synchronized
                Collections.swap(leftItems, fromPosition, toPosition);
                Collections.swap(rightItems, fromPosition, toPosition);

                adapterLeft.notifyItemMoved(fromPosition, toPosition);
                adapterRight.notifyItemMoved(fromPosition, toPosition);
                saveListOrder(leftItems);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Non utilisé
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewLeft);

        TextView infoServIP = findViewById(R.id.InfoServIP);
        TextView infoServPort = findViewById(R.id.InfoServPort);
        infoServIP.setText("- IP : " + ServeurConfigurationActivity.IP);
        infoServPort.setText("- Port : " + ServeurConfigurationActivity.PORT);
    }
}
