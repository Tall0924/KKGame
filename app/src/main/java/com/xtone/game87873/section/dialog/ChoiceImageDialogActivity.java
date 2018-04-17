package com.xtone.game87873.section.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xtone.game87873.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_choice_image_dialog)
public class ChoiceImageDialogActivity extends Activity {

    @ViewById
    RelativeLayout exit_layout;
    @ViewById
    Button btn_album, btn_photograph, btn_cancle;
    public static final int CHOICE_ABLUM = 1;
    public static final int CHOICE_CAMERA = 2;

    @AfterViews
    void afterView() {
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
    }

    @Click(R.id.exit_layout)
    void exitClick() {
        finish();
    }

    @Click(R.id.btn_album)
    void albumClick() {
        Intent intent = new Intent();
        intent.putExtra("choice", CHOICE_ABLUM);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Click(R.id.btn_photograph)
    void photographClick() {
        Intent intent = new Intent();
        intent.putExtra("choice", CHOICE_CAMERA);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Click(R.id.btn_cancle)
    void cancleClick() {
        finish();
    }

}
