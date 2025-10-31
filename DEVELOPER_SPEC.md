# Note Scanner App - Developer Specification

## Project Description
Android application for scanning handwritten notes and converting them to digital text using OCR technology. These extracted notes should then be editable and exportable to different file formats. This is a hobby project focused on creating a user-friendly interface for capturing, processing, and managing handwritten notes.

## Developer Guidelines

### Clarification Protocol
**IMPORTANT**: When working on tasks or implementing features, always ask for clarification if:

- Requirements are ambiguous or incomplete
- Multiple implementation approaches are possible
- Technical decisions need user input (e.g., library choices, architecture patterns)
- UI/UX specifications are unclear
- Business logic requirements are not well-defined
- Performance requirements or constraints are unknown
- Integration details with external systems are missing
- Error handling strategies need definition
- Testing requirements are not specified

### Communication Best Practices
- **Be Specific**: Ask concrete questions about unclear requirements
- **Provide Options**: When multiple approaches exist, present alternatives with pros/cons
- **Confirm Understanding**: Restate requirements in your own words for confirmation
- **Request Examples**: Ask for specific examples when behavior is unclear
- **Clarify Scope**: Ensure feature boundaries and limitations are well-defined

### Implementation Approach
- **Start Small**: Break complex features into smaller, manageable pieces
- **Seek Feedback Early**: Show progress frequently rather than waiting for completion
- **Document Assumptions**: When making technical decisions, document reasoning
- **Ask Before Major Changes**: Confirm architectural or design changes before implementation

## Technology Stack & Environment

### Platform & Language
- **Platform**: Android
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose with Material3
- **Compose BOM**: 2024.09.00

### SDK Configuration
- **Target SDK**: 36
- **Minimum SDK**: 35
- **Compile SDK**: 35
- **Java Version**: 11

### Build System
- **Build System**: Gradle with Version Catalogs
- **IDE**: Android Studio

### Libraries & Dependencies
- **OCR Engine**: Google ML Kit (planned)
- **Image Processing**: TBD
- **Database**: Room (Android Architecture Components)
- **Storage**: Local device storage

## Project Structure
```
app/src/main/java/com/marpal/note_scanner/
├── MainActivity.kt                 # Entry point with Compose UI
├── data/                          # Database layer
│   ├── NoteDatabase.kt            # Room database configuration
│   ├── NoteEntity.kt              # Note data model
│   └── NoteDao.kt                 # Database access object
├── ui/theme/                      # Compose theming
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── [other source files]
```

## Database Specifications

### Database Technology
- **Framework**: Room Database (Android Architecture Components)
- **Database Name**: `note_database`
- **Version**: 1
- **Export Schema**: false

### Database Architecture
- **Pattern**: Repository pattern with DAO
- **Threading**: Coroutines with suspend functions
- **Data Flow**: Flow-based reactive streams for UI updates

### Data Model

#### NoteEntity Table
**Table Name**: `notes`

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | Primary Key, Auto-generate | Unique identifier |
| `imagePath` | String | Not null | Path to captured image file |
| `timestamp` | Long | Not null | Creation timestamp (epoch time) |
| `title` | String | Not null | User-defined note title |
| `parsedText` | String | Default: "" | OCR extracted text content |

### DAO Operations

#### NoteDao Interface
- `insertNote(note: NoteEntity): Long` - Insert new note, returns generated ID
- `getAllNotes(): Flow<List<NoteEntity>>` - Get all notes ordered by timestamp (descending)
- `getNoteById(id: Long): NoteEntity?` - Get specific note by ID
- `deleteNote(id: Long)` - Delete note by ID

### Database Instance Management
- **Pattern**: Singleton with thread-safe initialization
- **Context**: Application context used for database builder
- **Migration Strategy**: Currently none (version 1)

### Future Database Considerations
- **Migrations**: Add migration strategies when schema changes
- **Indexing**: Consider adding indexes for timestamp and title for performance
- **Backup/Export**: Implement database export functionality
- **Full-text Search**: Add FTS for searching within parsed text
- **Tags/Categories**: Potential additional tables for note organization

## UI/UX Requirements

### Design System
- **Framework**: Jetpack Compose with Material3 design system
- **Theme**: Material3 theming with dynamic color support (Android 12+)
- **Colors**: Purple-based color scheme (Purple80/40, PurpleGrey80/40, Pink80/40)
- **Typography**: Material3 default typography system
- **Design Guidelines**: Follow Google Material Design guidelines

### Layout Architecture
- **Main Structure**: Scaffold-based layout with TopAppBar
- **Navigation**: Single-screen application with modal navigation drawer (currently unused)
- **Screen Pattern**: Single-activity architecture with Compose

### Current UI Components

#### Notes List View
- **Layout**: LazyColumn with 16dp spacing and padding
- **Item Design**: 
  - Card-based layout (120.dp height, 12dp rounded corners)
  - Background image with overlay text
  - Swipe-to-delete functionality with visual feedback
  - Status indicator (red cross icon for unparsed notes)
- **Item Content**:
  - Thumbnail: Full-size background image with crop scaling
  - Title: White text with shadow, semibold 14sp, single line with ellipsis
  - Timestamp: White text with shadow, 12sp, format "dd.MM.yyyy HH:mm:ss"
  - Status: Red circular icon with white cross (20dp size)

#### Floating Action Buttons
- **Position**: Bottom-right corner with 16dp padding
- **Buttons**: 
  - Camera capture button (CameraAlt icon, 56dp size)
  - Sort options button (Sort icon, 56dp size) with dropdown menu
- **Arrangement**: Vertical column with 16dp spacing

