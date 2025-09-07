/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema;


import java.io.InputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Thebonn
 */
public class Conectar {

    public static String pegarAvancado(String link) {
        try {

            Connection.Response res = Jsoup.connect(link)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .data("text", "json")
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = res.parse();
            return doc.body().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String pegarImg(String link) {
        try {

            Connection.Response res = Jsoup.connect(link)
                    .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                    .data("text", "json")
                    .method(Connection.Method.GET)
                    .execute();

            Document doc = res.parse();
            return doc.body().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static String pegarSimples(String link) throws Exception {
            return Jsoup.connect(link).ignoreContentType(true).timeout(9000).execute().body();
    }
    
    public static InputStream pegarInputStream(String link) {
        try {
            return Jsoup.connect(link).ignoreContentType(true).timeout(9000).execute().bodyStream();
        } catch (Exception ex) {
            return null;
        }
        
    }
    
    
}
