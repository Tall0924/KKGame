//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.info;

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
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class SettingActivity_
    extends SettingActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_setting);
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

    public static SettingActivity_.IntentBuilder_ intent(Context context) {
        return new SettingActivity_.IntentBuilder_(context);
    }

    public static SettingActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new SettingActivity_.IntentBuilder_(fragment);
    }

    public static SettingActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new SettingActivity_.IntentBuilder_(supportFragment);
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
        btn_logout = ((Button) hasViews.findViewById(id.btn_logout));
        tvDownloadAddress = ((TextView) hasViews.findViewById(id.tvDownloadAddress));
        iv_headLeft = ((ImageView) hasViews.findViewById(id.iv_headLeft));
        tv_version = ((TextView) hasViews.findViewById(id.tv_version));
        ll_suggestion_feedback = ((LinearLayout) hasViews.findViewById(id.ll_suggestion_feedback));
        tv_headTitle = ((TextView) hasViews.findViewById(id.tv_headTitle));
        ivIsNoteOn = ((ImageView) hasViews.findViewById(id.ivIsNoteOn));
        iv_onlyWifiDownload = ((ImageView) hasViews.findViewById(id.iv_onlyWifiDownload));
        iv_autoDelete = ((ImageView) hasViews.findViewById(id.iv_autoDelete));
        ll_check_for_updates = ((LinearLayout) hasViews.findViewById(id.ll_check_for_updates));
        ll_recommed_to_friends = ((LinearLayout) hasViews.findViewById(id.ll_recommed_to_friends));
        ll_aboutUs = ((LinearLayout) hasViews.findViewById(id.ll_aboutUs));
        if (ll_check_for_updates!= null) {
            ll_check_for_updates.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.checkForUpdateClick();
                }

            }
            );
        }
        if (iv_headLeft!= null) {
            iv_headLeft.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.backClick();
                }

            }
            );
        }
        if (ll_recommed_to_friends!= null) {
            ll_recommed_to_friends.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.shareClick();
                }

            }
            );
        }
        if (iv_onlyWifiDownload!= null) {
            iv_onlyWifiDownload.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.setIsDownloadOnlyWifi();
                }

            }
            );
        }
        if (ll_suggestion_feedback!= null) {
            ll_suggestion_feedback.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.toFeedbackClick();
                }

            }
            );
        }
        if (btn_logout!= null) {
            btn_logout.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.logoutClick();
                }

            }
            );
        }
        if (ll_aboutUs!= null) {
            ll_aboutUs.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.aboutUsClick();
                }

            }
            );
        }
        if (iv_autoDelete!= null) {
            iv_autoDelete.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.setIsAutoDelete();
                }

            }
            );
        }
        if (ivIsNoteOn!= null) {
            ivIsNoteOn.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    SettingActivity_.this.setReceiveNote();
                }

            }
            );
        }
        afterViews();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<SettingActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, SettingActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), SettingActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), SettingActivity_.class);
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