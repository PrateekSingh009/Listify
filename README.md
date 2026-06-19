# Listify - Premium Expense Tracker

![Listify Banner](https://placehold.co/1200x400/1B5548/FFFFFF/png?text=Listify&font=playfair+display) 

[![Platform](https://img.shields.io/badge/Platform-Android-green)](https://developer.android.com)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)](https://developer.android.com/jetpack/compose)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blueviolet)](https://kotlinlang.org)
[![Gemini](https://img.shields.io/badge/AI-Gemini%20Nano%20%2F%20ML%20Kit-orange)](https://deepmind.google/technologies/gemini/#introduction)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

**Listify** is a premium, fintech-inspired expense management application built entirely natively for Android using Jetpack Compose. Adopting a sleek, high-end design pattern with warm cream backgrounds and rich teal typography, Listify transforms standard ledger sheets into structured, intelligent data clusters. Equipped with automated notification-based payment parsing and local AI OCR engines, Listify lets you naturally "pencil it in" while handling the rest automatically.

---

## Why Listify? 🚀

* **100% Local AI Intelligence**: Parses receipts right on your device hardware using local ML Kit processing text extractors combined with Google's on-device **Gemini Nano** engine. No private data ever hits a remote cloud server.
* **Ambient Notification Interception**: Captures incoming dynamic SMS and app transaction pushes (such as GPay, PhonePe, and Paytm) locally to pre-build transactions before you even unlock your phone.
* **Fintech-First Design Framework**: High-end UX utilizing `Playfair Display` for bold typographic headers alongside strict fluid layout grids constructed with `DM Sans`.
* **Structured Cluster Architectures**: Groups lists inside customizable overarching logical categories to keep personal, business, family, and medical ledgers strictly isolated.

---

## System Screens & Interfaces 📱

### 1. Unified Management Hub (Dashboard)
* **Sticky Navigation Banner**: Vertical Deep Teal gradient top panel containing user context profiles and the floating structural primary call-to-action button (**Add to the Book**).
* **Live Stat Counter Matrices**: Quick-glance contextual metric row detailing active structural data nodes (`Clusters Count`, `Dynamic Categories`, `Global Ledger Items`).
* **Interception Prompts**: Suspended, low-alpha contextual cards prompting immediate inclusion of automatically grabbed foreground background push data.

### 2. Deep Ledger Metrics (Category Ledger View)
* **Budget Metrics Visualizers**: Inline sliding trackers comparing real-time spend vectors to preset bounds, shifting dynamically to warnings or alert limits instantly if thresholds cross.
* **Dynamic Content Toggles**: Flip-switches displaying granular elements ordered by structural index variations (**Linear Calendar Dates** vs. **Normalized Structural Titles**).
* **Sliding Quick Menus**: Micro-interactions sliding from card edges via spring curves ($280\text{ms}$ FastOutSlowIn acceleration) to modify records on the fly.

### 3. Captured Payment Influx Ledger (Interceptions Tracker)
* **Self-Expiring Buffers**: Temporary incoming notifications remain staged for a 7-day floating lifetime window backed by animated auto-draining status bars.
* **Verified Ledger Transformations**: Fully processed rows settle into zero-elevation disabled visibility layouts to maintain an ongoing history of captured imports.

### 4. Edge-Processed AI OCR View (Receipt Engine)
* **Live Execution Stacks**: Visual milestone tickers explicitly verifying running on-device AI tasks sequentially (*ML Kit Character Dump* $\rightarrow$ *Gemini Itemization Structure Extraction*).
* **Contextual Selective Bounding Box Items**: Checkboxes mapping detected item arrays, dynamically computing aggregate tallies prior to committing items into ledger books.

---

## Technical Stack 🔧

* **UI Layer**: Jetpack Compose (Declarative UI layout system built entirely with modern state management principles)
* **Architecture Style**: MVVM (Model-View-ViewModel) + Clean Architecture pattern mapping explicit single-direction streams
* **Local Intelligence Core**: Google ML Kit Text Recognition Engine + On-Device Gemini Nano Architecture
* **Underlying Database Engine**: Room Persistence Architecture leveraging multi-table relations and integrated SQL indexing
* **Dependency Delivery Framework**: Hilt (JSR-330 compliant compile-time safe injection matrices)
* **Reactive Coroutine Streams**: Kotlin StateFlow architecture running asynchronous background data streams

---

## Installation & Setup 🛠️

1. **Clone the Repository**:
   ```bash
   git clone [https://github.com/yourusername/listify.git](https://github.com/yourusername/listify.git)
   cd listify

2. **Android Studio Configurations**:
   * Open Android Studio (Ladybug or later version required).
   * Verify that your target JDK profile is configured to utilize Java 17 runtimes.
   * Sync the Gradle project files.

3. **Deploying the Application**:
   * Attach a physical device or power on an emulator running API level 24 (Android 7.0 Nougat) or later.
   * Ensure Google Play Services are up to date on your target runtimes to permit Local ML Kit operation.
   * Hit execution run (`Shift + F10`).

---

## Contributing 🤝

1. Fork the project repository.
2. Spin up a designated local workspace branch: `git checkout -b feature/AmazingFeature`.
3. Save structured atomic revisions: `git commit -m 'Add some AmazingFeature'`.
4. Upload variations to upstream configurations: `git push origin feature/AmazingFeature`.
5. Lodge an official **Pull Request**.

---

## License 📄

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
