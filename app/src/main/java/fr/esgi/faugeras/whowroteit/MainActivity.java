package fr.esgi.faugeras.whowroteit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Définition de la classe MainActivity qui hérite de AppCompatActivity
public class MainActivity extends AppCompatActivity {
    // Déclaration des variables pour les éléments de l'interface utilisateur
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    // Méthode appelée lors de la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activation du mode plein écran
        EdgeToEdge.enable(this);
        // Définition du layout pour cette activité
        setContentView(R.layout.activity_main);

        // Initialisation des variables avec les éléments de l'interface utilisateur
        mBookInput = (EditText)findViewById(R.id.bookInput);
        mTitleText = (TextView)findViewById(R.id.titleText);
        mAuthorText=(TextView)findViewById(R.id.authorText);
    }

    // Méthode appelée lorsque l'utilisateur clique sur le bouton de recherche
    public void searchBooks(View view) {
        // Récupération de la chaîne de recherche du champ de saisie
        String queryString = mBookInput.getText().toString();
        // Récupération du gestionnaire de clavier
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // Fermeture du clavier virtuel
        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        // Récupération du gestionnaire de connectivité
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            // Récupération de l'information sur la connectivité réseau
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        // Vérification de la connectivité réseau et de la présence d'une chaîne de recherche
        if (networkInfo != null && networkInfo.isConnected()
                && queryString.length() != 0) {
            // Exécution de la tâche asynchrone de recherche de livre
            new FetchBook(mTitleText, mAuthorText).execute(queryString);
            // Réinitialisation des champs de texte
            mAuthorText.setText("");
            mTitleText.setText(R.string.loading);
        } else {
            // Gestion des cas d'erreur
            if (queryString.length() == 0) {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_search_term);
            } else {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_network);
            }
        }
    }
}