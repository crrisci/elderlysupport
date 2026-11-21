package com.example.elderlysupport

import android.content.Intent
import androidx.compose.ui.res.stringResource
import com.example.elderlysupport.notifications.ReminderSettingsActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.elderlysupport.data.AppDatabase
import com.example.elderlysupport.data.Farmaco
import kotlinx.coroutines.launch

@Composable
fun SchermataProva() {
    val context = LocalContext.current
    val farmacoDao = remember { AppDatabase.getDatabase(context).farmacoDao() }
    val scope = rememberCoroutineScope()

    val listaFarmaci by remember { farmacoDao.getTutti() }.collectAsState(initial = emptyList())

    var nome by remember { mutableStateOf("") }
    var scadenza by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome del farmaco") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = scadenza,
                onValueChange = { scadenza = it },
                label = { Text("Scadenza (es. 2027-03)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                enabled = nome.isNotBlank(),
                onClick = {
                    val nuovoFarmaco = Farmaco(
                        nome = nome.trim(),
                        scadenza = scadenza.trim().ifBlank { null }
                    )
                    scope.launch {
                        farmacoDao.inserisci(nuovoFarmaco)
                        nome = ""
                        scadenza = ""
                    }
                }
            ) {
                Text("Salva farmaco")
            }

            TextButton(onClick = {
                context.startActivity(Intent(context, ReminderSettingsActivity::class.java))
            }) {
                Text(stringResource(R.string.reminder_settings))
            }

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = listaFarmaci,
                    key = { it.id }
                ) { farmaco ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val testoScadenza = farmaco.scadenza ?: "nessuna scadenza"
                        Text(text = "${farmaco.nome} - $testoScadenza")

                        TextButton(
                            onClick = {
                                scope.launch {
                                    farmacoDao.elimina(farmaco)
                                }
                            }
                        ) {
                            Text("Elimina")
                        }
                    }
                }
            }
        }
    }
}