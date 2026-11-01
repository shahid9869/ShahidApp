package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Chat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdEntity
import com.example.data.model.AdStatus
import com.example.data.model.CategoryEntity
import com.example.data.model.MessageRole
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ChatMessageBubble
import com.example.ui.components.FeaturedBadge
import com.example.ui.components.StatusBadge
import com.example.ui.components.TypeBadge
import com.example.ui.theme.BorderLight
import com.example.ui.theme.DangerRed
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.PendingOrange
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.KarobarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: KarobarViewModel,
  onExitAdmin: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onExitAdmin() }

  val isUrdu by viewModel.isUrdu.collectAsState()
  val selectedTab by viewModel.adminSelectedTab.collectAsState()
  val allAds by viewModel.allAds.collectAsState()
  val pendingAds by viewModel.pendingAds.collectAsState()
  val approvedAds by viewModel.approvedAds.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val rejectingAdId by viewModel.rejectingAdId.collectAsState()
  val rejectionReason by viewModel.rejectionReasonText.collectAsState()
  val settingsMessage by viewModel.settingsMessage.collectAsState()
  val unreadChatCount by viewModel.unreadUserMessagesCount.collectAsState()

  val totalViews = allAds.sumOf { it.viewsCount }
  val featuredCount = allAds.count { it.isFeatured }
  val rejectedCount = allAds.count { it.status == AdStatus.REJECTED.name }

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(settingsMessage) {
    if (settingsMessage != null) {
      snackbarHostState.showSnackbar(settingsMessage ?: "")
      viewModel.clearSettingsMessage()
    }
  }

  // Rejection Dialog
  if (rejectingAdId != null) {
    AlertDialog(
      onDismissRequest = { viewModel.cancelRejectAd() },
      title = {
        Text(
          text = if (isUrdu) "اشتہار مسترد کرنے کی وجہ" else "Reason for Ad Rejection",
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column {
          Text(
            text = if (isUrdu) "اشتہار دہندہ کو وجہ سے آگاہ کریں تاکہ وہ تصحیح کر سکے:" else "Inform the user why the ad was rejected:",
            fontSize = 12.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = rejectionReason,
            onValueChange = { viewModel.rejectionReasonText.value = it },
            modifier = Modifier.fillMaxWidth().testTag("rejection_reason_input"),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DangerRed),
            placeholder = { Text(if (isUrdu) "مثلاً: نامکمل پتہ یا فون نمبر غلط ہے" else "e.g. Incomplete details") },
            maxLines = 3
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { viewModel.confirmRejectAd() },
          colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("confirm_reject_button")
        ) {
          Text(text = if (isUrdu) "مسترد کریں" else "Reject Ad", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.cancelRejectAd() }) {
          Text(text = if (isUrdu) "منسوخ" else "Cancel")
        }
      }
    )
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFE8F5E9), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = if (isUrdu) "ایڈمن کنٹرول پینل" else "Admin Control Panel",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark
              )
              Text(
                text = if (isUrdu) "مکمل ایپ کنٹرول و اشتہارات مینجمنٹ" else "Full App & Ads Control",
                fontSize = 11.sp,
                color = EmeraldPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        },
        actions = {
          // Exit button
          Surface(
            onClick = onExitAdmin,
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF3F4F6),
            modifier = Modifier.padding(end = 8.dp).testTag("exit_admin_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isUrdu) "ایپ پر جائیں" else "Exit to App",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = DangerRed
              )
            }
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
    ) {
      // 1. Stats Bar
      StatsOverviewRow(
        pendingCount = pendingAds.size,
        approvedCount = approvedAds.size,
        featuredCount = featuredCount,
        rejectedCount = rejectedCount,
        totalViews = totalViews,
        unreadChatCount = unreadChatCount,
        isUrdu = isUrdu,
        onPendingClick = { viewModel.adminSelectedTab.value = 0 },
        onChatClick = { viewModel.adminSelectedTab.value = 2 }
      )

      // 2. Tab Navigation
      val chatTabTitle = if (unreadChatCount > 0) {
        if (isUrdu) "💬 کسٹمر چیٹ ($unreadChatCount)" else "💬 Chat ($unreadChatCount)"
      } else {
        if (isUrdu) "💬 کسٹمر چیٹ" else "💬 Chat"
      }

      val tabTitles = if (isUrdu) {
        listOf(
          "⏳ منتظر (${pendingAds.size})",
          "📋 تمام (${allAds.size})",
          chatTabTitle,
          "📢 بینر",
          "🏷️ کیٹیگریز",
          "⚙️ سیٹنگز"
        )
      } else {
        listOf(
          "Pending (${pendingAds.size})",
          "All Ads (${allAds.size})",
          chatTabTitle,
          "Banner",
          "Categories",
          "Settings"
        )
      }

      ScrollableTabRow(
        selectedTabIndex = selectedTab,
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = EmeraldPrimary
          )
        }
      ) {
        tabTitles.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { viewModel.adminSelectedTab.value = index },
            text = {
              Text(
                text = title,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedTab == index) EmeraldPrimary else TextMuted,
                fontSize = 12.sp
              )
            }
          )
        }
      }

      Divider(color = BorderLight)

      // Tab Content
      when (selectedTab) {
        0 -> PendingAdsTab(
          pendingAds = pendingAds,
          isUrdu = isUrdu,
          onApprove = { viewModel.approveAd(it) },
          onReject = { viewModel.promptRejectAd(it) },
          onToggleFeatured = { id, cur -> viewModel.toggleFeatured(id, cur) },
          onDelete = { viewModel.deleteAd(it) }
        )
        1 -> AllAdsTab(
          ads = allAds,
          isUrdu = isUrdu,
          onApprove = { viewModel.approveAd(it) },
          onReject = { viewModel.promptRejectAd(it) },
          onToggleFeatured = { id, cur -> viewModel.toggleFeatured(id, cur) },
          onDelete = { viewModel.deleteAd(it) }
        )
        2 -> AdminChatTab(
          viewModel = viewModel,
          isUrdu = isUrdu
        )
        3 -> BannerControlTab(
          viewModel = viewModel,
          isUrdu = isUrdu
        )
        4 -> CategoriesControlTab(
          viewModel = viewModel,
          categories = categories,
          isUrdu = isUrdu
        )
        5 -> SettingsControlTab(
          viewModel = viewModel,
          isUrdu = isUrdu
        )
      }
    }
  }
}

