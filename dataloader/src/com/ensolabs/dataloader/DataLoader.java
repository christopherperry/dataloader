package com.ensolabs.dataloader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * A DataLoader is a class that works in conjunction with
 * a {@link DataAgent DataAgent} in order to fulfill your Asynchronous data
 * loading needs.
 * <p/>
 * Simply instantiate an DataLoader, give him a {@link DataAgent DataAgent}
 * to work with in his constructor, and he'll grab your
 * data behind the scenes (on a background thread).
 *
 * @param <T> The type of data you would like returned.
 * @author christopherperry
 */
public class DataLoader<T> extends AsyncTaskLoader<T> {
    private DataAgent<T> dataAgent;
    private T data;

    public DataLoader(Context context, DataAgent<T> dataAgent) {
        super(context);
        this.dataAgent = dataAgent;
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (data != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(data);
        } else {
            forceLoad();
        }
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public T loadInBackground() {
        return dataAgent.getData();
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }

            return;
        }

        T oldData = data;
        this.data = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

        // At this point we can release the resources if needed,
        // now that the new result is delivered we know that it is no longer in use.
        if (oldData != null) {
            onReleaseResources(oldData);
        }
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources if needed.
        if (data != null) {
            onReleaseResources(data);
            data = null;
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(T data) {
        super.onCanceled(data);

        // At this point we can release the resources associated if needed.
        onReleaseResources(data);
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set. If you need this, override
     * this class and
     */
    protected void onReleaseResources(T data) {
        // For a simple List there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
