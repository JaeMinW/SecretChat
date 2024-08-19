package parser;

public class ParserItem {

    public String Client(String value){

        if( value.equals("key")){
            String client_key = "WjOJVQUCKcE57fdxcMMa";
            return client_key;
        }else{
            String client_secret = "F3qNsvJRrz";
            return client_secret;
        }

    }


}
