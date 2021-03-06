package com.akexorcist.sleepingforless.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.view.View;

import com.akexorcist.sleepingforless.R;
import com.akexorcist.sleepingforless.view.bookmark.BookmarkActivity;
import com.akexorcist.sleepingforless.view.feed.FeedActivity;
import com.akexorcist.sleepingforless.view.feed.FeedAdapter;
import com.akexorcist.sleepingforless.view.post.PostByIdActivity;
import com.akexorcist.sleepingforless.view.search.SearchActivity;
import com.akexorcist.sleepingforless.view.settings.SettingsActivity;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

/**
 * Created by Akexorcist on 4/17/2016 AD.
 */

public class FeedActivityTest extends ActivityInstrumentationTestCase2 {
    private static Class targetActivity = FeedActivity.class;
    private static String ACTIVITY_NAME = targetActivity.getName();

    private Solo solo;
    private static Class<?> launcherActivityClass;

    static {
        try {
            launcherActivityClass = Class.forName(targetActivity.getCanonicalName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public FeedActivityTest() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }

    public void testFabMenuClick() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        View fabMenu = solo.getView(R.id.fab_menu);
        View shadow = solo.getView(R.id.view_content_shadow);
        solo.sleep(500);
        solo.clickOnView(fabMenu);
        solo.sleep(600);
        assertEquals(View.INVISIBLE, fabMenu.getVisibility());
        assertEquals(View.VISIBLE, shadow.getVisibility());
        solo.clickOnView(shadow);
        solo.sleep(600);
        assertEquals(View.VISIBLE, fabMenu.getVisibility());
        assertEquals(View.GONE, shadow.getVisibility());
    }

    public void testAvailableMenu() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        View menuSettings = solo.getView(R.id.btn_menu_settings);
        View menuSort = solo.getView(R.id.btn_menu_sort);
        View menuBookmark = solo.getView(R.id.btn_menu_bookmark);
        View menuSearch = solo.getView(R.id.btn_menu_search);
        View menuRefresh = solo.getView(R.id.btn_menu_refresh);
        assertEquals(View.VISIBLE, menuSettings.getVisibility());
        assertEquals(View.VISIBLE, menuSort.getVisibility());
        assertEquals(View.VISIBLE, menuBookmark.getVisibility());
        assertEquals(View.VISIBLE, menuSearch.getVisibility());
        assertEquals(View.GONE, menuRefresh.getVisibility());
    }

