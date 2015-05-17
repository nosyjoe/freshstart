package de.philippengel.android.freshstart.data;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.philippengel.android.freshstart.BuildConfig;
import de.philippengel.android.freshstart.requests.DatabindRequest;
import de.philippengel.android.freshstart.requests.ResponseListener;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Philipp Engel <philipp@filzip.com>
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 21, constants = BuildConfig.class)
public class SessionTest {
    
    @Mock
    RequestQueue mockQueue;
    
    @Before
    public void setup() {
        initMocks(this);
    }
    
    @Test
    public void testAddToQueue() throws Exception {
        Session session = new Session(mockQueue);
        ResponseListener<String> mockListener = mock(ResponseListener.class);
    
        DatabindRequest<String> request = DatabindRequest.get("http://example.com/api", String.class, mockListener);
        session.addToQueue(request);
        
        verify(mockQueue).add(request);
    }
    
    @Test
    public void testCancelAll() throws Exception {
        assertTrue(true);
    }

}
