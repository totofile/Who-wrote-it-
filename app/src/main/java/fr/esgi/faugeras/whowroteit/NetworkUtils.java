package fr.esgi.faugeras.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Classe utilitaire pour gérer les requêtes réseau à l'API Google Books
public class NetworkUtils {
    // Constantes pour la construction de l'URL de la requête
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String BOOK_BASE_URL = "https://books.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";

    // Méthode pour obtenir les informations sur un livre à partir de l'API Google Books
    static String getBookInfo(String queryString){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            // Construction de l'URL de la requête
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            URL requestURL = new URL(builtURI.toString());

            // Ouverture de la connexion à l'URL
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Lecture de la réponse de l'API
            InputStream inputStream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            // Si la réponse est vide, retourne null
            if (builder.length() == 0) {
                return null;
            }

            // Conversion de la réponse en chaîne de caractères
            bookJSONString = builder.toString();
            // Log de la réponse
            Log.d(LOG_TAG, bookJSONString);

        } catch (IOException e){
            // Gestion des exceptions
            e.printStackTrace();
        } finally {
            // Fermeture de la connexion et du reader
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

        }

        // Retour de la réponse de l'API
        return bookJSONString;
    }

}