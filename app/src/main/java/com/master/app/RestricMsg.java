package com.master.app;

import com.master.app.tools.ObjectUtils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * @param
 * @author Litao-pc on 2016/11/28.
 *         ~
 */

public class RestricMsg {

    private static final String TAG = RestricMsg.class.getSimpleName();
    private static RestricMsg restricMsg;

    public static boolean DEBUG = false;
    private Object smark;


    private RestricMsg() {
    }


    public static synchronized RestricMsg get() {
        if (restricMsg == null) {
            restricMsg = new RestricMsg();
        }
        return restricMsg;
    }

    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();


    public <T> Observable register(@NonNull Object smark, Class<T> tClass) {
        List<Subject> subjectList = subjectMapper.get(smark);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(smark, subjectList);
        }
        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        if (DEBUG) Log.d(TAG, "[register]subjectMapper: " + subjectMapper);
        return subject;

    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove((Subject) observable);
            if (ObjectUtils.isEmpty(subjects)) {
                subjectMapper.remove(tag);
            }
        }
        if (DEBUG) Log.d(TAG, "[unregister]subjectMapper: " + subjectMapper);
    }

    public void post(@NonNull Object content) {
        post(content.getClass().getName(), content);
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (!ObjectUtils.isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
        if (DEBUG) Log.d(TAG, "[post]subjectMapper: " + subjectMapper);
    }


    /**
     * 通过反射获取tag类型
     */
}
