package net.nemerosa.jenkins.seed.triggering.connector.bitbucket;

import net.nemerosa.jenkins.seed.triggering.SeedChannel;
import net.nemerosa.jenkins.seed.triggering.SeedEvent;
import net.nemerosa.jenkins.seed.triggering.SeedEventType;
import net.nemerosa.jenkins.seed.triggering.SeedService;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static net.nemerosa.jenkins.seed.triggering.connector.EndPointTestSupport.mockStaplerResponse;
import static org.mockito.Mockito.*;

public class BitBucketEndPointTest {

    public static final SeedChannel BITBUCKET_CHANNEL = SeedChannel.of("bitbucket", "Seed BitBucket end point");

    @Test
    public void commit_event() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockBitBucketRequest("repo:push", "/bitbucket-payload-commit.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        getEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "master",
                        SeedEventType.COMMIT,
                        BITBUCKET_CHANNEL)
                        .withParam("commit", "a083aca5efac42ee26ee5a554f0c57c7af3bc64c")
        );
    }

    protected BitBucketEndPoint getEndPoint(SeedService seedService) {
        return new BitBucketEndPoint(seedService);
    }

    @Test
    public void seed_event() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockBitBucketRequest("repo:push", "/bitbucket-payload-seed.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        getEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "master",
                        SeedEventType.SEED,
                        BITBUCKET_CHANNEL)
        );
    }

    @Test
    public void seed_event_mixed() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockBitBucketRequest("repo:push", "/bitbucket-payload-seed-mixed.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        getEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "master",
                        SeedEventType.SEED,
                        BITBUCKET_CHANNEL)
        );
    }

    @Test
    public void create_branch() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockBitBucketRequest("repo:push", "/bitbucket-payload-create.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        getEndPoint(seedService).doDynamic(request, response);
        // Verifying
        verify(seedService, times(1)).post(
                new SeedEvent(
                        "nemerosa/seed-demo",
                        "test",
                        SeedEventType.CREATION,
                        BITBUCKET_CHANNEL)
        );
    }

    @Test
    public void create_tag() throws IOException {
        StaplerResponse response = mockStaplerResponse();
        // Request
        StaplerRequest request = mockBitBucketRequest("repo:push", "/bitbucket-payload-create-tag.json");
        // Service mock
        SeedService seedService = mock(SeedService.class);
        // Call
        getEndPoint(seedService).doDynamic(request, response);
        // Verifying that the event is not accepted
        verify(seedService, times(0)).post(any(SeedEvent.class));
    }

    private StaplerRequest mockBitBucketRequest(String event, String payload) throws IOException {
        StaplerRequest request = mock(StaplerRequest.class);
        when(request.getHeader("X-Event-Key")).thenReturn(event);
        when(request.getReader()).thenReturn(
                new BufferedReader(
                        new InputStreamReader(
                                getClass().getResourceAsStream(payload),
                                "UTF-8"
                        )
                )
        );
        return request;
    }
}
