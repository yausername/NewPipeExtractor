package org.schabi.newpipe.extractor.services.reddit;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.jsoup.helper.StringUtil;
import org.schabi.newpipe.extractor.DownloadResponse;
import org.schabi.newpipe.extractor.Downloader;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.comments.CommentsInfoItemExtractor;
import org.schabi.newpipe.extractor.comments.CommentsInfoItemsCollector;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.utils.JsonUtils;
import org.schabi.newpipe.extractor.utils.Localization;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

public class RedditCommentsExtractor extends CommentsExtractor {
    
    private String subReddit;
    private InfoItemsPage<CommentsInfoItem> initPage;
    
    private static final String BASE_ENDPOINT = "https://www.reddit.com";
    private static final String SEARCH_ENDPOINT = "https://api.reddit.com/search.json?syntax=cloudsearch&q=%s&sort=comments&type=link&limit=1";
    private static final String QUERY_FORMAT_YT = "(url:%s) AND (site:youtube.com OR site:youtu.be)";
    private static final String QUERY_FORMAT = "(url:%s) AND (site:%s)";
    //private static final String SEARCH_ENDPOINT = "https://www.reddit.com/api/info.json?url=%s&sort=comments&type=link&limit=1";

    public RedditCommentsExtractor(StreamingService service, ListLinkHandler uiHandler, Localization localization) {
        super(service, uiHandler, localization);
    }

    @Override
    public InfoItemsPage<CommentsInfoItem> getInitialPage() throws IOException, ExtractionException {
        super.fetchPage();
        return initPage;
    }

    @Override
    public String getNextPageUrl() throws IOException, ExtractionException {
        super.fetchPage();
        return initPage.getNextPageUrl();
    }

    @Override
    public InfoItemsPage<CommentsInfoItem> getPage(String pageUrl) throws IOException, ExtractionException {
        DownloadResponse response = NewPipe.getDownloader().get(pageUrl);
        if (null == response || null == response.getResponseBody()) {
            return InfoItemsPage.emptyPage();
        }
        JsonArray jsonArr;
        try {
            jsonArr = JsonParser.array().from(response.getResponseBody());
        } catch (JsonParserException e) {
            throw new ParsingException("Could not parse json data for reddit comments", e);
        }
        if (null == jsonArr || jsonArr.size() < 2) {
            return InfoItemsPage.emptyPage();
        }
        JsonArray commentsArr = JsonUtils.getArray(jsonArr.getObject(1), "data.children");
        if(commentsArr.isEmpty()) {
            return InfoItemsPage.emptyPage();
        }
        CommentsInfoItemsCollector collector = new CommentsInfoItemsCollector(getServiceId());
        collectCommentsFrom(collector, commentsArr);
        return new InfoItemsPage<>(collector, getNextPageUrl(commentsArr));
    }

    private void collectCommentsFrom(CommentsInfoItemsCollector collector, JsonArray commentsArr) {
        for(Object c: commentsArr) {
            if(c instanceof JsonObject) {
                // kind == t1 for comments
                if("t1".equals(((JsonObject)c).getString("kind"))) {
                    CommentsInfoItemExtractor extractor = new RedditCommentsInfoItemExtractor((JsonObject) c);
                    collector.commit(extractor);
                }
            }
        }
    }

    private String getNextPageUrl(JsonArray commentsArr) {
        return "";
    }

    @Override
    public void onFetchPage(Downloader downloader) throws IOException, ExtractionException {
        String commentsEndpoint = null;
        try {
            commentsEndpoint = searchComments(getId(), downloader);
        } catch (JsonParserException | URISyntaxException e) {
            //nothing to do here
        }
        if(StringUtil.isBlank(commentsEndpoint)) {
            initPage = InfoItemsPage.emptyPage();
        }else {
            initPage = getPage(commentsEndpoint);
        }
    }
    
    private String searchComments(String videoId, Downloader downloader) throws ReCaptchaException, IOException, JsonParserException, ParsingException, URISyntaxException {
        String query = null;
        if(getServiceId() == ServiceList.YouTube.getServiceId()) {
            query = String.format(QUERY_FORMAT_YT, videoId);
        }else {
            URI uri = new URI(getUrl());
            String host = uri.getHost();
            StringBuilder url = new StringBuilder(uri.getPath().substring(1));
            if(uri.getQuery() != null) url.append(uri.getQuery());
            query = String.format(QUERY_FORMAT, url.toString(), host);
        }
        String searchUrl = String.format(SEARCH_ENDPOINT, URLEncoder.encode(query, "UTF-8"));
        //String searchUrl = String.format(SEARCH_ENDPOINT, query);
        DownloadResponse response = downloader.get(searchUrl);
        if (null == response || null == response.getResponseBody()) {
            return "";
        }
        JsonObject json = JsonParser.object().from(response.getResponseBody());
        if (null == json) {
            return "";
        }
        JsonArray arr = JsonUtils.getArray(json, "data.children");
        if(null == arr || arr.size() == 0) {
            return "";
        }
        subReddit = JsonUtils.getString(arr.getObject(0), "data.subreddit_name_prefixed");
        String permalink = JsonUtils.getString(arr.getObject(0), "data.permalink");
        return BASE_ENDPOINT + permalink + ".json?depth=1&sort=best";
    }

    @Override
    public String getName() throws ParsingException {
        return subReddit;
    }

}
