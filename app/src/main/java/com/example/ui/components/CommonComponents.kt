package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdStatus
import com.example.data.model.AdType
import com.example.ui.theme.CallBlue
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldOnContainer
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.PendingOrange
import com.example.ui.theme.WhatsAppGreen

object ActionHelpers {
  fun launchDialer(context: Context, phone: String) {
    try {
      val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${phone.trim()}")
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "ڈائلر کھولنے میں دشواری ہوئی", Toast.LENGTH_SHORT).show()
    }
  }

  fun launchWhatsApp(context: Context, whatsapp: String, message: String = "") {
    try {
      val cleanNumber = whatsapp.replace("+", "").replace("-", "").replace(" ", "").trim()
      val formatted = if (cleanNumber.startsWith("0")) "92" + cleanNumber.substring(1) else cleanNumber
      val url = "https://api.whatsapp.com/send?phone=$formatted&text=${Uri.encode(message)}"
      val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "واٹس ایپ کھولنے میں دشواری ہوئی", Toast.LENGTH_SHORT).show()
    }
  }

  fun launchShare(context: Context, title: String, text: String) {
    try {
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$text\n\n(اشتہار بذریعہ کاروبار و سروسز ایپ)")
      }
      context.startActivity(Intent.createChooser(intent, "اشتہار شیئر کریں"))
    } catch (_: Exception) {}
  }
}

@Composable
fun StatusBadge(status: String, isUrdu: Boolean = true) {
  val (label, bg, fg, icon) = when (status) {
    AdStatus.APPROVED.name -> Quadruple(
      if (isUrdu) "منظور شدہ" else "Approved",
      Color(0xFFE8F5E9),
      Color(0xFF2E7D32),
      Icons.Default.CheckCircle
    )
    AdStatus.PENDING.name -> Quadruple(
      if (isUrdu) "زیر جائزہ" else "Pending Review",
      Color(0xFFFFF3E0),
      PendingOrange,
      Icons.Default.HourglassTop
    )
    else -> Quadruple(
      if (isUrdu) "مسترد شدہ" else "Rejected",
      Color(0xFFFFEBEE),
      DangerRed,
      Icons.Outlined.Cancel
    )
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bg,
    modifier = Modifier.padding(2.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = fg,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun TypeBadge(type: String, isUrdu: Boolean = true) {
  val isBusiness = type == AdType.BUSINESS.name
  val label = if (isBusiness) {
    if (isUrdu) "کاروبار" else "Business"
  } else {
    if (isUrdu) "سروس" else "Service"
  }
  val bg = if (isBusiness) EmeraldContainer else Color(0xFFE3F2FD)
  val fg = if (isBusiness) EmeraldOnContainer else Color(0xFF0D47A1)
  val icon = if (isBusiness) Icons.Default.Business else Icons.Default.Handyman

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = bg
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = fg,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun FeaturedBadge(isUrdu: Boolean = true) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = GoldContainer
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = GoldOnContainer,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = if (isUrdu) "نمایاں" else "Featured",
        color = GoldOnContainer,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
