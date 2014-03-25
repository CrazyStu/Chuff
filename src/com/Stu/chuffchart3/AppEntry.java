package com.Stu.chuffchart3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ParcelFileDescriptor.OnCloseListener;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SearchViewCompatIcs.MySearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;


/**
 * This class holds the per-item data in our Loader.
 */
public class AppEntry {
    public AppEntry(AppListLoader loader, ApplicationInfo info) {
        mLoader = loader;
        mInfo = info;
        mApkFile = new File(info.sourceDir);
    }

    public ApplicationInfo getApplicationInfo() {
        return mInfo;
    }

    public String getLabel() {
        return mLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mInfo.loadIcon(mLoader.mPm);
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mLoader.mPm);
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mLoader.getContext().getResources().getDrawable(
                android.R.drawable.sym_def_app_icon);
    }

    @Override public String toString() {
        return mLabel;
    }

    void loadLabel(Context context) {
        if (mLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mLabel = mInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mLabel = label != null ? label.toString() : mInfo.packageName;
            }
        }
    }

    private final AppListLoader mLoader;
    private final ApplicationInfo mInfo;
    private final File mApkFile;
    private String mLabel;
    private Drawable mIcon;
    private boolean mMounted;
}

/**
 * Perform alphabetical comparison of application entry objects.
 */
public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
    private final Collator sCollator = Collator.getInstance();
    @Override
    public int compare(AppEntry object1, AppEntry object2) {
        return sCollator.compare(object1.getLabel(), object2.getLabel());
    }
};

/**
 * Helper for determining if the configuration has changed in an interesting
 * way so we need to rebuild the app list.
 */
public static class InterestingConfigChanges {
    final Configuration mLastConfiguration = new Configuration();
    int mLastDensity;

    boolean applyNewConfig(Resources res) {
        int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
        boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
        if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
                |ActivityInfo.CONFIG_UI_MODE|ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
            mLastDensity = res.getDisplayMetrics().densityDpi;
            return true;
        }
        return false;
    }
}

/**
 * Helper class to look for interesting changes to the installed apps
 * so that the loader can be updated.
 */
public static class PackageIntentReceiver extends BroadcastReceiver {
    final AppListLoader mLoader;

    public PackageIntentReceiver(AppListLoader loader) {
        mLoader = loader;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mLoader.getContext().registerReceiver(this, filter);
        // Register for events related to sdcard installation.
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        mLoader.getContext().registerReceiver(this, sdFilter);
    }

    @Override public void onReceive(Context context, Intent intent) {
        // Tell the loader about the change.
        mLoader.onContentChanged();
    }
}

/**
 * A custom Loader that loads all of the installed applications.
 */
public static class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {
    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    final PackageManager mPm;

    List<AppEntry> mApps;
    PackageIntentReceiver mPackageObserver;

    public AppListLoader(Context context) {
        super(context);

        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        mPm = getContext().getPackageManager();
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override public List<AppEntry> loadInBackground() {
        // Retrieve all known applications.
        List<ApplicationInfo> apps = mPm.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES |
                PackageManager.GET_DISABLED_COMPONENTS);
        if (apps == null) {
            apps = new ArrayList<ApplicationInfo>();
        }

        final Context context = getContext();

        // Create corresponding array of entries and load their labels.
        List<AppEntry> entries = new ArrayList<AppEntry>(apps.size());
        for (int i=0; i<apps.size(); i++) {
            AppEntry entry = new AppEntry(this, apps.get(i));
            entry.loadLabel(context);
            entries.add(entry);
        }

        // Sort the list.
        Collections.sort(entries, ALPHA_COMPARATOR);

        // Done!
        return entries;
    }
    @Override public void deliverResult(List<AppEntry> apps) {
        List<AppEntry> oldApps = mApps;
        mApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mApps != null) {
            deliverResult(mApps);
        }
        // Start watching for changes in the app data.
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }
        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());
        if (takeContentChanged() || mApps == null || configChange) {
            forceLoad();
        }
    }


    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<AppEntry> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
