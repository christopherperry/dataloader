package com.ensolabs.dataloader;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;

/**
 * Decorator for {@link LoaderManager.LoaderCallbacks LoaderCallbacks} that turns
 * indeterminate progress on/off in the {@link android.app.ActionBar Actionbar}.
 * <p/>
 * Make sure your {@link FragmentActivity FragmentActivity} has called
 * {@link FragmentActivity#requestWindowFeature(int) requestWindowFeauture} with
 * {@link android.view.Window#FEATURE_INDETERMINATE_PROGRESS FEATURE_INDETERMINATE_PROGRESS} or
 * nothing will show up.
 *
 * @author christopherperry
 */
public class ProgressCallbacks<T> implements LoaderManager.LoaderCallbacks<T> {
    private WeakReference<FragmentActivity> activityWeakReference;
    private LoaderManager.LoaderCallbacks<T> wrappedCallbacks;

    public ProgressCallbacks(LoaderManager.LoaderCallbacks<T> wrappedCallbacks, FragmentActivity fragmentActivity) {
        this.wrappedCallbacks = wrappedCallbacks;
        activityWeakReference = new WeakReference<FragmentActivity>(fragmentActivity);
    }

    @Override
    public Loader<T> onCreateLoader(int id, Bundle args) {
        FragmentActivity fragmentActivity = activityWeakReference.get();
        if (fragmentActivity != null) {
            fragmentActivity.setProgressBarIndeterminateVisibility(true);
        }
        return wrappedCallbacks.onCreateLoader(id, args);
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {
        FragmentActivity fragmentActivity = activityWeakReference.get();
        if (fragmentActivity != null) {
            LoaderManager loaderManager = fragmentActivity.getSupportLoaderManager();
            if (loaderManager != null && !loaderManager.hasRunningLoaders()) {
                fragmentActivity.setProgressBarIndeterminateVisibility(false);
            }
        }
        wrappedCallbacks.onLoadFinished(loader, data);
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        wrappedCallbacks.onLoaderReset(loader);
    }
}
