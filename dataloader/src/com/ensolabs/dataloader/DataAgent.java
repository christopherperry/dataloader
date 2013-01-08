package com.ensolabs.dataloader;

/**
 * A DataAgent is a class that simply provides a means to get
 * some data, nothing more.
 * <p/>
 * Whether you're making a web request, grabbing data from
 * the file system, a database, or anything else that floats
 * your boat, you would use a DataAgent in conjunction with
 * an {@link DataLoader DataLoader} to do your dirty work.
 *
 * @author christopherperry
 */
public interface DataAgent<T> {

    /**
     * Get some data baby, get some!
     *
     * @return The data to be gotten
     */
    T getData();
}
