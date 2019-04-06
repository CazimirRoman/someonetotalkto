package com.cazimir.someonetotalkto;

public interface AgentViewState {

    //Loading
    final class Loading implements AgentViewState{ }

    //AgentReponse
    final class AgentReponse implements AgentViewState {

        private final String message;
        private final String result;

        public AgentReponse(String message, String result) {
            this.message = message;
            this.result = result;
        }

        public String getMessage() {
            return message;
        }
        public String getResult() {
            return result;
        }
    }

    //Error
    final class Error implements AgentViewState {
        private final String message;
        private final Throwable error;

        public Error(String message, Throwable error) {
            this.message = message;
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getError() {
            return error;
        }
    }
}
