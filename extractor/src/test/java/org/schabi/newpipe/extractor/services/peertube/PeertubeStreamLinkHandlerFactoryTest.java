package org.schabi.newpipe.extractor.services.peertube;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.schabi.newpipe.DownloaderTestImpl;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ParsingException;
import org.schabi.newpipe.extractor.services.peertube.linkHandler.PeertubeStreamLinkHandlerFactory;

/**
 * Test for {@link PeertubeStreamLinkHandlerFactory}
 */
public class PeertubeStreamLinkHandlerFactoryTest {
    private static PeertubeStreamLinkHandlerFactory linkHandler;

    @BeforeClass
    public static void setUp() throws Exception {
        linkHandler = PeertubeStreamLinkHandlerFactory.getInstance();
        NewPipe.init(DownloaderTestImpl.getInstance());
    }

    @Test
    public void getId() throws Exception {
        assertEquals("986aac60-1263-4f73-9ce5-36b18225cb60", linkHandler.fromUrl("https://peertube.mastodon.host/videos/watch/986aac60-1263-4f73-9ce5-36b18225cb60").getId());
        assertEquals("986aac60-1263-4f73-9ce5-36b18225cb60", linkHandler.fromUrl("https://peertube.mastodon.host/videos/watch/986aac60-1263-4f73-9ce5-36b18225cb60?fsdafs=fsafa").getId());
    }


    @Test
    public void testAcceptUrl() throws ParsingException {
        assertTrue(linkHandler.acceptUrl("https://peertube.mastodon.host/videos/watch/986aac60-1263-4f73-9ce5-36b18225cb60"));
        assertTrue(linkHandler.acceptUrl("https://peertube.mastodon.host/videos/watch/986aac60-1263-4f73-9ce5-36b18225cb60?fsdafs=fsafa"));
    }
}