@Composable
private fun StatsOverviewRow(
  pendingCount: Int,
  approvedCount: Int,
  featuredCount: Int,
  rejectedCount: Int,
  totalViews: Int,
  unreadChatCount: Int,
  isUrdu: Boolean,
  onPendingClick: () -> Unit,
  onChatClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Chat card with badge
    Surface(
      onClick = onChatClick,
      shape = RoundedCornerShape(12.dp),
      color = if (unreadChatCount > 0) Color(0xFFE8F5E9) else Color(0xFFF3F4F6),
      border = if (unreadChatCount > 0) BorderStroke(1.dp, Color(0xFF2E7D32)) else null
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Chat,
          contentDescription = null,
          tint = if (unreadChatCount > 0) Color(0xFF2E7D32) else TextMuted,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isUrdu) "کسٹمر چیٹ: $unreadChatCount" else "Chat: $unreadChatCount",
          fontWeight = FontWeight.Bold,
          color = if (unreadChatCount > 0) Color(0xFF2E7D32) else TextDark,
          fontSize = 11.sp
        )
      }
    }

    // Pending card with alert styling
    Surface(
      onClick = onPendingClick,
      shape = RoundedCornerShape(12.dp),
      color = if (pendingCount > 0) Color(0xFFFFF3E0) else Color(0xFFF3F4F6),
      border = if (pendingCount > 0) BorderStroke(1.dp, PendingOrange) else null
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.HourglassTop,
          contentDescription = null,
          tint = if (pendingCount > 0) PendingOrange else TextMuted,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isUrdu) "زیر جائزہ: $pendingCount" else "Pending: $pendingCount",
          fontWeight = FontWeight.Bold,
          color = if (pendingCount > 0) PendingOrange else TextDark,
          fontSize = 11.sp
        )
      }
    }

    // Approved Live
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFFE8F5E9)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isUrdu) "لائیو: $approvedCount" else "Live: $approvedCount",
          fontWeight = FontWeight.Bold,
          color = Color(0xFF2E7D32),
          fontSize = 11.sp
        )
      }
    }

    // Featured VIP
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFFFFF8E1)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isUrdu) "نمایاں: $featuredCount" else "Featured: $featuredCount",
          fontWeight = FontWeight.Bold,
          color = GoldSecondary,
          fontSize = 11.sp
        )
      }
    }

    // Total Impressions
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFFE3F2FD)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isUrdu) "ملاحظات: $totalViews" else "Views: $totalViews",
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1976D2),
          fontSize = 11.sp
        )
      }
    }
  }
}

