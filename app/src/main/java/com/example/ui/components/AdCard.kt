package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdEntity
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CallBlue
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WhatsAppGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdCard(
  ad: AdEntity,
  isUrdu: Boolean = true,
  isBookmarked: Boolean = false,
  onBookmarkClick: () -> Unit = {},
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("ad_card_${ad.id}")
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = if (ad.isFeatured) BorderStroke(1.5.dp, GoldSecondary) else BorderStroke(1.dp, BorderLight),
    elevation = CardDefaults.cardElevation(defaultElevation = if (ad.isFeatured) 3.dp else 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      // Top badges & bookmark
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          TypeBadge(type = ad.type, isUrdu = isUrdu)
          if (ad.isFeatured) {
            FeaturedBadge(isUrdu = isUrdu)
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF3F4F6)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = ad.city,
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        IconButton(
          onClick = onBookmarkClick,
          modifier = Modifier.size(36.dp).testTag("bookmark_ad_${ad.id}")
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "محفوظ کریں",
            tint = if (isBookmarked) EmeraldPrimary else TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title
      Text(
        text = ad.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = TextDark
      )

      if (ad.businessName.isNotBlank() && ad.businessName != ad.title) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = ad.businessName,
          style = MaterialTheme.typography.bodySmall,
          color = EmeraldPrimary,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Description snippet
      Text(
        text = ad.description,
        style = MaterialTheme.typography.bodyMedium,
        color = TextMuted,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Price / Rate banner
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = ad.priceOrRate,
          style = MaterialTheme.typography.titleSmall,
          color = EmeraldPrimary,
          fontWeight = FontWeight.Bold
        )

        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "${ad.viewsCount}",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons: Call & WhatsApp
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = { ActionHelpers.launchDialer(context, ad.phone) },
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("call_button_${ad.id}"),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, CallBlue),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = CallBlue)
        ) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isUrdu) "کال کریں" else "Call",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Button(
          onClick = {
            val preMsg = if (isUrdu) {
              "السلام علیکم! میں نے کاروبار ایپ پر آپ کا اشتہار دیکھا: '${ad.title}'۔ اس کے بارے میں مزید معلومات چاہیے۔"
            } else {
              "Hi, I saw your ad on Karobar Ads: '${ad.title}'. Interested in more details."
            }
            ActionHelpers.launchWhatsApp(context, ad.whatsapp, preMsg)
          },
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("whatsapp_button_${ad.id}"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = WhatsAppGreen,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = Icons.Rounded.ChatBubble,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isUrdu) "واٹس ایپ" else "WhatsApp",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
