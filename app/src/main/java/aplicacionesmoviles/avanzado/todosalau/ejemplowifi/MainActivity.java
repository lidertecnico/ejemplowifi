package aplicacionesmoviles.avanzado.todosalau.ejemplowifi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import android.os.Bundle;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WifiManager wifiManager;

    private Button scanButton;

    // Declarar ListView
    private ListView listViewRedes;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar WifiManager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        // Inicializar botón de escaneo
        scanButton = findViewById(R.id.scanButton);

        // Inicializar ListView
        listViewRedes = findViewById(R.id.listViewRedes);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listViewRedes.setAdapter(adapter);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Solicitar permisos si es necesario
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }

                // Iniciar escaneo
                scanWifi();
            }
        });

        // Registrar receptor de escaneo
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void scanWifi() {
        // Mostrar mensaje de escaneo
        Toast.makeText(this, "Escaneando redes Wi-Fi...", Toast.LENGTH_SHORT).show();

        // Iniciar escaneo
        boolean success = wifiManager.startScan();
        if (!success) {
            // Mostrar mensaje de error
            Toast.makeText(this, "Error al iniciar el escaneo", Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                List<ScanResult> results = wifiManager.getScanResults();
                List<String> redes = new ArrayList<>();
                for (ScanResult result : results) {
                    redes.add(result.SSID + " - Intensidad: " + result.level + "dBm" + " - BSSID: " + result.BSSID);
                }
                adapter.clear();
                adapter.addAll(redes);
            } else {
                // Mostrar mensaje de error
                Toast.makeText(context, "Error al obtener resultados del escaneo", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(wifiScanReceiver);
    }
}
