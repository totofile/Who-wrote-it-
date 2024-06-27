package fr.esgi.faugeras.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

// Définition de la classe FetchBook qui hérite de AsyncTask
// Cette classe est utilisée pour effectuer une tâche asynchrone de récupération de données de livre
public class FetchBook extends AsyncTask<String, Void, String> {
    // Déclaration des variables pour les éléments de l'interface utilisateur
    // Utilisation de WeakReference pour éviter les fuites de mémoire
    private WeakReference<TextView> mTitleText;
    private WeakReference<TextView> mAuthorText;

    // Constructeur de la classe FetchBook
    FetchBook(TextView titleText, TextView authorText) {
        this.mTitleText = new WeakReference<>(titleText);
        this.mAuthorText = new WeakReference<>(authorText);
    }

    // Méthode appelée après l'exécution de la tâche asynchrone
    // Cette méthode est utilisée pour mettre à jour l'interface utilisateur avec les résultats de la tâche asynchrone
    @Override
    protected void onPostExecute(String s) {
        try {
            // Création d'un JSONObject à partir de la chaîne JSON
            JSONObject jsonObject = new JSONObject(s);
            // Récupération du tableau d'éléments à partir du JSONObject
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String authors = null;
            // Boucle sur le tableau d'éléments jusqu'à ce qu'un livre soit trouvé
            while (i < itemsArray.length() &&
                    (authors == null && title == null)) {
                // Récupération du livre et des informations sur le volume à partir du tableau d'éléments
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                try {
                    // Récupération du titre et des auteurs du livre
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            // Mise à jour de l'interface utilisateur avec le titre et les auteurs du livre
            if (title != null && authors != null) {
                mTitleText.get().setText(title);
                mAuthorText.get().setText(authors);
            } else {
                // Mise à jour de l'interface utilisateur avec un message d'erreur si aucun livre n'a été trouvé
                mTitleText.get().setText(R.string.no_results);
                mAuthorText.get().setText("");
            }
        } catch (Exception e) {
            // Mise à jour de l'interface utilisateur avec un message d'erreur si une exception a été levée
            mTitleText.get().setText(R.string.no_results);
            mAuthorText.get().setText("");
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }

    // Méthode appelée pour exécuter la tâche asynchrone
    // Cette méthode est utilisée pour effectuer la requête réseau et récupérer les données de livre
    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }
}