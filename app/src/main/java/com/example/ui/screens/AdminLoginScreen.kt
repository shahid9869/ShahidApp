package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.KarobarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
  viewModel: KarobarViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val isUrdu by viewModel.isUrdu.collectAsState()
  val pinInput by viewModel.adminPinInput.collectAsState()
  val pinError by viewModel.adminPinError.collectAsState()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isUrdu) "ایڈمن پینل لاگ ان" else "Admin Portal Login",
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("admin_login_back")) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Admin Logo Badge
      Box(
        modifier = Modifier
          .size(80.dp)
          .background(EmeraldContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AdminPanelSettings,
          contentDescription = null,
          tint = EmeraldPrimary,
          modifier = Modifier.size(46.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isUrdu) "ایڈمن کنٹرول روم" else "Admin Control Center",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = if (isUrdu) {
          "ایپ کو کنٹرول کرنے اور اشتہارات کی تصدیق کے لیے 4 ہندسوں کا پن کوڈ درج کریں"
        } else {
          "Enter your 4-digit security PIN to moderate and manage ads"
        },
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // PIN Input Field
      OutlinedTextField(
        value = pinInput,
        onValueChange = { if (it.length <= 6) viewModel.adminPinInput.value = it },
        label = { Text(if (isUrdu) "ایڈمن پاس کوڈ" else "Admin PIN") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        leadingIcon = {
          Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = EmeraldPrimary)
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("admin_pin_input"),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = EmeraldPrimary,
          focusedLabelColor = EmeraldPrimary
        )
      )

      if (pinError != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = pinError ?: "",
          color = DangerRed,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          textAlign = TextAlign.Center
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Login Button
      Button(
        onClick = { viewModel.loginAdmin() },
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("admin_login_submit"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
      ) {
        Text(
          text = if (isUrdu) "لاگ ان کریں" else "Access Admin Panel",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Default Hint Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (isUrdu) "پہلے سے طے شدہ ایڈمن پن: 1234" else "Default Admin PIN: 1234",
            fontWeight = FontWeight.Bold,
            color = EmeraldOnContainer,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (isUrdu) "(ایڈمن پینل کے اندر سے آپ پن تبدیل بھی کر سکتے ہیں)" else "(You can change this PIN inside Admin Settings)",
            color = EmeraldOnContainer.copy(alpha = 0.8f),
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
