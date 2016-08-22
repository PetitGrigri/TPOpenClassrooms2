package tp.oc.com.tpopenclassrooms2;

import android.util.Log;

import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Cette classe à pour put de représenter un ITEM d'un flux RSS,
 * Elle recevra lors de sa création un Element, correspondant à la une balise <item></item> qui aura été parser préalablement
 *
 * Pour plus d'info : https://validator.w3.org/feed/docs/rss2.html#hrelementsOfLtitemgt
 *
 * Cette classe implémentera la méthode de l'interface Comparable permettant un trie (ici on se basera sur la date de publication
 */
public class RSSItem implements Comparable<RSSItem>
{
    //les différents éléments que peut contenir un item
    private static String RSS_ITEM_TITLE = "title";
    private static String RSS_ITEM_LINK = "link";
    private static String RSS_ITEM_DESCRIPTION = "description";
    private static String RSS_ITEM_AUTHOR = "author";
    private static String RSS_ITEM_CATEGORY = "category";
    private static String RSS_ITEM_COMMENTS = "comments";
    private static String RSS_ITEM_ENCLOSURE = "enclosure";
    private static String RSS_ITEM_ENCLOSURE_URL = "url";
    private static String RSS_ITEM_GUID = "guid";
    private static String RSS_ITEM_PUBDATE = "pubDate";
    private static String RSS_ITEM_SOURCE = "source";

    //L'élément que l'on aura reçu lors de la construction de l'objet
    private Element _element;


    //Le constructeur qui se contentera de conserver l'Element que l'on nous aura envoyer
    public RSSItem (Element element)
    {
        _element = element;
    }

    //récupération du titre de l'item du flux RSS
    public String getTitle(){ return getItemValue(RSS_ITEM_TITLE); }

    //récupération du lien de l'item du flux RSS
    public String getLink(){
        return getItemValue(RSS_ITEM_LINK);
    }

    //récupération du lien de l'item du flux RSS
    public String getDescription() {
        return getItemValue(RSS_ITEM_DESCRIPTION);
    }

    //récupération de l'auteur de l'item du flux RSS
    public String getAuthor() {
        return getItemValue(RSS_ITEM_AUTHOR);
    }

    //récupération de la catégorie de l'item du flux RSS
    public String getCategory() {
        return getItemValue(RSS_ITEM_CATEGORY);
    }

    //récupération des commentaires de l'item du flux RSS
    public String getComments() {
        return getItemValue(RSS_ITEM_COMMENTS);
    }

    /**
     * récupération de l'url contenu par l'Element Enclosure du flux RSS
     * (dans la logique on pourrait renvoyer un nouvel objet RSSEnclosure, mais j'ai fait au plus rapide ;) )
     */
    public String getEnclosure() {
        Element tempo = (Element) _element.getElementsByTagName(RSS_ITEM_ENCLOSURE).item(0);
        if (tempo !=null)
            return tempo.getAttribute(RSS_ITEM_ENCLOSURE_URL);
        else
            return "";
    }

    //récupération du "guid" de l'item du flux RSS
    public String getGuid() {
        return getItemValue(RSS_ITEM_GUID);
    }

    //récupération de la date de publication  de l'item du flux RSS
    public Date getPubdate()
    {
        try {
            //récupération de la date sous format string puis conversion de cette dernière en Date
            String dateString = getItemValue(RSS_ITEM_PUBDATE);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzzz", Locale.ENGLISH);
            Date date = sdf.parse(dateString);
            return date;
        } catch (ParseException e) {
            //si on n'a rien à parser ou autre on renvoit null
            e.printStackTrace();
            return null;
        }
    }
    //récupération de la source de l'item du flux RSS
    public String getSource() {
        return getItemValue(RSS_ITEM_SOURCE);
    }

    /**
     * Méthode permettant de lire un Element de notre RSS ITEM
     *
     * @param itemName
     * @return
     */
    private String getItemValue(String itemName) {
        if (_element.getElementsByTagName(itemName) != null && _element.getElementsByTagName(itemName).item(0) != null)
            return _element.getElementsByTagName(itemName).item(0).getTextContent();
        else
            return "";
    }

    /**
     * Cette méthode (de l'interface Comparable) permettra de trier nos RSSItem en fonction de leur date de publication
     * @param rssItem
     * @return
     */
    @Override
    public int compareTo(RSSItem rssItem) {
        if ((rssItem.getPubdate() != null) && (this.getPubdate() != null))
            return rssItem.getPubdate().compareTo(this.getPubdate());
        else
            return 0;
    }
}
