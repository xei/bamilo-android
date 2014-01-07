/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/manuelsilva/Documents/NewFrameworkFinalProject/pt.rocket.framework/src/pt/rocket/framework/service/IRemoteServiceCallback.aidl
 */
package pt.rocket.framework.service;
public interface IRemoteServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements pt.rocket.framework.service.IRemoteServiceCallback
{
private static final java.lang.String DESCRIPTOR = "pt.rocket.framework.service.IRemoteServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an pt.rocket.framework.service.IRemoteServiceCallback interface,
 * generating a proxy if needed.
 */
public static pt.rocket.framework.service.IRemoteServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof pt.rocket.framework.service.IRemoteServiceCallback))) {
return ((pt.rocket.framework.service.IRemoteServiceCallback)iin);
}
return new pt.rocket.framework.service.IRemoteServiceCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getResponse:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
_arg0 = new android.os.Bundle();
this.getResponse(_arg0);
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getError:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
_arg0 = new android.os.Bundle();
this.getError(_arg0);
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements pt.rocket.framework.service.IRemoteServiceCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void getResponse(android.os.Bundle bundle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getResponse, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void getError(android.os.Bundle bundle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getError, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_getResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void getResponse(android.os.Bundle bundle) throws android.os.RemoteException;
public void getError(android.os.Bundle bundle) throws android.os.RemoteException;
}
