package uk.ac.wlv.tothespec;

import java.util.UUID;

public class Spec {
    private UUID mId;
    private String mMessage;
    private String mUrl;

        public Spec() {
            this(UUID.randomUUID());
        }

        public Spec(UUID id) {
            mId = id;
        }

        public UUID getId() {
            return mId;
        }

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String message) {
            mMessage = message;
        }

        public String getUrl() { return mUrl;}

        public void setUrl(String url) { mUrl = url;}
    }

