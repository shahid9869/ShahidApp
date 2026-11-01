package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MessageRole
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ChatMessageBubble
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextDark
import com.example.ui.viewmodel.KarobarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerChatScreen(
  viewModel: KarobarViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val context = LocalContext.current
  val isUrdu by viewModel.isUrdu.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val inputText by viewModel.customerChatInputText.collectAsState()
  val isRecording by viewModel.isRecordingVoice.collectAsState()
  val recordingDuration by viewModel.recordingDurationSec.collectAsState()
  val playingPath by viewModel.currentPlayingAudioPath.collectAsState()
  val audioProgress by viewModel.currentAudioProgressSec.collectAsState()

  val listState = rememberLazyListState()

  LaunchedEffect(chatMessages.size) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(chatMessages.size - 1)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .background(EmeraldContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (isUrdu) "ایڈمن سپورٹ ہیلپ ڈیسک" else "Admin Support Desk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF2E7D32), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isUrdu) "آن لائن • فوری جواب" else "Online • Quick Reply",
                  fontSize = 11.sp,
                  color = Color(0xFF2E7D32),
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("chat_back_button")) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      ChatInputBar(
        inputText = inputText,
        onInputTextChange = { viewModel.customerChatInputText.value = it },
        onSendText = { viewModel.sendTextMessage(MessageRole.USER, inputText) },
        onImagePicked = { path -> viewModel.sendImageMessage(MessageRole.USER, path) },
        isRecording = isRecording,
        recordingDurationSec = recordingDuration,
        onStartRecord = { viewModel.startVoiceRecording(context) },
        onStopAndSendRecord = { viewModel.stopAndSendVoiceRecording(MessageRole.USER) },
        onCancelRecord = { viewModel.cancelVoiceRecording() },
        isUrdu = isUrdu
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
      state = listState,
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      items(chatMessages, key = { it.id }) { message ->
        val isMyMessage = message.senderRole == MessageRole.USER.name
        val isThisAudioPlaying = playingPath == message.mediaPath

        ChatMessageBubble(
          message = message,
          isMyMessage = isMyMessage,
          isPlayingAudio = isThisAudioPlaying,
          audioProgressSec = audioProgress,
          onToggleAudio = { viewModel.toggleAudioPlayback(message.mediaPath) }
        )
      }
    }
  }
}
