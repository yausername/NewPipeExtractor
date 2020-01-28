package org.schabi.newpipe.extractor.services.peertube.extractors;

import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.localization.DateWrapper;
import org.schabi.newpipe.extractor.services.peertube.PeertubeParsingHelper;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.StreamType;
import org.schabi.newpipe.extractor.utils.JsonUtils;

import com.grack.nanojson.JsonObject;

public class PeertubeStreamInfoItemExtractor implements StreamInfoItemExtractor {
    
    protected final JsonObject item;
    private final String baseUrl;
    
    public PeertubeStreamInfoItemExtractor(JsonObject item, String baseUrl) {
        this.item = item;
        this.baseUrl = baseUrl;
    }
    
    @Override
    public String getUrl() throws ParsingException {
        String uuid = JsonUtils.getString(item, "uuid");
        return ServiceList.PeerTube.getStreamLHFactory().fromId(uuid, baseUrl).getUrl();
    }
    
    @Override
    public String getThumbnailUrl() throws ParsingException {
        String value = JsonUtils.getString(item, "thumbnailPath");
        return baseUrl + value;
    }
    
    @Override
    public String getName() throws ParsingException {
        return JsonUtils.getString(item, "name");
    }
    
    @Override
    public boolean isAd() throws ParsingException {
        return false;
    }
    
    @Override
    public long getViewCount() throws ParsingException {
        Number value = JsonUtils.getNumber(item, "views");
        return value.longValue();
    }
    
    @Override
    public String getUploaderUrl() throws ParsingException {
        String name = JsonUtils.getString(item, "account.name");
        String host = JsonUtils.getString(item, "account.host");
        return ServiceList.PeerTube.getChannelLHFactory().fromId(name + "@" + host, baseUrl).getUrl();
    }
    
    @Override
    public String getUploaderName() throws ParsingException {
        return JsonUtils.getString(item, "account.displayName");
    }
    
    @Override
    public String getTextualUploadDate() throws ParsingException {
        return JsonUtils.getString(item, "publishedAt");
    }

    @Override
    public DateWrapper getUploadDate() throws ParsingException {
        final String textualUploadDate = getTextualUploadDate();

        if (textualUploadDate == null) {
            return null;
        }

        return new DateWrapper(PeertubeParsingHelper.parseDateFrom(textualUploadDate));
    }
   
    @Override
    public StreamType getStreamType() throws ParsingException {
        return StreamType.VIDEO_STREAM;
    }
    
    @Override
    public long getDuration() throws ParsingException {
        Number value = JsonUtils.getNumber(item, "duration");
        return value.longValue();
    }

}
