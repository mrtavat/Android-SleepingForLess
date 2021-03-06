package com.akexorcist.sleepingforless.view.offline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.akexorcist.sleepingforless.R;
import com.akexorcist.sleepingforless.analytic.EventKey;
import com.akexorcist.sleepingforless.analytic.EventTracking;
import com.akexorcist.sleepingforless.bus.BusProvider;
import com.akexorcist.sleepingforless.common.SFLActivity;
import com.akexorcist.sleepingforless.constant.Key;
import com.akexorcist.sleepingforless.database.BookmarkManager;
import com.akexorcist.sleepingforless.util.AnimationUtility;
import com.akexorcist.sleepingforless.util.ExternalBrowserUtility;
import com.akexorcist.sleepingforless.util.Utility;
import com.akexorcist.sleepingforless.util.content.ContentConverter;
import com.akexorcist.sleepingforless.util.content.ContentResult;
import com.akexorcist.sleepingforless.util.content.ContentUtility;
import com.akexorcist.sleepingforless.view.bookmark.model.Bookmark;
import com.akexorcist.sleepingforless.view.bookmark.model.BookmarkRemoveEvent;
import com.akexorcist.sleepingforless.view.post.model.BasePost;
import com.akexorcist.sleepingforless.widget.MenuButton;
import com.bowyer.app.fabtransitionlayout.FooterLayout;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Duration;
import com.squareup.otto.Subscribe;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class OfflinePostActivity extends SFLActivity implements OfflinePostAdapter.PostClickListener {
    private static final String KEY_BOOKMARK = "key_bookmark";
    private static final String KEY_POST_LIST = "key_post_list";

    @Bind(R.id.tb_title)
    Toolbar tbTitle;

    @Bind(R.id.fab_menu)
    FloatingActionButton fabMenu;

    @Bind(R.id.fl_menu)
    FooterLayout flMenu;

    @Bind(R.id.view_content_shadow)
    View viewContentShadow;

    @Bind(R.id.pb_offline_post_loading)
    DilatingDotsProgressBar pbPostLoading;

    @Bind(R.id.btn_menu_update)
    MenuButton btnMenuUpdate;

    @Bind(R.id.btn_menu_delete)
    MenuButton btnMenuDelete;

    @Bind(R.id.btn_menu_open_from_original)
    MenuButton btnMenuOpenFromOriginal;

    @Bind(R.id.bsl_menu)
    BottomSheetLayout bslMenu;

    @Bind(R.id.rv_offline_post_list)
    RecyclerView rvPostList;

    OfflinePostAdapter adapter;

    Bookmark bookmark;
    List<BasePost> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_post);

        if (savedInstanceState == null) {
            restoreIntentData();
        }

        ButterKnife.bind(this);
        setupView();
        setToolbar();

        if (savedInstanceState == null) {
            screenTracking();
            readContentTracking();
            setBookmark();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ExternalBrowserUtility.getInstance().bindService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ExternalBrowserUtility.getInstance().unbindService(this);
    }

    private void setupView() {
        viewContentShadow.setVisibility(View.GONE);
        btnMenuUpdate.setVisibility(View.GONE);
        flMenu.setFab(fabMenu);
        rvPostList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void setToolbar() {
        setSupportActionBar(tbTitle);
        tbTitle.setNavigationIcon(R.drawable.vector_ic_back);
        tbTitle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setBookmark() {
        showLoading();
        setTitle(bookmark);
        setPost(bookmark);
    }

    private void setTitle(Bookmark bookmark) {
        String title = getString(R.string.title_offline, ContentUtility.getInstance().removeLabelFromTitle(bookmark.getTitle()));
        setTitle(highlightText(title, "[Offline]"));
    }

    private void restoreIntentData() {
        bookmark = Parcels.unwrap(getIntent().getParcelableExtra(Key.BOOKMARK));
    }

    @Override
    public void onBackPressed() {
        if (bslMenu.isSheetShowing()) {
            bslMenu.dismissSheet();
        } else if (flMenu.isFabExpanded()) {
            closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onImageClickListener(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Key.IMAGE_PATH, url);
        bundle.putString(Key.POST_ID, bookmark.getPostId());
        openActivity(OfflineImagePostPreviewActivity.class, bundle);
    }

    @Override
    public void onImageLongClickListener(String url) {
        Utility.getInstance().copyTextToClipboard("Image URL", url);
        Snackbar.make(tbTitle, R.string.copy_image_url_to_clipboard, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLinkClickListener(String url) {
        ExternalBrowserUtility.getInstance().open(this, url);
    }

    @Override
    public void onVideoClickListener(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Subscribe
    public void onContentConvertResult(ContentResult result) {
        postList = result.getBasePostList();
        setPostList(postList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_BOOKMARK, Parcels.wrap(bookmark));
        outState.putParcelable(KEY_POST_LIST, Parcels.wrap(postList));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bookmark = Parcels.unwrap(savedInstanceState.getParcelable(KEY_BOOKMARK));
        postList = Parcels.unwrap(savedInstanceState.getParcelable(KEY_POST_LIST));
        setTitle(bookmark);
        setPostList(postList);
    }

    @OnClick(R.id.btn_menu_update)
    public void onMenuUpdateClick() {
        // TODO update offline bookmark
    }

    @OnClick(R.id.btn_menu_delete)
    public void onMenuDeleteClick() {
        new MaterialStyledDialog(this)
                .setCancelable(true)
                .setTitle(getString(R.string.remove_confirm_title))
                .setDescription(ContentUtility.getInstance().removeLabelFromTitle(bookmark.getTitle()))
                .withDialogAnimation(true, Duration.FAST)
                .withIconAnimation(true)
                .setHeaderColor(R.color.colorAccent)
                .setIcon(R.mipmap.ic_warning)
                .setPositive(getString(R.string.remove_confirm_yes), new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeBookmark();
                    }
                })
                .setNegative(getString(R.string.remove_confirm_no), new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .build()
                .show();
        closeMenu();
    }

    @OnClick(R.id.btn_menu_open_from_original)
    public void onMenuOpenInOnlineClick() {
        ExternalBrowserUtility.getInstance().open(this, bookmark.getUrl());
        closeMenu();
    }

    @OnLongClick(R.id.fab_menu)
    public boolean scrollContentToTop() {
        rvPostList.smoothScrollToPosition(0);
        return true;
    }

    public void removeBookmark() {
        removeBookmarkTracking();
        BookmarkManager.getInstance().removeBookmark(bookmark.getPostId());
        finish();
        notifyBookmarkRemoved();
    }

    public void notifyBookmarkRemoved() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post(new BookmarkRemoveEvent(bookmark.getPostId()));
            }
        }, 500);
    }

    private void setPost(Bookmark bookmark) {
        if (bookmark != null) {
            ContentConverter.getInstance().convert(bookmark.getContent());
        }
    }

    private void setPostList(List<BasePost> postList) {
        adapter = new OfflinePostAdapter(bookmark.getPostId(), postList);
        adapter.setPostClickListener(this);
        rvPostList.setAdapter(adapter);
        hideLoading();
    }

    @OnClick(R.id.fab_menu)
    public void openMenu() {
        flMenu.expandFab();
        AnimationUtility.getInstance().fadeIn(viewContentShadow, 200);
    }

    @OnClick(R.id.view_content_shadow)
    public void closeMenu() {
        flMenu.contractFab();
        AnimationUtility.getInstance().fadeOut(viewContentShadow, 200);
    }

    public void showSnackbar(String message) {
        Snackbar.make(tbTitle, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoading() {
        pbPostLoading.showNow();
        rvPostList.setVisibility(View.GONE);
    }

    private void hideLoading() {
        pbPostLoading.hideNow();
        rvPostList.setVisibility(View.VISIBLE);
    }

    private Spannable highlightText(String message, String highlight) {
        int start = message.indexOf(highlight);
        int end = start + highlight.length();
        Spannable spannableMessage = new SpannableString(message);
        spannableMessage.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.text_offline)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableMessage;
    }

    // Google Analytics
    private void screenTracking() {
        EventTracking.getInstance().addScreen(EventKey.Page.READ_BOOKMARK);
    }

    private void removeBookmarkTracking() {
        EventTracking.getInstance().addContentTracking(EventKey.Action.REMOVE_BOOKMARK, getPostTitle());
    }

    private void readContentTracking() {
        EventTracking.getInstance().addContentTracking(EventKey.Action.READ, getPostTitle());
    }

    private String getPostTitle() {
        if (bookmark != null) {
            return ContentUtility.getInstance().removeLabelFromTitle(bookmark.getTitle());
        }
        return null;
    }
}
