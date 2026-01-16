package com.example.coligify;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AI_ChatActivity extends AppCompatActivity {

    /* ================= UI ================= */
    private EditText etPrompt;
    private ImageView btnMic, btnSend, btnBack, ivAdd, btnMenu;
    private LinearLayout chatContainer;
    private ScrollView contentScroll;
    private View bottomBar;
    private DrawerLayout drawerLayout;
    private RecyclerView rvHistory;

    /* ================= SPEECH ================= */
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private Animation micAnimation;

    /* ================= CHAT STORAGE ================= */
    private SharedPreferences chatPrefs;
    private String currentChatId;
    private final ArrayList<ChatMessage> currentMessages = new ArrayList<>();

    private static final int REQ_RECORD_AUDIO = 101;

    /* ================= GEMINI ================= */
    // 🔐 USE API KEY FROM https://aistudio.google.com/app/apikey
    private static final String API_KEY = "AIzaSyA7njnuWkqQDYoYs3PEYt3R7Ia4n7KEL30";

    // ✅ SAFE & AVAILABLE MODEL
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    private ActivityResultLauncher<Intent> filePickerLauncher;

    /* ================= ACTIVITY ================= */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_chat);

        /* Views */
        etPrompt = findViewById(R.id.etPrompt);
        btnMic = findViewById(R.id.btnMic);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        ivAdd = findViewById(R.id.ivAdd);
        btnMenu = findViewById(R.id.btnMenu);
        chatContainer = findViewById(R.id.chatContainer);
        contentScroll = findViewById(R.id.contentScroll);
        bottomBar = findViewById(R.id.bottomBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        rvHistory = findViewById(R.id.rvHistory);

        chatPrefs = getSharedPreferences("chat_db", MODE_PRIVATE);
        startNewChat();

        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            loadChatHistory();
            drawerLayout.openDrawer(GravityCompat.END);
        });

        micAnimation = AnimationUtils.loadAnimation(this, R.anim.mic_pulse);

        /* Keyboard handling */
        View root = findViewById(R.id.llaichat);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(sys.left, sys.top, sys.right, 0);
            bottomBar.setTranslationY(-Math.max(sys.bottom, ime.bottom));
            return insets;
        });

        /* Input */
        etPrompt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean empty = s.toString().trim().isEmpty();
                btnSend.setVisibility(empty ? View.GONE : View.VISIBLE);
                btnMic.setVisibility(empty ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v ->
                sendTypedMessage(etPrompt.getText().toString())
        );

        setupSpeechRecognizer();

        btnMic.setOnClickListener(v -> {
            if (checkMicPermission()) {
                speechRecognizer.startListening(speechIntent);
            }
        });

        /* File picker */
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        handleSelectedFile(result.getData().getData());
                    }
                }
        );

        ivAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        });
    }

    /* ================= CHAT SESSION ================= */

    private void startNewChat() {
        currentChatId = String.valueOf(System.currentTimeMillis());
        currentMessages.clear();
    }

    private void sendTypedMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) return;

        if (currentMessages.isEmpty()) {
            saveChatHeader(msg);
        }

        addUserMessage(msg);
        currentMessages.add(new ChatMessage("user", msg));

        etPrompt.setText("");
        sendToGemini(msg);
    }

    /* ================= STORAGE ================= */

    private void saveChatHeader(String title) {
        try {
            JSONObject chat = new JSONObject();
            chat.put("title", title);
            chat.put("messages", new JSONArray());
            chatPrefs.edit().putString(currentChatId, chat.toString()).apply();
        } catch (Exception ignored) {}
    }

    private void saveFullChat() {
        try {
            JSONObject chat = new JSONObject(chatPrefs.getString(currentChatId, "{}"));
            JSONArray arr = new JSONArray();

            for (ChatMessage m : currentMessages) {
                JSONObject o = new JSONObject();
                o.put("role", m.role);
                o.put("text", m.text);
                arr.put(o);
            }

            chat.put("messages", arr);
            chatPrefs.edit().putString(currentChatId, chat.toString()).apply();
        } catch (Exception ignored) {}
    }

    /* ================= HISTORY ================= */

    private void loadChatHistory() {
        List<String> titles = new ArrayList<>();

        for (String key : chatPrefs.getAll().keySet()) {
            try {
                JSONObject chat = new JSONObject(chatPrefs.getString(key, ""));
                titles.add(chat.getString("title"));
            } catch (Exception ignored) {}
        }

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(new RecyclerView.Adapter<HistoryVH>() {
            @Override
            public HistoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_history, parent, false);
                return new HistoryVH(v);
            }

            @Override
            public void onBindViewHolder(HistoryVH holder, int position) {
                holder.tv.setText(titles.get(position));
            }

            @Override
            public int getItemCount() {
                return titles.size();
            }
        });
    }

    static class HistoryVH extends RecyclerView.ViewHolder {
        TextView tv;
        HistoryVH(View v) {
            super(v);
            tv = v.findViewById(R.id.tvTitle);
        }
    }

    /* ================= SPEECH ================= */

    private boolean checkMicPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQ_RECORD_AUDIO
            );
            return false;
        }
        return true;
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {
                btnMic.startAnimation(micAnimation);
            }
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {
                btnMic.clearAnimation();
            }
            @Override public void onError(int error) {
                btnMic.clearAnimation();
            }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    sendTypedMessage(matches.get(0));
                }
            }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    /* ================= GEMINI (SAFE PARSING) ================= */

    private void sendToGemini(String message) {
        try {
            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();

            parts.put(new JSONObject().put("text", message));
            content.put("role", "user");
            content.put("parts", parts);
            contents.put(content);
            body.put("contents", contents);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            new OkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            addBotMessage("❌ Network error")
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    Log.d("GEMINI_RESPONSE", res);

                    try {
                        JSONObject json = new JSONObject(res);

                        if (json.has("error")) {
                            String err = json.getJSONObject("error")
                                    .optString("message", "Unknown error");
                            runOnUiThread(() -> addBotMessage("❌ " + err));
                            return;
                        }

                        JSONArray candidates = json.optJSONArray("candidates");
                        if (candidates == null || candidates.length() == 0) {
                            runOnUiThread(() -> addBotMessage("⚠️ No response from Gemini"));
                            return;
                        }

                        JSONObject content = candidates
                                .getJSONObject(0)
                                .optJSONObject("content");
                        if (content == null) {
                            runOnUiThread(() -> addBotMessage("⚠️ Empty response"));
                            return;
                        }

                        JSONArray parts = content.optJSONArray("parts");
                        if (parts == null || parts.length() == 0) {
                            runOnUiThread(() -> addBotMessage("⚠️ No text returned"));
                            return;
                        }

                        StringBuilder reply = new StringBuilder();
                        for (int i = 0; i < parts.length(); i++) {
                            reply.append(parts.getJSONObject(i).optString("text", ""));
                        }

                        runOnUiThread(() -> addBotMessage(reply.toString()));

                    } catch (Exception e) {
                        runOnUiThread(() -> addBotMessage("❌ Parse error"));
                    }
                }
            });

        } catch (Exception e) {
            addBotMessage("❌ Error");
        }
    }

    /* ================= UI ================= */

    private void addUserMessage(String msg) {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.item_user_message, chatContainer, false);
        ((TextView) v.findViewById(R.id.tvUserMessage)).setText(msg);
        chatContainer.addView(v);
        scrollDown();
    }

    private void addBotMessage(String msg) {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.item_bot_message, chatContainer, false);
        ((TextView) v.findViewById(R.id.tvBotMessage)).setText(msg);
        chatContainer.addView(v);

        currentMessages.add(new ChatMessage("bot", msg));
        saveFullChat();
        scrollDown();
    }

    private void scrollDown() {
        contentScroll.post(() ->
                contentScroll.fullScroll(View.FOCUS_DOWN)
        );
    }

    /* ================= FILE ================= */

    private void handleSelectedFile(Uri uri) {
        addUserMessage("📎 " + getFileName(uri));
    }

    private String getFileName(Uri uri) {
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            c.close();
            return name;
        }
        return "File";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }

    /* ================= MODEL ================= */

    static class ChatMessage {
        String role;
        String text;

        ChatMessage(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }
}
