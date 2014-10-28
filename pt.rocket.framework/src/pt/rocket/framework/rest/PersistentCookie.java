package pt.rocket.framework.rest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie;

/**
 * A wrapper class around {@link Cookie} and/or {@link BasicClientCookie} designed for use in {@link PersistentCookieStore}.
 * @see https://github.com/loopj/android-async-http/blob/master/library/src/main/java/com/loopj/android/http/SerializableCookie.java
 * @author spereira
 */
public class PersistentCookie implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8567141964901776111L;

    private transient final Cookie mCookie;
    
    private transient BasicClientCookie clientCookie;

    public PersistentCookie(Cookie cookie) {
        this.mCookie = cookie;
    }

    public Cookie getCookie() {
        Cookie bestCookie = mCookie;
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
        out.writeObject(mCookie.getExpiryDate());
        out.writeObject(mCookie.getPath());
        out.writeInt(mCookie.getVersion());
        out.writeBoolean(mCookie.isSecure());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        clientCookie = new BasicClientCookie(name, value);
        clientCookie.setComment((String) in.readObject());
        clientCookie.setDomain((String) in.readObject());
        clientCookie.setExpiryDate((Date) in.readObject());
        clientCookie.setPath((String) in.readObject());
        clientCookie.setVersion(in.readInt());
        clientCookie.setSecure(in.readBoolean());
    }
}
