package org.schabi.newpipe.extractor;

import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

public interface CommentingService {
    int getServiceId();
    String getName();
    CommentsExtractor getCommentsExtractor(String url) throws ExtractionException;
}
