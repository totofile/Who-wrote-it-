package fr.esgi.faugeras.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    // URL de base pour le Books API.
    private static final String BOOK_BASE_URL = "https://books.googleapis.com/books/v1/volumes?";
    // Paramètre pour la chaîne de recherche.
    private static final String QUERY_PARAM = "q";
    // Paramètre qui limite les résultats de la recherche.
    private static final String MAX_RESULTS = "maxResults";
    // Paramètre pour filtrer par type de publication.
    private static final String PRINT_TYPE = "printType";
    static String getBookInfo(String queryString){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;


        try {
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Récupère le InputStream.
            InputStream inputStream = urlConnection.getInputStream();
            // Crée un lecteur tamponné à partir de ce flux d'entrée.
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // Utilisez un StringBuilder pour contenir la réponse entrante.
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            // Puisqu'il s'agit de JSON, l'ajout d'une nouvelle ligne n'est pas nécessaire
            // (cela n'affectera pas l'analyse) mais cela facilite grandement le débogage
            // si vous affichez le buffer entier pour le débogage.
                builder.append("\n");
            }
            if (builder.length() == 0) {
                return null;
            }
            bookJSONString = builder.toString(); // Assign the built string to bookJSONString
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Écrit la réponse JSON finale dans le journal
            if(bookJSONString != null) {
                Log.d(LOG_TAG, bookJSONString);
            }
        }

        return bookJSONString;
    }
}
