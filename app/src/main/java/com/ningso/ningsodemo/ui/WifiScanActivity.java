package com.ningso.ningsodemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.ningso.ningsodemo.R;
import com.ningso.ningsodemo.adapter.WiFiAdapter;
import com.ningso.ningsodemo.models.WiFiBean;
import com.ningso.ningsodemo.utils.ILog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NingSo on 15/12/3.下午2:34
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class WifiScanActivity extends AppCompatActivity {
    private List<WiFiBean> mlist = new ArrayList<>();
    private String fileName = "/data/misc/wifi/wpa_supplicant.conf";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private WiFiAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifiscan);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_wifi);
        // 申请root
        boolean root = upgradeRootPermission(fileName);
        // 读取文件摘取信息
        if (root) {
            readWIFIFile(fileName);
        } else {
            Toast.makeText(this, "对不起，手机没有root权限！", Toast.LENGTH_LONG).show();
            return;
        }
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new WiFiAdapter(mlist);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 读取wifi文件并摘取信息
     *
     * @param fileName
     */
    private void readWIFIFile(String fileName) {
        try {
            String line;
            boolean flag = false;
            File file = new File(fileName);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    if (line.equals("")) {
                        continue;
                    }
                    line = line.trim();
                    ILog.e(line);
                    if (line.equals("network={")) {
                        flag = true;
                        continue;
                    }
                    WiFiBean wifiBean = null;
                    if (flag) {
                        wifiBean = new WiFiBean();
                        if (line.startsWith("ssid=\"")) {
                            wifiBean.setSsid(line.substring(line.indexOf("\"") + 1, line.length() - 1).trim());
                            continue;
                        }
                        if (line.startsWith("psk=\"")) {
                            wifiBean.setKey(line.substring(line.indexOf("\"") + 1,
                                    line.length() - 1).trim());
                            continue;
                        }
                    }
                    if (line.equals("}")) {
                        flag = false;
                        mlist.add(wifiBean);
                    }
                }
                try {
                    br.close();
                } catch (Exception ignored) {

                }
            } else {
                Toast.makeText(this, "对不起，wpa_supplicant.conf不存在！",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 要让Android应用获得Root权限，首先Android设备必须已经获得Root权限。
     * 应用获取Root权限的原理：让应用的代码执行目录获取最高权限。在Linux中通过chmod 777 [代码执行目录] 应用程序运行命令获取
     * Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + "/data/misc/wifi " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); // 切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            System.out.println("我执行了");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
