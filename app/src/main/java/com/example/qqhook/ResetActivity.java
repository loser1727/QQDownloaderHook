package com.example.qqhook;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 重置工具 —— 如果模块导致系统异常，点一下即可恢复
 */
public class ResetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("如果安装模块后出现手柄延迟等问题，\n点击下方按钮重置输入系统。");
        text.setPadding(48, 48, 48, 24);
        text.setTextSize(16);

        Button btn = new Button(this);
        btn.setText("重置输入系统 (需 Root)");
        btn.setPadding(48, 24, 48, 24);
        btn.setOnClickListener(v -> {
            try {
                Process p = Runtime.getRuntime().exec(new String[]{
                    "su", "-c", "kill $(pidof system_server)"
                });
                p.waitFor();
                int exitCode = p.exitValue();
                if (exitCode == 0) {
                    Toast.makeText(this, "已重置，屏幕短暂黑屏后恢复正常", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "重置失败，请确保已授予 Root 权限", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "执行失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            finish();
        });

        setContentView(new android.widget.LinearLayout(this) {{
            setOrientation(VERTICAL);
            setPadding(48, 48, 48, 48);
            addView(text);
            addView(btn);
        }});
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