@Composable
private fun PendingAdsTab(
  pendingAds: List<AdEntity>,
  isUrdu: Boolean,
  onApprove: (Long) -> Unit,
  onReject: (Long) -> Unit,
  onToggleFeatured: (Long, Boolean) -> Unit,
  onDelete: (Long) -> Unit
) {
  if (pendingAds.isEmpty()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = Color(0xFF2E7D32),
          modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = if (isUrdu) "کوئی نیا اشتہار زیر جائزہ نہیں ہے!" else "No pending ads right now!",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (isUrdu) "تمام جمع شدہ اشتہارات جانچے جا چکے ہیں" else "All submitted ads have been reviewed",
          style = MaterialTheme.typography.bodySmall,
          color = TextMuted
        )
      }
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(pendingAds, key = { it.id }) { ad ->
        AdminPendingAdCard(
          ad = ad,
          isUrdu = isUrdu,
          onApprove = { onApprove(ad.id) },
          onReject = { onReject(ad.id) },
          onToggleFeatured = { onToggleFeatured(ad.id, ad.isFeatured) },
          onDelete = { onDelete(ad.id) }
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminPendingAdCard(
  ad: AdEntity,
  isUrdu: Boolean,
  onApprove: () -> Unit,
  onReject: () -> Unit,
  onToggleFeatured: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.5.dp, PendingOrange),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth().testTag("pending_ad_card_${ad.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          StatusBadge(status = ad.status, isUrdu = isUrdu)
          TypeBadge(type = ad.type, isUrdu = isUrdu)
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = ad.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )

      if (ad.businessName.isNotBlank()) {
        Text(
          text = ad.businessName,
          style = MaterialTheme.typography.bodySmall,
          color = EmeraldPrimary,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "${ad.city} | ${ad.ownerName} | فون: ${ad.phone}",
        fontSize = 11.sp,
        color = TextMuted
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = ad.description,
        style = MaterialTheme.typography.bodySmall,
        color = TextDark,
        maxLines = 3
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "${if (isUrdu) "نرخ:" else "Rate:"} ${ad.priceOrRate}",
        fontWeight = FontWeight.Bold,
        color = EmeraldPrimary,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.height(12.dp))
      Divider(color = BorderLight)
      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Approve Button
        Button(
          onClick = onApprove,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
          modifier = Modifier.weight(1f).height(40.dp).testTag("approve_btn_${ad.id}")
        ) {
          Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isUrdu) "منظور کریں" else "Approve", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Reject Button
        OutlinedButton(
          onClick = onReject,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, DangerRed),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
          modifier = Modifier.weight(1f).height(40.dp).testTag("reject_btn_${ad.id}")
        ) {
          Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isUrdu) "مسترد کریں" else "Reject", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Featured Toggle Button
        IconButton(
          onClick = onToggleFeatured,
          modifier = Modifier.size(40.dp)
        ) {
          Icon(
            imageVector = if (ad.isFeatured) Icons.Default.Star else Icons.Outlined.StarOutline,
            contentDescription = "Feature",
            tint = if (ad.isFeatured) GoldSecondary else TextMuted
          )
        }
      }
    }
  }
}

