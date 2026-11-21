package com.example.elderlysupport.medicine

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val expiryFormat = DateTimeFormatter.ofPattern("MM/yyyy", Locale.ITALY)

private fun createPhotoUri(context: Context): Uri {
    val directory = File(context.cacheDir, "medicine_photos").apply { mkdirs() }
    val photo = File.createTempFile("medicine_", ".jpg", directory)
    return FileProvider.getUriForFile(
        context, "${context.packageName}.medicine.fileprovider", photo
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScannerScreen(
    onMedicineConfirmed: (RecognizedMedicine) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var recognizedText by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var expiryInput by remember { mutableStateOf("") }
    var attempted by remember { mutableStateOf(false) }

    fun process(uri: Uri) {
        loading = true
        error = null
        attempted = false
        MedicineOcr.recognize(context, uri) { result ->
            loading = false
            result.fold(
                onSuccess = { proposal ->
                    recognizedText = proposal.originalOcrText
                    name = proposal.proposedName.orEmpty()
                    expiryInput = proposal.proposedExpiryMonth?.format(expiryFormat).orEmpty()
                    if (proposal.originalOcrText.isBlank()) {
                        error = "Nessun testo leggibile: riprova con una foto nitida."
                    }
                },
                onFailure = { failure ->
                    error = "Impossibile leggere la foto: ${failure.localizedMessage ?: "errore sconosciuto"}"
                }
            )
        }
    }

    val choosePhoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) process(uri)
    }
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) pendingPhotoUri?.let(::process)
        else error = "Foto non acquisita."
    }

    val parsedExpiry = remember(expiryInput) {
        if (expiryInput.matches(Regex("""\d{2}/\d{4}"""))) {
            MedicineParser.readMonth(expiryInput)
        } else null
    }
    val validName = name.trim().length >= 2

    Scaffold(topBar = { TopAppBar(title = { Text("Riconosci farmaco") }) }) { contentPadding ->
        Column(
            modifier = modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Fotografa il nome e la data di scadenza presenti sulla confezione.")
            Button(
                enabled = !loading,
                onClick = {
                    try {
                        val uri = createPhotoUri(context)
                        pendingPhotoUri = uri
                        takePicture.launch(uri)
                    } catch (exception: Exception) {
                        error = "Fotocamera non disponibile: ${exception.localizedMessage}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Scatta una foto") }

            OutlinedButton(
                enabled = !loading,
                onClick = { choosePhoto.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Scegli una foto dalla galleria") }

            if (loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Riconoscimento in corso…")
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (recognizedText.isNotBlank()) {
                Text("Controlla e correggi i dati", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome farmaco") },
                    singleLine = true,
                    isError = attempted && !validName,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expiryInput,
                    onValueChange = { expiryInput = it },
                    label = { Text("Scadenza (MM/AAAA)") },
                    placeholder = { Text("12/2027") },
                    singleLine = true,
                    isError = attempted && parsedExpiry == null,
                    modifier = Modifier.fillMaxWidth()
                )
                parsedExpiry?.let { expiry ->
                    val status = when (expiryStatus(expiry)) {
                        ExpiryStatus.EXPIRED -> "ATTENZIONE: farmaco scaduto"
                        ExpiryStatus.EXPIRING_SOON -> "Attenzione: scadenza vicina (entro 2 mesi)"
                        ExpiryStatus.VALID -> "Scadenza non imminente"
                    }
                    Text(status, fontWeight = FontWeight.SemiBold)
                }
                Text("La lettura automatica può sbagliare: verifica sempre la confezione.")
                Button(
                    onClick = {
                        attempted = true
                        val expiry = parsedExpiry
                        if (validName && expiry != null) {
                            onMedicineConfirmed(
                                RecognizedMedicine(name.trim(), expiry, recognizedText)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Conferma farmaco") }
                HorizontalDivider()
                Text("Testo originale letto (per verifica)", fontWeight = FontWeight.Bold)
                Text(recognizedText)
            }
        }
    }
}