    public void testPostFetch() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        final RecyclerView feedList = (RecyclerView) solo.getView(R.id.rv_feed_list);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return feedList.getAdapter().getItemCount() > 0;
            }
        }, 4000);
        assertEquals(31, feedList.getAdapter().getItemCount());
    }

    public void testPostScrollDownToLoadMore() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        final RecyclerView feedList = (RecyclerView) solo.getView(R.id.rv_feed_list);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return feedList.getAdapter().getItemCount() > 0;
            }
        }, 4000);
        feedList.smoothScrollToPosition(31);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return feedList.getAdapter().getItemCount() > 31;
            }
        }, 4000);
        assertTrue(feedList.getAdapter().getItemCount() > 31);
    }

    @SuppressWarnings("deprecation")
    public void testPostPullToRefresh() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        final RecyclerView feedList = (RecyclerView) solo.getView(R.id.rv_feed_list);
        final SwipeRefreshLayout pullRefresh = (SwipeRefreshLayout) solo.getView(R.id.srl_feed_list);
        final FeedAdapter feedAdapter = (FeedAdapter) feedList.getAdapter();
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return feedAdapter.getItemCount() > 0;
            }
        }, 4000);
        assertEquals(31, feedAdapter.getItemCount());
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        solo.drag(width / 2, width / 2, height / 2, height / 2 + height / 3, 20);
        assertEquals(true, pullRefresh.isRefreshing());
        solo.sleep(500);
        assertEquals(1, feedAdapter.getItemCount());
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return !pullRefresh.isRefreshing();
            }
        }, 4000);
        assertEquals(31, feedAdapter.getItemCount());
    }

    public void testClickOnMenuSettings() {
        String settingActivityName = SettingsActivity.class.getSimpleName();
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        View fabMenu = solo.getView(R.id.fab_menu);
        View menuSettings = solo.getView(R.id.btn_menu_settings);
        solo.clickOnView(fabMenu);
        solo.sleep(500);
        solo.clickOnView(menuSettings);
        solo.sleep(1000);
        assertEquals(settingActivityName, solo.getCurrentActivity().getClass().getSimpleName());
    }

    public void testClickOnMenuBookmark() {
        String bookmarkActivityName = BookmarkActivity.class.getSimpleName();
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        View fabMenu = solo.getView(R.id.fab_menu);
        View menuSettings = solo.getView(R.id.btn_menu_bookmark);
        solo.clickOnView(fabMenu);
        solo.sleep(500);
        solo.clickOnView(menuSettings);
        solo.sleep(1000);
        assertEquals(bookmarkActivityName, solo.getCurrentActivity().getClass().getSimpleName());
    }

    public void testClickOnMenuSearch() {
        String searchActivityName = SearchActivity.class.getSimpleName();
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        View fabMenu = solo.getView(R.id.fab_menu);
        View menuSettings = solo.getView(R.id.btn_menu_search);
        solo.clickOnView(fabMenu);
        solo.sleep(500);
        solo.clickOnView(menuSettings);
        solo.sleep(1000);
        assertEquals(searchActivityName, solo.getCurrentActivity().getClass().getSimpleName());
    }

    public void testClickOnPost() {
        String postActivityName = PostByIdActivity.class.getSimpleName();
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        final RecyclerView feedList = (RecyclerView) solo.getView(R.id.rv_feed_list);

        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return feedList.getAdapter().getItemCount() > 0;
            }
        }, 4000);
        solo.clickInRecyclerView(0);
        solo.sleep(800);
        assertEquals(postActivityName, solo.getCurrentActivity().getClass().getSimpleName());
    }

    public void testClickOnSameSortType() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        View fabMenu = solo.getView(R.id.fab_menu);
        View menuSort = solo.getView(R.id.btn_menu_sort);
        BottomSheetLayout bottomSheetSort = (BottomSheetLayout) solo.getView(R.id.bsl_menu);
        assertEquals(false, bottomSheetSort.isSheetShowing());
        solo.clickOnView(fabMenu);
        solo.sleep(500);
        solo.clickOnView(menuSort);
        solo.sleep(500);
        assertEquals(true, bottomSheetSort.isSheetShowing());
        solo.clickOnMenuItem("Published Date");
        solo.sleep(500);
        assertEquals(false, bottomSheetSort.isSheetShowing());
    }

    public void testClickOnDifferentSortType() {
        solo.waitForActivity(ACTIVITY_NAME, 1000);
        RecyclerView feedList = (RecyclerView) solo.getView(R.id.rv_feed_list);
        View fabMenu = solo.getView(R.id.fab_menu);
        View menuSort = solo.getView(R.id.btn_menu_sort);
        final BottomSheetLayout bottomSheetSort = (BottomSheetLayout) solo.getView(R.id.bsl_menu);
        assertEquals(false, bottomSheetSort.isSheetShowing());
        solo.clickOnView(fabMenu);
        solo.sleep(500);
        solo.clickOnView(menuSort);
        solo.sleep(500);
        assertEquals(true, bottomSheetSort.isSheetShowing());
        solo.clickOnMenuItem("Updated Date");
        solo.sleep(500);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return !bottomSheetSort.isSheetShowing();
            }
        }, 1000);
        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return !bottomSheetSort.isSheetShowing();
            }
        }, 1000);
        solo.waitForView(feedList);
        assertEquals(31, feedList.getAdapter().getItemCount());
    }

}
