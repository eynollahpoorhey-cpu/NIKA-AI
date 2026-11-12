package com.nika.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NikaApp() }
    }
}

private data class ChatLine(val text: String, val fromUser: Boolean)

@Composable
private fun NikaApp() {
    val messages = remember {
        mutableStateListOf(ChatLine("سلام! من نیکا هستم 💙\nنسخه‌ی آزمایشی من آماده‌ی شروعه.", false))
    }
    var draft by remember { mutableStateOf("") }
    val navy = Color(0xFF142B55)
    val blue = Color(0xFF5B9DDF)

    MaterialTheme {
        Surface(Modifier.fillMaxSize(), color = Color(0xFFF3F7FF)) {
            Column(
                Modifier.fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFFE8F2FF), Color(0xFFF8FAFF))))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).background(blue, CircleShape), contentAlignment = Alignment.Center) {
                        Text("N", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("NIKA AI", color = navy, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        Text("دستیار فارسی • نسخه آزمایشی", color = Color(0xFF58708F), fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { line ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = if (line.fromUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                color = if (line.fromUser) Color(0xFFDCEBFF) else Color.White,
                                shape = RoundedCornerShape(18.dp),
                                shadowElevation = 1.dp,
                                modifier = Modifier.fillMaxWidth(0.88f)
                            ) {
                                Text(line.text, Modifier.padding(14.dp), color = navy, fontSize = 15.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = draft, onValueChange = { draft = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("پیامت رو برای نیکا بنویس…") },
                    shape = RoundedCornerShape(18.dp), maxLines = 4
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val message = draft.trim()
                        if (message.isNotEmpty()) {
                            messages.add(ChatLine(message, true))
                            messages.add(ChatLine("پیامت دریافت شد 💙\nدر این نسخه هنوز موتور هوش مصنوعی به برنامه وصل نشده است.", false))
                            draft = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = navy),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("ارسال پیام") }
                Spacer(Modifier.height(4.dp))
                Text(
                    "این نسخه پوسته‌ی اولیه است؛ پاسخ هوشمند، صدا و ساخت تصویر هنوز فعال نیستند.",
                    color = Color(0xFF6B7890), fontSize = 11.sp
                )
            }
        }
    }
}
