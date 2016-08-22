package tp.oc.com.tpopenclassrooms2;

import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dans un soucis de débugage, j'ai laissé les Log, pour pouvoir continuer sur ce TP, pour ajouter d'autres options
 */
public class MainActivity extends AppCompatActivity {

    //liste des sources des flux RSS (les flux en questions n'utilisent pas les balises CDATA, qui nécessitent
    private List<String> _urlList = Arrays.asList(
            "http://www.lemonde.fr/enseignement-superieur/rss_full.xml",
            "http://www.lemonde.fr/politique/rss_full.xml",
            "http://www.lemonde.fr/paris/rss_full.xml",
            "http://www.legorafi.fr/feed/"
            );

    //la liste des AsyncTask qui seront utilisées pour télécharger en synchro
    private List<DownloadRSSFluxAsyncTask> _asyncTasks;
    //l'adapter de la recycler view
    private RSSAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("MainActivity ","onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //création d'une liste d'asyncTask qui seront lancées en même temps plus tard
        _asyncTasks = new ArrayList<DownloadRSSFluxAsyncTask>();

        //récupération de la RecyclerView et configuration de cette dernière
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        _adapter = new RSSAdapter();
        rv.setAdapter(_adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        //création d'un listener pour la RecyclerView afin d'afficher ou de cacher la progressbar
        _adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                findViewById(R.id.progress).setVisibility(View.GONE);
                findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
            }
        });

        //Chargement des flux RSS en synchro
        refresh();

    }

    /**
     *
     * Lorsque l'on quitte l'application, on Annule l'exécution de toute les AsyncTask
     */
    @Override
    protected void onDestroy() {
        Log.e("MainActivity ","onDestroy");
        super.onDestroy();
        clear();

    }

    /**
     * Cette méthode va permettre à partir de la liste d'url _urlList de lancer différentes AsyncTask qui mettront à jour la RecyclerView via l'adapter
     */
    private void refresh()
    {
        Log.e("MainActivity ","refresh");

        //annulation et suppression des AsyncTask si elles sont en cours
        clear();

        //on cache la RecyclerView, et on affiche la barre de progression tant que l'on a pas reçu le contenu
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.recycler_view).setVisibility(View.GONE);



        //création d'une AsyncTask pour chaque URL de _urlList et execution de cette dernière (executeOnExecutor permet de lancer les AsyncTask en même temps)
        for (String url : _urlList){
            Log.e("MainActiviy ", "je crée l'asyncTask avec l'url ".concat(url).concat("et je l'exécute"));
            _asyncTasks.add(new DownloadRSSFluxAsyncTask(_adapter));
            _asyncTasks.get(_asyncTasks.size()-1).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    /**
     * Cette méthode va permettre l'anulation de AsyncTask en cours de réalisation, puis de les supprimer
     * (une AsyncTask ne peux être exécuter qu'un seule fois il faudra donc la recréer pour l'exécuter de nouveau)
     */
    private void clear()
    {
        Log.e("MainActivity ","clear");

        //Annulation de l'exécution de chaque AsyncTask
        for (DownloadRSSFluxAsyncTask asyncTask : _asyncTasks) {
            Log.e("MainActiviy ", "Je Kill l'asyncTask numéro ".concat(Integer.toString(_asyncTasks.indexOf(asyncTask))));

            if ( asyncTask != null)
                asyncTask.cancel(true);
        }
        //On vide notre liste d'AsyncTask
        _asyncTasks.clear();
        //On vide le contenu des éléments gérer par l'adapter de la RecyclerView
        _adapter.clear();
    }


    /**
     * Permet d'afficher un menu dans notre action Bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Permet de gérer les click sur les éléments du menu
     * (Pour le moment on n'a qu'un élément donc on utilise un simple if à la place du switch)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //raffraichissement des données affichées par la RecyclerView
        if (item.getItemId() == R.id.main_menu_refresh){
            Log.e("MainActivity ", "Je dois raffraichir l'affichage");
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }
}
