package com.ningso.ningsodemo.utils;

import android.content.Context;
import android.os.Environment;

import com.ningso.ningsodemo.App;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import dalvik.system.DexClassLoader;

/**
 * Created by NingSo on 16/2/23.下午3:58
 *
 * @author: NingSo
 * @Email: ningdev@163.com
 */
public class DexClassLoadProxy {

    private static DexClassLoadProxy mInstance;
    private DexClassLoader mDexClassLoader = null;

    private DexClassLoadProxy() {
    }

    public static DexClassLoadProxy getInstance() {
        if (mInstance == null) {
            synchronized (DexClassLoadProxy.class) {
                if (mInstance == null) {
                    mInstance = new DexClassLoadProxy();
                }
            }
        }
        return mInstance;
    }

    private DexClassLoader getDexClassLoader() {
        if (mDexClassLoader == null) {
            File optimizedDexOutputPath = new File(Environment.getExternalStorageDirectory().toString()
                    + File.separator + "classes.dex");
            File dexOutputDir = App.getInstance().getDir("dex", Context.MODE_PRIVATE);
            mDexClassLoader = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
                    dexOutputDir.getAbsolutePath(), null, App.getInstance().getClassLoader());
        }
        return mDexClassLoader;
    }

    /**
     * @param classStr   类名
     * @param methodStr  方法名
     * @param staticMeth 是否是静态方法
     * @param args       参数数组
     * @return
     */
    public Object executeClass(String classStr, String methodStr, boolean staticMeth, Object... args) {
        Class iclass;
        Object instance;
        Object result = null;
        try {
            iclass = getDexClassLoader().loadClass(classStr);
            instance = iclass.newInstance();
            if (args.length != 0) {
                Class[] params = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Context) {
                        params[i] = Context.class;
                    } else if (args[i] instanceof Boolean) {
                        params[i] = Boolean.TYPE;
                    } else if (args[i] instanceof Integer) {
                        params[i] = Integer.TYPE;
                    } else {
                        params[i] = args[i].getClass();
                    }
                }
                if (staticMeth) {
                    result = iclass.getMethod(methodStr, params).invoke(iclass, args);
                } else {
                    result = iclass.getMethod(methodStr, params).invoke(instance, args);
                }
            } else {
                if (staticMeth) {
                    result = iclass.getMethod(methodStr, new Class[]{}).invoke(iclass);
                } else {
                    result = iclass.getMethod(methodStr, new Class[]{}).invoke(instance);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

}
