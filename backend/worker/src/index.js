/**
 * NIKA AI private chat gateway (Cloudflare Workers).
 *
 * Required Worker secrets:
 *   GEMINI_API_KEY  - Google AI Studio key; never put this in the Android app.
 *   NIKA_GATE_TOKEN - temporary server-side access gate; do not ship it in the APK.
 *
 * Keep this endpoint private until a proper per-user authentication flow and
 * Cloudflare rate limiting are configured. A token embedded in an APK is not secret.
 */
export default {
  async fetch(request, env) {
    if (request.method !== "POST") {
      return json({ error: "Method not allowed" }, 405);
    }

    if (!env.GEMINI_API_KEY || !env.NIKA_GATE_TOKEN) {
      return json({ error: "Server is not configured" }, 503);
    }

    const auth = request.headers.get("Authorization") || "";
    if (auth !== `Bearer ${env.NIKA_GATE_TOKEN}`) {
      return json({ error: "Unauthorized" }, 401);
    }

    let body;
    try {
      body = await request.json();
    } catch {
      return json({ error: "Invalid JSON" }, 400);
    }

    const message = typeof body?.message === "string" ? body.message.trim() : "";
    if (!message || message.length > 5000) {
      return json({ error: "Message must contain 1–5000 characters" }, 400);
    }

    const model = env.GEMINI_MODEL || "gemini-3.1-flash-lite";
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 20000);

    try {
      const upstream = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/${encodeURIComponent(model)}:generateContent`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "x-goog-api-key": env.GEMINI_API_KEY,
          },
          body: JSON.stringify({
            systemInstruction: {
              parts: [{
                text: "تو نیکا هستی، یک دستیار فارسی‌زبان مهربان، دقیق و مسئولیت‌پذیر. پاسخ‌ها را روشن و متناسب با سن کاربر بده. اگر مطمئن نیستی، صادقانه بگو."
              }]
            },
            contents: [{ role: "user", parts: [{ text: message }] }],
            generationConfig: { maxOutputTokens: 700, temperature: 0.7 }
          }),
          signal: controller.signal,
        }
      );

      if (!upstream.ok) {
        // Do not forward provider diagnostics or secrets to the client.
        return json({ error: "AI provider request failed" }, 502);
      }

      const data = await upstream.json();
      const answer = data?.candidates?.[0]?.content?.parts
        ?.map((part) => typeof part.text === "string" ? part.text : "")
        .join("")
        .trim();

      if (!answer) return json({ error: "No answer returned" }, 502);
      return json({ reply: answer }, 200);
    } catch {
      return json({ error: "AI service timed out or is unavailable" }, 504);
    } finally {
      clearTimeout(timeout);
    }
  }
};

function json(value, status) {
  return new Response(JSON.stringify(value), {
    status,
    headers: { "Content-Type": "application/json; charset=utf-8" },
  });
}
