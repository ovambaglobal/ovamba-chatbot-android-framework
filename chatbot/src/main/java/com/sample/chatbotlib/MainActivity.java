package com.sample.chatbotlib;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    JSONArray jsonArr;
    private ListView chatListView;
    public ChatArrayAdapter adapter;
    private EditText chatEditText;
    private ImageView send;
    //  private static String[] bot_questions = null;
    // public ArrayList<String> bot_answers = new ArrayList<>();
    private int i = 0;
    private TextView typestatus;
    private Context context;
    private RelativeLayout form;
    // public ArrayList<String> checkboxOptions = new ArrayList<>();
    private int anspos = 0;
    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private boolean isListCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        adapter = new ChatArrayAdapter(getApplicationContext());
        form = (RelativeLayout) findViewById(R.id.form);
        //bot_questions = getResources().getStringArray(R.array.bot_questions);
        typestatus = (TextView) findViewById(R.id.typestatus);
        typestatus.setVisibility(View.GONE);
        chatListView = (ListView) findViewById(R.id.chat_listView);
        chatListView.setAdapter(adapter);
        send = (ImageView) findViewById(R.id.send);
        chatEditText = (EditText) findViewById(R.id.chat_editText);
        String readFile = readFromfile("ChatBotData.txt", context);
        try {
            JSONObject jobj = new JSONObject(readFile);
            String c = jobj.getString("botdata");
            jsonArr = new JSONArray(c);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            ArrayList<String> options = new ArrayList<>();

            if (jsonArr.optJSONObject(0).has("options")) {
                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(0).optString("options"));

                for (int j = 0; j < foptionarray.length(); j++) {
                    String option = foptionarray.getJSONObject(j).getString("option");
                    options.add(option);
                }
            }
            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(0).optString("question"), jsonArr.optJSONObject(0).optString("type"), jsonArr.optJSONObject(0).optString("option1"), jsonArr.optJSONObject(0).optString("option2"), options));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    final String answer = chatEditText.getText().toString();
                    if (!answer.equals("")) {
                        adapter.add(new ChatMessage(false, answer));
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
                                    String regex = jsonArr.optJSONObject(i).optString("regex");
                                    String type = jsonArr.optJSONObject(i).optString("type");

                                    if (type.equals("1")) {
                                        if (!regex.equals("")) {
                                            if (answer.matches(regex)) {
                                                try {
                                                    jsonArr.optJSONObject(i).put("answer", answer);
                                                    i = i + 1;
                                                    if (i < jsonArr.length()) {
                                                        try {
                                                            ArrayList<String> options = new ArrayList<>();

                                                            if (jsonArr.optJSONObject(i).has("options")) {
                                                                JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(i).optString("options"));
                                                                for (int j = 0; j < foptionarray.length(); j++) {
                                                                    String option = foptionarray.getJSONObject(j).getString("option");
                                                                    options.add(option);
                                                                }
                                                            }
                                                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(i).optString("question"), jsonArr.optJSONObject(i).optString("type"), jsonArr.optJSONObject(i).optString("option1"), jsonArr.optJSONObject(i).optString("option2"), options));

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else {
                                                        //adapter.add(new ChatMessage(true, "Waow !You are registered successfully."));

                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                adapter.add(new ChatMessage(true, getResources().getString(R.string.enter_wrong), "1"));
                                            }
                                        } else {
                                            try {
                                                jsonArr.optJSONObject(i).put("answer", answer);
                                                i = i + 1;
                                                if (i < jsonArr.length()) {
                                                    try {
                                                        ArrayList<String> options = new ArrayList<>();

                                                        if (jsonArr.optJSONObject(i).has("options")) {
                                                            JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(i).optString("options"));

                                                            for (int j = 0; j < foptionarray.length(); j++) {
                                                                String option = foptionarray.getJSONObject(j).getString("option");
                                                                options.add(option);
                                                            }
                                                        }
                                                        adapter.add(new ChatMessage(true, jsonArr.optJSONObject(i).optString("question"), jsonArr.optJSONObject(i).optString("type"), jsonArr.optJSONObject(i).optString("option1"), jsonArr.optJSONObject(i).optString("option2"), options));

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                } else {
                                                    //adapter.add(new ChatMessage(true, "Waow !You are registered successfully."));

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
                final String answer = chatEditText.getText().toString();
                if (!answer.equals("")) {
                    adapter.add(new ChatMessage(false, answer));
                    chatEditText.setText("");
                    adapter.notifyDataSetChanged();
                    typestatus.setVisibility(View.VISIBLE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            typestatus.setVisibility(View.GONE);

                            if (i  < jsonArr.length()) {
                                String regex = jsonArr.optJSONObject(i).optString("regex");
                                String type = jsonArr.optJSONObject(i).optString("type");
                                if (type.equals("1")) {
                                if (!regex.equals("")) {
                                    if (answer.matches(regex)) {
                                        try {
                                            jsonArr.optJSONObject(i).put("answer", answer);
                                            i = i + 1;
                                            if (i < jsonArr.length()) {
                                                try {
                                                    ArrayList<String> options = new ArrayList<>();
                                                    if (jsonArr.optJSONObject(i).has("options")) {
                                                        JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(i).optString("options"));
                                                        for (int j = 0; j < foptionarray.length(); j++) {
                                                            String option = foptionarray.getJSONObject(j).getString("option");
                                                            options.add(option);
                                                        }
                                                    }
                                                    adapter.add(new ChatMessage(true, jsonArr.optJSONObject(i).optString("question"), jsonArr.optJSONObject(i).optString("type"), jsonArr.optJSONObject(i).optString("option1"), jsonArr.optJSONObject(i).optString("option2"), options));

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {


                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        adapter.add(new ChatMessage(true, getResources().getString(R.string.enter_wrong), "1"));
                                    }
                                } else {
                                    try {
                                        jsonArr.optJSONObject(i).put("answer", answer);
                                        i = i + 1;
                                        if (i < jsonArr.length()) {
                                            try {
                                                ArrayList<String> options = new ArrayList<>();
                                                if (jsonArr.optJSONObject(i).has("options")) {
                                                    JSONArray foptionarray = new JSONArray(jsonArr.optJSONObject(i).optString("options"));
                                                    for (int j = 0; j < foptionarray.length(); j++) {
                                                        String option = foptionarray.getJSONObject(j).getString("option");
                                                        options.add(option);
                                                    }
                                                }
                                                adapter.add(new ChatMessage(true, jsonArr.optJSONObject(i).optString("question"), jsonArr.optJSONObject(i).optString("type"), jsonArr.optJSONObject(i).optString("option1"), jsonArr.optJSONObject(i).optString("option2"), options));

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

            }
        });
    }

    private String readFromfile(String fileName, Context context) {
        StringBuffer returnString = new StringBuffer();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
                returnString.append("\n");

            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

        private static final int LEFT_MESSAGE = -1;
        private static final int RIGHT_MESSAGE = 1;
        private boolean activate = false;
        private DatePickerDialog dobDialog;

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
                    //holder.editView = (ImageView) row.findViewById(R.id.edit);
                    holder.radiolay = (RelativeLayout) row.findViewById(R.id.radiolay);
                    holder.rgroup = (RadioGroup) row.findViewById(R.id.rgroup);
                    holder.radiob1 = (RadioButton) row.findViewById(R.id.radiob1);
                    holder.radiob2 = (RadioButton) row.findViewById(R.id.radiob2);
                    holder.cntrySP = (Spinner) row.findViewById(R.id.cntrySP);
                    holder.spinnerq = (TextView) row.findViewById(R.id.spinnerq);
                    holder.checkboxq = (TextView) row.findViewById(R.id.checkboxq);
                    holder.spinnerlay = (RelativeLayout) row.findViewById(R.id.spinnerlay);
                    holder.checkboxlay = (RelativeLayout) row.findViewById(R.id.checkboxlay);
                    holder.cities = (LinearLayout) row.findViewById(R.id.cities);
                    holder.datepickerlay=(RelativeLayout)row.findViewById(R.id.datepickerlay);
                    holder.datepickerq=(TextView)row.findViewById(R.id.datepickerq);
                    holder.datebirth=(TextView)row.findViewById(R.id.datebirth);
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
                   // holder.wrapper.setEnabled(false);
                  /*  //holder.rgroup.setClickable(false);
                    holder.cntrySP.setClickable(false);
                    holder.datebirth.setClickable(false);
                    holder.checkboxlay.setClickable(false);*/
                } else {
                    holder.editView1.setVisibility(View.VISIBLE);


                }

                holder.editView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.d("messgae to be redit", holder.messageTextView.getText().toString());
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("Update");
                        //alertDialog.setMessage("Enter Password");

                        final EditText input = new EditText(MainActivity.this);
                        input.setText(holder.messageTextView.getText().toString());
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
                                        String editValue = input.getText().toString();
                                        Log.d("POS", position + "");

                                        for (int k = 0; k < jsonArr.length(); k++) {
                                            obj = jsonArr.optJSONObject(k);
                                            if (obj != null) {
                                                String ans = obj.optString("answer");
                                                if (ans.equals(holder.messageTextView.getText().toString())) {
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
                                            String reg = obj.optString("regex");
                                            if (!reg.equals("")) {
                                                if (editValue.matches(reg)) {
                                                    try {
                                                        obj.put("answer", editValue);
                                                        chatMessages.remove(position);
                                                        chatMessages.add(position, new ChatMessage(false, editValue));
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
                                                    chatMessages.add(position, new ChatMessage(false, editValue));
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
                //for normal questions
                if (qtype.equals("1")) {
                    holder.messageTextView.setVisibility(View.VISIBLE);
                    holder.radiolay.setVisibility(View.GONE);
                    holder.spinnerlay.setVisibility(View.GONE);
                    holder.checkboxlay.setVisibility(View.GONE);
                    holder.datepickerlay.setVisibility(View.GONE);
                }
                // for radio button selection questions
                else if (qtype.equals("2")) {
                    holder.messageTextView.setVisibility(View.GONE);
                    holder.radiolay.setVisibility(View.VISIBLE);
                    holder.radioq.setText(chatMessages.get(position).text);
                    holder.radiob1.setText(chatMessages.get(position).option1);
                    holder.radiob2.setText(chatMessages.get(position).option2);
                    holder.spinnerlay.setVisibility(View.GONE);
                    holder.checkboxlay.setVisibility(View.GONE);
                    holder.datepickerlay.setVisibility(View.GONE);
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
                                    }

                                } else {
                                    if (radioSexButton.getText().toString().equalsIgnoreCase(obj.optString("option1"))) {
                                        adapter.add(new ChatMessage(true, getResources().getString(R.string.congrts), "1"));
                                        adapter.activateButtons(true);

                                        isListCompleted = true;

                                        form.setVisibility(View.GONE);
                                        try {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        qpos = qpos - 1;
                                        ArrayList<String> options = new ArrayList<>();
                                        if (jsonArr.optJSONObject(qpos).has("options")) {
                                            JSONArray foptionarray = null;
                                            try {
                                                foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));

                                                for (int j = 0; j < foptionarray.length(); j++) {
                                                    String option = foptionarray.getJSONObject(j).getString("option");
                                                    options.add(option);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options));

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
                                if (!isListCompleted)  {
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
                                            adapter.add(new ChatMessage(true, getResources().getString(R.string.congrts), "1"));
                                            adapter.activateButtons(true);
                                            form.setVisibility(View.GONE);

                                            isListCompleted = true;


                                            try {
                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(form.getWindowToken(), 0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                             qpos=qpos-1;
                                            ArrayList<String> options = new ArrayList<>();
                                            if (jsonArr.optJSONObject(qpos).has("options")) {
                                                JSONArray foptionarray = null;
                                                try {
                                                    foptionarray = new JSONArray(jsonArr.optJSONObject(qpos).optString("options"));

                                                    for (int j = 0; j < foptionarray.length(); j++) {
                                                        String option = foptionarray.getJSONObject(j).getString("option");
                                                        options.add(option);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            adapter.add(new ChatMessage(true, jsonArr.optJSONObject(qpos).optString("question"), jsonArr.optJSONObject(qpos).optString("type"), jsonArr.optJSONObject(qpos).optString("option1"), jsonArr.optJSONObject(qpos).optString("option2"), options));

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
                            }
                            }
                        }
                    });
                }
                //for spinner selection
                else if (qtype.equals("3")) {
                    holder.messageTextView.setVisibility(View.GONE);
                    holder.radiolay.setVisibility(View.GONE);
                    holder.spinnerlay.setVisibility(View.VISIBLE);
                    holder.checkboxlay.setVisibility(View.GONE);
                    holder.datepickerlay.setVisibility(View.GONE);
                    holder.spinnerq.setText(chatMessages.get(position).text);
                    holder.cntrySP.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

                    ArrayAdapter<String> cntryAdapter = new ArrayAdapter<>(context, R.layout.custom_drop_down, R.id.textView, chatMessages.get(position).options);
                    holder.cntrySP.setAdapter(cntryAdapter);
                    if (chatMessages.get(position).selectedCntry != null) {
                        Log.d("Selected spinner value",chatMessages.get(position).selectedCntry);
                        holder.cntrySP.setSelection(chatMessages.get(position).options.indexOf(chatMessages.get(position).selectedCntry));
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
                                    Log.d("Addd", "Spinner answer" + selectedCntry);
                                    try {
                                        obj.put("answer", selectedCntry);
                                        chatMessages.get(position).selectedCntry = selectedCntry;

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    qpos = qpos + 1;
                                    i = i + 1;
                                    if (qpos < jsonArr.length()) {
                                        try {
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
                                        }

                                    }

                                } else if (qpos != -1 && !anss.equals("")) {
                                    Log.d("Update", "spinner answer" + selectedCntry);


                                        try {
                                            obj.put("answer", selectedCntry);

                                            chatMessages.get(position).selectedCntry = selectedCntry;

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else if (qtype.equals("4")) {
                    holder.checkboxlay.setVisibility(View.VISIBLE);
                    holder.messageTextView.setVisibility(View.GONE);
                    holder.radiolay.setVisibility(View.GONE);
                    holder.spinnerlay.setVisibility(View.GONE);
                    holder.datepickerlay.setVisibility(View.GONE);
                    holder.checkboxq.setText(chatMessages.get(position).text);
                    for (int ii = 0; ii < chatMessages.get(position).options.size(); ii++) {
                        CheckBox checkBox = new CheckBox(context);

                        checkBox.setText(chatMessages.get(position).options.get(ii).toString());
                        if (chatMessages.get(position).selectedOptions != null) {
                            for (int k = 0; k < chatMessages.get(position).selectedOptions.size(); k++) {
                                if (chatMessages.get(position).selectedOptions.get(k).equals(checkBox.getText().toString())) {
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
                else if (qtype.equals("5")) {
                    holder.checkboxlay.setVisibility(View.GONE);
                    holder.messageTextView.setVisibility(View.GONE);
                    holder.radiolay.setVisibility(View.GONE);
                    holder.spinnerlay.setVisibility(View.GONE);
                    holder.datepickerlay.setVisibility(View.VISIBLE);
                    holder.datepickerq.setText(chatMessages.get(position).text);
                    if(chatMessages.get(position).selectedDate!=null)
                    {
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

                                    if(!AppUtilityFunction.isNextDate(date, dateFormatter)) {
                                        chatMessages.get(position).selectedDate=date;
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
                                                }

                                            }

                                        } else {
                                            Log.d("Updated", "datepicker answer" +date);
                                            try {
                                                obj.put("answer", date);


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(context, getResources().getString(R.string.enter_wrong),
                                                Toast.LENGTH_SHORT).show();
                                    }


                                }

                            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                                    newCalendar.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.show();
                        }
                    });
                }
            }


            return row;
        }

        public Bitmap decodeToBitmap(byte[] decodedByte) {
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }

        public class ViewHolder {
            private TextView messageTextView, radioq, spinnerq, checkboxq,datepickerq,datebirth;
            private ImageView editView1;
            private LinearLayout wrapper1;
            private RelativeLayout radiolay;
            private RadioGroup rgroup;
            private RelativeLayout wrapper, spinnerlay, checkboxlay,datepickerlay;
            private RadioButton radiob2, radiob1;
            private Spinner cntrySP;
            private LinearLayout cities;

        }

    }

}
