# NIKA-AI

A first Android starter for the Persian-language NIKA AI assistant.

## Included in this starter
- Native Android app shell using Kotlin and Jetpack Compose.
- Persian chat interface with demo-only replies.
- GitHub Actions workflow intended to build a debug APK and publish it as a short-lived artifact.

## Not implemented yet
- Real AI responses (requires a backend service and securely managed API credentials).
- Voice input/output and speech settings.
- Image generation/editing integration.
- Web search integration.
- Optional user-controlled memory and privacy controls.
- 3D/anime character model and lip animation.

Do not put API keys, passwords, or personal credentials in this repository or in the APK.

## Build
The workflow is configured to run on pushes to the main branch or manually from the Actions tab. A successful run should publish an artifact named `NIKA-AI-debug-apk`. This is a test build, not a Play Store release.
