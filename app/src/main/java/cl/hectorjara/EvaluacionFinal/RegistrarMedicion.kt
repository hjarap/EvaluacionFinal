    package cl.hectorjara.EvaluacionFinal

    import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.hectorjara.EvaluacionFinal.BD.Reading
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

    // Pantalla para el formulario de registro de mediciones
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ReadingFormScreen(
        navController: NavController,
        viewModel: ReadingViewModel = viewModel(),
        errorMessageResId: Int // Cambia el parámetro a un ID de recurso de cadena
    ) {
        // Obtiene el mensaje de error utilizando stringResource
        val errorMessage = stringResource(id = errorMessageResId)
        // Opciones de tipo de mediciones
        val typeOptions = listOf("Agua", "Luz", "Gas")

        // Estado para el tipo de medición seleccionado
        var selectedType by remember { mutableStateOf(typeOptions.first()) }

        // Estado para el valor de la medición
        var value by remember { mutableStateOf("") }

        // Fecha actual formateada
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Estado para el manejo de Snackbar
        val snackbarHostState = remember { SnackbarHostState() }

        // Scope de la corrutina
        val coroutineScope = rememberCoroutineScope()

        // Diseño de Scaffold para la pantalla
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(id = R.string.title_register_measurement)) })
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {

                // Sección para seleccionar el tipo de medición
                Text(text = stringResource(id = R.string.label_type), style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    typeOptions.forEach { label ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {

                            // RadioButton para cada opción de tipo
                            RadioButton(
                                selected = selectedType == label,
                                onClick = { selectedType = label }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Campo de texto para ingresar el valor de la medición
                TextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(stringResource(id = R.string.label_value_CLP)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo de texto para mostrar la fecha actual (no editable)
                TextField(
                    value = currentDate,
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.label_date)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Botón para guardar la medición
                Button(onClick = {
                    if (selectedType.isNotBlank() && value.isNotBlank()) {
                        val valueInCLP = try {

                            // Intenta convertir el valor a Double
                            NumberFormat.getNumberInstance(Locale("es", "CL")).parse(value)?.toDouble() ?: 0.0
                        } catch (e: Exception) {
                            0.0
                        }
                        if (valueInCLP > 0) {

                            // Guarda la medición en el ViewModel
                            viewModel.saveReading(
                                Reading(type = selectedType, value = valueInCLP, date = currentDate)
                            )

                            // Navega de regreso a la lista de mediciones
                            navController.popBackStack()
                        } else {
                            coroutineScope.launch {

                                // Muestra un Snackbar si el valor ingresado no es válido
                                snackbarHostState.showSnackbar(errorMessage)
                            }
                        }
                    } else {
                        coroutineScope.launch {

                            // Muestra un Snackbar si algún campo está vacío
                            snackbarHostState.showSnackbar(errorMessage)
                        }
                    }
                }) {
                    Text(text = stringResource(id = R.string.button_save))
                }
            }
        }
    }
