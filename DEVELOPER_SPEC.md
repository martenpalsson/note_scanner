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

## Character Recognition & Training System

### Current OCR Implementation

#### Technology Stack
- **Framework**: Google ML Kit Text Recognition v16.0.1
- **Model Type**: Pre-trained, cloud-connected on-device models
- **Script Support**: Latin script only
- **Model Management**: Automatic download and bundling via ML Kit library

#### Model Architecture
ML Kit uses multiple TensorFlow Lite models working in pipeline:

1. **Text Detection** (`rpn_text_detector_mobile_space_to_depth_quantized_mbv2_v1.tflite`)
   - Detects text regions in images
   - Based on Region Proposal Network (RPN) architecture
   - MobileNetV2 backbone for efficiency

2. **Line Recognition** (`tflite_langid.tflite`)
   - Recognizes text lines and character sequences
   - Language identification capability
   - Legacy mobile optimized model

3. **Layout Analysis Models**
   - Line splitting: `line_splitting_custom_ops/model.tflite`
   - Line clustering: `line_clustering_custom_ops/model.tflite`
   - Organizes detected text into structured blocks

#### Model Location
Build artifacts stored in:
```
app/build/intermediates/assets/[debug|release]/merge[Debug|Release]Assets/mlkit-google-ocr-models/
├── taser/detector/          # Text detection models
├── gocr/gocr_models/        # Recognition models
└── gocr/layout/             # Layout analysis models
```

**Note**: Models are bundled automatically by ML Kit dependency - no source assets directory exists.

#### OCR Pipeline Implementation
**Primary Service**: `app/src/main/java/com/marpal/note_scanner/ocr/OcrService.kt`

```
Image Capture → Note Creation → OCR Processing → Text Extraction → Database Update
     ↓              ↓                 ↓                 ↓               ↓
CameraActivity  (pending)    OcrService.kt    TextRecognizer    (completed)
```

**Process Flow**:
1. Image captured via CameraX and saved to external storage
2. Note entity created in database with status "pending"
3. OcrService automatically triggered: `processNoteForOcr(noteId)`
4. Image loaded from file path using BitmapFactory
5. Bitmap converted to ML Kit InputImage
6. TextRecognizer processes image asynchronously
7. Success: Extracted text saved to database, status → "completed"
8. Failure: Status → "failed", retry available via UI

#### Current Limitations
- **Handwriting Recognition**: Poor accuracy on handwritten text (optimized for printed text)
- **No Custom Training**: Relies entirely on Google's pre-trained models
- **No Image Preprocessing**: Raw camera images fed directly to ML Kit
- **No User Feedback Loop**: No mechanism to collect corrections for improvement
- **No Model Versioning**: Model updates tied to ML Kit library updates only

### Training System Architecture

#### Overview
The training system enables continuous improvement of handwriting recognition through:
1. **Image Preprocessing**: Enhance images before OCR for better accuracy
2. **Custom Model Training**: Train TensorFlow Lite models on handwriting-specific datasets
3. **User Feedback Collection**: Gather corrected samples from users
4. **Model Deployment**: Push updated models to production
5. **A/B Testing**: Compare model performance and rollback if needed

