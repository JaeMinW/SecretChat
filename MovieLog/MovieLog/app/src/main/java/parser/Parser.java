package parser;

import android.util.Log;

import com.kty.pj_movie.BoardMovieSearchActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vo.MovieVO;

public class Parser {

    MovieVO vo;
    String myQuery = "";//검색어 저장
    ParserItem parserItem = new ParserItem();

    public ArrayList<MovieVO> connectNaver(){
        ArrayList<MovieVO> list = new ArrayList<>();

        try{
            //search : EditText
            myQuery = URLEncoder.encode(BoardMovieSearchActivity.search.getText().toString(),"UTF-8");
            String urlStr = "https://openapi.naver.com/v1/search/movie.xml?query="+myQuery+"&display=100";
            Log.i("myQuery", ""+myQuery);
            //실제 경로에 접속을 시도
            URL url = new URL(urlStr);
            Log.i("url", ""+url);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            //발급받은 ID와 Secret을 서버로 전달
            connection.setRequestProperty("X-Naver-Client-Id", "WjOJVQUCKcE57fdxcMMa");
            connection.setRequestProperty("X-Naver-Client-Secret", "F3qNsvJRrz");

            //위의 URL을 수행하여 받은 자원들을 자바코르도 '파싱'
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            //connection객체가 접속 후 가지게 된 내용을 parser가 스트림으로 읽어온다
            parser.setInput(connection.getInputStream(), null);

            //파서객체를 통해 각 요소별 접근을 하게되고, 태그(요소) 내부의 값들을 가져온다
            int parserEvent = parser.getEventType();

            while(parserEvent != XmlPullParser.END_DOCUMENT){
                //서버 쪽 xml문서의 끝을 만날때까지 while문이 반복

                //시작태그의 이름을 가져와서 vo에 담을 수 있는 정보라면 vo에 추가
                if(parserEvent == XmlPullParser.START_TAG){
                    String tagName = parser.getName();
                    Pattern pattern;
                    Matcher matcher;

                    if(tagName.equals("title")) {
                        vo = new MovieVO();
                        String title = parser.nextText();
                        pattern = Pattern.compile("<.*?>");
                        matcher = pattern.matcher(title);

                        if( matcher.find()){
                            String s_title = matcher.replaceAll("");
                            vo.setTitle(s_title);
                        }else{
                            vo.setTitle(title);
                        }

                        vo.setTitle(title);
                    }else if(tagName.equals("subtitle")){
                        String subtitle = parser.nextText();
                        pattern = Pattern.compile("<.*?>");
                        matcher = pattern.matcher(subtitle);

                        if( matcher.find() ){
                            String s_subtitle = matcher.replaceAll("");
                            vo.setSub_title(s_subtitle);
                        }else{
                            vo.setSub_title(subtitle);
                        }
                    }else if(tagName.equals("pubDate")){
                        String pubDate = parser.nextText();
                        vo.setPubDate(pubDate);
                    }else if(tagName.equals("director")) {
                        String director = parser.nextText();

                        pattern = Pattern.compile("\\|");
                        matcher = pattern.matcher(director);

                        if(matcher.find()){
                            String s_director = matcher.replaceAll(" ");
                            vo.setDirector(s_director);
                        }else{
                            vo.setDirector(director);
                        }
                    }else if(tagName.equals("image")){
                        String image = parser.nextText();
                        vo.setImg(image);
                    }else if(tagName.equals("actor")){
                        String actor = parser.nextText();

                        pattern = Pattern.compile("\\|");
                        matcher = pattern.matcher(actor);

                        if (matcher.find()){
                            String s_actor = matcher.replaceAll(" ");
                            vo.setActor(s_actor);
                        }else {
                            vo.setActor(actor);
                        }
                        list.add(vo);
                    }else if(tagName.equals("link")){
                        String link = parser.nextText();
                        Log.i("link", "connectNaver-link: "+link);
                    }
                    else if(tagName.equals("userRating")){
                        String s_userRating = parser.nextText();
                        //int userRating = Integer.parseInt(s_userRating);
                        vo.setUserRating(s_userRating);
                        Log.i("Rating","userRating : "+s_userRating);
                    }
                }//if()

                parserEvent = parser.next();//다음요소로 이동
            }//while

        }catch (Exception e){

        }

        return list;
    }
}
