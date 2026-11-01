package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdEntity
import com.example.data.model.CategoryEntity
import com.example.ui.components.AdCard
import com.example.ui.components.BannerCard
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.KarobarViewModel
import com.example.ui.viewmodel.Screen

private val PAKISTANI_CITIES = listOf(
  "تمام شہر",
  "لاہور",
  "کراچی",
  "اسلام آباد",
  "فیصل آباد",
  "راولپنڈی",
  "ملتان",
  "پشاور",
  "گوجرانوالہ",
  "سیالکوٹ",
  "کوئٹہ"
)

@Composable
fun HomeScreen(
  viewModel: KarobarViewModel,
  onAdClick: (Long) -> Unit,
  onPostAdClick: () -> Unit,
  onAdminClick: () -> Unit,
  onChatClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isUrdu by viewModel.isUrdu.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategoryId.collectAsState()
  val selectedType by viewModel.selectedType.collectAsState()
  val selectedCity by viewModel.selectedCity.collectAsState()
  val filteredAds by viewModel.filteredAds.collectAsState()
  val featuredAds by viewModel.featuredAds.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val configs by viewModel.configs.collectAsState()
  val pendingAds by viewModel.pendingAds.collectAsState()
  val bookmarkedIds by viewModel.bookmarkedAdIds.collectAsState()

  // Banner details from config
  val bannerTitle = configs.find { it.configKey == "banner_title" }?.configValue ?: ""
  val bannerMessage = configs.find { it.configKey == "banner_message" }?.configValue ?: ""
  val bannerActive = configs.find { it.configKey == "banner_active" }?.configValue?.toBoolean() ?: true

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onPostAdClick,
        containerColor = EmeraldPrimary,
        contentColor = Color.White,
        icon = {
          Icon(imageVector = Icons.Default.Add, contentDescription = null)
        },
        text = {
          Text(
            text = if (isUrdu) "نیا اشتہار لگائیں" else "Post New Ad",
            fontWeight = FontWeight.Bold
          )
        },
        modifier = Modifier.testTag("post_ad_fab")
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 90.dp)
    ) {
      // 1. Top Header & Branding
      item {
        TopBrandingBar(
          isUrdu = isUrdu,
          pendingCount = pendingAds.size,
          onLanguageToggle = { viewModel.toggleLanguage() },
          onAdminClick = onAdminClick,
          onChatClick = onChatClick
        )
      }

      // 2. Search Field
      item {
        SearchBarSection(
          searchQuery = searchQuery,
          onQueryChange = { viewModel.searchQuery.value = it },
          isUrdu = isUrdu
        )
      }

      // 3. Ad Type Tabs (All, Business, Services)
      item {
        AdTypePills(
          selectedType = selectedType ?: "ALL",
          onTypeSelect = { viewModel.selectedType.value = it },
          isUrdu = isUrdu
        )
      }

      // 4. City Horizontal Filter
      item {
        CityFilterRow(
          selectedCity = selectedCity ?: "تمام شہر",
          onCitySelect = { viewModel.selectedCity.value = it },
          isUrdu = isUrdu
        )
      }

      // 5. Admin Announcement Banner (if active)
      if (bannerActive && (bannerTitle.isNotBlank() || bannerMessage.isNotBlank())) {
        item {
          Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            BannerCard(title = bannerTitle, message = bannerMessage)
          }
        }
      }

      // Direct Chat with Admin Helpdesk Card
      item {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
          Card(
            onClick = onChatClick,
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
            modifier = Modifier.fillMaxWidth().testTag("chat_admin_banner_card")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .background(Color(0xFF2E7D32), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.SupportAgent,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isUrdu) "ایڈمن سے بات کریں (میسج، تصویر، وائس)" else "Chat with Admin (Text, Photo, Voice)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color(0xFF1B5E20)
                )
                Text(
                  text = if (isUrdu) "کوئی سوال پوچھیں، تصویر یا آڈیو میسج بھیجیں" else "Send a message, photo, or audio voice note",
                  fontSize = 10.sp,
                  color = Color(0xFF2E7D32)
                )
              }
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2E7D32)
              ) {
                Text(
                  text = if (isUrdu) "بات کریں" else "Chat",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      // 6. Categories Row
      item {
        CategoriesSection(
          categories = categories.filter { it.isActive },
          selectedCategoryId = selectedCategory,
          onSelectCategory = { id ->
            viewModel.selectedCategoryId.value = if (selectedCategory == id) null else id
          },
          isUrdu = isUrdu
        )
      }

      // 7. Featured Ads Carousel (if any and not searching heavily)
      if (featuredAds.isNotEmpty() && searchQuery.isBlank() && selectedCategory == null) {
        item {
          FeaturedSection(
            featuredAds = featuredAds,
            isUrdu = isUrdu,
            bookmarkedIds = bookmarkedIds,
            onBookmarkToggle = { viewModel.toggleBookmark(it) },
            onAdClick = onAdClick
          )
        }
      }

      // 8. Main Feed Header
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (isUrdu) "تازہ ترین کاروباری و سروسز اشتہارات" else "Latest Business & Service Ads",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
          Text(
            text = "${filteredAds.size} " + (if (isUrdu) "اشتہار" else "ads"),
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
          )
        }
      }

      // 9. Ads List or Empty State
      if (filteredAds.isEmpty()) {
        item {
          EmptyAdsView(isUrdu = isUrdu, onReset = {
            viewModel.searchQuery.value = ""
            viewModel.selectedCategoryId.value = null
            viewModel.selectedType.value = "ALL"
            viewModel.selectedCity.value = "تمام شہر"
          })
        }
      } else {
        items(filteredAds, key = { it.id }) { ad ->
          Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            AdCard(
              ad = ad,
              isUrdu = isUrdu,
              isBookmarked = bookmarkedIds.contains(ad.id),
              onBookmarkClick = { viewModel.toggleBookmark(ad.id) },
              onClick = { onAdClick(ad.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun TopBrandingBar(
  isUrdu: Boolean,
  pendingCount: Int,
  onLanguageToggle: () -> Unit,
  onAdminClick: () -> Unit,
  onChatClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = if (isUrdu) "کاروبار و سروسز اشتہارات" else "Karobar & Services",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        color = EmeraldPrimary
      )
      Text(
        text = if (isUrdu) "مفت کاروباری پروموشن و خدمات" else "Business Promotion & Local Services",
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted
      )
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
      // Chat with Admin Button
      Surface(
        onClick = onChatClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE8F5E9),
        modifier = Modifier.testTag("top_bar_chat_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "چیٹ کریں",
            tint = Color(0xFF2E7D32),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isUrdu) "چیٹ" else "Chat",
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Language Toggle Button
      IconButton(
        onClick = onLanguageToggle,
        modifier = Modifier.size(36.dp)
      ) {
        Surface(
          shape = CircleShape,
          color = Color(0xFFF3F4F6),
          modifier = Modifier.size(30.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = if (isUrdu) "EN" else "اردو",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = EmeraldPrimary
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(4.dp))

      // Admin Control Room Button with badge
      Surface(
        onClick = onAdminClick,
        shape = RoundedCornerShape(12.dp),
        color = EmeraldContainer,
        modifier = Modifier.testTag("admin_panel_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          BadgedBox(
            badge = {
              if (pendingCount > 0) {
                Badge(
                  containerColor = Color(0xFFD32F2F),
                  contentColor = Color.White
                ) {
                  Text(text = "$pendingCount")
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = "ایڈمن پینل",
              tint = EmeraldOnContainer,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isUrdu) "ایڈمن" else "Admin",
            color = EmeraldOnContainer,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
private fun SearchBarSection(
  searchQuery: String,
  onQueryChange: (String) -> Unit,
  isUrdu: Boolean
) {
  Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onQueryChange,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("search_ad_input"),
      placeholder = {
        Text(
          text = if (isUrdu) "اشتہار، کاروبار یا سروس تلاش کریں..." else "Search ads, business, or services...",
          fontSize = 13.sp,
          color = TextMuted
        )
      },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = null,
          tint = EmeraldPrimary
        )
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
          }
        }
      },
      shape = RoundedCornerShape(14.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldPrimary,
        unfocusedBorderColor = Color(0xFFE5E7EB),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
      ),
      singleLine = true
    )
  }
}

@Composable
private fun AdTypePills(
  selectedType: String,
  onTypeSelect: (String) -> Unit,
  isUrdu: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    TypePillItem(
      label = if (isUrdu) "تمام اشتہارات" else "All Ads",
      isSelected = selectedType == "ALL",
      onClick = { onTypeSelect("ALL") },
      modifier = Modifier.weight(1f)
    )
    TypePillItem(
      label = if (isUrdu) "🏢 کاروباری" else "🏢 Business",
      isSelected = selectedType == "BUSINESS",
      onClick = { onTypeSelect("BUSINESS") },
      modifier = Modifier.weight(1f)
    )
    TypePillItem(
      label = if (isUrdu) "🛠️ سروسز" else "🛠️ Services",
      isSelected = selectedType == "SERVICE",
      onClick = { onTypeSelect("SERVICE") },
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun TypePillItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(12.dp),
    color = if (isSelected) EmeraldPrimary else Color.White,
    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
    modifier = modifier.height(38.dp)
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        color = if (isSelected) Color.White else TextDark,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

@Composable
private fun CityFilterRow(
  selectedCity: String,
  onCitySelect: (String) -> Unit,
  isUrdu: Boolean
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    items(PAKISTANI_CITIES) { city ->
      val isSelected = selectedCity == city
      Surface(
        onClick = { onCitySelect(city) },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) EmeraldContainer else Color(0xFFF3F4F6),
        modifier = Modifier.height(32.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.padding(horizontal = 12.dp)
        ) {
          Text(
            text = city,
            color = if (isSelected) EmeraldOnContainer else TextDark,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }
  }
}

@Composable
private fun CategoriesSection(
  categories: List<CategoryEntity>,
  selectedCategoryId: String?,
  onSelectCategory: (String) -> Unit,
  isUrdu: Boolean
) {
  Column(modifier = Modifier.padding(vertical = 8.dp)) {
    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(categories) { cat ->
        val isSelected = selectedCategoryId == cat.id
        val icon = getCategoryIcon(cat.iconName)
        Surface(
          onClick = { onSelectCategory(cat.id) },
          shape = RoundedCornerShape(14.dp),
          color = if (isSelected) EmeraldPrimary else Color.White,
          border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
          modifier = Modifier.height(44.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = if (isSelected) Color.White else EmeraldPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isUrdu) cat.nameUrdu else cat.nameEnglish,
              color = if (isSelected) Color.White else TextDark,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }
    }
  }
}

@Composable
private fun FeaturedSection(
  featuredAds: List<AdEntity>,
  isUrdu: Boolean,
  bookmarkedIds: Set<Long>,
  onBookmarkToggle: (Long) -> Unit,
  onAdClick: (Long) -> Unit
) {
  Column(modifier = Modifier.padding(vertical = 8.dp)) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = GoldSecondary,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (isUrdu) "نمایاں اشتہارات (VIP)" else "Featured Ads",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    LazyRow(
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(featuredAds, key = { "feat_${it.id}" }) { ad ->
        AdCard(
          ad = ad,
          isUrdu = isUrdu,
          isBookmarked = bookmarkedIds.contains(ad.id),
          onBookmarkClick = { onBookmarkToggle(ad.id) },
          onClick = { onAdClick(ad.id) },
          modifier = Modifier.width(280.dp)
        )
      }
    }
  }
}

@Composable
private fun EmptyAdsView(
  isUrdu: Boolean,
  onReset: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = null,
        tint = TextMuted,
        modifier = Modifier.size(48.dp)
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = if (isUrdu) "کوئی اشتہار نہیں ملا" else "No ads found",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (isUrdu) "فلٹرز تبدیل کریں یا نیا اشتہار شامل کریں" else "Try clearing filters or post a new ad",
        style = MaterialTheme.typography.bodySmall,
        color = TextMuted
      )
      Spacer(modifier = Modifier.height(12.dp))
      Surface(
        onClick = onReset,
        shape = RoundedCornerShape(10.dp),
        color = EmeraldContainer
      ) {
        Text(
          text = if (isUrdu) "فلٹرز ری سیٹ کریں" else "Reset Filters",
          color = EmeraldOnContainer,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }
    }
  }
}

private fun getCategoryIcon(iconName: String): ImageVector {
  return when (iconName) {
    "Build" -> Icons.Default.Build
    "Storefront" -> Icons.Default.Storefront
    "DirectionsCar" -> Icons.Default.DirectionsCar
    "HomeWork" -> Icons.Default.HomeWork
    "School" -> Icons.Default.School
    "Restaurant" -> Icons.Default.Restaurant
    "Print" -> Icons.Default.Print
    "LocalHospital" -> Icons.Default.LocalHospital
    else -> Icons.Default.Category
  }
}
