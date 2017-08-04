package com.example.android.asynctaskloaderexample;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    /** AsyncTaskLoader

     * 로더가 아닌 AsyncTask등을 통해 백그라운드에서 작업을 진행하고 있는 도중에 화면 회전 등의 이벤트가 발생해서 액티비티가 파괴된다면,
     * 작업중인 스레드가 좀비 Activity자원을 붙잡고 있게된다. 결괏값도 이미 해제될 activity로 반환하게 됨.
     * 이를 해결하기 위해 액티비티의 수명주기와는 별도로 움직이는 Loader패턴이 필요하다.
     *
     * 절차:
     * 로더의 리턴값을 받기 위해 필요한 KEY값을 선언
     * AsyncTaskLoader를 생성하고, 리턴값을 받을 액티비티에서 LoaderManager.LoaderCallbacks 인터페이스를 구현
     * onCreateLoader: AsyncTaskLoader 객체의 각 메소드들을 정의하면서 생성, 리턴
     *                 (AsyncTask에서의 preExecute, doInBackground와 같은 역할을 하는 메소드 두가지 :
     *                  onStartLoading, loadInBackground를 정의해 주어야 한다)
     * onLoadFinished: onPostExecute와 같은 역할을 하는 메소드 구현
     * 로더 객체 안에 성능향상을 위한 cache 변수를 멤버변수로 두고, deliverResult(data) 메소드를 오버라이드해서 캐시에 값을 담아줌
     * 백그라운드 작업을 실행할 곳에서 getSupportLoaderManager().initLoader( .. , .. , ..) 메소드 호출
     */

    private static final int LOADER_KEY = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(LOADER_KEY, null, this);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String>(this) {

            private String cache = null;

            @Override
            protected void onStartLoading() {

                // 백그라운드 작업을 시작하기 전에 필요한 사항들 준비 (화면 내용 제거 및 progress bar 출력 등)

                super.onStartLoading();

                if(cache != null) {

                    // 캐싱된 내용이 있다면 무거운 작업을 두번 하지 말고 바로 캐시에 저장된 내용 전달: deliverResult(data)
                    deliverResult(cache);

                } else {

                    // background 작업 시작
                    forceLoad();

                }

            }

            @Override
            public String loadInBackground() {

                // 가져오는데 시간이 걸리는 긴 작업

                return null;

            }

            @Override
            public void deliverResult(String data) {

                this.cache = data;

                super.deliverResult(data);

            }

        };

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        // polishing 적용(progress bar 제거, 검사 후에 에러/정상 내용 가시화

        // data에 결과값이 들어있다. 이를 이용해서 액티비티의 UI변경, 에러 메세지 출력 함수 호출 등, 필요한 작업 진행

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
