package org.schabi.newpipe.extractor.comments;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.utils.JsonUtils;

import com.grack.nanojson.JsonObject;

public class RedditCommentsInfoItemExtractor implements CommentsInfoItemExtractor {

    private final JsonObject json;
    private String thumbnail;
    
    private static final Random rnd = new Random();
    private static final String BASE_ENDPOINT = "https://www.reddit.com";
    private static final String AVATAR_ENDPOINT = "https://www.redditstatic.com/avatars/avatar_default_";
    private static final List<String> defaultThumbnailsColors = Arrays.asList("A5A4A4", "545452", "A06A42", "C18D42",
            "FF4500", "FF8717", "FFB000", "FFD635", "DDBD37", "D4E815", "94E044", "46A508", "46D160", "0DD3BB",
            "25B79F", "008985", "24A0ED", "0079D3", "7193FF", "4856A3", "7E53C1", "FF66AC", "DB0064", "EA0027",
            "FF585B");
    private static final List<String> defaultThumbnailsOptions = Arrays.asList("01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");

    public RedditCommentsInfoItemExtractor(JsonObject json) {
        this.json = json;
    }

    @Override
    public String getUrl() throws ParsingException {
        try {
            return BASE_ENDPOINT + JsonUtils.getString(json, "data.permalink");
        } catch (Exception e) {
            throw new ParsingException("Could not get url", e);
        }
    }

    @Override
    public String getThumbnailUrl() throws ParsingException {
        return getThumbnail();
    }
    
    private String getThumbnail() {
        if(null != thumbnail) return thumbnail;
        String randomColor = defaultThumbnailsColors.get(rnd.nextInt(defaultThumbnailsColors.size()));
        String randomOption = defaultThumbnailsOptions.get(rnd.nextInt(defaultThumbnailsOptions.size()));
        return AVATAR_ENDPOINT + randomOption + "_" + randomColor + ".png";
    }

    @Override
    public String getName() throws ParsingException {
        try {
            return JsonUtils.getString(json, "data.author");
        } catch (Exception e) {
            throw new ParsingException("Could not get author name", e);
        }
    }

    @Override
    public String getPublishedTime() throws ParsingException {
        try {
            return getDateTime(JsonUtils.getNumber(json, "data.created_utc").longValue()*1000);
        } catch (Exception e) {
            throw new ParsingException("Could not get publishedTimeText", e);
        }
    }

    private String getDateTime(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        return sdf.format(date);
    }

    @Override
    public Integer getLikeCount() throws ParsingException {
        try {
            return JsonUtils.getNumber(json, "data.score").intValue();
        } catch (Exception e) {
            throw new ParsingException("Could not get like count", e);
        }
    }

    @Override
    public String getCommentText() throws ParsingException {
        try {
            return JsonUtils.getString(json, "data.body");
        } catch (Exception e) {
            throw new ParsingException("Could not get comment text", e);
        }
    }

    @Override
    public String getCommentId() throws ParsingException {
        try {
            return JsonUtils.getString(json, "data.id");
        } catch (Exception e) {
            throw new ParsingException("Could not get comment id", e);
        }
    }

    @Override
    public String getAuthorThumbnail() throws ParsingException {
        return getThumbnail();
    }

    @Override
    public String getAuthorName() throws ParsingException {
        try {
            return JsonUtils.getString(json, "data.author");
        } catch (Exception e) {
            throw new ParsingException("Could not get author name", e);
        }
    }

    @Override
    public String getAuthorEndpoint() throws ParsingException {
        try {
            return BASE_ENDPOINT + "/user/" + JsonUtils.getString(json, "data.author");
        } catch (Exception e) {
            throw new ParsingException("Could not get author endpoint", e);
        }
    }

}
