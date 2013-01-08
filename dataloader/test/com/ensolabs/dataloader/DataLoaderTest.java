package com.ensolabs.dataloader;

import android.content.Context;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class DataLoaderTest {
    private DataAgent<String> mockDataAgent;
    private Context mockContext;
    private DataLoader<String> dataLoader;

    @Before
    public void setup() {
        mockDataAgent = mock(DataAgent.class);
        mockContext = mock(Context.class);
        dataLoader = spy(new DataLoader<String>(mockContext, mockDataAgent));
    }

    @Test
    public void loadInBackground_shouldCallGetDataOnDataAgent() {
        dataLoader.loadInBackground();

        // Verify this was called in loadInBackground()
        verify(mockDataAgent).getData();
    }

    @Test
    public void shouldNotDeliverResultsWhenReset() {
        doReturn(true).when(dataLoader).isReset();

        // Call deliver result for the test
        dataLoader.deliverResult("Result");

        verify(dataLoader, never()).isStarted();
    }

    @Test
    public void shouldDeliverResultsWhenNotResetAndIsStarted() {
        doReturn(false).when(dataLoader).isReset();
        doReturn(true).when(dataLoader).isStarted();

        // Call deliver result for the test
        dataLoader.deliverResult("Results");

        // It should suffice to check the order of operations, since we can't verify
        // that a method on a base class was called explicitly. Unfortunately the Google
        // engineers did not follow the principle of 'favor composition over inheritance'
        // which would eliminate this problem
        InOrder inorder = inOrder(dataLoader);
        inorder.verify(dataLoader).isReset(); // called first
        inorder.verify(dataLoader).isStarted(); // called second
    }

    @Test
    public void onStopLoading_shouldCancelLoading() {
        // Request that the loader stops loading
        dataLoader.onStopLoading();

        // Verify it cancelled loading
        verify(dataLoader).cancelLoad();
    }

    @Test
    public void shouldDeliverResultsIfWeHaveThemWhenStarting() throws NoSuchFieldException, IllegalAccessException {
        DataLoader<String> dataLoader = new DataLoader<String>(mockContext, mockDataAgent);
        Class<? extends DataLoader> operativeClass = dataLoader.getClass();
        Field data = operativeClass.getDeclaredField("data");
        data.setAccessible(true);
        data.set(dataLoader, "Test value");

        dataLoader = spy(dataLoader);
        dataLoader.onStartLoading();

        verify(dataLoader).deliverResult("Test value");
        verify(dataLoader, never()).forceLoad();
    }

    @Test
    public void shouldForceDownloadIfWeDontHaveResultsWhenStarting() {
        dataLoader.onStartLoading();

        verify(dataLoader, never()).deliverResult(anyString());
        verify(dataLoader, times(1)).forceLoad();
    }


}
