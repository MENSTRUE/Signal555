package com.wafa.signal555

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wafa.signal555.ui.Signal555App
import com.wafa.signal555.ui.theme.Signal555Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Signal555Theme {
                Signal555App()
            }
        }
    }
}