#### Architecture Diagram
```
┌─────────────────────────────────────────────────────────────────┐
│                        Android Application                       │
│                                                                   │
│  ┌──────────────┐      ┌─────────────────┐                      │
│  │   Camera     │─────>│  Image Capture  │                      │
│  │  (CameraX)   │      └─────────────────┘                      │
│  └──────────────┘               │                                │
│                                  v                                │
│                        ┌──────────────────┐                      │
│                        │  Image Storage   │                      │
│                        │  (.jpg files)    │                      │
│                        └──────────────────┘                      │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │         Image Preprocessing Pipeline           │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ 1. Grayscale Conversion                  │  │              │
│  │  │ 2. Contrast Enhancement (CLAHE)          │  │              │
│  │  │ 3. Noise Reduction (Gaussian Blur)       │  │              │
│  │  │ 4. Deskewing (Perspective Correction)    │  │              │
│  │  │ 5. Binarization (Adaptive Threshold)     │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │         OCR Engine (Dual Model System)         │              │
│  │                                                 │              │
│  │  ┌──────────────────┐  ┌──────────────────┐   │              │
│  │  │   ML Kit Model   │  │  Custom TFLite   │   │              │
│  │  │  (Printed Text)  │  │   (Handwriting)  │   │              │
│  │  └──────────────────┘  └──────────────────┘   │              │
│  │           │                      │             │              │
│  │           v                      v             │              │
│  │  ┌────────────────────────────────────┐       │              │
│  │  │   Confidence-Based Selector        │       │              │
│  │  │  (Choose best result or combine)   │       │              │
│  │  └────────────────────────────────────┘       │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │          User Correction Interface             │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • Display extracted text                 │  │              │
│  │  │ • Allow user corrections                 │  │              │
│  │  │ • "Was this correct?" prompt             │  │              │
│  │  │ • Easy correction workflow               │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │         Training Data Collection               │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ Store: Original Image + Corrected Text   │  │              │
│  │  │ Local SQLite or Upload to Server         │  │              │
│  │  │ Privacy-aware (opt-in, anonymization)    │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
└──────────────────────────────────┼────────────────────────────────┘
                                   │
                                   v
┌─────────────────────────────────────────────────────────────────┐
│                    Training Infrastructure                       │
│                      (Python / TensorFlow)                       │
│                                                                   │
│  ┌────────────────────────────────────────────────┐              │
│  │           Dataset Management                   │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • User-contributed corrections           │  │              │
│  │  │ • IAM Handwriting Database               │  │              │
│  │  │ • EMNIST/MNIST datasets                  │  │              │
│  │  │ • Custom synthetic data                  │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │         Data Preprocessing Pipeline            │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • Image augmentation (rotation, scale)   │  │              │
│  │  │ • Normalization and resizing             │  │              │
│  │  │ • Text label encoding (CTC)              │  │              │
│  │  │ • Train/validation/test split            │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │           Model Training Engine                │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │  CRNN Architecture:                      │  │              │
│  │  │  • Convolutional layers (feature extract)│  │              │
│  │  │  • Recurrent layers (LSTM/GRU)           │  │              │
│  │  │  • CTC loss function                     │  │              │
│  │  │  • Beam search decoder                   │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │         Model Evaluation & Validation          │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • Character Error Rate (CER)             │  │              │
│  │  │ • Word Error Rate (WER)                  │  │              │
│  │  │ • Confidence calibration                 │  │              │
│  │  │ • Performance benchmarking               │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │          TFLite Model Conversion               │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • Convert TensorFlow to TFLite           │  │              │
│  │  │ • Quantization (float16/int8)            │  │              │
│  │  │ • Optimization for mobile inference      │  │              │
│  │  │ • Model size reduction                   │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
└──────────────────────────────────┼────────────────────────────────┘
                                   │
                                   v
┌─────────────────────────────────────────────────────────────────┐
│                   Model Deployment System                        │
│                                                                   │
│  ┌────────────────────────────────────────────────┐              │
│  │           Model Versioning & Registry          │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • Version tracking (semantic versioning) │  │              │
│  │  │ • Model metadata (accuracy, size, date)  │  │              │
│  │  │ • Rollback capability                    │  │              │
│  │  │ • A/B testing infrastructure             │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
│                                  v                                │
│  ┌────────────────────────────────────────────────┐              │
│  │           Model Distribution Server            │              │
│  │  ┌──────────────────────────────────────────┐  │              │
│  │  │ • REST API for model downloads           │  │              │
│  │  │ • Delta updates (incremental downloads)  │  │              │
│  │  │ • Bandwidth optimization                 │  │              │
│  │  │ • Device-specific model variants         │  │              │
│  │  └──────────────────────────────────────────┘  │              │
│  └────────────────────────────────────────────────┘              │
│                                  │                                │
└──────────────────────────────────┼────────────────────────────────┘
                                   │
                                   v
                    ┌──────────────────────────┐
                    │   Mobile App Update      │
                    │  (Download new model)    │
                    └──────────────────────────┘
```

### Image Preprocessing Specifications

#### Purpose
Enhance image quality before OCR processing to improve character recognition accuracy, especially for handwritten text.

#### Preprocessing Pipeline
All preprocessing implemented in: `app/src/main/java/com/marpal/note_scanner/preprocessing/ImagePreprocessor.kt`

#### Processing Steps

**Step 1: Grayscale Conversion**
- Convert RGB to grayscale to reduce complexity
- Use luminosity method: `Y = 0.299*R + 0.587*G + 0.114*B`
- Reduces data size by 3x, improves processing speed

