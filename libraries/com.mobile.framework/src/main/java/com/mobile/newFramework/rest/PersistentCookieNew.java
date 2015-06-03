package com.mobile.newFramework.rest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpCookie;


public class PersistentCookieNew implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8567141964901776111L;

    private transient final HttpCookie mCookie;
    
    private transient HttpCookie clientCookie;

    public PersistentCookieNew(HttpCookie cookie) {
        this.mCookie = cookie;
    }

    public HttpCookie getCookie() {
        HttpCookie bestCookie = mCookie;
        if (clientCookie != null) {
            bestCookie = clientCookie;
        }
        return bestCookie;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getCookie().toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(mCookie.getName());
        out.writeObject(mCookie.getValue());
        out.writeObject(mCookie.getComment());
        out.writeObject(mCookie.getDomain());
        out.writeObject(mCookie.getMaxAge());
        out.writeObject(mCookie.getPath());
        out.writeInt(mCookie.getVersion());
        out.writeBoolean(mCookie.getSecure());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        clientCookie = new HttpCookie(name, value);
        clientCookie.setComment((String) in.readObject());
        clientCookie.setDomain((String) in.readObject());
        clientCookie.setMaxAge((Long) in.readObject());
        clientCookie.setPath((String) in.readObject());
        clientCookie.setVersion(in.readInt());
        clientCookie.setSecure(in.readBoolean());
    }
}
