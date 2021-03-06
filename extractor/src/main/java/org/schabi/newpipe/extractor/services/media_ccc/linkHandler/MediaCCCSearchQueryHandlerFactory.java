package org.schabi.newpipe.extractor.services.media_ccc.linkHandler;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MediaCCCSearchQueryHandlerFactory extends SearchQueryHandlerFactory {

    public static final String ALL = "all";
    public static final String CONFERENCES = "conferences";
    public static final String EVENTS = "events";

    @Override
    public String[] getAvailableContentFilter() {
        return new String[] {
                ALL,
                CONFERENCES,
                EVENTS
        };
    }

    @Override
    public String[] getAvailableSortFilter() {
        return new String[0];
    }

    @Override
    public String getUrl(String querry, List<String> contentFilter, String sortFilter) throws ParsingException {
        try {
            return "https://api.media.ccc.de/public/events/search?q=" + URLEncoder.encode(querry, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ParsingException("Could not create search string with querry: " + querry, e);
        }
    }
}
