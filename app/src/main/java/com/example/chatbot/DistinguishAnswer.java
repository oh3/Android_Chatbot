package com.example.chatbot;


import static android.telephony.PhoneNumberUtils.is12Key;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// 2022-01-07 황우진
// 챗봇으로부터 받은 답변을 일단 item_bottext ui으로 출력한뒤, 챗봇으로부터 받은 답변을 분석하여
// 답변에 따라 연락처 앱 연결, 카카오맵 연결, 웹 미리보기 등을 추가적으로 출력한다.
// 이 클래스는 챗봇으로부터 받은 답변을 분석하기 위해 존재하는 클래스이다.
public class DistinguishAnswer {

    // 이 함수로 String에 URL이 포함되어 있는지 확인
    public static boolean containsLink(String input) {
        boolean result = false;

        String[] parts = input.split("\\s+");
        Pattern pattern = Patterns.WEB_URL;

        for (String item : parts) {
            if (android.util.Patterns.WEB_URL.matcher(item).matches()) {    // matcher 는 해당하는 string 이 WEB_URL 패턴에 맞는지 확인한다. matches 는 그 여부를 가져온다.
                result = true;
                break;
            }
        }

        if(input.contains("http://kko.to/")){
            result = true;
        }
        return result;
    }

    // String 에 있는 URL 추출
    public static List<String> extractUrls(String input) {
        List<String> result = new ArrayList<String>();

        String[] words = input.split("\\s+");

        Pattern pattern = Patterns.WEB_URL;
        for(String word : words)
        {
            // 만약에 word에서 WEB_URL 패턴이 발견이 되었다면
            if(pattern.matcher(word).find())    // matcher 는 해당하는 string 이 WEB_URL 패턴에 맞는지 확인한다. find 는 패턴이 맞는 장소로 이동시킨다.
            {
                if(!word.toLowerCase().contains("http://") && !word.toLowerCase().contains("https://")) {   // http:// 또는 https:// 가 발견되었을 경우 아래의 구문을 작동시킨다.
                    word = "http://" + word;
                }
                result.add(word);
            }
        }

        return result;
    }


    // String 에서 전화번호 추출
    public static String getPhoneNumber(String str){
        String phone = "";
        for(char c : str.toCharArray()) {
            if(is12Key(c)) {    // 자체적으로 제공하는 폰번호유틸 클래스의 함수를 통하여 해당하는 char 데이터가 숫자인지 판별한다.
                phone += c;
            }
        }

        if(isValidPhoneNumber(phone)){
            return phone;
        } else{
            return null;
        }
    }

    // 전화번호\전화번호 --> 서버대답.split("\");

    // 2022-01-10 황우진
    // 전화번호인지 확인 --> 9미만, 12 초과 시 전화번호라고 인식 안함 // 처음 3자리가 042가 아닐시 전화번호라고 인식안함..
    // 일단 이 코드로 대체.
    public final static boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || target.length() < 9 || target.length() > 12) {
            return false;
        } else {
            if(target.charAt(0)=='0' && target.charAt(1)=='4' && target.charAt(2)=='2'){
                return true;
            } else {
                return false;
            }
        }
    }

}
