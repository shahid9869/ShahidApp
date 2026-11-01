package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdEntity
import com.example.data.model.AdStatus
import com.example.data.model.AdType
import com.example.data.model.AppConfigEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MessageRole
import com.example.data.model.MessageType
import com.example.data.repository.KarobarRepository
import com.example.util.AudioPlayerHelper
import com.example.util.AudioRecorderHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
  object Home : Screen()
  data class AdDetail(val adId: Long) : Screen()
  object PostAd : Screen()
  object AdminLogin : Screen()
  object AdminDashboard : Screen()
  object CustomerChat : Screen()
}

data class PostAdFormState(
  val title: String = "",
  val businessName: String = "",
  val category: String = "services_repair",
  val type: String = AdType.SERVICE.name,
  val ownerName: String = "",
  val phone: String = "",
  val whatsapp: String = "",
  val city: String = "لاہور",
  val address: String = "",
  val priceOrRate: String = "",
  val description: String = "",
  val isSubmitting: Boolean = false,
  val errorMessage: String? = null,
  val successMessage: String? = null
)

class KarobarViewModel(
  private val repository: KarobarRepository
) : ViewModel() {

  // Current Navigation Screen
  private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

  // Navigation History Stack for BackHandler
  private val screenStack = mutableListOf<Screen>(Screen.Home)

  // Language: true for Urdu, false for English
  private val _isUrdu = MutableStateFlow(true)
  val isUrdu: StateFlow<Boolean> = _isUrdu.asStateFlow()

  // User Search & Filters
  val searchQuery = MutableStateFlow("")
  val selectedCategoryId = MutableStateFlow<String?>(null)
  val selectedType = MutableStateFlow<String?>("ALL") // "ALL", "BUSINESS", "SERVICE"
  val selectedCity = MutableStateFlow<String?>("تمام شہر")

  // Bookmarks / Saved Ads
  private val _bookmarkedAdIds = MutableStateFlow<Set<Long>>(emptySet())
  val bookmarkedAdIds: StateFlow<Set<Long>> = _bookmarkedAdIds.asStateFlow()

  // Post Ad Form
  val postAdForm = MutableStateFlow(PostAdFormState())

  // Admin Security & Portal State
  private val _isAdminAuthenticated = MutableStateFlow(false)
  val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

  val adminPinInput = MutableStateFlow("")
  val adminPinError = MutableStateFlow<String?>(null)
  val adminSelectedTab = MutableStateFlow(0) // 0: Pending, 1: All Ads, 2: Chat, 3: Banner, 4: Categories, 5: Settings

  // Reject Dialog
  val rejectingAdId = MutableStateFlow<Long?>(null)
  val rejectionReasonText = MutableStateFlow("")

  // Banner Edit State for Admin
  val editBannerTitle = MutableStateFlow("")
  val editBannerMessage = MutableStateFlow("")
  val editBannerActive = MutableStateFlow(true)

  // Password / Settings edit for Admin
  val newAdminPin = MutableStateFlow("")
  val autoApproveToggle = MutableStateFlow(false)
  val settingsMessage = MutableStateFlow<String?>(null)

  // Direct Customer <-> Admin Chat State
  val customerChatInputText = MutableStateFlow("")
  val adminChatInputText = MutableStateFlow("")

  // Voice recording & playback helpers
  private var recorderHelper: AudioRecorderHelper? = null
  val playerHelper = AudioPlayerHelper()

  val isRecordingVoice = MutableStateFlow(false)
  val recordingDurationSec = MutableStateFlow(0)
  private var timerJob: Job? = null

  val currentPlayingAudioPath = MutableStateFlow<String?>(null)
  val currentAudioProgressSec = MutableStateFlow(0)
  val currentAudioTotalSec = MutableStateFlow(0)

  // Chat stream
  val chatMessages = repository.allChatMessages.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val unreadUserMessagesCount = repository.unreadUserMessagesCount.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    0
  )

  // Data streams from repository
  val approvedAds = repository.approvedAds.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val featuredAds = repository.featuredAds.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val pendingAds = repository.pendingAds.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val allAds = repository.allAds.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val categories = repository.allCategories.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  val configs = repository.allConfigs.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptyList()
  )

  // Filtered approved ads for Home screen
  val filteredAds: StateFlow<List<AdEntity>> = combine(
    approvedAds,
    searchQuery,
    selectedCategoryId,
    selectedType,
    selectedCity
  ) { ads, query, catId, type, city ->
    ads.filter { ad ->
      val matchesQuery = query.isBlank() ||
          ad.title.contains(query, ignoreCase = true) ||
          ad.businessName.contains(query, ignoreCase = true) ||
          ad.description.contains(query, ignoreCase = true) ||
          ad.city.contains(query, ignoreCase = true)

      val matchesCategory = catId == null || catId == "all" || ad.category == catId
      val matchesType = type == null || type == "ALL" || ad.type == type
      val matchesCity = city == null || city == "تمام شہر" || city == "All Cities" || ad.city == city

      matchesQuery && matchesCategory && matchesType && matchesCity
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      configs.collect { configList ->
        configList.forEach { config ->
          when (config.configKey) {
            "banner_title" -> if (editBannerTitle.value.isEmpty()) editBannerTitle.value = config.configValue
            "banner_message" -> if (editBannerMessage.value.isEmpty()) editBannerMessage.value = config.configValue
            "banner_active" -> editBannerActive.value = config.configValue.toBoolean()
            "auto_approve" -> autoApproveToggle.value = config.configValue.toBoolean()
          }
        }
      }
    }
  }

  // Navigation
  fun navigateTo(screen: Screen) {
    screenStack.add(screen)
    _currentScreen.value = screen
    if (screen is Screen.AdminDashboard) {
      markUserMessagesRead()
    }
  }

  fun navigateBack(): Boolean {
    stopAudioPlayback()
    if (screenStack.size > 1) {
      screenStack.removeAt(screenStack.lastIndex)
      _currentScreen.value = screenStack.last()
      return true
    }
    return false
  }

  fun toggleLanguage() {
    _isUrdu.value = !_isUrdu.value
  }

  fun toggleBookmark(adId: Long) {
    val current = _bookmarkedAdIds.value.toMutableSet()
    if (current.contains(adId)) {
      current.remove(adId)
    } else {
      current.add(adId)
    }
    _bookmarkedAdIds.value = current
  }

  fun recordAdView(adId: Long) {
    viewModelScope.launch {
      repository.incrementViews(adId)
    }
  }

  // Chat Actions
  fun sendTextMessage(role: MessageRole, text: String) {
    if (text.isBlank()) return
    viewModelScope.launch {
      val msg = ChatMessageEntity(
        senderRole = role.name,
        senderName = if (role == MessageRole.ADMIN) "ایڈمن سپورٹ" else "کسٹمر",
        messageType = MessageType.TEXT.name,
        textContent = text.trim(),
        timestamp = System.currentTimeMillis(),
        isRead = role == MessageRole.ADMIN
      )
      repository.sendChatMessage(msg)
      if (role == MessageRole.USER) {
        customerChatInputText.value = ""
      } else {
        adminChatInputText.value = ""
      }
    }
  }

  fun sendImageMessage(role: MessageRole, imagePath: String, caption: String = "") {
    viewModelScope.launch {
      val msg = ChatMessageEntity(
        senderRole = role.name,
        senderName = if (role == MessageRole.ADMIN) "ایڈمن سپورٹ" else "کسٹمر",
        messageType = MessageType.IMAGE.name,
        textContent = caption.trim(),
        mediaPath = imagePath,
        timestamp = System.currentTimeMillis(),
        isRead = role == MessageRole.ADMIN
      )
      repository.sendChatMessage(msg)
    }
  }

  fun sendAudioMessage(role: MessageRole, audioPath: String, durationSec: Int) {
    viewModelScope.launch {
      val msg = ChatMessageEntity(
        senderRole = role.name,
        senderName = if (role == MessageRole.ADMIN) "ایڈمن سپورٹ" else "کسٹمر",
        messageType = MessageType.AUDIO.name,
        mediaPath = audioPath,
        audioDurationSec = durationSec,
        timestamp = System.currentTimeMillis(),
        isRead = role == MessageRole.ADMIN
      )
      repository.sendChatMessage(msg)
    }
  }

  fun deleteChatMessage(id: Long) {
    viewModelScope.launch {
      repository.deleteChatMessage(id)
    }
  }

  fun markUserMessagesRead() {
    viewModelScope.launch {
      repository.markUserMessagesAsRead()
    }
  }

  // Voice recording methods
  fun startVoiceRecording(context: Context): Boolean {
    stopAudioPlayback()
    val helper = AudioRecorderHelper(context)
    val started = helper.startRecording()
    if (started) {
      recorderHelper = helper
      isRecordingVoice.value = true
      recordingDurationSec.value = 0
      timerJob = viewModelScope.launch {
        while (isRecordingVoice.value) {
          delay(1000)
          recordingDurationSec.value += 1
        }
      }
    }
    return started
  }

  fun stopAndSendVoiceRecording(role: MessageRole) {
    timerJob?.cancel()
    timerJob = null
    val result = recorderHelper?.stopRecording()
    isRecordingVoice.value = false
    val sec = recordingDurationSec.value
    recordingDurationSec.value = 0
    recorderHelper = null

    if (result != null) {
      val (path, duration) = result
      sendAudioMessage(role, path, duration.coerceAtLeast(sec))
    }
  }

  fun cancelVoiceRecording() {
    timerJob?.cancel()
    timerJob = null
    recorderHelper?.cancelRecording()
    recorderHelper = null
    isRecordingVoice.value = false
    recordingDurationSec.value = 0
  }

  // Audio playback methods
  fun toggleAudioPlayback(filePath: String) {
    if (playerHelper.isPlaying(filePath)) {
      stopAudioPlayback()
    } else {
      currentPlayingAudioPath.value = filePath
      playerHelper.playAudio(
        filePath = filePath,
        onProgress = { current, total ->
          currentAudioProgressSec.value = current
          currentAudioTotalSec.value = total
        },
        onFinished = {
          currentPlayingAudioPath.value = null
          currentAudioProgressSec.value = 0
          currentAudioTotalSec.value = 0
        }
      )
    }
  }

  fun stopAudioPlayback() {
    playerHelper.stopAudio()
    currentPlayingAudioPath.value = null
    currentAudioProgressSec.value = 0
    currentAudioTotalSec.value = 0
  }

  override fun onCleared() {
    super.onCleared()
    stopAudioPlayback()
    cancelVoiceRecording()
  }

  // User Post Ad
  fun updatePostFormField(update: PostAdFormState.() -> PostAdFormState) {
    postAdForm.value = postAdForm.value.update()
  }

  fun submitAd() {
    val form = postAdForm.value
    if (form.title.isBlank()) {
      postAdForm.value = form.copy(errorMessage = if (_isUrdu.value) "براہ کرم اشتہار کا عنوان درج کریں" else "Please enter ad title")
      return
    }
    if (form.phone.isBlank()) {
      postAdForm.value = form.copy(errorMessage = if (_isUrdu.value) "رابطہ نمبر درج کرنا ضروری ہے" else "Phone number is required")
      return
    }
    if (form.description.isBlank()) {
      postAdForm.value = form.copy(errorMessage = if (_isUrdu.value) "تفصیلات درج کریں" else "Please enter ad description")
      return
    }

    viewModelScope.launch {
      postAdForm.value = form.copy(isSubmitting = true, errorMessage = null)
      val autoApprove = repository.getConfigDirect("auto_approve")?.toBoolean() ?: false
      val initialStatus = if (autoApprove) AdStatus.APPROVED.name else AdStatus.PENDING.name

      val newAd = AdEntity(
        title = form.title.trim(),
        businessName = if (form.businessName.isNotBlank()) form.businessName.trim() else form.title.trim(),
        category = form.category,
        type = form.type,
        ownerName = if (form.ownerName.isNotBlank()) form.ownerName.trim() else "User",
        phone = form.phone.trim(),
        whatsapp = if (form.whatsapp.isNotBlank()) form.whatsapp.trim() else form.phone.trim(),
        city = form.city,
        address = form.address.trim(),
        priceOrRate = if (form.priceOrRate.isNotBlank()) form.priceOrRate.trim() else "رابطہ کریں",
        description = form.description.trim(),
        status = initialStatus,
        isFeatured = false,
        viewsCount = 0
      )

      repository.insertAd(newAd)

      val successMsg = if (autoApprove) {
        if (_isUrdu.value) "مبارک ہو! آپ کا اشتہار فوری طور پر لائیو ہو چکا ہے۔" else "Your ad is now live!"
      } else {
        if (_isUrdu.value) "آپ کا اشتہار کامیابی سے جمع ہو گیا ہے۔ ایڈمن کی جانچ کے بعد فوری منظور ہو جائے گا۔" else "Ad submitted successfully! It will be reviewed by Admin shortly."
      }

      postAdForm.value = PostAdFormState(successMessage = successMsg)
    }
  }

  fun resetPostForm() {
    postAdForm.value = PostAdFormState()
  }

  // Admin Authentication
  fun loginAdmin() {
    viewModelScope.launch {
      val actualPin = repository.getConfigDirect("admin_pin") ?: "1234"
      if (adminPinInput.value == actualPin) {
        _isAdminAuthenticated.value = true
        adminPinError.value = null
        adminPinInput.value = ""
        navigateTo(Screen.AdminDashboard)
      } else {
        adminPinError.value = if (_isUrdu.value) "غلط پن کوڈ! دوبارہ کوشش کریں (پہلے سے طے شدہ: 1234)" else "Incorrect PIN! Try again (Default: 1234)"
      }
    }
  }

  fun logoutAdmin() {
    _isAdminAuthenticated.value = false
    navigateTo(Screen.Home)
  }

  // Admin Ad Actions
  fun approveAd(id: Long) {
    viewModelScope.launch {
      repository.approveAd(id)
    }
  }

  fun promptRejectAd(id: Long) {
    rejectingAdId.value = id
    rejectionReasonText.value = if (_isUrdu.value) "معلومات نا مکمل ہیں یا اصولوں کے مطابق نہیں" else "Incomplete details or violates ad policies"
  }

  fun confirmRejectAd() {
    val id = rejectingAdId.value ?: return
    viewModelScope.launch {
      repository.rejectAd(id, rejectionReasonText.value)
      rejectingAdId.value = null
      rejectionReasonText.value = ""
    }
  }

  fun cancelRejectAd() {
    rejectingAdId.value = null
    rejectionReasonText.value = ""
  }

  fun toggleFeatured(id: Long, current: Boolean) {
    viewModelScope.launch {
      repository.toggleFeatured(id, !current)
    }
  }

  fun deleteAd(id: Long) {
    viewModelScope.launch {
      repository.deleteAd(id)
    }
  }

  // Admin Banner Actions
  fun saveBannerSettings() {
    viewModelScope.launch {
      repository.setConfig("banner_title", editBannerTitle.value)
      repository.setConfig("banner_message", editBannerMessage.value)
      repository.setConfig("banner_active", editBannerActive.value.toString())
      settingsMessage.value = if (_isUrdu.value) "بینر کی ترتیبات محفوظ ہو گئیں!" else "Banner settings saved!"
    }
  }

  // Admin Settings
  fun changeAdminPin() {
    if (newAdminPin.value.length < 4) {
      settingsMessage.value = if (_isUrdu.value) "پن کم از کم 4 ہندسوں پر مشتمل ہونا چاہیے" else "PIN must be at least 4 digits"
      return
    }
    viewModelScope.launch {
      repository.setConfig("admin_pin", newAdminPin.value)
      newAdminPin.value = ""
      settingsMessage.value = if (_isUrdu.value) "نیا ایڈمن پن کامیابی سے تبدیل ہو گیا!" else "Admin PIN updated successfully!"
    }
  }

  fun toggleAutoApprove(enabled: Boolean) {
    autoApproveToggle.value = enabled
    viewModelScope.launch {
      repository.setConfig("auto_approve", enabled.toString())
      settingsMessage.value = if (_isUrdu.value) {
        if (enabled) "خودکار منظوری فعال کر دی گئی" else "اب تمام اشتہارات ایڈمن کی منظوری کے بعد لائیو ہوں گے"
      } else {
        if (enabled) "Auto-approve enabled" else "Manual Admin review enabled"
      }
    }
  }

  fun clearSettingsMessage() {
    settingsMessage.value = null
  }

  fun toggleCategory(categoryId: String, isActive: Boolean) {
    viewModelScope.launch {
      repository.toggleCategory(categoryId, !isActive)
    }
  }

  fun addNewCategory(id: String, urdu: String, eng: String, type: String) {
    viewModelScope.launch {
      val cat = CategoryEntity(
        id = id.lowercase().replace(" ", "_"),
        nameUrdu = urdu,
        nameEnglish = eng,
        iconName = "Category",
        adType = type,
        isActive = true
      )
      repository.addCategory(cat)
    }
  }
}

class KarobarViewModelFactory(
  private val repository: KarobarRepository
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(KarobarViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return KarobarViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
