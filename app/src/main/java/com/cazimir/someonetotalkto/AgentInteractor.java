package com.cazimir.someonetotalkto;

import io.reactivex.Observable;

//interactor se ocupa doar de returnarea unui STATE in functie de ce face callul nostru catre dialog flow

public class AgentInteractor {

    private final AgentEngine agentEngine;

    public AgentInteractor(AgentEngine agentEngine) {
        this.agentEngine = agentEngine;
    }


    // TODO: 06-Apr-19 need to handle empty responses from agent, if any.

    /**
     * @param message
     * @return in the map function you need to have a check to either return one implementation
     * of the AgentViewState or the other otherwise the startWith will not work.
     */
    public Observable<AgentViewState> sendMessageToAgent(String message) {

        return agentEngine.sendMessageToAgent(message)
                .map(response -> {
                    if (!response.isEmpty()) {
                        return new AgentViewState.AgentReponse(message, response);
                    } else {
                        return new AgentViewState.Loading();
                    }
                })
                .startWith(new AgentViewState.Loading())
                .onErrorReturn(error -> new AgentViewState.Error(message, error));
    }
}
