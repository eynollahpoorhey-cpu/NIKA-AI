package com.nika.ai

import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NikaApp() }
    }
}

private data class ChatLine(val text: String, val fromUser: Boolean)

private val DeepBlue = Color(0xFF172B59)
private val SkyBlue = Color(0xFF68A9F2)
private val PaleBlue = Color(0xFFEAF3FF)
private val Ink = Color(0xFF233657)

@Composable
private fun NikaApp() {
    val messages = remember {
        mutableStateListOf(
            ChatLine("سلام! من نیکا هستم 💙\nخوش اومدی! فعلاً در نسخه‌ی اولیه‌ام و هنوز به موتور هوش مصنوعی وصل نیستم.", false)
        )
    }
    var draft by remember { mutableStateOf("") }
    var speechRate by remember { mutableFloatStateOf(1.0f) }
    val context = LocalContext.current
    var speechEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                speechEngine?.language = Locale.forLanguageTag("fa-IR")
            }
        }
        speechEngine = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            speechEngine = null
        }
    }

    fun sendMessage(value: String) {
        val message = value.trim()
        if (message.isNotEmpty()) {
            messages.add(ChatLine(message, true))
            messages.add(
                ChatLine(demoReply(message), false)
            )
            draft = ""
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF5F8FF)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFE6F1FF), Color(0xFFF7F9FF), Color(0xFFF1F5FF))
                        )
                    )
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Header()
                Spacer(Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("گفت‌وگو", color = DeepBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Surface(color = Color(0xFFDDEBFF), shape = RoundedCornerShape(30.dp)) {
                        Text("نسخه‌ی آزمایشی", color = Color(0xFF416A9C), fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(messages) { line ->
                        MessageBubble(line) {
                            speechEngine?.setSpeechRate(speechRate)
                            speechEngine?.speak(line.text, TextToSpeech.QUEUE_FLUSH, null, "nika_reply")
                        }
                    }
                }

                if (messages.size == 1) {
                    Text("می‌تونی با یکی از این پیام‌ها شروع کنی:", color = Color(0xFF71809A), fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip("سلام نیکا", Modifier.weight(1f)) { sendMessage("سلام نیکا") }
                        SuggestionChip("خودت رو معرفی کن", Modifier.weight(1f)) { sendMessage("خودت رو معرفی کن") }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(22.dp),
                    shadowElevation = 5.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("پیامت رو برای نیکا بنویس…", fontSize = 14.sp) },
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyBlue,
                                unfocusedBorderColor = Color(0xFFE2EAF6)
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { sendMessage(draft) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBlue),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text("ارسال پیام", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("➤", fontSize = 17.sp)
                        }
                    }
                }
                Spacer(Modifier.height(9.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(horizontal = 13.dp, vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("سرعت صدای نیکا", color = DeepBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.weight(1f))
                            Text(
                                when {
                                    speechRate < 0.95f -> "آهسته"
                                    speechRate > 1.05f -> "تند"
                                    else -> "معمولی"
                                },
                                color = Color(0xFF71809A),
                                fontSize = 11.sp
                            )
                        }
                        Slider(
                            value = speechRate,
                            onValueChange = { speechRate = it },
                            valueRange = 0.75f..1.35f,
                            steps = 5,
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
                Spacer(Modifier.height(9.dp))
                Text(
                    "پخش صدا به پشتیبانی موتور گفتار فارسی گوشی بستگی دارد. هوش مصنوعی و ساخت تصویر هنوز فعال نشده‌اند.",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF7C8AA2),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun Header() {
    Surface(
        color = Color.White.copy(alpha = 0.94f),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF8CC8FF), Color(0xFF477DD0))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("N", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text("NIKA AI", color = DeepBlue, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text("دستیار هوشمند فارسی", color = Color(0xFF71809A), fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(7.dp).background(Color(0xFFEDB95C), CircleShape))
                    Spacer(Modifier.width(5.dp))
                    Text("در حال توسعه", color = Color(0xFF8A6A32), fontSize = 10.sp)
                }
                Text("v0.2", color = Color(0xFF9AA7BA), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun MessageBubble(line: ChatLine, onSpeak: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (line.fromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!line.fromUser) {
            Box(
                Modifier.size(27.dp).background(
                    Brush.linearGradient(listOf(Color(0xFF8CC8FF), Color(0xFF477DD0))),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Text("N", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(7.dp))
        }
        Surface(
            color = if (line.fromUser) Color(0xFFDCEBFF) else Color.White,
            shape = RoundedCornerShape(
                topStart = 19.dp, topEnd = 19.dp,
                bottomStart = if (line.fromUser) 19.dp else 5.dp,
                bottomEnd = if (line.fromUser) 5.dp else 19.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    line.text,
                    color = Ink,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                if (!line.fromUser) {
                    TextButton(onClick = onSpeak, contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp)) {
                        Text("🔊 پخش پاسخ", color = Color(0xFF477DD0), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCE8FA))
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            color = Color(0xFF42699C), fontSize = 11.sp, textAlign = TextAlign.Center)
    }
}


private fun demoReply(message: String): String {
    val text = message.trim().lowercase()
    return when {
        text.contains("سلام") || text == "درود" ->
            "سلام! 💙\\nخوشحالم که اومدی. دوست داری درباره‌ی چی گپ بزنیم؟"
        text.contains("خودت") || text.contains("معرفی") ->
            "من نیکا هستم؛ یک دستیار فارسی در حال توسعه با ظاهر آبی و سفید. 🌸\\nفعلاً نسخه‌ی نمایشی‌ام و هنوز هوش مصنوعی واقعی، صدا و ساخت تصویر به من وصل نشده."
        text.contains("چه کار") || text.contains("قابلیت") || text.contains("کمک") ->
            "هدفم اینه که بتونم به پرسش‌ها جواب بدم، گفت‌وگوی صوتی داشته باشم و در ساخت تصویر کمک کنم. ✨\\nاین قابلیت‌ها هنوز در حال ساخته شدن هستند."
        text.contains("ممنون") || text.contains("مرسی") ->
            "خواهش می‌کنم! 💙"
        text.contains("اسمت") || text.contains("نامت") ->
            "اسمم نیکاست! 💙"
        else ->
            "پیامت رو دریافت کردم 💙\\nاین نسخه هنوز پاسخ‌گوی هوشمند نیست. برای جواب واقعی باید سرویس هوش مصنوعی از طریق یک اتصال امن به برنامه وصل بشه."
    }
}