#### Interactive Elements
- **Swipe Gesture**: Horizontal drag for delete with 150dp threshold
- **Sort Options**: Dropdown menu with Title A-Z, Newest First, Oldest First
- **Delete Confirmation**: In-place dialog with Cancel/Delete options
- **Title Input**: AlertDialog with OutlinedTextField for new notes

#### Visual Feedback
- **Swipe Animation**: Smooth offset with color transition from placeholder to error color
- **Color Transitions**: Background color interpolation during swipe
- **Icons**: Material Icons with appropriate content descriptions

### Accessibility Requirements
- **Content Descriptions**: All interactive elements must have meaningful descriptions
- **Touch Targets**: Minimum 48dp touch targets for all interactive elements
- **Color Contrast**: Follow Material3 accessibility guidelines
- **Screen Reader Support**: Proper semantic structure for TalkBack

### Responsive Design
- **Device Support**: Optimize for various screen sizes and orientations
- **Drawer Width**: Dynamic sizing (50% of screen width)
- **Layout Adaptation**: Flexible layouts that work across different device sizes

### Theme Support
- **Dynamic Colors**: Support Android 12+ dynamic theming
- **Dark/Light Mode**: Automatic system theme detection
- **Color Schemes**: Consistent color application across all components

### Interaction Patterns
- **Gesture Navigation**: Swipe-to-delete with visual feedback
- **Touch Feedback**: Appropriate ripple effects and state changes
- **Dialog Management**: Modal dialogs for confirmation and input
- **State Management**: Proper handling of UI state with Compose state

### Performance Considerations
- **Image Loading**: Async image loading with Coil library
- **List Performance**: Efficient LazyColumn with proper key handling
- **State Optimization**: Minimize recomposition with proper state management

### UI Implementation Details

#### Progress Indicators
- **OCR Progress**: Spinner overlay in lower right corner of note thumbnails
- **Scroll Indicator**: Modern scroll indicator for list views
- **Export Progress**: Loading states during export operations

#### Note Status Indicators
- **Processing**: Spinner during OCR extraction
- **Success**: Green checkmark when OCR completed successfully
- **Failed**: Red cross with retry option for failed OCR
- **No Text**: Gray icon when no text detected

#### Text Editing Screen
- **Layout**: Full-screen editor with top app bar
- **Toolbar**: Formatting controls (Bold, Italic, Underline)
- **Navigation**: Back button in top app bar + hardware back support
- **Auto-save**: Real-time saving with visual confirmation

### Future UI Considerations
- **Search Interface**: Search bar and filtering capabilities
- **Settings Screen**: App configuration and preferences
- **Batch Selection**: Multi-select interface for batch operations

## Core Features

### Implemented
- Notes view as main interface
- Image capture functionality
- Notes deletion
- Basic UI with Jetpack Compose

### Planned
- Camera integration for note capture
- Image preprocessing and enhancement
- Handwriting recognition and OCR of captured images and text extraction
- Text editing and correction interface
- Export to multiple formats (TXT, PDF, etc.)
- Note organization and management
- Offline functionality

### Feature Specifications

#### Text Extraction (OCR)
- **Technology**: Google ML Kit Text Recognition
- **Timing**: 
  - Automatic: Immediately after image capture
  - Manual: On-demand retry option for failed extractions
- **Progress Indication**: Spinner in lower right corner of image thumbnail
- **Error Handling**: Retry mechanism for failed OCR attempts

#### Text Editing Interface
- **Screen Type**: Separate dedicated editing screen with swipeable image viewer
- **Navigation**:
  - Open: Click note item in list (after OCR completion)
  - Back: Hardware back button or top app bar back button
  - Return to: Notes list view
- **Swipeable View System**:
  - **Page 0 (Text Editor)**: Default view showing extracted text with editing capabilities
  - **Page 1 (Image Viewer)**: Full-screen view of the captured note image
  - **Swipe Right**: From text editor to view the original captured image
  - **Swipe Left**: From image viewer to return to text editor
  - **Navigation**: Swipe gesture only (no toolbar navigation buttons for simplicity)
- **Editing Features** (Text Editor Page):
  - Basic text manipulation (copy, paste, add/remove text)
  - Line management (add/remove new lines)
  - Plain text editing (text formatting features reserved for future enhancement)
- **Image Viewer Features**:
  - Full-screen image display with black background
  - Fit-to-screen scaling for optimal viewing
  - Error handling for missing image files
  - On-screen hint: "Swipe to return to text"
- **Auto-save**: Changes saved automatically to database with 500ms debouncing
- **Export Feedback**: Snackbar notifications for export success/failure

#### Export Functionality
- **Supported Formats**: TXT, PDF
- **Export Options**:
  - Per-note: From editing screen
  - Batch export: From notes list view
- **Storage Location**: User-selectable folder with Downloads as default
- **File Naming**: Note title + timestamp + format extension

## Development Guidelines

### Code Quality
- Follow Android development best practices
- Follow Google Materials design guidelines when designing UI
- Focus on user experience and accessibility
- Implement proper error handling for OCR failures
- Test on multiple device sizes and orientations

### OCR Considerations
- Optimize for various handwriting styles
- Consider image quality and lighting conditions
- Handle OCR failures gracefully

### Build Commands
```bash
# Build project
./gradlew build

# Install debug build
./gradlew installDebug
```

## Project Status
Currently in active development phase. Recent work includes:
- Implementation of notes view as main interface with calm marine blue background
- Swipeable text/image viewer in note editing screen
- Full-screen image viewing capability
- Removal of capture view from codebase
- Addition of capture button to notes view
- Basic notes management functionality
- ADB over WiFi deployment setup for WSL development

## Development Context
- This is a hobby project with no set deadline
- Focus on learning and experimenting with Android development
- Emphasis on practical OCR implementation for handwritten text
- Modern Android development practices using Jetpack Compose