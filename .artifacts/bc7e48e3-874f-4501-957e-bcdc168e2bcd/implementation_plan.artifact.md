# NADI – Raga & Shruti Implementation Plan

Transform the existing UI-only app into a functional music practice tool for Carnatic musicians.

## User Review Required

> [!IMPORTANT]
> - The audio engine will use `AudioTrack` for low-latency synthesis.
> - `DataStore` will be used for persistence.
> - Hilt will NOT be added to keep it simple unless requested. ViewModel will be used for state management.

## Proposed Changes

### Core Logic & Models

#### [NEW] [Swara.kt](file:///C:/Users/welcome/AndroidStudioProjects/Naadi/app/src/main/java/com/example/nadi/model/Swara.kt)
Define the Carnatic swara types and their frequency ratios.

#### [NEW] [Raga.kt](file:///C:/Users/welcome/AndroidStudioProjects/Naadi/app/src/main/java/com/example/nadi/model/Raga.kt)
Data classes for Ragas, including Arohanam and Avarohanam.

#### [NEW] [MusicEngine.kt](file:///C:/Users/welcome/AndroidStudioProjects/Naadi/app/src/main/java/com/example/nadi/audio/MusicEngine.kt)
Logic for frequency calculations based on the base 'Sa' frequency.

### Audio Engine

#### [NEW] [AudioEngine.kt](file:///C:/Users/welcome/AndroidStudioProjects/Naadi/app/src/main/java/com/example/nadi/audio/AudioEngine.kt)
Low-level audio synthesis using `AudioTrack` to generate sine waves without pops.

### ViewModel & State

#### [NEW] [NadiViewModel.kt](file:///C:/Users/welcome/AndroidStudioProjects/Naadi/app/src/main/java/com/example/nadi/ui/NadiViewModel.kt)
Centralized state management (Sa frequency, selected Raga, Tanpura status).

### UI Integration

#### [MODIFY] [MainActivity.kt](file:///C:/Users/welcome/AndroidStudioProjects/Naadi/app/src/main/java/com/example/nadi/MainActivity.kt)
Connect the UI components to the `NadiViewModel`. Implement dynamic Swara buttons and Raga information.

---

## Verification Plan

### Automated Tests
- Unit tests for `MusicEngine` to verify frequency calculations for different ragas.
- Unit tests for `Raga` data model.

### Manual Verification
- Deploy to a physical device/emulator.
- Change 'Sa' frequency and verify that swara buttons sound correct.
- Play Tanpura and verify continuous Sa-Pa-Sa drone.
- Select different ragas and check if Arohanam/Avarohanam updates.
