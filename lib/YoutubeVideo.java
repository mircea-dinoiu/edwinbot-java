package lib;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class YoutubeVideo {
    private String id;
    private String title;
    private String author;
    private String url;

    public YoutubeVideo(String videoId) throws Exception {
        id = videoId;
        url = String.format("http://www.youtube.com/watch?v=%s", videoId);

        collectData();
    }

    private void parseXMLData(String url) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document xmlDoc = docBuilder.parse(url);

        title = xmlDoc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
        author = xmlDoc.getElementsByTagName("author").item(0).getFirstChild().getFirstChild().getNodeValue();
    }

    private void collectData() throws Exception {
        String url = String.format(
            "http://gdata.youtube.com/feeds/api/videos/%s",
            id
        );

        parseXMLData(url);
    }

    public String getURL() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
