package com.fzuclover.putmedown.businessmodule.totaltimingtoday;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.fzuclover.putmedown.BaseActivity;
import com.fzuclover.putmedown.R;
import com.fzuclover.putmedown.businessmodule.achievement.AchievementActivity;
import com.fzuclover.putmedown.businessmodule.login.LoginActivity;
import com.fzuclover.putmedown.businessmodule.setting.SettingActivity;
import com.fzuclover.putmedown.businessmodule.timing.TimingActivity;
import com.fzuclover.putmedown.businessmodule.timingrecord.TimingRecordActivity;
import com.fzuclover.putmedown.model.RecordModel;
import com.fzuclover.putmedown.model.UserModel;
import com.fzuclover.putmedown.util.LogUtil;
import com.fzuclover.putmedown.view.NumPickerView;
import com.fzuclover.putmedown.view.WaveProgressView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TotalTimingTodayActivity extends BaseActivity implements TotalTimingTodayContract.View ,
        View.OnClickListener{

    private TotalTimingTodayContract.Presenter mPresenter;

    private Button mStartBtn;
    private TextView mSetBtn;
    private TextView mLogoutBtn;
    private LinearLayout mAchievementLayout;
    private LinearLayout mHistoryLayout;
    private WaveProgressView mWaveProgressView;
    private DrawerLayout mDrawerLayout;
    private ImageView mCurrentPlaceImg;
    //今日已经计时的时间(分钟)
    private int mTimedToday;
    //目标时间(分钟)
    private int mTargetTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_timing_today);
        init();
    }

    private void init(){
        mPresenter = new TotalTimingTodayPresenter(this, RecordModel.getRecordModelInstance(),
                UserModel.getUserModelInstance());

        mStartBtn = (Button) findViewById(R.id.start_timing_btn);
        mStartBtn.setOnClickListener(this);
        mSetBtn = (TextView) findViewById(R.id.set_btn);
        mSetBtn.setOnClickListener(this);
        mLogoutBtn = (TextView) findViewById(R.id.logout_btn);
        mLogoutBtn.setOnClickListener(this);
        mAchievementLayout = (LinearLayout) findViewById(R.id.achievement_layout);
        mAchievementLayout.setOnClickListener(this);
        mHistoryLayout = (LinearLayout) findViewById(R.id.history_layout);
        mHistoryLayout.setOnClickListener(this);

        mCurrentPlaceImg = (ImageView) findViewById(R.id.current_place_img);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mWaveProgressView = (WaveProgressView) findViewById(R.id.wave_progress_view);
        mWaveProgressView.setOnClickListener(this);
        //TODO 本日已经计时时间从sharepreferences获取
        mTimedToday = 0;
        //todo 每日目标时间sharepreferences获取
        mTargetTime = 3 * 60;
        //设置进度球最大进度为目标时间
        mWaveProgressView.setMaxProgress(mTargetTime);
        mWaveProgressView.setCurrent(mTimedToday, mTimedToday + "min/" + mTargetTime + "min");
        mWaveProgressView.setWaveColor("#5b9ef4");
        //设置波浪高度宽度
        mWaveProgressView.setWave(10,30);
        //设置波动速读
        mWaveProgressView.setmWaveSpeed(10);
        //设置字体颜色大小
        mWaveProgressView.setText("#f0f0f0", 80);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_timing_btn:
                showEditTimingCommentsDialog();
                break;
            case R.id.set_btn:
                toSettingActivity();
                break;
            case R.id.logout_btn:
                toLoginActivity();
                this.finish();
                break;
            case R.id.wave_progress_view:
                showSetTargetTimeDialog();
                break;
            case R.id.achievement_layout:
                toAchievementActivity();
                break;
            case R.id.history_layout:
                toHistoryActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void toSettingActivity() {
        Intent intent = new Intent(TotalTimingTodayActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public void toLoginActivity() {
        Intent intent = new Intent(TotalTimingTodayActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void toTimingActivity(int mimute) {
        Intent intent = new Intent(TotalTimingTodayActivity.this, TimingActivity.class);
        intent.putExtra("minute", mimute);
        startActivity(intent);
    }

    @Override
    public void toAchievementActivity() {
        Intent intent = new Intent(this, AchievementActivity.class);
        startActivity(intent);
    }

    @Override
    public void toHistoryActivity() {
        Intent intent = new Intent(this, TimingRecordActivity.class);
        startActivity(intent);
    }

    @Override
    public void showSetTargetTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置每日目标时间");
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.dialog_set_target_time, null);
        builder.setView(view);
        final EditText targetTimeEdt = (EditText) view.findViewById(R.id.set_target_time_edt);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Pattern pattern = Pattern.compile("[0-9]*");
                String targetTimeStr = targetTimeEdt.getText().toString();
                if(!TextUtils.isEmpty(targetTimeStr)){
                    Matcher isNum = pattern.matcher(targetTimeStr);
                    if(isNum.matches()){
                        int temp;
                        temp = Integer.valueOf(targetTimeStr);
                        if(temp <= 1440){
                            mTargetTime = temp;
                            //todo 将mTargetTime持久化
                            mWaveProgressView.setMaxProgress(mTargetTime);
                            mWaveProgressView.setCurrent(mTimedToday, mTimedToday + "min/" + mTargetTime + "min");
                        }else{
                            toastShort("目标时间超过24小时啦>.<");
                        }
                    }else{
                        toastShort("请输入数字");
                    }
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
        targetTimeEdt.setText(String.valueOf(mTargetTime));
    }

    @Override
    public void showEditTimingCommentsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        View view = getLayoutInflater().inflate(R.layout.dialog_set_timing_comments, null);
        //设置时间选择
        final NumPickerView minutePicker = (NumPickerView) view.findViewById(R.id.minute_picker);
        final String[] minuteStrs = {"15", "30", "45", "60"};
        minutePicker.setDisplayedValues(minuteStrs);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(3);

        builder.setView(view);
        EditText commentEdt = (EditText) findViewById(R.id.comment_edt);
        builder.setPositiveButton("开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //todo 保存备注到数据库
                int index = minutePicker.getValue();
                int minute = Integer.valueOf(minuteStrs[index]);
                toTimingActivity(minute);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }



}
