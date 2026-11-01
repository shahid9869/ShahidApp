package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AdDao
import com.example.data.dao.AppConfigDao
import com.example.data.dao.CategoryDao
import com.example.data.model.AdEntity
import com.example.data.model.AdStatus
import com.example.data.model.AdType
import com.example.data.model.AppConfigEntity
import com.example.data.dao.ChatDao
import com.example.data.model.CategoryEntity
import com.example.data.model.ChatMessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [AdEntity::class, CategoryEntity::class, AppConfigEntity::class, ChatMessageEntity::class],
  version = 2,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun adDao(): AdDao
  abstract fun categoryDao(): CategoryDao
  abstract fun appConfigDao(): AppConfigDao
  abstract fun chatDao(): ChatDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "karobar_ads_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateDatabase(database)
          }
        }
      }
    }

    suspend fun populateDatabase(database: AppDatabase) {
      val configDao = database.appConfigDao()
      val categoryDao = database.categoryDao()
      val adDao = database.adDao()

      // Default Admin Configs
      val configs = listOf(
        AppConfigEntity("admin_pin", "1234"),
        AppConfigEntity("auto_approve", "false"),
        AppConfigEntity("banner_active", "true"),
        AppConfigEntity("banner_title", "📢 خوش آمدید: کاروباری و سروسز اشتہارات"),
        AppConfigEntity("banner_message", "اپنے کاروبار یا سروس کا مفت اشتہار لگائیں اور ہزاروں گاہک حاصل کریں۔ تمام اشتہارات ایڈمن کی تصدیق کے بعد لائیو ہوتے ہیں۔"),
        AppConfigEntity("admin_whatsapp", "923001234567")
      )
      configDao.insertAll(configs)

      // Categories
      val categories = listOf(
        CategoryEntity("services_repair", "الیکٹریشن و ہوم ریپیئر", "Services & Repairs", "Build", "SERVICE"),
        CategoryEntity("wholesale_trade", "ہول سیل و گارمنٹس", "Wholesale & Garments", "Storefront", "BUSINESS"),
        CategoryEntity("auto_transport", "ٹرانسپورٹ و رینٹ اے کار", "Transport & Rentals", "DirectionsCar", "ALL"),
        CategoryEntity("property_realestate", "پراپرٹی و رئیل اسٹیٹ", "Property & Real Estate", "HomeWork", "ALL"),
        CategoryEntity("education_tuition", "اکیڈمی و ہوم ٹیوشن", "Academy & Tuition", "School", "SERVICE"),
        CategoryEntity("catering_events", "کیٹرنگ و شادی ایونٹس", "Catering & Events", "Restaurant", "BUSINESS"),
        CategoryEntity("it_printing", "پرنٹنگ و ڈیجیٹل سروسز", "Printing & IT", "Print", "ALL"),
        CategoryEntity("health_clinic", "کلینک و میڈیکل سروسز", "Medical & Health", "LocalHospital", "SERVICE")
      )
      categoryDao.insertAll(categories)

      // Sample Initial Ads (Approved & Pending for Admin review)
      val sampleAds = listOf(
        AdEntity(
          title = "ملک الیکٹریکل و اے سی انورٹر سروسز",
          businessName = "ملک ایئرکنڈیشننگ اینڈ الیکٹرانکس",
          category = "services_repair",
          type = AdType.SERVICE.name,
          ownerName = "محمد عاصم ملک",
          phone = "03001234567",
          whatsapp = "923001234567",
          city = "لاہور",
          address = "گلبرگ III، فیروزپور روڈ، لاہور",
          priceOrRate = "Rs 1,200 وزٹ چارجز",
          description = "ہر قسم کے ڈی سی انورٹر اے سی، ریفریجریٹر، ڈیپ فریزر اور واشنگ مشین کی پروفیشنل ریپیئرنگ اور انسٹالیشن۔ تجربہ کار ٹیکنیشنز کی فوری ہوم وزٹ سروس۔",
          status = AdStatus.APPROVED.name,
          isFeatured = true,
          viewsCount = 342,
          createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        AdEntity(
          title = "الحرم ٹیکسٹائل ہول سیل مارکیٹ",
          businessName = "الحرم کلاتھ مرچنٹس",
          category = "wholesale_trade",
          type = AdType.BUSINESS.name,
          ownerName = "حاجی طارق محمود",
          phone = "03217654321",
          whatsapp = "923217654321",
          city = "فیصل آباد",
          address = "گول بازار، کچہری بازار چوک، فیصل آباد",
          priceOrRate = "ہول سیل ریٹس دستیاب",
          description = "لیڈیز و جینٹس سوٹ، پیور لان، کاٹن، واش اینڈ ویئر، اور ایمبرائیڈری کلیکشن کے ہول سیل ڈیلرز۔ پورے پاکستان میں کارگو و کیش آن ڈیلیوری سہولت۔",
          status = AdStatus.APPROVED.name,
          isFeatured = true,
          viewsCount = 589,
          createdAt = System.currentTimeMillis() - 86400000L * 3
        ),
        AdEntity(
          title = "راولپنڈی اسلام آباد رینٹ اے کار سروس",
          businessName = "ٹوئن سٹیز کار رینٹل",
          category = "auto_transport",
          type = AdType.SERVICE.name,
          ownerName = "چوہدری رضوان",
          phone = "03335558899",
          whatsapp = "923335558899",
          city = "اسلام آباد",
          address = "بلیو ایریا، اسلام آباد و صدر راولپنڈی",
          priceOrRate = "Rs 4,000 فی دن سے",
          description = "کرولا، گرینڈی، یارس، آلٹو اور پراڈو مع تجربہ کار ڈرائیور اور سیلف ڈرائیو۔ ائیرپورٹ پک اینڈ ڈراپ اور ٹورز پیکجز دستیاب ہیں۔",
          status = AdStatus.APPROVED.name,
          isFeatured = false,
          viewsCount = 215,
          createdAt = System.currentTimeMillis() - 86400000L
        ),
        AdEntity(
          title = "مدینہ پرنٹنگ پریس و فلیکس بینرز",
          businessName = "مدینہ گرافکس اینڈ پبلشرز",
          category = "it_printing",
          type = AdType.BUSINESS.name,
          ownerName = "شاہد اقبال",
          phone = "03459988776",
          whatsapp = "923459988776",
          city = "کراچی",
          address = "اردو بازار، ایم اے جناح روڈ، کراچی",
          priceOrRate = "Rs 25 فی اسکوائر فٹ",
          description = "بزنس کارڈز، بروشرز، فلیکس بینرز، شاپ سائن بورڈز، بل بکس اور تشہیری سامان کی ہائی کوالٹی ڈیجیٹل پرنٹنگ۔ سستی اور بروقت ڈلیوری۔",
          status = AdStatus.APPROVED.name,
          isFeatured = false,
          viewsCount = 178,
          createdAt = System.currentTimeMillis() - 86400000L * 4
        ),
        AdEntity(
          title = "المصطفیٰ ہوم ٹیوشن و کیلیگرافی اکیڈمی",
          businessName = "المصطفیٰ ایجوکیشنل نیٹ ورک",
          category = "education_tuition",
          type = AdType.SERVICE.name,
          ownerName = "پروفیسر احمد ندیم",
          phone = "03124443322",
          whatsapp = "923124443322",
          city = "ملتان",
          address = "بوسن روڈ، گلگشت کالونی، ملتان",
          priceOrRate = "Rs 5,000 ماہانہ",
          description = "میٹرک، ایف ایس سی، او / اے لیول، اور قرآن پاک ترجمہ و کیلیگرافی کے لیے تجربہ کار مرد و خاتون اساتذہ۔ ہوم وزٹ اور زوم آن لائن کلاسز۔",
          status = AdStatus.APPROVED.name,
          isFeatured = false,
          viewsCount = 132,
          createdAt = System.currentTimeMillis() - 86400000L * 5
        ),
        // Pending Ad 1 - for Admin to approve/reject
        AdEntity(
          title = "خان پلمبنگ و سینیٹری ایمرجنسی سروس",
          businessName = "خان پلمبنگ ورکس",
          category = "services_repair",
          type = AdType.SERVICE.name,
          ownerName = "امان اللہ خان",
          phone = "03341122334",
          whatsapp = "923341122334",
          city = "پشاور",
          address = "حیات آباد فیز 3، پشاور",
          priceOrRate = "Rs 800 وزٹ",
          description = "پانی کی موٹر کی وائنڈنگ، گیزر ریپیئر، زیر زمین پائپ لیکیج ڈیٹیکشن اور تمام سینیٹری کام۔ ایمرجنسی میں 30 منٹ کے اندر پہنچنے کی سہولت۔",
          status = AdStatus.PENDING.name,
          isFeatured = false,
          viewsCount = 0,
          createdAt = System.currentTimeMillis() - 3600000L * 2
        ),
        // Pending Ad 2 - for Admin to approve/reject
        AdEntity(
          title = "سپر کار واش و سیرامک کوٹنگ ورکشاپ",
          businessName = "سپر شائن آٹو کیئر",
          category = "auto_transport",
          type = AdType.BUSINESS.name,
          ownerName = "سردار بلال",
          phone = "03029990011",
          whatsapp = "923029990011",
          city = "لاہور",
          address = "مین کینال روڈ، مغل پورہ، لاہور",
          priceOrRate = "Rs 15,000 سے پیکجز",
          description = "گاڑیوں کی ڈیٹیلنگ، پینٹ پروٹیکشن فلم (PPF)، 9H سیرامک کوٹنگ اور انٹیرئیر کلیننگ کا جدید آٹومیٹک اسٹوڈیو۔ 2 سال کی آفیشل وارنٹی۔",
          status = AdStatus.PENDING.name,
          isFeatured = false,
          viewsCount = 0,
          createdAt = System.currentTimeMillis() - 3600000L * 5
        )
      )
      adDao.insertAll(sampleAds)

      val chatDao = database.chatDao()
      val sampleChat = listOf(
        ChatMessageEntity(
          senderRole = "ADMIN",
          senderName = "ایڈمن سپورٹ",
          messageType = "TEXT",
          textContent = "السلام علیکم! کاروبار و سروسز ایڈمن سپورٹ میں خوش آمدید۔ آپ کا کوئی بھی سوال، اشتہار کی رہنمائی یا تصدیق کے لیے میسج، تصویر یا آڈیو بھیج سکتے ہیں۔",
          timestamp = System.currentTimeMillis() - 7200000L,
          isRead = true
        ),
        ChatMessageEntity(
          senderRole = "USER",
          senderName = "کسٹمر",
          messageType = "TEXT",
          textContent = "وعلیکم السلام! میں نے اپنے ہول سیل کاروبار کا اشتہار پوسٹ کیا ہے۔ کیا مجھے اس کی تصدیق اور نمایاں کروانے کا طریقہ بتا سکتے ہیں؟",
          timestamp = System.currentTimeMillis() - 3600000L,
          isRead = false
        )
      )
      chatDao.insertAll(sampleChat)
    }
  }
}
