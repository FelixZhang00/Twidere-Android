/*
 * Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.activity.support;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import org.mariotaku.twidere.BuildConfig;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.activity.iface.IThemedActivity;
import org.mariotaku.twidere.app.TwidereApplication;
import org.mariotaku.twidere.util.KeyboardShortcutsHandler;
import org.mariotaku.twidere.util.KeyboardShortcutsHandler.KeyboardShortcutCallback;
import org.mariotaku.twidere.util.StrictModeUtils;
import org.mariotaku.twidere.util.ThemeUtils;
import org.mariotaku.twidere.util.ThemedLayoutInflaterFactory;
import org.mariotaku.twidere.util.Utils;
import org.mariotaku.twidere.view.ShapedImageView.ShapeStyle;

public abstract class ThemedFragmentActivity extends FragmentActivity implements Constants,
        IThemedActivity, KeyboardShortcutCallback {

    // Utility classes
    private KeyboardShortcutsHandler mKeyboardShortcutsHandler;

    // Data fields
    private int mCurrentThemeResource, mCurrentThemeColor, mCurrentThemeBackgroundAlpha;
    @ShapeStyle
    private int mProfileImageStyle;
    private String mCurrentThemeBackgroundOption;
    private String mCurrentThemeFontFamily;

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        final LayoutInflater inflater = super.getLayoutInflater();
        if (inflater.getFactory() == null) {
            LayoutInflaterCompat.setFactory(inflater, new ThemedLayoutInflaterFactory(this, new LayoutInflaterFactory() {
                @Override
                public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                    return ThemedFragmentActivity.this.onCreateView(parent, name, context, attrs);
                }
            }));
        }
        return inflater;
    }

    @Override
    public String getCurrentThemeFontFamily() {
        return mCurrentThemeFontFamily;
    }

    @Override
    public int getCurrentThemeBackgroundAlpha() {
        return mCurrentThemeBackgroundAlpha;
    }

    @Override
    public String getCurrentThemeBackgroundOption() {
        return mCurrentThemeBackgroundOption;
    }

    @Override
    public int getCurrentThemeColor() {
        return mCurrentThemeColor;
    }

    @Override
    public final int getCurrentThemeResourceId() {
        return mCurrentThemeResource;
    }

    @Override
    public int getThemeBackgroundAlpha() {
        return ThemeUtils.getUserThemeBackgroundAlpha(this);
    }

    @Override
    public String getThemeBackgroundOption() {
        return ThemeUtils.getThemeBackgroundOption(this);
    }

    @Override
    public String getThemeFontFamily() {
        return ThemeUtils.getThemeFontFamily(this);
    }

    @Override
    @ShapeStyle
    public int getCurrentProfileImageStyle() {
        return mProfileImageStyle;
    }

    @Override
    public final void restart() {
        Utils.restartActivity(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictModeUtils.detectAllVmPolicy();
            StrictModeUtils.detectAllThreadPolicy();
        }
        super.onCreate(savedInstanceState);
        mKeyboardShortcutsHandler = TwidereApplication.getInstance(this).getKeyboardShortcutsHandler();
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(mCurrentThemeResource = getThemeResourceId());
        if (shouldApplyWindowBackground()) {
            ThemeUtils.applyWindowBackground(this, getWindow(), mCurrentThemeResource,
                    mCurrentThemeBackgroundOption, mCurrentThemeBackgroundAlpha);
        }
    }

    @Override
    protected void onApplyThemeResource(@NonNull Resources.Theme theme, int resId, boolean first) {
        mCurrentThemeColor = getThemeColor();
        mCurrentThemeFontFamily = getThemeFontFamily();
        mCurrentThemeBackgroundAlpha = getThemeBackgroundAlpha();
        mCurrentThemeBackgroundOption = getThemeBackgroundOption();
        mProfileImageStyle = Utils.getProfileImageStyle(this);
        super.onApplyThemeResource(theme, resId, first);
    }

    @Override
    public boolean handleKeyboardShortcutSingle(@NonNull KeyboardShortcutsHandler handler, int keyCode, @NonNull KeyEvent event) {
        return false;
    }

    @Override
    public boolean handleKeyboardShortcutRepeat(@NonNull KeyboardShortcutsHandler handler, int keyCode, int repeatCount, @NonNull KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (handleKeyboardShortcutSingle(mKeyboardShortcutsHandler, keyCode, event)) return true;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (handleKeyboardShortcutRepeat(mKeyboardShortcutsHandler, keyCode, event.getRepeatCount(), event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int getThemeResourceId() {
        return 0;
    }

    protected boolean shouldApplyWindowBackground() {
        return true;
    }
}
