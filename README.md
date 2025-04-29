# Random String Generator App

A simple Android application that allows users to generate, display, and manage random strings of a specified length.

## 🚀 Features

- Generate random strings by user-defined length
- Display a list of generated strings
- Delete individual strings or clear all
- Built with modern Android components:
  - Jetpack Compose UI
  - MVVM Architecture
  - Kotlin Coroutines
  - Clean code separation

## 🛠️ Built With

- **Kotlin**
- **Jetpack Compose**
- **MVVM & Flow**
- **Coroutines for async tasks**
- **Content Provider**

## 📦 Project Structure

com.example.randomstringgeneratorapp/
├── data/                               # Handles the data layer of the app (model, repository)
│   ├── model/                          # Data classes (e.g., RandomStringData)
│   ├── repository/                     # Repository that handles random string generation logic
│   └── provider/                       # # Contains helper classes like RandomStringProviderHelper for fetching random string data from content providers
├── ui/                                 # UI layer for the app (composable UI components, screens, and state)
│   ├── screens/                        # Composable screen like RandomStringScreen
│   ├── state/                          # Defines different UI states (Loading, Success, Error, Empty) for the RandomStringScreen
│   └── theme/                          # Ttheme setup (colors, typography, etc.)
├── viewmodel/                          # Contains ViewModel for managing UI state and business logic (e.g., RandomStringViewModel)
├── MainActivity.kt                     # App entry point using Jetpack Compose


### Summary of Structure:

- **data/** – Domain logic for managing data
- **ui/** – UI components and screens
- **viewmodel/** – Business logic and state handling
- **MainActivity.kt** – Composable host and system UI setup