@Composable
private fun AllAdsTab(
  ads: List<AdEntity>,
  isUrdu: Boolean,
  onApprove: (Long) -> Unit,
  onReject: (Long) -> Unit,
  onToggleFeatured: (Long, Boolean) -> Unit,
  onDelete: (Long) -> Unit
) {
  var filterStatus by remember { mutableStateOf<String?>("ALL") }

  val displayedAds = when (filterStatus) {
    "APPROVED" -> ads.filter { it.status == AdStatus.APPROVED.name }
    "PENDING" -> ads.filter { it.status == AdStatus.PENDING.name }
    "REJECTED" -> ads.filter { it.status == AdStatus.REJECTED.name }
    "FEATURED" -> ads.filter { it.isFeatured }
    else -> ads
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Status Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FilterChipItem(label = if (isUrdu) "تمام (${ads.size})" else "All (${ads.size})", isSelected = filterStatus == "ALL") { filterStatus = "ALL" }
      FilterChipItem(label = if (isUrdu) "لائیو (${ads.count { it.status == AdStatus.APPROVED.name }})" else "Live", isSelected = filterStatus == "APPROVED") { filterStatus = "APPROVED" }
      FilterChipItem(label = if (isUrdu) "منتظر (${ads.count { it.status == AdStatus.PENDING.name }})" else "Pending", isSelected = filterStatus == "PENDING") { filterStatus = "PENDING" }
      FilterChipItem(label = if (isUrdu) "نمایاں (${ads.count { it.isFeatured }})" else "Featured", isSelected = filterStatus == "FEATURED") { filterStatus = "FEATURED" }
      FilterChipItem(label = if (isUrdu) "مسترد (${ads.count { it.status == AdStatus.REJECTED.name }})" else "Rejected", isSelected = filterStatus == "REJECTED") { filterStatus = "REJECTED" }
    }

    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(displayedAds, key = { "all_${it.id}" }) { ad ->
        AdminAdRowCard(
          ad = ad,
          isUrdu = isUrdu,
          onApprove = { onApprove(ad.id) },
          onReject = { onReject(ad.id) },
          onToggleFeatured = { onToggleFeatured(ad.id, ad.isFeatured) },
          onDelete = { onDelete(ad.id) }
        )
      }
    }
  }
}

