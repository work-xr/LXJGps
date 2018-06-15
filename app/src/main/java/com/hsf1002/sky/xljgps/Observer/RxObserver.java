package com.hsf1002.sky.xljgps.Observer;


import com.allen.library.RxHttpUtils;
import com.allen.library.base.BaseDataObserver;
import com.allen.library.bean.BaseData;
import com.allen.library.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

/**
 * Created by hefeng on 18-6-15.
 */


public abstract class RxObserver<T> extends BaseDataObserver<T> {

        public RxObserver() {
        }

        /**
         * 失败回调
         *
         * @param errorMsg 错误信息
         */
        protected abstract void onError(T errorMsg);

        /**
         * 成功回调
         *
         * @param resultMsg 结果
         */
        protected abstract void onSuccess(T resultMsg);


        @Override
        public void doOnSubscribe(Disposable d) {
                RxHttpUtils.addDisposable(d);
        }

        @Override
        public void doOnError(String errorMsg) {
                ToastUtils.showToast(errorMsg);
                //onError(errorMsg);
        }


        @Override
        public void doOnNext(BaseData<T> data) {
                //可以根据需求对code统一处理
                switch (data.getCode()) {
                        case 10000:
                                onSuccess(data.getData());
                                break;
                        case 60001:
                        case 60002:
                                break;
                        case 10005:
                        case 40000:
                        case 40001:
                        case 40003:
                        case 40004:
                        case 40005:
                        case 50000:
                        case 50003:
                                //onError(data.getMsg());
                                break;
                        default:
                                //onError(data.getMsg());
                                break;
                }
        }

        @Override
        public void doOnCompleted() {

        }
}