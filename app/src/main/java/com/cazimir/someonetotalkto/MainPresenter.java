package com.cazimir.someonetotalkto;

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

class MainPresenter extends MviBasePresenter<IMainActivity, AgentViewState> {

    private final AgentInteractor agentInteractor;

    MainPresenter(AgentInteractor agentInteractor) {
        this.agentInteractor = agentInteractor;
    }

    @Override
    protected void bindIntents() {

        //to business rules
        Observable<AgentViewState> sendMessage = intent(IMainActivity::sendMessageToAgentIntent)
                .switchMap(agentInteractor::sendMessageToAgent)
                .observeOn(AndroidSchedulers.mainThread());

        //back to UI
        subscribeViewState(sendMessage, IMainActivity::render);

    }
}
