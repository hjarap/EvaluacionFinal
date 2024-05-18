package cl.hectorjara.EvaluacionFinal

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.hectorjara.EvaluacionFinal.BD.Reading
import cl.hectorjara.EvaluacionFinal.BD.ReadingDao
import cl.hectorjara.EvaluacionFinal.BD.ReadingDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}

// ReadingItem es una función composable que muestra un ítem de lectura en una tarjeta
@Composable
fun ReadingItem(reading: Reading) {

    // Determina el recurso de icono apropiado basado en el tipo de lectura
    val iconRes = when (reading.type) {
        "Agua" -> R.drawable.water
        "Luz" -> R.drawable.lightbulb
        "Gas" -> R.drawable.gas
        else -> R.drawable.otro // Un icono por defecto para tipos desconocidos
    }
    // Tarjeta para mostrar los detalles de la lectura
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = reading.type,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Muestra los detalles de la lectura
            Column {
                Text("Tipo: ${reading.type}", style = MaterialTheme.typography.titleSmall)
                Text("Valor: ${reading.value}", style = MaterialTheme.typography.bodyMedium)
                Text("Fecha: ${reading.date}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ReadingListScreen es una función composable que muestra la lista de lecturas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingListScreen(navController: NavController, viewModel: ReadingViewModel = viewModel()) {
    val readings by viewModel.allReadings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mediciones Registradas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("form") }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(readings) { reading ->
                ReadingItem(reading)
            }
        }
    }
}

// ReadingViewModel es un ViewModel que maneja los datos relacionados con la UI para la pantalla de lista de lecturas
class ReadingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReadingRepository

    // MutableStateFlow para mantener la lista de lecturas
    private val _allReadings = MutableStateFlow<List<Reading>>(emptyList())
    val allReadings: StateFlow<List<Reading>> = _allReadings.asStateFlow()

    init {
        // Inicializa el repositorio
        val readingDao = ReadingDatabase.getDatabase(application).readingDao()
        repository = ReadingRepository(readingDao)

        // Lanza una corrutina para recoger las lecturas del repositorio
        viewModelScope.launch {
            repository.allReadings.collect { readings ->
                _allReadings.value = readings
            }
        }
    }

    // Función para guardar una lectura
    fun saveReading(reading: Reading) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(reading)
    }
}

// ReadingRepository proporciona una API limpia para el acceso a datos
class ReadingRepository(private val readingDao: ReadingDao) {

    // Flow de todas las lecturas
    val allReadings: Flow<List<Reading>> = readingDao.getAllReadings()

    // Función suspendida para insertar una lectura
    suspend fun insert(reading: Reading) {
        readingDao.insert(reading)
    }
}

// AppNavHost es una función composable que configura la navegación en la aplicación
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "list") {
        composable("list") {
            ReadingListScreen(navController)
        }
        composable("form") {
            ReadingFormScreen(navController = navController, viewModel = viewModel(), errorMessageResId = R.string.error_invalid_value)
        }
    }
}

