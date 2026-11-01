package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MessageRole
import com.example.data.model.MessageType
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.util.ImageHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageBubble(
  message: ChatMessageEntity,
  isMyMessage: Boolean,
  isPlayingAudio: Boolean,
  audioProgressSec: Int,
  onToggleAudio: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showFullImage by remember { mutableStateOf(false) }

  val alignment = if (isMyMessage) Alignment.End else Alignment.Start
  val bubbleBg = if (isMyMessage) EmeraldPrimary else Color(0xFFF3F4F6)
  val textColor = if (isMyMessage) Color.White else TextDark
  val subTextColor = if (isMyMessage) Color.White.copy(alpha = 0.75f) else TextMuted

  val bubbleShape = if (isMyMessage) {
    RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
  } else {
    RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
  }

  val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
  val formattedTime = timeFormat.format(Date(message.timestamp))

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp),
    horizontalAlignment = alignment
  ) {
    // Sender label
    Text(
      text = message.senderName,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (message.senderRole == "ADMIN") EmeraldPrimary else TextMuted,
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )

    Surface(
      shape = bubbleShape,
      color = bubbleBg,
      modifier = Modifier.widthIn(max = 290.dp)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        when (message.messageType) {
          MessageType.TEXT.name -> {
            Text(
              text = message.textContent,
              color = textColor,
              fontSize = 14.sp,
              lineHeight = 20.sp
            )
          }

          MessageType.IMAGE.name -> {
            val bitmap = remember(message.mediaPath) {
              ImageHelper.loadBitmap(message.mediaPath)
            }

            if (bitmap != null) {
              Image(
                bitmap = bitmap,
                contentDescription = "Photo",
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { showFullImage = true },
                contentScale = ContentScale.Crop
              )
            } else {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(100.dp)
                  .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "🖼️ [تصویر / Photo]", color = textColor, fontSize = 12.sp)
              }
            }

            if (message.textContent.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = message.textContent,
                color = textColor,
                fontSize = 13.sp
              )
            }
          }

          MessageType.AUDIO.name -> {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(vertical = 4.dp)
            ) {
              IconButton(
                onClick = onToggleAudio,
                modifier = Modifier
                  .size(42.dp)
                  .background(
                    if (isMyMessage) Color.White.copy(alpha = 0.25f) else EmeraldContainer,
                    CircleShape
                  )
              ) {
                Icon(
                  imageVector = if (isPlayingAudio) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlayingAudio) "Pause" else "Play",
                  tint = if (isMyMessage) Color.White else EmeraldOnContainer,
                  modifier = Modifier.size(24.dp)
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Audiotrack,
                      contentDescription = null,
                      tint = subTextColor,
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                      text = "وائس میسج",
                      fontSize = 11.sp,
                      color = subTextColor,
                      fontWeight = FontWeight.Medium
                    )
                  }
                  Text(
                    text = if (isPlayingAudio) {
                      String.format(Locale.US, "%d:%02d", audioProgressSec / 60, audioProgressSec % 60)
                    } else {
                      String.format(Locale.US, "%d:%02d", message.audioDurationSec / 60, message.audioDurationSec % 60)
                    },
                    fontSize = 11.sp,
                    color = subTextColor,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val progress = if (message.audioDurationSec > 0 && isPlayingAudio) {
                  (audioProgressSec.toFloat() / message.audioDurationSec.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                  progress = { progress },
                  modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                  color = if (isMyMessage) Color.White else EmeraldPrimary,
                  trackColor = if (isMyMessage) Color.White.copy(alpha = 0.3f) else Color(0xFFD1D5DB)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Timestamp & Read ticks
        Row(
          modifier = Modifier.align(Alignment.End),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = formattedTime,
            fontSize = 10.sp,
            color = subTextColor
          )
          if (isMyMessage) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = null,
              tint = if (message.isRead) Color(0xFF80D8FF) else subTextColor,
              modifier = Modifier.size(12.dp)
            )
          }
        }
      }
    }
  }

  // Full image viewer dialog
  if (showFullImage) {
    Dialog(onDismissRequest = { showFullImage = false }) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.Black.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
          .padding(16.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          val fullBmp = remember(message.mediaPath) { ImageHelper.loadBitmap(message.mediaPath, maxDim = 1600) }
          if (fullBmp != null) {
            Image(
              bitmap = fullBmp,
              contentDescription = "Full photo",
              modifier = Modifier.fillMaxWidth().height(320.dp),
              contentScale = ContentScale.Fit
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
          IconButton(
            onClick = { showFullImage = false },
            modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }
      }
    }
  }
}