**Step 2: Contrast Enhancement (CLAHE)**
- **Algorithm**: Contrast Limited Adaptive Histogram Equalization
- **Purpose**: Improve local contrast while preventing noise amplification
- **Parameters**:
  - Clip Limit: 2.0 (limits contrast amplification)
  - Tile Grid Size: 8x8 (adaptive region size)
- **Benefit**: Enhances faded ink, improves edge detection

**Step 3: Noise Reduction**
- **Algorithm**: Gaussian Blur with adaptive kernel
- **Parameters**:
  - Kernel Size: 3x3 (small kernel to preserve text edges)
  - Sigma: 0.5 (mild smoothing)
- **Purpose**: Remove camera sensor noise without blurring text
- **Trade-off**: Balance between noise reduction and edge preservation

**Step 4: Deskewing (Perspective Correction)**
- **Purpose**: Correct rotated or skewed text lines
- **Algorithm**:
  - Detect text orientation using projection profile
  - Calculate skew angle (-15° to +15° range)
  - Apply affine transformation to straighten text
- **Benefit**: Improves line detection and character segmentation

**Step 5: Binarization (Adaptive Thresholding)**
- **Algorithm**: Adaptive Gaussian Threshold (OpenCV)
- **Purpose**: Convert to black text on white background
- **Parameters**:
  - Block Size: 15 (local neighborhood size)
  - Constant: 8 (offset from mean)
  - Threshold Type: Binary
- **Benefit**: Improves character boundary definition, removes background variations

#### Implementation Requirements

**Dependencies Required**:
```gradle
// OpenCV for Android (image processing)
implementation "org.opencv:opencv:4.8.0"

// Or alternative: Android NDK with custom C++ implementation
```

**API Design**:
```kotlin
class ImagePreprocessor {
    /**
     * Preprocess image for OCR
     * @param bitmap Original captured image
     * @param preprocessingLevel Level of preprocessing (LIGHT, STANDARD, AGGRESSIVE)
     * @return Preprocessed bitmap ready for OCR
     */
    fun preprocess(
        bitmap: Bitmap,
        preprocessingLevel: PreprocessingLevel = PreprocessingLevel.STANDARD
    ): Bitmap

    enum class PreprocessingLevel {
        LIGHT,      // Grayscale + mild contrast only
        STANDARD,   // Full pipeline with balanced parameters
        AGGRESSIVE  // Maximum enhancement (may introduce artifacts)
    }
}
```

**Integration Point**:
Modify `OcrService.kt` to include preprocessing before ML Kit processing:
```kotlin
suspend fun processNoteForOcr(noteId: Long) {
    val bitmap = BitmapFactory.decodeFile(note.imagePath)

    // Add preprocessing step
    val preprocessedBitmap = imagePreprocessor.preprocess(
        bitmap,
        PreprocessingLevel.STANDARD
    )

    val inputImage = InputImage.fromBitmap(preprocessedBitmap, 0)
    textRecognizer.process(inputImage)
    // ... rest of OCR processing
}
```

#### Performance Considerations
- **Processing Time**: Target < 500ms for preprocessing pipeline
- **Memory Usage**: Process images at maximum 1080p resolution
- **Battery Impact**: Optimize algorithms to minimize CPU usage
- **Trade-offs**: Balance between accuracy improvement and resource consumption

#### Testing & Validation
- **Test Dataset**: Create suite of 100+ handwritten note samples
- **Metrics**:
  - Character Error Rate (CER) before/after preprocessing
  - Processing time per image
  - Memory usage during preprocessing
- **A/B Testing**: Compare ML Kit accuracy with/without preprocessing

### Custom Model Training Pipeline

#### Training Infrastructure

**Environment Setup**:
```
Training Server / Local Workstation:
├── Python 3.9+
├── TensorFlow 2.14+
├── TensorFlow Lite Converter
├── NumPy, OpenCV, scikit-learn
├── GPU Support (CUDA 11.8+ for faster training)
└── Dataset Storage (50GB+ recommended)
```

**Project Structure**:
```
training/
├── datasets/
│   ├── iam_handwriting/          # IAM Handwriting Database
│   ├── user_corrections/         # User-submitted corrections
│   └── synthetic/                # Generated training data
├── preprocessing/
│   └── augmentation.py           # Data augmentation pipeline
├── models/
│   ├── crnn.py                   # CRNN model architecture
│   ├── transformer.py            # Alternative: Transformer model
│   └── model_config.yaml         # Hyperparameters
├── training/
│   ├── train.py                  # Training script
│   ├── evaluate.py               # Evaluation metrics
│   └── convert_tflite.py         # TFLite conversion
├── deployment/
│   ├── model_server.py           # Model distribution API
│   └── version_manager.py        # Version control
└── requirements.txt
```

