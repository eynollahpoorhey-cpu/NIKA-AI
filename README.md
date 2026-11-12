# NIKA AI

A Persian-language Android assistant prototype built with Kotlin and Jetpack Compose.

## Implemented
- Persian right-to-left chat interface with blue-and-white styling.
- Demo replies (keyword-based; not real AI).
- Persian speech-recognition handoff through Android's speech-recognition service.
- Text-to-speech playback controls and adjustable speech speed (Persian voice availability depends on the device).
- HTTPS gateway client with request validation, timeouts, and bearer-token support.
- GitHub Actions workflow that builds and uploads a debug APK.

## Not yet connected or implemented
- The gateway client is not yet called by the chat screen.
- No deployed backend URL or runtime sign-in/token flow is configured.
- Real Gemini responses are not active until a backend is deployed and securely configured.
- Image generation/editing, web search, optional user-controlled memory, 3D avatar, and lip animation are not implemented.

## Security
- Never put provider API keys or long-lived gateway tokens in the Android app, BuildConfig, or this public repository.
- The Cloudflare Worker starter expects server-side secrets. Do not expose it publicly until proper user authentication, abuse protection, and rate limits are in place.
- A successful debug build does not guarantee that Google Play Protect will allow installation. Do not disable device protection to install a test build.

## Build and download
The GitHub Actions workflow runs on pushes to `main` and can also be started manually. A successful run uploads an artifact named `NIKA-AI-debug-apk`. Download it from the run's **Artifacts** section. Artifacts expire after seven days and are test builds, not Play Store releases.

## Release signing
A signed release APK is produced only when all four GitHub Actions secrets are configured:
- `NIKA_KEYSTORE_BASE64`
- `NIKA_KEYSTORE_PASSWORD`
- `NIKA_KEY_ALIAS`
- `NIKA_KEY_PASSWORD`

Keep the keystore and passwords private and backed up securely. Never commit them to this repository.
