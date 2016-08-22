package tp.oc.com.tpopenclassrooms2;
/**
 * Created by fabibi on 15/08/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Cette AsyncTask à pour but de télécharger le contenu d'un flux RSS
 * Ce flux sera ensuite envoyer sous la forme d'une liste d'élément RSS (List<RSSItem>) à un Adapter qui implémentera l'interface RSSItemsConsumer
 */
public class DownloadRSSFluxAsyncTask extends AsyncTask<String, Void, Document> {

    //l'interface RSSItemsConsumer
    interface RSSItemsConsumer{
        public void AddRSSItems (List<RSSItem> listRSSItem);
    }

    //notre Adapter implémentant l'interface RSSItemsConsumer
    private RSSItemsConsumer _consumer;

    /**
     * Le constructeur de notre AsyncTask qui recevra un Adapter implémentant RSSItemsConsumer
     */
    public DownloadRSSFluxAsyncTask(RSSItemsConsumer consumer) {
        Log.e("DownloadRSSFlux ","Constructeur");
        _consumer = consumer;
    }

    /**
     * Méthode qui sera utilisée pour télécharger un flux RSS
     * @param params
     * @return
     */
    @Override
    protected Document doInBackground(String... params)
    {

        try{
             //Thread.sleep(5000);

            //récupération de l'URL du flux RSS
            URL url = new URL(params[0]);

            //téléchargement du flux RSS
            Log.e("DownloadRSSFlux ","doInBackground a reçu l'url ".concat(url.toString()));
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            InputStream stream = urlConnection.getInputStream();

            //Analyse du flux RSS téléchargé et renvoit de son contenu sous la forme d'un Objet Document
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            } finally {
                stream.close();
            }

        }catch (Exception e){
            return null;

        }

    }


    /**
     * Méthode qui sera utilisé Quand on aura téléchargé un flux RSS
     * Elle aura pour but d'analyser le Document que l'on aura reçu et de renvoyer une liste d'Item RSS
     * @param result
     */
    @Override
    protected void onPostExecute(Document result)
    {
        Log.e("DownloadRSSFlux ","onPostExecute ".concat(result.getElementsByTagName("title").item(0).getTextContent()));

        //création d'une liste de RSSItem
        ArrayList<RSSItem> RSSItems = new ArrayList<RSSItem>();

        //récupération des éléments item du Document  result
        NodeList listeArticle = result.getElementsByTagName("item");

        //création d'un RSSItem pour chaque item  du Document result. On l'ajoute ensuite dans la liste de RSSItem
        for (int a = 0; a<listeArticle.getLength(); a++) {
            RSSItems.add(new RSSItem((Element) listeArticle.item(a)));
        }

        //envoit de notre liste de RSSItem à l'adapter
        _consumer.AddRSSItems(RSSItems);
    }
}