# 🛡️ SafeNest — Intelligent Personal Safety & Emergency Response

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)]()
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)]()
[![Backend](https://img.shields.io/badge/Backend-Firebase-orange.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg)]()

SafeNest is a **production-level Android personal safety application** engineered to provide **proactive protection and rapid emergency response**.

Unlike traditional safety apps that require manual interaction, SafeNest integrates:

- 🔘 Hardware-level SOS triggers  
- 📍 Intelligent geofencing  
- ☁️ Real-time cloud synchronization  

This ensures help is always accessible — even when the phone is locked.

> ⚡ In emergencies, seconds matter. SafeNest eliminates friction between danger and response.

---

## 📖 Table of Contents

- [🚨 Problem Statement](#-problem-statement)
- [✨ Core Features](#-core-features)
- [🏗 Architecture & Design Decisions](#-architecture--design-decisions)
- [🛠 Tech Stack & Engineering Rationale](#-tech-stack--engineering-rationale)
- [📂 Project Structure](#-project-structure)
- [🧠 Advanced Android Concepts](#-advanced-android-concepts)
- [⚙️ Installation & Setup](#️-installation--setup)
- [⚡ Performance & Security](#-performance--security)
- [🧪 Testing Strategy](#-testing-strategy)
- [⚠️ Known Limitations](#️-known-limitations)
- [🗺 Future Roadmap](#-future-roadmap)
- [🤝 Contribution](#-contribution)
- [📄 License](#-license)

---

## 🚨 Problem Statement

In critical situations, users often cannot:

- Unlock their phone  
- Open an app  
- Navigate complex UI  
- Tap emergency buttons  

Traditional safety apps rely heavily on manual interaction — which fails under stress.

**SafeNest solves this by enabling hardware-triggered emergency response**, passive monitoring, and automated alerts — ensuring assistance is accessible even when the user cannot interact with their device.

---

## ✨ Core Features

### 1️⃣ Hardware SOS Trigger (Power Button)

- Trigger emergency siren by pressing the power button **4 times**
- Works even when the device is locked
- Implemented via `BroadcastReceiver`
- Near-zero latency response
- Background-safe execution

**Why this approach?**  
Hardware triggers eliminate UI dependency and drastically reduce response time in emergencies.

---

### 2️⃣ Intelligent Geofencing

- Safety zone monitoring using Google Geofencing API
- Entry & exit notifications
- Dynamic geofence sync from Firebase Firestore
- Dwell-time filtering to prevent boundary “ping-pong” alerts

**Engineering Tradeoff:**  
Balanced battery consumption vs location accuracy using adaptive priority switching.

---

### 3️⃣ Safety Education Suite

- Self-defense guides  
- Legal awareness resources  
- Safety best practices  
- BottomSheet quick-access UI  
- Card-based modular layout  

Designed for contextual learning without disrupting the main app flow.

---

### 4️⃣ Emergency Contact Management

- Trusted contact list  
- Alert dispatch on SOS trigger  
- Firebase-backed storage  
- Real-time synchronization  

---

## 🏗 Architecture & Design Decisions

SafeNest follows a **modular, service-oriented Android architecture**.

### 🔍 Why Service-Heavy?

Android aggressively kills background apps to optimize memory.

For a safety app:

> **Reliability > Abstraction**

Core safety components use persistent foreground services:

- `SirenService`
- `GeofencingService`
- System-level `BroadcastReceivers`

This ensures triggers remain active even when the UI is destroyed.

---

### 🧩 UI Architecture

- Single-Activity architecture  
- Fragment-based navigation  
- Jetpack Navigation Component  
- ViewBinding for null safety  

Benefits:

- Predictable backstack behavior  
- Reduced memory leaks  
- Lifecycle-safe UI handling  

---

## 🛠 Tech Stack & Engineering Rationale

| Layer | Technology | Why It Was Chosen |
|--------|------------|------------------|
| Language | Kotlin | Null safety, concise syntax, modern Android standard |
| UI | XML + Material Design | High performance & consistent UI rendering |
| Navigation | Jetpack Navigation | Structured backstack & modular transitions |
| Backend | Firebase Firestore/Auth | Real-time sync without backend maintenance |
| Location | Google Maps + Geofencing API | High-accuracy location monitoring |
| Async | Firebase Tasks/Callbacks | Event-driven real-time model |
| Services | Foreground Services | Process survival under memory pressure |
| Image Loading | Glide | Memory-efficient and optimized image loading |
| Storage | Firestore | Dynamic geofence & contacts sync |
| Permissions | Android 14+ APIs | Modern background location compliance |

---

## 📂 Project Structure


com.example.safenest  
│  
├── adapters/ # RecyclerView adapters  
├── dialogs/ # BottomSheet educational modules  
├── models/ # Firebase data models  
├── receiver/ # Hardware & system listeners  
├── service/ # Foreground services  
│  
├── MainActivity.kt  
├── HomeFragment.kt  
└── education.kt

  

---

## 🧠 Advanced Android Concepts

- Foreground Services for persistence  
- BroadcastReceiver intent filtering  
- Geofence dwell-time optimization  
- Background location permission handling (Android 14+)  
- Audio focus management  
- Dynamic Firestore synchronization  
- Fragment lifecycle management  
- Battery-aware location prioritization  

---

## ⚙️ Installation & Setup

### 1️⃣ Clone Repository

```bash
git clone https://github.com/yourusername/SafeNest.git
```
* * *

## 2️⃣ Firebase Setup

1.  Create Firebase project
    
2.  Enable Firestore
    
3.  Enable Authentication
    
4.  Add `google-services.json` to `app/`
    

* * *

## 3️⃣ Google Maps API

Add API key to:

local.properties

or

AndroidManifest.xml

* * *

## 4️⃣ Run App

-   Open in Android Studio
    
-   Sync Gradle
    
-   Run on physical device (recommended)
    

Geofencing and hardware triggers may not work reliably on emulator.

* * *

# ⚡ Performance & Security Considerations

### Battery Optimization

-   Balanced location priority by default
    
-   High accuracy only during SOS
    
-   Adaptive monitoring
    

### Permission Handling

-   Background location permissions
    
-   Android 14+ compliant flows
    
-   User transparency
    

### Audio Focus

Siren overrides media audio using managed focus control.

### Process Survival

Foreground services ensure core safety features persist even under memory pressure.

* * *

# 🧪 Testing Strategy

-   Manual real-device testing for geofencing
    
-   Background service validation
    
-   Power button trigger stress testing
    
-   Permission denial scenarios
    
-   Lifecycle testing under process kill
    

* * *

# ⚠️ Known Limitations

-   Power-button detection may vary by OEM
    
-   Aggressive battery savers may restrict services
    
-   Requires location permissions
    
-   Works best on stock Android
    

* * *

# 🗺 Future Roadmap

-    AI scream detection
    
-    Community safety heatmaps
    
-    Wear OS support
    
-    Offline safety docs via Room
    
-    SMS-based SOS fallback
    
-    End-to-end encrypted alerts
