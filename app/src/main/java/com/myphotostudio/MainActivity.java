
package com.myphotostudio;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    LinearLayout mainLayout, content;
    SharedPreferences prefs;

    String selectedTemplate = "Faceless Video";
    String selectedVideo = "";

    EditText promptInput, scriptInput;
    Spinner durationSpinner, ratioSpinner;

    int bg = Color.rgb(15, 17, 27);
    int card = Color.rgb(29, 33, 48);
    int accent = Color.rgb(115, 85, 255);
    int white = Color.WHITE;
    int muted = Color.rgb(175, 180, 195);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("MyStudio", MODE_PRIVATE);

        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);

        buildApp();
        showHome();
    }

    int dp(float value) {
        return (int) (value *
            getResources().getDisplayMetrics().density);
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    TextView text(String value, int size, int color,
                  boolean bold) {

        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);

        if (bold) {
            t.setTypeface(null, Typeface.BOLD);
        }

        return t;
    }

    void buildApp() {

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(bg);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18), dp(15), dp(18), dp(12));

        TextView logo = text("MY PHOTO STUDIO", 23,
                             white, true);

        TextView subtitle = text(
            "AI VIDEO CREATOR  •  EDITOR",
            11, muted, true
        );

        header.addView(logo);
        header.addView(subtitle);

        mainLayout.addView(header);

        HorizontalScrollView navScroll =
            new HorizontalScrollView(this);

        navScroll.setHorizontalScrollBarEnabled(false);

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(dp(10), dp(5), dp(10), dp(10));

        String[] names = {
            "Home", "Create", "Shorts",
            "Templates", "Projects", "Settings"
        };

        for (String name : names) {
            Button b = new Button(this);
            b.setText(name);
            b.setTextSize(11);
            b.setTextColor(white);
            b.setAllCaps(false);
            b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(card)
            );

            b.setOnClickListener(v -> navigate(name));

            nav.addView(b, new LinearLayout.LayoutParams(
                dp(95), dp(45)
            ));
        }

        navScroll.addView(nav);
        mainLayout.addView(navScroll);

        ScrollView scroll = new ScrollView(this);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(10), dp(16), dp(25));

        scroll.addView(content);

        mainLayout.addView(scroll,
            new LinearLayout.LayoutParams(
                -1, 0, 1
            )
        );

        setContentView(mainLayout);
    }

    void navigate(String page) {

        switch (page) {

            case "Home":
                showHome();
                break;

            case "Create":
                showCreate();
                break;

            case "Shorts":
                showShorts();
                break;

            case "Templates":
                showTemplates();
                break;

            case "Projects":
                showProjects();
                break;

            case "Settings":
                showSettings();
                break;
        }
    }

    LinearLayout newPage(String title, String description) {

        content.removeAllViews();

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);

        page.addView(text(title, 25, white, true));

        TextView desc = text(description, 13, muted, false);
        desc.setPadding(0, dp(5), 0, dp(18));

        page.addView(desc);
        content.addView(page);

        return page;
    }

    LinearLayout addCard(LinearLayout parent,
                         String title, String description) {

        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(15), dp(16), dp(15));
        box.setBackground(shape(card, 17));

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, -2);

        params.bottomMargin = dp(12);

        parent.addView(box, params);

        box.addView(text(title, 17, white, true));

        TextView desc = text(description, 13, muted, false);
        desc.setPadding(0, dp(7), 0, 0);

        box.addView(desc);

        return box;
    }

    Button addButton(LinearLayout parent, String label,
                     Runnable action) {

        Button button = new Button(this);

        button.setText(label);
        button.setTextSize(13);
        button.setAllCaps(false);
        button.setTextColor(white);

        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(accent)
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(-1, dp(50));

        params.topMargin = dp(8);
        params.bottomMargin = dp(8);

        parent.addView(button, params);

        button.setOnClickListener(v -> action.run());

        return button;
    }

    void showHome() {

        LinearLayout page = newPage(
            "Create something amazing",
            "Your AI-powered video creation studio."
        );

        LinearLayout banner = addCard(
            page,
            "YOUR VIDEO STUDIO",
            "Turn your ideas, scripts and stories into engaging videos."
        );

        banner.addView(text(
            "TEXT → SCRIPT → VIDEO → EXPORT",
            13, white, true
        ));

        addButton(page, "＋  Create New AI Video",
            () -> showCreate());

        addCard(page,
            "AI Script Writer",
            "Turn a simple idea into a complete video script."
        );

        addButton(page, "Write a Script",
            () -> showCreate());

        addCard(page,
            "Long Video to Shorts",
            "Prepare long videos for vertical social media clips."
        );

        addButton(page, "Create Shorts",
            () -> showShorts());

        addCard(page,
            "AI Voiceover",
            "Plan narration, voice selection and language options."
        );

        addCard(page,
            "Automatic Captions",
            "Prepare subtitles and caption styles for your videos."
        );

        addCard(page,
            "Video Templates",
            "Faceless videos, advertisements, explainers and stories."
        );

        addButton(page, "Explore Templates",
            () -> showTemplates());

        addCard(page,
            "My Projects",
            "Continue working on your saved video ideas."
        );

        addButton(page, "Open Projects",
            () -> showProjects());
    }

    void showCreate() {

        LinearLayout page = newPage(
            "AI Video Generator",
            "Describe your idea or paste your complete script."
        );

        LinearLayout promptCard = addCard(
            page,
            "1. Describe your video",
            "What video do you want to create?"
        );

        promptInput = new EditText(this);
        promptInput.setTextColor(white);
        promptInput.setHintTextColor(muted);
        promptInput.setHint(
            "Example: Create an African folktale about a mysterious village..."
        );
        promptInput.setGravity(Gravity.TOP);
        promptInput.setMinLines(4);
        promptInput.setPadding(dp(12), dp(12), dp(12), dp(12));
        promptInput.setBackground(shape(
            Color.rgb(43, 47, 64), 12
        ));

        promptCard.addView(promptInput);

        LinearLayout scriptCard = addCard(
            page,
            "2. Full video script",
            "Paste your script here or write your own."
        );

        scriptInput = new EditText(this);
        scriptInput.setTextColor(white);
        scriptInput.setHintTextColor(muted);
        scriptInput.setHint("Enter your video script...");
        scriptInput.setGravity(Gravity.TOP);
        scriptInput.setMinLines(5);
        scriptInput.setPadding(dp(12), dp(12), dp(12), dp(12));
        scriptInput.setBackground(shape(
            Color.rgb(43, 47, 64), 12
        ));

        scriptCard.addView(scriptInput);

        LinearLayout options = addCard(
            page,
            "3. Video settings",
            "Choose your preferred video format."
        );

        addLabel(options, "Video duration");

        durationSpinner = addSpinner(options,
            new String[]{
                "30 seconds", "1 minute",
                "3 minutes", "5 minutes", "10 minutes"
            });

        addLabel(options, "Aspect ratio");

        ratioSpinner = addSpinner(options,
            new String[]{
                "9:16 - TikTok / Reels / Shorts",
                "16:9 - YouTube",
                "1:1 - Square"
            });

        addLabel(options, "Selected template");

        TextView template = text(
            selectedTemplate, 15, white, true
        );

        options.addView(template);

        addButton(page, "Choose Template",
            () -> showTemplates());

        addButton(page, "Prepare Video Project",
            () -> saveProject());

        addCard(page,
            "AI generation connection",
            "Video generation, AI narration, stock footage and automatic captions will become available after connecting the AI services."
        );
    }

    void addLabel(LinearLayout parent, String label) {

        TextView t = text(label, 13, muted, true);
        t.setPadding(0, dp(12), 0, dp(5));

        parent.addView(t);
    }

    Spinner addSpinner(LinearLayout parent, String[] items) {

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
            );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        parent.addView(spinner);

        return spinner;
    }

    void saveProject() {

        String prompt = promptInput.getText().toString().trim();
        String script = scriptInput.getText().toString().trim();

        if (prompt.isEmpty() && script.isEmpty()) {

            Toast.makeText(this,
                "Enter a video idea or script first.",
                Toast.LENGTH_SHORT).show();

            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("last_prompt", prompt);
        editor.putString("last_script", script);
        editor.putString("last_template", selectedTemplate);

        editor.putString("last_duration",
            durationSpinner.getSelectedItem().toString());

        editor.putString("last_ratio",
            ratioSpinner.getSelectedItem().toString());

        editor.putLong("last_saved", System.currentTimeMillis());

        editor.apply();

        new android.app.AlertDialog.Builder(this)
            .setTitle("Project saved")
            .setMessage(
                "Your video project draft has been saved.\n\n" +
                "The next development step is connecting the AI " +
                "generation service to create the actual video."
            )
            .setPositiveButton("OK", (d, w) -> showProjects())
            .show();
    }

    void showTemplates() {

        LinearLayout page = newPage(
            "Video Templates",
            "Choose a workflow for your next creation."
        );

        String[][] templates = {
            {"Faceless Video",
             "Create narration-based videos with visuals."},

            {"African Storytelling",
             "Folktales, characters and cinematic storytelling."},

            {"YouTube Explainer",
             "Educational videos and informative content."},

            {"Marketing Advertisement",
             "Product promotions and advertising videos."},

            {"Podcast Clips",
             "Short clips from longer discussions."},

            {"News Style",
             "News presentation and informational videos."},

            {"Motivational Video",
             "Motivational scripts, narration and captions."},

            {"Animation",
             "Animated storytelling and visual scenes."}
        };

        for (String[] item : templates) {

            LinearLayout box = addCard(
                page, item[0], item[1]
            );

            addButton(box, "Select Template",
                () -> {

                    selectedTemplate = item[0];

                    Toast.makeText(this,
                        item[0] + " selected",
                        Toast.LENGTH_SHORT).show();

                    showCreate();
                });
        }
    }

    void showShorts() {

        LinearLayout page = newPage(
            "Long Video to Shorts",
            "Prepare your long videos for short-form editing."
        );

        LinearLayout box = addCard(
            page,
            "Select a source video",
            "Choose a video from your phone."
        );

        addButton(box, "Choose Video",
            () -> chooseVideo());

        addCard(page,
            "AI Context Analysis",
            "Identify interesting moments, important ideas and strong hooks."
        );

        addCard(page,
            "Automatic Clip Selection",
            "Prepare short video segments from your source video."
        );

        addCard(page,
            "Smart Reframing",
            "Plan 9:16 vertical, 1:1 square and 16:9 landscape formats."
        );

        addCard(page,
            "Dynamic Captions",
            "Plan animated subtitles and highlighted keywords."
        );

        addCard(page,
            "Silence Removal",
            "Prepare automatic pause and filler-word removal."
        );

        addButton(page, "Prepare Shorts Project",
            () -> {

                if (selectedVideo.isEmpty()) {

                    Toast.makeText(this,
                        "Choose a video first.",
                        Toast.LENGTH_SHORT).show();

                    return;
                }

                Toast.makeText(this,
                    "Video selected. AI clipping service still needs connection.",
                    Toast.LENGTH_LONG).show();
            });
    }

    void chooseVideo() {

        Intent intent = new Intent(
            Intent.ACTION_OPEN_DOCUMENT
        );

        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                   int resultCode,
                                   Intent data) {

        super.onActivityResult(
            requestCode, resultCode, data
        );

        if (requestCode == 101 &&
            resultCode == RESULT_OK &&
            data != null) {

            Uri uri = data.getData();

            if (uri != null) {

                selectedVideo = uri.toString();

                getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                Toast.makeText(this,
                    "Video selected successfully.",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    void showProjects() {

        LinearLayout page = newPage(
            "My Projects",
            "Your saved video creation projects."
        );

        String prompt = prefs.getString(
            "last_prompt", ""
        );

        String script = prefs.getString(
            "last_script", ""
        );

        String template = prefs.getString(
            "last_template", ""
        );

        if (prompt.isEmpty() && script.isEmpty()) {

            addCard(page,
                "No saved projects yet",
                "Start by creating your first video project."
            );

            addButton(page, "Create First Project",
                () -> showCreate());

            return;
        }

        LinearLayout project = addCard(
            page,
            "Latest Video Project",
            "Template: " + template
        );

        project.addView(text(
            "Idea: " + prompt,
            13, muted, false
        ));

        project.addView(text(
            "Script: " + script,
            13, muted, false
        ));

        addButton(page, "Continue Editing",
            () -> {

                showCreate();

                promptInput.setText(prompt);
                scriptInput.setText(script);
            });
    }

    void showSettings() {

        LinearLayout page = newPage(
            "Settings",
            "Manage your video studio."
        );

        addCard(page,
            "App Information",
            "MyPhotoStudio AI Video Creator"
        );

        addCard(page,
            "AI Services",
            "Connect video generation, AI voice, captions and stock media services."
        );

        addCard(page,
            "Languages",
            "Prepare multilingual narration and subtitles."
        );

        addCard(page,
            "Export Settings",
            "Configure MP4 video output, aspect ratio and quality."
        );

        addCard(page,
            "Account",
            "Account and cloud project features can be added later."
        );

        addCard(page,
            "Version",
            "MyPhotoStudio AI Video Studio - Version 2.0"
        );
    }
}
