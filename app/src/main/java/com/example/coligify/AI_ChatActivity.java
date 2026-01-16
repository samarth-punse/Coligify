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
import androidx.appcompat.app.AlertDialog;
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
import org.json.JSONException;
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

    /* ================= MIC ================= */
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private Animation micAnimation;
    private static final int REQ_RECORD_AUDIO = 101;

    /* ================= STORAGE ================= */
    private SharedPreferences chatPrefs;
    private String currentChatId;
    private final ArrayList<ChatMessage> currentMessages = new ArrayList<>();

    /* ================= GEMINI ================= */
    private static final String API_KEY = "AIzaSyDlqFAGPm2Q8Twwm44HwLVZkAkfwpZNyi4";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final MediaType JSON =
            MediaType.parse("application/json; charset=utf-8");

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_chat);

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

        setupInsets();
        setupInput();
        setupSpeech();
        setupFilePicker();
    }

    /* ================= CHAT ================= */

    private void startNewChat() {
        currentChatId = String.valueOf(System.currentTimeMillis());
        currentMessages.clear();
        chatContainer.removeAllViews();
    }

    private void sendTypedMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) return;

        if (currentMessages.isEmpty()) saveChatHeader(msg);

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
        List<ChatHistoryItem> list = new ArrayList<>();
        for (String key : chatPrefs.getAll().keySet()) {
            try {
                JSONObject chat = new JSONObject(chatPrefs.getString(key, ""));
                list.add(new ChatHistoryItem(key, chat.getString("title")));
            } catch (Exception ignored) {}
        }
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(new HistoryAdapter(list));
    }

    private void openChat(String chatId) {
        currentChatId = chatId;
        currentMessages.clear();
        chatContainer.removeAllViews();

        try {
            JSONObject chat = new JSONObject(chatPrefs.getString(chatId, ""));
            JSONArray messages = chat.getJSONArray("messages");

            for (int i = 0; i < messages.length(); i++) {
                JSONObject m = messages.getJSONObject(i);
                String role = m.getString("role");
                String text = m.getString("text");

                currentMessages.add(new ChatMessage(role, text));
                if (role.equals("user")) addUserMessage(text);
                else addBotMessageWithoutSaving(text);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load chat", Toast.LENGTH_SHORT).show();
        }
    }

    /* ================= DELETE CHAT (CUSTOM DIALOG) ================= */

    private void deleteChat(String chatId) {
        chatPrefs.edit().remove(chatId).apply();
        if (chatId.equals(currentChatId)) startNewChat();
        loadChatHistory();
    }

    private void showDeleteDialog(ChatHistoryItem item) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_delete_chat, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView btnCancel = view.findViewById(R.id.tvCancel);
        TextView btnDelete = view.findViewById(R.id.tvDelete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            deleteChat(item.chatId);
            dialog.dismiss();
        });

        dialog.show();
    }

    /* ================= ADAPTER ================= */

    class HistoryAdapter extends RecyclerView.Adapter<HistoryVH> {

        List<ChatHistoryItem> list;

        HistoryAdapter(List<ChatHistoryItem> list) {
            this.list = list;
        }

        @Override
        public HistoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_history, parent, false);
            return new HistoryVH(v);
        }

        @Override
        public void onBindViewHolder(HistoryVH holder, int position) {
            ChatHistoryItem item = list.get(position);
            holder.tv.setText(item.title);

            holder.itemView.setOnClickListener(v -> {
                openChat(item.chatId);
                drawerLayout.closeDrawer(GravityCompat.END);
            });

            holder.itemView.setOnLongClickListener(v -> {
                showDeleteDialog(item);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    static class HistoryVH extends RecyclerView.ViewHolder {
        TextView tv;
        HistoryVH(View v) {
            super(v);
            tv = v.findViewById(R.id.tvTitle);
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

    private void addBotMessageWithoutSaving(String msg) {
        View v = LayoutInflater.from(this)
                .inflate(R.layout.item_bot_message, chatContainer, false);
        ((TextView) v.findViewById(R.id.tvBotMessage)).setText(msg);
        chatContainer.addView(v);
        scrollDown();
    }

    private void scrollDown() {
        contentScroll.post(() -> contentScroll.fullScroll(View.FOCUS_DOWN));
    }

    /* ================= MIC ================= */

    private void setupSpeech() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle p) {
                btnMic.startAnimation(micAnimation);
            }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rms) {}
            @Override public void onBufferReceived(byte[] b) {}
            @Override public void onEndOfSpeech() {
                btnMic.clearAnimation();
            }
            @Override public void onError(int e) {
                btnMic.clearAnimation();
            }
            @Override public void onResults(Bundle r) {
                btnMic.clearAnimation();
                ArrayList<String> m =
                        r.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (m != null && !m.isEmpty()) sendTypedMessage(m.get(0));
            }
            @Override public void onPartialResults(Bundle r) {}
            @Override public void onEvent(int t, Bundle b) {}
        });

        btnMic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQ_RECORD_AUDIO);
            } else {
                speechRecognizer.startListening(speechIntent);
            }
        });
    }

    /* ================= GEMINI ================= */

    private void sendToGemini(String message) {
        try {
            JSONObject body = new JSONObject();
            JSONArray contents = new JSONArray();

            JSONObject user = new JSONObject();
            JSONArray parts = new JSONArray();
            parts.put(new JSONObject().put("text", message));
            user.put("role", "user");
            user.put("parts", parts);
            contents.put(user);
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
                    try {
                        JSONObject json = new JSONObject(response.body().string());

                        if (json.has("error")) {
                            runOnUiThread(() ->
                                    {
                                        try {
                                            addBotMessage("❌ " +
                                                    json.getJSONObject("error")
                                                            .optString("message"));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            );
                            return;
                        }

                        JSONArray parts = json.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts");

                        runOnUiThread(() ->
                                {
                                    try {
                                        addBotMessage(parts.getJSONObject(0)
                                                .getString("text"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    } catch (Exception e) {
                        runOnUiThread(() ->
                                addBotMessage("❌ Parse error")
                        );
                    }
                }
            });
        } catch (Exception e) {
            addBotMessage("❌ Request error");
        }
    }

    /* ================= UTILS ================= */

    private void setupInsets() {
        View root = findViewById(R.id.llaichat);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, i) -> {
            Insets s = i.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = i.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(s.left, s.top, s.right, 0);
            bottomBar.setTranslationY(-Math.max(s.bottom, ime.bottom));
            return i;
        });
    }

    private void setupInput() {
        etPrompt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c){
                boolean empty = s.toString().trim().isEmpty();
                btnSend.setVisibility(empty ? View.GONE : View.VISIBLE);
                btnMic.setVisibility(empty ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s){}
        });

        btnSend.setOnClickListener(v ->
                sendTypedMessage(etPrompt.getText().toString()));
    }

    private void setupFilePicker() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                r -> {
                    if (r.getResultCode() == RESULT_OK && r.getData() != null) {
                        addUserMessage("📎 " +
                                getFileName(r.getData().getData()));
                    }
                });

        ivAdd.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            filePickerLauncher.launch(i);
        });
    }

    private String getFileName(Uri uri) {
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            String n = c.getString(
                    c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            c.close();
            return n;
        }
        return "File";
    }

    /* ================= MODELS ================= */

    static class ChatMessage {
        String role, text;
        ChatMessage(String r, String t) {
            role = r;
            text = t;
        }
    }

    static class ChatHistoryItem {
        String chatId, title;
        ChatHistoryItem(String id, String t) {
            chatId = id;
            title = t;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }
}
