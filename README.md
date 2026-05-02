# 🌾 Halli Santhe Digital

A digital marketplace app for rural artisans and farmers to showcase and sell their handcrafted products, agricultural goods, and local specialties directly to consumers — bridging the gap between rural talent and urban markets.

> **Halli Santhe** means "Village Market" in Kannada.

---

## ✨ Features

- **📱 Phone OTP Authentication** — Secure login via Firebase Phone Auth
- **🛍️ Product Listing** — Browse products across categories like Handicrafts, Pottery, Textiles, Spices & Food, Jewellery, and more
- **📸 Upload Products** — Sellers can photograph and list products with image compression
- **🤖 AI-Powered Descriptions** — Generate compelling product descriptions using Google Gemini AI
- **🔍 Search & Filter** — Browse by category chips or search by product name
- **👤 Seller Profiles** — Each user has a profile linked to their listings
- **📞 Direct Contact** — Buyers can directly call or WhatsApp sellers

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI** | XML Layouts + Material Design Components |
| **Auth** | Firebase Phone Authentication |
| **Database** | Cloud Firestore |
| **Storage** | Firebase Cloud Storage |
| **AI** | Google Gemini Generative AI |
| **Images** | Glide + Custom Image Compression |
| **Build** | Gradle 8.5 + AGP 8.1.1 |

---

## 📂 Project Structure

```
app/src/main/java/com/halliSanthe/app/
├── activities/
│   ├── SplashActivity.kt        # App launch screen
│   ├── LoginActivity.kt         # Phone OTP login
│   ├── HomeActivity.kt          # Main screen with bottom navigation
│   ├── UploadProductActivity.kt # Product listing form + AI description
│   └── ProductDetailActivity.kt # Product detail view
├── fragments/
│   ├── HomeFragment.kt          # Featured products feed
│   ├── BrowseFragment.kt        # Category-based browsing
│   ├── MyProductsFragment.kt    # Seller's own listings
│   └── ProfileFragment.kt      # User profile
├── adapters/
│   └── ProductAdapter.kt       # RecyclerView adapter for product cards
├── models/
│   └── Product.kt              # Product data class
└── utils/
    ├── GeminiHelper.kt          # Gemini AI integration
    └── ImageCompressHelper.kt   # Image compression utility
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Hedgehog or later)
- **JDK 17+**
- **Firebase project** with Phone Auth, Firestore, and Storage enabled

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/HalliSantheDigital.git
   cd HalliSantheDigital
   ```

2. **Firebase Configuration**
   - Create a project at [Firebase Console](https://console.firebase.google.com/)
   - Enable **Phone Authentication**, **Cloud Firestore**, and **Cloud Storage**
   - Download `google-services.json` and place it in the `app/` directory

3. **Firestore Indexes** — Create these composite indexes:

   | Collection | Field 1 | Field 2 |
   |-----------|---------|---------|
   | `products` | `sellerId` ↑ | `timestamp` ↓ |
   | `products` | `category` ↑ | `timestamp` ↓ |

4. **Gemini AI Key**
   - Get an API key from [Google AI Studio](https://aistudio.google.com/apikey)
   - Update the key in `GeminiHelper.kt`

5. **Build & Run**
   - Open the project in Android Studio
   - Sync Gradle and run on a device/emulator

---

## 📸 App Screens

| Splash | Login | Home | Browse | Upload |
|--------|-------|------|--------|--------|
| Launch screen | OTP verification | Featured products | Category filters | AI descriptions |

---

## 🔮 Future Roadmap

- [ ] In-app payment integration (UPI / Razorpay)
- [ ] Order management system
- [ ] Multi-language support (Kannada, Hindi, English)
- [ ] Push notifications for new products
- [ ] Seller ratings and reviews
- [ ] Location-based product discovery

---

## 📄 License

This project is for educational purposes.

---

<p align="center">
  Made with ❤️ for rural India 🇮🇳
</p>
