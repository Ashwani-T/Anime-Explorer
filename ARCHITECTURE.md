# Anime Explorer Architecture

This project follows a **Feature-Based Clean Architecture** pattern, designed to be modular, maintainable, and scalable.

## Architecture Overview

The project is organized into two main high-level packages:
1.  **`core`**: Contains globally shared logic, infrastructure, and UI components.
2.  **`features`**: Contains self-contained modules, each representing a specific application feature (Home, Search, Collection, Detail).

Each feature follows a **Clean Architecture** structure with three distinct layers:
*   **Data**: Handles data retrieval from remote (API) and local (Room DB) sources. Includes DAOs, Entities, and Mappers.
*   **Domain**: Contains the business logic, including Repository interfaces and feature-specific models.
*   **UI (Presentation)**: Handles the UI rendering using Jetpack Compose, state management with ViewModels, and UI-specific state models.

---

## High-Level Flow

1.  **User Interaction**: The user interacts with the UI (e.g., clicks on an anime).
2.  **ViewModel**: The ViewModel receives the event, updates the `UiState`, and calls the appropriate Domain Repository.
3.  **Repository**: The Repository implementation decides whether to fetch data from the Local Database (cache) or the Remote API.
4.  **Data Source**: Data is fetched, mapped to Domain/UI models, and flowed back to the ViewModel.
5.  **UI Update**: The ViewModel updates the `UiState` (StateFlow), which triggers a recomposition of the Compose UI.

---

## Detailed Feature Breakdown

### 1. Home Feature (`features.home`)
*   **Purpose**: Displays trending, upcoming, top, and seasonal anime.
*   **Flow**:
    *   `HomeRepository` fetches cached data from `AnimeCachedDao`.
    *   If a refresh is needed, `HomeRepositoryImpl` calls `AnimeApiService` to update the cache.
    *   The `HomeScreen` displays multiple horizontal lists populated from the `HomeViewModel`.

### 2. Search Feature (`features.search`)
*   **Purpose**: Allows users to search and filter anime with advanced criteria.
*   **Flow**:
    *   The `SearchViewModel` manages a complex `SearchUiState` including filters (Genre, Status, Rating, etc.).
    *   `SearchRepositoryImpl` performs paginated searches via `AnimeApiService`.
    *   Results are displayed in a responsive grid.

### 3. Collection Feature (`features.collection`)
*   **Purpose**: Manages the user's personal anime library (Watch Later, Completed, etc.).
*   **Flow**:
    *   Uses `AnimeCollectionDao` to persist user-selected anime in the local Room database.
    *   `AnimeLibraryViewModel` provides a filtered view of the user's collection.

### 4. Detail Feature (`features.detail`)
*   **Purpose**: Displays comprehensive information about a specific anime.
*   **Flow**:
    *   `DetailRepositoryImpl` first checks the local database for cached details before hitting the API.
    *   Users can add/remove the anime from their collection directly from this screen via a bottom sheet.

---

## Core Components (`core`)

*   **`core.data.remote`**: Centralized `Retrofit` service (`AnimeApiService`) and DTOs.
*   **`core.data.connectivity`**: `ConnectivityObserver` to monitor network status and show an "Offline" banner in `MainActivity`.
*   **`core.database`**: Central `AppDatabase` (Room) configuration.
*   **`core.ui.components`**: Reusable Compose components like `AnimeItem`, `ArcLoader`, and `AutoAdvancePager`.
*   **`core.domain`**: Shared UI models used across multiple features to avoid code duplication.

---

## Tech Stack
*   **UI**: Jetpack Compose
*   **Dependency Injection**: Hilt (Dagger)
*   **Networking**: Retrofit + Kotlinx Serialization
*   **Local Database**: Room
*   **Image Loading**: Coil
*   **Navigation**: Compose Navigation (Type-safe)
*   **Async Processing**: Kotlin Coroutines & Flow
