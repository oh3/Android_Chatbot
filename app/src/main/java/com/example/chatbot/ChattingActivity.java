package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

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

// 2022-01-12 황우진
// 채팅 화면에 대한 자바소스코드. + 서버 연결 코드.
public class ChattingActivity extends AppCompatActivity {
    // 서버 URL
    // 192.168.0.14
    // 192.168.0.7
    // 112.150.84.136
    private String URL = "http://" + "192.168.0.7" + ":" + 5000 + "/query/TEST";


    // 리사이클러뷰에 필요한 변수들
    private ArrayList<Chat> chatArrayList;
    private ChatAdapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView chatView;

    // 뷰홀더 타입별로 생성
    private final String USER_KEY = "user";
    private final String BOTTEXT_KEY = "bottext";
    private final String BOTSTART_KEY = "botstart";
    private final String BOTBUTTON_KEY = "botbutton";
    private final String BOTWEB_KEY = "botweb";
    private final String BOTMAP_KEY = "botmap";
    private final String BOTIMAGE_KEY = "botimage";

    private TextToSpeech mTTS;

    EditText editMessage;
    ImageButton btnsend;
    FloatingActionButton info, speak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        editMessage =(EditText)findViewById(R.id.editMessage);
        btnsend = (ImageButton) findViewById(R.id.btnSend);
        info = (FloatingActionButton)findViewById(R.id.info);
        speak = (FloatingActionButton)findViewById(R.id.speak);
        chatView = (RecyclerView)findViewById(R.id.chatView);

        // TTS 객체를 생성한다. 리스너도 등록시킨다.
        mTTS = new TextToSpeech(ChattingActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.KOREAN); // 언어는 한국어로..
                    if (result == TextToSpeech.LANG_MISSING_DATA    // 만약 바로 위의 구문이 실패했을 경우
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                }
                else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        // 리사이클러뷰 만드는 함수 (길어서 따로 분리..)
        CreateRecyclerview();

        // 처음 앱 실행하자마자 보이는 말풍선.
        chatArrayList.add(new Chat(BOTSTART_KEY, null));