@Composable
fun ChatInputBar(
  inputText: String,
  onInputTextChange: (String) -> Unit,
  onSendText: () -> Unit,
  onImagePicked: (String) -> Unit,
  isRecording: Boolean,
  recordingDurationSec: Int,
  onStartRecord: () -> Unit,
  onStopAndSendRecord: () -> Unit,
  onCancelRecord: () -> Unit,
  isUrdu: Boolean = true,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      val savedPath = ImageHelper.saveUriToInternalStorage(context, uri)
      if (savedPath != null) {
        onImagePicked(savedPath)
      } else {
        Toast.makeText(context, "تصویر محفوظ نہ ہو سکی", Toast.LENGTH_SHORT).show()
      }
    }
  }

  val audioPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      onStartRecord()
    } else {
      Toast.makeText(context, "وائس میسج کے لیے مائیک کی اجازت درکار ہے", Toast.LENGTH_LONG).show()
    }
  }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shadowElevation = 8.dp,
    color = MaterialTheme.colorScheme.surface
  ) {
    if (isRecording) {
      // Audio Recording Active View
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(12.dp)
              .background(Color.Red, CircleShape)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "ریکارڈنگ: ${String.format(Locale.US, "%d:%02d", recordingDurationSec / 60, recordingDurationSec % 60)}",
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            fontSize = 13.sp
          )
        }

        Row {
          // Cancel recording
          IconButton(
            onClick = onCancelRecord,
            modifier = Modifier.size(40.dp)
          ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Send voice note
          IconButton(
            onClick = onStopAndSendRecord,
            modifier = Modifier
              .size(40.dp)
              .background(EmeraldPrimary, CircleShape)
          ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
          }
        }
      }
    } else {
      // Normal Input Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Image Picker Button
        IconButton(
          onClick = {
            photoPickerLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          modifier = Modifier.size(40.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "تصویر بھیجیں",
            tint = EmeraldPrimary
          )
        }

        // Text input field
        OutlinedTextField(
          value = inputText,
          onValueChange = onInputTextChange,
          placeholder = {
            Text(
              text = if (isUrdu) "میسج لکھیں..." else "Type a message...",
              fontSize = 13.sp,
              color = TextMuted
            )
          },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("chat_input_text"),
          shape = RoundedCornerShape(22.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldPrimary,
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color(0xFFF9FAFB),
            unfocusedContainerColor = Color(0xFFF9FAFB)
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.width(6.dp))

        if (inputText.isNotBlank()) {
          // Send Text Button
          IconButton(
            onClick = onSendText,
            modifier = Modifier
              .size(44.dp)
              .background(EmeraldPrimary, CircleShape)
              .testTag("send_chat_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        } else {
          // Voice Mic Button
          IconButton(
            onClick = {
              val permission = Manifest.permission.RECORD_AUDIO
              if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                onStartRecord()
              } else {
                audioPermissionLauncher.launch(permission)
              }
            },
            modifier = Modifier
              .size(44.dp)
              .background(EmeraldContainer, CircleShape)
              .testTag("voice_record_button")
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "آڈیو میسج ریکارڈ کریں",
              tint = EmeraldOnContainer,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }
    }
  }
}
