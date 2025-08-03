package com.learn.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learn.jettipapp.component.InputField
import com.learn.jettipapp.ui.theme.JetTipAppTheme
import com.learn.jettipapp.util.calculateTotalPerPerson
import com.learn.jettipapp.util.calculateTotalTip
import com.learn.jettipapp.widget.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.headlineMedium
                )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

//@Preview
@Composable
fun MainContent() {
    BillForm() { billAmt ->
        Log.d("AMT", "MainContent: ${billAmt.toInt()}")
    }

}

@Preview
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
) {
    // State untuk menyimpan input dari TextField (sumber kebenaran utama)
    val totalBillsState = remember {
        mutableStateOf("")
    }
    // State untuk validasi, bergantung pada totalBillsState
    val validState = remember(totalBillsState.value) {
        totalBillsState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    // State untuk posisi slider (0f hingga 1f)
    val slidePositionState = remember {
        mutableFloatStateOf(0f)
    }

    // State untuk jumlah orang
    val splitByState = remember {
        mutableIntStateOf(1)
    }

    // Compose akan otomatis memperbarui UI setiap kali salah satu state sumber (seperti totalBillsState) berubah.
    // `toDoubleOrNull()` digunakan untuk mencegah crash jika input tidak valid.
    val totalBill = totalBillsState.value.toDoubleOrNull() ?: 0.0
    val tipPercentage = (slidePositionState.floatValue * 100).toInt()

    // Hitung jumlah tip. Tidak perlu `remember` karena ini adalah kalkulasi cepat.
    val tipAmount = calculateTotalTip(totalBill = totalBill, tipPercentage = tipPercentage)

    // Hitung total per orang. Ini juga akan selalu ter-update secara otomatis.
    val totalPerPerson = calculateTotalPerPerson(
        totalBill = totalBill,
        splitBy = splitByState.intValue,
        tipPercentage = tipPercentage
    )

    Column(
        modifier.padding(vertical = 12.dp)
    ) {
        // TopHeader langsung menggunakan nilai `totalPerPerson` yang sudah dihitung
        TopHeader(totalPerPerson = totalPerPerson)

        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    valueState = totalBillsState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillsState.value.trim())

                        keyboardController?.hide()
                    }
                )
                if (validState) {
                    Column(
                        modifier.padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Split",
                                modifier.align(alignment = Alignment.CenterVertically)
                            )
                            Row(
                                horizontalArrangement = Arrangement.End
                            ) {
                                RoundIconButton(
                                    imageVector = Icons.Default.Remove,
                                    onClick = {
                                        // Kalkulasi `totalPerPerson` akan diperbarui secara otomatis.
                                        splitByState.intValue =
                                            if (splitByState.intValue > 1) {
                                                splitByState.intValue - 1
                                            } else 1
                                    }
                                )

                                Text(
                                    text = "${splitByState.intValue}",
                                    modifier.align(Alignment.CenterVertically).padding(start = 9.dp, end = 9.dp)
                                )

                                RoundIconButton(
                                    imageVector = Icons.Default.Add,
                                    onClick = {
                                        if (splitByState.intValue < 100) { // Menggunakan 100 sebagai batas atas
                                            splitByState.intValue++
                                        }
                                    }
                                )
                            }
                        }

                        // Tip Row
                        Row(
                            modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Tip", modifier.align(alignment = Alignment.CenterVertically))
                            // Nilai `tipAmount` yang dihitung langsung.
                            Text(text = "$ ${String.format("%.2f", tipAmount)}", modifier.align(alignment = Alignment.CenterVertically))
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "$tipPercentage %")

                            // Slider
                            Slider(
                                value = slidePositionState.floatValue,
                                onValueChange = { newVal ->
                                    // Cukup perbarui posisi slider.
                                    // `tipAmount` dan `totalPerPerson` akan dihitung ulang secara otomatis.
                                    slidePositionState.floatValue = newVal
                                }
                            )
                        }
                    }
                } else {
                    Box(

                    ) {

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    MyApp {
        MainContent()
    }
}