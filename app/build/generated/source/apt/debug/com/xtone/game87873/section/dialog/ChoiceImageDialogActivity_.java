//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.2.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.xtone.game87873.section.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.xtone.game87873.R.id;
import com.xtone.game87873.R.layout;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ChoiceImageDialogActivity_
    extends ChoiceImageDialogActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_choice_image_dialog);
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

    public static ChoiceImageDialogActivity_.IntentBuilder_ intent(Context context) {
        return new ChoiceImageDialogActivity_.IntentBuilder_(context);
    }

    public static ChoiceImageDialogActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new ChoiceImageDialogActivity_.IntentBuilder_(fragment);
    }

    public static ChoiceImageDialogActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new ChoiceImageDialogActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        exit_layout = ((RelativeLayout) hasViews.findViewById(id.exit_layout));
        btn_album = ((Button) hasViews.findViewById(id.btn_album));
        btn_cancle = ((Button) hasViews.findViewById(id.btn_cancle));
        btn_photograph = ((Button) hasViews.findViewById(id.btn_photograph));
        if (exit_layout!= null) {
            exit_layout.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ChoiceImageDialogActivity_.this.exitClick();
                }

            }
            );
        }
        if (btn_album!= null) {
            btn_album.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ChoiceImageDialogActivity_.this.albumClick();
                }

            }
            );
        }
        if (btn_photograph!= null) {
            btn_photograph.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ChoiceImageDialogActivity_.this.photographClick();
                }

            }
            );
        }
        if (btn_cancle!= null) {
            btn_cancle.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View view) {
                    ChoiceImageDialogActivity_.this.cancleClick();
                }

            }
            );
        }
        afterView();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<ChoiceImageDialogActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, ChoiceImageDialogActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), ChoiceImageDialogActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), ChoiceImageDialogActivity_.class);
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
