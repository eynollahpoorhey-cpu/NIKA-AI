# NIKA AI Gateway (starter)

This is a **backend starter**, not a deployed service. The Android app is not connected to it yet.

## What it does

- Accepts a JSON POST body: `{"message":"سلام"}`
- Checks a server-side bearer gate before forwarding anything.
- Calls Gemini through Google's `generateContent` REST API.
- Keeps the Gemini API key on the server, never in Android source or the APK.
- Limits each message to 5,000 characters and applies a 20-second timeout.
- Returns only `{"reply":"..."}` to the client.

## Adult-assisted setup required

A parent/guardian should manage any provider account, API key, billing settings, and deployment.

1. Create a Cloudflare Worker from this folder using Wrangler.
2. Add the secrets in the Worker environment (never commit them):
   - `GEMINI_API_KEY`
   - `NIKA_GATE_TOKEN` (generate a long random value)
3. Keep the Worker private while testing. Configure Cloudflare rate limiting and monitoring before any public release.
4. Check the Gemini free-tier limits and data-use terms before sending real conversations. Do not send passwords, addresses, or other private information.

Example secret commands (run locally in a trusted terminal, not in GitHub source):

```sh
npx wrangler secret put GEMINI_API_KEY
npx wrangler secret put NIKA_GATE_TOKEN
```

Deploy only after reviewing the Cloudflare and Google account settings:

```sh
npx wrangler deploy
```

## Important security limitation

The bearer gate is only a temporary server-side lock. **Do not embed `NIKA_GATE_TOKEN` in the Android APK**: anyone can extract it. Before connecting the mobile app, add a proper user authentication design and enforce rate limits/quotas at the gateway. This repository intentionally does not contain either secret or a public endpoint URL.

## API contract

Request:

```http
POST /
Authorization: Bearer <server-side gate token>
Content-Type: application/json

{"message":"سلام نیکا"}
```

Success:

```json
{"reply":"سلام! خوش اومدی 💙"}
```
