//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.game;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.xtone.game87873.R.id;
import com.xtone.game87873.R.layout;
import com.xtone.game87873.general.widget.xlistview.XListView;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SearchActivity_
    extends SearchActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_search);
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static SearchActivity_.IntentBuilder_ intent(Context context) {
        return new SearchActivity_.IntentBuilder_(context);
    }

    public static SearchActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new SearchActivity_.IntentBuilder_(fragment);
    }

    public static SearchActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new SearchActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (((SdkVersionHelper.getSdkInt()< 5)&&(keyCode == KeyEvent.KEYCODE_BACK))&&(event.getRepeatCount() == 0)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        v_translucent = hasViews.findViewById(id.v_translucent);
        edt_gameName = ((EditText) hasViews.findViewById(id.edt_gameName));
        ll_searchNone = ((LinearLayout) hasViews.findViewById(id.ll_searchNone));
        ll_keyWord = ((LinearLayout) hasViews.findViewById(id.ll_keyWord));
        ll_root = ((LinearLayout) hasViews.findViewById(id.ll_root));
        btn_change = ((Button) hasViews.findViewById(id.btn_change));
        lv_gameList = ((XListView) hasViews.findViewById(id.lv_gameList));
        ll_contain = ((LinearLayout) hasViews.findViewById(id.ll_contain));
        {
            View view = hasViews.findViewById(id.iv_headLeft);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SearchActivity_.this.backClick();
                    }

                }
                );
            }
        }
        if (btn_change!= null) {
            btn_change.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SearchActivity_.this.change();
                }

            }
            );
        }
        {
            View view = hasViews.findViewById(id.iv_search);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SearchActivity_.this.searchClick();
                    }

                }
                );
            }
        }
        {
            View view = hasViews.findViewById(id.iv_scan);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SearchActivity_.this.scanClick();
                    }

                }
                );
            }
        }
        if (v_translucent!= null) {
            v_translucent.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SearchActivity_.this.traslucentClick();
                }

            }
            );
        }
        {
            View view = hasViews.findViewById(id.btn_back);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SearchActivity_.this.backHome();
                    }

                }
                );
            }
        }
        afterViews();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<SearchActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, SearchActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), SearchActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), SearchActivity_.class);
            fragmentSupport_ = fragment;
        }

        @Override
        public void startForResult(int requestCode) {
            if (fragmentSupport_!= null) {
                fragmentSupport_.startActivityForResult(intent, requestCode);
            } else {
                if (fragment_!= null) {
                    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                        fragment_.startActivityForResult(intent, requestCode, lastOptions);
                    } else {
                        fragment_.startActivityForResult(intent, requestCode);
                    }
                } else {
                    if (context instanceof Activity) {
                        Activity activity = ((Activity) context);
                        ActivityCompat.startActivityForResult(activity, intent, requestCode, lastOptions);
                    } else {
                        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                            context.startActivity(intent, lastOptions);
                        } else {
                            context.startActivity(intent);
                        }
                    }
                }
            }
        }

    }

}
