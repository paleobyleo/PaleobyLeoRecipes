# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.9] - 2025-09-09

### Improvements
- Recipe print functionality enhancements
  - Reduced recipe title text size by 30%
  - Made additional notes header smaller than other section headers
  - Made text in additional notes field the same size as other text elements
- Improved print pagination algorithm for better content distribution
- Enhanced image handling in printed recipes

## [1.0.8] - 2025-09-08

### Added
- Automatic update checking system
- Update notification dialog
- GitHub Releases integration for version checking
- Version comparison functionality

### Improvements
- Enhanced user experience with non-intrusive update notifications
- Robust version comparison using semantic versioning
- Graceful handling of network errors during update checks
- Clean and intuitive update dialog interface

## [1.0.0] - 2025-08-30

### Added
- Initial release of Paleo Recipes app
- Recipe management functionality (add, edit, delete)
- Search and filter capabilities
- Favorites system
- OCR scanning for importing paper recipes
- Print functionality for recipes
- Dark theme implementation
- Material Design 3 UI
- Jetpack Compose implementation
- Room database for local storage
- Hilt dependency injection
- ML Kit text recognition for OCR

### Features
- Main screen with recipe cards
- Detailed recipe view
- Add/Edit recipe form
- Camera and gallery integration for OCR
- Search by recipe title
- Filter by favorites
- Print recipes to PDF or physical printer
- Responsive UI design
- Offline functionality

### Technical
- MVVM architecture
- Kotlin coroutines
- LiveData for UI updates
- Repository pattern
- ProGuard optimization
- R8 full mode compilation
- Production-ready build configuration
- GitHub publishing setup

## [Unreleased]

### Planned
- Cloud synchronization
- Recipe sharing functionality
- Meal planning features
- Nutritional information tracking
- Shopping list generation
- Recipe rating system
- Import/Export recipes as files
- Multi-language support