package org.schabi.newpipe.extractor.services.reddit;

import java.util.Collections;

import org.schabi.newpipe.extractor.CommentingService;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;

public class RedditCommentingService implements CommentingService {
    private final int serviceId;
    
    public RedditCommentingService(int serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int getServiceId() {
        return serviceId;
    }

    @Override
    public CommentsExtractor getCommentsExtractor(String url) throws ExtractionException {
        StreamingService service = NewPipe.getServiceByUrl(url);
        LinkHandler lh = service.getStreamLHFactory().fromUrl(url);
        return new RedditCommentsExtractor(service, new ListLinkHandler(lh, Collections.<String>emptyList(), null), NewPipe.getPreferredLocalization());
    }

    @Override
    public String getName() {
        return "Reddit";
    }
}
