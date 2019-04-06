package com.cazimir.someonetotalkto;

import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.reactivex.Observable;

interface IMainActivity extends MvpView {


    /**
     * @return An observable emitting the message sent to agent
     */
    Observable<String> sendMessageToAgentIntent();


    /**
     * @param agentState The current agentState that should be displayed
     */
    void render(AgentViewState agentState);
}
