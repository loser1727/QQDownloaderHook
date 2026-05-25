package com.example.qqhook;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 重置工具 —— LSPosed Manager 中点模块名打开，一键恢复输入系统
 */
public class ResetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 48, 48, 48);
        layout.setGravity(Gravity.CENTER);

        TextView text = new TextView(this);
        text.setText("如果安装模块后出现手柄延迟等异常，\n点击下方按钮重置输入系统。\n\n屏幕会短暂黑屏，几秒后自动恢复。");
        text.setTextSize(16);
        text.setPadding(0, 0, 0, 32);
        layout.addView(text);

        Button btn = new Button(this);
        btn.setText("重置输入系统 (需 Root)");
        btn.setOnClickListener(v -> {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{
                    "su", "-c", "kill $(pidof system_server)"
                });
                p.waitFor();
                if (p.exitValue() == 0) {
                    Toast.makeText(this, "已重置", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "失败，请确保已授予 Root 权限", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "执行失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finish();
        });
        layout.addView(btn);

        setContentView(layout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
