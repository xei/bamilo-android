//package com.mobile.service.rest.cookies;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.net.HttpCookie;
//
//
//public class AigPersistentHttpCookie implements Serializable {
//
//    private static final long serialVersionUID = 8567141964901776111L;
//
//    private transient final HttpCookie mCookie;
//
//    private transient HttpCookie mSerializableCookie;
//
//    public AigPersistentHttpCookie(HttpCookie cookie) {
//        this.mCookie = cookie;
//    }
//
//    public HttpCookie getCookie() {
//        return mSerializableCookie != null ? mSerializableCookie : mCookie;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see java.lang.Object#toString()
//     */
//    @Override
//    public String toString() {
//        return getCookie().toString();
//    }
//
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.writeObject(mCookie.getName());
//        out.writeObject(mCookie.getValue());
//        out.writeObject(mCookie.getComment());
//        out.writeObject(mCookie.getDomain());
//        out.writeObject(mCookie.getMaxAge());
//        out.writeObject(mCookie.getPath());
//        out.writeInt(mCookie.getVersion());
//        out.writeBoolean(mCookie.getSecure());
//        out.writeBoolean(mCookie.getDiscard());
//        out.writeObject(mCookie.getCommentURL());
//        out.writeObject(mCookie.getPortlist());
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        String name = (String) in.readObject();
//        String value = (String) in.readObject();
//        mSerializableCookie = new HttpCookie(name, value);
//        mSerializableCookie.setComment((String) in.readObject());
//        mSerializableCookie.setDomain((String) in.readObject());
//        mSerializableCookie.setMaxAge((Long) in.readObject());
//        mSerializableCookie.setPath((String) in.readObject());
//        mSerializableCookie.setVersion(in.readInt());
//        mSerializableCookie.setSecure(in.readBoolean());
//        mSerializableCookie.setDiscard(in.readBoolean());
//        mSerializableCookie.setCommentURL((String) in.readObject());
//        mSerializableCookie.setPortlist((String) in.readObject());
//    }
//
//}