#### Model Architecture: CRNN (Convolutional Recurrent Neural Network)

**Architecture Overview**:
```
Input Image (32 x W x 1)
       ↓
[Convolutional Layers]
   Conv2D + BatchNorm + ReLU + MaxPool (7 layers)
   Feature extraction from image
       ↓
[Reshape & Permute]
   Convert to sequence: (W/8, 512)
       ↓
[Recurrent Layers]
   Bidirectional LSTM/GRU (2 layers, 256 units each)
   Learn sequential dependencies
       ↓
[Fully Connected Layer]
   Dense(num_characters + 1)  # +1 for CTC blank
       ↓
[CTC Loss Layer]
   Connectionist Temporal Classification
   Align predictions with ground truth
       ↓
Output: Character sequence
```

**Key Components**:

1. **Convolutional Layers** (Feature Extraction)
   - Extract visual features from image
   - Reduce spatial dimensions: H: 32 → 1, W: variable
   - Output: Feature maps representing text characteristics

2. **Recurrent Layers** (Sequence Modeling)
   - Bidirectional LSTM captures left-to-right and right-to-left context
   - Models dependencies between characters
   - Handles variable-length sequences

3. **CTC Loss** (Alignment-Free Training)
   - No need for character-level segmentation
   - Automatically learns alignment between image and text
   - Handles varying text lengths

**Model Hyperparameters**:
```yaml
# model_config.yaml
architecture:
  input_height: 32
  input_width: variable  # Width normalized, maintains aspect ratio

  conv_layers:
    - filters: 64, kernel: [3,3], pool: [2,2]
    - filters: 128, kernel: [3,3], pool: [2,2]
    - filters: 256, kernel: [3,3], pool: [2,1]
    - filters: 256, kernel: [3,3], pool: [2,1]
    - filters: 512, kernel: [3,3], pool: [2,1]
    - filters: 512, kernel: [3,3], pool: [2,1]
    - filters: 512, kernel: [2,2], pool: [2,1]

  recurrent_layers:
    type: LSTM  # or GRU
    units: 256
    bidirectional: true
    num_layers: 2
    dropout: 0.25

  output:
    num_classes: 80  # Characters + blank for CTC
    activation: softmax

training:
  batch_size: 32
  learning_rate: 0.001
  optimizer: Adam
  epochs: 50
  early_stopping_patience: 5
  reduce_lr_patience: 3

  augmentation:
    rotation_range: [-5, 5]  # degrees
    width_shift: 0.1
    height_shift: 0.1
    shear_range: 0.1
    zoom_range: [0.9, 1.1]
    elastic_transform: true
```

#### Training Datasets

**Primary Dataset: IAM Handwriting Database**
- **Size**: 115,000 word images from 657 writers
- **Format**: Grayscale images + transcription text
- **License**: Free for research use (registration required)
- **URL**: https://fki.tic.heia-fr.ch/databases/iam-handwriting-database
- **Quality**: High-quality English handwriting samples

**Supplementary Datasets**:
- **EMNIST**: Extended MNIST with handwritten letters (814,255 characters)
- **RIMES**: French handwriting dataset (for multi-language support)
- **User Corrections**: Real-world data from app users (privacy-compliant)

**Dataset Preparation**:
```python
# preprocessing/prepare_data.py
def prepare_training_data():
    """
    1. Load IAM dataset
    2. Normalize images to height=32px
    3. Apply preprocessing pipeline (grayscale, contrast, etc.)
    4. Split: 80% train, 10% validation, 10% test
    5. Create TFRecord files for efficient training
    """
    pass

def augment_data(image, text):
    """
    Apply random augmentation:
    - Random rotation (-5° to +5°)
    - Random scaling (0.9x to 1.1x)
    - Random elastic distortion
    - Random noise injection
    - Random brightness/contrast adjustment
    """
    pass
```

#### Training Process

