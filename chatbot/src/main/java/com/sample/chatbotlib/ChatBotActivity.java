package com.sample.chatbotlib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatBotActivity extends Activity {
    public static final String TRANSITION_HEADER_TEXT = "transitionheaderText";
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    public ChatArrayAdapter adapter;
    public ArrayList<String> selectedOptions1 = new ArrayList<>();
    public TextToSpeech t1;
    JSONArray jsonArr;
    int sflag = 0;
    private ListView chatListView;
    private EditText chatEditText;
    private ImageView send;
    private int i = 0;
    private SpeechRecognizerManager mSpeechManager;
    private TextView typestatus;
    private Context context;
    private RelativeLayout form;
    private int anspos = 0;
    private Uri mImageCaptureUri;
    private boolean isListCompleted = false;
    private TextView text, subheader;
    private String header, filename;
    private ImageView back;
    private int REQUEST_IMAGE_CAPTURE = 11;
    private String filledjson, profilescreen;
    private String selectedDate = null, selectedpath = null;
    private int selectedradioCheck = -1;
    private String selectedSpinnerValue = null;
    private int reqcode;
    private String TAG = "ChatBot Recording";
    private Animation startAnimation;
    private int browsepos = -1;
    private ImageView next;
    private File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        text = (TextView) findViewById(R.id.text);
        subheader = (TextView) findViewById(R.id.subheader);
        back = (ImageView) findViewById(R.id.back);
        form = (RelativeLayout) findViewById(R.id.form);
        context = this;
        if (getIntent() != null) {
            callIntents();
            if (profilescreen != null) {
                subheader.setVisibility(View.VISIBLE);
                subheader.setText(profilescreen);
            }

        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new ChatArrayAdapter(getApplicationContext());

        typestatus = (TextView) findViewById(R.id.typestatus);
        typestatus.setVisibility(View.GONE);
        chatListView = (ListView) findViewById(R.id.chat_listView);
        chatListView.setAdapter(adapter);
        send = (ImageView) findViewById(R.id.send);

        chatEditText = (EditText) findViewById(R.id.chat_editText);
        if (filledjson == null) {
            // Render data on chatbot screen from ChatBotData.txt file of assets folder
            Log.d("FIlled DATA", "NULL");
            setData(filename);
        } else {
            // Render data on chatbot screen from local db
            setDatafromLocal();

        }

        chatEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    send.setImageResource(R.drawable.send);
                    sflag = 1;
                } else {
                    send.setImageResource(R.drawable.record);
                    sflag = 0;
                }
            }
        });
    }

    public void setData(String filename) {

        String readFile = AppUtilityFunction.readFromfile(filename, context);
        try {
            JSONObject jobj = new JSONObject(readFile);
            String c = jobj.getString("botdata");
            jsonArr = new JSONArray(c);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            ArrayList<String> options = new ArrayList<>();
            ArrayList<String> optionids = new ArrayList<>();
            String subtype = null;
            int input = 1;
            if (jsonArr.optJSONObject(0).has("options")) {
                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(0).optString("options"));

                for (int j = 0; j < foptionarray.length(); j++) {
                    String option = foptionarray.getJSONObject(j).getString("option");
                    String optionid = foptionarray.getJSONObject(j).getString("id");
                    options.add(option);
                    optionids.add(optionid);
                }
            }
            if (jsonArr.optJSONObject(0).has("subtype")) {
                subtype = jsonArr.optJSONObject(0).optString("subtype");
            }
            List<Object> res = new ArrayList<>();
            res = getOptions(0);
            options = (ArrayList) res.get(0);
            optionids = (ArrayList) res.get(1);
            if (jsonArr.optJSONObject(0).has("input")) {
                input = jsonArr.optJSONObject(0).optInt("input");
                if (input == 1) {
                    chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {

                    chatEditText.setInputType(input);
                }
                // chatEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(0).optString("question"), jsonArr.optJSONObject(0).optString("type"), subtype, jsonArr.optJSONObject(0).optString("option1"), jsonArr.optJSONObject(0).optString("option2"), options, optionids));

        } catch (JSONException e) {
            e.printStackTrace();
        }


        chatEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    final String answer = chatEditText.getText().toString().trim();
                    if (!answer.equals("")) {

                        //Save answer of current question in json array & render next question

                        updateAnswer(answer);
                    }
                    return true;
                }

                return false;
            }
        });

        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (sflag == 1) {
                    final String answer = chatEditText.getText().toString().trim();
                    if (!answer.equals("")) {
                        //Save answer of current question in json array & render next question
                        updateAnswer(answer);
                    }
                } else {
                    Log.d("RECORDING CODE", "YES" + sflag);
                    if (mSpeechManager == null) {
                        SetSpeechListener();
                    } else if (!mSpeechManager.ismIsListening()) {
                        mSpeechManager.destroy();
                        SetSpeechListener();
                    }
                    startAnimation = AnimationUtils.loadAnimation(context, R.anim.blinking_animation);
                    send.startAnimation(startAnimation);


                }

            }
        });
    }

    private void SetSpeechListener() {
        mSpeechManager = new SpeechRecognizerManager(this, new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {


                if (results != null && results.size() > 0) {

                    send.clearAnimation();
                    mSpeechManager.destroy();
                    mSpeechManager = null;
                    Log.d("RECORDED", results.get(0));
                    //Save answer of current question in json array & render next question
                    updateAnswer(results.get(0));

                } else {
                    send.clearAnimation();
                    //Toast.makeText(context, R.string.not_spoken, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (mSpeechManager != null) {
            mSpeechManager.destroy();
            mSpeechManager = null;
            send.clearAnimation();
        }
        super.onPause();
    }

    public void updateAnswer(final String answer) {
        sflag = 0;
        chatEditText.setText("");
        adapter.notifyDataSetChanged();
        typestatus.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                typestatus.setVisibility(View.GONE);

                if (i < jsonArr.length()) {
                    int input = -1;
                    String subtype = null;
                    String regex = jsonArr.optJSONObject(i).optString("regex");
                    String type = jsonArr.optJSONObject(i).optString("type");
                    if (jsonArr.optJSONObject(i).has("subtype"))
                        subtype = jsonArr.optJSONObject(i).optString("subtype");
                    if (type.equals("1")) {
                        if (!regex.equals("")) {
                            if (answer.matches(regex)) {
                                if (jsonArr.optJSONObject(i).has("input")) {
                                    input = jsonArr.optJSONObject(i).optInt("input");
                                    Log.d("Input type", input + "TYPEEEEEEE");
                                }
                                adapter.add(new ChatMessage(false, answer, true, input));
                                try {
                                    jsonArr.optJSONObject(i).put("answer", answer);
                                    i = i + 1;
                                    if (i < jsonArr.length()) {
                                        try {
                                            ArrayList<String> options = new ArrayList<>();
                                            ArrayList<String> optionids = new ArrayList<>();

                                            if (jsonArr.optJSONObject(i).has("options")) {
                                                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(i).optString("options"));
                                                for (int j = 0; j < foptionarray.length(); j++) {
                                                    String option = foptionarray.getJSONObject(j).getString("option");
                                                    String optionid = foptionarray.getJSONObject(j).getString("id");
                                                    options.add(option);
                                                    optionids.add(optionid);
                                                }
                                            }
                                            if (jsonArr.optJSONObject(i).has("subtype")) {
                                                subtype = jsonArr.optJSONObject(i).optString("subtype");
                                            }
                                            List<Object> res = new ArrayList<>();
                                            res = getOptions(i);
                                            options = (ArrayList) res.get(0);
                                            optionids = (ArrayList) res.get(1);

                                            int input1 = 1;
                                            if (jsonArr.optJSONObject(i).has("input")) {
                                                input1 = jsonArr.optJSONObject(i).optInt("input");
                                                if (input1 == 1) {
                                                    chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                    chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                                } else {

                                                    chatEditText.setInputType(input1);
                                                }
                                            } else {
                                                try {
                                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(i).optString("question"), jsonArr.optJSONObject(i).optString("type"), subtype, jsonArr.optJSONObject(i).optString("option1"), jsonArr.optJSONObject(i).optString("option2"), options, optionids));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else {


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                            /*try {

                                                t1.speak(getResources().getString(R.string.enter_wrong), TextToSpeech.QUEUE_FLUSH, null);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }*/
                                adapter.add(new ChatMessage(false, answer, false));
                                adapter.add(new ChatMessage(true, getResources().getString(R.string.enter_wrong), "1"));

                            }
                        } else {
                            adapter.add(new ChatMessage(false, answer, true));
                            try {
                                jsonArr.optJSONObject(i).put("answer", answer);
                                i = i + 1;
                                if (i < jsonArr.length()) {
                                    try {
                                        if (jsonArr.optJSONObject(i).has("subtype")) {
                                            subtype = jsonArr.optJSONObject(i).optString("subtype");
                                        }
                                        ArrayList<String> options = new ArrayList<>();
                                        ArrayList<String> optionids = new ArrayList<>();

                                        if (jsonArr.optJSONObject(i).has("options")) {
                                            JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(i).optString("options"));
                                            for (int j = 0; j < foptionarray.length(); j++) {
                                                String option = foptionarray.getJSONObject(j).getString("option");
                                                String optionid = foptionarray.getJSONObject(j).getString("id");
                                                options.add(option);
                                                optionids.add(optionid);
                                            }
                                        }
                                        List<Object> res = new ArrayList<>();
                                        res = getOptions(i);
                                        options = (ArrayList) res.get(0);
                                        optionids = (ArrayList) res.get(1);
                                        int input1 = 1;
                                        if (jsonArr.optJSONObject(i).has("input")) {
                                            input1 = jsonArr.optJSONObject(i).optInt("input");
                                            if (input1 == 1) {
                                                chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                            } else {

                                                chatEditText.setInputType(input1);
                                            }
                                        } else {
                                            try {
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        adapter.add(new ChatMessage(true, jsonArr.optJSONObject(i).optString("question"), jsonArr.optJSONObject(i).optString("type"), subtype, jsonArr.optJSONObject(i).optString("option1"), jsonArr.optJSONObject(i).optString("option2"), options, optionids));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {

                        adapter.add(new ChatMessage(true, getResources().getString(R.string.enter_wrong), "1"));
                    }
                }

            }
        }, 600);
    }

    public void browsePdf() {
        try {
            Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
            intent.putExtra("CONTENT_TYPE", "*/*");
            // intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(intent, 20);
        } catch (Exception e) {
            try {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(i, 20);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    public void getImageFrom() {


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        try {
            intent.putExtra("return-data", true);

            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        adapter.onActivityResult(requestCode, resultCode, result);

    }

    public List<Object> getOptions(int pos) {
        String subtype;
        ArrayList<String> options = new ArrayList<>();
        ArrayList<String> optionids = new ArrayList<>();
        List<Object> response = new ArrayList<>();
        if (jsonArr.optJSONObject(pos).has("subtype")) {
            subtype = jsonArr.optJSONObject(pos).optString("subtype");
            if (subtype.equals("C")) {
                options = AppUtility.countryLabel;
                optionids = AppUtility.countryValue;
            }

        } else {
            if (jsonArr.optJSONObject(pos).has("options")) {
                JSONArray foptionarray = null;
                try {
                    foptionarray = new JSONArray(jsonArr.optJSONObject(pos).optString("options"));
                    for (int j = 0; j < foptionarray.length(); j++) {
                        String option = foptionarray.getJSONObject(j).getString("option");
                        String optionid = foptionarray.getJSONObject(j).getString("id");
                        options.add(option);
                        optionids.add(optionid);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        response.add(options);
        response.add(optionids);
        return response;

    }

    public void callIntents() {
        header = getIntent().getStringExtra(AppUtilityFunction.HEADER_TEXT);
        filename = getIntent().getStringExtra(AppUtilityFunction.FILENAME);

        profilescreen = getIntent().getStringExtra(AppUtilityFunction.PROFILESCREEN);
        reqcode = getIntent().getIntExtra(AppUtilityFunction.REQCODE, 0);
        text.setText(header);
    }

    public void setDatafromLocal() {
        Log.d("FIlled DATA", "NOT NULL");
        next.setVisibility(View.VISIBLE);
        form.setVisibility(View.GONE);
        try {
            jsonArr = new JSONArray(filledjson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Update chatbot data if answer is there
        for (int u = 0; u < jsonArr.length() - 1; u++) {
            String subtype = null;
            String regex = jsonArr.optJSONObject(u).optString("regex");
            String type = jsonArr.optJSONObject(u).optString("type");
            if (jsonArr.optJSONObject(u).has("subtype")) {
                subtype = jsonArr.optJSONObject(u).optString("subtype");
            }
            String answer = jsonArr.optJSONObject(u).optString("answer");
            if (!answer.equals("")) {


                if (u < jsonArr.length()) {
                    try {
                        ArrayList<String> options = new ArrayList<>();
                        ArrayList<String> optionids = new ArrayList<>();

                        if (jsonArr.optJSONObject(u).has("options")) {
                            JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(u).optString("options"));
                            for (int j = 0; j < foptionarray.length(); j++) {
                                String option = foptionarray.getJSONObject(j).getString("option");
                                String optionid = foptionarray.getJSONObject(j).getString("id");
                                options.add(option);
                                optionids.add(optionid);

                            }
                        }
                        if (jsonArr.optJSONObject(u).has("subtype")) {
                            subtype = jsonArr.optJSONObject(u).optString("subtype");
                        }
                        if (type.equals("2")) {
                            if (answer.equalsIgnoreCase(jsonArr.optJSONObject(u).optString("option1"))) {
                                selectedradioCheck = 1;
                            } else {
                                selectedradioCheck = 0;
                            }
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));

                        }
                        if (type.equals("3")) {
                            selectedSpinnerValue = answer;
                            List<Object> res = new ArrayList<>();
                            res = getOptions(u);
                            options = (ArrayList) res.get(0);
                            optionids = (ArrayList) res.get(1);

                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));

                        }
                        if (type.equals("4")) {
                            selectedOptions1 = AppUtilityFunction.convertStringToArray(answer.trim());
                            Log.d("SELECTED OPTIONS", selectedOptions1.toString().trim());
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));

                        }
                        if (type.equals("5")) {
                            selectedDate = answer;
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));

                        }
                        if (type.equals("6")) {
                            selectedpath = answer;
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));


                        }

                        if (type.equals("1")) {
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(u).optString("question"), jsonArr.optJSONObject(u).optString("type"), subtype, jsonArr.optJSONObject(u).optString("option1"), jsonArr.optJSONObject(u).optString("option2"), options, optionids, selectedradioCheck, selectedSpinnerValue, selectedOptions1, selectedDate, selectedpath));

                            adapter.add(new ChatMessage(false, answer, true));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                }


            }


        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(AppUtilityFunction.ONRESULT_RESPONSE, jsonArr.toString());
                setResult(reqcode, intent);
                finish();
            }
        });
    }

    public void callnormalQues(ChatArrayAdapter.ViewHolder holder) {
        holder.messageTextView.setVisibility(View.VISIBLE);
        holder.radiolay.setVisibility(View.GONE);
        holder.spinnerlay.setVisibility(View.GONE);
        holder.checkboxlay.setVisibility(View.GONE);
        holder.datepickerlay.setVisibility(View.GONE);
        holder.docbrowselay.setVisibility(View.GONE);
        holder.catsubcatlay.setVisibility(View.GONE);
    }

    public void callRadioButtonQues(final ChatArrayAdapter.ViewHolder holder, final int position, final List<ChatMessage> chatMessages) {
        holder.messageTextView.setVisibility(View.GONE);
        holder.radiolay.setVisibility(View.VISIBLE);
        holder.radioq.setText(chatMessages.get(position).text);
        holder.radiob1.setText(chatMessages.get(position).option1);
        holder.radiob2.setText(chatMessages.get(position).option2);
        holder.spinnerlay.setVisibility(View.GONE);
        holder.checkboxlay.setVisibility(View.GONE);
        holder.datepickerlay.setVisibility(View.GONE);
        holder.docbrowselay.setVisibility(View.GONE);
        holder.catsubcatlay.setVisibility(View.GONE);
        Log.d("CheckRadio", chatMessages.get(position).radiocheck + "");
        if (chatMessages.get(position).radiocheck == 1) {
            ((RadioButton) holder.rgroup.getChildAt(0)).setChecked(true);

        } else if (chatMessages.get(position).radiocheck == 0) {
            ((RadioButton) holder.rgroup.getChildAt(1)).setChecked(true);
        } else {
            holder.rgroup.clearCheck();
        }
        holder.rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int qpos = -1;

                RadioButton radioSexButton = (RadioButton) group.findViewById(checkedId);
                JSONObject obj = null;
                String anss = null;
                Log.d("POS", position + "");

                for (int k = 0; k < jsonArr.length(); k++) {
                    obj = jsonArr.optJSONObject(k);
                    if (obj != null) {
                        String q = obj.optString("question");
                        anss = obj.optString("answer");
                        if (q.equals(holder.radioq.getText().toString())) {
                            qpos = k;
                            break;
                        } else {
                            qpos = -1;
                        }

                    }
                }

                Log.d("QuestionPos", qpos + "");
                if (qpos != -1 && anss.equals("")) {
                    Log.d("Addd", "radiobutton answer" + radioSexButton.getText());
                    try {
                        obj.put("answer", radioSexButton.getText());

                        if (radioSexButton.getText().toString().equalsIgnoreCase(obj.optString("option1"))) {
                            chatMessages.get(position).radiocheck = 1;
                        } else {
                            chatMessages.get(position).radiocheck = 0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    qpos = qpos + 1;
                    i = i + 1;
                    if (qpos < jsonArr.length()) {
                        try {
                            ArrayList<String> options = new ArrayList<>();
                            ArrayList<String> optionids = new ArrayList<>();
                            String subtype = null;
                            if (jsonArr.optJSONObject(qpos).has("options")) {
                                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                for (int j = 0; j < foptionarray.length(); j++) {
                                    String option = foptionarray.getJSONObject(j).getString("option");
                                    String optionid = foptionarray.getJSONObject(j).getString("id");
                                    options.add(option);
                                    optionids.add(optionid);
                                }
                            }
                            if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                            }
                            List<Object> res = new ArrayList<>();
                            res = getOptions(qpos);
                            options = (ArrayList) res.get(0);
                            optionids = (ArrayList) res.get(1);

                            int input = 1;
                            if (jsonArr.optJSONObject(qpos).has("input")) {
                                input = jsonArr.optJSONObject(qpos).optInt("input");
                                if (input == 1) {
                                    chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                } else {

                                    chatEditText.setInputType(input);
                                }
                            } else {
                                try {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        if (radioSexButton.getText().toString().equalsIgnoreCase(obj.optString("option1"))) {
                            // adapter.add(new ChatMessage(true, getResources().getString(R.string.congrts), "1"));
                            adapter.activateButtons(true);
                            form.setVisibility(View.GONE);
                            isListCompleted = true;
                            Intent intent = new Intent();
                            intent.putExtra(AppUtilityFunction.ONRESULT_RESPONSE, jsonArr.toString());
                            setResult(reqcode, intent);
                            finish();

                            try {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            qpos = qpos - 1;
                            ArrayList<String> options = new ArrayList<>();
                            ArrayList<String> optionids = new ArrayList<>();
                            String subtype = null;
                            if (jsonArr.optJSONObject(qpos).has("options")) {
                                JSONArray foptionarray = null;

                                try {
                                    foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));

                                    for (int j = 0; j < foptionarray.length(); j++) {
                                        String option = foptionarray.getJSONObject(j).getString("option");
                                        String optionid = foptionarray.getJSONObject(j).getString("id");
                                        options.add(option);
                                        optionids.add(optionid);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                            }
                            List<Object> res = new ArrayList<>();
                            res = getOptions(qpos);
                            options = (ArrayList) res.get(0);
                            optionids = (ArrayList) res.get(1);

                            int input = 1;
                            if (jsonArr.optJSONObject(qpos).has("input")) {
                                input = jsonArr.optJSONObject(qpos).optInt("input");
                                if (input == 1) {
                                    chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                } else {

                                    chatEditText.setInputType(input);
                                }
                            } else {
                                try {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    chatListView.smoothScrollToPosition(2);
                                }
                            }, 100);
                        }
                    }

                } else if (qpos != -1 && !anss.equals("")) {
                    Log.d("Update", "radiobutton answer" + anss);
                    if (!isListCompleted) {
                        try {
                            obj.put("answer", radioSexButton.getText());
                            if (radioSexButton.getText().toString().equalsIgnoreCase(obj.optString("option1"))) {
                                chatMessages.get(position).radiocheck = 1;
                            } else {
                                chatMessages.get(position).radiocheck = 0;
                            }
                            qpos = qpos + 1;
                            // i = i + 1;
                            if (qpos < jsonArr.length()) {
                                        /*try {
                                            ArrayList<String> options = new ArrayList<>();
                                            if (jsonArr.optJSONObject(qpos).has("options")) {
                                                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                                for (int j = 0; j < foptionarray.length(); j++) {
                                                    String option = foptionarray.getJSONObject(j).getString("option");
                                                    options.add(option);
                                                }
                                            }
                                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }*/

                            } else {
                                if (radioSexButton.getText().toString().equalsIgnoreCase(obj.optString("option1"))) {
                                    //   adapter.add(new ChatMessage(true, getResources().getString(R.string.congrts), "1"));
                                    adapter.activateButtons(true);
                                    form.setVisibility(View.GONE);
                                    isListCompleted = true;
                                    Intent intent = new Intent();
                                    intent.putExtra(AppUtilityFunction.ONRESULT_RESPONSE, jsonArr.toString());
                                    setResult(reqcode, intent);
                                    finish();
                                    try {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    qpos = qpos - 1;
                                    ArrayList<String> options = new ArrayList<>();
                                    ArrayList<String> optionids = new ArrayList<>();
                                    String subtype = null;
                                    if (jsonArr.optJSONObject(qpos).has("options")) {
                                        JSONArray foptionarray = null;
                                        try {
                                            foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));

                                            for (int j = 0; j < foptionarray.length(); j++) {
                                                String option = foptionarray.getJSONObject(j).getString("option");
                                                String optionid = foptionarray.getJSONObject(j).getString("id");
                                                options.add(option);
                                                optionids.add(optionid);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                        subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                                    }
                                    List<Object> res = new ArrayList<>();
                                    res = getOptions(qpos);
                                    options = (ArrayList) res.get(0);
                                    optionids = (ArrayList) res.get(1);
                                    int input = 1;
                                    if (jsonArr.optJSONObject(qpos).has("input")) {
                                        input = jsonArr.optJSONObject(qpos).optInt("input");
                                        if (input == 1) {
                                            chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        } else {

                                            chatEditText.setInputType(input);
                                        }
                                    } else {
                                        try {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            chatListView.smoothScrollToPosition(2);
                                        }
                                    }, 100);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
            }
        });
    }

    public void callSpinnerQues(final ChatArrayAdapter.ViewHolder holder, final int position, final List<ChatMessage> chatMessages) {
        holder.messageTextView.setVisibility(View.GONE);
        holder.radiolay.setVisibility(View.GONE);
        holder.spinnerlay.setVisibility(View.VISIBLE);
        holder.checkboxlay.setVisibility(View.GONE);
        holder.datepickerlay.setVisibility(View.GONE);
        holder.docbrowselay.setVisibility(View.GONE);
        holder.catsubcatlay.setVisibility(View.GONE);
        holder.spinnerq.setText(chatMessages.get(position).text);
        holder.cntrySP.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        if (chatMessages.get(position).options != null) {
            Log.d("Spinners value", "NOT NULL");
            ArrayAdapter<String> cntryAdapter = new ArrayAdapter<>(context, R.layout.custom_drop_down, R.id.textView, chatMessages.get(position).options);
            holder.cntrySP.setAdapter(cntryAdapter);
            if (chatMessages.get(position).selectedSpinner != null) {
                Log.d("Selected spinner value", chatMessages.get(position).selectedSpinner);
                holder.cntrySP.setSelection(chatMessages.get(position).optionids.indexOf(chatMessages.get(position).selectedSpinner));
            }
            holder.cntrySP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int kk, long l) {
                    String selectedCntry = holder.cntrySP.getSelectedItem().toString();
                    int qpos = -1;
                    JSONObject obj = null;
                    String anss = null;

                    Log.d("POS", position + "");
                    if (kk != 0) {
                        for (int k = 0; k < jsonArr.length(); k++) {
                            obj = jsonArr.optJSONObject(k);
                            if (obj != null) {
                                String q = obj.optString("question");
                                anss = obj.optString("answer");
                                if (q.equals(holder.spinnerq.getText().toString())) {
                                    qpos = k;
                                    break;
                                } else {
                                    qpos = -1;
                                }

                            }
                        }

                        Log.d("QuestionPos", qpos + "");
                        if (qpos != -1 && anss.equals("")) {
                            Log.d("Addd", "Spinner answer pos" + chatMessages.get(position).optionids.get(chatMessages.get(position).options.indexOf(selectedCntry)));
                            try {
                                obj.put("answer", chatMessages.get(position).optionids.get(chatMessages.get(position).options.indexOf(selectedCntry)));
                                chatMessages.get(position).selectedSpinner = chatMessages.get(position).optionids.get(chatMessages.get(position).options.indexOf(selectedCntry));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            qpos = qpos + 1;
                            i = i + 1;
                            if (qpos < jsonArr.length()) {
                                try {
                                    ArrayList<String> options = new ArrayList<>();
                                    ArrayList<String> optionids = new ArrayList<>();
                                    String subtype = null;
                                    if (jsonArr.optJSONObject(qpos).has("options")) {
                                        JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                        for (int j = 0; j < foptionarray.length(); j++) {
                                            String option = foptionarray.getJSONObject(j).getString("option");
                                            String optionid = foptionarray.getJSONObject(j).getString("id");
                                            options.add(option);
                                            optionids.add(optionid);
                                        }
                                    }
                                    if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                        subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                                    }
                                    List<Object> res = new ArrayList<>();
                                    res = getOptions(qpos);
                                    options = (ArrayList) res.get(0);
                                    optionids = (ArrayList) res.get(1);
                                    int input = 1;
                                    if (jsonArr.optJSONObject(qpos).has("input")) {
                                        input = jsonArr.optJSONObject(qpos).optInt("input");
                                        if (input == 1) {
                                            chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                            chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                        } else {

                                            chatEditText.setInputType(input);
                                        }
                                    } else {
                                        try {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        } else if (qpos != -1 && !anss.equals("")) {
                            Log.d("Update", "spinner answer" + selectedCntry);


                            try {
                                obj.put("answer", chatMessages.get(position).optionids.get(chatMessages.get(position).options.indexOf(selectedCntry)));

                                chatMessages.get(position).selectedSpinner = chatMessages.get(position).optionids.get(chatMessages.get(position).options.indexOf(selectedCntry));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        // adapter.add(new ChatMessage(true, getResources().getString(R.string.enter_wrong), "1"));

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    public void callCheckboxQues(final ChatArrayAdapter.ViewHolder holder, final int position, final List<ChatMessage> chatMessages) {
        holder.checkboxlay.setVisibility(View.VISIBLE);
        holder.messageTextView.setVisibility(View.GONE);
        holder.radiolay.setVisibility(View.GONE);
        holder.spinnerlay.setVisibility(View.GONE);
        holder.datepickerlay.setVisibility(View.GONE);
        holder.docbrowselay.setVisibility(View.GONE);
        holder.catsubcatlay.setVisibility(View.GONE);
        holder.checkboxq.setText(chatMessages.get(position).text);
        for (int ii = 0; ii < chatMessages.get(position).options.size(); ii++) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setChecked(false);
            checkBox.setText(chatMessages.get(position).options.get(ii).toString());
            if (chatMessages.get(position).selectedOptions != null) {
                for (int k = 0; k < chatMessages.get(position).selectedOptions.size(); k++) {
                    if (chatMessages.get(position).selectedOptions.get(k).trim().equals(checkBox.getText().toString().trim())) {
                        checkBox.setChecked(true);
                    } else {
                        //checkBox.setChecked(false);
                    }
                }
            }
            holder.cities.addView(checkBox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        int qpos = -1;
                                                        JSONObject obj = null;
                                                        String anss = null;

                                                        Log.d("POS", position + "");

                                                        for (int k = 0; k < jsonArr.length(); k++) {
                                                            obj = jsonArr.optJSONObject(k);
                                                            if (obj != null) {
                                                                String q = obj.optString("question");
                                                                anss = obj.optString("answer");
                                                                if (q.equals(holder.checkboxq.getText().toString())) {
                                                                    qpos = k;
                                                                    break;
                                                                } else {
                                                                    qpos = -1;
                                                                }

                                                            }
                                                        }
                                                        Log.d("QuestionPos checkbox", qpos + "");
                                                        if (isChecked) {
                                                            chatMessages.get(position).selectedOptions.add(buttonView.getText().toString());


                                                        } else {
                                                            chatMessages.get(position).selectedOptions.remove(buttonView.getText().toString());
                                                        }
                                                        if (qpos != -1 && anss.equals("")) {
                                                            Log.d("Addd", "Checkbox answer" + buttonView.getText().toString());
                                                            try {
                                                                obj.put("answer", chatMessages.get(position).selectedOptions.toString());


                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            qpos = qpos + 1;
                                                            i = i + 1;
                                                            if (qpos < jsonArr.length()) {
                                                                try {
                                                                    ArrayList<String> options = new ArrayList<>();
                                                                    ArrayList<String> optionids = new ArrayList<>();
                                                                    String subtype = null;
                                                                    if (jsonArr.optJSONObject(qpos).has("options")) {
                                                                        JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                                                        for (int j = 0; j < foptionarray.length(); j++) {
                                                                            String option = foptionarray.getJSONObject(j).getString("option");
                                                                            String optionid = foptionarray.getJSONObject(j).getString("id");
                                                                            options.add(option);
                                                                            optionids.add(optionid);
                                                                        }
                                                                    }
                                                                    if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                                                        subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                                                                    }
                                                                    List<Object> res = new ArrayList<>();
                                                                    res = getOptions(qpos);
                                                                    options = (ArrayList) res.get(0);
                                                                    optionids = (ArrayList) res.get(1);
                                                                    int input = 1;
                                                                    if (jsonArr.optJSONObject(qpos).has("input")) {
                                                                        input = jsonArr.optJSONObject(qpos).optInt("input");
                                                                        if (input == 1) {
                                                                            chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                                            chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                                                        } else {

                                                                            chatEditText.setInputType(input);
                                                                        }
                                                                    } else {
                                                                        try {
                                                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                            imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                    adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                        } else {
                                                            Log.d("Updated", "Checkbox answer" + buttonView.getText().toString());
                                                            try {
                                                                obj.put("answer", chatMessages.get(position).selectedOptions.toString());


                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
            );
        }
    }

    public void callDatepickerQues(final ChatArrayAdapter.ViewHolder holder, final int position, final List<ChatMessage> chatMessages) {
        holder.checkboxlay.setVisibility(View.GONE);
        holder.messageTextView.setVisibility(View.GONE);
        holder.radiolay.setVisibility(View.GONE);
        holder.spinnerlay.setVisibility(View.GONE);
        holder.datepickerlay.setVisibility(View.VISIBLE);
        holder.docbrowselay.setVisibility(View.GONE);
        holder.catsubcatlay.setVisibility(View.GONE);

        holder.datepickerq.setText(chatMessages.get(position).text);
        if (chatMessages.get(position).selectedDate != null) {
            holder.datebirth.setText(chatMessages.get(position).selectedDate);
        }


        holder.datepickerlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar newCalendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String date = dateFormatter.format(newDate.getTime());
                        if (chatMessages.get(position).subtype != null) {
                            if (chatMessages.get(position).subtype.equals("NXT")) {
                                if (AppUtilityFunction.isNextDate(date, dateFormatter)) {
                                    chatMessages.get(position).selectedDate = date;
                                    holder.datebirth.setText(chatMessages.get(position).selectedDate);

                                    int qpos = -1;
                                    JSONObject obj = null;
                                    String anss = null;

                                    Log.d("POS", position + "");

                                    for (int k = 0; k < jsonArr.length(); k++) {
                                        obj = jsonArr.optJSONObject(k);
                                        if (obj != null) {
                                            String q = obj.optString("question");
                                            anss = obj.optString("answer");
                                            if (q.equals(holder.datepickerq.getText().toString())) {
                                                qpos = k;
                                                break;
                                            } else {
                                                qpos = -1;
                                            }

                                        }
                                    }
                                    Log.d("QuestionPos datepicker", qpos + "");
                                    if (qpos != -1 && anss.equals("")) {
                                        Log.d("Addd", "datepicker answer" + date);
                                        try {
                                            obj.put("answer", date);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        qpos = qpos + 1;
                                        i = i + 1;
                                        if (qpos < jsonArr.length()) {
                                            try {
                                                ArrayList<String> options = new ArrayList<>();
                                                ArrayList<String> optionids = new ArrayList<>();
                                                String subtype = null;
                                                if (jsonArr.optJSONObject(qpos).has("options")) {
                                                    JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                                    for (int j = 0; j < foptionarray.length(); j++) {
                                                        String option = foptionarray.getJSONObject(j).getString("option");
                                                        String optionid = foptionarray.getJSONObject(j).getString("id");
                                                        options.add(option);
                                                        optionids.add(optionid);
                                                    }
                                                }
                                                if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                                    subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                                                }
                                                List<Object> res = new ArrayList<>();
                                                res = getOptions(qpos);
                                                options = (ArrayList) res.get(0);
                                                optionids = (ArrayList) res.get(1);
                                                int input = 1;
                                                if (jsonArr.optJSONObject(qpos).has("input")) {
                                                    input = jsonArr.optJSONObject(qpos).optInt("input");
                                                    if (input == 1) {
                                                        chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                        chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                                    } else {

                                                        chatEditText.setInputType(input);
                                                    }
                                                } else {
                                                    try {
                                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                        imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                    } else {
                                        Log.d("Updated", "datepicker answer" + date);
                                        try {
                                            obj.put("answer", date);


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {

                                    Toast.makeText(context, getResources().getString(R.string.ftrdate),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        } else {
                            if (!AppUtilityFunction.isNextDate(date, dateFormatter)) {
                                chatMessages.get(position).selectedDate = date;
                                holder.datebirth.setText(chatMessages.get(position).selectedDate);

                                int qpos = -1;
                                JSONObject obj = null;
                                String anss = null;

                                Log.d("POS", position + "");

                                for (int k = 0; k < jsonArr.length(); k++) {
                                    obj = jsonArr.optJSONObject(k);
                                    if (obj != null) {
                                        String q = obj.optString("question");
                                        anss = obj.optString("answer");
                                        if (q.equals(holder.datepickerq.getText().toString())) {
                                            qpos = k;
                                            break;
                                        } else {
                                            qpos = -1;
                                        }

                                    }
                                }
                                Log.d("QuestionPos datepicker", qpos + "");
                                if (qpos != -1 && anss.equals("")) {
                                    Log.d("Addd", "datepicker answer" + date);
                                    try {
                                        obj.put("answer", date);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    qpos = qpos + 1;
                                    i = i + 1;
                                    if (qpos < jsonArr.length()) {
                                        try {
                                            ArrayList<String> options = new ArrayList<>();
                                            ArrayList<String> optionids = new ArrayList<>();
                                            String subtype = null;
                                            if (jsonArr.optJSONObject(qpos).has("options")) {
                                                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                                for (int j = 0; j < foptionarray.length(); j++) {
                                                    String option = foptionarray.getJSONObject(j).getString("option");
                                                    String optionid = foptionarray.getJSONObject(j).getString("id");
                                                    options.add(option);
                                                    optionids.add(optionid);
                                                }
                                            }
                                            if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                                subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                                            }
                                            List<Object> res = new ArrayList<>();
                                            res = getOptions(qpos);
                                            options = (ArrayList) res.get(0);
                                            optionids = (ArrayList) res.get(1);
                                            int input = 1;
                                            if (jsonArr.optJSONObject(qpos).has("input")) {
                                                input = jsonArr.optJSONObject(qpos).optInt("input");
                                                if (input == 1) {
                                                    chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                    chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                                } else {

                                                    chatEditText.setInputType(input);
                                                }
                                            } else {
                                                try {
                                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                } else {
                                    Log.d("Updated", "datepicker answer" + date);
                                    try {
                                        obj.put("answer", date);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(context, getResources().getString(R.string.enter_wrong),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }


                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }

        });
    }

    public void callDocuploadQues(ChatArrayAdapter.ViewHolder holder, final int position, List<ChatMessage> chatMessages) {
        holder.checkboxlay.setVisibility(View.GONE);
        holder.messageTextView.setVisibility(View.GONE);
        holder.radiolay.setVisibility(View.GONE);
        holder.spinnerlay.setVisibility(View.GONE);
        holder.datepickerlay.setVisibility(View.GONE);
        holder.catsubcatlay.setVisibility(View.GONE);
        holder.docbrowselay.setVisibility(View.VISIBLE);
        holder.docbrowseq.setText(chatMessages.get(position).text);

        if (chatMessages.get(position).browsePath != null) {
            holder.docpath.setText(chatMessages.get(position).browsePath);
        }
        holder.browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browsePdf();
                browsepos = position;
            }
        });
        holder.takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFrom();
                browsepos = position;
            }
        });
    }

    public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

        private static final int LEFT_MESSAGE = -1;
        private static final int RIGHT_MESSAGE = 1;
        private boolean activate = false;
        private DatePickerDialog dobDialog;
        private boolean show = true;

        private List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();


        public ChatArrayAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public void add(ChatMessage object) {
            chatMessages.add(object);
            super.add(object);
        }

        public void activateButtons(boolean activate) {
            this.activate = activate;

            adapter.notifyDataSetChanged(); //need to call it for the child views to be re-created with buttons.
        }


        public int getCount() {
            return this.chatMessages.size();
        }

        public ChatMessage getItem(int index) {
            return this.chatMessages.get(index);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (chatMessages.get(position).left ? LEFT_MESSAGE : RIGHT_MESSAGE);
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {
            View row = convertView;
            //   ChatMessage comment = getItem(position);

            int type = getItemViewType(position);

            final ViewHolder holder;
            if (row == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (type == LEFT_MESSAGE) {
                    row = inflater.inflate(R.layout.chat_listitem_left, parent, false);
                    holder.wrapper = (RelativeLayout) row.findViewById(R.id.wrapper);

                    holder.messageTextView = (TextView) row.findViewById(R.id.text);
                    holder.radioq = (TextView) row.findViewById(R.id.radioq);
                    holder.catradioq = (TextView) row.findViewById(R.id.catradioq);
                    //holder.editView = (ImageView) row.findViewById(R.id.edit);
                    holder.radiolay = (RelativeLayout) row.findViewById(R.id.radiolay);
                    holder.rgroup = (RadioGroup) row.findViewById(R.id.rgroup);
                    holder.radiob1 = (RadioButton) row.findViewById(R.id.radiob1);
                    holder.radiob2 = (RadioButton) row.findViewById(R.id.radiob2);

                    holder.r1 = (RadioButton) row.findViewById(R.id.catradiob1);
                    holder.r2 = (RadioButton) row.findViewById(R.id.catradiob2);
                    holder.cntrySP = (Spinner) row.findViewById(R.id.cntrySP);
                    holder.spinnerq = (TextView) row.findViewById(R.id.spinnerq);
                    holder.checkboxq = (TextView) row.findViewById(R.id.checkboxq);
                    holder.spinnerlay = (RelativeLayout) row.findViewById(R.id.spinnerlay);
                    holder.checkboxlay = (RelativeLayout) row.findViewById(R.id.checkboxlay);
                    holder.cities = (LinearLayout) row.findViewById(R.id.cities);
                    holder.datepickerlay = (RelativeLayout) row.findViewById(R.id.datepickerlay);
                    holder.docbrowselay = (RelativeLayout) row.findViewById(R.id.docbrowselay);
                    holder.datepickerq = (TextView) row.findViewById(R.id.datepickerq);
                    holder.datebirth = (TextView) row.findViewById(R.id.datebirth);
                    holder.docbrowseq = (TextView) row.findViewById(R.id.docbrowseq);
                    holder.browse = (ImageView) row.findViewById(R.id.browse);
                    holder.takepic = (ImageView) row.findViewById(R.id.takepic);
                    holder.docpath = (TextView) row.findViewById(R.id.docpath);
                    holder.catq = (TextView) row.findViewById(R.id.catq);
                    holder.catsubcatlay = (RelativeLayout) row.findViewById(R.id.catsubcatlay);
                    holder.catSP = (Spinner) row.findViewById(R.id.catSP);
                    holder.subcatSP = (Spinner) row.findViewById(R.id.subcatSP);
                    holder.rgroupcat = (RadioGroup) row.findViewById(R.id.rgroupcat);
                }
                if (type == RIGHT_MESSAGE) {
                    row = inflater.inflate(R.layout.chat_listitem_right, parent, false);

                    holder.wrapper = (RelativeLayout) row.findViewById(R.id.wrapper);
                    holder.messageTextView = (TextView) row.findViewById(R.id.text);
                    holder.editView1 = (ImageView) row.findViewById(R.id.edit);
                }
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();

            }


            holder.messageTextView.setText(chatMessages.get(position).text);
            if (type == RIGHT_MESSAGE) {
                if (activate) {
                    holder.editView1.setVisibility(View.GONE);
                } else {
                    holder.editView1.setVisibility(View.VISIBLE);


                }
                if (chatMessages.get(position).show) {
                    holder.editView1.setVisibility(View.VISIBLE);
                } else {
                    holder.editView1.setVisibility(View.GONE);

                }
                holder.messageTextView.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                if (chatMessages.get(position).input != -1) {
                    if (chatMessages.get(position).input == 1) {
                        holder.messageTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
                holder.editView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("messgae to be redit", holder.messageTextView.getText().toString());
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatBotActivity.this);
                        alertDialog.setTitle("Update");
                        final EditText input = new EditText(ChatBotActivity.this);
                        input.setText(holder.messageTextView.getText().toString());
                        final String cstring = holder.messageTextView.getText().toString().trim();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        alertDialog.setView(input);
                        alertDialog.setIcon(R.drawable.ic_bot);

                        alertDialog.setPositiveButton("CONFIRM",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        JSONObject obj = null;
                                        String reg = null;
                                        String editValue = input.getText().toString();
                                        Log.d("POS", position + "");

                                        for (int k = 0; k < jsonArr.length(); k++) {
                                            obj = jsonArr.optJSONObject(k);
                                            if (obj != null) {
                                                String ans = obj.optString("answer");
                                                reg = obj.optString("regex");
                                                if (ans.equals(cstring)) {
                                                    anspos = k;
                                                    break;
                                                } else {
                                                    anspos = -1;
                                                }
                                            }
                                        }

                                        Log.d("AnswerPos", anspos + "");
                                        //Update Listview

                                        //Update answer array


                                        if (anspos != -1) {

                                            if (!reg.equals("")) {
                                                if (editValue.matches(reg)) {
                                                    try {
                                                        obj.put("answer", editValue);
                                                        chatMessages.remove(position);
                                                        chatMessages.add(position, new ChatMessage(false, editValue, true));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    Toast.makeText(context, getResources().getString(R.string.enter_wrong),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                try {
                                                    obj.put("answer", editValue);
                                                    chatMessages.remove(position);
                                                    chatMessages.add(position, new ChatMessage(false, editValue, true));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }


                                    }
                                });

                        alertDialog.setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        alertDialog.show();
                    }

                });
            }
            if (type == LEFT_MESSAGE) {
                String qtype = chatMessages.get(position).type;

                //for rendering normal questions
                if (qtype.equals("1")) {
                    callnormalQues(holder);
                }
                // for rendering radio button selection questions
                else if (qtype.equals("2")) {
                    callRadioButtonQues(holder, position, chatMessages);
                }
                //for rendering spinner selection questions
                else if (qtype.equals("3")) {
                    callSpinnerQues(holder, position, chatMessages);

                }
                //for rendering checkbox selection questions
                else if (qtype.equals("4")) {
                    callCheckboxQues(holder, position, chatMessages);
                }
                //for rendering datepicker questions
                else if (qtype.equals("5")) {
                    callDatepickerQues(holder, position, chatMessages);
                }
                //for rendering document uploading questions
                else if (qtype.equals("6")) {
                    callDocuploadQues(holder, position, chatMessages);
                }

            }


            return row;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent result) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case 20:
                        Uri Fpath = result.getData();
                        afterImg(Fpath);

                        break;
                    case 11:
                        Log.d("Upper 23 URI", mImageCaptureUri.toString());
                        Uri uriaftrcmpression;
                        uriaftrcmpression = mImageCaptureUri;
                        try {
                            String p = AppUtilityFunction.compressImage(context, mImageCaptureUri.toString());
                            uriaftrcmpression = Uri.fromFile(new File(p));
                            Log.d("URI after cmpression", uriaftrcmpression.toString());
                        } catch (Exception e) {

                        }
                        afterImg(uriaftrcmpression);
                        break;
                }
            }
        }

        public void afterImg(Uri Fpath) {
            String filee = AppUtilityFunction.getPath(context, Fpath);
            // String theFilePath = result.getData().getPath();
            Log.d("Selected File Path", "" + filee);
            f = new File(filee);
            if (AppUtilityFunction.getSize(f) >= AppUtilityFunction.IMAGE_SIZE_LIMIT_KB) {
                Toast.makeText(context, getString(R.string.image_size_error), Toast.LENGTH_LONG).show();
                return;
            } else if (AppUtilityFunction.getFormat(filee).equalsIgnoreCase("jpeg") || AppUtilityFunction.getFormat(filee).equalsIgnoreCase("jpg") || AppUtilityFunction.getFormat(filee).equalsIgnoreCase("pdf") || AppUtilityFunction.getFormat(filee).equalsIgnoreCase("doc") || AppUtilityFunction.getFormat(filee).equalsIgnoreCase("png")) {
                chatMessages.get(browsepos).browsePath = filee;
                notifyDataSetChanged();
                int qpos = -1;
                JSONObject obj = null;
                String anss = null;

                Log.d("POS", browsepos + "");

                for (int k = 0; k < jsonArr.length(); k++) {
                    obj = jsonArr.optJSONObject(k);
                    if (obj != null) {
                        String q = obj.optString("question");
                        anss = obj.optString("answer");
                        if (q.equals(chatMessages.get(browsepos).text)) {
                            qpos = k;
                            break;
                        } else {
                            qpos = -1;
                        }

                    }
                }
                Log.d("QuestionPos Browse", qpos + "");

                if (qpos != -1 && anss.equals("")) {

                    try {
                        obj.put("answer", filee);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    qpos = qpos + 1;
                    i = i + 1;
                    if (qpos < jsonArr.length()) {
                        try {
                            ArrayList<String> options = new ArrayList<>();
                            ArrayList<String> optionids = new ArrayList<>();
                            String subtype = null;
                            if (jsonArr.optJSONObject(qpos).has("options")) {
                                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));
                                for (int j = 0; j < foptionarray.length(); j++) {
                                    String option = foptionarray.getJSONObject(j).getString("option");
                                    String optionid = foptionarray.getJSONObject(j).getString("id");
                                    options.add(option);
                                    optionids.add(optionid);
                                }
                            }
                            if (jsonArr.optJSONObject(qpos).has("subtype")) {
                                subtype = jsonArr.optJSONObject(qpos).optString("subtype");
                            }
                            List<Object> res = new ArrayList<>();
                            res = getOptions(qpos);
                            options = (ArrayList) res.get(0);
                            optionids = (ArrayList) res.get(1);
                            int input = 1;
                            if (jsonArr.optJSONObject(qpos).has("input")) {
                                input = jsonArr.optJSONObject(qpos).optInt("input");
                                if (input == 1) {
                                    chatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    chatEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                } else {

                                    chatEditText.setInputType(input);
                                }
                            } else {
                                try {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), subtype, jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options, optionids));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                } else {
                    Log.d("Updated", "Browse answer" + filee);
                    try {
                        obj.put("answer", filee);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(context, getString(R.string.valid_format), Toast.LENGTH_LONG).show();
            }
        }

        public class ViewHolder {
            public ImageView editView1, browse, takepic;
            private TextView messageTextView, radioq, catradioq, spinnerq, checkboxq, datepickerq, datebirth, docbrowseq, docpath, catq;
            private LinearLayout wrapper1;
            private RelativeLayout radiolay;
            private RadioGroup rgroup, rgroupcat;
            private RadioButton r1, r2;
            private RelativeLayout wrapper, spinnerlay, checkboxlay, datepickerlay, docbrowselay, catsubcatlay;
            private RadioButton radiob2, radiob1;
            private Spinner cntrySP, catSP, subcatSP;
            private LinearLayout cities;

        }

    }
}