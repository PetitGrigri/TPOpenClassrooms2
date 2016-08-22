package tp.oc.com.tpopenclassrooms2;
/**
 * Created by fabibi on 17/08/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fabibi on 27/06/2016.
 * L'adapter permet de gérer le contenu et des vue affichée par notre RecyclerView
 * L'Adapter triera les éléments qu'il recevra en fonction de leur date
 */
public class RSSAdapter extends RecyclerView.Adapter<RSSAdapter.RSSViewHolder> implements DownloadRSSFluxAsyncTask.RSSItemsConsumer {

    //la liste des données qui sera utilisée
    private List<RSSItem> _RSSItems = new ArrayList<RSSItem>();

    /**
     * Appelé quand RecyclerView a besoin d'un nouveau ViewHolder.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RSSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //ici on aura deux affichage légèrement différent en fonction que l'on ai une ligne paire ou impaire
        View view = inflater.inflate(viewType==1 ? R.layout.item_rss_odd: R.layout.item_rss_even, parent, false);
        return new RSSViewHolder(view);
    }

    /**
     * Bonus :)
     * Permet d'indiquer le type d'élément affiché ici on obtiendra soit 0 soit 1 (pour savoir si on a une ligne paire ou impaire)
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position%2;
    }

    /**
     * Permet à la RecyclerView d'afficher les données à la position spécifiée.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RSSViewHolder holder, int position) {
        RSSItem item = _RSSItems.get(position);
        holder.setItem(item);
    }

    /**
     * Renvoie le nombre total d'éléments de l'Adapter
     *
     * @return
     */
    @Override
    public int getItemCount()
    {
        if (_RSSItems != null)
            return _RSSItems.size();
        else
            return 0;
    }

    /**
     * Cette méthode de l'interface DownloadRSSFluxAsyncTask.RSSItemsConsumer permettra d'ajouter des éléments à notre adapter et de les trier
     * @param listRSSItem
     */
    @Override
    public void AddRSSItems(List<RSSItem> listRSSItem)
    {
        _RSSItems.addAll(listRSSItem);
        Collections.sort(_RSSItems);

        notifyDataSetChanged();
    }

    /**
     * Cette méthode permettra vider la liste des RSSItem de l'Adapter
     */
    public void clear()
    {
        _RSSItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Cette méthode sera appelé lorsque la vue du holder ne sera plus affichée
     * Dans notre cas précis elle arretera le téléchargement d'une image en cours de téléchargement s'il y en avait une.
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(RSSViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopAsyncDownload();
    }

    /**
     * Le ViewHolder décrit la vue et les données d'un d'élément dans le RecyclerView
     */
    public class RSSViewHolder extends RecyclerView.ViewHolder
    {
        //le RSSItem du holder
        private RSSItem _item;

        //les différentes View  qui seront utilisées par le holder
        private TextView _titre;
        private TextView _description;
        private TextView _date;
        private TextView _source;
        private ImageView _image;
        private DownloadImageAsyncTask _asyncTask;

        /**
         * constructeur du holder
         *
         * @param itemView
         */
        public RSSViewHolder(final View itemView)
        {
            super(itemView);
            //récupération des différentes View de itemView
            _titre = (TextView) itemView.findViewById(R.id.item_titre);
            _description = (TextView) itemView.findViewById(R.id.item_description);
            _date = (TextView) itemView.findViewById(R.id.item_date);
            _source = (TextView) itemView.findViewById(R.id.item_source);
            _image = (ImageView) itemView.findViewById(R.id.item_image);

            //mise en place d'un onClickLister afin d'afficher l'actualité correspondant à notre Item du flux RSS
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //récupération du lien de l'item RSSItem
                    String url = _item.getLink();

                    //création d'un intent qui affichera l'article qui correspondra à notre Item du flux RSS
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    view.getContext().startActivity(i);
                }
            });

        }

        /**
         * Méthode qui permettra de set notre RSSItem, et de s'occuper de l'affichage de notre Item RSS
         * Si le RSSItem qu'on a reçu contient une url d'image dans son "enclosure" on la téléchargera et on l'affichera
         *
         * @param item
         */
        public void setItem(RSSItem item)
        {
            //formatage de la date du RSSItem et affichage
            SimpleDateFormat formater = new SimpleDateFormat("'le' dd/MM/yyyy 'à' hh:mm:ss");
            String datePub= (item.getPubdate() != null) ?formater.format(item.getPubdate()).toString() : "";

            //récupération de la source du flux RSS (ici on se basera sur le "Link" du RSSItem dont on récupèrera le "host")
            String hostLink= "";
            try {
                URL url = new URL(item.getLink());
                hostLink= url.getHost();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            //récupération de l'url d'une image contenu dans l'enclosure de l'item RSS. Si on a une url, on téléchargera l'image via une AsyncTask
            String tempo_url_image = item.getEnclosure();
            _image.setVisibility(View.GONE);

            if (!tempo_url_image.isEmpty()) {
                Log.e("RSSViewHolder ", "J'ai une url pour l'image : ".concat(tempo_url_image));
                _asyncTask =  new DownloadImageAsyncTask(_image);
                _asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tempo_url_image);
            }
            else {
                Log.e("RSSViewHolder ", "PAS d'URL");
            }

            //affichage des informations de l'item RSS
            _item = item;
            _titre.setText(item.getTitle());
            _description.setText(item.getDescription());
            _date.setText(datePub);
            _source.setText(hostLink);
        }

        //méthode qui permettra de stopper le téléchargement de l'image si il y en a une
        public void stopAsyncDownload()
        {
            if (_asyncTask != null)
            {
                _asyncTask.cancel(true);
                Log.e("RSSViewHolder ", "Annulation du téléchargement de l'image");
            }
        }
    }
}

