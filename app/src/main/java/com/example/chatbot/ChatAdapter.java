package com.example.chatbot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.ResponseListener;
import io.github.ponnamkarthik.richlinkpreview.RichPreview;

// 2022-01-06 황우진
// 채팅창 구현을 위한 ChatAdapter 제작
// 대답 형식에 따라 각각의 UI 뷰홀더를 갖추고 있음.
public class ChatAdapter extends RecyclerView.Adapter{

    // RecyclerView 가 출력되는 Context 객체를 저장
    private static Context context;

    // RecyclerView 에 출력될 리스트 객체 변수 선언.
    ArrayList<Chat> chatArrayList;


    // 생성자에서 데이터 리스트 객체를 전달받음.
    public ChatAdapter(ArrayList<Chat> chatArrayList, Context context) {
        this.chatArrayList = chatArrayList;
        this.context = context; // RecyclerView 가 출력되는 context 를 생성자를 통해 전달받는다.
    }

    // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 해당하는 뷰홀더 객체를 리턴한다.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        switch (i){ // switch 문을 통하여 홀더 타입에 따라서 생성하는 뷰홀더 객체도 다르게 한다.
            case 0:
                // LayoutInflater 는 XML 에 정의된 Resource 를 View 객체로 반환해주는 역할을 한다
                // LayoutInflater.from을 통해 얻어온 LayoutInflater 의 inflate 함수를 통하여 xml에 표기된 레이아웃들을 객체화 시킨다.
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, viewGroup, false);
                return new userViewHolder(view);    // 해당하는 ViewHolder 를 반환한다.
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bottext, viewGroup, false);
                return new bottextViewHolder(view);
            case 2:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botstart, viewGroup, false);
                return new botStartViewHolder(view);
            case 3:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botbutton, viewGroup, false);
                return new botButtonViewHolder(view);
            case 4:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botweb, viewGroup, false);
                return new botWebViewHolder(view);
            case 5:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botmap, viewGroup, false);
                return new botMapViewHolder(view);
            case 6:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_botimage, viewGroup, false);
                return new botImageViewHolder(view);
        }
        return null;
    }



    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시한다.
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        Chat chat = chatArrayList.get(i);   // chatArrayList 데이터 타입에서 바인딩 하고자 하는 데이터를 position 데이터(i)를 통하여 얻어온다.
        final String message = chatArrayList.get(i).getMessasge();  // 데이터 중에서 message 데이터를 파싱한다.

        switch (chatArrayList.get(i).getWho()) {    // 무슨 타입 이냐 에 따라서 뷰홀더 타입의 UI 객체 표기도 다르게 한다.
            case "user": {  // 만약 user 타입이면 해당하는 ui 타입으로 출력한다.
                ((userViewHolder)viewHolder).userMsg.setText(message);
                break;
            }

            case "bottext":{ // 만약 bot에서 text만을 출력하는 타입이면 해당하는 ui 타입으로 출력한다.
                ((bottextViewHolder)viewHolder).bottextMsg.setText(message);

                break;
            }

            case "botstart": { // 만약 bot에서 처음 시작하는 말풍선 ui 를 출력하는 타입이면 해당하는 ui 타입으로 출력한다.

                // 첫번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA1.getText());
                    }
                });

                // 두번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA2.getText());
                    }
                });

                // 세번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA3.getText());
                    }
                });

                // 네번째 질문 edittext에..
                ((botStartViewHolder)viewHolder).btnQnA4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText et = ((Activity)context).findViewById(R.id.editMessage);
                        et.setText(((botStartViewHolder)viewHolder).btnQnA4.getText());
                    }
                });
                break;
            }

            case "botbutton": { // 만약 버튼 형식이면 해당하는 ui를 출력한다.
                ((botButtonViewHolder)viewHolder).btnBotbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 전화 걸기
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + message));
                        context.startActivity(mIntent);
                    }
                });
                break;
            }

            case "botweb": { // 만약 web 미리보기를 제공을 해야한다면 해당하는 UI를 출력한다.

                // 웹 미리보기를 제공하는 라이브러리를 사용한다.
                // URL을 읽어서 URL의 metadata를 얻어 사이트명, url, 설명, 이미지를 얻는다.
                // 아래의 코드는 userHolder에 차례대로 적용시키는 중이다.
                RichPreview richPreview = new RichPreview(new ResponseListener() {
                    @Override
                    public void onData(MetaData metaData) { // URL으로부터 메타데이터를 출력한다. 메타데이터에는 웹 미리보기를 하는데에 필요한 데이터가 있다. 거의 모든 웹사이트가 이 메타데이터를 가지고 있다.

                        if (metaData != null) {
                            if(metaData.getTitle() != null){     // 만약에 메타데이터에 타이틀 데이터가 null값이 아니라면 아래의 구문대로 UI를 수정하여 제공한다.
                                ((botWebViewHolder)viewHolder).preview_title.setText(metaData.getTitle());
                            }
                            if(metaData.getUrl() != null){       // 만약에 메타데이터에 url 데이터가 null값이 아니라면 아래의 구문대로 UI를 수정하여 제공한다.
                                ((botWebViewHolder)viewHolder).preview_link.setText(metaData.getUrl());
                            }
                            if(metaData.getDescription() != null){       // 만약에 메타데이터에 세부설명 데이터가 null값이 아니라면 아래의 구문대로 UI를 수정하여 제공한다.
                                ((botWebViewHolder)viewHolder).preview_description.setText(metaData.getDescription());
                            }
                            if(metaData.getImageurl() != null){      // 만약에 메타데이터에 이미지url 데이터가 null값이 아니라면 아래의 구문대로 UI를 수정하여 제공한다.
                                Glide.with(context).load(metaData.getImageurl()).into(((botWebViewHolder)viewHolder).img_preview);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(e.getMessage(), e.getMessage());

                    }
                });

                // 사전에 미리 분석하여 얻은 url을 리치프리뷰에게 넘겨 메타데이터를 얻은 뒤 앞에서 설정한 UI대로 자동으로 만들어준다.
                richPreview.getPreview(message);

                ((botWebViewHolder)viewHolder).layout_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));     // message 로 넘어온 url 데이터를 통하여 해당하는 웹사이트로 Intent를 통하여 넘긴다.
                        context.startActivity(intent);
                    }
                });
                break;
            }

            case "botmap": {     // 만약 map 미리보기를 제공을 해야한다면 해당하는 UI를 출력한다.

                final String mapLink = "https://map.kakao.com/link/to/" + message;  // 맵 링크를 출력한다. 이 맵 링크는 사전에 미리 정해진 형식에 맞춘 가공된 데이터를 사용한다. ( 데이터 형식 : 주소,위도,경도 )

                // RichPreview 객체를 이용하여 메타데이터 중에서 이미지 url 정보만을 가져와 이미지만을 출력한다.
                RichPreview richPreview = new RichPreview(new ResponseListener() {
                    @Override
                    public void onData(MetaData metaData) {
                        if (metaData != null) {
                            if(metaData.getImageurl() != null){
                                Glide.with(context).load(metaData.getImageurl()).into(((botMapViewHolder)viewHolder).botMapImage);
                            }
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.d(e.getMessage(), e.getMessage());
                    }
                });

                // chat 에 저장된 데이터 중 이미지 url 데이터를 불러온다. 이 이미지 url 객체 데이터는 생성자를 통하여 저장할 수 없다. set 메서드를 통하여 따로 저장해야 한다.
                richPreview.getPreview(chat.getImageUrl());

                // 클릭 할 시, 사전에 생성해둔 mapLink 를 이용하여 웹을 통하여 앱을 실행시켜준다.
                ((botMapViewHolder)viewHolder).botMapImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapLink));
                        context.startActivity(intent);
                    }
                });

                // 클릭 할 시, 사전에 생성해둔 mapLink 를 이용하여 웹을 통하여 앱을 실행시켜준다.
                ((botMapViewHolder)viewHolder).btnMoveMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapLink));
                        context.startActivity(intent);
                    }
                });
                break;
            }

            case "botimage": {   // 만약 이미지를 제공을 해야한다면 해당하는 UI를 출력한다.
                Glide.with(context).load(message).into(((botImageViewHolder)viewHolder).botImage); // Glide를 통하여 이미지 url 을 통해 이미지를 출력시킨다.

                // 이미지 누를시 이미지를 확대할 수 있는 액티비티로 이동한다.
                ((botImageViewHolder) viewHolder).botImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Intent 전환시 비트맵으로 데이터를 전달시킨다.
                        Intent intent = new Intent(context, ImageActivity.class);
                        // 비트맵을 바이트 배열로 전환시켜 데이터를 전달한다.
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bitmap = ((BitmapDrawable) ((botImageViewHolder) viewHolder).botImage.getDrawable()).getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        intent.putExtra("image", byteArray);
                        context.startActivity(intent);

                        // Intent intent = new Intent(context, ImageActivity.class);
                        //intent.putExtra("image", message);
                        // context.startActivity(intent);
                    }
                });



                break;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // position에 해당하는 아이템 항목에 따른 뷰타입을 리턴한다. Chat 이라는 데이터 클래스를 따로 정의한다.
    @Override
    public int getItemViewType(int position) {
        switch (chatArrayList.get(position).getWho()){
            case "user": // user 뷰홀더
                return 0;
            case "bottext": // bot text 뷰홀더
                return 1;
            case "botstart": // bot 시작 뷰홀더
                return 2;
            case "botbutton": // bot Button 대답 뷰홀더
                return 3;
            case "botweb": // bot web 미리보기 대답 뷰홀더
                return 4;
            case "botmap": // bot map 미리보기 및 앱 바로가기 대답 뷰홀더
                return 5;
            case "botimage": // bot 이미지 보기 및 이미지 확대 액티비티 이동 뷰홀더
                return 6;
            default:
                return -1;
        }
    }


    // 데이터 개수 반환
    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }



    // ---------------------------------------아이템 뷰를 저장하는 뷰홀더 클래스 정의-----------------------------------------------


    public static class userViewHolder extends RecyclerView.ViewHolder {
        TextView userMsg;

        public userViewHolder(@NonNull View itemView) {
            super(itemView);
            userMsg = (TextView)itemView.findViewById(R.id.userTextMsg);
        }
    }

    public static class bottextViewHolder extends RecyclerView.ViewHolder {
        TextView bottextMsg;

        public bottextViewHolder(@NonNull View itemView) {
            super(itemView);
            bottextMsg =  (TextView)itemView.findViewById(R.id.botTextMsg);
        }
    }

    public static class botStartViewHolder extends RecyclerView.ViewHolder {
        TextView botStartMsg;
        Button btnQnA1, btnQnA2, btnQnA3, btnQnA4;

        public botStartViewHolder(@NonNull View itemView) {
            super(itemView);
            botStartMsg = (TextView)itemView.findViewById(R.id.botStartMsg);
            btnQnA1 =  (Button)itemView.findViewById(R.id.btnQnA1);
            btnQnA2 =  (Button)itemView.findViewById(R.id.btnQnA2);
            btnQnA3 =  (Button)itemView.findViewById(R.id.btnQnA3);
            btnQnA4 =  (Button)itemView.findViewById(R.id.btnQnA4);
        }
    }

    public static class botButtonViewHolder extends  RecyclerView.ViewHolder {
        Button btnBotbutton;

        public botButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            btnBotbutton = (Button) itemView.findViewById(R.id.btnBotbutton);
        }
    }

    public static class botWebViewHolder extends  RecyclerView.ViewHolder {
        LinearLayout layout_preview;
        ImageView img_preview;
        TextView preview_link,preview_title,preview_description;

        public botWebViewHolder(@NonNull View itemView) {
            super(itemView);
            img_preview = (ImageView) itemView.findViewById(R.id.img_preview);
            preview_link = (TextView) itemView.findViewById(R.id.preview_link);
            preview_title = (TextView)itemView.findViewById(R.id.preview_title);
            preview_description = (TextView)itemView.findViewById(R.id.preview_description);
            layout_preview = (LinearLayout)itemView.findViewById(R.id.layout_preview);
        }
    }

    public static class botMapViewHolder extends  RecyclerView.ViewHolder {
        ImageView botMapImage;
        Button btnMoveMap;

        public botMapViewHolder(@NonNull View itemView) {
            super(itemView);
            botMapImage = (ImageView)itemView.findViewById(R.id.botMapImage);
            btnMoveMap = (Button)itemView.findViewById(R.id.btnMoveMap);
        }
    }

    public static class botImageViewHolder extends  RecyclerView.ViewHolder {
        ImageView botImage;


        public botImageViewHolder(@NonNull View itemView) {
            super(itemView);
            botImage = (ImageView)itemView.findViewById(R.id.botImage);
        }
    }



}
