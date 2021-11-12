package com.example.workmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    OneTimeWorkRequest workRequest, workRequest1;
    TextView onetimeRequest, periodictimeRequest, chainingRequest;
    PeriodicWorkRequest periodictimerequest;
    WorkContinuation chain1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onetimeRequest = findViewById(R.id.textviewOneTimeRequest);
        periodictimeRequest = findViewById(R.id.textviewPeriodicTimeRequest);
        chainingRequest = findViewById(R.id.textviewChainingRequest);

        periodictimerequest = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES).build();

        workRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        workRequest1 = new OneTimeWorkRequest.Builder(MyWorker.class).build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(periodictimerequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        periodictimeRequest.append(workInfo.getState().name() + "\n");
                    }
                });
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override

                    public void onChanged(WorkInfo workInfo) {
                        onetimeRequest.append(workInfo.getState().name() + "\n");
                    }
                });
        chain1 = WorkManager.getInstance(this).
                beginWith(workRequest)
                .then(workRequest1);
        chain1.getWorkInfosLiveData()
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                            for (WorkInfo work : workInfos) {
                                chainingRequest.append(work.getState().name() + "\n");
                        }
                    }
                });
    }

    public void buttonOneTimeRequest(View view) {
        WorkManager.getInstance(this).enqueue(workRequest);

    }

    public void buttonPeriodicTimeRequest(View view) {

        WorkManager.getInstance(this).enqueue(periodictimerequest);

    }

    public void buttonPeriodicTimeRequestCancel(View view) {
        WorkManager.getInstance(this).cancelWorkById(periodictimerequest.getId());
    }

    public void buttonchainingRequest(View view) {
        chain1.enqueue();

    }
}