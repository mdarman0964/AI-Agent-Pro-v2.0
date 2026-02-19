# 🤖 AI Agent Pro v2.0

Complete Multi-Agent AI Project Generator with ALL Features!

## ✨ v2.0 Features

### 🚀 Core Features
- ✅ **Real AI Integration** - Works with OpenAI, Moonshot (Kimi), Claude, DeepSeek, Groq, Gemini
- ✅ **Chat Interface** - Interactive chat with AI assistant
- ✅ **Code Editor** - Syntax highlighting with Sora Editor
- ✅ **Project History** - Room database for saved projects
- ✅ **ZIP Download** - Export projects as ZIP files
- ✅ **GitHub Integration** - Push directly to GitHub repos
- ✅ **Voice Input** - Speech-to-text for prompts
- ✅ **Multi-Language** - English + বাংলা (Bangla)
- ✅ **Dark Mode** - Full dark theme support
- ✅ **Project Templates** - 8+ pre-built templates

### 📱 Android App Features
| Feature | Description |
|---------|-------------|
| 🤖 5 AI Agents | Android, Python, UI/UX, Full-Stack, ML |
| 💬 Chat | Real-time chat with AI |
| 📝 Code Editor | Syntax highlighting for all languages |
| 📚 History | Save and manage generated projects |
| ⬇️ Download | ZIP export with share option |
| 🐙 GitHub | Direct push to repositories |
| 🎤 Voice | Speech recognition for input |
| 🌙 Dark Mode | Beautiful dark theme |
| 🌍 Bilingual | English + বাংলা |

### 🐍 Backend Features
| Feature | Description |
|---------|-------------|
| 🔌 Universal API | Works with 7+ AI providers |
| 📦 ZIP Generation | Server-side ZIP creation |
| 🐙 GitHub API | Push files to repos |
| ⚡ FastAPI | High-performance async API |
| 🐳 Docker | Containerized deployment |

## 🚀 Quick Start

### Backend
```bash
cd backend
pip install -r requirements.txt
uvicorn app.main:app --reload

# Or with Docker
docker-compose up -d
```

### Android App
```bash
cd android
./gradlew assembleDebug
```

## 📁 Project Structure

```
AI-Agent-Pro-v2.0/
├── android/
│   ├── app/
│   │   ├── src/main/java/com/aiagent/
│   │   │   ├── data/
│   │   │   │   ├── db/           # Room Database
│   │   │   │   ├── model/        # Data models
│   │   │   │   └── prefs/        # DataStore
│   │   │   ├── di/               # Hilt DI
│   │   │   ├── network/          # Retrofit API
│   │   │   ├── repository/       # Repositories
│   │   │   ├── ui/               # Activities & Fragments
│   │   │   │   ├── chat/         # Chat interface
│   │   │   │   ├── codeeditor/   # Code editor
│   │   │   │   ├── generate/     # Project generation
│   │   │   │   ├── history/      # Project history
│   │   │   │   ├── main/         # Main activity
│   │   │   │   ├── settings/     # Settings
│   │   │   │   └── setup/        # API setup
│   │   │   ├── utils/            # Utilities
│   │   │   └── viewmodel/        # ViewModels
│   │   └── res/                  # Resources
│   ├── build.gradle
│   └── settings.gradle
├── backend/
│   ├── app/
│   │   └── main.py               # FastAPI app
│   ├── requirements.txt
│   ├── Dockerfile
│   └── docker-compose.yml
└── README.md
```

## 🔌 Supported AI Providers

| Provider | Model | Status |
|----------|-------|--------|
| OpenAI | GPT-4 Turbo | ✅ Full |
| Moonshot | Kimi 2.5 | ✅ Full |
| Anthropic | Claude 3 Opus | ✅ Full |
| DeepSeek | DeepSeek Chat | ✅ Full |
| Groq | LLaMA2-70B | ✅ Full |
| Google | Gemini Pro | ✅ Full |
| Custom | Your LLM | ✅ Full |

## 🛠️ Tech Stack

**Android:**
- Kotlin + Coroutines + Flow
- MVVM Architecture
- Hilt Dependency Injection
- Room Database
- Retrofit2 + OkHttp
- Material Design 3
- Sora Code Editor

**Backend:**
- FastAPI
- Pydantic v2
- Async HTTPX
- Docker

## 📝 API Endpoints

```
POST   /generate/project    # Generate project with AI
POST   /chat                # Chat with AI
POST   /github/push         # Push to GitHub
POST   /download/zip        # Download ZIP
GET    /providers           # List providers
GET    /health              # Health check
```

## 🎯 Usage

### 1. Setup API Key
```
Launch app → Enter API Key (OpenAI/Moonshot/etc.) → Save
```

### 2. Select Agent
```
Home → Select Agent (Android/Python/etc.) → Choose Template
```

### 3. Generate Project
```
Enter description → Add features → Tap Generate
```

### 4. Export Options
```
- Download ZIP
- Push to GitHub
- View in Code Editor
- Save to History
```

## 🌟 v2.0 vs v1.0

| Feature | v1.0 | v2.0 |
|---------|------|------|
| Real AI | ❌ Mock | ✅ Real API |
| Chat | ❌ | ✅ Full chat |
| Code Editor | ❌ | ✅ Sora Editor |
| History | ❌ | ✅ Room DB |
| Download | ❌ | ✅ ZIP export |
| GitHub | ❌ | ✅ Push to repo |
| Voice | ❌ | ✅ Speech input |
| Dark Mode | ❌ | ✅ Full support |
| Templates | ❌ | ✅ 8+ templates |

## 📄 License

MIT License

---

**Made with ❤️ for developers worldwide!**
