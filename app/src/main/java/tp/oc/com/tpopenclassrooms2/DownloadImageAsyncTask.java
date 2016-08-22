package tp.oc.com.tpopenclassrooms2;
/**
 * Created by fabibi on 21/08/2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Cette AsyncTask permet de télécharger une image et de l'afficher dans l'ImageView que l'on aura fournis lors de l'initialisation de cette AsyncTask
 */
public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    ImageView _image;

    /**
     * Consctructeur qui recevra une ImageView.
     */
    public DownloadImageAsyncTask(ImageView image) {
        _image = image;
    }

    /**
     * Méthode qui lancera le téléchargement de l'image
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            //récupération de l'url de l'image et récupération de son contenu
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
            InputStream stream = urlConnection.getInputStream();
            //création d'un contenu Bitmap via le stream téléchargé préalablement
            Bitmap image = BitmapFactory.decodeStream(stream);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Méthode lancé à la fin du téléchargement de l'image.
     * On enverra le Bitmap du doInBackground à l'ImageView via setImageBitmap
     *
     * @param result
     */
    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            _image.setImageBitmap(result);
            _image.setVisibility(View.VISIBLE);
        }
    }
}