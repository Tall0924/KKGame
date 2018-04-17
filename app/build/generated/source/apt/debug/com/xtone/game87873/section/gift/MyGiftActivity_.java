//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.gift;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xtone.game87873.R.id;
import com.xtone.game87873.R.layout;
import com.xtone.game87873.general.widget.xlistview.XListView;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class MyGiftActivity_
    extends MyGiftActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_my_gift);
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

    public static MyGiftActivity_.IntentBuilder_ intent(Context context) {
        return new MyGiftActivity_.IntentBuilder_(context);
    }

    public static MyGiftActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new MyGiftActivity_.IntentBuilder_(fragment);
    }

    public static MyGiftActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new MyGiftActivity_.IntentBuilder_(supportFragment);
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
        iv_all = ((ImageView) hasViews.findViewById(id.iv_all));
        tv_headTitle = ((TextView) hasViews.findViewById(id.tv_headTitle));
        btn_refresh = ((Button) hasViews.findViewById(id.btn_refresh));
        btn_delete = ((Button) hasViews.findViewById(id.btn_delete));
        tv_Msg2 = ((TextView) hasViews.findViewById(id.tv_Msg2));
        lv_myGift = ((XListView) hasViews.findViewById(id.lv_myGift));
        ivMsg = ((ImageView) hasViews.findViewById(id.ivMsg));
        il_loadFailure = ((LinearLayout) hasViews.findViewById(id.il_loadFailure));
        iv_headLeft = ((ImageView) hasViews.findViewById(id.iv_headLeft));
        iv_headRight = ((ImageView) hasViews.findViewById(id.iv_headRight));
        tvMsg = ((TextView) hasViews.findViewById(id.tvMsg));
        ll_content = ((LinearLayout) hasViews.findViewById(id.ll_content));
        ll_operate = ((LinearLayout) hasViews.findViewById(id.ll_operate));
        if (btn_delete!= null) {
            btn_delete.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    MyGiftActivity_.this.deleteClick();
                }

            }
            );
        }
        if (iv_all!= null) {
            iv_all.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    MyGiftActivity_.this.allClick();
                }

            }
            );
        }
        if (iv_headRight!= null) {
            iv_headRight.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    MyGiftActivity_.this.editOrFinishClik();
                }

            }
            );
        }
        if (btn_refresh!= null) {
            btn_refresh.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    MyGiftActivity_.this.refreshClick();
                }

            }
            );
        }
        if (iv_headLeft!= null) {
            iv_headLeft.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    MyGiftActivity_.this.backClick();
                }

            }
            );
        }
        afterViews();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<MyGiftActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, MyGiftActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), MyGiftActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), MyGiftActivity_.class);
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
