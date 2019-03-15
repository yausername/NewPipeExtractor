package org.schabi.newpipe.extractor.services.reddit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.schabi.newpipe.extractor.ServiceList.YouTube;

import java.io.IOException;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.Downloader;
import org.schabi.newpipe.extractor.ListExtractor.InfoItemsPage;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.comments.CommentsInfo;
import org.schabi.newpipe.extractor.comments.CommentsInfoItem;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.utils.Localization;

public class RedditCommentsExtractorTest {

    @BeforeClass
    public static void setUp() throws Exception {
        NewPipe.init(Downloader.getInstance(), new Localization("GB", "en"));
    }

    @Test
    public void testGetCommentsYoutube() throws IOException, ExtractionException {
        CommentsExtractor extractor = new RedditCommentingService(0).getCommentsExtractor("https://www.youtube.com/watch?v=6ZfuNTqbHE8");
        boolean result = false;
        InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();
        result = findInComments(comments, "This fucker is getting all the stones by the end of that movie. Holy shit.");

        while (comments.hasNextPage() && !result) {
            comments = extractor.getPage(comments.getNextPageUrl());
            result = findInComments(comments, "This fucker is getting all the stones by the end of that movie. Holy shit.");
        }

        assertTrue(result);
    }
    
    @Test
    public void testGetCommentsSoundcloud() throws IOException, ExtractionException {
        boolean result = false;
        CommentsExtractor extractor = new RedditCommentingService(0).getCommentsExtractor("https://soundcloud.com/pushat/the-story-of-adidon");
        InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();
        result = findInComments(comments, "Jesus Christ Pusha, he has a family. Or at least, you've told us he does");

        while (comments.hasNextPage() && !result) {
            comments = extractor.getPage(comments.getNextPageUrl());
            result = findInComments(comments, "Jesus Christ Pusha, he has a family. Or at least, you've told us he does");
        }

        assertTrue(result);
    }

    @Test
    public void testGetCommentsFromCommentsInfo() throws IOException, ExtractionException {
        CommentsExtractor extractor = new RedditCommentingService(0).getCommentsExtractor("https://www.youtube.com/watch?v=6ZfuNTqbHE8");
        boolean result = false;
        CommentsInfo commentsInfo = CommentsInfo.getInfo(extractor);
        assertTrue("r/movies".equals(commentsInfo.getName()));
        result = findInComments(commentsInfo.getRelatedItems(), "This fucker is getting all the stones by the end of that movie. Holy shit.");

        String nextPage = commentsInfo.getNextPageUrl();
        while (!StringUtil.isBlank(nextPage) && !result) {
            InfoItemsPage<CommentsInfoItem> moreItems = CommentsInfo.getMoreItems(YouTube, commentsInfo, nextPage);
            result = findInComments(moreItems.getItems(), "This fucker is getting all the stones by the end of that movie. Holy shit.");
            nextPage = moreItems.getNextPageUrl();
        }

        assertTrue(result);
    }
    
    @Test
    public void testGetCommentsAllData() throws IOException, ExtractionException {
        CommentsExtractor extractor = new RedditCommentingService(0).getCommentsExtractor("https://www.youtube.com/watch?v=6ZfuNTqbHE8");
        InfoItemsPage<CommentsInfoItem> comments = extractor.getInitialPage();
        for(CommentsInfoItem c: comments.getItems()) {
            assertFalse(StringUtil.isBlank(c.getAuthorEndpoint()));
            assertFalse(StringUtil.isBlank(c.getAuthorName()));
            assertFalse(StringUtil.isBlank(c.getAuthorThumbnail()));
            assertFalse(StringUtil.isBlank(c.getCommentId()));
            assertFalse(StringUtil.isBlank(c.getCommentText()));
            assertFalse(StringUtil.isBlank(c.getName()));
            assertFalse(StringUtil.isBlank(c.getPublishedTime()));
            assertFalse(StringUtil.isBlank(c.getThumbnailUrl()));
            assertFalse(StringUtil.isBlank(c.getUrl()));
            assertFalse(c.getLikeCount() == null);
        }
    }

    private boolean findInComments(InfoItemsPage<CommentsInfoItem> comments, String comment) {
        return findInComments(comments.getItems(), comment);
    }

    private boolean findInComments(List<CommentsInfoItem> comments, String comment) {
        for(CommentsInfoItem c: comments) {
            if(c.getCommentText().contains(comment)) {
                return true;
            }
        }
        return false;
    }
}
