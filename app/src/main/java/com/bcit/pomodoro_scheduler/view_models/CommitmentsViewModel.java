package com.bcit.pomodoro_scheduler.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bcit.pomodoro_scheduler.model.Commitment;
import com.bcit.pomodoro_scheduler.model.Repeat;
import com.bcit.pomodoro_scheduler.repositories.CommitmentRepository;

import java.util.HashMap;
import java.util.List;

public class CommitmentsViewModel extends ViewModel implements CommitmentRepository.OnFirestoreTaskComplete {

    private final MutableLiveData<HashMap<Repeat, List<Commitment>>> commitmentsModelData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> commitmentUpdated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> commitmentDeleted = new MutableLiveData<>();

    public LiveData<HashMap<Repeat, List<Commitment>>> getCommitmentsModelData() {
        return commitmentsModelData;
    }

    public LiveData<Boolean> updateCommitmentData(Commitment commitment) {
        commitmentRepository.addOrUpdateCommitment(userEmail, commitment);
        commitmentRepository.getCommitmentsData(userEmail);
        return commitmentUpdated;
    }

    public LiveData<Boolean> deleteCommitmentData(String id) {
        commitmentRepository.deleteCommitment(userEmail, id);
        commitmentRepository.getCommitmentsData(userEmail);
        return commitmentDeleted;
    }

    private final String userEmail;

    private final CommitmentRepository commitmentRepository = new CommitmentRepository(
            this
    );

    public CommitmentsViewModel(String userEmail) {
        this.userEmail = userEmail;
        commitmentRepository.createDocForNewUser(this.userEmail);
        commitmentRepository.getCommitmentsData(this.userEmail);
    }

    @Override
    public void onCommitmentDeleted() {
        commitmentDeleted.setValue(Boolean.TRUE);
    }

    @Override
    public void onCommitmentUpdated() {
        commitmentUpdated.setValue(Boolean.TRUE);
    }

    @Override
    public void commitmentsDataAdded(HashMap<Repeat, List<Commitment>> commitmentsModel) {
        commitmentsModelData.setValue(commitmentsModel);
    }

    @Override
    public void onErrorGetCommitmentData(Exception e) {
        Log.w("GET_COMMITMENT_DATA", "Error getting documents.", e);
    }

    @Override
    public void onErrorUpdateCommitmentData(Exception e) {
        commitmentUpdated.setValue(Boolean.FALSE);
        Log.w("UPDATE_COMMITMENT_DATA", "Error getting documents.", e);
    }

    @Override
    public void onErrorDeleteCommitmentData(Exception e) {
        commitmentDeleted.setValue(Boolean.FALSE);
        Log.w("DELETE_COMMITMENT_DATA", "Error getting documents.", e);
    }
}
