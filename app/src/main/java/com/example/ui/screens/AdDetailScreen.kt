package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.example.ui.components.ActionHelpers
import com.example.ui.components.FeaturedBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TypeBadge
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CallBlue
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.KarobarViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdDetailScreen(
  adId: Long,
  viewModel: KarobarViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val context = LocalContext.current
  val isUrdu by viewModel.isUrdu.collectAsState()
  val allAds by viewModel.allAds.collectAsState()
  val bookmarkedIds by viewModel.bookmarkedAdIds.collectAsState()

  val ad = allAds.find { it.id == adId }

  LaunchedEffect(adId) {
    viewModel.recordAdView(adId)
  }

  if (ad == null) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(if (isUrdu) "اشتہار نہیں ملا" else "Ad Not Found") },
          navigationIcon = {
            IconButton(onClick = onBackClick) {
              Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        )
      }
    ) { padding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = Alignment.Center
      ) {
        Text(text = if (isUrdu) "یہ اشتہار موجود نہیں یا حذف ہو چکا ہے" else "This ad does not exist or was deleted")
      }
    }
    return
  }

  val isBookmarked = bookmarkedIds.contains(ad.id)

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isUrdu) "اشتہار کی تفصیل" else "Ad Details",
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("ad_detail_back")) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "واپس")
          }
        },
        actions = {
          IconButton(
            onClick = {
              ActionHelpers.launchShare(
                context,
                ad.title,
                "${ad.description}\nقیمت: ${ad.priceOrRate}\nرابطہ: ${ad.phone} | شہر: ${ad.city}"
              )
            }
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
          }
          IconButton(onClick = { viewModel.toggleBookmark(ad.id) }) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (isBookmarked) EmeraldPrimary else TextDark
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = { ActionHelpers.launchDialer(context, ad.phone) },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_call_button"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, CallBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CallBlue)
          ) {
            Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (isUrdu) "کال کریں" else "Call", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val preMsg = if (isUrdu) {
                "السلام علیکم! میں نے کاروبار ایپ پر آپ کا اشتہار دیکھا: '${ad.title}'۔ اس کے بارے میں مزید معلومات چاہیے۔"
              } else {
                "Hi, I saw your ad on Karobar Ads: '${ad.title}'. Please provide details."
              }
              ActionHelpers.launchWhatsApp(context, ad.whatsapp, preMsg)
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_whatsapp_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = WhatsAppGreen,
              contentColor = Color.White
            )
          ) {
            Icon(imageVector = Icons.Rounded.ChatBubble, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (isUrdu) "واٹس ایپ" else "WhatsApp", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Badges
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        TypeBadge(type = ad.type, isUrdu = isUrdu)
        StatusBadge(status = ad.status, isUrdu = isUrdu)
        if (ad.isFeatured) {
          FeaturedBadge(isUrdu = isUrdu)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Title
      Text(
        text = ad.title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Price / Rate Highlight Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldContainer)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isUrdu) "قیمت / چارجز" else "Price / Charges",
              style = MaterialTheme.typography.bodySmall,
              color = EmeraldOnContainer.copy(alpha = 0.8f)
            )
            Text(
              text = ad.priceOrRate,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = EmeraldOnContainer
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Visibility,
              contentDescription = null,
              tint = EmeraldOnContainer,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${ad.viewsCount} " + (if (isUrdu) "ملاحظات" else "views"),
              fontSize = 12.sp,
              color = EmeraldOnContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Business & Owner Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(EmeraldContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = ad.businessName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
              )
              Text(
                text = if (isUrdu) "مالک / نمائندہ: ${ad.ownerName}" else "Owner / Contact: ${ad.ownerName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Divider(color = BorderLight)
          Spacer(modifier = Modifier.height(12.dp))

          // Phone & WhatsApp info
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = if (isUrdu) "فون نمبر" else "Phone", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(text = ad.phone, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextDark)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = if (isUrdu) "واٹس ایپ" else "WhatsApp", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(text = ad.whatsapp, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = WhatsAppGreen)
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = if (isUrdu) "شہر و علاقہ" else "City & Location", style = MaterialTheme.typography.bodySmall, color = TextMuted)
            Text(text = "${ad.city}${if (ad.address.isNotBlank()) "، ${ad.address}" else ""}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = TextDark)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Description Section
      Text(
        text = if (isUrdu) "تفصیلات و سروسز" else "Description & Details",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight)
      ) {
        Text(
          text = ad.description,
          style = MaterialTheme.typography.bodyMedium,
          lineHeight = 22.sp,
          color = TextDark,
          modifier = Modifier.padding(14.dp)
        )
      }

      if (ad.rejectionReason.isNotBlank()) {
        Spacer(modifier = Modifier.height(14.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
          Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(text = if (isUrdu) "ایڈمن کا نوٹ:" else "Admin Note:", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 12.sp)
              Text(text = ad.rejectionReason, color = Color(0xFFB71C1C), fontSize = 12.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Safety Notice Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, BorderLight)
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = EmeraldPrimary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isUrdu) {
              "حفاظتی ہدایت: سودا طے کرتے وقت براہ راست معائنہ کریں۔ پیشگی رقم بھیجنے سے پہلے مکمل تسلی کریں۔ یہ اشتہار ایڈمن سے تصدیق شدہ ہے۔"
            } else {
              "Safety Tip: Always inspect goods/services in person. Never send advance payment without verification."
            },
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