        // 마이크 버튼 눌렀을때
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // 사전에 먼저 TedPermission 라이브러리를 통하여 퍼미션 체크를 먼저 해준다.
                if(!TedPermission.isGranted(ChattingActivity.this, Manifest.permission.RECORD_AUDIO)) {

                    PermissionListener permissionlistener = new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            Snackbar.make(view, "음성인식", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            ChattingActivity.VoiceTask voiceTask = new ChattingActivity.VoiceTask();
                            voiceTask.execute();
                        }

                        @Override
                        public void onPermissionDenied(List<String> deniedPermissions) {

                        }
                    };
                    TedPermission.with(ChattingActivity.this)
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("접근 거부하셨습니다.\n[설정] - [권한]에서 권한을 허용해주세요.")
                            .setPermissions(Manifest.permission.RECORD_AUDIO)
                            .check();
                }
                else {  // 음성인식을 해준다.
                    Snackbar.make(view, "음성인식", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                    ChattingActivity.VoiceTask voiceTask = new ChattingActivity.VoiceTask();
                    voiceTask.execute();
                }
            }
        });


        // 보내기 버튼 눌렀을때
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editMessage 가 null 값이면 리턴한다.
                if(editMessage.getText().toString().equals("") || editMessage.getText().toString() == null) {
                    return;
                }

                // 유저가 보낸 메세지를 List 에 넣는다.
                String message = editMessage.getText().toString();
                Chat chat = new Chat(USER_KEY, message);
                chatArrayList.add(chat);
                adapter.notifyItemInserted(chatArrayList.size()-1); // 해당하는 위치에 아이템 추가 업데이트.

                // 서버에 전송하여 봇의 대답을 받는다.
                // postRequest(ChattingActivity.this, editMessage.getText().toString());

                // 테스트코드 --> 나중에 지우기
                chatArrayList.add(new Chat(BOTTEXT_KEY, message));
                adapter.notifyItemInserted(chatArrayList.size()-1);
                if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                    mTTS.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                }


                chatView.scrollToPosition(chatArrayList.size()-1); // 스크롤 위치를 해당하는 위치로 이동시킨다.

                // 입력창 초기화
                editMessage.setText("");
            }
        });

        // 도움말 버튼 눌렀을때
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ChattingActivity.this);
                ad.setTitle("우송이에게 물어보세요!");
                ad.setMessage("'우송대 위치'\n" +
                        "'우송대 전화번호'\n" +
                        "학사일정\n" +
                        "학과별 전화번호\n" +
                        "학과별 홈페이지 주소\n" +
                        "도서관 위치안내\n" +
                        "도서관 예약안내\n" +
                        "우송대 IT융합학부 안내\n" +
                        "학교 설명\n" +
                        "학과별 위치안내\n" +
                        "기숙사 위치\n" +
                        "교내식당 위치안내\n" +
                        "\n" +
                        "우송대학교에 대해 궁금한것을 물어보세요!");
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });
    }

    // Activity 가 종료 될때 mTTS 객체가 해당하는 작업을 수행하고 있을 경우, 멈추게 한다.
    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    // 음성인식 하는 클래스.. 아마 AsyncTask로 음성인식을 돌려주는 기능을 한다.
    public class VoiceTask extends AsyncTask<String, Integer, String> {
        String str = null;
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                getVoice();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return str;
        }
        @Override
        protected void onPostExecute(String result) {
            try {

            } catch (Exception e) {
                Log.d("onActivityResult", "getImageURL exception");
            }
        }
    }

    // 구글 음성인식 창 보여주는 함수.. 새로운 Intent 를 생성하여 보여주게 된다.
    private void getVoice() {

        Intent intent = new Intent();
        intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        String language = "ko-KR";

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        startActivityForResult(intent, 2);

    }

    // 음성인식한 것의 결과를 받는 함수..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            ArrayList<String> results = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String str = results.get(0);

            TextView tv = findViewById(R.id.editMessage);
            tv.setText(str);
        }
    }

    // 리사이클러뷰 초기화하는 함수. 처음 액티비티 실행때만 실행되는 것이다.
    private void CreateRecyclerview(){
        chatArrayList = new ArrayList<>();
        // Adapter 생성
        adapter = new ChatAdapter(chatArrayList, ChattingActivity.this);
        // LayoutManager 생성 --> 아이템 뷰가 나열되는 형태를 관리하기 위한 요소이다. 여러가지가 있지만 그중에서 LinearLayoutManager를 사용함.
        manager = new LinearLayoutManager(ChattingActivity.this);
        // Recycler 에 LayoutManager 와 Adapter 등록한다.
        chatView.setLayoutManager(manager);
        chatView.setAdapter(adapter);
        // RecyclerView가 최대로 기억할 캐쉬 사이즈를 설정한다.
        chatView.setItemViewCacheSize(100);
    }

    // 함수 내부적으로 사용하는 함수.
    // 통신하는데에 필요한 RequestBody를 만드는데에 필요한 함수이다.
    private RequestBody buildRequestBody(String msg) {
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("query", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonInput.toString()
        );

        return reqBody;
    }

    // 서버에 연결하는함수. 첫번째 인자로 Activity, 두번째 인자로 사용자 질문을 넣는다.
    private void postRequest(final Context context, String message) {
        RequestBody requestBody = buildRequestBody(message);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request
                .Builder()
                .post(requestBody)
                .url(URL)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            // 에러날 경우..
            @Override
            public void onFailure(final Call call, final IOException e) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        call.cancel();
                        // 만약 ERROR 일어날 경우..
                        // 나중에 UI에 나타나지 않도록 따로 수정

                        // 2022-01-17 황우진 --> 오류를 출력하는 코드.
                        /*chatArrayList.add(new Chat(BOTTEXT_KEY, e.toString()));
                        adapter.notifyItemInserted(chatArrayList.size()-1);
                        if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                            mTTS.speak(e.toString(), TextToSpeech.QUEUE_FLUSH, null);
                        }*/

                        // 2022-01-17 황우진 --> 서비스 종료 텍스트 출력 코드
                        chatArrayList.add(new Chat(BOTTEXT_KEY, "서비스가 원할하지 않습니다."));
                        adapter.notifyItemInserted(chatArrayList.size()-1);
                        if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                            mTTS.speak("서비스가 원할하지 않습니다.", TextToSpeech.QUEUE_FLUSH, null);
                        }

                    }
                });
            }

            // 응답을 정상적으로 가져올 경우..
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // JSON 데이터를 받아온다.
                            String message = response.body().string();
                            JSONObject jObject = new JSONObject(message);

                            String intent = null;
                            String Message = null;
                            String image = null;


                            // JSON 데이터 파싱
                            if(!jObject.isNull("Answer")) {
                                Message = jObject.getString("Answer");
                            }
                            if(!jObject.isNull("Intent")) {
                                intent = jObject.getString("Intent");
                            }
                            if(!jObject.isNull("AnswerImage")) {
                                image = jObject.getString("Image");
                            }


                            // 길찾기에 관한 내용일때만 빼고 item_bottext 레이아웃으로 Message 출력. 또한 답변이 null값이 아닐 경우에만 text UI 형태로 출력한다.
                            if(!Message.contains("http://kko.to/") || !jObject.isNull("Answer")) {
                                chatArrayList.add(new Chat(BOTTEXT_KEY, Message));
                                adapter.notifyItemInserted(chatArrayList.size()-1);
                                if(PreferenceManager.getBoolean(ChattingActivity.this, "IsTTS")){
                                    mTTS.speak(Message, TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }

                            // 이후에 AddExtraItem()를 실행하여 추가적인 것을 실행하도록 한다.
                            AddExtraItem(Message, intent, image);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    };

    // 답변을 분석하여 연락처 / URL / MAP 구분한다.
    private void AddExtraItem(String botAnswer, String intent, String image) {
        // 만약 String 안에 URL 이 있을때
        if(DistinguishAnswer.containsLink(botAnswer)) {
            List<String> urls =  DistinguishAnswer.extractUrls(botAnswer);
            String url = TextUtils.join("", urls);

            // 만약 botAnswer 가 길찾기이면.. intent 추가..
            if (botAnswer.contains("http://kko.to/")) {
                String[] answer = botAnswer.split(",");
                String text = answer[0] + "," + answer[1] + "," + answer[2];

                Chat chat = new Chat(BOTMAP_KEY, text);
                chat.setImageUrl(answer[3]);
                chatArrayList.add(chat);
            }
            // 길찾기가 아니면 Web 미리보기 창으로
            else {
                chatArrayList.add(new Chat(BOTWEB_KEY, url));
            }
            adapter.notifyItemInserted(chatArrayList.size()-1);
        }
        // 만약 이미지 출력을 요구하는 경우
        else if(image != null) {
            chatArrayList.add(new Chat(BOTIMAGE_KEY, image));
            adapter.notifyItemInserted(chatArrayList.size()-1);
        }
        // 만약 String 안에 전화번호가 있을때 --> 앞의 세글자가 042 이고 총 전화번호 개수가 9~12 사이여야 됨.(9, 12 포함) ==> intent 추가
        else if(DistinguishAnswer.getPhoneNumber(botAnswer) != null) {
            chatArrayList.add(new Chat(BOTBUTTON_KEY,  DistinguishAnswer.getPhoneNumber(botAnswer)));
            adapter.notifyItemInserted(chatArrayList.size()-1);
        }

        // 스크롤의 위치를 해당하는 위치로 이동시킨다.
        chatView.scrollToPosition(chatArrayList.size()-1);
    }
}