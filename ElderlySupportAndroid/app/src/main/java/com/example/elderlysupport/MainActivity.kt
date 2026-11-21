package com.example.elderlysupport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.elderlysupport.medicine.MedicineScannerScreen
import com.example.elderlysupport.medicine.RecognizedMedicine
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                var scanning by remember { mutableStateOf(false) }
                var confirmed by remember { mutableStateOf<RecognizedMedicine?>(null) }

                when {
                    scanning -> MedicineScannerScreen(
                        onMedicineConfirmed = { medicine ->
                            confirmed = medicine
                            scanning = false
                        }
                    )
                    else -> Scaffold { padding ->
                        Column(
                            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            Text("Elderly Support", style = MaterialTheme.typography.headlineMedium)
                            Text("Riconosci il nome e la scadenza di un farmaco dalla confezione.")
                            Button(onClick = { scanning = true }, modifier = Modifier.fillMaxWidth()) {
                                Text("Scansiona un farmaco")
                            }
                            confirmed?.let { medicine ->
                                HorizontalDivider()
                                Text("Ultimo farmaco confermato", style = MaterialTheme.typography.titleMedium)
                                Text("Nome: ${medicine.name}")
                                Text("Scadenza: ${medicine.expiryMonth.format(DateTimeFormatter.ofPattern("MM/yyyy"))}")
                                Text("Dati ancora da salvare nel database del team.")
                            }
                        }
                    }
                }
            }
        }
    }
}