**Training Script** (`training/train.py`):
```python
import tensorflow as tf
from models.crnn import build_crnn_model

# Load datasets
train_dataset = load_tfrecord('datasets/train.tfrecord')
val_dataset = load_tfrecord('datasets/val.tfrecord')

# Build model
model = build_crnn_model(
    input_height=32,
    num_classes=80,
    rnn_units=256
)

# Compile with CTC loss
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
    loss=tf.keras.backend.ctc_batch_cost
)

# Callbacks
callbacks = [
    tf.keras.callbacks.ModelCheckpoint(
        'checkpoints/best_model.h5',
        save_best_only=True,
        monitor='val_loss'
    ),
    tf.keras.callbacks.EarlyStopping(
        patience=5,
        restore_best_weights=True
    ),
    tf.keras.callbacks.ReduceLROnPlateau(
        factor=0.5,
        patience=3
    ),
    tf.keras.callbacks.TensorBoard(log_dir='logs/')
]

# Train
history = model.fit(
    train_dataset,
    validation_data=val_dataset,
    epochs=50,
    callbacks=callbacks
)

# Save final model
model.save('models/handwriting_crnn_final.h5')
```

**Evaluation Metrics**:
```python
# training/evaluate.py
def evaluate_model(model, test_dataset):
    """
    Calculate:
    - Character Error Rate (CER): % of incorrectly recognized characters
    - Word Error Rate (WER): % of incorrectly recognized words
    - Accuracy: % of perfectly recognized samples
    - Confidence calibration: alignment of confidence scores with accuracy
    """
    predictions = model.predict(test_dataset)
    ground_truth = test_dataset.labels

    cer = calculate_character_error_rate(predictions, ground_truth)
    wer = calculate_word_error_rate(predictions, ground_truth)
    accuracy = calculate_accuracy(predictions, ground_truth)

    return {
        'CER': cer,
        'WER': wer,
        'Accuracy': accuracy
    }
```

**Success Criteria**:
- **CER < 10%** on test set (handwriting)
- **CER < 5%** on printed text test set
- **Inference Time < 200ms** per image on mobile device
- **Model Size < 10MB** after quantization

#### TensorFlow Lite Conversion

**Conversion Script** (`training/convert_tflite.py`):
```python
import tensorflow as tf

# Load trained Keras model
model = tf.keras.models.load_model('models/handwriting_crnn_final.h5')

# Convert to TFLite with optimization
converter = tf.lite.TFLiteConverter.from_keras_model(model)

# Optimization: Post-training quantization
converter.optimizations = [tf.lite.Optimize.DEFAULT]
converter.target_spec.supported_types = [tf.float16]  # float16 quantization

# Additional optimizations for mobile
converter.experimental_new_converter = True
converter.experimental_new_quantizer = True

# Convert
tflite_model = converter.convert()

# Save
with open('models/handwriting_model.tflite', 'wb') as f:
    f.write(tflite_model)

# Validate conversion
interpreter = tf.lite.Interpreter(model_path='models/handwriting_model.tflite')
interpreter.allocate_tensors()

# Test inference
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()
print(f"Input shape: {input_details[0]['shape']}")
print(f"Output shape: {output_details[0]['shape']}")
print(f"Model size: {os.path.getsize('models/handwriting_model.tflite') / 1024:.2f} KB")
```

**Quantization Strategies**:
- **Float16**: Reduces model size by 50%, minimal accuracy loss
- **Int8**: Reduces size by 75%, may lose 1-2% accuracy
- **Dynamic Range**: Quantize weights only, keep activations in float

**Target Specifications**:
- Model size: < 10MB (preferably < 5MB)
- Inference time: < 200ms on mid-range devices
- Accuracy loss: < 2% compared to full-precision model

### User Feedback & Data Collection System

#### Purpose
Collect real-world corrections from users to continuously improve the model through iterative training.

#### Data Collection Flow

**UI Components** (to be implemented):

1. **Correction Interface** (`EditNoteScreen.kt`)
   ```kotlin
   // After OCR completion, show confidence indicator
   if (ocrConfidence < 0.85) {
       AlertDialog(
           title = "Was this recognition correct?",
           buttons = {
               TextButton("Yes, perfect") { /* Mark as correct */ }
               TextButton("Needs correction") { /* Open correction UI */ }
           }
       )
   }
   ```

2. **Correction Editor**
   - Side-by-side view: Original image + Editable text
   - Highlight low-confidence characters
   - Easy submit button: "Help improve recognition"

3. **Opt-in Consent**
   - First-time setup: Ask user for consent to contribute data
   - Privacy policy: Explain data usage (training only, anonymized)
   - Opt-out anytime in settings

**Database Schema Extension**:

