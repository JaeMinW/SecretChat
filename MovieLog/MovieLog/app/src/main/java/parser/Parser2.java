package parser;

import com.kty.pj_movie.NaverActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vo.MovieVO;

public class Parser2 {

    MovieVO vo;
    String myQuery = "";

    public ArrayList<MovieVO> connectNaver(int user_idx){
        ArrayList<MovieVO> list = new ArrayList<>();


        try{
            //검색어(myQuery) UTF-8 형태로 인코딩
            myQuery = URLEncoder.encode(NaverActivity.search.getText().toString(),"UTF-8");
            String urlStr = "https://openapi.naver.com/v1/search/movie.xml?query="+myQuery+"&display=100";

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            //발급받은 id, secret 전달
            connection.setRequestProperty("X-Naver-Client-Id", "61ITHsl4YSs7DNlZcs1L");
            connection.setRequestProperty("X-Naver-Client-Secret", "QNfgjtP9O6");

            //url 자원 자바코드로 파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            //connection객체 접속 후 받은 내용 parser가 스트림으로 읽어온다
            parser.setInput(connection.getInputStream(), null);

            //파서객체를 통해 각 요소별 접근, 태그 내부 값 가져옴
            int parserEvent = parser.getEventType();

            while( parserEvent != XmlPullParser.END_DOCUMENT ){

                if( parserEvent == XmlPullParser.START_TAG ){
                    String tagname = parser.getName();
                    if( tagname.equals("title") ){
                        vo = new MovieVO();
                        String title = parser.nextText();
                        vo.setM_title(title);
                        vo.setUser_idx(user_idx);

                    }else if( tagname.equals("image") ){
                        String img = parser.nextText();
                        vo.setM_img(img);
                    }else if( tagname.equals("subtitle") ){
                        String subtitle = parser.nextText();


                        //가져온 title에 <b>태그가 포함되어 있는지를 검사
                        Pattern pattern1 = Pattern.compile("<.*?>");
                        Matcher matcher1 = pattern1.matcher(subtitle);

                        if( matcher1.find() ){
                            String s_subtitle = matcher1.replaceAll("");
                            vo.setM_subtitle(s_subtitle);
                        }else{
                            vo.setM_subtitle(subtitle);
                        }

                    }else if(tagname.equals("pubDate") ){
                        String pubDate = parser.nextText();
                        vo.setM_pubDate(pubDate);
                    }else if(tagname.equals("director") ){
                        String director = parser.nextText();

                        Pattern pattern2 = Pattern.compile("\\|");
                        Matcher matcher2 = pattern2.matcher(director);

                        if( matcher2.find() ){
                            String s_director = director.replaceAll("\\|",", ");

                            if(s_director.charAt(s_director.length() - 2) == ','){
                                String str = s_director.substring(0, s_director.length()-2);
                                vo.setM_director(str);
                            }else {

                                vo.setM_director(s_director);
                            }
                        }else{
                            vo.setM_director(director);
                        }
                        list.add(vo);
                    }
                }//if(.Start_TAG)

                parserEvent = parser.next();//다음요소(줄바꿈)
            }//while

        }catch (Exception e){

        }

        return list;
    }

}