@Composable
private fun FilterChipItem(label: String, isSelected: Boolean, onClick: () -> Unit) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) EmeraldPrimary else Color(0xFFF3F4F6)
  ) {
    Text(
      text = label,
      color = if (isSelected) Color.White else TextDark,
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminAdRowCard(
  ad: AdEntity,
  isUrdu: Boolean,
  onApprove: () -> Unit,
  onReject: () -> Unit,
  onToggleFeatured: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BorderLight),
    modifier = Modifier.fillMaxWidth().testTag("admin_ad_row_${ad.id}")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          StatusBadge(status = ad.status, isUrdu = isUrdu)
          TypeBadge(type = ad.type, isUrdu = isUrdu)
          if (ad.isFeatured) FeaturedBadge(isUrdu = isUrdu)
        }

        Row {
          // Toggle Featured
          IconButton(onClick = onToggleFeatured, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = if (ad.isFeatured) Icons.Default.Star else Icons.Outlined.StarOutline,
              contentDescription = "Feature",
              tint = if (ad.isFeatured) GoldSecondary else TextMuted
            )
          }
          // Delete
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
          }
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = ad.title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )

      Text(
        text = "${ad.city} • ${ad.phone} • ${ad.priceOrRate}",
        fontSize = 11.sp,
        color = TextMuted
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Status toggle row
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (ad.status != AdStatus.APPROVED.name) {
          Surface(
            onClick = onApprove,
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE8F5E9)
          ) {
            Text(
              text = if (isUrdu) "منظور کریں" else "Approve",
              color = Color(0xFF2E7D32),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        if (ad.status != AdStatus.REJECTED.name) {
          Surface(
            onClick = onReject,
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFFFEBEE)
          ) {
            Text(
              text = if (isUrdu) "مسترد کریں" else "Reject",
              color = DangerRed,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AdminChatTab(
  viewModel: KarobarViewModel,
  isUrdu: Boolean
) {
  val context = LocalContext.current
  val chatMessages by viewModel.chatMessages.collectAsState()
  val inputText by viewModel.adminChatInputText.collectAsState()
  val isRecording by viewModel.isRecordingVoice.collectAsState()
  val recordingDuration by viewModel.recordingDurationSec.collectAsState()
  val playingPath by viewModel.currentPlayingAudioPath.collectAsState()
  val audioProgress by viewModel.currentAudioProgressSec.collectAsState()

  val listState = rememberLazyListState()

  LaunchedEffect(Unit) {
    viewModel.markUserMessagesRead()
  }

  LaunchedEffect(chatMessages.size) {
    if (chatMessages.isNotEmpty()) {
      listState.animateScrollToItem(chatMessages.size - 1)
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    // Header Info banner
    Surface(
      color = Color(0xFFE8F5E9),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (isUrdu) "کسٹمر سے لائیو بات چیت (ایڈمن پورٹل)" else "Live Customer Conversation",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF1B5E20)
          )
          Text(
            text = if (isUrdu) "کسٹمرز کے سوالات کے جوابات بذریعہ ٹیکسٹ، تصویر یا وائس آڈیو دیں" else "Reply via text, image upload, or audio voice messages",
            fontSize = 10.sp,
            color = Color(0xFF2E7D32)
          )
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      state = listState,
      contentPadding = PaddingValues(vertical = 10.dp)
    ) {
      items(chatMessages, key = { "adm_chat_${it.id}" }) { message ->
        val isMyMessage = message.senderRole == MessageRole.ADMIN.name
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

    ChatInputBar(
      inputText = inputText,
      onInputTextChange = { viewModel.adminChatInputText.value = it },
      onSendText = { viewModel.sendTextMessage(MessageRole.ADMIN, inputText) },
      onImagePicked = { path -> viewModel.sendImageMessage(MessageRole.ADMIN, path) },
      isRecording = isRecording,
      recordingDurationSec = recordingDuration,
      onStartRecord = { viewModel.startVoiceRecording(context) },
      onStopAndSendRecord = { viewModel.stopAndSendVoiceRecording(MessageRole.ADMIN) },
      onCancelRecord = { viewModel.cancelVoiceRecording() },
      isUrdu = isUrdu
    )
  }
}

@Composable
private fun BannerControlTab(
  viewModel: KarobarViewModel,
  isUrdu: Boolean
) {
  val bannerTitle by viewModel.editBannerTitle.collectAsState()
  val bannerMessage by viewModel.editBannerMessage.collectAsState()
  val bannerActive by viewModel.editBannerActive.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = if (isUrdu) "ہوم اسکرین بینر و اعلانات کنٹرول" else "Home Announcement Banner Control",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = TextDark
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = if (isUrdu) {
        "یہاں سے آپ ایپ کے تمام صارفین کو ہوم اسکرین پر براہ راست اعلانات اور نوٹس دکھا سکتے ہیں۔"
      } else {
        "Control the live announcement broadcast to all app users on the home screen."
      },
      style = MaterialTheme.typography.bodySmall,
      color = TextMuted
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Active Toggle Switch
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderLight),
      modifier = Modifier.fillMaxWidth()
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
            text = if (isUrdu) "بینر لائیو دکھائیں" else "Show Banner Live",
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = if (bannerActive) {
              if (isUrdu) "بینر ہوم اسکرین پر نظر آ رہا ہے" else "Banner is visible to users"
            } else {
              if (isUrdu) "بینر پوشیدہ ہے" else "Banner is hidden"
            },
            fontSize = 11.sp,
            color = if (bannerActive) EmeraldPrimary else TextMuted
          )
        }

        Switch(
          checked = bannerActive,
          onCheckedChange = { viewModel.editBannerActive.value = it },
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = EmeraldPrimary
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Banner Title
    OutlinedTextField(
      value = bannerTitle,
      onValueChange = { viewModel.editBannerTitle.value = it },
      label = { Text(if (isUrdu) "بینر کا عنوان" else "Banner Title") },
      modifier = Modifier.fillMaxWidth().testTag("banner_title_input"),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Banner Message
    OutlinedTextField(
      value = bannerMessage,
      onValueChange = { viewModel.editBannerMessage.value = it },
      label = { Text(if (isUrdu) "بینر کا پیغام / اعلان" else "Banner Message") },
      modifier = Modifier.fillMaxWidth().height(100.dp).testTag("banner_msg_input"),
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
      maxLines = 4
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Save Button
    Button(
      onClick = { viewModel.saveBannerSettings() },
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
      modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_banner_btn")
    ) {
      Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (isUrdu) "بینر محفوظ و لاگو کریں" else "Save & Apply Live",
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
private fun CategoriesControlTab(
  viewModel: KarobarViewModel,
  categories: List<CategoryEntity>,
  isUrdu: Boolean
) {
  var newCatUrdu by remember { mutableStateOf("") }
  var newCatEng by remember { mutableStateOf("") }
  var newCatType by remember { mutableStateOf("ALL") }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = if (isUrdu) "کیٹیگریز مینجمنٹ" else "Category Management",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = TextDark
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Add New Category Section
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderLight),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = if (isUrdu) "نئی کیٹیگری شامل کریں" else "Add New Category",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = EmeraldPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newCatUrdu,
            onValueChange = { newCatUrdu = it },
            placeholder = { Text(if (isUrdu) "اردو نام" else "Urdu Name", fontSize = 11.sp) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
          OutlinedTextField(
            value = newCatEng,
            onValueChange = { newCatEng = it },
            placeholder = { Text("English", fontSize = 11.sp) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = {
            if (newCatUrdu.isNotBlank() && newCatEng.isNotBlank()) {
              viewModel.addNewCategory(newCatEng, newCatUrdu, newCatEng, newCatType)
              newCatUrdu = ""
              newCatEng = ""
            }
          },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
          modifier = Modifier.fillMaxWidth().height(36.dp)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = if (isUrdu) "کیٹیگری شامل کریں" else "Add Category", fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // List of Categories
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      items(categories, key = { it.id }) { cat ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderLight),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = if (isUrdu) cat.nameUrdu else cat.nameEnglish, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
              Text(text = cat.nameEnglish, fontSize = 10.sp, color = TextMuted)
            }
            Switch(
              checked = cat.isActive,
              onCheckedChange = { viewModel.toggleCategory(cat.id, cat.isActive) },
              colors = SwitchDefaults.colors(checkedTrackColor = EmeraldPrimary)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsControlTab(
  viewModel: KarobarViewModel,
  isUrdu: Boolean
) {
  val autoApprove by viewModel.autoApproveToggle.collectAsState()
  val newPin by viewModel.newAdminPin.collectAsState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = if (isUrdu) "ایڈمن سسٹم سیٹنگز" else "Admin System Settings",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = TextDark
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Auto Approve Toggle
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderLight),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (isUrdu) "خودکار منظوری (Auto-Approve)" else "Auto-Approve Ads",
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = if (autoApprove) {
              if (isUrdu) "صارف کا اشتہار لگتے ہی فوراً لائیو ہو جائے گا" else "Ads go live immediately without review"
            } else {
              if (isUrdu) "تمام اشتہارات ایڈمن کی منظوری کے بعد ہی لائیو ہوں گے (موصول شدہ اشتہار کو کنٹرول کرنا)" else "Manual review required by Admin before publishing"
            },
            fontSize = 11.sp,
            color = if (autoApprove) GoldSecondary else EmeraldPrimary,
            lineHeight = 16.sp
          )
        }

        Switch(
          checked = autoApprove,
          onCheckedChange = { viewModel.toggleAutoApprove(it) },
          colors = SwitchDefaults.colors(checkedTrackColor = EmeraldPrimary)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Change Admin PIN
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      border = BorderStroke(1.dp, BorderLight),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = if (isUrdu) "ایڈمن پن کوڈ تبدیل کریں" else "Change Admin PIN",
          fontWeight = FontWeight.Bold,
          color = TextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (isUrdu) "ایڈمن پینل کی سیکیورٹی کے لیے نیا 4 ہندسوں کا پن مقرر کریں" else "Set a new 4-digit PIN for Admin access",
          fontSize = 11.sp,
          color = TextMuted
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = newPin,
            onValueChange = { if (it.length <= 6) viewModel.newAdminPin.value = it },
            placeholder = { Text(if (isUrdu) "نیا پن (مثلاً 5678)" else "New PIN", fontSize = 12.sp) },
            modifier = Modifier.weight(1f).testTag("new_pin_input"),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
          )
          Button(
            onClick = { viewModel.changeAdminPin() },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
            modifier = Modifier.height(52.dp).testTag("save_new_pin_btn")
          ) {
            Text(text = if (isUrdu) "محفوظ کریں" else "Save")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Admin Info Card
    Card(
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Text(
          text = if (isUrdu) "ایڈمن کنٹرول معلومات" else "Admin Info",
          fontWeight = FontWeight.Bold,
          color = EmeraldOnContainer,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = if (isUrdu) {
            "• آپ کسی بھی اشتہار کو منظور، مسترد، یا نمایاں (VIP) بنا سکتے ہیں۔\n• ہوم اسکرین کا بینر لائیو تبدیل کر سکتے ہیں۔\n• تمام ڈیٹا مقامی ڈیٹا بیس میں محفوظ رہتا ہے۔"
          } else {
            "• Moderate, approve, reject, or feature any ad.\n• Live update the broadcast banner.\n• All operations are persisted reliably."
          },
          fontSize = 11.sp,
          color = EmeraldOnContainer.copy(alpha = 0.9f),
          lineHeight = 18.sp
        )
      }
    }
  }
}