Add new table for training samples:
```kotlin
@Entity(tableName = "training_samples")
data class TrainingSampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long,                    // Reference to original note
    val originalImagePath: String,       // Path to captured image
    val ocrResult: String,               // What OCR predicted
    val correctedText: String,           // User's correction
    val ocrConfidence: Float,            // Confidence score
    val timestamp: Long,                 // When correction was made
    val uploaded: Boolean = false,       // Sync status
    val modelVersion: String             // Which model was used
)

@Dao
interface TrainingSampleDao {
    @Insert suspend fun insertSample(sample: TrainingSampleEntity): Long
    @Query("SELECT * FROM training_samples WHERE uploaded = 0")
    suspend fun getUnuploadedSamples(): List<TrainingSampleEntity>
    @Query("UPDATE training_samples SET uploaded = 1 WHERE id = :id")
    suspend fun markAsUploaded(id: Long)
}
```

**Data Upload Service**:
```kotlin
// app/src/main/java/com/marpal/note_scanner/training/TrainingDataUploader.kt
class TrainingDataUploader(
    private val database: NoteDatabase,
    private val apiClient: TrainingApiClient
) {
    /**
     * Upload training samples to server
     * - Runs in background with WorkManager
     * - Batches samples for efficiency
     * - Compresses images before upload
     * - Handles network failures with retry
     */
    suspend fun uploadPendingSamples() {
        val samples = database.trainingSampleDao().getUnuploadedSamples()

        samples.chunked(10).forEach { batch ->
            try {
                val uploadData = batch.map { sample ->
                    TrainingDataPayload(
                        image = compressImage(sample.originalImagePath),
                        groundTruth = sample.correctedText,
                        metadata = TrainingMetadata(
                            ocrPrediction = sample.ocrResult,
                            confidence = sample.ocrConfidence,
                            modelVersion = sample.modelVersion,
                            timestamp = sample.timestamp
                        )
                    )
                }

                apiClient.uploadTrainingBatch(uploadData)
                batch.forEach { database.trainingSampleDao().markAsUploaded(it.id) }
            } catch (e: Exception) {
                // Retry logic handled by WorkManager
            }
        }
    }
}
```

#### Privacy & Security

**Privacy Considerations**:
- **Opt-in Only**: Users must explicitly consent to data collection
- **Anonymization**: Strip all metadata (GPS, device ID) from images
- **Transparency**: Clear explanation of how data is used
- **Control**: Users can delete contributed samples anytime
- **Local-first**: Option to train locally without uploading data

**Data Minimization**:
- Only collect: Image + Corrected text
- Don't collect: User identity, device info, location, timestamp of note content
- Delete original images after model training (retain only for validation)

**Security Measures**:
- HTTPS for all data transmission
- Server-side encryption at rest
- Access controls for training data
- Regular security audits

### Model Deployment & Versioning

#### Model Distribution Server

**Server Architecture**:
```python
# deployment/model_server.py
from flask import Flask, send_file, jsonify
import os

app = Flask(__name__)

@app.route('/api/models/latest')
def get_latest_model():
    """
    Return metadata for the latest model version
    """
    return jsonify({
        'version': '1.2.0',
        'model_url': '/api/models/download/1.2.0',
        'size_bytes': 4_800_000,
        'release_date': '2025-11-14',
        'improvements': 'Improved handwriting recognition for cursive text',
        'min_app_version': '2.0.0',
        'cer': 8.5,  # Character Error Rate on test set
        'checksum': 'sha256:abcdef...'
    })

@app.route('/api/models/download/<version>')
def download_model(version):
    """
    Download specific model version
    """
    model_path = f'models/handwriting_model_v{version}.tflite'
    return send_file(model_path, mimetype='application/octet-stream')

@app.route('/api/models/feedback', methods=['POST'])
def submit_feedback():
    """
    Submit model performance feedback from app
    """
    # Track: version, device type, average confidence, user satisfaction
    pass
```

