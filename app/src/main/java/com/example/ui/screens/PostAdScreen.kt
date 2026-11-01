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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdType
import com.example.ui.theme.BorderLight
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.KarobarViewModel

private val PAKISTANI_CITIES = listOf(
  "لاہور", "کراچی", "اسلام آباد", "فیصل آباد", "راولپنڈی",
  "ملتان", "پشاور", "گوجرانوالہ", "سیالکوٹ", "کوئٹہ", "دیگر شہر"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAdScreen(
  viewModel: KarobarViewModel,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBackClick() }

  val isUrdu by viewModel.isUrdu.collectAsState()
  val formState by viewModel.postAdForm.collectAsState()
  val categories by viewModel.categories.collectAsState()

  var categoryDropdownExpanded by remember { mutableStateOf(false) }
  var cityDropdownExpanded by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (isUrdu) "نیا اشتہار لگائیں" else "Post New Advertisement",
            fontWeight = FontWeight.Bold,
            color = TextDark
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackClick, modifier = Modifier.testTag("post_ad_back")) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "واپس")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { padding ->
    if (formState.successMessage != null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BorderLight),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = EmeraldPrimary,
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = if (isUrdu) "اشتہار موصول ہو گیا!" else "Ad Submitted!",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = formState.successMessage ?: "",
              style = MaterialTheme.typography.bodyMedium,
              color = TextMuted,
              lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
              onClick = {
                viewModel.resetPostForm()
                onBackClick()
              },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
              modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
              Text(
                text = if (isUrdu) "ہوم پر واپس جائیں" else "Return to Home",
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      // Type Selector: Business vs Service
      Text(
        text = if (isUrdu) "اشتہار کی قسم منتخب کریں:" else "Select Advertisement Type:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TextDark
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val isBusiness = formState.type == AdType.BUSINESS.name
        val isService = formState.type == AdType.SERVICE.name

        Surface(
          onClick = { viewModel.updatePostFormField { copy(type = AdType.BUSINESS.name) } },
          shape = RoundedCornerShape(12.dp),
          color = if (isBusiness) EmeraldPrimary else Color.White,
          border = BorderStroke(1.dp, if (isBusiness) EmeraldPrimary else BorderLight),
          modifier = Modifier.weight(1f).height(50.dp).testTag("select_business_type")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Business,
              contentDescription = null,
              tint = if (isBusiness) Color.White else TextDark,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isUrdu) "کاروباری اشتہار" else "Business Ad",
              color = if (isBusiness) Color.White else TextDark,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }

        Surface(
          onClick = { viewModel.updatePostFormField { copy(type = AdType.SERVICE.name) } },
          shape = RoundedCornerShape(12.dp),
          color = if (isService) EmeraldPrimary else Color.White,
          border = BorderStroke(1.dp, if (isService) EmeraldPrimary else BorderLight),
          modifier = Modifier.weight(1f).height(50.dp).testTag("select_service_type")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Handyman,
              contentDescription = null,
              tint = if (isService) Color.White else TextDark,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isUrdu) "سروسز کا اشتہار" else "Service Ad",
              color = if (isService) Color.White else TextDark,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Ad Title
      OutlinedTextField(
        value = formState.title,
        onValueChange = { viewModel.updatePostFormField { copy(title = it) } },
        label = { Text(if (isUrdu) "اشتہار کا عنوان *" else "Ad Title *") },
        placeholder = { Text(if (isUrdu) "مثلاً: اے سی ریپیئر سروس یا ہول سیل گارمنٹس" else "e.g. AC Repair Service or Wholesale Garments") },
        modifier = Modifier.fillMaxWidth().testTag("post_ad_title_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = EmeraldPrimary,
          focusedLabelColor = EmeraldPrimary
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Business Name / Shop Name
      OutlinedTextField(
        value = formState.businessName,
        onValueChange = { viewModel.updatePostFormField { copy(businessName = it) } },
        label = { Text(if (isUrdu) "دکان / ادارے کا نام" else "Business / Shop Name") },
        placeholder = { Text(if (isUrdu) "مثلاً: ملک الیکٹرانکس یا الحرم ٹیکسٹائل" else "e.g. Malik Electronics") },
        modifier = Modifier.fillMaxWidth().testTag("post_ad_business_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = EmeraldPrimary,
          focusedLabelColor = EmeraldPrimary
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Category Picker
      ExposedDropdownMenuBox(
        expanded = categoryDropdownExpanded,
        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
      ) {
        val selectedCategory = categories.find { it.id == formState.category }
        val categoryLabel = if (selectedCategory != null) {
          if (isUrdu) selectedCategory.nameUrdu else selectedCategory.nameEnglish
        } else {
          formState.category
        }

        OutlinedTextField(
          value = categoryLabel,
          onValueChange = {},
          readOnly = true,
          label = { Text(if (isUrdu) "کیٹیگری منتخب کریں" else "Select Category") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
          modifier = Modifier.menuAnchor().fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
        )

        ExposedDropdownMenu(
          expanded = categoryDropdownExpanded,
          onDismissRequest = { categoryDropdownExpanded = false }
        ) {
          categories.filter { it.isActive }.forEach { cat ->
            DropdownMenuItem(
              text = { Text(if (isUrdu) cat.nameUrdu else cat.nameEnglish) },
              onClick = {
                viewModel.updatePostFormField { copy(category = cat.id) }
                categoryDropdownExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Owner Name
      OutlinedTextField(
        value = formState.ownerName,
        onValueChange = { viewModel.updatePostFormField { copy(ownerName = it) } },
        label = { Text(if (isUrdu) "رابطہ کار کا نام" else "Contact Person / Owner Name") },
        placeholder = { Text(if (isUrdu) "مثلاً: محمد احمد" else "e.g. Muhammad Ahmad") },
        modifier = Modifier.fillMaxWidth().testTag("post_ad_owner_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Phone
      OutlinedTextField(
        value = formState.phone,
        onValueChange = { viewModel.updatePostFormField { copy(phone = it) } },
        label = { Text(if (isUrdu) "موبائل فون نمبر *" else "Phone Number *") },
        placeholder = { Text("03001234567") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth().testTag("post_ad_phone_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      // WhatsApp with Copy from Phone shortcut
      OutlinedTextField(
        value = formState.whatsapp,
        onValueChange = { viewModel.updatePostFormField { copy(whatsapp = it) } },
        label = { Text(if (isUrdu) "واٹس ایپ نمبر" else "WhatsApp Number") },
        placeholder = { Text("03001234567") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth().testTag("post_ad_whatsapp_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WhatsAppGreen),
        singleLine = true,
        trailingIcon = {
          if (formState.phone.isNotBlank()) {
            OutlinedButton(
              onClick = { viewModel.updatePostFormField { copy(whatsapp = formState.phone) } },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.padding(end = 4.dp).height(32.dp),
              border = BorderStroke(1.dp, EmeraldPrimary)
            ) {
              Text(
                text = if (isUrdu) "وہی نمبر" else "Same",
                fontSize = 11.sp,
                color = EmeraldPrimary
              )
            }
          }
        }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // City Dropdown
      ExposedDropdownMenuBox(
        expanded = cityDropdownExpanded,
        onExpandedChange = { cityDropdownExpanded = !cityDropdownExpanded }
      ) {
        OutlinedTextField(
          value = formState.city,
          onValueChange = {},
          readOnly = true,
          label = { Text(if (isUrdu) "شہر منتخب کریں" else "Select City") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityDropdownExpanded) },
          modifier = Modifier.menuAnchor().fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary)
        )

        ExposedDropdownMenu(
          expanded = cityDropdownExpanded,
          onDismissRequest = { cityDropdownExpanded = false }
        ) {
          PAKISTANI_CITIES.forEach { city ->
            DropdownMenuItem(
              text = { Text(city) },
              onClick = {
                viewModel.updatePostFormField { copy(city = city) }
                cityDropdownExpanded = false
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Address
      OutlinedTextField(
        value = formState.address,
        onValueChange = { viewModel.updatePostFormField { copy(address = it) } },
        label = { Text(if (isUrdu) "علاقہ / گلی / مارکیٹ کا پتہ" else "Location / Market Address") },
        placeholder = { Text(if (isUrdu) "مثلاً: گلبرگ III، فیروزپور روڈ" else "e.g. Gulberg III") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Price / Rate
      OutlinedTextField(
        value = formState.priceOrRate,
        onValueChange = { viewModel.updatePostFormField { copy(priceOrRate = it) } },
        label = { Text(if (isUrdu) "قیمت / وزٹ چارجز / نرخ" else "Price / Service Charges / Rate") },
        placeholder = { Text(if (isUrdu) "مثلاً: Rs 1,500 یا رابطہ کریں یا مناسب ریٹس" else "e.g. Rs 1,500 or Contact") },
        modifier = Modifier.fillMaxWidth().testTag("post_ad_price_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Description
      OutlinedTextField(
        value = formState.description,
        onValueChange = { viewModel.updatePostFormField { copy(description = it) } },
        label = { Text(if (isUrdu) "اشتہار کی مکمل تفصیلات *" else "Full Description *") },
        placeholder = { Text(if (isUrdu) "اپنے کام، سروسز، تجربے اور سہولیات کی تفصیل لکھیں..." else "Describe your business or services...") },
        modifier = Modifier.fillMaxWidth().height(130.dp).testTag("post_ad_desc_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EmeraldPrimary),
        maxLines = 6
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Admin Review Notice Box
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldContainer)
      ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EmeraldPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isUrdu) {
              "نوٹ: تمام اشتہارات ایڈمن پینل سے جانچ پڑتال کے بعد منظور اور لائیو کیے جاتے ہیں۔"
            } else {
              "Note: All ads are reviewed and approved by the Admin Panel before going live."
            },
            fontSize = 12.sp,
            color = TextDark,
            lineHeight = 17.sp
          )
        }
      }

      // Error message if any
      if (formState.errorMessage != null) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = formState.errorMessage ?: "",
          color = Color(0xFFD32F2F),
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Submit Button
      Button(
        onClick = { viewModel.submitAd() },
        enabled = !formState.isSubmitting,
        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("submit_ad_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
      ) {
        if (formState.isSubmitting) {
          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
          Text(
            text = if (isUrdu) "اشتہار جمع کروائیں" else "Submit Advertisement",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