**Android Model Manager**:
```kotlin
// app/src/main/java/com/marpal/note_scanner/ml/ModelManager.kt
class ModelManager(
    private val context: Context,
    private val apiClient: ModelApiClient
) {
    private val modelDir = File(context.filesDir, "ml_models")

    /**
     * Check for model updates and download if available
     * - Called on app startup (background)
     * - Uses WorkManager for reliable download
     * - Validates checksum before using new model
     */
    suspend fun checkForModelUpdate() {
        val currentVersion = getInstalledModelVersion()
        val latestVersion = apiClient.getLatestModelVersion()

        if (latestVersion.version > currentVersion) {
            downloadAndInstallModel(latestVersion)
        }
    }

    /**
     * Load model for inference
     * - Returns appropriate model based on content type
     * - Falls back to bundled model if download fails
     */
    fun loadModel(contentType: ContentType): Interpreter {
        val modelPath = when (contentType) {
            ContentType.HANDWRITING -> getModelPath("handwriting")
            ContentType.PRINTED -> getModelPath("printed")
            ContentType.MIXED -> getModelPath("handwriting")  // Use handwriting model as default
        }

        return Interpreter(loadModelFile(modelPath))
    }

    enum class ContentType {
        HANDWRITING, PRINTED, MIXED
    }
}
```

#### A/B Testing Infrastructure

**Testing Strategy**:
- Roll out new models to 10% of users initially
- Monitor: CER, user satisfaction, crash rate, inference time
- Gradual rollout: 10% → 25% → 50% → 100%
- Automatic rollback if metrics degrade

**Metrics Collection**:
```kotlin
// app/src/main/java/com/marpal/note_scanner/analytics/ModelAnalytics.kt
data class ModelPerformanceMetrics(
    val modelVersion: String,
    val avgConfidence: Float,
    val avgInferenceTimeMs: Long,
    val userCorrectionsRate: Float,  // % of results corrected by user
    val crashRate: Float,
    val deviceType: String,
    val sampleSize: Int
)

class ModelAnalytics {
    fun trackInference(
        modelVersion: String,
        confidence: Float,
        inferenceTime: Long,
        corrected: Boolean
    ) {
        // Send to analytics backend
        // Aggregate locally and send batch reports
    }
}
```

#### Rollback Mechanism

**Safety Net**:
- Always keep previous model version as fallback
- Automatic rollback if:
  - New model crashes on >5% of devices
  - CER increases by >3% compared to previous version
  - User correction rate increases by >20%
- Manual rollback option in admin dashboard

**Implementation**:
```kotlin
class ModelManager {
    private val models = mutableMapOf<String, File>()

    fun rollbackToPreviousVersion() {
        val currentVersion = getCurrentVersion()
        val previousVersion = getPreviousVersion()

        // Swap model files
        setActiveModel(previousVersion)

        // Notify analytics
        reportRollback(currentVersion, previousVersion, reason = "Performance degradation")
    }
}
```

### Development Roadmap

#### Phase 1: Image Preprocessing (Week 1-2)
**Status**: Ready to implement
- Set up OpenCV for Android
- Implement preprocessing pipeline
- Integrate with existing OcrService
- Test on sample images
- Measure accuracy improvement

**Deliverables**:
- `ImagePreprocessor.kt` with full pipeline
- Updated `OcrService.kt` with preprocessing integration
- Test suite with before/after comparisons
- Performance benchmarks

#### Phase 2: Training Infrastructure Setup (Week 3-4)
**Status**: Planned
- Set up Python training environment
- Download and prepare IAM Handwriting Database
- Implement CRNN model architecture
- Create training pipeline
- Train baseline model

**Deliverables**:
- Training codebase in `training/` directory
- Trained baseline model (`.h5` format)
- Converted TFLite model
- Evaluation report (CER, WER, accuracy)

#### Phase 3: Custom Model Integration (Week 5-6)
**Status**: Planned
- Add TFLite interpreter to Android app
- Implement custom model inference
- Create dual-model system (ML Kit + Custom)
- Confidence-based model selection
- Performance optimization

**Deliverables**:
- `CustomModelInference.kt` with TFLite integration
- Updated `OcrService.kt` with dual-model support
- A/B testing infrastructure
- Performance benchmarks (inference time, memory usage)

#### Phase 4: User Feedback System (Week 7-8)
**Status**: Planned
- Design and implement correction UI
- Add training data collection database schema
- Implement data upload service
- Add privacy controls and consent flow
- Create model performance analytics

**Deliverables**:
- Updated `EditNoteScreen.kt` with correction UI
- New `TrainingSampleEntity` and DAO
- `TrainingDataUploader.kt` service
- Privacy policy and consent UI
- Analytics dashboard (basic)

#### Phase 5: Model Deployment System (Week 9-10)
**Status**: Planned
- Build model distribution server
- Implement model versioning
- Add A/B testing infrastructure
- Create rollback mechanism
- Set up continuous training pipeline

**Deliverables**:
- Model distribution server (Flask/FastAPI)
- `ModelManager.kt` with update mechanism
- A/B testing framework
- Automated retraining pipeline
- Deployment documentation

#### Phase 6: Continuous Improvement (Ongoing)
**Status**: Planned
- Collect user corrections
- Retrain model monthly/quarterly
- Monitor performance metrics
- Optimize model architecture
- Expand to multi-language support

**Metrics to Track**:
- Character Error Rate (CER) over time
- User correction rate
- Model download/update success rate
- Average inference time
- User satisfaction (app ratings, feedback)

### Technical Specifications Summary

#### Current Implementation
- **OCR Engine**: Google ML Kit Text Recognition v16.0.1
- **Model Type**: Pre-trained, Latin script
- **Preprocessing**: None (raw camera images)
- **Accuracy**: Good for printed text, poor for handwriting

#### Target Implementation
- **OCR Engine**: Dual system (ML Kit + Custom TFLite)
- **Custom Model**: CRNN architecture trained on handwriting datasets
- **Preprocessing**: 5-step pipeline (grayscale, CLAHE, denoise, deskew, binarize)
- **Model Size**: < 10MB (quantized TFLite)
- **Inference Time**: < 200ms per image
- **Target CER**: < 10% on handwriting, < 5% on printed text
- **Training Data**: IAM Database + user corrections
- **Update Mechanism**: Over-the-air model downloads
- **Feedback Loop**: User corrections collected for retraining

#### Key Dependencies (New)
```gradle
// OpenCV for image preprocessing
implementation "org.opencv:opencv:4.8.0"

// TensorFlow Lite for custom model inference
implementation "org.tensorflow:tensorflow-lite:2.14.0"
implementation "org.tensorflow:tensorflow-lite-gpu:2.14.0"  // GPU acceleration
implementation "org.tensorflow:tensorflow-lite-support:0.4.4"

// WorkManager for background model updates
implementation "androidx.work:work-runtime-ktx:2.9.0"

// Retrofit for model download API (if not already present)
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.retrofit2:converter-gson:2.9.0"
```

#### File Structure Updates
```
app/src/main/java/com/marpal/note_scanner/
├── ocr/
│   ├── OcrService.kt                    # (Modified) Add preprocessing + dual model
│   └── CustomModelInference.kt          # (New) TFLite inference
├── preprocessing/
│   └── ImagePreprocessor.kt             # (New) Image preprocessing pipeline
├── ml/
│   ├── ModelManager.kt                  # (New) Model versioning & updates
│   └── ModelAnalytics.kt                # (New) Performance tracking
├── training/
│   ├── TrainingSampleEntity.kt          # (New) Training data model
│   ├── TrainingSampleDao.kt             # (New) Database access
│   └── TrainingDataUploader.kt          # (New) Upload service
└── data/
    └── NoteDatabase.kt                  # (Modified) Add training_samples table

app/src/main/assets/
└── ml_models/
    ├── handwriting_baseline.tflite      # (New) Bundled custom model
    └── model_metadata.json              # (New) Model version info

training/                                 # (New) Python training infrastructure
├── datasets/
├── models/
├── training/
└── deployment/
```

### Success Metrics

**Immediate (Phase 1 - Preprocessing)**:
- 15-25% reduction in Character Error Rate
- < 500ms preprocessing time per image
- No increase in app size or memory usage

**Short-term (Phase 3 - Custom Model)**:
- < 10% CER on handwritten text
- < 5% CER on printed text
- < 10MB increase in app size
- < 200ms inference time on mid-range devices

**Long-term (Phase 6 - Continuous Improvement)**:
- > 500 user-contributed training samples per month
- Model updates every 2-3 months
- Continuous decrease in CER (target: < 5% for handwriting)
- > 80% user satisfaction with OCR accuracy

### Open Questions & Future Enhancements

**Open Questions**:
1. Should we support offline-first training (on-device model updates)?
2. Multi-language support priority (Spanish, French, German)?
3. Real-time OCR (during camera preview) vs. post-capture?
4. Integration with stylus input (for tablets)?

**Future Enhancements**:
- **Document Layout Analysis**: Preserve formatting, tables, lists
- **Mathematical Equation Recognition**: Support for math notation
- **Multi-column Text**: Handle complex page layouts
- **Signature Recognition**: Detect and preserve signatures
- **Diagram Extraction**: Identify and extract diagrams separately from text
- **Cloud Backup**: Sync models and training data across devices